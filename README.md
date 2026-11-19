# ytlegacy-proxy

A local compatibility proxy for the 2013-era YouTube 5.0.21 Android client.

## v1.6

- Live YouTube search suggestions at the legacy `/complete/search` endpoint, with a bounded 32-query cache and short TTL.
- Playlist video feeds through the legacy GData route, using yt2009 first and InnerTube browse as fallback.
- Modern trending-page fallback for legacy `most_popular`, `top_rated`, and `recently_featured` feeds.
- Direct Googlevideo stream fallback when yt2009 cannot provide a video URL. Byte-range requests are forwarded, and only HTTPS `*.googlevideo.com` stream hosts are accepted.
- `/api/video/{id}` JSON metadata endpoint for quick diagnostics.
- `/api/cache` shows metadata-cache counts and hit/miss totals; `POST /api/cache/clear` clears local metadata caches.
- More accurate fallback reporting when all configured yt2009 instances are temporarily unavailable.
- Keeps the listener bound to `127.0.0.1:8080`; it is intended for clients running on the same device.

## v1.5

- Keeps the legacy yt2009/GData path first and uses YouTube InnerTube as a best-effort fallback for search, related videos, channel pages, uploads, and missing player metadata.
- Exposes `/health` and `/api/status` JSON diagnostics with uptime, request count, version, and backend-pool health.
- Handles notification APIs across Android API 8–16+ and reports server startup failures instead of claiming that the proxy is running.
- Bounds upstream text responses and closes completed upstream fetch connections.
- Avoids inventing fake channel IDs when a real YouTube UC ID cannot be resolved.

## Build

The Android project is in `ytlegacy-android/`.

Requirements: JDK 17, Android SDK platform 30, Android build-tools 29.0.3, Android Gradle Plugin 7.4.2, and Gradle 7.5.1.

```sh
cd ytlegacy-android
gradle --no-daemon :app:assembleDebug
```

The debug APK is written to `app/build/outputs/apk/debug/app-debug.apk`. GitHub Actions also uploads it as the `ytlegacy-proxy-v1.6-debug` artifact for 14 days. The v1.6 release asset is published after the `release v1.6` commit builds successfully.

The optional `build-manual.sh` path is for environments that already provide the Android SDK tools, D8, Gson 2.2.4, and NanoHTTPD 2.3.1 jars.

## Diagnostics

After starting the service, open:

- `http://127.0.0.1:8080/health`
- `http://127.0.0.1:8080/api/status`
- `http://127.0.0.1:8080/api/cache`
- `http://127.0.0.1:8080/api/video/VIDEO_ID`

To clear the local metadata caches, send a POST request to `http://127.0.0.1:8080/api/cache/clear`.

## Notes

InnerTube and Google suggestion endpoints are undocumented and can change without notice. The yt2009/GData path remains the preferred source. Direct playback fallback requires a non-ciphered HTTPS format URL in the InnerTube player response; videos that only expose signature-ciphered formats may still need yt2009.
