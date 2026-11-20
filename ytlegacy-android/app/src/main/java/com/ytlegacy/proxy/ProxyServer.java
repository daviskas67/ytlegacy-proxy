package com.ytlegacy.proxy;

import android.util.Log;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fi.iki.elonen.NanoHTTPD;

import java.io.IOException;
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

    private static final String VERSION = "1.10";
    private final InvidiousClient invidious = new InvidiousClient();
    private final PipedClient piped = new PipedClient();
    private final Yt2009Client yt2009 = new Yt2009Client();
    private final InnerTubeClient innerTube = new InnerTubeClient();
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
        super("127.0.0.1", 8080);
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
                VideoData v = getVideoData(vid);
                JsonObject result = new JsonObject();
                result.addProperty("id", v.id);
                result.addProperty("title", v.title);
                result.addProperty("author", v.author);
                result.addProperty("channelId", v.channelId);
                result.addProperty("durationSeconds", v.lengthSeconds);
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
                return xml(GDataXmlBuilder.buildPlayerEntry(getVideoData(vid)));
            }

            // === /feeds/api/videos?q= — Invidious, then Piped, then legacy fallbacks ===
            if (uri.startsWith("/feeds/api/videos") && !uri.contains("/videos/")) {
                String query = session.getParms().get("q");
                if (query == null) query = "";
                Log.i(TAG, "SEARCH: " + query);

                JsonArray videos = invidious.search(query);
                if (videos != null && videos.size() > 0) {
                    String feed = FeedXmlBuilder.buildInvidiousFeed(
                        videos, "search:" + query, "Search results for " + query);
                    if (feed.contains("<entry")) return xml(feed);
                }

                JsonArray pipedVideos = piped.search(query);
                if (pipedVideos != null && pipedVideos.size() > 0) {
                    String feed = FeedXmlBuilder.buildInvidiousFeed(
                        pipedVideos, "search:" + query, "Search results for " + query);
                    if (feed.contains("<entry")) return xml(feed);
                }

                if (invidious.getHealthyCount() > 0 || piped.getHealthyCount() > 0)
                    return xml(emptyFeed("search"));

                String body = yt2009.fetchText(appendQuery("/feeds/api/videos", session.getParms()));
                if (body != null && body.contains("<entry")) return xml(body);
                try {
                    JsonObject modern = innerTube.search(query);
                    String modernFeed = FeedXmlBuilder.buildSearchFeed(modern, query);
                    if (modernFeed.contains("<entry")) return xml(modernFeed);
                } catch (Exception e) {
                    Log.w(TAG, "InnerTube search fallback failed: " + e.getMessage());
                }
                return xml(body != null ? body : emptyFeed("search"));
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

            // === /feeds/api/standardfeeds/* — Invidious, then Piped trending ===
            if (uri.startsWith("/feeds/api/standardfeeds/")) {
                Log.i(TAG, "FEED: " + uri);
                String feedName = stripQuery(uri.substring("/feeds/api/standardfeeds/".length()));
                JsonArray trending = invidious.trending();
                if (trending == null || trending.size() == 0) trending = piped.trending();
                if (trending != null && trending.size() > 0) {
                    String feed = FeedXmlBuilder.buildInvidiousFeed(
                        trending, "standard:" + feedName, "Trending videos");
                    if (feed.contains("<entry")) return xml(feed);
                }

                if (invidious.getHealthyCount() > 0 || piped.getHealthyCount() > 0)
                    return xml(emptyFeed("feed"));

                String body = yt2009.fetchText(appendQuery(uri, session.getParms()));
                if (body != null && body.contains("<entry")) return xml(body);
                if ("most_popular".equals(feedName) || "top_rated".equals(feedName)
                        || "recently_featured".equals(feedName)) {
                    try {
                        JsonObject modern = innerTube.browse("FEtrending");
                        String modernFeed = FeedXmlBuilder.buildStandardFeed(modern, feedName);
                        if (modernFeed.contains("<entry")) return xml(modernFeed);
                    } catch (Exception e) {
                        Log.w(TAG, "InnerTube standard feed fallback failed: " + e.getMessage());
                    }
                }
                return xml(body != null ? body : emptyFeed("feed"));
            }

            // === /feeds/api/users/default/* ===
            if (uri.startsWith("/feeds/api/users/default/")) {
                String kind = uri.substring("/feeds/api/users/default/".length());
                if (kind.contains("/")) kind = kind.substring(0, kind.indexOf('/'));

                // A logged-out legacy client has no personal subscriptions.
                // Fill its subscriptions screen with a useful public discovery feed.
                if ("subscriptions".equals(kind) || "activity".equals(kind)) {
                    JsonArray recommendations = invidious.trending();
                    if (recommendations == null || recommendations.size() == 0)
                        recommendations = piped.trending();
                    if (recommendations != null && recommendations.size() > 0) {
                        return xml(FeedXmlBuilder.buildInvidiousFeed(
                            recommendations, "default:recommendations", "Recommended videos"));
                    }

                    if (invidious.getHealthyCount() == 0 && piped.getHealthyCount() == 0) {
                        String popular = yt2009.fetchText("/feeds/api/standardfeeds/most_popular");
                        if (popular != null && popular.contains("<entry")) return xml(popular);
                    }

                    // InnerTube is the final public-feed fallback. This is discovery,
                    // not a personalized account recommendation feed.
                    try {
                        JsonObject modern = innerTube.browse("FEtrending");
                        String feed = FeedXmlBuilder.buildStandardFeed(modern, "most_popular");
                        if (feed.contains("<entry")) return xml(feed);
                    } catch (Exception e) {
                        Log.w(TAG, "Default recommendations fallback failed: " + e.getMessage());
                    }
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

                Response invidiousStream = tryInvidiousStream(vid, session);
                if (invidiousStream != null) return invidiousStream;

                if (invidious.getHealthyCount() == 0) {
                    java.net.HttpURLConnection yc = yt2009.tryStream(vid, session);
                    if (yc != null) {
                        Log.i(TAG, "STREAM via yt2009 fallback OK");
                        return buildStreamResponse(yc);
                    }
                }

                Log.w(TAG, "Invidious stream unavailable for " + vid + ", trying InnerTube");
                Response modernStream = tryInnerTubeStream(vid, session);
                if (modernStream != null) return modernStream;

                Log.w(TAG, "All stream sources unavailable for " + vid + ", returning empty MP4");
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
                    Response invidiousStream = tryInvidiousStream(vid, session);
                    if (invidiousStream != null) return invidiousStream;
                    if (invidious.getHealthyCount() == 0) {
                        java.net.HttpURLConnection yc = yt2009.tryStream(vid, session);
                        if (yc != null) {
                            Log.i(TAG, "STREAM legacy via yt2009 fallback OK");
                            return buildStreamResponse(yc);
                        }
                    }
                    Log.w(TAG, "Legacy stream unavailable from Invidious/yt2009; trying InnerTube");
                    Response modernStream = tryInnerTubeStream(vid, session);
                    if (modernStream != null) return modernStream;                } else {
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
        VideoData v = null;
        boolean invidiousMetadataLoaded = false;
        try {
            JsonObject data = invidious.video(vid);
            VideoData fromInvidious = VideoData.fromInvidious(data);
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

        if (!invidiousMetadataLoaded && !legacyMetadataLoaded) {
            try {
                VideoData modern = VideoData.fromInnerTube(innerTube.player(vid));
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
                    Log.i(TAG, "getVideoData: InnerTube tertiary fallback used for " + vid);
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

    private Response tryInvidiousStream(String videoId, IHTTPSession session) {
        try {
            JsonObject player = invidious.video(videoId);
            VideoData video = VideoData.fromInvidious(player);
            if (video == null || video.streamUrl == null || video.streamUrl.length() == 0) return null;
            if (!invidious.isTrustedStreamUrl(video.streamUrl)) {
                Log.w(TAG, "Rejected untrusted Invidious stream URL for " + videoId);
                return null;
            }
            Response response = proxyVideo(video.streamUrl, session);
            int status = response.getStatus().getRequestStatus();
            if (status == 200 || status == 206) {
                Log.i(TAG, "Invidious stream OK for " + videoId + " itag=" + video.itag);
                return response;
            }
            Log.w(TAG, "Invidious stream returned " + status + " for " + videoId);
        } catch (Exception e) {
            Log.w(TAG, "Invidious stream failed for " + videoId + ": " + e.getMessage());
        }
        return null;
    }

    private Response tryInnerTubeStream(String videoId, IHTTPSession session) {
        try {
            JsonObject player = innerTube.player(videoId);
            VideoData modern = VideoData.fromInnerTube(player);
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
            if (status == 200 || status == 206) {
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

    private Response healthResponse() {
        JsonObject status = new JsonObject();
        status.addProperty("ok", true);
        status.addProperty("version", VERSION);
        status.addProperty("uptimeSeconds", Math.max(0L, (System.currentTimeMillis() - startedAt) / 1000L));
        status.addProperty("requests", requestCount.get());
        String selectedBackend = invidious.getHealthyCount() > 0 ? "invidious"
            : (piped.getHealthyCount() > 0 ? "piped" : "yt2009-fallback");
        status.addProperty("backend", selectedBackend);
        status.add("invidious", invidious.getStatus());
        status.add("piped", piped.getStatus());

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
