# Changelog

All notable changes recorded here are for the releases updated during the current maintenance work. Dates use YYYY-MM-DD.

## [1.11] - 2026-11-20

### Added
- Added `Yt2009AccountManager` for the upstream yt2009 `gsign` device-linking flow, pinned to `http://femboy.email:730`.
- Added local `POST /api/yt2009/login/start`, `GET /api/yt2009/login/status`, and `GET /api/yt2009/login/picker` endpoints.
- Added MainActivity controls to start sign-in, copy the device code, check the linked account, and open the YouTube channel picker.
- Added `Yt2009Client.fetchAccountFeed`, which requests the personalized recommendations route with `X-GData-Device` and never sends that identifier through dynamic instance failover.
- Personalized yt2009 recommendations are now attempted before public Invidious/Piped discovery feeds for the legacy default subscriptions/activity routes.
- Added local redirects for legacy `/gsign` and account-picker links.

### Changed
- Set Android `versionName` to `1.11` and `versionCode` to `8`.
- The proxy stores only the seven-character yt2009 device code locally. The upstream yt2009 instance handles the Google authorization token and account session.

### Security and compatibility notes
- This is yt2009's legacy `gsign` flow, not standard Google OAuth 2.0.
- The configured instance URL is plain HTTP. The Google authorization token is submitted directly by the upstream sign-in page to that host and can be intercepted by an observer on the network. Use only when the operator and network are trusted.
- The proxy sends the account device identifier only to `femboy.email:730`; the generic yt2009 failover path strips `X-GData-Device` and does not forward it to discovered instances.
- The Android debug APK built successfully in GitHub Actions for commit `72a858f8712dccdb6a4da9c7b3e1e5ad9bcfc594` (run `44706719157`).
- No physical-device sign-in or personalized-feed test is claimed yet. The upstream sign-in and channel-picker flow must be completed in a browser, then verified on the target Android device.

## [1.10] - 2026-11-20

### Added
- Added 44 user-supplied HTTPS-only Invidious candidates in addition to the four instances in the current Invidious documentation list. Each candidate must return a valid `/api/v1/stats` response identifying Invidious before it is selected.
- Added bounded Invidious probing with a six-worker executor to avoid creating one thread per candidate on older Android devices.
- Added `PipedClient` with 15 API endpoints from the current Piped public-instances documentation and 14 additional user-supplied candidates. The client probes `/trending?region=PL` and normalizes Piped search/trending items into the existing feed model.
- Added Piped fallback for legacy search feeds, standard trending feeds, logged-out discovery feeds, and the protobuf activities endpoint.
- Added `/api/piped` diagnostics and Piped status to `/health`.

### Changed
- Set Android `versionName` to `1.10` and `versionCode` to `7`.
- User-supplied HTTP candidates are attempted over HTTPS only; no plaintext HTTP fallback is made.
- Piped requests are read-only and do not send Authorization headers, cookies, or account credentials.

### Compatibility and security notes
- Piped is used only for public search and trending/discovery feeds in this release. Piped proxy URLs are not used for playback.
- The extra community instances are candidates, not endorsed or security-reviewed providers. Their health is checked at runtime; HTTPS support alone does not make an instance trusted.
- Google account sign-in and personalized recommendations are not implemented. The upstream yt2009 `gsign` flow is not standard OAuth and requires handing sensitive authorization data to a third-party server. This release does not forward Google tokens to public instances. A safe authenticated integration requires a separately configured Google OAuth client and YouTube API access.
- No physical-device test of the new Piped feed path is claimed yet.

### Verification status
- The Android debug APK built successfully in GitHub Actions for commit `683bde495c48b0ac001c3cb02724dc5a0669c487` (run `44704171753`).
- The new Piped search/trending paths and the community instance pools still need validation on a physical Android device. Health checks at runtime determine which endpoints are usable.

## [1.9] - 2026-11-20

### Added
- Added `YouTubeV3Proto`, a small protobuf wire-format encoder for the subset of YouTube Data API v3 consumed by the legacy YouTube 5.0.21 client.
- Implemented a protobuf response for `GET /youtube/v3/activities`. The proxy converts Invidious trending videos into upload-style activity records and respects `maxResults` with a limit of 1–50.
- Implemented protobuf responses for `GET /youtube/v3/videos` and `GET /youtube/v3/channels`. Requested IDs are resolved through Invidious and returned as legacy-compatible item records.
- Added a shared response helper that sends protobuf bytes with the `application/x-protobuf` content type.

### Changed
- Set Android `versionName` to `1.9` and `versionCode` to `6`.
- Changed the launcher label from `ytlegacy-proxy 1.7` to `ytlegacy-proxy`; the app name no longer contains a release number.
- Updated the GitHub Actions artifact name to `ytlegacy-proxy-v1.9-debug`.
- Added a GitHub Actions release step for the `v1.9` tag/release. It publishes the APK as `ytlegacy-proxy-v1.9.apk`.
- Updated the README release links and v1.9 notes.

### Compatibility notes
- The `/youtube/v3/players` route still returns an empty protobuf body; player data is handled by the existing legacy player/feed routes.
- This is a targeted protobuf implementation for the old client's observed requests, not a complete implementation of the YouTube Data API v3.

### Verification status
- The Android debug APK build completed successfully in GitHub Actions.
- The GitHub Release `v1.9` was published with the APK attached.
- Protobuf feed rendering has not been confirmed on the physical device. Treat the v3 feed support as best-effort until the client is tested against it.

## [1.8] - 2026-11-19

### Added
- Added a public discovery fallback for logged-out requests to `/feeds/api/users/default/subscriptions` and `/feeds/api/users/default/activity`.
- The default discovery feed first uses Invidious trending videos. If no Invidious instance is healthy, it tries the yt2009 `most_popular` feed, then InnerTube's trending browse feed.
- Added a locally generated neutral profile silhouette for missing channel avatars and the `/avatar_wait` placeholder route.

### Changed
- Channel avatars are now fetched from real channel thumbnail URLs supplied by Invidious, trying available image sizes instead of relying on a single image URL.
- Added alternate Google-host image URL attempts for channel images when the original CDN host fails.
- Channel avatar fallback uses the yt2009 channel profile when available, then the neutral silhouette.
- Removed unrelated Rick Astley video thumbnails as generic avatar/image fallbacks. A missing avatar no longer silently becomes a thumbnail for an unrelated video.
- Changed channel stubs to request the real local `/channel_avatar/{channelId}` endpoint.
- Updated the proxy-reported version to `1.8`, Android `versionName` to `1.8`, and `versionCode` to `5`.
- Renamed the GitHub Actions debug artifact to `ytlegacy-proxy-v1.8-debug` and documented the release in the README.

### Fixed
- Corrected escaping in the channel-image URL extraction regular expression.
- Protocol-relative image URLs from the yt2009 profile are upgraded to HTTPS before fetching.

### Compatibility notes
- The legacy `/youtube/v3/activities` endpoint was changed to return an empty protobuf response rather than a JSON placeholder, preventing protobuf parsing from being fed JSON bytes.
- That v1.8 response was only a compatibility placeholder: it did not contain activity items. Populated protobuf activity/video/channel responses were added as a separate v1.9 change.

---

## Scope

This file documents the v1.8 and v1.9 release changes made during this maintenance pass. Earlier project history (including releases before v1.8) remains in the repository's README and Git history; it is intentionally not reconstructed here as assistant-authored work.
