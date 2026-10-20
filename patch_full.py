import os
import re
import sys

WORK_DIR = "yt_patch_work/smali"
LOCALHOST = "127.0.0.1:8080"
LOCALHOST_URL = f"http://{LOCALHOST}"

# Порядок важен — длинные домены первыми
DOMAINS = [
    "youtubei.googleapis.com",
    "youtube.googleapis.com",
    "gdata.youtube.com",
    "www.youtube.com",
    "m.youtube.com",
    "s.youtube.com",
    "yt3.ggpht.com",
    "yt3.googleusercontent.com",
    "i.ytimg.com",
    "youtube.com",
    "ytimg.com",
    "googlevideo.com",
    "googleapis.com",
    "www.google.com",
    "www.google-analytics.com",
    "google-analytics.com",
    "ggpht.com",
    "ytstatic.com",
    "googlesyndication.com",
    "googleadservices.com",
    "doubleclick.net",
    "google.com",
]

# После патча надо вернуть эти (это XML namespace, а не URL!)
SCHEMA_RESTORES = [
    (f"{LOCALHOST_URL}/schemas/2007", "http://gdata.youtube.com/schemas/2007"),
    (f"{LOCALHOST_URL}/schemas/android.com/apk/res/android", "http://schemas.android.com/apk/res/android"),
    (f"{LOCALHOST_URL}/schemas/android.com/apk/res-auto", "http://schemas.android.com/apk/res-auto"),
    (f"{LOCALHOST_URL}/apk/res/android", "http://schemas.android.com/apk/res/android"),
    (f"{LOCALHOST_URL}/apk/res-auto", "http://schemas.android.com/apk/res-auto"),
    (f"{LOCALHOST_URL}/2005/Atom", "http://www.w3.org/2005/Atom"),
    (f"{LOCALHOST_URL}/g/2005", "http://schemas.google.com/g/2005"),
    (f"{LOCALHOST_URL}/sax", "http://xml.org/sax"),
    (f"{LOCALHOST_URL}/mrss/", "http://search.yahoo.com/mrss/"),
    (f"{LOCALHOST_URL}/georss", "http://www.georss.org/georss"),
    (f"{LOCALHOST_URL}/gml", "http://www.opengis.net/gml"),
    (f"{LOCALHOST_URL}/-/spec/opensearch", "http://a9.com/-/spec/opensearch"),
    (f"{LOCALHOST_URL}/ns/", "http://ns."),
    (f"{LOCALHOST_URL}/xml/", "http://xml."),
]

def patch_file(path):
    try:
        with open(path, 'r', encoding='utf-8', errors='ignore') as fp:
            content = fp.read()
    except Exception:
        return False

    original = content

    # 1. Схема https → http
    content = content.replace('"https://"', '"http://"')
    content = content.replace('"https"', '"http"')

    # 2. Полные URL с протоколом
    for domain in DOMAINS:
        content = content.replace(f"https://{domain}", LOCALHOST_URL)
        content = content.replace(f"http://{domain}", LOCALHOST_URL)

    # 3. Голые домены в кавычках (для Uri.Builder)
    for domain in DOMAINS:
        content = content.replace(f'"{domain}"', f'"{LOCALHOST}"')

    # 4. Восстанавливаем XML-namespace
    for wrong, right in SCHEMA_RESTORES:
        content = content.replace(wrong, right)

    if content != original:
        with open(path, 'w', encoding='utf-8') as fp:
            fp.write(content)
        return True
    return False

count = 0
for root, dirs, files in os.walk(WORK_DIR):
    for f in files:
        if f.endswith(".smali"):
            path = os.path.join(root, f)
            if patch_file(path):
                count += 1

print(f"✅ Patched {count} smali files")

# Verification
remaining = 0
for root, dirs, files in os.walk(WORK_DIR):
    for f in files:
        if not f.endswith(".smali"):
            continue
        path = os.path.join(root, f)
        with open(path, 'r', encoding='utf-8', errors='ignore') as fp:
            content = fp.read()
        # Exclude legit schema URIs
        for domain in ["gdata.youtube.com", "www.youtube.com", "www.google.com", "youtube.com"]:
            for line in content.split('\n'):
                if domain in line and 'schemas' not in line and 'schema' not in line.lower():
                    remaining += 1
                    print(f"⚠️  STILL: {path}: {line.strip()[:100]}")
                    break

if remaining == 0:
    print("✅ No more real YouTube/Google URLs (schemas preserved)")
else:
    print(f"⚠️  {remaining} remaining occurrences")
