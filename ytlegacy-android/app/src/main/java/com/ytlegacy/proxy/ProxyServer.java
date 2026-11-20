package com.ytlegacy.proxy;

import android.util.Log;
import android.content.Context;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fi.iki.elonen.NanoHTTPD;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

public class ProxyServer extends NanoHTTPD {

    private static final String TAG = "ProxyServer";
    private static final Pattern VIDEO_ID = Pattern.compile("^[A-Za-z0-9_-]{11}$");
    private static final Pattern PLAYLIST_ID = Pattern.compile("^[A-Za-z0-9_-]{10,80}$");
    private static final String DEVICE_KEY = "XqWdDlb37csu+WU5qX2zR3y3gamS4ITVnK+r0looTM0=";
    private static final String LOCAL_BASE = "http://127.0.0.1:8080";

    private static final String VERSION = "1.15";
    private final InvidiousClient invidious = new InvidiousClient();
    private final PipedClient piped = new PipedClient();
    private final Yt2009Client yt2009 = new Yt2009Client();
    private final Yt2009AccountManager yt2009Account;
    private final Context applicationContext;
    private final InnerTubeClient innerTube = new InnerTubeClient();
    private final SourceManager sourceManager = new SourceManager();
    private final AtomicLong requestCount = new AtomicLong();
    private final AtomicLong channelCacheHits = new AtomicLong();
    private final AtomicLong channelCacheMisses = new AtomicLong();
    private final long startedAt = System.currentTimeMillis();
    private final java.util.Map<String, String> channelCache = new java.util.concurrent.ConcurrentHashMap<String, String>();
    private final java.util.Map<String, Long> channelCacheTime = new java.util.concurrent.ConcurrentHashMap<String, Long>();
    private static final long CHANNEL_TTL = 600000L;
    private static byte[] EMPTY_MP4 = null;
    private final java.util.Map<String, String> nameToUc = new java.util.concurrent.ConcurrentHashMap<String, String>();
    private final Map<String, Long> likeCache = new ConcurrentHashMap<String, Long>();
    private final Map<String, Long> likeCacheTime = new ConcurrentHashMap<String, Long>();
    private static final long LIKE_TTL = 600000L;

    public ProxyServer() throws IOException {
        this(null);
    }

    public ProxyServer(Context context) throws IOException {
        super("127.0.0.1", 8080);
        applicationContext = context == null ? null : context.getApplicationContext();
        yt2009Account = new Yt2009AccountManager(context);
        // Загружаем список инстансов в фоне, чтобы не блокировать запуск
        new Thread(new Runnable() {
            public void run() {
                yt2009.updateInstances();
            }
        }, "yt2009-instance-loader").start();
        invidious.refreshInBackground();
        piped.refreshInBackground();
    }

    private Response protobuf(byte[] data) {
        byte[] body = data == null ? new byte[0] : data;
        return newFixedLengthResponse(Response.Status.OK, "application/x-protobuf",
            new java.io.ByteArrayInputStream(body), body.length);
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        requestCount.incrementAndGet();
        Log.d(TAG, "REQ " + session.getMethod() + " " + uri);
        try {
            // === служебные ===
            if ("/health".equals(uri) || "/api/status".equals(uri)) return healthResponse();
            if ("/api/invidious".equals(uri)) return json(invidious.getStatus().toString());
            if ("/api/piped".equals(uri)) return json(piped.getStatus().toString());
            if ("/api/sources".equals(uri)) return json(sourceManager.getStatus().toString());
            if ("/api/yt2009/login/start".equals(uri)) {
                if (!"POST".equalsIgnoreCase(session.getMethod().name()))
                    return text(Response.Status.METHOD_NOT_ALLOWED, "Use POST");
                try {
                    return json(yt2009Account.startLogin().toString());
                } catch (Exception e) {
                    Log.w(TAG, "yt2009 login initialization failed: " + e.getMessage());
                    JsonObject error = new JsonObject();
                    error.addProperty("ok", false);
                    error.addProperty("error", e.getMessage() == null ? "yt2009 connection failed" : e.getMessage());
                    return json(error.toString());
                }
            }
            if ("/api/yt2009/login/status".equals(uri))
                return json(yt2009Account.checkStatus().toString());
            if ("/api/yt2009/login/picker".equals(uri)) {
                if (!yt2009Account.isAuthorized())
                    return text(Response.Status.UNAUTHORIZED, "Complete yt2009 sign-in first");
                Response picker = newFixedLengthResponse(Response.Status.REDIRECT, "text/plain", "");
                picker.addHeader("Location", yt2009Account.getAccountPickerUrl());
                return picker;
            }
            if ("/api/cache".equals(uri)) return cacheResponse();
            if ("/api/cache/clear".equals(uri)) {
                if (!"POST".equalsIgnoreCase(session.getMethod().name()))
                    return text(Response.Status.METHOD_NOT_ALLOWED, "Use POST");
                int cleared = clearLocalCaches();
                JsonObject result = new JsonObject();
                result.addProperty("ok", true);
                result.addProperty("clearedEntries", cleared);
                return json(result.toString());
            }
            if (uri.startsWith("/api/video/")) {
                String vid = stripQuery(uri.substring("/api/video/".length()));
                if (!VIDEO_ID.matcher(vid).matches()) return text(Response.Status.BAD_REQUEST, "Bad id");
                VideoData v = getVideoData(vid, getRequestedQuality(session));
                JsonObject result = new JsonObject();
                result.addProperty("id", v.id);
                result.addProperty("title", v.title);
                result.addProperty("author", v.author);
                result.addProperty("channelId", v.channelId);
                result.addProperty("durationSeconds", v.lengthSeconds);
                result.addProperty("itag", v.itag);
                result.addProperty("width", v.width);
                result.addProperty("height", v.height);
                result.addProperty("hasPlayableStream", v.streamUrl != null && !v.streamUrl.isEmpty());
                result.addProperty("viewCount", v.viewCount);
                result.addProperty("likeCount", v.likeCount);
                result.addProperty("isLive", v.isLive);
                result.addProperty("isHd", v.isHd);
                return json(result.toString());
            }
            if ("/youtube/accounts/registerDevice".equals(uri))
                return newFixedLengthResponse(Response.Status.OK, "text/plain",
                    "DeviceId=test_device_12345\nDeviceKey=" + DEVICE_KEY);
            if ("/deviceregistration/v1/devices".equals(uri))
                return json("{\"id\":\"test_device_12345\",\"key\":\"dGVzdA==\"}");
            if ("/leanback_ajax".equals(uri))
                return json("{\"environment\":{\"country\":\"RU\",\"start_time\":1546329600}}");
            if (uri.startsWith("/complete/search")) {
                String query = session.getParms().get("q");
                String suggestions = SuggestionClient.fetchJson(query);
                Response response = newFixedLengthResponse(Response.Status.OK,
                    "application/json; charset=utf-8", suggestions);
                response.addHeader("Cache-Control", "public, max-age=45");
                return response;
            }
            if (uri.startsWith("/youtubeMusic/v1/")) {
                Log.i(TAG, "YTMUSIC: " + uri);
                return json("{\"items\":[]}");
            }

            if (uri.startsWith("/api/stats/"))
                return newFixedLengthResponse(Response.Status.OK, "text/plain", "");
            if (uri.startsWith("/youtube/v3/")) {
                Log.i(TAG, "YOUTUBE_V3: " + uri);
                if (uri.startsWith("/youtube/v3/activities")) {
                    int max = 20;
                    try { max = Integer.parseInt(session.getParms().get("maxResults")); }
                    catch (Exception ignored) {}
                    max = Math.max(1, Math.min(max, 50));
                    JsonArray trending = invidious.trending();
                    if (trending == null || trending.size() == 0) trending = piped.trending();
                    return protobuf(YouTubeV3Proto.activities(trending, max));
                }
                if (uri.startsWith("/youtube/v3/videos")) {
                    java.util.List<JsonObject> videos = new java.util.ArrayList<JsonObject>();
                    java.util.List<String> ids = YouTubeV3Proto.parseIds(session.getParms().get("id"), 50);
                    for (String id : ids) {
                        if (!VIDEO_ID.matcher(id).matches()) continue;
                        JsonObject video = invidious.video(id);
                        if (video != null) videos.add(video);
                    }
                    return protobuf(YouTubeV3Proto.videos(videos));
                }
                if (uri.startsWith("/youtube/v3/channels")) {
                    java.util.List<JsonObject> channels = new java.util.ArrayList<JsonObject>();
                    java.util.List<String> ids = YouTubeV3Proto.parseIds(session.getParms().get("id"), 50);
                    for (String id : ids) {
                        if (!id.startsWith("UC") || id.length() != 24) continue;
                        JsonObject channel = invidious.channel(id);
                        if (channel != null) channels.add(channel);
                    }
                    return protobuf(YouTubeV3Proto.channels(channels));
                }
                // The legacy player endpoint is not needed for feed cards yet,
                // but it must still receive a valid protobuf body.
                return protobuf(new byte[0]);
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
                VideoData v = getVideoData(vid, getRequestedQuality(session));
                Log.i(TAG, "PLAYER built: title='" + v.title + "' ch='" + v.channelId + "'");
                return xml(GDataXmlBuilder.buildPlayerEntry(v));
            }

            // === /feeds/api/videos/{id}/related ===
            if (uri.startsWith("/feeds/api/videos/") && uri.contains("/related")) {
                String vid = stripQuery(uri.substring("/feeds/api/videos/".length(),
                    uri.indexOf("/related")));
                if (!VIDEO_ID.matcher(vid).matches()) return text(Response.Status.BAD_REQUEST, "Bad id");
                Log.i(TAG, "RELATED: " + vid);

                String body = null;
                JsonObject invVideo = invidious.video(vid);
                if (invVideo != null && invVideo.has("recommendedVideos")
                        && invVideo.get("recommendedVideos").isJsonArray()) {
                    String feed = FeedXmlBuilder.buildInvidiousFeed(
                        invVideo.getAsJsonArray("recommendedVideos"), "related:" + vid, "Related videos");
                    if (feed.contains("<entry")) return xml(feed);
                }
                if (invidious.getHealthyCount() == 0) {
                    body = yt2009.fetchText(appendQuery(
                        "/feeds/api/videos/" + vid + "/related", session.getParms()));
                    if (body != null && body.contains("<entry") && body.length() >= 200) return xml(body);
                }
                try {
                    JsonObject next = innerTube.next(vid);
                    String modernFeed = FeedXmlBuilder.buildRelatedFeed(next, vid);
                    if (modernFeed.contains("<entry")) return xml(modernFeed);
                } catch (Exception e) {
                    Log.w(TAG, "InnerTube related fallback failed: " + e.getMessage());
                }
                if (invidious.getHealthyCount() == 0) {
                    String pop = yt2009.fetchText("/feeds/api/standardfeeds/most_popular");
                    if (pop != null && pop.contains("<entry")) return xml(pop);
                }
                return xml(body != null ? body : emptyFeed("related"));
            }

            // === /feeds/api/videos/{id}/comments ===
            if (uri.startsWith("/feeds/api/videos/") && uri.contains("/comments")) {
                String vid = stripQuery(uri.substring("/feeds/api/videos/".length(),
                    uri.indexOf("/comments")));
                if (!VIDEO_ID.matcher(vid).matches()) return text(Response.Status.BAD_REQUEST, "Bad id");
                Log.i(TAG, "COMMENTS: " + vid);
                JsonObject invComments = invidious.comments(vid);
                if (invComments != null) return xml(FeedXmlBuilder.buildInvidiousComments(invComments, vid));
                if (invidious.getHealthyCount() == 0) {
                    String body = yt2009.fetchText(appendQuery(
                        "/feeds/api/videos/" + vid + "/comments", session.getParms()));
                    if (body != null) return xml(body);
                }
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
                return xml(GDataXmlBuilder.buildPlayerEntry(getVideoData(vid, getRequestedQuality(session))));
            }

            // === /feeds/api/videos?q= — ordered provider fallback ===
            if (uri.startsWith("/feeds/api/videos") && !uri.contains("/videos/")) {
                final String query = session.getParms().get("q") == null
                    ? "" : session.getParms().get("q");
                Log.i(TAG, "SEARCH: " + query);

                SourceManager.Result<String> primary = sourceManager.firstAvailable("search",
                    videoArrayFeedSource("invidious", "search:" + query,
                        "Search results for " + query, new SourceManager.Fetcher<JsonArray>() {
                            @Override public JsonArray fetch() { return invidious.search(query); }
                        }),
                    videoArrayFeedSource("piped", "search:" + query,
                        "Search results for " + query, new SourceManager.Fetcher<JsonArray>() {
                            @Override public JsonArray fetch() { return piped.search(query); }
                        })
                );
                if (primary.isSuccess()) return xml(primary.value);

                // Preserve the existing policy: don't hit legacy fallbacks while
                // either public pool is healthy, even when the query has no matches.
                if (invidious.getHealthyCount() > 0 || piped.getHealthyCount() > 0)
                    return xml(primary.value != null ? primary.value : emptyFeed("search"));

                SourceManager.Result<String> fallback = sourceManager.firstAvailable("search-fallback",
                    rawFeedSource("yt2009", new SourceManager.Fetcher<String>() {
                        @Override public String fetch() throws Exception {
                            return yt2009.fetchText(appendQuery("/feeds/api/videos", session.getParms()));
                        }
                    }),
                    rawFeedSource("innertube", new SourceManager.Fetcher<String>() {
                        @Override public String fetch() {
                            JsonObject modern = innerTube.search(query);
                            return modern == null ? null : FeedXmlBuilder.buildSearchFeed(modern, query);
                        }
                    })
                );
                if (fallback.isSuccess()) return xml(fallback.value);
                if (fallback.value != null) return xml(fallback.value);
                return xml(primary.value != null ? primary.value : emptyFeed("search"));
            }

            // === /feeds/api/playlists/{id} — Invidious first ===
            if (uri.startsWith("/feeds/api/playlists/")) {
                String playlistPath = stripQuery(uri.substring("/feeds/api/playlists/".length()));
                boolean videosPath = playlistPath.endsWith("/videos");
                if (videosPath) playlistPath = playlistPath.substring(0, playlistPath.length() - 7);
                if (playlistPath.indexOf('/') >= 0)
                    playlistPath = playlistPath.substring(0, playlistPath.indexOf('/'));
                if (!PLAYLIST_ID.matcher(playlistPath).matches())
                    return text(Response.Status.BAD_REQUEST, "Bad playlist id");
                JsonObject invPlaylist = invidious.playlist(playlistPath, 1);
                if (invPlaylist != null && invPlaylist.has("videos")
                        && invPlaylist.get("videos").isJsonArray()) {
                    String feed = FeedXmlBuilder.buildInvidiousFeed(
                        invPlaylist.getAsJsonArray("videos"), "playlist:" + playlistPath,
                        VideoData.getStr(invPlaylist, "title", "Playlist " + playlistPath));
                    if (feed.contains("<entry") || invidious.getHealthyCount() > 0) return xml(feed);
                }
                if (invidious.getHealthyCount() == 0) {
                    String legacyPath = "/feeds/api/playlists/" + playlistPath
                        + (videosPath ? "/videos" : "");
                    String body = yt2009.fetchText(appendQuery(legacyPath, session.getParms()));
                    if (body != null && body.contains("<entry")) return xml(body);
                    try {
                        String browseId = playlistPath.startsWith("VL")
                            ? playlistPath : "VL" + playlistPath;
                        JsonObject modern = innerTube.browse(browseId);
                        String modernFeed = FeedXmlBuilder.buildPlaylistFeed(modern, playlistPath);
                        if (modernFeed.contains("<entry")) return xml(modernFeed);
                    } catch (Exception e) {
                        Log.w(TAG, "InnerTube playlist fallback failed: " + e.getMessage());
                    }
                    return xml(body != null ? body : emptyFeed("playlist"));
                }
                return xml(emptyFeed("playlist"));
            }

            // === /feeds/api/standardfeeds/* — ordered provider fallback ===
            if (uri.startsWith("/feeds/api/standardfeeds/")) {
                Log.i(TAG, "FEED: " + uri);
                final String feedName = stripQuery(uri.substring("/feeds/api/standardfeeds/".length()));

                SourceManager.Result<String> primary = sourceManager.firstAvailable("trending",
                    videoArrayFeedSource("invidious", "standard:" + feedName,
                        "Trending videos", new SourceManager.Fetcher<JsonArray>() {
                            @Override public JsonArray fetch() { return invidious.trending(); }
                        }),
                    videoArrayFeedSource("piped", "standard:" + feedName,
                        "Trending videos", new SourceManager.Fetcher<JsonArray>() {
                            @Override public JsonArray fetch() { return piped.trending(); }
                        })
                );
                if (primary.isSuccess()) return xml(primary.value);
                if (invidious.getHealthyCount() > 0 || piped.getHealthyCount() > 0)
                    return xml(primary.value != null ? primary.value : emptyFeed("feed"));

                SourceManager.Source<String> yt2009Source = rawFeedSource("yt2009",
                    new SourceManager.Fetcher<String>() {
                        @Override public String fetch() throws Exception {
                            return yt2009.fetchText(appendQuery(uri, session.getParms()));
                        }
                    });
                SourceManager.Result<String> fallback;
                if ("most_popular".equals(feedName) || "top_rated".equals(feedName)
                        || "recently_featured".equals(feedName)) {
                    fallback = sourceManager.firstAvailable("trending-fallback", yt2009Source,
                        rawFeedSource("innertube", new SourceManager.Fetcher<String>() {
                            @Override public String fetch() {
                                JsonObject modern = innerTube.browse("FEtrending");
                                return modern == null ? null
                                    : FeedXmlBuilder.buildStandardFeed(modern, feedName);
                            }
                        })
                    );
                } else {
                    fallback = sourceManager.firstAvailable("trending-fallback", yt2009Source);
                }
                if (fallback.isSuccess()) return xml(fallback.value);
                if (fallback.value != null) return xml(fallback.value);
                return xml(primary.value != null ? primary.value : emptyFeed("feed"));
            }

            // === /feeds/api/users/default/* ===
            if (uri.startsWith("/feeds/api/users/default/")) {
                String kind = uri.substring("/feeds/api/users/default/".length());
                if (kind.contains("/")) kind = kind.substring(0, kind.indexOf('/'));

                // A logged-out legacy client has no personal subscriptions.
                // Fill its subscriptions screen with a useful public discovery feed.
                if ("subscriptions".equals(kind) || "activity".equals(kind)) {
                    // First try the personalized yt2009 home feed. The account
                    // device id is sent only to the explicitly selected instance,
                    // never to the Invidious/Piped community pools.
                    if (yt2009Account.isAuthorized()) {
                        String personal = yt2009.fetchAccountFeed(
                            "/feeds/api/users/default/recommendations",
                            yt2009Account.getDeviceId());
                        if (personal != null && personal.contains("<entry")) {
                            Log.i(TAG, "Serving personalized yt2009 recommendations");
                            return xml(personal);
                        }
                    }

                    SourceManager.Result<String> publicFeed = sourceManager.firstAvailable(
                        "recommendations",
                        videoArrayFeedSource("invidious", "default:recommendations",
                            "Recommended videos", new SourceManager.Fetcher<JsonArray>() {
                                @Override public JsonArray fetch() { return invidious.trending(); }
                            }),
                        videoArrayFeedSource("piped", "default:recommendations",
                            "Recommended videos", new SourceManager.Fetcher<JsonArray>() {
                                @Override public JsonArray fetch() { return piped.trending(); }
                            })
                    );
                    if (publicFeed.isSuccess()) return xml(publicFeed.value);

                    // The public yt2009 feed is only a fallback when both API pools
                    // are down. InnerTube remains the final unauthenticated option.
                    if (invidious.getHealthyCount() == 0 && piped.getHealthyCount() == 0) {
                        SourceManager.Result<String> ytFallback = sourceManager.firstAvailable(
                            "recommendations-fallback",
                            rawFeedSource("yt2009", new SourceManager.Fetcher<String>() {
                                @Override public String fetch() {
                                    return yt2009.fetchText("/feeds/api/standardfeeds/most_popular");
                                }
                            })
                        );
                        if (ytFallback.isSuccess()) return xml(ytFallback.value);
                    }

                    SourceManager.Result<String> modernFallback = sourceManager.firstAvailable(
                        "recommendations-fallback",
                        rawFeedSource("innertube", new SourceManager.Fetcher<String>() {
                            @Override public String fetch() {
                                JsonObject modern = innerTube.browse("FEtrending");
                                return modern == null ? null
                                    : FeedXmlBuilder.buildStandardFeed(modern, "most_popular");
                            }
                        })
                    );
                    if (modernFallback.isSuccess()) return xml(modernFallback.value);
                }
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

                // Resolve non-UC channel names via Invidious before considering yt2009.
                if (!cid.startsWith("UC") && !sub.isEmpty()) {
                    String uc = nameToUc.get(cid);
                    if (uc == null && invidious.getHealthyCount() > 0) {
                        JsonArray matches = invidious.searchChannels(cid);
                        if (matches != null) {
                            for (int i = 0; i < matches.size(); i++) {
                                try {
                                    String candidate = VideoData.getStr(matches.get(i).getAsJsonObject(), "authorId", "");
                                    if (candidate.startsWith("UC") && candidate.length() == 24) {
                                        uc = candidate;
                                        break;
                                    }
                                } catch (Exception ignored) {}
                            }
                        }
                    }
                    if (uc == null && invidious.getHealthyCount() == 0) {
                        String profile = yt2009.fetchText("/feeds/api/users/" + cid);
                        if (profile != null) uc = extractUcIdFromProfile(profile);
                    }
                    if (uc != null) {
                        nameToUc.put(cid, uc);
                        cid = uc;
                    }
                }

                boolean uploadsRequest = sub.startsWith("uploads");
                if (uploadsRequest && cid.startsWith("UC") && cid.length() == 24) {
                    JsonArray uploads = invidious.channelVideos(cid, 1);
                    if (uploads != null) {
                        String feed = FeedXmlBuilder.buildInvidiousFeed(
                            uploads, "uploads:" + cid, "Uploads from " + cid);
                        if (feed.contains("<entry") || invidious.getHealthyCount() > 0) return xml(feed);
                    }
                    if (invidious.getHealthyCount() > 0) return xml(emptyFeed("uploads"));
                }

                if (sub.isEmpty() && invidious.getHealthyCount() > 0) {
                    JsonObject profile = invidious.channel(cid);
                    if (profile != null) {
                        ChannelData channel = ChannelData.fromInvidious(profile, cid);
                        if (!channel.title.isEmpty() || !channel.videos.isEmpty())
                            return xml(GDataXmlBuilder.buildChannelEntry(channel));
                    }
                    return xml(emptyFeed("user"));
                }

                String ytPath = "/feeds/api/users/" + cid + (sub.isEmpty() ? "" : "/" + sub);
                Log.i(TAG, "CHANNEL: " + ytPath);
                String body = null;
                if (invidious.getHealthyCount() == 0)
                    body = yt2009.fetchText(appendQuery(ytPath, session.getParms()));

                if (body != null && !(uploadsRequest && !body.contains("<entry"))) {
                    if (sub.isEmpty() && !cid.startsWith("UC")) {
                        String uc = extractUcIdFromProfile(body);
                        if (uc != null) nameToUc.put(cid, uc);
                    }
                    return xml(body);
                }

                if (uploadsRequest && cid.startsWith("UC") && cid.length() == 24
                        && invidious.getHealthyCount() == 0) {
                    try {
                        ChannelData channel = ChannelData.fromBrowse(innerTube.browse(cid), cid);
                        if (!channel.videos.isEmpty())
                            return xml(GDataXmlBuilder.buildChannelUploadsFeed(channel));
                    } catch (Exception e) {
                        Log.w(TAG, "InnerTube uploads fallback failed: " + e.getMessage());
                    }
                    String alt = yt2009.fetchText("/feeds/api/videos?author="
                        + java.net.URLEncoder.encode(cid, "UTF-8"));
                    if (alt != null && alt.length() > 200 && alt.contains("<entry")) return xml(alt);
                }
                return xml(emptyFeed(sub.isEmpty() ? "user" : sub));
            }

            // === /resumable/feeds/api/users/default/uploads ===
            if (uri.startsWith("/resumable/feeds/")) {
                Log.i(TAG, "RESUMABLE: " + uri);
                return xml(emptyFeed("resumable"));
            }

            // === /feeds/api/channels/{cid} — Invidious first ===
            if (uri.startsWith("/feeds/api/channels/")) {
                String cid = stripQuery(uri.substring("/feeds/api/channels/".length()));
                Log.i(TAG, "CHANNELS: " + cid);
                String body = null;
                JsonObject invProfile = invidious.channel(cid);
                if (invProfile != null) {
                    ChannelData channel = ChannelData.fromInvidious(invProfile, cid);
                    if (!channel.title.isEmpty() || !channel.videos.isEmpty())
                        body = GDataXmlBuilder.buildChannelEntry(channel);
                }
                if (body == null && invidious.getHealthyCount() == 0) {
                    body = fetchChannelCached(cid);
                    if (body == null && cid.startsWith("UC") && cid.length() == 24) {
                        try {
                            ChannelData channel = ChannelData.fromBrowse(innerTube.browse(cid), cid);
                            if (!channel.title.isEmpty() || !channel.videos.isEmpty())
                                body = GDataXmlBuilder.buildChannelEntry(channel);
                        } catch (Exception e) {
                            Log.w(TAG, "InnerTube channel fallback failed: " + e.getMessage());
                        }
                    }
                }
                if (body != null) {
                    String uc = extractUcIdFromProfile(body);
                    if (uc == null || !uc.startsWith("UC") || uc.length() != 24)
                        uc = (cid.startsWith("UC") && cid.length() == 24) ? cid : "";
                    if (!uc.isEmpty()) {
                        StringBuilder inject = new StringBuilder();
                        if (!body.contains("<link rel=\"self\""))
                            inject.append("<link rel=\"self\" type=\"application/atom+xml\" href=\"http://127.0.0.1:8080/feeds/api/channels/").append(uc).append("?v=2\"/>");
                        if (!body.contains("<link rel=\"alternate\""))
                            inject.append("<link rel=\"alternate\" type=\"text/html\" href=\"http://127.0.0.1:8080/channel/").append(uc).append("\"/>");
                        if (!body.contains("<yt:channelId>")) inject.append("<yt:channelId>").append(uc).append("</yt:channelId>");
                        if (!body.contains("user.uploads")) {
                            inject.append("<gd:feedLink rel=\"http://gdata.youtube.com/schemas/2007#user.uploads\" href=\"http://127.0.0.1:8080/feeds/api/users/").append(uc).append("/uploads\" countHint=\"10\"/>");
                            inject.append("<gd:feedLink rel=\"http://gdata.youtube.com/schemas/2007#user.favorites\" href=\"http://127.0.0.1:8080/feeds/api/users/").append(uc).append("/favorites\" countHint=\"0\"/>");
                            inject.append("<gd:feedLink rel=\"http://gdata.youtube.com/schemas/2007#user.subscriptions\" href=\"http://127.0.0.1:8080/feeds/api/users/").append(uc).append("/subscriptions\" countHint=\"0\"/>");
                        }
                        if (inject.length() > 0) body = body.replace("</entry>", inject.toString() + "</entry>");
                    }
                    return xml(body);
                }
                return xml(emptyFeed("channel"));
            }

            // Keep old GData sign-in links local while opening the same trusted yt2009 instance.
            if ("/gsign".equals(uri)) {
                String device = yt2009Account.getDeviceId();
                if (device == null) return text(Response.Status.UNAUTHORIZED, "Start Google login in ytlegacy-proxy first");
                Response redirect = newFixedLengthResponse(Response.Status.REDIRECT, "text/plain", "");
                redirect.addHeader("Location", Yt2009AccountManager.INSTANCE + "/gsign?device=" + device);
                return redirect;
            }
            if ("/mobile/mobilehelper/account_picker.html".equals(uri)) {
                String device = yt2009Account.getDeviceId();
                if (device == null) return text(Response.Status.UNAUTHORIZED, "No yt2009 login session");
                Response redirect = newFixedLengthResponse(Response.Status.REDIRECT, "text/plain", "");
                redirect.addHeader("Location", Yt2009AccountManager.accountPickerUrl(device));
                return redirect;
            }

            // === /avatar_wait — neutral placeholder while a channel avatar loads ===
            if (uri.equals("/avatar_wait")) {
                return avatarPlaceholderResponse();
            }

            // === /feeds/api/partners/{user}/branding/default ===
            if (uri.startsWith("/feeds/api/partners/") && uri.contains("/branding/")) {
                String path = uri.substring("/feeds/api/partners/".length());
                String user = path.contains("/") ? path.substring(0, path.indexOf('/')) : path;
                Log.i(TAG, "PARTNER_BRANDING: " + user);
                String x = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                    "<entry xmlns=\"http://www.w3.org/2005/Atom\" " +
                    "xmlns:yt=\"http://gdata.youtube.com/schemas/2007\">" +
                    "<id>tag:youtube.com,2008:partner:" + xmlEscape(user) + ":branding</id>" +
                    "<title>Branding for " + xmlEscape(user) + "</title>" +
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
                return xml(GDataXmlBuilder.buildPlayerEntry(getVideoData(vid, getRequestedQuality(session))));
            }

            // === /video/{id}.mp4 — каскад стрима ===
            if (uri.startsWith("/video/")) {
                String fname = stripQuery(uri.substring("/video/".length()));
                int dot = fname.indexOf('.');
                String vid = dot > 0 ? fname.substring(0, dot) : fname;
                if (!VIDEO_ID.matcher(vid).matches()) return text(Response.Status.BAD_REQUEST, "Bad id");
                Log.i(TAG, "VIDEO PROXY: " + vid);

                return servePlayback(vid, session, getRequestedQuality(session));
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
                        "https://img.youtube.com/vi/" + vid + "/" + cdnName + ".jpg",
                        "https://img.youtube.com/vi/" + vid + "/hqdefault.jpg"
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
                return handleChannelImage(cid, "media:thumbnail", null);
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
                    return servePlayback(vid, session, getRequestedQuality(session));
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







    /** Metadata priority: fastest healthy Invidious, then yt2009 if the whole pool is down. */
    private VideoData getVideoData(String vid) {
        return getVideoData(vid, PlaybackSettings.getQuality(applicationContext));
    }

    private VideoData getVideoData(String vid, String quality) {
        VideoData v = null;
        boolean invidiousMetadataLoaded = false;
        try {
            JsonObject data = invidious.video(vid);
            VideoData fromInvidious = VideoData.fromInvidious(data, quality);
            if (fromInvidious != null) {
                v = fromInvidious;
                invidiousMetadataLoaded = true;
                Log.i(TAG, "getVideoData: Invidious OK via " + invidious.getFastestHost());
            }
        } catch (Exception e) {
            Log.w(TAG, "getVideoData: Invidious lookup failed: " + e.getMessage());
        }

        if (v == null) {
            v = new VideoData();
            v.id = vid;
        }

        boolean legacyMetadataLoaded = false;
        if (!invidiousMetadataLoaded && invidious.getHealthyCount() == 0) {
            try {
                Yt2009Client.Info info = yt2009.getVideoInfo(vid);
                if ("ok".equals(info.status)) {
                    v.title = info.title;
                    v.author = info.author;
                    v.lengthSeconds = info.lengthSeconds;
                    legacyMetadataLoaded = true;
                    Log.i(TAG, "getVideoData: yt2009 fallback OK for " + vid);
                } else {
                    Log.w(TAG, "getVideoData: yt2009 fallback status=" + info.status);
                }
            } catch (Exception e) {
                Log.e(TAG, "getVideoData: yt2009 fallback failed: " + e);
            }
        }

        // Ask InnerTube for a stream when Invidious metadata is valid but
        // has no complete progressive MP4, or when yt2009 supplied metadata only.
        if ((!invidiousMetadataLoaded && !legacyMetadataLoaded)
                || v.streamUrl == null || v.streamUrl.isEmpty()) {
            try {
                VideoData modern = VideoData.fromInnerTube(innerTube.player(vid), quality);
                if (modern != null) {
                    if (v.title.isEmpty()) v.title = modern.title;
                    if (v.author.isEmpty()) v.author = modern.author;
                    if (v.channelId.isEmpty()) v.channelId = modern.channelId;
                    if (v.lengthSeconds <= 0) v.lengthSeconds = modern.lengthSeconds;
                    if (v.viewCount <= 0) v.viewCount = modern.viewCount;
                    if (v.description.isEmpty()) v.description = modern.description;
                    if (v.published == null || v.published.startsWith("2013-")) {
                        v.published = modern.published;
                        v.uploadDate = modern.uploadDate;
                    }
                    if (modern.likeCount > 0) v.likeCount = modern.likeCount;
                    v.isLive = modern.isLive;
                    if ((v.streamUrl == null || v.streamUrl.isEmpty())
                            && modern.streamUrl != null && !modern.streamUrl.isEmpty()) {
                        v.streamUrl = modern.streamUrl;
                        v.itag = modern.itag;
                        v.mimeType = modern.mimeType;
                        v.width = modern.width;
                        v.height = modern.height;
                        v.isHd = modern.isHd;
                    }
                    Log.i(TAG, "getVideoData: InnerTube metadata/stream fallback used for " + vid);
                }
            } catch (Exception e) {
                Log.w(TAG, "getVideoData: InnerTube fallback failed: " + e.getMessage());
            }
        }

        if (v.channelId == null || v.channelId.isEmpty())
            v.channelId = (v.author != null && !v.author.isEmpty()) ? v.author : "";

        if (!v.channelId.isEmpty() && (!v.channelId.startsWith("UC") || v.channelId.length() != 24)
                && invidious.getHealthyCount() > 0) {
            try {
                JsonArray channels = invidious.searchChannels(v.author);
                if (channels != null) {
                    for (int i = 0; i < channels.size(); i++) {
                        String candidate = VideoData.getStr(channels.get(i).getAsJsonObject(), "authorId", "");
                        if (candidate.startsWith("UC") && candidate.length() == 24) {
                            v.channelId = candidate;
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                Log.w(TAG, "Invidious channel-id resolution failed: " + e.getMessage());
            }
        } else if (!v.channelId.isEmpty()
                && (!v.channelId.startsWith("UC") || v.channelId.length() != 24)
                && invidious.getHealthyCount() == 0) {
            try {
                String profile = fetchChannelCached(v.channelId);
                String uc = (profile != null) ? extractUcIdFromProfile(profile) : null;
                if (uc != null && uc.startsWith("UC") && uc.length() == 24) v.channelId = uc;
                else v.channelId = (v.author != null) ? v.author : "";
            } catch (Exception e) {
                Log.w(TAG, "getVideoData: channel lookup failed for " + v.author, e);
            }
        }

        if (v.title == null || v.title.isEmpty()) v.title = vid;
        if (v.author == null || v.author.isEmpty()) v.author = "YouTube";
        if (v.channelId == null || v.channelId.isEmpty()) v.channelId = v.author;
        Log.i(TAG, "getVideoData: id=" + v.id + " title='" + v.title + "' ch='" + v.channelId + "'");
        return v;
    }

    private Response servePlayback(final String videoId,
                                  final IHTTPSession session,
                                  final String quality) {
        SourceManager.Result<Response> result = sourceManager.firstAvailable("playback",
            playbackSource("invidious", new SourceManager.Fetcher<Response>() {
                @Override public Response fetch() {
                    return tryInvidiousStream(videoId, session, quality);
                }
            }),
            playbackSource("piped", new SourceManager.Fetcher<Response>() {
                @Override public Response fetch() {
                    return tryPipedStream(videoId, session, quality);
                }
            }),
            playbackSource("yt2009-pool", new SourceManager.Fetcher<Response>() {
                @Override public Response fetch() {
                    java.net.HttpURLConnection connection =
                        yt2009.tryStream(videoId, session, quality);
                    return connection == null ? null : buildStreamResponse(connection);
                }
            }),
            playbackSource("innertube", new SourceManager.Fetcher<Response>() {
                @Override public Response fetch() {
                    return tryInnerTubeStream(videoId, session, quality);
                }
            })
        );

        if (result.isSuccess()) {
            Log.i(TAG, "Playback selected provider=" + result.source
                + " attempts=" + result.attempted + " quality=" + quality);
            return result.value;
        }

        Log.w(TAG, "All playback providers failed for " + videoId + ": " + result.detail);
        byte[] emptyMp4 = (EMPTY_MP4 != null) ? EMPTY_MP4 : new byte[]{
            0,0,0,0x18,'f','t','y','p','i','s','o','m',
            0,0,0x02,0x00,'i','s','o','m','i','s','o','2',
            'a','v','c','1','m','p','4','1',
            0,0,0,0x08,'f','r','e','e',0,0,0,0x00
        };
        String range = session == null || session.getHeaders() == null
            ? null : session.getHeaders().get("range");
        return emptyMp4Response(emptyMp4, range);
    }

    private SourceManager.Source<Response> playbackSource(
            String sourceName, final SourceManager.Fetcher<Response> fetcher) {
        return SourceManager.Source.<Response>of(sourceName,
            new SourceManager.Fetcher<Response>() {
                @Override public Response fetch() throws Exception {
                    return fetcher.fetch();
                }
            },
            new SourceManager.DataCheck<Response>() {
                @Override public boolean hasData(Response response) throws Exception {
                    if (response == null || response.getStatus() == null)
                        throw new IOException("Provider returned no playback response");
                    int status = response.getStatus().getRequestStatus();
                    if (status == 200 || status == 206 || status == 416) return true;
                    throw new IOException("Provider returned HTTP " + status);
                }
            });
    }

    private Response tryInvidiousStream(String videoId, IHTTPSession session, String quality) {
        try {
            JsonObject player = invidious.video(videoId);
            VideoData video = VideoData.fromInvidious(player, quality);
            if (video == null || video.streamUrl == null || video.streamUrl.length() == 0) return null;
            if (!invidious.isTrustedStreamUrl(video.streamUrl)) {
                Log.w(TAG, "Rejected untrusted Invidious stream URL for " + videoId);
                return null;
            }
            Response response = proxyVideo(video.streamUrl, session);
            int status = response.getStatus().getRequestStatus();
            if (status == 200 || status == 206 || status == 416) {
                Log.i(TAG, "Invidious stream OK for " + videoId + " itag=" + video.itag);
                return response;
            }
            Log.w(TAG, "Invidious stream returned " + status + " for " + videoId);
        } catch (Exception e) {
            Log.w(TAG, "Invidious stream failed for " + videoId + ": " + e.getMessage());
        }
        return null;
    }

    private Response tryPipedStream(String videoId, IHTTPSession session, String quality) {
        try {
            JsonObject streams = piped.streams(videoId);
            PipedStreamSelector.Selection selected = PipedStreamSelector.select(streams, quality);
            if (selected == null) return null;
            if (!PipedStreamSelector.isTrustedStreamUrl(selected.proxyUrl, selected.url)) {
                Log.w(TAG, "Rejected Piped stream URL outside its advertised proxy origin");
                return null;
            }

            Response response = proxyVideo(selected.url, session, selected.proxyUrl);
            int status = response.getStatus().getRequestStatus();
            if (status == 200 || status == 206 || status == 416) {
                Log.i(TAG, "Piped stream OK for " + videoId + " height="
                    + selected.height + " codec=" + selected.codec);
                return response;
            }
            Log.w(TAG, "Piped stream returned HTTP " + status + " for " + videoId);
        } catch (Exception e) {
            Log.w(TAG, "Piped stream fallback failed for " + videoId + ": " + e.getMessage());
        }
        return null;
    }

    private Response tryInnerTubeStream(String videoId, IHTTPSession session, String quality) {
        try {
            JsonObject player = innerTube.player(videoId);
            VideoData modern = VideoData.fromInnerTube(player, quality);
            if (modern == null || modern.streamUrl == null || modern.streamUrl.isEmpty()) return null;

            java.net.URL streamUrl = new java.net.URL(modern.streamUrl);
            String host = streamUrl.getHost() == null ? "" : streamUrl.getHost().toLowerCase(java.util.Locale.US);
            if (!"https".equalsIgnoreCase(streamUrl.getProtocol())
                    || !(host.equals("googlevideo.com") || host.endsWith(".googlevideo.com"))) {
                Log.w(TAG, "Rejected non-Google video stream host: " + host);
                return null;
            }

            Response response = proxyVideo(modern.streamUrl, session);
            int status = response.getStatus().getRequestStatus();
            if (status == 200 || status == 206 || status == 416) {
                Log.i(TAG, "InnerTube stream OK for " + videoId + " itag=" + modern.itag);
                return response;
            }
            Log.w(TAG, "InnerTube stream returned HTTP " + status + " for " + videoId);
        } catch (Exception e) {
            Log.w(TAG, "InnerTube stream fallback failed for " + videoId + ": " + e.getMessage());
        }
        return null;
    }

    private Response buildStreamResponse(java.net.HttpURLConnection yc) {
        boolean handedOff = false;
        try {
            int code = yc.getResponseCode();
            if (StreamHttpSupport.isRangeNotSatisfiable(code)) {
                Response response = text(Response.Status.RANGE_NOT_SATISFIABLE, "Range Not Satisfiable");
                String contentRange = yc.getHeaderField("Content-Range");
                if (contentRange != null) response.addHeader("Content-Range", contentRange);
                response.addHeader("Accept-Ranges", "bytes");
                return response;
            }
            if (!StreamHttpSupport.isSuccessfulStreamStatus(code)) {
                return text(Response.Status.INTERNAL_ERROR, "yt2009 stream HTTP " + code);
            }
            String contentType = yc.getContentType();
            if (contentType == null) contentType = "video/mp4";
            int length = yc.getContentLength();
            java.io.InputStream input = disconnectingStream(yc, yc.getInputStream());
            Response.Status status = StreamHttpSupport.isPartialContent(code)
                ? Response.Status.PARTIAL_CONTENT : Response.Status.OK;
            Response response = length >= 0
                ? newFixedLengthResponse(status, contentType, input, length)
                : newChunkedResponse(status, contentType, input);
            String contentRange = yc.getHeaderField("Content-Range");
            if (contentRange != null) response.addHeader("Content-Range", contentRange);
            String acceptRanges = yc.getHeaderField("Accept-Ranges");
            response.addHeader("Accept-Ranges", acceptRanges != null ? acceptRanges : "bytes");
            handedOff = true;
            return response;
        } catch (Exception e) {
            Log.e(TAG, "buildStreamResponse error: " + e.getMessage(), e);
            return text(Response.Status.INTERNAL_ERROR, "stream unavailable");
        } finally {
            if (!handedOff) yc.disconnect();
        }
    }

    private static java.io.InputStream disconnectingStream(
            final HttpURLConnection connection, java.io.InputStream input) {
        return new java.io.FilterInputStream(input) {
            private boolean closed;
            @Override public void close() throws IOException {
                if (closed) return;
                closed = true;
                try { super.close(); }
                finally { connection.disconnect(); }
            }
        };
    }

    private Response handleChannelImage(String cid, String tagName, String redirectFallback) {
        boolean avatar = "media:thumbnail".equals(tagName);
        if (invidious.getHealthyCount() > 0) {
            try {
                JsonObject channel = invidious.channel(cid);
                if (channel != null) {
                    String key = avatar ? "authorThumbnails" : "authorBanners";
                    if (channel.has(key) && channel.get(key).isJsonArray()) {
                        JsonArray images = channel.getAsJsonArray(key);
                        // Invidious usually returns thumbnails from small to large.
                        // Try every size so one stale CDN URL does not force a placeholder.
                        for (int i = images.size() - 1; i >= 0; i--) {
                            try {
                                JsonObject image = images.get(i).getAsJsonObject();
                                String url = VideoData.getStr(image, "url", "");
                                if (url.length() == 0) continue;
                                Log.i(TAG, "Invidious channel image candidate: " + url);
                                Response response = proxyChannelImage(url);
                                if (response != null) return response;
                            } catch (Exception ignored) {}
                        }
                    }
                }
            } catch (Exception e) {
                Log.w(TAG, "Invidious channel image failed: " + e.getMessage());
            }
            // Never contact yt2009 while the Invidious pool is healthy.
        } else {
            try {
                String feed = yt2009.fetchText("/feeds/api/users/" + cid);
                if (feed != null) {
                    java.util.regex.Matcher m = java.util.regex.Pattern
                        .compile("<" + tagName.replace(":", "\\:") + "[^>]*url=['\"]([^'\"]+)['\"]")
                        .matcher(feed);
                    if (m.find()) {
                        String url = m.group(1);
                        if (url.startsWith("//")) url = "https:" + url;
                        Log.i(TAG, "img from yt2009 fallback: " + url);
                        Response response = proxyChannelImage(url);
                        if (response != null) return response;
                    }
                }
            } catch (Exception e) {
                Log.w(TAG, "yt2009 channel image failed: " + e.getMessage());
            }
        }

        if (avatar) return avatarPlaceholderResponse();
        if (redirectFallback != null) {
            Response r = newFixedLengthResponse(Response.Status.REDIRECT, "text/plain", "");
            r.addHeader("Location", redirectFallback);
            return r;
        }
        return text(Response.Status.NOT_FOUND, "no image");
    }

    /** Fetches a channel image without ever substituting an unrelated video thumbnail. */
    private Response proxyChannelImage(String url) {
        Response response = proxyImageTry(url);
        if (response != null) return response;

        String[] hosts = { "yt4.googleusercontent.com", "lh4.googleusercontent.com" };
        String[] sourceHosts = {
            "yt3.ggpht.com", "yt4.ggpht.com",
            "yt3.googleusercontent.com", "lh3.googleusercontent.com"
        };
        for (String host : hosts) {
            String alternate = url;
            for (String source : sourceHosts) alternate = alternate.replace(source, host);
            if (!alternate.equals(url)) {
                response = proxyImageTry(alternate);
                if (response != null) return response;
            }
        }
        return null;
    }

    /** A neutral local silhouette used only when a real channel avatar is unavailable. */
    private Response avatarPlaceholderResponse() {
        try {
            android.graphics.Bitmap bitmap = android.graphics.Bitmap.createBitmap(
                96, 96, android.graphics.Bitmap.Config.ARGB_8888);
            android.graphics.Canvas canvas = new android.graphics.Canvas(bitmap);
            android.graphics.Paint paint = new android.graphics.Paint(
                android.graphics.Paint.ANTI_ALIAS_FLAG);
            paint.setColor(android.graphics.Color.rgb(232, 235, 239));
            canvas.drawCircle(48, 48, 48, paint);
            paint.setColor(android.graphics.Color.rgb(126, 136, 148));
            canvas.drawCircle(48, 35, 16, paint);
            canvas.drawOval(new android.graphics.RectF(19, 57, 77, 105), paint);

            java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();
            bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, output);
            bitmap.recycle();
            byte[] image = output.toByteArray();
            Response response = newFixedLengthResponse(Response.Status.OK, "image/png",
                new java.io.ByteArrayInputStream(image), image.length);
            response.addHeader("Cache-Control", "public, max-age=3600");
            return response;
        } catch (Exception e) {
            Log.w(TAG, "Avatar placeholder generation failed: " + e.getMessage());
            return text(Response.Status.NOT_FOUND, "no avatar");
        }
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
        // Never substitute an unrelated video thumbnail for the requested image.
        Log.w(TAG, "IMG all failed: " + url);
        return text(Response.Status.NOT_FOUND, "img all failed");
    }

    private static boolean isAllowedImageUrl(java.net.URL url) {
        if (url == null || !"https".equalsIgnoreCase(url.getProtocol())) return false;
        if (url.getUserInfo() != null || (url.getPort() != -1 && url.getPort() != 443)) return false;
        String host = url.getHost() == null ? "" : url.getHost().toLowerCase(java.util.Locale.US);
        return host.equals("img.youtube.com")
            || host.endsWith(".ytimg.com") || host.equals("ytimg.com")
            || host.endsWith(".ggpht.com") || host.equals("ggpht.com")
            || host.endsWith(".googleusercontent.com") || host.equals("googleusercontent.com");
    }

    private Response proxyImageTry(String rawUrl) {
        HttpURLConnection connection = null;
        boolean handedOff = false;
        try {
            java.net.URL currentUrl = new java.net.URL(rawUrl);
            for (int redirects = 0; redirects <= 4; redirects++) {
                if (!isAllowedImageUrl(currentUrl)) {
                    Log.w(TAG, "Rejected image redirect outside HTTPS allowlist: " + currentUrl);
                    return null;
                }

                connection = (HttpURLConnection) currentUrl.openConnection();
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(15000);
                connection.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Linux; Android 4.4) AppleWebKit/537.36 Chrome/120.0 Mobile Safari/537.36");
                connection.setRequestProperty("Accept", "image/*,*/*;q=0.8");
                connection.setInstanceFollowRedirects(false);
                int code = connection.getResponseCode();

                if (code == 301 || code == 302 || code == 303 || code == 307 || code == 308) {
                    String location = connection.getHeaderField("Location");
                    if (location == null || location.trim().length() == 0 || redirects == 4) return null;
                    java.net.URL redirectUrl = new java.net.URL(currentUrl, location);
                    connection.disconnect();
                    connection = null;
                    currentUrl = redirectUrl;
                    continue;
                }
                if (code != HttpURLConnection.HTTP_OK) return null;

                String contentType = connection.getContentType();
                if (contentType == null || !contentType.toLowerCase(java.util.Locale.US).startsWith("image/")) {
                    Log.w(TAG, "Rejected non-image response from " + currentUrl + ": " + contentType);
                    return null;
                }
                int length = connection.getContentLength();
                if (length > 16 * 1024 * 1024) {
                    Log.w(TAG, "Rejected oversized image response: " + length);
                    return null;
                }

                java.io.InputStream input = disconnectingStream(connection, connection.getInputStream());
                Response response = length >= 0
                    ? newFixedLengthResponse(Response.Status.OK, contentType, input, length)
                    : newChunkedResponse(Response.Status.OK, contentType, input);
                response.addHeader("Cache-Control", "public, max-age=86400");
                handedOff = true;
                Log.i(TAG, "IMG OK: " + currentUrl + " (" + length + "b)");
                return response;
            }
            return null;
        } catch (Exception e) {
            Log.w(TAG, "IMG err: " + rawUrl + " -> " + e.getClass().getSimpleName());
            return null;
        } finally {
            if (!handedOff && connection != null) connection.disconnect();
        }
    }

    private boolean isTrustedVideoUrl(java.net.URL url) {
        return isTrustedVideoUrl(url, null);
    }

    private boolean isTrustedVideoUrl(java.net.URL url, String pipedProxyUrl) {
        if (url == null || !"https".equalsIgnoreCase(url.getProtocol())) return false;
        if (url.getUserInfo() != null || (url.getPort() != -1 && url.getPort() != 443)) return false;
        String host = url.getHost() == null ? "" : url.getHost().toLowerCase(java.util.Locale.US);
        boolean googleVideo = host.equals("googlevideo.com") || host.endsWith(".googlevideo.com");
        return googleVideo || invidious.isTrustedStreamUrl(url.toString())
            || PipedStreamSelector.isTrustedStreamUrl(pipedProxyUrl, url.toString());
    }

    private Response proxyVideo(String rawUrl, IHTTPSession session) {
        return proxyVideo(rawUrl, session, null);
    }

    private Response proxyVideo(String rawUrl, IHTTPSession session, String pipedProxyUrl) {
        HttpURLConnection connection = null;
        boolean handedOff = false;
        try {
            java.net.URL currentUrl = new java.net.URL(rawUrl);
            String range = null;
            if (session != null && session.getHeaders() != null) {
                range = session.getHeaders().get("range");
            }

            for (int redirects = 0; redirects <= 5; redirects++) {
                if (!isTrustedVideoUrl(currentUrl, pipedProxyUrl)) {
                    Log.w(TAG, "Rejected untrusted video URL/redirect: " + currentUrl);
                    return text(Response.Status.INTERNAL_ERROR, "untrusted video host");
                }

                connection = (HttpURLConnection) currentUrl.openConnection();
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(60000);
                connection.setRequestProperty("User-Agent",
                    "com.google.android.youtube/5.0.21 (Linux; U; Android 4.4.2; en_US)");
                connection.setInstanceFollowRedirects(false);
                StreamHttpSupport.applyRangeHeader(connection, range);

                int code = connection.getResponseCode();
                if (code == 301 || code == 302 || code == 303 || code == 307 || code == 308) {
                    String location = connection.getHeaderField("Location");
                    if (location == null || location.trim().length() == 0 || redirects == 5) {
                        return text(Response.Status.INTERNAL_ERROR, "video redirect unavailable");
                    }
                    java.net.URL redirectUrl = new java.net.URL(currentUrl, location);
                    connection.disconnect();
                    connection = null;
                    currentUrl = redirectUrl;
                    continue;
                }

                Response.Status status;
                if (code == HttpURLConnection.HTTP_PARTIAL) status = Response.Status.PARTIAL_CONTENT;
                else if (code == HttpURLConnection.HTTP_OK) status = Response.Status.OK;
                else if (code == 416) {
                    Response response = text(Response.Status.RANGE_NOT_SATISFIABLE, "Range Not Satisfiable");
                    String contentRange = connection.getHeaderField("Content-Range");
                    if (contentRange != null) response.addHeader("Content-Range", contentRange);
                    response.addHeader("Accept-Ranges", "bytes");
                    return response;
                } else {
                    Log.w(TAG, "Video upstream returned HTTP " + code);
                    return text(Response.Status.INTERNAL_ERROR, "video upstream HTTP " + code);
                }

                String contentType = connection.getContentType();
                if (contentType == null) contentType = "video/mp4";
                int length = connection.getContentLength();
                java.io.InputStream input = disconnectingStream(connection, connection.getInputStream());
                Response response = length >= 0
                    ? newFixedLengthResponse(status, contentType, input, length)
                    : newChunkedResponse(status, contentType, input);
                String contentRange = connection.getHeaderField("Content-Range");
                if (contentRange != null) response.addHeader("Content-Range", contentRange);
                String acceptRanges = connection.getHeaderField("Accept-Ranges");
                response.addHeader("Accept-Ranges", acceptRanges != null ? acceptRanges : "bytes");
                handedOff = true;
                return response;
            }
            return text(Response.Status.INTERNAL_ERROR, "too many video redirects");
        } catch (Exception e) {
            Log.e(TAG, "proxyVideo error: " + e.getMessage(), e);
            return text(Response.Status.INTERNAL_ERROR, "video stream unavailable");
        } finally {
            if (!handedOff && connection != null) connection.disconnect();
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

    private static String xmlEscape(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;").replace("<", "&lt;")
            .replace(">", "&gt;").replace("\"", "&quot;")
            .replace("'", "&apos;");
    }

    private static String urlEncode(String value) {
        try { return URLEncoder.encode(value == null ? "" : value, "UTF-8"); }
        catch (Exception ignored) { return ""; }
    }

    private static String stripQuery(String s) {
        int q = s.indexOf('?');
        return q >= 0 ? s.substring(0, q) : s;
    }

    private String getRequestedQuality(IHTTPSession session) {
        String requested = session == null ? null : session.getParms().get("quality");
        if (requested != null && requested.trim().length() > 0) return requested.trim();
        return PlaybackSettings.getQuality(applicationContext);
    }

    private SourceManager.Source<String> videoArrayFeedSource(
            String sourceName, final String feedId, final String title,
            final SourceManager.Fetcher<JsonArray> fetcher) {
        return SourceManager.Source.<String>of(sourceName,
            new SourceManager.Fetcher<String>() {
                @Override public String fetch() throws Exception {
                    JsonArray videos = fetcher.fetch();
                    if (videos == null) return null;
                    return FeedXmlBuilder.buildInvidiousFeed(videos, feedId, title);
                }
            },
            new SourceManager.DataCheck<String>() {
                @Override public boolean hasData(String feed) {
                    return feed != null && feed.contains("<entry");
                }
            });
    }

    private SourceManager.Source<String> rawFeedSource(
            String sourceName, final SourceManager.Fetcher<String> fetcher) {
        return SourceManager.Source.<String>of(sourceName,
            new SourceManager.Fetcher<String>() {
                @Override public String fetch() throws Exception {
                    String feed = fetcher.fetch();
                    if (feed == null) return null;
                    if (!feed.contains("<feed"))
                        throw new IOException("Provider returned an invalid Atom feed");
                    return feed;
                }
            },
            new SourceManager.DataCheck<String>() {
                @Override public boolean hasData(String feed) {
                    return feed != null && feed.contains("<entry");
                }
            });
    }

    private Response healthResponse() {
        JsonObject status = new JsonObject();
        status.addProperty("ok", true);
        status.addProperty("version", VERSION);
        status.addProperty("uptimeSeconds", Math.max(0L, (System.currentTimeMillis() - startedAt) / 1000L));
        status.addProperty("requests", requestCount.get());
        String selectedBackend = invidious.getHealthyCount() > 0 ? "invidious"
            : (piped.getHealthyCount() > 0 ? "piped" : "yt2009-fallback");
        status.addProperty("backend", selectedBackend);
        status.addProperty("playbackQuality", PlaybackSettings.getQuality(applicationContext));
        status.add("invidious", invidious.getStatus());
        status.add("piped", piped.getStatus());
        status.add("sourceRouting", sourceManager.getStatus());

        JsonObject account = new JsonObject();
        account.addProperty("optional", true);
        account.addProperty("instance", Yt2009AccountManager.INSTANCE);
        account.addProperty("deviceConfigured", yt2009Account.getDeviceId() != null);
        account.addProperty("authorized", yt2009Account.isAuthorized());
        account.addProperty("accountPicker", yt2009Account.getAccountPickerUrl());
        status.add("yt2009Account", account);

        JsonObject fallback = new JsonObject();
        fallback.addProperty("configuredInstances", yt2009.getConfiguredInstanceCount());
        fallback.addProperty("healthyInstances", yt2009.getHealthyInstanceCount());
        fallback.addProperty("usingFallback",
            invidious.getHealthyCount() == 0 && piped.getHealthyCount() == 0);
        status.add("yt2009Fallback", fallback);

        JsonObject cache = new JsonObject();
        cache.addProperty("channelEntries", channelCache.size());
        cache.addProperty("channelHits", channelCacheHits.get());
        cache.addProperty("channelMisses", channelCacheMisses.get());
        status.add("cache", cache);

        JsonArray features = new JsonArray();
        features.add(new com.google.gson.JsonPrimitive("invidious-primary-fastest-first"));
        features.add(new com.google.gson.JsonPrimitive("piped-secondary-read-only"));
        features.add(new com.google.gson.JsonPrimitive("public-feeds-no-login"));
        features.add(new com.google.gson.JsonPrimitive("yt2009-gsign-personalized-feed-optional"));
        features.add(new com.google.gson.JsonPrimitive("yt2009-last-resort"));
        features.add(new com.google.gson.JsonPrimitive("search-suggestions"));
        features.add(new com.google.gson.JsonPrimitive("playlist-feeds"));
        features.add(new com.google.gson.JsonPrimitive("InnerTube-tertiary-stream-fallback"));
        status.add("features", features);
        return json(status.toString());
    }

    private Response cacheResponse() {
        JsonObject result = new JsonObject();
        result.addProperty("channelEntries", channelCache.size());
        result.addProperty("nameMappings", nameToUc.size());
        result.addProperty("likeEntries", likeCache.size());
        result.addProperty("channelHits", channelCacheHits.get());
        result.addProperty("channelMisses", channelCacheMisses.get());
        result.addProperty("channelTtlMs", CHANNEL_TTL);
        return json(result.toString());
    }

    private int clearLocalCaches() {
        int cleared = channelCache.size() + nameToUc.size()
            + likeCache.size() + likeCacheTime.size();
        channelCache.clear();
        channelCacheTime.clear();
        nameToUc.clear();
        likeCache.clear();
        likeCacheTime.clear();
        return cleared;
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

    private static String emptyFeed(String type) {
        String safeType = xmlEscape(type);
        String encodedType = urlEncode(type);
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<feed xmlns=\"http://www.w3.org/2005/Atom\" " +
            "xmlns:gd=\"http://schemas.google.com/g/2005\" " +
            "xmlns:yt=\"http://gdata.youtube.com/schemas/2007\" " +
            "xmlns:media=\"http://search.yahoo.com/mrss/\" " +
            "xmlns:openSearch=\"http://a9.com/-/spec/opensearch/1.1/\">\n" +
            "  <id>tag:youtube.com,2008:feed:" + safeType + "</id>\n" +
            "  <title type=\"text\">" + safeType + "</title>\n" +
            "  <updated>2013-08-07T00:00:00.000Z</updated>\n" +
            "  <openSearch:totalResults>0</openSearch:totalResults>\n" +
            "  <openSearch:startIndex>1</openSearch:startIndex>\n" +
            "  <openSearch:itemsPerPage>25</openSearch:itemsPerPage>\n" +
            "  <link rel=\"self\" type=\"application/atom+xml\" href=\"" + LOCAL_BASE + "/feeds/api/" + encodedType + "\" />\n" +
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
        String cached = channelCache.get(cid);
        if (ts != null && cached != null) {
            long ttl = cached.length() == 0 ? 30000L : CHANNEL_TTL;
            if (now - ts.longValue() < ttl) {
                channelCacheHits.incrementAndGet();
                Log.d(TAG, "CHANNEL CACHE HIT: " + cid);
                return cached.length() == 0 ? null : cached;
            }
        }

        channelCacheMisses.incrementAndGet();
        String body = yt2009.fetchText("/feeds/api/users/" + cid);
        if (body != null && body.length() > 100) {
            if (channelCache.size() >= 128) {
                channelCache.clear();
                channelCacheTime.clear();
            }
            channelCache.put(cid, body);
            channelCacheTime.put(cid, now);
            return body;
        }

        channelCache.put(cid, "");
        channelCacheTime.put(cid, now);
        return null;
    }

    private String extractUcIdFromProfile(String xmlBody) {
        if (xmlBody == null) return null;
        java.util.regex.Matcher m = java.util.regex.Pattern
            .compile("UC[A-Za-z0-9_-]{22}").matcher(xmlBody);
        if (m.find()) return m.group(0);
        return null;
    }
}
