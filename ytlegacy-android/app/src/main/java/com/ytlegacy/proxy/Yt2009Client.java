package com.ytlegacy.proxy;

import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import com.google.gson.reflect.TypeToken;
import fi.iki.elonen.NanoHTTPD;

public class Yt2009Client {
    private static final String TAG = "Yt2009";
    private static final String BASE = "http://femboy.email:730";
    private static final String INTERNAL_HOST = "68.172.220.11:730";
    private static final String LOCAL_BASE = "http://127.0.0.1:8080";
    private final java.util.Map<String, Long> failedVideos = new java.util.concurrent.ConcurrentHashMap<String, Long>();
    private static final long NEG_CACHE_TTL = 60000L;  // 60 сек

    private List<String> availableInstances = new ArrayList<String>();
    private int currentInstanceIndex = 0;
    private Map<String, Long> deadInstances = new HashMap<String, Long>();
    private static final long DEAD_TTL = 300000L;
    private static final String FALLBACK_INSTANCE = "http://femboy.email:730";
    private static final String INSTANCES_JSON_URL = "http://144.31.189.129/notPipe.json";
    private static final String BROWSER_UA =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
        "(KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36";

    public static class Info {
        public String title = "";
        public String author = "";
        public String status = "";
        public int lengthSeconds = 0;
        public Map<Integer, String> fmtUrls = new HashMap<>();
    }

    /** Открывает соединение с правильными заголовками. Если session != null — пробрасывает Range. */
    public HttpURLConnection open(String path, NanoHTTPD.IHTTPSession session) throws Exception {
        URL u = new URL(BASE + path);
        HttpURLConnection c = (HttpURLConnection) u.openConnection();
        c.setConnectTimeout(10000);
        c.setReadTimeout(25000);
        c.setRequestProperty("User-Agent", BROWSER_UA);
        c.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        c.setRequestProperty("Accept-Language", "en-US,en;q=0.9");
        c.setRequestProperty("Referer", BASE + "/");
        if (session != null) {
            String range = session.getHeaders().get("range");
            if (range != null) c.setRequestProperty("Range", range);
        }
        return c;
    }

    /**
     * Fetches a personalized account feed only from the explicitly configured
     * femboy.email instance. Unlike openWithFailover(), this method never sends
     * the account device identifier to any discovered community instance.
     */
    public String fetchAccountFeed(String path, String deviceId) {
        if (deviceId == null || !deviceId.matches("[A-Za-z0-9]{7}")) {
            Log.w(TAG, "Personal feed skipped: invalid device id");
            return null;
        }
        HttpURLConnection connection = null;
        try {
            String suffix = path == null ? "" : path;
            if (!suffix.startsWith("/")) suffix = "/" + suffix;
            URL target = new URL(BASE + suffix);
            connection = (HttpURLConnection) target.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(7000);
            connection.setReadTimeout(20000);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestProperty("User-Agent",
                "com.google.android.youtube/5.0.21 (Linux; U; Android 4.4.2; en_US)");
            connection.setRequestProperty("Accept", "application/atom+xml, application/xml;q=0.9, */*;q=0.8");
            connection.setRequestProperty("Accept-Language", "en-US,en;q=0.8");
            connection.setRequestProperty("Referer", BASE + "/");
            connection.setRequestProperty("X-GData-Device", "device-id=\"" + deviceId + "\"");
            
            int status = connection.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                Log.w(TAG, "Personal feed " + suffix + " -> HTTP " + status);
                return null;
            }
            String body = readBody(connection);
            if (body == null || body.length() == 0) return null;
            String rewritten = rewriteUrls(body);
            Log.i(TAG, "Personal yt2009 feed OK (" + rewritten.length() + " chars)");
            return rewritten;
        } catch (Exception e) {
            Log.w(TAG, "Personal yt2009 feed failed: " + e.getMessage());
            return null;
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    /** Passthrough: тянет текст с yt2009 и переписывает URL на локальные. Возвращает null при ошибке. */
    public String fetchText(String path) {
        for (int attempt = 1; attempt <= 2; attempt++) {
            HttpURLConnection connection = null;
            try {
                connection = openWithFailover(path, null);
                if (connection == null) continue;
                int code = connection.getResponseCode();
                if (code != 200) {
                    Log.w(TAG, "fetch " + path + " -> HTTP " + code + " (attempt " + attempt + ")");
                    continue;
                }
                String body = readBody(connection);
                if (body == null || body.isEmpty()) continue;
                String rewritten = rewriteUrls(body);
                int viUrls = countOccurrences(rewritten, LOCAL_BASE + "/vi/");
                int imgUrls = countOccurrences(rewritten, LOCAL_BASE + "/img/");
                int leftYtimg = countOccurrences(rewritten, "ytimg") + countOccurrences(rewritten, "img.youtube");
                Log.i(TAG, "fetch " + path + " OK (" + rewritten.length() + "b, vi=" + viUrls
                    + ", img=" + imgUrls + ", left_ytimg=" + leftYtimg + ", attempt=" + attempt + ")");
                return rewritten;
            } catch (java.io.EOFException e) {
                Log.w(TAG, "fetch " + path + " EOFException (attempt " + attempt + "), retrying");
            } catch (java.net.SocketTimeoutException e) {
                Log.w(TAG, "fetch " + path + " timeout (attempt " + attempt + ")");
            } catch (Exception e) {
                Log.e(TAG, "fetch " + path + " error (attempt " + attempt + "): " + e);
            } finally {
                if (connection != null) connection.disconnect();
            }
        }
        Log.e(TAG, "fetch " + path + " FAILED after 2 attempts");
        return null;
    }

    private static final int MAX_TEXT_RESPONSE_BYTES = 8 * 1024 * 1024;

    private String readBody(HttpURLConnection c) throws Exception {
        long contentLength = c.getContentLength();
        if (contentLength > MAX_TEXT_RESPONSE_BYTES) {
            throw new java.io.IOException("Response exceeds " + MAX_TEXT_RESPONSE_BYTES + " bytes");
        }
        java.io.InputStream is = new java.io.BufferedInputStream(c.getInputStream(), 8192);
        java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        int n;
        try {
            while ((n = is.read(buf)) != -1) {
                if (bos.size() + n > MAX_TEXT_RESPONSE_BYTES) {
                    throw new java.io.IOException("Response exceeds " + MAX_TEXT_RESPONSE_BYTES + " bytes");
                }
                bos.write(buf, 0, n);
            }
        } finally {
            is.close();
        }
        return new String(bos.toByteArray(), "UTF-8");
    }

    /** Заменяет все внешние URL yt2009 на локальный прокси. */
    public String rewriteUrls(String s) {
        if (s == null) return null;
        // inject self links for videos in feeds (Astra spec)
        try {
            // Сначала заменяем URL (БЕЗ голого gdata.youtube.com - ломает неймспейсы!)
            s = s.replace("http://gdata.youtube.com/feeds/", LOCAL_BASE + "/feeds/");
            s = s.replace("https://gdata.youtube.com/feeds/", LOCAL_BASE + "/feeds/");
            s = s.replace("//gdata.youtube.com/feeds/", LOCAL_BASE + "/feeds/");

            // Быстрая вставка self links без регулярки (для Android 4.4)
            int start = 0;
            while ((start = s.indexOf("<entry>", start)) != -1) {
                int end = s.indexOf("</entry>", start);
                if (end == -1) break;
                String entry = s.substring(start + 7, end);
                String vid = null;
                
                int ytIdx = entry.indexOf("<youTubeId");
                if (ytIdx != -1) {
                    int idStart = entry.indexOf("id='", ytIdx);
                    if (idStart == -1) idStart = entry.indexOf("id=\"", ytIdx);
                    if (idStart != -1) {
                        int idEnd = entry.indexOf("'", idStart + 4);
                        if (idEnd == -1) idEnd = entry.indexOf("\"", idStart + 4);
                        if (idEnd != -1) vid = entry.substring(idStart + 4, idEnd);
                    }
                }
                if (vid == null) {
                    int vIdx = entry.indexOf("/videos/");
                    if (vIdx != -1) {
                        String tail = entry.substring(vIdx + 8);
                        if (tail.length() >= 11) vid = tail.substring(0, 11);
                    }
                }

                if (vid != null && vid.length() == 11) {
                    String selfLink = "<link rel=\"self\" type=\"application/atom+xml\" href=\"" + LOCAL_BASE + "/feeds/api/videos/" + vid + "?v=2\"/>";
                    s = s.substring(0, end) + selfLink + s.substring(end);
                    start = end + selfLink.length();
                } else {
                    start = end;
                }
            }
        } catch (Exception e) {}

        // 1. Прокси-домены yt2009
        s = s.replace("http://femboy.email:730", LOCAL_BASE);
        s = s.replace("http://68.172.220.11:730", LOCAL_BASE);
        s = s.replace("//femboy.email:730", LOCAL_BASE);
        s = s.replace("//68.172.220.11:730", LOCAL_BASE);
        // 2. CDN-превью: i.ytimg / img.youtube / yt3.ggpht / lh3.googleusercontent
        s = s.replace("http://i.ytimg.com/vi/", LOCAL_BASE + "/vi/");
        s = s.replace("https://i.ytimg.com/vi/", LOCAL_BASE + "/vi/");
        s = s.replace("//i.ytimg.com/vi/", LOCAL_BASE + "/vi/");
        s = s.replace("http://img.youtube.com/vi/", LOCAL_BASE + "/vi/");
        s = s.replace("https://img.youtube.com/vi/", LOCAL_BASE + "/vi/");
        s = s.replace("//img.youtube.com/vi/", LOCAL_BASE + "/vi/");
        s = s.replace("http://yt3.ggpht.com/", LOCAL_BASE + "/img/");
        s = s.replace("https://yt3.ggpht.com/", LOCAL_BASE + "/img/");
        s = s.replace("//yt3.ggpht.com/", LOCAL_BASE + "/img/");
        s = s.replace("http://yt3.googleusercontent.com/", LOCAL_BASE + "/img/");
        s = s.replace("https://yt3.googleusercontent.com/", LOCAL_BASE + "/img/");
        s = s.replace("//yt3.googleusercontent.com/", LOCAL_BASE + "/img/");
        s = s.replace("http://lh3.googleusercontent.com/", LOCAL_BASE + "/img/");
        s = s.replace("https://lh3.googleusercontent.com/", LOCAL_BASE + "/img/");
        s = s.replace("//lh3.googleusercontent.com/", LOCAL_BASE + "/img/");
        // ПЕРЕПИСЫВАЕМ ТОЛЬКО URL-ПУТИ (НЕ трогаем xmlns:yt!)
        s = s.replace("http://gdata.youtube.com/feeds/", LOCAL_BASE + "/feeds/");
        s = s.replace("https://gdata.youtube.com/feeds/", LOCAL_BASE + "/feeds/");
        s = s.replace("//gdata.youtube.com/feeds/", LOCAL_BASE + "/feeds/");
        s = s.replace("http://stage.gdata.youtube.com/feeds/", LOCAL_BASE + "/feeds/");
        s = s.replace("https://stage.gdata.youtube.com/feeds/", LOCAL_BASE + "/feeds/");
        s = s.replace("http://dev.gdata.youtube.com/feeds/", LOCAL_BASE + "/feeds/");
        s = s.replace("https://dev.gdata.youtube.com/feeds/", LOCAL_BASE + "/feeds/");
        return s;
    }

    /** gvi + парсинг fmt_stream_map. */
    public Info getVideoInfo(String videoId) {
        Info info = new Info();
        if (videoId == null || !videoId.matches("[A-Za-z0-9_-]{11}")) return info;

        HttpURLConnection connection = null;
        try {
            connection = openWithFailover("/get_video_info?video_id="
                + java.net.URLEncoder.encode(videoId, "UTF-8"), null);
            if (connection == null) {
                Log.w(TAG, "gvi unavailable: no healthy yt2009 instance");
                return info;
            }
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                Log.w(TAG, "gvi HTTP " + responseCode);
                return info;
            }

            String body = readBody(connection);
            if (body == null || body.length() == 0) return info;

            for (String kv : body.split("&")) {
                int eq = kv.indexOf('=');
                if (eq <= 0) continue;
                String key = kv.substring(0, eq);
                String value = kv.substring(eq + 1);
                try {
                    if ("status".equals(key)) info.status = URLDecoder.decode(value, "UTF-8");
                    else if ("title".equals(key)) info.title = URLDecoder.decode(value, "UTF-8");
                    else if ("author".equals(key)) info.author = URLDecoder.decode(value, "UTF-8");
                    else if ("length_seconds".equals(key)) {
                        info.lengthSeconds = Math.max(0, Integer.parseInt(value));
                    } else if ("fmt_stream_map".equals(key)) {
                        parseFmtMap(URLDecoder.decode(value, "UTF-8"), info);
                    }
                } catch (Exception malformedValue) {
                    Log.w(TAG, "Ignoring malformed gvi field " + key);
                }
            }
            Log.i(TAG, "gvi " + videoId + ": status=" + info.status + " title='"
                + info.title + "' itags=" + info.fmtUrls.keySet());
        } catch (Exception e) {
            Log.e(TAG, "gvi error: " + e.getMessage());
        } finally {
            if (connection != null) connection.disconnect();
        }
        return info;
    }

    private void parseFmtMap(String decoded, Info info) {
        for (String part : decoded.split(",")) {
            int pipe = part.indexOf('|');
            if (pipe <= 0) continue;
            String itagStr = part.substring(0, pipe).trim();
            String url = part.substring(pipe + 1).trim();
            url = url.replace(INTERNAL_HOST, "femboy.email:730");
            while (url.endsWith("&") || url.endsWith(",")) url = url.substring(0, url.length() - 1);
            try {
                int itag = Integer.parseInt(itagStr);
                if (!info.fmtUrls.containsKey(itag)) info.fmtUrls.put(itag, url);
            } catch (Exception e) { }
        }
    }

    /**
     * Каскад видео-эндпоинтов yt2009. Возвращает уже открытый HttpURLConnection с 200/206.
     * Порядок: exp_hd (MP4 itag 22/37) -> get_480 (FLV itag 35) -> get_video (MP4 itag 5).
     */
    public HttpURLConnection tryStream(String videoId, NanoHTTPD.IHTTPSession session) {
        if (videoId == null || !videoId.matches("[A-Za-z0-9_-]{11}")) return null;

        long now = System.currentTimeMillis();
        Long failedAt = failedVideos.get(videoId);
        if (failedAt != null) {
            if (now - failedAt.longValue() < NEG_CACHE_TTL) {
                Log.i(TAG, "tryStream " + videoId + " skipped (negative cache)");
                return null;
            }
            failedVideos.remove(videoId, failedAt);
        }

        // Prefer progressive MP4; the old client cannot reliably play FLV.
        String[] paths = {
            "/exp_hd?video_id=" + videoId,
            "/get_video?video_id=" + videoId + "/mp4",
            "/get_video?video_id=" + videoId
        };
        for (String path : paths) {
            HttpURLConnection connection = null;
            try {
                connection = openWithFailover(path, session);
                if (connection == null) {
                    Log.w(TAG, "tryStream " + path + " skipped: no healthy yt2009 instance");
                    continue;
                }

                connection.setConnectTimeout(3000);
                connection.setReadTimeout(60000);
                int code = connection.getResponseCode();
                Log.i(TAG, "tryStream " + path + " -> HTTP " + code);

                int redirects = 0;
                while ((code == 301 || code == 302 || code == 303
                        || code == 307 || code == 308) && redirects < 5) {
                    String location = connection.getHeaderField("Location");
                    if (location == null || location.trim().length() == 0) break;

                    // Resolve the redirect before disconnecting the previous response.
                    URL target = new URL(connection.getURL(), location);
                    connection.disconnect();
                    connection = (HttpURLConnection) target.openConnection();
                    connection.setConnectTimeout(10000);
                    connection.setReadTimeout(120000);
                    connection.setInstanceFollowRedirects(false);
                    connection.setRequestProperty("User-Agent", BROWSER_UA);
                    if (session != null && session.getHeaders() != null) {
                        String range = session.getHeaders().get("range");
                        if (range != null) connection.setRequestProperty("Range", range);
                    }
                    code = connection.getResponseCode();
                    redirects++;
                    Log.i(TAG, "  redirect " + redirects + " -> HTTP " + code);
                }

                String contentType = connection.getContentType();
                Log.i(TAG, "  final HTTP " + code + " ct=" + contentType);
                if ((code == HttpURLConnection.HTTP_OK || code == HttpURLConnection.HTTP_PARTIAL)
                        && contentType != null
                        && (contentType.startsWith("video/")
                            || contentType.startsWith("application/octet-stream"))) {
                    HttpURLConnection ready = connection;
                    connection = null; // Ownership transfers to ProxyServer.
                    failedVideos.remove(videoId);
                    return ready;
                }
            } catch (Exception e) {
                Log.w(TAG, "tryStream " + path + " error: " + e.getMessage());
            } finally {
                if (connection != null) connection.disconnect();
            }
        }

        // Keep the negative cache bounded on long-running devices.
        if (failedVideos.size() >= 256) failedVideos.clear();
        failedVideos.put(videoId, System.currentTimeMillis());
        Log.w(TAG, "tryStream " + videoId + " -> neg cache (all failed)");
        return null;
    }

    private int countOccurrences(String haystack, String needle) {
        if (haystack == null || needle == null) return 0;
        int count = 0, i = 0;
        while ((i = haystack.indexOf(needle, i)) != -1) { count++; i += needle.length(); }
        return count;
    }

    public void updateInstances() {
        HttpURLConnection connection = null;
        java.io.InputStream input = null;
        try {
            connection = (HttpURLConnection) new URL(INSTANCES_JSON_URL).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(false);

            int status = connection.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                Log.w(TAG, "Instance list returned HTTP " + status);
                return;
            }

            input = connection.getInputStream();
            java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int count;
            while ((count = input.read(buffer)) != -1) {
                if (output.size() + count > 256 * 1024) {
                    throw new java.io.IOException("Instance list exceeded 256 KiB");
                }
                output.write(buffer, 0, count);
            }
            String body = new String(output.toByteArray(), "UTF-8");
            List<String> parsed = new com.google.gson.Gson().fromJson(
                body, new com.google.gson.reflect.TypeToken<List<String>>() {}.getType());
            if (parsed == null) return;

            List<String> updated = new ArrayList<String>();
            for (String value : parsed) {
                if (value == null) continue;
                String instance = value.trim();
                if (instance.length() == 0) continue;
                try {
                    URL url = new URL(instance);
                    // Discovered hosts are public fallback endpoints only. Never
                    // allow a plaintext URL into a failover pool that may carry client headers.
                    if (!"https".equalsIgnoreCase(url.getProtocol())) continue;
                    if (url.getHost() == null || url.getHost().length() == 0) continue;
                    if (url.getUserInfo() != null || url.getQuery() != null || url.getRef() != null) continue;
                    while (instance.endsWith("/")) instance = instance.substring(0, instance.length() - 1);
                    if (!updated.contains(instance)) updated.add(instance);
                    if (updated.size() >= 100) break;
                } catch (Exception ignored) {}
            }

            synchronized (this) {
                availableInstances = updated;
                deadInstances.clear();
                currentInstanceIndex = 0;
            }
            Log.i(TAG, "Updated HTTPS-only proxy instances: " + updated.size());
        } catch (Exception e) {
            Log.w(TAG, "Failed to update instances: " + e.getMessage());
        } finally {
            if (input != null) {
                try { input.close(); } catch (Exception ignored) {}
            }
            if (connection != null) connection.disconnect();
        }
    }

    public synchronized int getConfiguredInstanceCount() {
        return availableInstances == null ? 0 : availableInstances.size();
    }

    public synchronized int getHealthyInstanceCount() {
        if (availableInstances == null || availableInstances.isEmpty()) return 0;
        long now = System.currentTimeMillis();
        int healthy = 0;
        for (String instance : availableInstances) {
            Long deadAt = deadInstances.get(instance);
            if (deadAt == null || now - deadAt.longValue() >= DEAD_TTL) healthy++;
        }
        return healthy;
    }

    public synchronized boolean isUsingFallback() {
        return availableInstances == null || availableInstances.isEmpty()
            || getHealthyInstanceCount() == 0;
    }

    private synchronized String getActiveInstance() {
        if (availableInstances == null || availableInstances.isEmpty()) return FALLBACK_INSTANCE;
        long now = System.currentTimeMillis();
        int size = availableInstances.size();
        for (int i = 0; i < size; i++) {
            if (currentInstanceIndex >= size) currentInstanceIndex = 0;
            String instance = availableInstances.get(currentInstanceIndex);
            Long deadAt = deadInstances.get(instance);
            if (deadAt == null) return instance;
            if (now - deadAt.longValue() >= DEAD_TTL) {
                deadInstances.remove(instance);
                return instance;
            }
            currentInstanceIndex = (currentInstanceIndex + 1) % size;
        }
        return FALLBACK_INSTANCE;
    }

    public HttpURLConnection openWithFailover(String path, NanoHTTPD.IHTTPSession session) {
        Set<String> attempted = new HashSet<String>();
        for (int attempt = 0; attempt < 3; attempt++) {
            String instance = getActiveInstance();
            synchronized (this) {
                int scanned = 0;
                while (attempted.contains(instance) && availableInstances != null && !availableInstances.isEmpty() && scanned < availableInstances.size()) {
                    currentInstanceIndex = (currentInstanceIndex + 1) % availableInstances.size();
                    instance = getActiveInstance();
                    scanned++;
                }
            }
            if (instance == null || attempted.contains(instance)) break;
            attempted.add(instance);

            HttpURLConnection connection = null;
            try {
                String suffix = path == null ? "" : path;
                if (!suffix.startsWith("/")) suffix = "/" + suffix;
                String base = instance;
                while (base.endsWith("/")) base = base.substring(0, base.length() - 1);
                
                java.net.URL target = new java.net.URL(base + suffix);
                connection = (HttpURLConnection) target.openConnection();
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(60000);
                connection.setInstanceFollowRedirects(true);

                if (session != null) {
                    connection.setRequestMethod(session.getMethod().name());
                    Map<String, String> headers = session.getHeaders();
                    if (headers != null) {
                        for (Map.Entry<String, String> entry : headers.entrySet()) {
                            String key = entry.getKey();
                            String value = entry.getValue();
                            if (key == null || value == null) continue;
                            String lower = key.toLowerCase(Locale.US);
                            // Account device identity is sent only by fetchAccountFeed()
                            // to the explicitly pinned femboy.email instance.
                            if ("x-gdata-device".equals(lower)) continue;
                            if ("host".equals(lower) || "connection".equals(lower) || "proxy-connection".equals(lower) || "keep-alive".equals(lower) || "transfer-encoding".equals(lower) || "content-length".equals(lower) || "upgrade".equals(lower)) continue;
                            if ("user-agent".equals(lower) || "accept".equals(lower) || "accept-language".equals(lower) || "range".equals(lower) || "referer".equals(lower) || "origin".equals(lower)  || "if-none-match".equals(lower) || "if-modified-since".equals(lower) || "content-type".equals(lower) || lower.startsWith("x-gdata-") || lower.startsWith("x-youtube-")) {
                                connection.setRequestProperty(key, value);
                            }
                        }
                    }
                }

                int responseCode = connection.getResponseCode();
                if (responseCode == 200 || responseCode == 206) return connection;

                if (responseCode == 429 || responseCode >= 500) {
                    synchronized (this) {
                        deadInstances.put(instance, System.currentTimeMillis());
                        if (availableInstances != null && !availableInstances.isEmpty()) {
                            currentInstanceIndex = (currentInstanceIndex + 1) % availableInstances.size();
                        }
                    }
                    Log.w(TAG, "Instance temporarily disabled: " + instance + " (HTTP " + responseCode + ")");
                } else {
                    synchronized (this) {
                        if (availableInstances != null && !availableInstances.isEmpty()) {
                            currentInstanceIndex = (currentInstanceIndex + 1) % availableInstances.size();
                        }
                    }
                }
                connection.disconnect();
            } catch (Exception e) {
                if (connection != null) connection.disconnect();
                synchronized (this) {
                    deadInstances.put(instance, System.currentTimeMillis());
                    if (availableInstances != null && !availableInstances.isEmpty()) {
                        currentInstanceIndex = (currentInstanceIndex + 1) % availableInstances.size();
                    }
                }
                Log.w(TAG, "Instance connection failed: " + instance + " -> " + e.getMessage());
            }
        }
        return null;
    }
}
