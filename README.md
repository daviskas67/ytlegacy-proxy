# ytlegacy-proxy

A local proxy server and patched Android client designed to restore functionality to the legacy YouTube 5.0.21 (2013) application on older Android devices (4.4+) in modern network environments.

The project operates without requiring root access, VPNs, or iptables modifications. It acts as an adapter between the legacy GData API used by the client and a modern `yt2009` instance.

## Architecture

The system consists of two primary components:

1.  **ytlegacy-proxy.apk**
    A lightweight (150 KB) local HTTP server built on NanoHTTPD. It listens on `127.0.0.1:8080`. Its primary responsibilities are:
    *   Intercepting requests from the patched YouTube client.
    *   Translating legacy API requests into `yt2009` compatible HTTP requests.
    *   Intercepting and rewriting `yt2009` GData XML responses to ensure compliance with the strict Atom parsers of the 2013 client.
    *   Proxying video streams and image assets.

2.  **youtube-5.0.21-ytlegacy.apk**
    A repackaged version of the original YouTube 5.0.21 APK with the package name renamed to `org.ytlegacy.daviskas`. The application has been heavily modified at the Smali level.

### Data Flow

1.  The patched YouTube client sends a request to `http://127.0.0.1:8080/feeds/...` instead of `gdata.youtube.com`.
2.  The local proxy intercepts the request and fetches the data from the configured `yt2009` instance.
3.  The proxy parses the response, injects required XML fields, rewrites hostnames, and returns the valid Atom feed to the client.
4.  For video playback, the client requests `/video/{id}.mp4`. The proxy fetches the stream from `yt2009` using the `/get_video?video_id={id}/mp4` endpoint, ensuring high-speed MP4 delivery directly to the client's media player.

## Technical Implementations

### Proxy Server (Java)
*   **XML Sanitization and Injection:** The proxy dynamically injects mandatory Atom elements (`<link rel="self">`, `<gd:feedLink>`, `<yt:channelId>`) into the feeds. Legacy parsers will throw `NullPointerException` if these specific navigational URIs are absent.
*   **Host Fallback Mechanism:** Due to regional DNS blocks, the proxy attempts to fetch thumbnails via alternative domains (`i1.ytimg.com`, `yt4.googleusercontent.com`, `lh4.googleusercontent.com`) if the primary endpoints fail.
*   **Negative Caching:** The proxy utilizes a 60-second negative cache for 404/429 responses from `yt2009` to prevent request storms (rate limiting) and minimize CPU/battery usage on the device.
*   **Fallback Video Payload:** If a video is unavailable (404), the proxy returns a valid 44-byte empty MP4 container instead of a 404 error. This prevents the legacy `MediaPlayer` from hanging and triggering an ANR (Application Not Responding) crash.

### Client Patches (Smali/XML)
*   **Network Redirection:** All hardcoded instances of `gdata.youtube.com`, `youtube.com`, and `googleapis.com` have been patched to `127.0.0.1:8080` across all Smali classes.
*   **Crash Prevention (Null URI Fix):** The method `p()` in `com.google.android.youtube.app.honeycomb.phone.g.smali` was modified to safely bypass `uploadsUri` initialization if the URI is null. The original client crashed when parsing channels that lacked specific feed links.
*   **GData Hostname Override:** The default hostname in `res/values/arrays.xml` was changed to force the client to use the local proxy, bypassing the internal settings preference.
*   **Stream Format Override:** The `getItag()` method in `Stream.smali` was hardcoded to return `18` (MP4 360p/480p) to ensure compatibility with the device's native H.264 decoder.
*   **StrictMode Bypass:** `StrictMode.LAX` was injected into `YouTubeApplication.smali` to prevent `NetworkOnMainThreadException` on older Android versions.

## Build Requirements
*   Android SDK (Build Tools 29.0.3)
*   Java 8 (OpenJDK)
*   Apktool 2.7+
*   `d8.jar` for DEX compilation

To build the proxy, run:
```bash
cd ytlegacy-android
./build-manual.sh
