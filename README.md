# ytlegacy-proxy

A local compatibility proxy for the 2013-era YouTube 5.0.21 Android client.

## v1.7

- Invidious is now the primary data backend. The app probes the official public instance list in parallel at startup and measures response latency from the Android device itself.
- Requests use the fastest currently healthy instance first. Slow, unavailable, rate-limited, or server-error instances are temporarily skipped; the order is refreshed periodically.
- Search, video metadata, related videos, playlists, trending feeds, channel profiles/uploads, comments, and progressive MP4 playback use Invidious first.
- yt2009 is only used when the Invidious pool has no healthy instances. InnerTube remains a tertiary fallback for cases where an otherwise healthy provider cannot supply a particular stream/feed.
- `/api/invidious` exposes the current per-instance health, measured latency, selected fastest host, failures, and probe status.
- The stream proxy permits HTTPS URLs from the configured Invidious hosts and Google's `*.googlevideo.com` media hosts, and forwards byte-range requests.

## Invidious instances

The client currently probes these four instances from the official public list:

- `invidious.nerdvpn.de`
- `invidious.tiekoetter.com`
- `yt.chocolatemoo53.com`
- `inv.nadeko.net`

The fastest one is selected dynamically on each device. Public monitoring data can help shortlist servers, but does not predict every user's network route.

## v1.6

- Live YouTube search suggestions at the legacy `/complete/search` endpoint, with a bounded 32-query cache and short TTL.
- Playlist video feeds through yt2009 with InnerTube browse fallback.
- Modern trending-page fallback for legacy `most_popular`, `top_rated`, and `recently_featured` feeds.
- Direct Googlevideo stream fallback when yt2009 cannot provide a video URL.
- `/api/video/{id}` metadata diagnostics; `/api/cache` cache statistics; `POST /api/cache/clear` clears local metadata caches.
- More accurate fallback reporting.

## v1.5

- Legacy yt2009/GData path with InnerTube best-effort fallbacks for search, related videos, channel pages, uploads, and missing player metadata.
- `/health` and `/api/status` diagnostics.
- Android notification compatibility and improved server startup failure handling.
- Bounded upstream text responses and connection cleanup.
- Avoids inventing fake channel IDs.

## Build

The Android project is in `ytlegacy-android/`.

Requirements: JDK 17, Android SDK platform 30, Android build-tools 29.0.3, Android Gradle Plugin 7.4.2, and Gradle 7.5.1.

```sh
cd ytlegacy-android
gradle --no-daemon :app:assembleDebug
```

The debug APK is written to `app/build/outputs/apk/debug/app-debug.apk`. GitHub Actions uploads it as the `ytlegacy-proxy-v1.7-debug` artifact for 14 days.

The optional `build-manual.sh` path is for environments that already provide Android SDK tools, D8, Gson 2.2.4, and NanoHTTPD 2.3.1 jars.

## Diagnostics

After starting the service, open:

- `http://127.0.0.1:8080/health`
- `http://127.0.0.1:8080/api/status`
- `http://127.0.0.1:8080/api/invidious`
- `http://127.0.0.1:8080/api/cache`
- `http://127.0.0.1:8080/api/video/VIDEO_ID`

To clear local metadata caches, send a POST request to `http://127.0.0.1:8080/api/cache/clear`.

## Notes

Invidious is an unofficial, best-effort compatibility API and public instances can change or disappear. The client only trusts the hosts in its built-in allowlist and measures them on-device. yt2009 remains the last-resort source when no listed Invidious instance is healthy.
