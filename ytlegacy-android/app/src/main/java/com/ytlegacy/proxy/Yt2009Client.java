package com.ytlegacy.proxy;

import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import fi.iki.elonen.NanoHTTPD;

public class Yt2009Client {
    private static final String TAG = "Yt2009";
    private static final String BASE = "http://femboy.email:730";
    private static final String INTERNAL_HOST = "68.172.220.11:730";
    private static final String LOCAL_BASE = "http://127.0.0.1:8080";
    private final java.util.Map<String, Integer> hitCount = new java.util.concurrent.ConcurrentHashMap<String, Integer>();
    private static final int HIT_LIMIT = 5;  // после 5 запросов — throttle
    private final java.util.Map<String, Long> failedVideos = new java.util.concurrent.ConcurrentHashMap<String, Long>();
    private static final long NEG_CACHE_TTL = 600000L;  // 60 сек
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

    /** Passthrough: тянет текст с yt2009 и переписывает URL на локальные. Возвращает null при ошибке. */
    public String fetchText(String path) {
        // retry 2 раза — yt2009 иногда рвёт соединение под нагрузкой
        for (int attempt = 1; attempt <= 2; attempt++) {
            try {
                HttpURLConnection c = open(path, null);
                int code = c.getResponseCode();
                if (code != 200) {
                    Log.w(TAG, "fetch " + path + " -> HTTP " + code + " (attempt " + attempt + ")");
                    if (attempt == 2) return null;
                    continue;
                }
                String body = readBody(c);
                if (body == null || body.isEmpty()) {
                    Log.w(TAG, "fetch " + path + " empty body (attempt " + attempt + ")");
                    if (attempt == 2) return null;
                    continue;
                }
                String original = body;
                String rewritten = rewriteUrls(original);
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
                if (attempt == 2) return null;
            }
        }
        Log.e(TAG, "fetch " + path + " FAILED after 2 attempts");
        return null;
    }

    /** Читает тело ответа блоками через Content-Length (или до EOF), обходя проблемы chunked. */
    private String readBody(HttpURLConnection c) throws Exception {
        java.io.InputStream is = new java.io.BufferedInputStream(c.getInputStream(), 8192);
        long contentLength = c.getContentLength();
        java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        int n;
        if (contentLength > 0) {
            long total = 0;
            while (total < contentLength && (n = is.read(buf)) != -1) {
                bos.write(buf, 0, n);
                total += n;
            }
        } else {
            while ((n = is.read(buf)) != -1) {
                bos.write(buf, 0, n);
            }
        }
        is.close();
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
        try {
            HttpURLConnection c = open("/get_video_info?video_id=" + videoId, null);
            if (c.getResponseCode() != 200) {
                Log.w(TAG, "gvi HTTP " + c.getResponseCode());
                return info;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            br.close();

            for (String kv : sb.toString().split("&")) {
                int eq = kv.indexOf('=');
                if (eq <= 0) continue;
                String k = kv.substring(0, eq);
                String v = kv.substring(eq + 1);
                try {
                    if ("status".equals(k)) info.status = URLDecoder.decode(v, "UTF-8");
                    else if ("title".equals(k)) info.title = URLDecoder.decode(v, "UTF-8");
                    else if ("author".equals(k)) info.author = URLDecoder.decode(v, "UTF-8");
                    else if ("length_seconds".equals(k)) info.lengthSeconds = Integer.parseInt(v);
                    else if ("fmt_stream_map".equals(k)) parseFmtMap(URLDecoder.decode(v, "UTF-8"), info);
                } catch (Exception e) { }
            }
            Log.i(TAG, "gvi " + videoId + ": status=" + info.status + " title='" + info.title
                + "' itags=" + info.fmtUrls.keySet());
        } catch (Exception e) {
            Log.e(TAG, "gvi error: " + e);
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
        // негативный кэш: если видео недавно 404/429 — не долбить yt2009
        Long failedAt = failedVideos.get(videoId);
        if (failedAt != null && System.currentTimeMillis() - failedAt < NEG_CACHE_TTL) {
            Integer cnt = hitCount.get(videoId);
            int c = (cnt == null) ? 0 : cnt;
            hitCount.put(videoId, c + 1);
            if (c < HIT_LIMIT) {
                Log.i(TAG, "tryStream " + videoId + " skipped (negative cache, hit=" + c + ")");
            } else if (c == HIT_LIMIT) {
                Log.w(TAG, "tryStream " + videoId + " throttling (hit=" + c + ")");
            }
            if (c >= HIT_LIMIT) {
                try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
            }
            return null;
        }
        // 720p MP4 первым (кэшируется быстро), 240p MP4 как last resort.
        // /get_480 (FLV, 14с) убран — старый Android не проигрывает FLV.
                String[] paths = {
            "/exp_hd?video_id=" + videoId,
            "/get_video?video_id=" + videoId + "/mp4",  // Прямая ссылка на MP4 (как в браузере)
            "/get_video?video_id=" + videoId
        };
        for (String p : paths) {
            try {
                HttpURLConnection c = open(p, session);
                c.setConnectTimeout(3000);
                c.setReadTimeout(60000);
                int code = c.getResponseCode();
                Log.i(TAG, "tryStream " + p + " -> HTTP " + code);
                // manual redirect follow (Location может вести на /assets/...mp4?ac=...)
                int hops = 0;
                while ((code == 301 || code == 302 || code == 303 || code == 307 || code == 308)
                        && hops < 5) {
                    String loc = c.getHeaderField("Location");
                    Log.i(TAG, "  redirect -> " + loc);
                    if (loc == null) break;
                    // strip cache-buster ?ac=... — иначе yt2009 перегенерирует файл каждый раз
                    loc = loc.replaceAll("[?&]ac=[^&]*", "");
                    c.disconnect();
                    if (loc.startsWith("/")) {
                        c = open(loc, session);
                    } else {
                        java.net.URL abs = new java.net.URL(loc);
                        c = (HttpURLConnection) abs.openConnection();
                        c.setConnectTimeout(10000);
                        c.setReadTimeout(120000);
                        c.setRequestProperty("User-Agent", BROWSER_UA);
                        String range = session.getHeaders().get("range");
                        if (range != null) c.setRequestProperty("Range", range);
                    }
                    code = c.getResponseCode();
                    hops++;
                    Log.i(TAG, "  hop " + hops + " -> HTTP " + code);
                }
                String ct = c.getContentType();
                Log.i(TAG, "  final HTTP " + code + " ct=" + ct);
                if ((code == 200 || code == 206) && ct != null
                        && (ct.startsWith("video/") || ct.startsWith("application/octet-stream"))) {
                    return c;
                }
                c.disconnect();
            } catch (Exception e) {
                Log.w(TAG, "tryStream " + p + " error: " + e);
            }
        }
        // все paths провалились — в негативный кэш
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
}
