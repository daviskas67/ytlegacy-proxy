#!/bin/bash
set -e

PROJ="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SDK="/usr/lib/android-sdk"
BT="$SDK/build-tools/29.0.3"
PLATFORM="$SDK/platforms/android-30"
ANDROID_JAR="$PLATFORM/android.jar"
D8_JAR="$PROJ/tools/d8.jar"

cd "$PROJ"

echo "=== 1. Проверка инструментов ==="
for tool in "$BT/aapt2" "$BT/zipalign" "$BT/apksigner"; do
    [ -f "$tool" ] || { echo "MISSING: $tool"; exit 1; }
    echo "OK: $(basename $tool)"
done
[ -f "$D8_JAR" ] || { echo "MISSING: d8.jar"; exit 1; }
echo "OK: d8.jar"
[ -f "$ANDROID_JAR" ] || { echo "MISSING: android.jar"; exit 1; }
echo "OK: android.jar"
[ -f "libs/gson-2.2.4.jar" ] || { echo "MISSING: gson"; exit 1; }
[ -f "libs/nanohttpd-2.3.1.jar" ] || { echo "MISSING: nanohttpd"; exit 1; }
echo "OK: libs/*"

echo ""
echo "=== 2. Компиляция Java ==="
rm -rf build/manual
mkdir -p build/manual/classes build/manual/dex

JAVAC_LOG="build/manual/javac.log"
if ! javac -source 8 -target 8 \
    -cp "$ANDROID_JAR:libs/gson-2.2.4.jar:libs/nanohttpd-2.3.1.jar" \
    -d build/manual/classes \
    app/src/main/java/com/ytlegacy/proxy/*.java >"$JAVAC_LOG" 2>&1; then
    cat "$JAVAC_LOG"
    echo "!!! JAVAC FAILED — build aborted !!!"
    exit 1
fi
grep -v "bootstrap class path" "$JAVAC_LOG" || true

CLASS_COUNT=$(ls build/manual/classes/com/ytlegacy/proxy/*.class 2>/dev/null | wc -l)
if [ "$CLASS_COUNT" -lt 5 ]; then
    echo "!!! Only $CLASS_COUNT .class files — build broken !!!"
    exit 1
fi
echo "OK: Java compiled ($CLASS_COUNT classes)"

echo ""
echo "=== 3. DEX конверсия ==="
java -cp "$D8_JAR" com.android.tools.r8.D8 \
    --min-api 8 \
    --lib "$ANDROID_JAR" \
    --output build/manual/dex \
    build/manual/classes/com/ytlegacy/proxy/*.class \
    libs/gson-2.2.4.jar \
    libs/nanohttpd-2.3.1.jar

ls -lh build/manual/dex/classes.dex
echo "OK: DEX создан"

echo ""
echo "=== 4. Ресурсы ==="
mkdir -p app/src/main/res/values
[ -f app/src/main/res/values/strings.xml ] || cat > app/src/main/res/values/strings.xml << 'EOF'
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">ytlegacy-proxy</string>
</resources>
EOF

"$BT/aapt2" compile --dir app/src/main/res -o build/manual/res.zip
echo "OK: Ресурсы"

echo ""
echo "=== 5. Сборка базового APK ==="
"$BT/aapt2" link \
    -o build/manual/app-base.apk \
    -A app/src/main/assets \
    --manifest app/src/main/AndroidManifest.xml \
    --min-sdk-version 8 \
    --target-sdk-version 19 \
    --version-code 2 \
    --version-name "1.5" \
    -I "$ANDROID_JAR" \
    -R build/manual/res.zip \
    --auto-add-overlay

ls -lh build/manual/app-base.apk
echo "OK: Базовый APK"

echo ""
echo "=== 6. Вставка classes.dex ==="
cd build/manual/dex
zip -quj ../app-base.apk classes.dex
cd "$PROJ"
echo "OK: classes.dex внутри APK"

echo ""
echo "=== 7. Zipalign ==="
"$BT/zipalign" -f 4 build/manual/app-base.apk build/manual/app-aligned.apk
echo "OK: Zipalign"

echo ""
echo "=== 8. Подпись ==="
KEYSTORE="$HOME/.android/debug.keystore"
if [ ! -f "$KEYSTORE" ]; then
    mkdir -p "$HOME/.android"
    keytool -genkey -v -keystore "$KEYSTORE" \
        -storepass android -alias androiddebugkey -keypass android \
        -keyalg RSA -keysize 2048 -validity 10000 \
        -dname "CN=Android Debug,O=Android,C=US" 2>&1 | tail -2
fi

"$BT/apksigner" sign \
    --ks "$KEYSTORE" \
    --ks-pass pass:android \
    --key-pass pass:android \
    --out build/manual/ytlegacy-proxy.apk \
    build/manual/app-aligned.apk

echo "OK: Подписан"
echo ""
echo "=========================================="
echo "  APK: $PROJ/build/manual/ytlegacy-proxy.apk"
echo "=========================================="
ls -lh build/manual/ytlegacy-proxy.apk
