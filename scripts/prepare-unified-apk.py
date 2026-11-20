#!/usr/bin/env python3
"""Patch the decoded YouTube 5.0.21 APK to host ytlegacy-proxy in-process."""
import argparse
import pathlib
import re
import sys
import xml.etree.ElementTree as ET

ANDROID = "http://schemas.android.com/apk/res/android"
ET.register_namespace("android", ANDROID)

def a(name):
    return "{%s}%s" % (ANDROID, name)

def patch_manifest(path: pathlib.Path) -> None:
    tree = ET.parse(str(path))
    root = tree.getroot()
    if root.tag != "manifest" or root.get("package") != "org.ytlegacy.daviskas":
        raise SystemExit("Unexpected base package in AndroidManifest.xml")
    app = root.find("application")
    if app is None:
        raise SystemExit("Manifest has no application element")

    activities = {e.get(a("name")) for e in app.findall("activity")}
    services = {e.get(a("name")) for e in app.findall("service")}
    if "com.ytlegacy.proxy.MainActivity" not in activities:
        ET.SubElement(app, "activity", {
            a("name"): "com.ytlegacy.proxy.MainActivity",
            a("exported"): "false",
            a("label"): "ytlegacy",
            a("theme"): "@android:style/Theme.Black.NoTitleBar",
        })
    if "com.ytlegacy.proxy.ProxyService" not in services:
        ET.SubElement(app, "service", {
            a("name"): "com.ytlegacy.proxy.ProxyService",
            a("enabled"): "true",
            a("exported"): "false",
            a("foregroundServiceType"): "dataSync",
        })

    permissions = {
        e.get(a("name")) for e in root.findall("uses-permission")
    }
    if "android.permission.INTERNET" not in permissions:
        ET.SubElement(root, "uses-permission", {a("name"): "android.permission.INTERNET"})
    if "android.permission.WAKE_LOCK" not in permissions:
        ET.SubElement(root, "uses-permission", {a("name"): "android.permission.WAKE_LOCK"})
    if "android.permission.FOREGROUND_SERVICE" not in permissions:
        ET.SubElement(root, "uses-permission", {a("name"): "android.permission.FOREGROUND_SERVICE"})

    tree.write(str(path), encoding="utf-8", xml_declaration=True)

def patch_preferences(path: pathlib.Path) -> None:
    tree = ET.parse(str(path))
    root = tree.getroot()
    for node in root.iter("Preference"):
        if node.get(a("key")) == "ytlegacy_proxy_settings":
            tree.write(str(path), encoding="utf-8", xml_declaration=True)
            return

    category = ET.SubElement(root, "PreferenceCategory", {
        a("key"): "ytlegacy_proxy_category",
        a("title"): "ytlegacy",
    })
    preference = ET.SubElement(category, "Preference", {
        a("key"): "ytlegacy_proxy_settings",
        a("title"): "ytlegacy settings",
        a("summary"): "Local proxy, playback quality, providers and yt2009 account",
    })
    ET.SubElement(preference, "intent", {
        a("action"): "android.intent.action.MAIN",
        a("targetPackage"): "org.ytlegacy.daviskas",
        a("targetClass"): "com.ytlegacy.proxy.MainActivity",
    })
    tree.write(str(path), encoding="utf-8", xml_declaration=True)

def patch_application_start(path: pathlib.Path) -> None:
    source = path.read_text(encoding="utf-8")
    start = source.find(".method public final onCreate()V")
    if start < 0:
        raise SystemExit("BaseApplication.onCreate() was not found")
    end = source.find(".end method", start)
    if end < 0:
        raise SystemExit("BaseApplication.onCreate() method is unterminated")
    method = source[start:end]
    if "Lcom/ytlegacy/proxy/ProxyService;" in method:
        return
    if ".locals 1" not in method:
        raise SystemExit("Unexpected BaseApplication.onCreate() register layout")
    method = method.replace(".locals 1", ".locals 2", 1)
    marker = "    invoke-super {p0}, Landroid/app/Application;->onCreate()V"
    pos = method.find(marker)
    if pos < 0:
        raise SystemExit("BaseApplication.onCreate() Application super call was not found")
    pos += len(marker)
    launch = """

    # Start the embedded local API before YouTube initializes its clients.
    new-instance v0, Landroid/content/Intent;
    const-class v1, Lcom/ytlegacy/proxy/ProxyService;
    invoke-direct {v0, p0, v1}, Landroid/content/Intent;-><init>(Landroid/content/Context;Ljava/lang/Class;)V
    invoke-virtual {p0, v0}, Landroid/content/Context;->startService(Landroid/content/Intent;)Landroid/content/ComponentName;
    move-result-object v0
"""
    method = method[:pos] + launch + method[pos:]
    source = source[:start] + method + source[end:]
    path.write_text(source, encoding="utf-8")

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("decoded_client", type=pathlib.Path)
    args = parser.parse_args()
    root = args.decoded_client
    patch_manifest(root / "AndroidManifest.xml")
    patch_preferences(root / "res/xml/youtube_preferences.xml")
    patch_application_start(root / "smali/org/ytlegacy/daviskas/core/BaseApplication.smali")
    apktool_yml = root / "apktool.yml"
    yml = apktool_yml.read_text(encoding="utf-8")
    yml = re.sub(r"(?m)^  versionCode:.*$", "  versionCode: '5022'", yml, count=1)
    yml = re.sub(r"(?m)^  versionName:.*$", "  versionName: 5.0.21-ytlegacy-proxy-1.16", yml, count=1)
    apktool_yml.write_text(yml, encoding="utf-8")
    print("Patched manifest, independent ytlegacy settings entry, autostart and app version.")

if __name__ == "__main__":
    main()
