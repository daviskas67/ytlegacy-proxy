# Changelog

## [1.16] - Unreleased

### Changed
- Объединён патченный YouTube 5.0.21 и ytlegacy-proxy в один APK под исходным package `org.ytlegacy.daviskas`; отдельная установка прокси больше не нужна.
- Локальный `ProxyService` запускается автоматически на старте `Application` и слушает только `127.0.0.1:8080`.
- В исходном экране настроек YouTube добавлен отдельный пункт **ytlegacy** → **ytlegacy settings**, не заменяющий остальные настройки клиента.
- Классы прокси и зависимости объединяются с исходным клиентом в единственный `classes.dex` для совместимости с Android API 8.
- Добавлена повторная проверка итогового APK после декодирования: package, исходный launcher, service, settings preference и автозапуск.

### Fixed
- Исправлены protobuf field numbers для `contentDetails.upload` и вложенного `upload.videoId`: клиент YouTube 5.0.21 ожидает поле `10101` на обоих уровнях. Прежний ответ оставлял объект Upload пустым и приводил к `NullPointerException` в адаптере ленты.
- Добавлен regression-тест, который декодирует ответ `activities` и проверяет реальные номера/типы вложенных protobuf-полей.

### Fixed
- Маршрут `/youtube/v3/videos` больше не зависит только от Invidious: при недоступности Invidious используется Piped, затем yt2009 и InnerTube для метаданных.
- Добавлен разбор метаданных Piped `/streams/{videoId}`, включая автора, описание, длительность, просмотры и лайки; прогрессивный MP4 выбирается только при соблюдении существующих проверок формата.
- Запросы к Invidious/Piped ограничены двумя живыми инстансами и отдельными короткими таймаутами; помеченные нездоровыми Invidious-инстансы больше не используются после окончания cooldown до успешной проверки.
- Для `/youtube/v3/videos` используется metadata-only режим без лишних запросов InnerTube за потоком и без сетевого поиска channel ID по отображаемому имени.
- Добавлены regression-тесты преобразования ответа Piped в модель видео.

### Verification
- CI: JVM unit tests, сборка прокси, объединение APK, single-DEX/method-reference check, round-trip decode и проверка подписи.
- Воспроизведение, перемотка, автозапуск на устройстве и открытие пункта настроек требуют финальной проверки на реальном Android.
- Тестовая сборка подписывается отдельным debug-ключом; для установки поверх оригинального APK потребуется удалить предыдущую копию из-за несовпадения подписей.
## [1.15] - Unreleased

### Changed
- Добавлен единый порядок выбора playback-провайдера: Invidious → Piped → yt2009 community-instance pool → InnerTube; статистика доступна в `/api/sources`.
- Piped `/streams/{id}` добавлен как источник потоков через здоровые API-инстансы. Принимаются только цельные progressive H.264 MP4 (`videoOnly=false`) с HTTPS-origin, совпадающим с `proxyUrl` из ответа.
- yt2009 `fmt_stream_map` больше не переписывает внутренние ссылки на один фиксированный хост. Используется инстанс, вернувший `get_video_info`, затем ограниченный пул остальных доступных HTTPS-инстансов.
- Поддержаны Range, 206 и 416 на всех playback-провайдерах; добавлены тесты на перемотку и форматные фильтры.
- Версия Android package, health endpoint и service notification обновлены до 1.15.

### Verification
- Unit tests and APK build are being checked by GitHub Actions for this pull request.
- Actual audio playback, seeking, and quality switching still require testing on a physical legacy Android device.

## [1.14] - 2026-11-20

### Changed
- Made yt2009 account linking explicitly optional in the Android UI and documentation. Public search, discovery feeds, channel/video metadata, and playback continue to use public providers without an account.
- Kept personalized yt2009 recommendations as an opt-in enhancement only; the account device identifier remains restricted to the pinned upstream instance.
- Updated the proxy, foreground notification, Android package, CI artifact, and manual APK build metadata to version 1.14.

### Fixed
- Handled unavailable yt2009 failover connections without null dereferences in metadata and streaming requests.
- Closed yt2009 metadata connections and bounded the negative stream cache.
- Limited discovered yt2009 fallback instances to HTTPS and bounded/closed the instance-list response.
- Closed upstream image/video connections when responses finish, and rejected malformed or oversized image responses.
- Validated video IDs before building feeds and made relative-time/duration parsing robust against malformed upstream values.
- Escaped dynamic values in generated Atom XML and preserved actual video categories in feed entries.

### Verification
- GitHub Actions Android build is run for this branch; physical-device playback and upstream-provider availability still require device testing.
- The legacy upstream gsign token handoff remains a third-party HTTP flow. No browser interception or automatic token extraction has been added.

All notable changes recorded here are for the releases updated during the current maintenance work. Dates use YYYY-MM-DD.

## [1.13] - 2026-11-20

### Added
- Added one-tap sharing of the yt2009 sign-in URL, device code, and setup checklist from the Android sign-in dialog.
- Kept a separate copy-device-code action for manual transfer.
- The app refreshes account status after returning from the upstream channel picker and displays the selected channel.
- Simplified the sign-in action labels.

### Security and compatibility notes
- The app does not capture browser traffic or extract Google authorization tokens. The manual upstream `oauth2_4` step remains part of yt2009's gsign flow.
- Google account passwords should only be entered on Google's `accounts.google.com` page. The upstream token handoff to the selected yt2009 instance still uses plain HTTP.
- The Android debug APK built successfully in GitHub Actions for commit `efa6ce20975a2030623b60caaf96470c2ba29c25` (run `44711183134`); artifact `ytlegacy-proxy-v1.13-debug` was uploaded.
- No physical-device login test is claimed. The upstream gsign manual step still needs to be completed on the target device/browser.

## [1.12] - 2026-11-20

### Added
- Added automatic polling of the yt2009 account-link status every five seconds for up to 15 minutes after starting gsign.
- When an account becomes available, the app stops polling and prompts the user to open the YouTube channel picker.
- Polling pauses while the Activity is backgrounded and resumes when it returns; the current device code and account state are restored from the local proxy after relaunch.

### Security and compatibility notes
- The user still completes the upstream Google sign-in steps on the yt2009 PC page. The upstream flow requires manually copying its `oauth2_4` value; this release does not intercept browser traffic or automatically extract Google tokens.
- This is a UX automation layer around yt2009's existing gsign protocol, not a replacement authentication protocol.
- The automatic polling and lifecycle changes compiled successfully in GitHub Actions (run `44709884802`). A final v1.12 release build is run by the `release v1.12` commit. Physical-device sign-in remains unverified.

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
- The v1.11 release workflow publishes the signed debug APK as `ytlegacy-proxy-v1.11.apk`.
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
