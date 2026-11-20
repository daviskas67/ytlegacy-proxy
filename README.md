# ytlegacy-proxy

**A local compatibility proxy for the legacy YouTube 5.0.21 Android client.**  
**Локальный прокси совместимости для старого Android-клиента YouTube 5.0.21.**

[English](#english) · [Русский](#русский) · [Latest release](https://github.com/daviskas67/ytlegacy-proxy/releases/tag/v1.11)

---

<a id="русский"></a>

## Русский

> 🎉 **ytlegacy-proxy v1.11 уже вышел!**  
> Свежий APK доступен в [GitHub Releases](https://github.com/daviskas67/ytlegacy-proxy/releases/tag/v1.11). В этом выпуске добавлены привязка Google-аккаунта через yt2009 gsign и персональные рекомендации, а также расширенные пулы Invidious и Piped.


### v1.11

- Добавлено подключение Google-аккаунта через штатный legacy-процесс yt2009 `gsign` на `femboy.email:730`.
- Приложение запрашивает семизначный device code, показывает адрес страницы для завершения входа на ПК и умеет проверять состояние привязки.
- Добавлена кнопка выбора YouTube-канала через upstream `account_picker.html`.
- Запросы публичных рекомендаций `/feeds/api/users/default/subscriptions` и `activity` сначала пытаются получить персональную ленту с `/feeds/api/users/default/recommendations`.
- Идентификатор устройства передаётся только на закреплённый `femboy.email:730`; он не отправляется в пулы Invidious, Piped или случайные yt2009-инстансы.
- Добавлены локальные endpoints `/api/yt2009/login/start`, `/api/yt2009/login/status` и `/api/yt2009/login/picker`.

**Важно:** `gsign` — не стандартный OAuth 2.0. В upstream-процессе пользователь вручную передаёт Google authorization token серверу yt2009. Указанный инстанс использует обычный HTTP, поэтому токен и данные сессии могут быть перехвачены при наблюдении за сетевым трафиком. Используй этот вход только потому, что доверяешь именно этому хосту и сети. Сам прокси хранит локально только device code; обработка токена и аккаунтная сессия остаются на yt2009.

### v1.10

- Добавлены 44 пользовательских кандидата Invidious поверх четырёх экземпляров, перечисленных в официальной документации. Каждый кандидат проверяется через `/api/v1/stats`.
- Проверка большого пула ограничена шестью параллельными потоками, чтобы не создавать десятки соединений на старых Android-устройствах.
- Добавлен отдельный пул Piped: 15 адресов из актуальной документации и 14 дополнительных кандидатов.
- Piped подключён как резервный источник публичного поиска и trending-лент; его ответы переводятся в общую модель видео.
- Добавлены диагностика `/api/piped` и объединённый статус источников в `/health`.
- Кандидаты, переданные как HTTP, проверяются только по HTTPS. Если HTTPS не работает или сервер не отдаёт ожидаемый API-ответ, он пропускается.

### v1.9

- Добавлены protobuf-ответы для `/youtube/v3/activities`, `/youtube/v3/videos` и `/youtube/v3/channels`, которые запрашивает старый клиент.
- Для `/youtube/v3/activities` реализована выдача protobuf-записей на основе Invidious Trending.
- Для `/youtube/v3/videos` и `/youtube/v3/channels` добавлена выдача protobuf-метаданных по запрошенным ID.
- Это целевая поддержка используемого старым клиентом подмножества API; отображение ленты рекомендаций на устройстве всё ещё требует отдельной проверки.

### v1.8

- Реальные аватары каналов загружаются из Invidious; перед переходом к заглушке проверяются доступные размеры изображения.
- Rick Astley удалён из резервных изображений. Вместо него используется нейтральная иконка профиля.
- Публичная лента для неавторизованного клиента заполняется популярными видео из Invidious или Piped.

### О проекте

**ytlegacy-proxy** — Android-приложение, которое выступает промежуточным сервером между модифицированным клиентом YouTube 5.0.21 и современными источниками данных. Оно принимает старые запросы в формате YouTube GData/Atom и преобразует ответы Invidious в формат, который понимает клиент.

Прокси запускает локальный HTTP-сервер на `127.0.0.1:8080`. Внешние сервисы используются как источники данных, а сам клиент обращается к локальному адресу.

### Возможности

- **Invidious — основной источник данных.** При запуске прокси параллельно проверяет настроенные экземпляры и измеряет задержку непосредственно с устройства.
- **Динамический выбор сервера.** Запросы сначала отправляются на самый быстрый доступный экземпляр. Медленные, недоступные, ограничивающие запросы или возвращающие серверные ошибки экземпляры временно пропускаются. Проверка серверов повторяется периодически.
- **Поддержка основных разделов YouTube.** Поиск, информация о видео, похожие ролики, плейлисты, популярные видео, каналы, загрузки и комментарии используют Invidious в первую очередь.
- **Воспроизведение видео.** Прокси обрабатывает progressive MP4-потоки и передаёт byte-range запросы, необходимые для перемотки.
- **Резервные источники.** Piped используется для публичного поиска и трендов, когда Invidious не вернул данные. yt2009 и InnerTube остаются последующими резервами для существующих legacy-маршрутов и потоков.
- **Диагностика.** Локальные API показывают состояние сервера, выбранные экземпляры Invidious/Piped, задержки и статистику кэша.

### Пулы Invidious и Piped

Для Invidious клиент проверяет четыре экземпляра из [официального списка](https://docs.invidious.io/instances/) и 44 дополнительных пользовательских кандидата. Для Piped используется 15 API-адресов из [документации публичных экземпляров](https://docs.piped.video/docs/public-instances/) и 14 дополнительных кандидатов.

Все адреса проверяются непосредственно с устройства. Проверка Invidious использует `/api/v1/stats`, Piped — `/trending?region=PL`. В обоих пулах максимум шесть проверок выполняются одновременно. Адреса из пользовательского списка были переданы как HTTP, но прокси пробует только HTTPS: неподдерживающие TLS серверы будут отмечены как недоступные.

Дополнительные пользовательские инстансы не являются рекомендацией или проверкой их безопасности. В официальной документации Invidious отдельно предупреждают, что публичные инстансы вне её списка следует считать недоверенными. Не используй случайные публичные инстансы для передачи личных данных или авторизационных токенов.

Piped сейчас служит только резервом для публичного поиска и трендовых лент. Его proxy-адреса не используются для авторизации или воспроизведения видео в этой версии.

### Авторизация аккаунта

Войти можно из главного экрана приложения: **Start Google account sign-in** → завершить upstream-процесс на ПК → **Check sign-in status** → **Select YouTube channel**. После привязки прокси запрашивает персональную ленту yt2009 для старого клиента.

Локальные endpoints:
- `POST /api/yt2009/login/start` — создать device code.
- `GET /api/yt2009/login/status` — проверить привязку и получить список каналов.
- `GET /api/yt2009/login/picker` — открыть upstream channel picker.

Это именно legacy `gsign`, а не стандартный OAuth 2.0. Он передаёт Google authorization token на выбранный сервер yt2009 по обычному HTTP. Не используй его в недоверенной сети. Прокси не хранит Google token и отправляет device identifier только на `femboy.email:730`.

### Диагностика

После запуска сервера открой соответствующий адрес на устройстве:

| Endpoint | Назначение |
| --- | --- |
| `/health` | Общий статус прокси и резервного источника |
| `/api/status` | Состояние сервиса |
| `/api/invidious` | Состояние каждого Invidious-экземпляра, задержка, ошибки и выбранный сервер |
| `/api/piped` | Состояние каждого Piped API-инстанса и задержки |
| `/api/yt2009/login/status` | Состояние привязки yt2009 и список каналов |
| `/api/cache` | Статистика локального кэша |
| `/api/video/{id}` | Диагностика метаданных конкретного видео |

Полные адреса:

- [http://127.0.0.1:8080/health](http://127.0.0.1:8080/health)
- [http://127.0.0.1:8080/api/status](http://127.0.0.1:8080/api/status)
- [http://127.0.0.1:8080/api/invidious](http://127.0.0.1:8080/api/invidious)
- [http://127.0.0.1:8080/api/piped](http://127.0.0.1:8080/api/piped)
- [http://127.0.0.1:8080/api/yt2009/login/status](http://127.0.0.1:8080/api/yt2009/login/status)
- [http://127.0.0.1:8080/api/cache](http://127.0.0.1:8080/api/cache)
- [http://127.0.0.1:8080/api/video/VIDEO_ID](http://127.0.0.1:8080/api/video/VIDEO_ID)

Чтобы очистить локальные кэши метаданных, отправь **POST**-запрос на:

```text
http://127.0.0.1:8080/api/cache/clear
```

### Сборка

Android-проект находится в каталоге `ytlegacy-android/`.

**Требования:**

- JDK 17
- Android SDK Platform 30
- Android Build Tools 29.0.3
- Android Gradle Plugin 7.4.2
- Gradle 7.5.1

Сборка Debug APK:

```sh
cd ytlegacy-android
gradle --no-daemon :app:assembleDebug
```

Готовый APK появится здесь:

```text
app/build/outputs/apk/debug/app-debug.apk
```

GitHub Actions загружает APK как артефакт `ytlegacy-proxy-v1.11-debug` (доступен 14 дней) и публикует v1.11 в [GitHub Releases](https://github.com/daviskas67/ytlegacy-proxy/releases/tag/v1.11).

Проверочная сборка v1.11 прошла успешно: [GitHub Actions run #44706719157](https://github.com/daviskas67/ytlegacy-proxy/actions/runs/44706719157).

Дополнительный скрипт `build-manual.sh` рассчитан на окружения, где уже доступны Android SDK tools, D8, Gson 2.2.4 и NanoHTTPD 2.3.1 JAR-файлы.

### История версий

<details>
<summary><strong>v1.7</strong> — динамический пул Invidious</summary>

- Параллельная проверка четырёх экземпляров Invidious при запуске.
- Измерение задержки непосредственно с устройства и периодическое обновление списка доступных серверов.
- Invidious используется как основной источник для поиска, видео, лент, каналов, комментариев и потоков.
- yt2009 вызывается только при отсутствии доступных экземпляров Invidious.
- InnerTube остаётся дополнительным резервом для отдельных данных и потоков.
- Добавлен endpoint `/api/invidious` с подробной статистикой серверов.
- Добавлена обработка метаданных и progressive MP4 из Invidious.

</details>

<details>
<summary><strong>v1.6</strong> — расширение API и резервных источников</summary>

- Подсказки поиска через старый endpoint `/complete/search`, ограниченный кэш на 32 запроса и короткое время хранения.
- Ленты видео плейлистов через yt2009 с резервным InnerTube browse.
- Современный источник популярных видео для старых лент `most_popular`, `top_rated` и `recently_featured`.
- Прямой резервный источник Googlevideo, когда yt2009 не возвращает URL видео.
- Диагностика `/api/video/{id}`, статистика `/api/cache` и очистка через `POST /api/cache/clear`.
- Улучшена отчётность о переключении на резервные источники.

</details>

<details>
<summary><strong>v1.5</strong> — InnerTube fallback и диагностика</summary>

- Основной legacy-маршрут yt2009/GData с резервными запросами InnerTube для поиска, похожих видео, страниц каналов, загрузок и недостающих метаданных плеера.
- Добавлены `/health` и `/api/status`.
- Улучшена совместимость уведомлений с Android.
- Улучшена обработка ошибок запуска сервера.
- Ограничены размеры текстовых ответов от upstream-сервисов и улучшено закрытие соединений.
- Убрана генерация фиктивных ID каналов.

</details>

### Примечания

Invidious — неофициальный API совместимости. Публичные экземпляры могут менять адреса, ограничивать запросы или прекращать работу. Доступность и задержка проверяются самим приложением с устройства. Прокси разрешает потоковые HTTPS-адреса только от настроенных хостов Invidious и Google `*.googlevideo.com`.

---

<a id="english"></a>

## English

> 🎉 **ytlegacy-proxy v1.11 is here!**  
> Get the latest APK from [GitHub Releases](https://github.com/daviskas67/ytlegacy-proxy/releases/tag/v1.11). This release adds yt2009 gsign account linking, channel selection, and personalized recommendations alongside the expanded Invidious and Piped pools.


### v1.11

- Added Google account linking through the upstream yt2009 legacy `gsign` flow on `femboy.email:730`.
- The app requests a seven-character device code, displays the desktop sign-in URL, and can check whether the account has been linked.
- Added a YouTube channel picker using the upstream `account_picker.html` page.
- Legacy public recommendations routes `/feeds/api/users/default/subscriptions` and `activity` now try the personalized `/feeds/api/users/default/recommendations` feed first.
- The device identifier is sent only to the pinned `femboy.email:730` host. It is never sent to Invidious, Piped, or dynamically discovered yt2009 instances.
- Added local endpoints `/api/yt2009/login/start`, `/api/yt2009/login/status`, and `/api/yt2009/login/picker`.

**Important:** `gsign` is not standard OAuth 2.0. The upstream flow has the user manually submit a Google authorization token to the yt2009 server. The configured instance uses plain HTTP, so the token and session data can be intercepted by anyone able to observe that network traffic. Use this only because you trust this specific host and network. The proxy stores only the local device code; token processing and the account session stay on yt2009.

### v1.10

- Added 44 user-supplied Invidious candidates on top of the four instances listed in the official documentation. Each candidate is validated through `/api/v1/stats`.
- Bounded large-pool probing to six concurrent workers to avoid creating dozens of connections on older Android devices.
- Added a separate Piped pool with 15 API endpoints from its current documentation and 14 additional candidates.
- Integrated Piped as a secondary source for public search and trending feeds, normalizing its responses into the proxy's shared video model.
- Added the `/api/piped` diagnostics endpoint and combined provider status in `/health`.
- User-supplied HTTP URLs are tried over HTTPS only. Candidates are skipped when HTTPS or the expected API response is unavailable.

### v1.9

- Added protobuf responses for `/youtube/v3/activities`, `/youtube/v3/videos`, and `/youtube/v3/channels`, which are requested by the legacy client.
- Added protobuf activity records for `/youtube/v3/activities`, using Invidious Trending as the source.
- Added protobuf metadata responses for requested IDs on `/youtube/v3/videos` and `/youtube/v3/channels`.
- This targets the subset of the API used by the legacy client; actual recommendations-feed rendering on a device still needs verification.

### v1.8

- Channel avatars are fetched from Invidious, trying available image sizes before falling back.
- The Rick Astley image fallback is removed and replaced with a neutral profile icon.
- The empty subscriptions feed for a signed-out client is populated with popular Invidious videos.

### About

**ytlegacy-proxy** is an Android companion app that acts as a bridge between a modified YouTube 5.0.21 client and modern data providers. It accepts legacy YouTube GData/Atom requests and converts Invidious responses into a format understood by the old client.

The proxy starts a local HTTP server at `127.0.0.1:8080`. The client communicates with this local endpoint while the proxy retrieves data from external providers.

### Features

- **Invidious as the primary provider.** At startup, the proxy probes the configured instances in parallel and measures latency directly from the Android device.
- **Dynamic server selection.** Requests are routed to the fastest healthy instance. Slow, unavailable, rate-limited, or server-error instances are temporarily skipped; health checks run periodically.
- **Legacy YouTube features.** Search, video metadata, related videos, playlists, trending feeds, channel pages, uploads, and comments use Invidious first.
- **Video playback.** Progressive MP4 streams are supported, including byte-range forwarding for seeking.
- **Fallback providers.** Piped supplies public search and trending data when Invidious cannot. yt2009 and InnerTube remain later fallbacks for existing legacy routes and streams.
- **Diagnostics.** Local endpoints expose proxy status, Invidious/Piped instance health, selected servers, latency, and cache statistics.

### Invidious and Piped pools

For Invidious, the client checks the four instances in the [official list](https://docs.invidious.io/instances/) plus 44 additional user-supplied candidates. For Piped, it checks 15 API endpoints from the [public instances documentation](https://docs.piped.video/docs/public-instances/) plus 14 additional candidates.

All health checks run directly from the device. Invidious is validated through `/api/v1/stats`; Piped is validated through `/trending?region=PL`. Both pools are limited to six concurrent checks. The supplied community URLs used HTTP, but the proxy tries HTTPS only; servers without working TLS are marked unavailable.

The additional community hosts are candidates, not endorsements or security reviews. Invidious documentation explicitly warns that public instances outside its list should be considered untrusted. Do not send personal data or account authorization tokens to random public instances.

Piped is currently used only as a fallback for public search and trending feeds. Its proxy URLs are not used for account authorization or video playback in this version.

### Account authorization

From the app's main screen use **Start Google account sign-in**, complete the upstream flow on a PC, then tap **Check sign-in status** and **Select YouTube channel**. Once linked, the proxy requests the personalized yt2009 recommendations feed for the legacy client.

Local endpoints:
- `POST /api/yt2009/login/start` — create a device code.
- `GET /api/yt2009/login/status` — check linking and retrieve available channels.
- `GET /api/yt2009/login/picker` — open the upstream channel picker.

This is yt2009's legacy `gsign` flow, not standard OAuth 2.0. It sends a Google authorization token to the selected yt2009 server over plain HTTP. Do not use it on an untrusted network. The proxy does not store the Google token and sends the device identifier only to `femboy.email:730`.

### Diagnostics

After the service starts, open the relevant endpoint on the device:

| Endpoint | Description |
| --- | --- |
| `/health` | Overall proxy and fallback-provider health |
| `/api/status` | Service status |
| `/api/invidious` | Per-instance health, latency, failures, and selected server |
| `/api/piped` | Per-instance Piped API health and latency |
| `/api/yt2009/login/status` | yt2009 account-link status and available channels |
| `/api/cache` | Local cache statistics |
| `/api/video/{id}` | Metadata diagnostics for a video |

Full local URLs:

- [http://127.0.0.1:8080/health](http://127.0.0.1:8080/health)
- [http://127.0.0.1:8080/api/status](http://127.0.0.1:8080/api/status)
- [http://127.0.0.1:8080/api/invidious](http://127.0.0.1:8080/api/invidious)
- [http://127.0.0.1:8080/api/cache](http://127.0.0.1:8080/api/cache)
- [http://127.0.0.1:8080/api/video/VIDEO_ID](http://127.0.0.1:8080/api/video/VIDEO_ID)

To clear local metadata caches, send a **POST** request to:

```text
http://127.0.0.1:8080/api/cache/clear
```

### Build

The Android project is located in `ytlegacy-android/`.

**Requirements:**

- JDK 17
- Android SDK Platform 30
- Android Build Tools 29.0.3
- Android Gradle Plugin 7.4.2
- Gradle 7.5.1

Build the debug APK:

```sh
cd ytlegacy-android
gradle --no-daemon :app:assembleDebug
```

The APK is generated at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

GitHub Actions uploads the APK as the `ytlegacy-proxy-v1.11-debug` artifact (available for 14 days) and publishes the v1.11 build to [GitHub Releases](https://github.com/daviskas67/ytlegacy-proxy/releases/tag/v1.11).

The v1.11 verification build passed: [GitHub Actions run #44706719157](https://github.com/daviskas67/ytlegacy-proxy/actions/runs/44706719157).

The optional `build-manual.sh` script is intended for environments that already provide Android SDK tools, D8, Gson 2.2.4, and NanoHTTPD 2.3.1 JARs.

### Version history

<details>
<summary><strong>v1.7</strong> — dynamic Invidious pool</summary>

- Parallel startup checks for the four configured Invidious instances.
- On-device latency measurements and periodic health refresh.
- Invidious-first support for search, video data, feeds, channels, comments, and streams.
- yt2009 is used only when no Invidious instance is healthy.
- InnerTube remains an additional fallback for specific data and streams.
- Added the `/api/invidious` endpoint with detailed instance metrics.
- Added Invidious video metadata parsing and progressive MP4 support.

</details>

<details>
<summary><strong>v1.6</strong> — expanded API and fallback support</summary>

- Live search suggestions through the legacy `/complete/search` endpoint, with a bounded 32-query cache and short TTL.
- Playlist video feeds through yt2009 with an InnerTube browse fallback.
- Modern trending-page fallback for legacy `most_popular`, `top_rated`, and `recently_featured` feeds.
- Direct Googlevideo stream fallback when yt2009 cannot provide a video URL.
- Added `/api/video/{id}` metadata diagnostics, `/api/cache` statistics, and `POST /api/cache/clear`.
- Improved fallback reporting.

</details>

<details>
<summary><strong>v1.5</strong> — InnerTube fallback and diagnostics</summary>

- Legacy yt2009/GData path with best-effort InnerTube fallbacks for search, related videos, channel pages, uploads, and missing player metadata.
- Added `/health` and `/api/status`.
- Improved Android notification compatibility.
- Improved server startup error handling.
- Bounded upstream text responses and improved connection cleanup.
- Removed fake channel ID generation.

</details>

### Notes

Invidious is an unofficial, best-effort compatibility API. Public instances may change their addresses, rate-limit requests, or become unavailable. The app checks availability and latency from the device itself. For streaming, the proxy only permits HTTPS URLs from the configured Invidious hosts and Google's `*.googlevideo.com` media hosts.

---

<p align="center">
  <sub>Made for keeping a little piece of the old YouTube experience running.</sub>
</p>
