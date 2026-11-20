#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BASE_APK="$ROOT/release/youtube-5.0.21-ytlegacy-v1.3-FINAL.apk"
PROXY_APK="$ROOT/ytlegacy-android/app/build/outputs/apk/debug/app-debug.apk"
WORK="$ROOT/ytlegacy-android/build/unified"
OUT="$WORK/ytlegacy-5.0.21-v1.16-unified.apk"
ALIGNED="$WORK/ytlegacy-5.0.21-v1.16-aligned.apk"

SDK_ROOT="${ANDROID_SDK_ROOT:-${ANDROID_HOME:-}}"
BUILD_TOOLS="${SDK_ROOT}/build-tools/30.0.3"
if [[ -d "$BUILD_TOOLS" ]]; then
  export PATH="$BUILD_TOOLS:$PATH"
fi
ZIPALIGN="${ZIPALIGN:-$BUILD_TOOLS/zipalign}"
APKSIGNER="${APKSIGNER:-$BUILD_TOOLS/apksigner}"
AAPT="${AAPT:-$BUILD_TOOLS/aapt}"

for file in "$BASE_APK" "$PROXY_APK"; do
  [[ -s "$file" ]] || { echo "Missing input APK: $file" >&2; exit 1; }
done
for tool in apktool python3; do
  command -v "$tool" >/dev/null || { echo "Missing build tool: $tool" >&2; exit 1; }
done
for tool in "$ZIPALIGN" "$APKSIGNER" "$AAPT"; do
  [[ -x "$tool" ]] || { echo "Missing Android build tool: $tool" >&2; exit 1; }
done

rm -rf "$WORK"
mkdir -p "$WORK/client" "$WORK/proxy"
echo "[1/8] Decode original patched YouTube client"
apktool d -f "$BASE_APK" -o "$WORK/client" >/dev/null

echo "[2/8] Decode proxy build and collect its DEX classes"
apktool d -f "$PROXY_APK" -o "$WORK/proxy" >/dev/null

if [[ ! -d "$WORK/client/smali" || ! -d "$WORK/proxy/smali" ]]; then
  echo "Expected a single primary DEX in both applications." >&2
  exit 1
fi
if compgen -G "$WORK/proxy/smali_classes*" >/dev/null; then
  echo "Proxy produced multidex; API 8 cannot load this merged build." >&2
  exit 1
fi

python3 - "$WORK/client/smali" "$WORK/proxy/smali" <<'PY'
import pathlib, shutil, sys
base = pathlib.Path(sys.argv[1])
incoming = pathlib.Path(sys.argv[2])
copied = 0
for src in incoming.rglob("*.smali"):
    if src.name in {"R.smali", "BuildConfig.smali"} or src.name.startswith("R$"):
        continue
    rel = src.relative_to(incoming)
    dst = base / rel
    if dst.exists():
        raise SystemExit("Smali class collision: " + str(rel))
    dst.parent.mkdir(parents=True, exist_ok=True)
    shutil.copy2(src, dst)
    copied += 1
if copied < 20:
    raise SystemExit("Unexpectedly few proxy classes were imported: %d" % copied)
print("Imported %d proxy/dependency smali classes into the original DEX." % copied)
PY

echo "[3/8] Patch original manifest, settings XML and app bootstrap"
python3 "$ROOT/scripts/prepare-unified-apk.py" "$WORK/client"

echo "[4/8] Rebuild one combined APK"
apktool b "$WORK/client" -o "$WORK/unsigned.apk" >/dev/null

echo "[5/8] Validate package, one-DEX layout and DEX method limit"
python3 - "$WORK/unsigned.apk" <<'PY'
import struct, sys, zipfile
apk = sys.argv[1]
with zipfile.ZipFile(apk) as z:
    dex = sorted(n for n in z.namelist() if n == "classes.dex" or
                 (n.startswith("classes") and n.endswith(".dex")))
    if dex != ["classes.dex"]:
        raise SystemExit("Expected exactly one DEX for API 8, found: " + repr(dex))
    data = z.read("classes.dex")
    method_ids = struct.unpack_from("<I", data, 88)[0]
    class_defs = struct.unpack_from("<I", data, 96)[0]
    if method_ids >= 65536:
        raise SystemExit("DEX method reference limit exceeded: %d" % method_ids)
    if b"com/ytlegacy/proxy/ProxyService" not in data:
        raise SystemExit("ProxyService descriptor missing from merged DEX")
    if b"org/ytlegacy/daviskas/core/BaseApplication" not in data:
        raise SystemExit("Original YouTube class missing from merged DEX")
    print("classes.dex: %d bytes, %d method references, %d classes" %
          (len(data), method_ids, class_defs))
PY
"$AAPT" dump badging "$WORK/unsigned.apk" | grep -E "^(package:|sdkVersion:|targetSdkVersion:|application-label:|launchable-activity:)" || true

echo "[6/8] Round-trip verify manifest, settings entry and bootstrap"
apktool d -f "$WORK/unsigned.apk" -o "$WORK/verify" >/dev/null
grep -q 'package="org.ytlegacy.daviskas"' "$WORK/verify/AndroidManifest.xml"
grep -q 'Shell\$HomeActivity' "$WORK/verify/AndroidManifest.xml"
grep -q 'com.ytlegacy.proxy.ProxyService' "$WORK/verify/AndroidManifest.xml"
grep -q 'android.permission.FOREGROUND_SERVICE' "$WORK/verify/AndroidManifest.xml"
grep -q 'foregroundServiceType="dataSync"' "$WORK/verify/AndroidManifest.xml"
grep -q 'com.ytlegacy.proxy.MainActivity' "$WORK/verify/res/xml/youtube_preferences.xml"
grep -q 'ytlegacy_proxy_settings' "$WORK/verify/res/xml/youtube_preferences.xml"
grep -q 'Lcom/ytlegacy/proxy/ProxyService;' "$WORK/verify/smali/org/ytlegacy/daviskas/core/BaseApplication.smali"
echo "Verified original YouTube launcher, embedded service, dedicated settings entry and autostart."

echo "[7/8] Zipalign and sign the combined APK"
"$ZIPALIGN" -f 4 "$WORK/unsigned.apk" "$ALIGNED"
KEYSTORE="${YTLEGACY_KEYSTORE:-$HOME/.android/debug.keystore}"
STOREPASS="${YTLEGACY_KEYSTORE_PASSWORD:-android}"
ALIAS="${YTLEGACY_KEY_ALIAS:-androiddebugkey}"
KEYPASS="${YTLEGACY_KEY_PASSWORD:-$STOREPASS}"
if [[ ! -s "$KEYSTORE" ]]; then
  mkdir -p "$(dirname "$KEYSTORE")"
  keytool -genkeypair -v -keystore "$KEYSTORE" -storepass "$STOREPASS" \
    -alias "$ALIAS" -keypass "$KEYPASS" -keyalg RSA -keysize 2048 \
    -validity 10000 -dname "CN=ytlegacy unified debug,O=ytlegacy,C=US" >/dev/null 2>&1
fi
"$APKSIGNER" sign --ks "$KEYSTORE" --ks-pass "pass:$STOREPASS" \
  --key-pass "pass:$KEYPASS" --out "$OUT" "$ALIGNED"
"$APKSIGNER" verify --verbose "$OUT"

echo "[8/8] Combined APK ready"
ls -lh "$OUT"
echo "$OUT"
