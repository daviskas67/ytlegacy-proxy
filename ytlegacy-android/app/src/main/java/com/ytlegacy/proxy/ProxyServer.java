package com.ytlegacy.proxy;

import android.util.Log;
import com.google.gson.JsonObject;
import fi.iki.elonen.NanoHTTPD;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class ProxyServer extends NanoHTTPD {

    private static final String TAG = "ProxyServer";
    private static final Pattern VIDEO_ID = Pattern.compile("^[A-Za-z0-9_-]{11}$");
    private static final String DEVICE_KEY = "XqWdDlb37csu+WU5qX2zR3y3gamS4ITVnK+r0looTM0=";
    private static final String LOCAL_BASE = "http://127.0.0.1:8080";

    private final Yt2009Client yt2009 = new Yt2009Client();
    private final java.util.Map<String, String> channelCache = new java.util.concurrent.ConcurrentHashMap<String, String>();
    private final java.util.Map<String, Long> channelCacheTime = new java.util.concurrent.ConcurrentHashMap<String, Long>();
    private static final long CHANNEL_TTL = 600000L;
    private static byte[] EMPTY_MP4 = null;
    private final java.util.Map<String, String> nameToUc = new java.util.concurrent.ConcurrentHashMap<String, String>();
    private final Map<String, Long> likeCache = new ConcurrentHashMap<String, Long>();
    private final Map<String, Long> likeCacheTime = new ConcurrentHashMap<String, Long>();
    private static final long LIKE_TTL = 600000L;

    public ProxyServer() throws IOException {
        super("127.0.0.1", 8080);
        // Загружаем список инстансов в фоне, чтобы не блокировать запуск
        new Thread(new Runnable() {
            public void run() {
                yt2009.updateInstances();
            }
        }).start();
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        Log.d(TAG, "REQ " + session.getMethod() + " " + uri);
        try {
            // === служебные ===
            if ("/health".equals(uri)) return json("{\"ok\":true}");
            if ("/youtube/accounts/registerDevice".equals(uri))
                return newFixedLengthResponse(Response.Status.OK, "text/plain",
                    "DeviceId=test_device_12345\nDeviceKey=" + DEVICE_KEY);
            if ("/deviceregistration/v1/devices".equals(uri))
                return json("{\"id\":\"test_device_12345\",\"key\":\"dGVzdA==\"}");
            if ("/leanback_ajax".equals(uri))
                return json("{\"environment\":{\"country\":\"RU\",\"start_time\":1546329600}}");
            if (uri.startsWith("/complete/search"))
                return newFixedLengthResponse(Response.Status.OK, "application/json", "[]");
            if (uri.startsWith("/youtubeMusic/v1/")) {
                Log.i(TAG, "YTMUSIC: " + uri);
                return json("{\"items\":[]}");
            }

            if (uri.startsWith("/api/stats/"))
                return newFixedLengthResponse(Response.Status.OK, "text/plain", "");
            if (uri.startsWith("/youtube/v3/")) {
                Log.i(TAG, "YOUTUBE_V3: " + uri);
                // для players — пустой protobuf (app не крашится)
                if (uri.startsWith("/youtube/v3/players"))
                    return newFixedLengthResponse(Response.Status.OK, "application/x-protobuf", "");
                // остальные v3 — пустой JSON
                return newFixedLengthResponse(Response.Status.OK, "application/json", "{\"items\":[]}");
            }
            if (uri.startsWith("/schemas/2007/categories.cat"))
                return categoriesResponse();

            // === debug ===
            if (uri.startsWith("/debug/gvi/")) {
                String vid = stripQuery(uri.substring("/debug/gvi/".length()));
                Yt2009Client.Info info = yt2009.getVideoInfo(vid);
                return text(Response.Status.OK,
                    "status=" + info.status + "\ntitle=" + info.title + "\nauthor=" + info.author +
                    "\nlength=" + info.lengthSeconds + "\nitags=" + info.fmtUrls.keySet() +
                    "\nurls=" + info.fmtUrls + "\n");
            }
            if (uri.startsWith("/debug/yt2009/")) {
                String path = uri.substring("/debug/yt2009".length());
                String body = yt2009.fetchText(path);
                return newFixedLengthResponse(Response.Status.OK, "text/plain",
                    body == null ? "NULL (fetch failed)" : body);
            }

            // === /feeds/player/videos/{id} -> yt2009 /feeds/api/videos/{id} ===
            if (uri.startsWith("/feeds/player/videos/")) {
                String vid = stripQuery(uri.substring("/feeds/player/videos/".length()));
                if (!VIDEO_ID.matcher(vid).matches()) return text(Response.Status.BAD_REQUEST, "Bad id");
                Log.i(TAG, "PLAYER: " + vid);
                VideoData v = getVideoData(vid);
                Log.i(TAG, "PLAYER built: title='" + v.title + "' ch='" + v.channelId + "'");
                return xml(GDataXmlBuilder.buildPlayerEntry(v));
            }

            // === /feeds/api/videos/{id}/related ===
            if (uri.startsWith("/feeds/api/videos/") && uri.contains("/related")) {
                String vid = stripQuery(uri.substring("/feeds/api/videos/".length(),
                    uri.indexOf("/related")));
                if (!VIDEO_ID.matcher(vid).matches()) return text(Response.Status.BAD_REQUEST, "Bad id");
                Log.i(TAG, "RELATED: " + vid);
                String body = yt2009.fetchText(appendQuery("/feeds/api/videos/" + vid + "/related", session.getParms()));
                if (body != null && body.length() >= 2000) return xml(body);
                Log.w(TAG, "related too small (" + (body != null ? body.length() : 0)
                    + "b), using most_popular as fallback");
                String pop = yt2009.fetchText("/feeds/api/standardfeeds/most_popular");
                if (pop != null) return xml(pop);
                return xml(emptyFeed("related"));
            }

            // === /feeds/api/videos/{id}/comments ===
            if (uri.startsWith("/feeds/api/videos/") && uri.contains("/comments")) {
                String vid = stripQuery(uri.substring("/feeds/api/videos/".length(),
                    uri.indexOf("/comments")));
                if (!VIDEO_ID.matcher(vid).matches()) return text(Response.Status.BAD_REQUEST, "Bad id");
                Log.i(TAG, "COMMENTS: " + vid);
                String body = yt2009.fetchText(appendQuery("/feeds/api/videos/" + vid + "/comments", session.getParms()));
                if (body != null) return xml(body);
                return xml(emptyFeed("comments"));
            }

            // === /feeds/api/videos/{id}/ratings ===
            if (uri.startsWith("/feeds/api/videos/") && uri.contains("/ratings"))
                return xml("<?xml version=\"1.0\"?><feed xmlns=\"http://www.w3.org/2005/Atom\" xmlns:gd=\"http://schemas.google.com/g/2005\"><gd:rating average=\"5.0\" max=\"5\" min=\"1\" numRaters=\"0\"/></feed>");

            // === /feeds/api/videos/{id} — метаданные ===
            if (uri.startsWith("/feeds/api/videos/")
                && !uri.contains("/related")
                && !uri.contains("/comments")
                && !uri.contains("/ratings")) {
                String vid = stripQuery(uri.substring("/feeds/api/videos/".length()));
                if (!VIDEO_ID.matcher(vid).matches()) return text(Response.Status.BAD_REQUEST, "Bad id");
                Log.i(TAG, "VIDEO METADATA: " + vid);
                return xml(GDataXmlBuilder.buildPlayerEntry(getVideoData(vid)));
            }

            // === /feeds/api/videos?q= — поиск ===
            if (uri.startsWith("/feeds/api/videos") && !uri.contains("/videos/")) {
                String query = session.getParms().get("q");
                if (query == null) query = "";
                Log.i(TAG, "SEARCH: " + query);
                String body = yt2009.fetchText(appendQuery("/feeds/api/videos", session.getParms()));
                if (body != null) return xml(body);
                return xml(emptyFeed("search"));
            }

            // === /feeds/api/standardfeeds/* ===
            if (uri.startsWith("/feeds/api/standardfeeds/")) {
                Log.i(TAG, "FEED: " + uri);
                String body = yt2009.fetchText(appendQuery(uri, session.getParms()));
                if (body != null) return xml(body);
                return xml(emptyFeed("feed"));
            }

            // === /feeds/api/users/default/* ===
            if (uri.startsWith("/feeds/api/users/default/")) {
                String kind = uri.substring("/feeds/api/users/default/".length());
                if (kind.contains("/")) kind = kind.substring(0, kind.indexOf('/'));
                return xml(emptyFeed(kind));
            }

            // === /feeds/api/users/{cid}(/uploads|/favorites|...) ===
            if (uri.startsWith("/feeds/api/users/")) {
                String rest = stripQuery(uri.substring("/feeds/api/users/".length()));
                String cid = rest;
                String sub = "";
                int slash = rest.indexOf('/');
                if (slash > 0) {
                    cid = rest.substring(0, slash);
                    sub = rest.substring(slash + 1);
                }
                if (cid.equals("default") || cid.equals("Channel") || cid.equals("channel"))
                    return xml(emptyFeed("default"));

                // если sub начинается с uploads/favorites/etc и cid = имя, резолвим в UC-id
                if (!cid.startsWith("UC") && !sub.isEmpty()) {
                    String uc = nameToUc.get(cid);
                    if (uc == null) {
                        // тянем /feeds/api/users/{name} - там должен быть UC-id
                        String prof = yt2009.fetchText("/feeds/api/users/" + cid);
                        if (prof != null) {
                            uc = extractUcIdFromProfile(prof);
                            if (uc != null) {
                                nameToUc.put(cid, uc);
                                Log.i(TAG, "Resolved '" + cid + "' -> " + uc + " (from profile)");
                            }
                        }
                    }
                    if (uc != null) cid = uc;
                }

                // сначала пробуем как есть
                String ytPath = "/feeds/api/users/" + cid + (sub.isEmpty() ? "" : "/" + sub);
                Log.i(TAG, "CHANNEL: " + ytPath);
                String body = yt2009.fetchText(appendQuery(ytPath, session.getParms()));
                if (body != null) {
                    // при запросе профиля (без sub) — запоминаем UC-id
                    if (sub.isEmpty() && !cid.startsWith("UC")) {
                        String uc = extractUcIdFromProfile(body);
                        if (uc != null) {
                            nameToUc.put(cid, uc);
                            Log.i(TAG, "Cached '" + cid + "' -> " + uc);
                        }
                    }
                    return xml(body);
                }

                // fallback 1: попробовать как search по автору (для uploads/favorites)
                if (!sub.isEmpty() && (sub.startsWith("uploads") || sub.startsWith("favorites"))) {
                    String alt = yt2009.fetchText("/feeds/api/videos?author="
                        + java.net.URLEncoder.encode(cid, "UTF-8"));
                    if (alt != null && alt.length() > 200 && alt.contains("<entry")) {
                        Log.i(TAG, "CHANNEL alt OK for " + sub + " via author=" + cid);
                        return xml(alt);
                    }
                }
                // fallback 2: пустой фид (200), чтобы app не показывал "channel not found"
                Log.w(TAG, "CHANNEL empty fallback for " + ytPath);
                return xml(emptyFeed(sub.isEmpty() ? "user" : sub));
            }

            // === /resumable/feeds/api/users/default/uploads ===
            if (uri.startsWith("/resumable/feeds/")) {
                Log.i(TAG, "RESUMABLE: " + uri);
                return xml(emptyFeed("resumable"));
            }

            // === /feeds/api/channels/{cid} (app запрашивает) ===
            if (uri.startsWith("/feeds/api/channels/")) {
                String cid = stripQuery(uri.substring("/feeds/api/channels/".length()));
                Log.i(TAG, "CHANNELS: " + cid);
                String body = fetchChannelCached(cid);
                if (body != null) {
                    String uc = extractUcIdFromProfile(body);
                    if (uc == null || !uc.startsWith("UC") || uc.length() != 24) {
                        uc = "UC" + String.format("%022d", Math.abs(cid.hashCode()));
                    }
                    Log.i(TAG, "CHANNELS: inject A3 links for " + uc);

                    StringBuilder inject = new StringBuilder();
                    // Astra A3: link rel="self"
                    if (!body.contains("<link rel=\"self\"")) {
                        inject.append("<link rel=\"self\" type=\"application/atom+xml\" href=\"http://127.0.0.1:8080/feeds/api/channels/").append(uc).append("?v=2\"/>");
                    }
                    // Astra A3: link rel="alternate"
                    if (!body.contains("<link rel=\"alternate\"")) {
                        inject.append("<link rel=\"alternate\" type=\"text/html\" href=\"http://127.0.0.1:8080/channel/").append(uc).append("\"/>");
                    }
                    // Astra A3: yt:channelId
                    if (!body.contains("<yt:channelId>")) {
                        inject.append("<yt:channelId>").append(uc).append("</yt:channelId>");
                    }
                    // Astra A3: gd:feedLink
                    if (!body.contains("user.uploads")) {
                        inject.append("<gd:feedLink rel=\"http://gdata.youtube.com/schemas/2007#user.uploads\" href=\"http://127.0.0.1:8080/feeds/api/users/").append(uc).append("/uploads\" countHint=\"10\"/>");
                        inject.append("<gd:feedLink rel=\"http://gdata.youtube.com/schemas/2007#user.favorites\" href=\"http://127.0.0.1:8080/feeds/api/users/").append(uc).append("/favorites\" countHint=\"0\"/>");
                        inject.append("<gd:feedLink rel=\"http://gdata.youtube.com/schemas/2007#user.subscriptions\" href=\"http://127.0.0.1:8080/feeds/api/users/").append(uc).append("/subscriptions\" countHint=\"0\"/>");
                    }
                    body = body.replace("</entry>", inject.toString() + "</entry>");
                    return xml(body);
                }
                return xml(emptyFeed("channel"));
            }

            // === /avatar_wait — placeholder аватара (реальный JPEG) ===
            if (uri.equals("/avatar_wait")) {
                Response r = proxyImage("http://i1.ytimg.com/vi/dQw4w9WgXcQ/default.jpg");
                if (r != null) return r;
                return text(Response.Status.NOT_FOUND, "no avatar");
            }

            // === /feeds/api/partners/{user}/branding/default ===
            if (uri.startsWith("/feeds/api/partners/") && uri.contains("/branding/")) {
                String path = uri.substring("/feeds/api/partners/".length());
                String user = path.contains("/") ? path.substring(0, path.indexOf('/')) : path;
                Log.i(TAG, "PARTNER_BRANDING: " + user);
                String x = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                    "<entry xmlns=\"http://www.w3.org/2005/Atom\" " +
                    "xmlns:yt=\"http://gdata.youtube.com/schemas/2007\">" +
                    "<id>tag:youtube.com,2008:partner:" + user + ":branding</id>" +
                    "<title>Branding for " + user + "</title>" +
                    "</entry>";
                return xml(x);
            }

            // === /watch?v= ===
            // === /img/* — аватарки/баннеры с yt3.ggpht / googleusercontent ===
            if (uri.startsWith("/img/")) {
                String path = uri.substring("/img/".length());
                Log.i(TAG, "IMG: " + path);
                String[] tries = {
                    "https://yt3.ggpht.com/" + path,
                    "https://yt3.googleusercontent.com/" + path,
                    "https://lh3.googleusercontent.com/" + path
                };
                for (String upstream : tries) {
                    Response r = proxyImage(upstream);
                    if (r != null && r.getStatus().getRequestStatus() == 200) {
                        Log.i(TAG, "IMG OK: " + upstream);
                        return r;
                    }
                }
                Log.w(TAG, "IMG all failed: " + path);
                return text(Response.Status.NOT_FOUND, "no img");
            }

            if ("/watch".equals(uri)) {
                String vid = session.getParms().get("v");
                if (vid == null || !VIDEO_ID.matcher(vid).matches())
                    return text(Response.Status.BAD_REQUEST, "Bad id");
                Log.i(TAG, "WATCH: " + vid);
                return xml(GDataXmlBuilder.buildPlayerEntry(getVideoData(vid)));
            }

            // === /video/{id}.mp4 — каскад стрима ===
            if (uri.startsWith("/video/")) {
                String fname = stripQuery(uri.substring("/video/".length()));
                int dot = fname.indexOf('.');
                String vid = dot > 0 ? fname.substring(0, dot) : fname;
                if (!VIDEO_ID.matcher(vid).matches()) return text(Response.Status.BAD_REQUEST, "Bad id");
                Log.i(TAG, "VIDEO PROXY: " + vid);

                java.net.HttpURLConnection yc = yt2009.tryStream(vid, session);
                if (yc != null) {
                    Log.i(TAG, "STREAM via yt2009 OK");
                    return buildStreamResponse(yc);
                }

                Log.w(TAG, "yt2009 stream failed for " + vid + ", returning empty MP4 (prevent crash)");
                byte[] emptyMp4 = (EMPTY_MP4 != null) ? EMPTY_MP4 : new byte[]{
                    0,0,0,0x18,'f','t','y','p','i','s','o','m',
                    0,0,0x02,0x00,'i','s','o','m','i','s','o','2',
                    'a','v','c','1','m','p','4','1',
                    0,0,0,0x08,'f','r','e','e',0,0,0,0x00
                };
                return emptyMp4Response(emptyMp4, session.getHeaders().get("range"));
            }

            // === /vi/{id}/{n}.jpg — превью ===
            if (uri.startsWith("/vi/")) {
                String[] parts = uri.split("/");
                if (parts.length >= 3) {
                    String vid = parts[2];
                    String cdnName = "hqdefault";
                    if (parts.length >= 4) {
                        String fname = parts[3];
                        int dot = fname.indexOf('.');
                        String numStr = dot > 0 ? fname.substring(0, dot) : fname;
                        try {
                            int idx = Integer.parseInt(numStr);
                            cdnName = ThumbnailHelper.getCdnName(idx);
                        } catch (Exception e) {}
                    }
                    Log.i(TAG, "THUMB: " + vid + "/" + cdnName);
                    String[] tries = {
                        "http://img.youtube.com/vi/" + vid + "/" + cdnName + ".jpg",
                        "http://img.youtube.com/vi/" + vid + "/hqdefault.jpg"
                    };
                    for (String upstream : tries) {
                        Response r = proxyImage(upstream);
                        if (r != null && r.getStatus().getRequestStatus() == 200) {
                            Log.i(TAG, "THUMB OK: " + upstream);
                            return r;
                        }
                    }
                    Log.w(TAG, "THUMB all failed: " + vid);
                    return text(Response.Status.NOT_FOUND, "no thumb");
                }
            }

            // === /channel_avatar/{cid} ===
            if (uri.startsWith("/channel_avatar/")) {
                String cid = stripQuery(uri.substring("/channel_avatar/".length()));
                Log.i(TAG, "AVATAR: " + cid);
                return handleChannelImage(cid, "media:thumbnail", "http://img.youtube.com/vi/dQw4w9WgXcQ/default.jpg");
            }

            // === /channel_banner/{cid} ===
            if (uri.startsWith("/channel_banner/")) {
                String cid = stripQuery(uri.substring("/channel_banner/".length()));
                Log.i(TAG, "BANNER: " + cid);
                return handleChannelImage(cid, "media:banner", null);
            }

            // === legacy stream endpoints (YouTube 5.0.21 Honeycomb) ===
            if (uri.startsWith("/channel_fh264_getvideo")
                || uri.startsWith("/get_video")
                || uri.startsWith("/videoplayback")) {
                String vid = session.getParms().get("video_id");
                if (vid == null) vid = session.getParms().get("id");
                if (vid == null) vid = session.getParms().get("v");
                Log.i(TAG, "LEGACY_STREAM: " + uri + " params=" + session.getParms());
                if (vid != null && VIDEO_ID.matcher(vid).matches()) {
                    java.net.HttpURLConnection yc = yt2009.tryStream(vid, session);
                    if (yc != null) {
                        Log.i(TAG, "STREAM legacy via yt2009 OK for " + vid);
                        return buildStreamResponse(yc);
                    }
                    Log.w(TAG, "tryStream failed for " + vid + ", returning empty MP4");
                } else {
                    Log.w(TAG, "LEGACY_STREAM: no video_id in params");
                }
                byte[] emptyMp4 = (EMPTY_MP4 != null) ? EMPTY_MP4 : new byte[]{
                    0,0,0,0x18,'f','t','y','p','i','s','o','m',
                    0,0,0x02,0x00,'i','s','o','m','i','s','o','2',
                    'a','v','c','1','m','p','4','1',
                    0,0,0,0x08,'f','r','e','e',0,0,0,0x00
                };
                return emptyMp4Response(emptyMp4, session.getHeaders().get("range"));
            }

            Log.w(TAG, "UNHANDLED: " + uri);
            return text(Response.Status.NOT_FOUND, "Not implemented");
        } catch (Exception e) {
            Log.e(TAG, "Handler error: " + e, e);
            return text(Response.Status.INTERNAL_ERROR, "Error: " + e.getMessage());
        }
    }

    // ============== helpers ==============







    /** Метаданные видео: gvi (title/author/length) + InnerTube (лайки). */
    private VideoData getVideoData(String vid) {
        VideoData v = new VideoData();
        v.id = vid;
        try {
            Yt2009Client.Info info = yt2009.getVideoInfo(vid);
            if ("ok".equals(info.status)) {
                v.title = info.title;
                v.author = info.author;
                v.lengthSeconds = info.lengthSeconds;
                Log.i(TAG, "getVideoData: yt2009 OK title='" + v.title + "' author='" + v.author + "'");
            } else {
                Log.w(TAG, "getVideoData: yt2009 status=" + info.status);
            }
        } catch (Exception e) {
            Log.e(TAG, "getVideoData: " + e);
        }
        // channelId = author (имя). App пойдёт на /feeds/api/users/{author} -> yt2009
        v.channelId = (v.author != null && !v.author.isEmpty()) ? v.author : "";
        // yt2009 не даёт UC-id -> генерим fake UC-id из хеша имени (валидный формат)
        if (!v.channelId.startsWith("UC") || v.channelId.length() != 24) {
            try {
                String prof = fetchChannelCached(v.channelId);
                String uc = (prof != null) ? extractUcIdFromProfile(prof) : null;
                if (uc != null && uc.startsWith("UC") && uc.length() == 24) {
                    v.channelId = uc;
                    Log.i(TAG, "getVideoData: real UC-id " + uc);
                } else {
                    Log.w(TAG, "getVideoData: real channel ID unavailable for " + v.author);
                    Log.i(TAG, "getVideoData: fake UC-id " + v.channelId + " for " + v.author);
                }
            } catch (Exception e) {
                Log.w(TAG, "getVideoData: channel lookup failed for " + v.author, e);
            }
        }
        if (v.title == null || v.title.isEmpty()) v.title = vid;
        if (v.author == null || v.author.isEmpty()) v.author = "YouTube";
        if (v.channelId == null || v.channelId.isEmpty()) v.channelId = v.author;
        v.likeCount = 0;
        Log.i(TAG, "getVideoData: id=" + v.id + " title='" + v.title + "' ch='" + v.channelId + "'");
        return v;
    }



    private Response buildStreamResponse(java.net.HttpURLConnection yc) {
        try {
            int code = yc.getResponseCode();
            String ct = yc.getContentType();
            if (ct == null) ct = "video/mp4";
            long len = yc.getContentLength();
            java.io.InputStream is = yc.getInputStream();
            Response.Status st = (code == 206) ? Response.Status.PARTIAL_CONTENT : Response.Status.OK;
            Response r = (len > 0)
                ? newFixedLengthResponse(st, ct, is, len)
                : newChunkedResponse(st, ct, is);
            String cr = yc.getHeaderField("Content-Range");
            if (cr != null) r.addHeader("Content-Range", cr);
            String ar = yc.getHeaderField("Accept-Ranges");
            r.addHeader("Accept-Ranges", ar != null ? ar : "bytes");
            return r;
        } catch (Exception e) {
            Log.e(TAG, "buildStreamResponse error: " + e);
            return text(Response.Status.INTERNAL_ERROR, "stream err");
        }
    }

    private Response handleChannelImage(String cid, String tagName, String redirectFallback) {
        try {
            String feed = yt2009.fetchText("/feeds/api/users/" + cid);
            if (feed != null) {
                java.util.regex.Matcher m = java.util.regex.Pattern
                    .compile("<" + tagName.replace(":", "\\:") + "[^>]*url=['\"]([^'\"]+)['\"]")
                    .matcher(feed);
                if (m.find()) {
                    String url = m.group(1);
                    if (url.startsWith("//")) url = "http:" + url;
                    Log.i(TAG, "img from yt2009: " + url);
                    return proxyImage(url);
                }
            }
        } catch (Exception e) {}
        if (redirectFallback != null) {
            Response r = newFixedLengthResponse(Response.Status.REDIRECT, "text/plain", "");
            r.addHeader("Location", redirectFallback);
            return r;
        }
        return text(Response.Status.NOT_FOUND, "no image");
    }

    private Response proxyImage(String url) {
        Log.i(TAG, "IMG: " + url);
        // 1) оригинал
        Response r = proxyImageTry(url);
        if (r != null) return r;
        // 2) приоритет РАБОЧИХ хостов (yt4/lh4 googleusercontent — единственные пробиваются через VLESS)
        String[] workingHosts = {
            "yt4.googleusercontent.com",
            "lh4.googleusercontent.com"
        };
        // пытаемся переписать на каждый рабочий хост
        for (String wh : workingHosts) {
            String alt = url;
            // заменить любой из GGpht/GoogleUserContent на рабочий
            alt = alt.replace("yt3.ggpht.com", wh);
            alt = alt.replace("yt4.ggpht.com", wh);
            alt = alt.replace("yt3.googleusercontent.com", wh);
            alt = alt.replace("lh3.googleusercontent.com", wh);
            if (!alt.equals(url)) {
                Log.d(TAG, "IMG try: " + wh);
                r = proxyImageTry(alt);
                if (r != null) return r;
            }
        }
        // 3) i1.ytimg.com для видео-превью (иногда работает с телефона)
        if (url.contains("img.youtube.com") || url.contains("i.ytimg.com")) {
            String alt = url
                .replace("img.youtube.com", "i1.ytimg.com")
                .replace("i.ytimg.com", "i1.ytimg.com");
            if (!alt.equals(url)) {
                r = proxyImageTry(alt);
                if (r != null) return r;
            }
        }
        // 4) финальный fallback — дефолтная аватарка (i1.ytimg.com c телефона работал)
        Log.w(TAG, "IMG all failed, default avatar");
        Response def = proxyImageTry("http://i1.ytimg.com/vi/dQw4w9WgXcQ/default.jpg");
        if (def != null) return def;
        return text(Response.Status.NOT_FOUND, "img all failed");
    }

    private Response proxyImageTry(String url) {
        try {
            java.net.URL u = new java.net.URL(url);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) u.openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(15000);
            conn.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                "(KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36");
            conn.setRequestProperty("Accept", "image/*,*/*;q=0.8");
            conn.setInstanceFollowRedirects(true);
            int code = conn.getResponseCode();
            if (code != 200) {
                Log.d(TAG, "proxyImageTry " + code + " for " + url);
                conn.disconnect();
                return null;
            }
            String ct = conn.getContentType();
            if (ct == null) ct = "image/jpeg";
            long len = conn.getContentLength();
            java.io.InputStream is = conn.getInputStream();
            Response resp = (len > 0)
                ? newFixedLengthResponse(Response.Status.OK, ct, is, len)
                : newChunkedResponse(Response.Status.OK, ct, is);
            resp.addHeader("Cache-Control", "max-age=86400");
            Log.i(TAG, "IMG OK: " + url + " (" + len + "b)");
            return resp;
        } catch (java.net.UnknownHostException dns) {
            Log.w(TAG, "IMG DNS fail: " + url);
            return null;
        } catch (Exception e) {
            Log.w(TAG, "IMG err: " + url + " -> " + e.getClass().getSimpleName());
            return null;
        }
    }

    private Response proxyVideo(String url, IHTTPSession session) {
        try {
            java.net.URL u = new java.net.URL(url);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) u.openConnection();
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(60000);
            conn.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                "(KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36");
            conn.setInstanceFollowRedirects(true);
            String range = session.getHeaders().get("range");
            if (range != null) conn.setRequestProperty("Range", range);
            int code = conn.getResponseCode();
            Response.Status st;
            if (code == 206) st = Response.Status.PARTIAL_CONTENT;
            else if (code == 200) st = Response.Status.OK;
            else if (code == 416) return text(Response.Status.RANGE_NOT_SATISFIABLE, "Range");
            else return text(Response.Status.INTERNAL_ERROR, "upstream " + code);
            String ct = conn.getContentType();
            if (ct == null) ct = "video/mp4";
            long len = conn.getContentLength();
            java.io.InputStream is = conn.getInputStream();
            Response r = (len >= 0)
                ? newFixedLengthResponse(st, ct, is, len)
                : newChunkedResponse(st, ct, is);
            String cr = conn.getHeaderField("Content-Range");
            if (cr != null) r.addHeader("Content-Range", cr);
            String ar = conn.getHeaderField("Accept-Ranges");
            r.addHeader("Accept-Ranges", ar != null ? ar : "bytes");
            return r;
        } catch (Exception e) {
            Log.e(TAG, "proxyVideo error: " + e);
            return text(Response.Status.INTERNAL_ERROR, "proxy: " + e.getMessage());
        }
    }


    private Response emptyMp4Response(byte[] data, String rangeHeader) {
        int total = data != null ? data.length : 0;
        if (rangeHeader == null || !rangeHeader.startsWith("bytes=")) {
            byte[] body = data != null ? data : new byte[0];
            Response response = newFixedLengthResponse(Response.Status.OK,
                "video/mp4", new java.io.ByteArrayInputStream(body), body.length);
            response.addHeader("Accept-Ranges", "bytes");
            return response;
        }

        try {
            String spec = rangeHeader.substring(6).trim();
            if (spec.length() == 0 || spec.indexOf(',') >= 0) throw new IllegalArgumentException("Unsupported range");
            int dash = spec.indexOf('-');
            if (dash < 0) throw new IllegalArgumentException("Invalid range");

            String first = spec.substring(0, dash).trim();
            String last = spec.substring(dash + 1).trim();
            long start;
            long end;

            if (first.length() == 0) {
                long suffixLength = Long.parseLong(last);
                if (suffixLength <= 0 || total == 0) throw new IllegalArgumentException("Invalid suffix range");
                start = Math.max(0L, (long) total - suffixLength);
                end = total - 1L;
            } else {
                start = Long.parseLong(first);
                end = last.length() == 0 ? total - 1L : Long.parseLong(last);
                if (start < 0 || end < start || start >= total) {
                    throw new IllegalArgumentException("Unsatisfiable range");
                }
                if (end >= total) end = total - 1L;
            }

            if (total == 0 || start < 0 || end < start || start >= total) {
                throw new IllegalArgumentException("Unsatisfiable range");
            }

            int length = (int) (end - start + 1L);
            byte[] slice = new byte[length];
            System.arraycopy(data, (int) start, slice, 0, length);
            Response response = newFixedLengthResponse(Response.Status.PARTIAL_CONTENT,
                "video/mp4", new java.io.ByteArrayInputStream(slice), length);
            response.addHeader("Content-Range", "bytes " + start + "-" + end + "/" + total);
            response.addHeader("Accept-Ranges", "bytes");
            return response;
        } catch (Exception invalidRange) {
            Response response = newFixedLengthResponse(Response.Status.RANGE_NOT_SATISFIABLE,
                "text/plain", "Range Not Satisfiable");
            response.addHeader("Content-Range", "bytes */" + total);
            response.addHeader("Accept-Ranges", "bytes");
            return response;
        }
    }

    private String appendQuery(String path, Map<String, String> params) throws java.io.UnsupportedEncodingException {
        if (params == null || params.isEmpty()) return path;
        StringBuilder result = new StringBuilder(path);
        boolean first = path.indexOf('?') < 0;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key == null || value == null) continue;
            result.append(first ? '?' : '&');
            first = false;
            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append('=');
            result.append(URLEncoder.encode(value, "UTF-8"));
        }
        return result.toString();
    }

    private static String stripQuery(String s) {
        int q = s.indexOf('?');
        return q >= 0 ? s.substring(0, q) : s;
    }

    private Response json(String b) {
        return newFixedLengthResponse(Response.Status.OK, "application/json", b);
    }
    private Response xml(String b) {
        return newFixedLengthResponse(Response.Status.OK, "application/atom+xml", b);
    }
    private Response text(Response.Status s, String b) {
        return newFixedLengthResponse(s, "text/plain", b);
    }

    public void initEmptyMp4(android.content.Context ctx) {
        try {
            java.io.InputStream is = ctx.getAssets().open("empty.mp4");
            java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
            byte[] buf = new byte[4096];
            int n;
            while ((n = is.read(buf)) != -1) bos.write(buf, 0, n);
            is.close();
            EMPTY_MP4 = bos.toByteArray();
            Log.i(TAG, "empty.mp4 loaded: " + EMPTY_MP4.length + " bytes");
        } catch (Exception e) {
            Log.e(TAG, "empty.mp4 load failed: " + e);
        }
    }

    private static String emptyFeed(String t) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<feed xmlns=\"http://www.w3.org/2005/Atom\" " +
            "xmlns:gd=\"http://schemas.google.com/g/2005\" " +
            "xmlns:yt=\"http://gdata.youtube.com/schemas/2007\" " +
            "xmlns:media=\"http://search.yahoo.com/mrss/\" " +
            "xmlns:openSearch=\"http://a9.com/-/spec/opensearch/1.1/\">\n" +
            "  <id>tag:youtube.com,2008:feed:" + t + "</id>\n" +
            "  <title type=\"text\">" + t + "</title>\n" +
            "  <updated>2013-08-07T00:00:00.000Z</updated>\n" +
            "  <openSearch:totalResults>0</openSearch:totalResults>\n" +
            "  <openSearch:startIndex>1</openSearch:startIndex>\n" +
            "  <openSearch:itemsPerPage>25</openSearch:itemsPerPage>\n" +
            "  <link rel=\"self\" type=\"application/atom+xml\" href=\"http://127.0.0.1:8080/feeds/api/" + t + "\" />\n" +
            "</feed>";
    }

    private Response categoriesResponse() {
        String cats = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<app:categories xmlns:app=\"http://www.w3.org/2007/app\" " +
            "xmlns:atom=\"http://www.w3.org/2005/Atom\" " +
            "scheme=\"http://gdata.youtube.com/schemas/2007/categories.cat\" fixed=\"yes\">" +
            "<atom:category term=\"Music\" label=\"Music\"/>" +
            "<atom:category term=\"Entertainment\" label=\"Entertainment\"/>" +
            "<atom:category term=\"Comedy\" label=\"Comedy\"/>" +
            "<atom:category term=\"Gaming\" label=\"Gaming\"/>" +
            "<atom:category term=\"News\" label=\"News\"/>" +
            "<atom:category term=\"Sports\" label=\"Sports\"/>" +
            "<atom:category term=\"Tech\" label=\"Tech\"/>" +
            "<atom:category term=\"Education\" label=\"Education\"/>" +
            "<atom:category term=\"Film\" label=\"Film\"/>" +
            "<atom:category term=\"Howto\" label=\"Howto\"/>" +
            "<atom:category term=\"People\" label=\"People\"/>" +
            "<atom:category term=\"Science\" label=\"Science\"/>" +
            "<atom:category term=\"Travel\" label=\"Travel\"/>" +
            "</app:categories>";
        return newFixedLengthResponse(Response.Status.OK, "application/atom+xml;type=feed", cats);
    }

    /** Парсит UC-id из XML профиля канала yt2009: <yt:channelId>UC...</yt:channelId> или <id>...</id> */
    private String fetchChannelCached(String cid) {
        long now = System.currentTimeMillis();
        Long ts = channelCacheTime.get(cid);
        if (ts != null && now - ts < CHANNEL_TTL) {
            String cached = channelCache.get(cid);
            if (cached != null) {
                Log.d(TAG, "CHANNEL CACHE HIT: " + cid);
                return cached;
            }
        }
        String body = yt2009.fetchText("/feeds/api/users/" + cid);
        if (body != null && body.length() > 100) {
            channelCache.put(cid, body);
            channelCacheTime.put(cid, now);
        }
        return body;
    }

    private String extractUcIdFromProfile(String xmlBody) {
        if (xmlBody == null) return null;
        java.util.regex.Matcher m = java.util.regex.Pattern
            .compile("UC[A-Za-z0-9_-]{22}").matcher(xmlBody);
        if (m.find()) return m.group(0);
        return null;
    }
}
