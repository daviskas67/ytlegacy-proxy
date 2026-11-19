package com.ytlegacy.proxy;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class VideoData {
    public String id = "";
    public String title = "";
    public String author = "";
    public String channelId = "";
    public int lengthSeconds = 0;
    public long viewCount = 0;
    public String description = "";
    public String uploadDate = "20130101";
    public String published = "2013-01-01T00:00:00.000Z";
    public int itag = 18;
    public String streamUrl = "";
    public String mimeType = "video/mp4";
    public int width = 480;
    public int height = 360;
    public boolean isHd = false;
    public boolean isLive = false;
    public String category = "Entertainment";
    public List<String> tags = new ArrayList<>();
    public long likeCount = 0;

    /** Converts a public Invidious API video object into the legacy feed/player model. */
    public static VideoData fromInvidious(JsonObject data) {
        if (data == null) return null;
        VideoData v = new VideoData();
        v.id = getStr(data, "videoId", "");
        if (v.id.length() == 0) return null;

        v.title = getStr(data, "title", v.id);
        v.author = getStr(data, "author", "YouTube");
        v.channelId = getStr(data, "authorId", "");
        v.description = getStr(data, "description", "");
        v.lengthSeconds = getInt(data, "lengthSeconds", 0);
        v.viewCount = getLong(data, "viewCount", 0);
        v.likeCount = getLong(data, "likeCount", 0);
        v.isLive = getBool(data, "liveNow");
        v.category = getStr(data, "genre", "Entertainment");

        if (data.has("keywords") && data.get("keywords").isJsonArray()) {
            JsonArray words = data.getAsJsonArray("keywords");
            for (int i = 0; i < words.size() && i < 15; i++) {
                try { v.tags.add(words.get(i).getAsString()); } catch (Exception ignored) {}
            }
        }

        try {
            if (data.has("published") && data.get("published").isJsonPrimitive()) {
                long publishedAt = data.get("published").getAsLong();
                if (publishedAt > 0L) {
                    java.text.SimpleDateFormat iso = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.US);
                    iso.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                    v.published = iso.format(new java.util.Date(publishedAt * 1000L));
                    java.text.SimpleDateFormat day = new java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.US);
                    day.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                    v.uploadDate = day.format(new java.util.Date(publishedAt * 1000L));
                }
            }
        } catch (Exception ignored) {}

        // Prefer a progressive MP4 format compatible with the 2013 client.
        JsonObject selected = null;
        JsonArray formats = data.has("formatStreams") && data.get("formatStreams").isJsonArray()
            ? data.getAsJsonArray("formatStreams") : null;
        if (formats != null) {
            JsonObject firstMp4 = null;
            for (int i = 0; i < formats.size(); i++) {
                try {
                    JsonObject f = formats.get(i).getAsJsonObject();
                    String url = getStr(f, "url", "");
                    String type = getStr(f, "type", "");
                    int itag = getInt(f, "itag", -1);
                    if (url.length() == 0 || !type.toLowerCase(java.util.Locale.US).startsWith("video/mp4")) continue;
                    if (itag == 18) { selected = f; break; }
                    if (firstMp4 == null) firstMp4 = f;
                } catch (Exception ignored) {}
            }
            if (selected == null) selected = firstMp4;
        }
        if (selected != null) {
            v.streamUrl = getStr(selected, "url", "");
            v.itag = getInt(selected, "itag", 18);
            v.mimeType = getStr(selected, "type", "video/mp4");
            String resolution = getStr(selected, "resolution", "");
            String quality = getStr(selected, "qualityLabel", "");
            int height = parseResolutionHeight(resolution);
            if (height <= 0) height = parseResolutionHeight(quality);
            if (height > 0) v.height = height;
            if (v.height >= 720) v.isHd = true;
        }
        return v;
    }

    private static int parseResolutionHeight(String value) {
        if (value == null) return 0;
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d{3,4})p").matcher(value);
        return m.find() ? safeParseInt(m.group(1), 0) : 0;
    }

    private static int safeParseInt(String value, int fallback) {
        try { return Integer.parseInt(value); } catch (Exception ignored) { return fallback; }
    }

    public static VideoData fromInnerTube(JsonObject resp) {
        VideoData v = new VideoData();
        if (resp == null) return v;

        if (resp.has("videoDetails") && resp.get("videoDetails").isJsonObject()) {
            JsonObject d = resp.getAsJsonObject("videoDetails");
            v.id = getStr(d, "videoId", "");
            v.title = getStr(d, "title", "Unknown");
            v.author = getStr(d, "author", "YouTube");
            v.channelId = getStr(d, "channelId", "");
            v.lengthSeconds = getInt(d, "lengthSeconds", 0);
            v.viewCount = getLong(d, "viewCount", 0);
            v.description = getStr(d, "shortDescription", "");
            v.isLive = getBool(d, "isLiveContent");
            try {
                if (d.has("likeCount") && !d.get("likeCount").isJsonNull()) {
                    v.likeCount = d.get("likeCount").getAsLong();
                }
            } catch (Exception e) {}
            if (d.has("keywords") && d.get("keywords").isJsonArray()) {
                JsonArray kw = d.getAsJsonArray("keywords");
                for (int i = 0; i < kw.size() && i < 15; i++) {
                    v.tags.add(kw.get(i).getAsString());
                }
            }
        }

        if (resp.has("microformat") && resp.get("microformat").isJsonObject()) {
            JsonObject mf = resp.getAsJsonObject("microformat");
            if (mf.has("playerMicroformatRenderer")) {
                JsonObject pm = mf.getAsJsonObject("playerMicroformatRenderer");
                String pd = getStr(pm, "publishDate", "");
                if (pd.length() >= 10) {
                    v.published = pd.substring(0, 10) + "T00:00:00.000Z";
                    v.uploadDate = pd.substring(0, 10).replace("-", "");
                }
                String cat = getStr(pm, "category", "");
                if (!cat.isEmpty()) v.category = cat;
                if (getBool(pm, "isLiveContent")) v.isLive = true;
                if (v.title.isEmpty()) v.title = getStr(pm, "title", "");
                if (v.author.isEmpty()) v.author = getStr(pm, "ownerChannelName", "");
                if (v.channelId.isEmpty()) {
                    v.channelId = getStr(pm, "externalChannelId", "");
                    if (v.channelId.isEmpty()) {
                        String pu = getStr(pm, "ownerProfileUrl", "");
                        int ix = pu.indexOf("/channel/");
                        if (ix >= 0) {
                            String cid = pu.substring(ix + 9);
                            int sl = cid.indexOf('/');
                            if (sl > 0) cid = cid.substring(0, sl);
                            if (!cid.isEmpty()) v.channelId = cid;
                        }
                    }
                }
            }
        }

        if (resp.has("streamingData") && resp.get("streamingData").isJsonObject()) {
            JsonObject sd = resp.getAsJsonObject("streamingData");
            if (sd.has("formats") && sd.get("formats").isJsonArray()) {
                JsonArray formats = sd.getAsJsonArray("formats");
                for (int i = 0; i < formats.size(); i++) {
                    JsonObject f = formats.get(i).getAsJsonObject();
                    int itag = getInt(f, "itag", -1);
                    if (itag == 18) {
                        v.itag = 18;
                        v.streamUrl = getStr(f, "url", "");
                        v.mimeType = getStr(f, "mimeType", "video/mp4");
                        v.width = getInt(f, "width", 480);
                        v.height = getInt(f, "height", 360);
                        break;
                    }
                }
                if (v.streamUrl.isEmpty() && formats.size() > 0) {
                    JsonObject f = formats.get(0).getAsJsonObject();
                    v.itag = getInt(f, "itag", 18);
                    v.streamUrl = getStr(f, "url", "");
                    v.mimeType = getStr(f, "mimeType", "video/mp4");
                    v.width = getInt(f, "width", 480);
                    v.height = getInt(f, "height", 360);
                }
            }
            if (sd.has("adaptiveFormats") && sd.get("adaptiveFormats").isJsonArray()) {
                JsonArray af = sd.getAsJsonArray("adaptiveFormats");
                for (int i = 0; i < af.size(); i++) {
                    JsonObject f = af.get(i).getAsJsonObject();
                    int h = getInt(f, "height", 0);
                    if (h > v.height) v.height = h;
                }
            }
        }

        // adaptive fallback
        if (v.streamUrl == null || v.streamUrl.isEmpty()) {
            try {
                if (resp.has("streamingData") && resp.get("streamingData").isJsonObject()) {
                    JsonObject sd2 = resp.getAsJsonObject("streamingData");
                    if (sd2.has("adaptiveFormats") && sd2.get("adaptiveFormats").isJsonArray()) {
                        JsonArray af2 = sd2.getAsJsonArray("adaptiveFormats");
                        for (int i = 0; i < af2.size(); i++) {
                            JsonObject f2 = af2.get(i).getAsJsonObject();
                            String mt2 = getStr(f2, "mimeType", "");
                            String us2 = getStr(f2, "url", "");
                            if (mt2.startsWith("video/mp4") && !us2.isEmpty()) {
                                v.streamUrl = us2;
                                v.itag = getInt(f2, "itag", v.itag);
                                v.mimeType = mt2;
                                android.util.Log.w("VideoData", "adaptive fallback " + v.id + " itag=" + v.itag);
                                break;
                            }
                        }
                    }
                }
            } catch (Exception ex) {}
        }
        android.util.Log.i("VideoData",
            "PARSED " + v.id + " itag=" + v.itag + " streamUrl_len=" + v.streamUrl.length()
            + " mime=" + v.mimeType + " w=" + v.width + " h=" + v.height);
        if (v.height >= 720) v.isHd = true;
        return v;
    }

    public static String getStr(JsonObject o, String k, String d) {
        try {
            if (o.has(k) && !o.get(k).isJsonNull() && o.get(k).isJsonPrimitive()) {
                return o.get(k).getAsString();
            }
        } catch (Exception e) {}
        return d;
    }
    private static int getInt(JsonObject o, String k, int d) {
        try { if (o.has(k) && !o.get(k).isJsonNull()) return o.get(k).getAsInt(); } catch (Exception e) {}
        return d;
    }
    private static long getLong(JsonObject o, String k, long d) {
        try {
            if (!o.has(k) || o.get(k).isJsonNull()) return d;
            try { return o.get(k).getAsLong(); }
            catch (Exception e) { return Long.parseLong(o.get(k).getAsString()); }
        } catch (Exception e) { return d; }
    }
    private static boolean getBool(JsonObject o, String k) {
        try { if (o.has(k) && !o.get(k).isJsonNull()) return o.get(k).getAsBoolean(); } catch (Exception e) {}
        return false;
    }

    public static VideoData fromRenderer(JsonObject vr) {
        if (vr == null) return null;
        VideoData v = new VideoData();
        v.id = getStr(vr, "videoId", "");
        if (v.id.isEmpty()) return null;

        if (vr.has("title") && vr.get("title").isJsonObject()) {
            v.title = getRunsText(vr.getAsJsonObject("title"));
        }
        String[] bylines = {"longBylineText", "shortBylineText", "ownerText"};
        for (String bk : bylines) {
            if (vr.has(bk) && vr.get(bk).isJsonObject()) {
                JsonObject bl = vr.getAsJsonObject(bk);
                v.author = getRunsText(bl);
                v.channelId = extractChannelId(bl);
                break;
            }
        }
        if (vr.has("lengthText") && vr.get("lengthText").isJsonObject()) {
            v.lengthSeconds = parseDurationToSec(getRunsText(vr.getAsJsonObject("lengthText")));
        }
        try {
            String vc = "";
            if (vr.has("viewCountText") && vr.get("viewCountText").isJsonObject()) {
                vc = getRunsText(vr.getAsJsonObject("viewCountText"));
            } else if (vr.has("shortViewCountText") && vr.get("shortViewCountText").isJsonObject()) {
                vc = getRunsText(vr.getAsJsonObject("shortViewCountText"));
            }
            v.viewCount = parseNumber(vc);
        } catch (Exception e) {}
        if (vr.has("publishedTimeText") && vr.get("publishedTimeText").isJsonObject()) {
            v.published = parseRelativeTime(getRunsText(vr.getAsJsonObject("publishedTimeText")));
            v.uploadDate = v.published.substring(0, 10).replace("-", "");
        }
        try {
            if (vr.has("badges")) {
                JsonArray b = vr.getAsJsonArray("badges");
                for (int i = 0; i < b.size(); i++) {
                    JsonObject bo = b.get(i).getAsJsonObject();
                    if (bo.has("metadataBadgeRenderer")) {
                        JsonObject mr = bo.getAsJsonObject("metadataBadgeRenderer");
                        String lbl = getStr(mr, "label", "").toUpperCase();
                        String style = getStr(mr, "style", "");
                        if (lbl.contains("HD") || lbl.contains("4K")) v.isHd = true;
                        if (style.equals("BADGE_STYLE_TYPE_LIVE_NOW")) v.isLive = true;
                    }
                }
            }
        } catch (Exception e) {}
        return v;
    }

    public static String getRunsText(JsonObject obj) {
        try {
            if (obj.has("simpleText") && !obj.get("simpleText").isJsonNull()) return obj.get("simpleText").getAsString();
            if (obj.has("runs")) {
                JsonArray runs = obj.getAsJsonArray("runs");
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < runs.size(); i++) {
                    JsonObject r = runs.get(i).getAsJsonObject();
                    if (r.has("text") && !r.get("text").isJsonNull()) sb.append(r.get("text").getAsString());
                }
                return sb.toString();
            }
        } catch (Exception e) {}
        return "";
    }

    public static long parseNumber(String s) {
        if (s == null || s.isEmpty()) return 0;
        // Достаём первое число с возможной десятичной точкой и суффиксом
        java.util.regex.Matcher m = java.util.regex.Pattern
            .compile("([0-9.,]+)[ ]*([KkMmBb]?)")
            .matcher(s);
        if (!m.find()) return 0;
        String numStr = m.group(1).replace(",", "");
        String suffix = m.group(2).toUpperCase();
        try {
            double val = Double.parseDouble(numStr);
            if (suffix.equals("K")) val *= 1000;
            else if (suffix.equals("M")) val *= 1000000;
            else if (suffix.equals("B")) val *= 1000000000;
            return (long) val;
        } catch (Exception e) { return 0; }
    }

    public static int parseDurationToSec(String s) {
        if (s == null || s.isEmpty() || s.toUpperCase().contains("LIVE")) return 0;
        try {
            String[] p = s.split(":");
            if (p.length == 3) return Integer.parseInt(p[0])*3600 + Integer.parseInt(p[1])*60 + Integer.parseInt(p[2]);
            if (p.length == 2) return Integer.parseInt(p[0])*60 + Integer.parseInt(p[1]);
            if (p.length == 1) return Integer.parseInt(p[0]);
        } catch (Exception e) {}
        return 0;
    }

    public static String parseRelativeTime(String text) {
        if (text == null) return "2013-01-01T00:00:00.000Z";
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d+)\\s+(second|minute|hour|day|week|month|year)s?\\s+ago").matcher(text.toLowerCase());
        if (!m.find()) return "2013-01-01T00:00:00.000Z";
        int n = Integer.parseInt(m.group(1));
        String u = m.group(2);
        long now = System.currentTimeMillis();
        long off = 0;
        if (u.equals("second")) off = n*1000L;
        else if (u.equals("minute")) off = n*60000L;
        else if (u.equals("hour")) off = n*3600000L;
        else if (u.equals("day")) off = n*86400000L;
        else if (u.equals("week")) off = n*7L*86400000L;
        else if (u.equals("month")) off = n*30L*86400000L;
        else if (u.equals("year")) off = n*365L*86400000L;
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
        return sdf.format(new java.util.Date(now - off));
    }

    public static String extractChannelId(JsonObject byline) {
        try {
            if (!byline.has("runs")) return "";
            JsonArray runs = byline.getAsJsonArray("runs");
            if (runs.size() == 0) return "";
            JsonObject r = runs.get(0).getAsJsonObject();
            if (!r.has("navigationEndpoint")) return "";
            JsonObject nav = r.getAsJsonObject("navigationEndpoint");
            if (!nav.has("browseEndpoint")) return "";
            JsonObject be = nav.getAsJsonObject("browseEndpoint");
            if (!be.has("browseId")) return "";
            return be.get("browseId").getAsString();
        } catch (Exception e) { return ""; }
    }

    public static VideoData fromVideoWithContext(JsonObject vc) {
        if (vc == null) return null;
        VideoData v = new VideoData();
        v.id = getStr(vc, "videoId", "");
        if (v.id.isEmpty()) return null;

        // headline.runs[].text
        if (vc.has("headline") && vc.get("headline").isJsonObject()) {
            v.title = getRunsText(vc.getAsJsonObject("headline"));
        }

        // shortBylineText.runs
        if (vc.has("shortBylineText") && vc.get("shortBylineText").isJsonObject()) {
            JsonObject bl = vc.getAsJsonObject("shortBylineText");
            v.author = getRunsText(bl);
            v.channelId = extractChannelId(bl);
        }

        // lengthText
        if (vc.has("lengthText") && vc.get("lengthText").isJsonObject()) {
            v.lengthSeconds = parseDurationToSec(getRunsText(vc.getAsJsonObject("lengthText")));
        }

        // shortViewCountText / viewCountText
        try {
            String vc2 = "";
            if (vc.has("shortViewCountText") && vc.get("shortViewCountText").isJsonObject()) {
                vc2 = getRunsText(vc.getAsJsonObject("shortViewCountText"));
            } else if (vc.has("viewCountText") && vc.get("viewCountText").isJsonObject()) {
                vc2 = getRunsText(vc.getAsJsonObject("viewCountText"));
            }
            v.viewCount = parseNumber(vc2);
        } catch (Exception e) {}

        // publishedTimeText
        if (vc.has("publishedTimeText") && vc.get("publishedTimeText").isJsonObject()) {
            v.published = parseRelativeTime(getRunsText(vc.getAsJsonObject("publishedTimeText")));
            v.uploadDate = v.published.substring(0, 10).replace("-", "");
        }
        return v;
    }

    /**
     * Парсит likeCount из InnerTube /next response.
     * Path: contents.singleColumnWatchNextResults.results.watchNextTabbedResultsRenderer
     *       .tabs[0].tabRenderer.content.sectionListRenderer.contents[0]
     *       .itemSectionRenderer.contents[0].videoMetadataRenderer.likeButton
     *       .likeButtonRenderer.likeCount
     */
    public static void enrichFromNext(JsonObject nextResp, VideoData v) {
        if (nextResp == null || v == null) return;
        JsonObject primary = findFirstObj(nextResp, "videoPrimaryInfoRenderer");
        if (primary != null) {
            if (v.title.isEmpty() && primary.has("title") && primary.get("title").isJsonObject()) {
                v.title = getRunsText(primary.getAsJsonObject("title"));
            }
            if (primary.has("viewCount")) {
                try {
                    JsonObject vc = primary.getAsJsonObject("viewCount");
                    if (vc.has("videoViewCountRenderer")) {
                        JsonObject vr = vc.getAsJsonObject("videoViewCountRenderer");
                        if (vr.has("viewCount")) {
                            long parsed = parseNumber(getRunsText(vr.getAsJsonObject("viewCount")));
                            if (parsed > 0) v.viewCount = parsed;
                        }
                    }
                } catch (Exception e) {}
            }
            if (primary.has("dateText") && primary.get("dateText").isJsonObject()) {
                String dt = getRunsText(primary.getAsJsonObject("dateText"));
                if (!dt.isEmpty()) {
                    v.published = parseRelativeTime(dt);
                    if (v.published.length() >= 10)
                        v.uploadDate = v.published.substring(0,10).replace("-", "");
                }
            }
        }
        JsonObject secondary = findFirstObj(nextResp, "videoSecondaryInfoRenderer");
        if (secondary != null && secondary.has("owner") && secondary.get("owner").isJsonObject()) {
            JsonObject owner = secondary.getAsJsonObject("owner");
            if (owner.has("videoOwnerRenderer")) {
                JsonObject vor = owner.getAsJsonObject("videoOwnerRenderer");
                if (v.author.isEmpty() && vor.has("title") && vor.get("title").isJsonObject()) {
                    v.author = getRunsText(vor.getAsJsonObject("title"));
                }
                if (v.channelId.isEmpty() && vor.has("navigationEndpoint")) {
                    JsonObject nav = vor.getAsJsonObject("navigationEndpoint");
                    if (nav.has("browseEndpoint")) {
                        String bid = getStr(nav.getAsJsonObject("browseEndpoint"), "browseId", "");
                        if (bid.startsWith("UC")) v.channelId = bid;
                    }
                }
            }
        }
        if (v.channelId.isEmpty() || v.author.isEmpty() || v.title.isEmpty()) {
            JsonObject vd = findFirstObj(nextResp, "videoDetails");
            if (vd != null) {
                if (v.title.isEmpty()) v.title = getStr(vd, "title", "");
                if (v.author.isEmpty()) v.author = getStr(vd, "author", "");
                if (v.channelId.isEmpty()) v.channelId = getStr(vd, "channelId", "");
                if (v.viewCount == 0) v.viewCount = getLong(vd, "viewCount", 0);
            }
        }
        if (v.title.isEmpty()) v.title = v.id;
        if (v.author.isEmpty()) v.author = "YouTube";
    }

    private static JsonObject findFirstObj(com.google.gson.JsonElement el, String key) {
        if (el == null) return null;
        if (el.isJsonObject()) {
            JsonObject o = el.getAsJsonObject();
            if (o.has(key) && o.get(key).isJsonObject()) return o.getAsJsonObject(key);
            for (java.util.Map.Entry<String, com.google.gson.JsonElement> e : o.entrySet()) {
                JsonObject r = findFirstObj(e.getValue(), key);
                if (r != null) return r;
            }
        } else if (el.isJsonArray()) {
            for (com.google.gson.JsonElement e : el.getAsJsonArray()) {
                JsonObject r = findFirstObj(e, key);
                if (r != null) return r;
            }
        }
        return null;
    }

    public static long parseLikeCountFromNext(JsonObject resp) {
        if (resp == null) return -1;
        try {
            JsonObject contents = resp.getAsJsonObject("contents");
            if (contents == null) return -1;
            JsonObject sc = contents.getAsJsonObject("singleColumnWatchNextResults");
            if (sc == null) return -1;
            JsonObject results = sc.getAsJsonObject("results");
            if (results == null) return -1;
            JsonObject wn = results.getAsJsonObject("watchNextTabbedResultsRenderer");
            if (wn == null) return -1;
            JsonArray tabs = wn.getAsJsonArray("tabs");
            if (tabs == null || tabs.size() == 0) return -1;

            for (int t = 0; t < tabs.size(); t++) {
                JsonObject tab = tabs.get(t).getAsJsonObject();
                if (!tab.has("tabRenderer")) continue;
                JsonObject tr = tab.getAsJsonObject("tabRenderer");
                if (!tr.has("content")) continue;
                JsonObject content = tr.getAsJsonObject("content");
                if (!content.has("sectionListRenderer")) continue;
                JsonObject sl = content.getAsJsonObject("sectionListRenderer");
                if (!sl.has("contents")) continue;
                JsonArray sections = sl.getAsJsonArray("contents");
                for (int s = 0; s < sections.size(); s++) {
                    JsonObject section = sections.get(s).getAsJsonObject();
                    if (!section.has("itemSectionRenderer")) continue;
                    JsonObject isr = section.getAsJsonObject("itemSectionRenderer");
                    if (!isr.has("contents")) continue;
                    JsonArray items = isr.getAsJsonArray("contents");
                    for (int i = 0; i < items.size(); i++) {
                        JsonObject item = items.get(i).getAsJsonObject();
                        if (!item.has("videoMetadataRenderer")) continue;
                        JsonObject vm = item.getAsJsonObject("videoMetadataRenderer");
                        if (!vm.has("likeButton")) continue;
                        JsonObject lb = vm.getAsJsonObject("likeButton");
                        if (!lb.has("likeButtonRenderer")) continue;
                        JsonObject lbr = lb.getAsJsonObject("likeButtonRenderer");
                        if (lbr.has("likeCount") && !lbr.get("likeCount").isJsonNull()) {
                            return lbr.get("likeCount").getAsLong();
                        }
                    }
                }
            }
        } catch (Exception e) {
            // log потом
        }
        return -1;
    }
}
