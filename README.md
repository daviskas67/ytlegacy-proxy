# ytlegacy-proxy

A local compatibility proxy for the 2013-era YouTube 5.0.21 Android client.

## v1.5

- Keeps the legacy yt2009/GData path first and uses YouTube InnerTube as a best-effort fallback for search, related videos, channel pages, uploads, and missing player metadata.
- Exposes `/health` and `/api/status` JSON diagnostics with uptime, request count, version, and backend-pool health.
- Binds the HTTP listener to `127.0.0.1:8080`; the proxy is intended for clients running on the same device.
- Handles notification APIs across Android API 8–16+ and reports server startup failures instead of claiming that the proxy is running.
- Bounds upstream text responses and closes completed upstream fetch connections.
- Avoids inventing fake channel IDs when a real YouTube UC ID cannot be resolved.

## Build

The Android project is in `ytlegacy-android/`.

Requirements: JDK 11, Android SDK platform 30, Android build-tools 29.0.3, and Gradle 6.7.1.

```sh
cd ytlegacy-android
gradle --no-daemon :app:assembleDebug
```

The debug APK is written to `app/build/outputs/apk/debug/app-debug.apk`.

The optional `build-manual.sh` path is for environments that already provide the Android SDK tools, D8, Gson 2.2.4, and NanoHTTPD 2.3.1 jars.

## Diagnostics

After starting the service, open:

- `http://127.0.0.1:8080/health`
- `http://127.0.0.1:8080/api/status`

The diagnostics do not expose upstream instance URLs or credentials.

## Notes

InnerTube is an undocumented fallback API and can change without notice. The legacy yt2009/GData path remains the primary source.
