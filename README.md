# ytlegacy-proxy

**A local compatibility proxy for the legacy YouTube 5.0.21 Android client.**  
**Локальный прокси совместимости для старого Android-клиента YouTube 5.0.21.**

[English](#english) · [Русский](#русский) · [Latest release](https://github.com/daviskas67/ytlegacy-proxy/releases/tag/v1.7)

---

<a id="русский"></a>

## Русский

### v1.9

- Добавлены protobuf-ответы для `/youtube/v3/activities`, `/youtube/v3/videos` и `/youtube/v3/channels`, которые запрашивает старый клиент.
- Лента рекомендаций формируется из популярных роликов Invidious, а карточки дополняются метаданными видео и каналов.
- Исправлен MIME-тип ответов YouTube Data API v3: клиент получает protobuf вместо JSON-заглушки.

### v1.8

- Реальные аватары каналов загружаются из Invidious; перед переходом к заглушке проверяются доступные размеры изображения.
- Rick Astley удалён из резервных изображений. Вместо него используется нейтральная иконка профиля.
- Пустая лента подписок для неавторизованного клиента заполняется популярными видео из Invidious.

### О проекте

**ytlegacy-proxy** — Android-приложение, которое выступает промежуточным сервером между модифицированным клиентом YouTube 5.0.21 и современными источниками данных. Оно принимает старые запросы в формате YouTube GData/Atom и преобразует ответы Invidious в формат, который понимает клиент.

Прокси запускает локальный HTTP-сервер на `127.0.0.1:8080`. Внешние сервисы используются как источники данных, а сам клиент обращается к локальному адресу.

### Возможности

- **Invidious — основной источник данных.** При запуске прокси параллельно проверяет настроенные экземпляры и измеряет задержку непосредственно с устройства.
- **Динамический выбор сервера.** Запросы сначала отправляются на самый быстрый доступный экземпляр. Медленные, недоступные, ограничивающие запросы или возвращающие серверные ошибки экземпляры временно пропускаются. Проверка серверов повторяется периодически.
- **Поддержка основных разделов YouTube.** Поиск, информация о видео, похожие ролики, плейлисты, популярные видео, каналы, загрузки и комментарии используют Invidious в первую очередь.
- **Воспроизведение видео.** Прокси обрабатывает progressive MP4-потоки и передаёт byte-range запросы, необходимые для перемотки.
- **Резервные источники.** yt2009 используется только тогда, когда среди экземпляров Invidious нет доступных. InnerTube остаётся дополнительным резервом, когда доступный основной источник не может предоставить конкретный поток или ленту.
- **Диагностика.** Локальные API показывают состояние сервера, выбранный экземпляр Invidious, задержки и статистику кэша.

### Пул Invidious

Сейчас клиент проверяет четыре настроенных экземпляра:

| Экземпляр | Адрес |
| --- | --- |
| nerdvpn | [invidious.nerdvpn.de](https://invidious.nerdvpn.de) |
| tiekoetter | [invidious.tiekoetter.com](https://invidious.tiekoetter.com) |
| chocolatemoo53 | [yt.chocolatemoo53.com](https://yt.chocolatemoo53.com) |
| nadeko | [inv.nadeko.net](https://inv.nadeko.net) |

Порядок выбора определяется замерами с конкретного устройства. Публичные мониторинги могут помочь предварительно оценить доступность серверов, но не гарантируют такую же скорость в каждой сети.

### Диагностика

После запуска сервера открой соответствующий адрес на устройстве:

| Endpoint | Назначение |
| --- | --- |
| `/health` | Общий статус прокси и резервного источника |
| `/api/status` | Состояние сервиса |
| `/api/invidious` | Состояние каждого Invidious-экземпляра, задержка, ошибки и выбранный сервер |
| `/api/cache` | Статистика локального кэша |
| `/api/video/{id}` | Диагностика метаданных конкретного видео |

Полные адреса:

- [http://127.0.0.1:8080/health](http://127.0.0.1:8080/health)
- [http://127.0.0.1:8080/api/status](http://127.0.0.1:8080/api/status)
- [http://127.0.0.1:8080/api/invidious](http://127.0.0.1:8080/api/invidious)
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

GitHub Actions загружает APK как артефакт `ytlegacy-proxy-v1.9-debug` (доступен 14 дней) и публикует сборку v1.7 в [GitHub Releases](https://github.com/daviskas67/ytlegacy-proxy/releases/tag/v1.7).

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

### v1.9

- Added protobuf responses for `/youtube/v3/activities`, `/youtube/v3/videos`, and `/youtube/v3/channels`, which are requested by the legacy client.
- The recommendations feed is populated from Invidious trending videos, with video and channel metadata supplied for feed cards.
- Corrected the YouTube Data API v3 response MIME type so the client receives protobuf instead of JSON placeholders.

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
- **Fallback providers.** yt2009 is used only when no Invidious instance is healthy. InnerTube remains an additional fallback when a healthy primary provider cannot supply a particular feed or stream.
- **Diagnostics.** Local endpoints expose proxy status, Invidious instance health, selected server, latency, and cache statistics.

### Invidious pool

The client currently probes these four configured instances:

| Instance | Address |
| --- | --- |
| nerdvpn | [invidious.nerdvpn.de](https://invidious.nerdvpn.de) |
| tiekoetter | [invidious.tiekoetter.com](https://invidious.tiekoetter.com) |
| chocolatemoo53 | [yt.chocolatemoo53.com](https://yt.chocolatemoo53.com) |
| nadeko | [inv.nadeko.net](https://inv.nadeko.net) |

The selection order is based on measurements taken from each device. Public monitoring data can help shortlist providers, but it cannot predict performance on every user's network.

### Diagnostics

After the service starts, open the relevant endpoint on the device:

| Endpoint | Description |
| --- | --- |
| `/health` | Overall proxy and fallback-provider health |
| `/api/status` | Service status |
| `/api/invidious` | Per-instance health, latency, failures, and selected server |
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

GitHub Actions uploads the APK as the `ytlegacy-proxy-v1.9-debug` artifact (available for 14 days) and publishes the v1.7 build to [GitHub Releases](https://github.com/daviskas67/ytlegacy-proxy/releases/tag/v1.7).

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
