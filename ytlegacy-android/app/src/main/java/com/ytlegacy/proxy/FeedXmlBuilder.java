package com.ytlegacy.proxy;

import android.util.Log;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FeedXmlBuilder {

    private static final String TAG = "FeedXmlBuilder";
    private static final String BASE = "http://127.0.0.1:8080";

    private static final String[] RENDERER_KEYS = {
        "videoRenderer", "compactVideoRenderer", "gridVideoRenderer",
        "tileRenderer", "playlistVideoRenderer", "reelItemRenderer"
    };

    public static String buildSearchFeed(JsonObject resp, String query) {
        StringBuilder entries = new StringBuilder();
        int count = 0;

        try {
            JsonArray videos = extractVideoRenderers(resp);
            Log.i(TAG, "Extracted " + videos.size() + " video renderers for '" + query + "'");
            for (int i = 0; i < videos.size() && count < 20; i++) {
                JsonObject vr = videos.get(i).getAsJsonObject();
                String entry = renderVideoEntry(vr);
                if (entry != null) {
                    entries.append(entry);
                    count++;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Parse error: " + e.getMessage());
        }

        Log.i(TAG, "Built feed with " + count + " entries");
        String title = esc("Search results for " + query);
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<feed xmlns=\"http://www.w3.org/2005/Atom\" " +
            "xmlns:media=\"http://search.yahoo.com/mrss/\" " +
            "xmlns:yt=\"http://gdata.youtube.com/schemas/2007\" " +
            "xmlns:openSearch=\"http://a9.com/-/spec/opensearch/1.1/\" " +
            "xmlns:gd=\"http://schemas.google.com/g/2005\">\n" +
            "  <id>tag:youtube.com,2008:feed:search:" + esc(query) + "</id>\n" +
            "  <title>" + title + "</title>\n" +
            "  <openSearch:totalResults>" + count + "</openSearch:totalResults>\n" +
            "  <openSearch:startIndex>1</openSearch:startIndex>\n" +
            "  <openSearch:itemsPerPage>" + count + "</openSearch:itemsPerPage>\n" +
            entries.toString() +
            "</feed>";
    }

    public static String buildChannelStub(String channelId) {
        String t = esc(channelId);
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<entry xmlns=\"http://www.w3.org/2005/Atom\" " +
            "xmlns:media=\"http://search.yahoo.com/mrss/\" " +
            "xmlns:yt=\"http://gdata.youtube.com/schemas/2007\" xmlns:gd=\"http://schemas.google.com/g/2005\">\n" +
            "  <id>tag:youtube.com,2008:user:" + t + "</id>\n" +
            "  <title>" + t + "</title>\n" +
            "  <author><name>" + t + "</name></author>\n" +
            "  <yt:username>" + t + "</yt:username>\n" +
            "  <yt:channelId>" + t + "</yt:channelId>\n" +
            "  <media:thumbnail url=\"" + BASE + "/vi/dQw4w9WgXcQ/0.jpg\" height=\"90\" width=\"90\"/>\n" +
            "  <yt:statistics subscriberCount=\"0\" videoWatchCount=\"0\"/>\n" +
            "</entry>";
    }

    private static JsonArray extractVideoRenderers(JsonObject resp) {
        JsonArray result = new JsonArray();
        walkJson(resp, result, 0);
        return result;
    }

    private static void walkJson(JsonElement elem, JsonArray out, int depth) {
        if (depth > 20 || elem == null) return;

        if (elem.isJsonObject()) {
            JsonObject obj = elem.getAsJsonObject();
            for (String key : RENDERER_KEYS) {
                JsonElement r = obj.get(key);
                if (r != null && r.isJsonObject()) out.add(r);
            }
            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                walkJson(entry.getValue(), out, depth + 1);
            }
        } else if (elem.isJsonArray()) {
            JsonArray arr = elem.getAsJsonArray();
            for (int i = 0; i < arr.size(); i++) walkJson(arr.get(i), out, depth + 1);
        }
    }

    private static String renderVideoEntry(JsonObject vr) {
        try {
            String videoId = getStr(vr, "videoId");
            if (videoId.isEmpty()) return null;

            String title = vr.has("title") && vr.get("title").isJsonObject()
                ? getRunsText(vr.getAsJsonObject("title")) : "";

            String author = "YouTube";
            String channelId = "";
            String[] bylineKeys = {"longBylineText", "shortBylineText", "ownerText"};
            for (String bk : bylineKeys) {
                if (vr.has(bk) && vr.get(bk).isJsonObject()) {
                    JsonObject bl = vr.getAsJsonObject(bk);
                    author = getRunsText(bl);
                    channelId = extractChannelIdFromByline(bl);
                    break;
                }
            }

            String duration = "0";
            if (vr.has("lengthText") && vr.get("lengthText").isJsonObject()) {
                duration = parseDuration(getRunsText(vr.getAsJsonObject("lengthText")));
            } else if (vr.has("thumbnailOverlays")) {
                try {
                    JsonArray ov = vr.getAsJsonArray("thumbnailOverlays");
                    for (int i = 0; i < ov.size(); i++) {
                        JsonObject o = ov.get(i).getAsJsonObject();
                        if (o.has("thumbnailOverlayTimeStatusRenderer")) {
                            JsonObject t = o.getAsJsonObject("thumbnailOverlayTimeStatusRenderer");
                            if (t.has("text") && t.get("text").isJsonObject()) {
                                duration = parseDuration(getRunsText(t.getAsJsonObject("text")));
                            }
                        }
                    }
                } catch (Exception e) {}
            }

            long views = 0;
            try {
                String v = "";
                if (vr.has("viewCountText") && vr.get("viewCountText").isJsonObject()) {
                    v = getRunsText(vr.getAsJsonObject("viewCountText"));
                } else if (vr.has("shortViewCountText") && vr.get("shortViewCountText").isJsonObject()) {
                    v = getRunsText(vr.getAsJsonObject("shortViewCountText"));
                }
                views = parseNumber(v);
            } catch (Exception e) {}

            String published = "2013-01-01T00:00:00.000Z";
            if (vr.has("publishedTimeText") && vr.get("publishedTimeText").isJsonObject()) {
                String relTime = getRunsText(vr.getAsJsonObject("publishedTimeText"));
                published = parseRelativeTime(relTime);
            }

            String uploaded = published.substring(0, 10).replace("-", "");

            // HD / 4K badges
            boolean isHd = false;
            try {
                if (vr.has("badges")) {
                    JsonArray badges = vr.getAsJsonArray("badges");
                    for (int i = 0; i < badges.size(); i++) {
                        JsonObject b = badges.get(i).getAsJsonObject();
                        if (b.has("metadataBadgeRenderer")) {
                            JsonObject m = b.getAsJsonObject("metadataBadgeRenderer");
                            String label = getStr(m, "label").toUpperCase();
                            if (label.contains("HD") || label.contains("4K") || label.contains("8K")) {
                                isHd = true;
                            }
                        }
                    }
                }
            } catch (Exception e) {}

            boolean isLive = false;
            try {
                if (vr.has("isLiveNow") && !vr.get("isLiveNow").isJsonNull()) {
                    isLive = vr.get("isLiveNow").getAsBoolean();
                }
                if (vr.has("badges")) {
                    JsonArray badges = vr.getAsJsonArray("badges");
                    for (int i = 0; i < badges.size(); i++) {
                        JsonObject b = badges.get(i).getAsJsonObject();
                        if (b.has("metadataBadgeRenderer")) {
                            String style = getStr(b.getAsJsonObject("metadataBadgeRenderer"), "style");
                            if ("BADGE_STYLE_TYPE_LIVE_NOW".equals(style)) isLive = true;
                        }
                    }
                }
            } catch (Exception e) {}

            // Description
            String description = "";
            try {
                if (vr.has("detailedMetadataSnippets")) {
                    JsonArray snips = vr.getAsJsonArray("detailedMetadataSnippets");
                    if (snips.size() > 0) {
                        JsonObject s = snips.get(0).getAsJsonObject();
                        if (s.has("snippetText") && s.get("snippetText").isJsonObject()) {
                            description = getRunsText(s.getAsJsonObject("snippetText"));
                        }
                    }
                }
            } catch (Exception e) {}

            // Verified checkmark
            boolean isVerified = false;
            try {
                if (vr.has("ownerBadges")) {
                    JsonArray ob = vr.getAsJsonArray("ownerBadges");
                    for (int i = 0; i < ob.size(); i++) {
                        JsonObject b = ob.get(i).getAsJsonObject();
                        if (b.has("metadataBadgeRenderer")) {
                            JsonObject m = b.getAsJsonObject("metadataBadgeRenderer");
                            String icon = getStr(m, "iconType");
                            if (icon.contains("CHECK") || icon.contains("VERIFIED")) {
                                isVerified = true;
                            }
                        }
                    }
                }
            } catch (Exception e) {}

            String titleX = esc(title);
            String authorX = esc(author + (isVerified ? " \u2713" : ""));
            String channelX = esc(channelId.isEmpty() ? author : channelId);
            String descX = esc(description);

            String hdTag = isHd ? "<yt:hd/>" : "";
            String liveTag = isLive ? "<yt:live/>" : "";

            Log.d(TAG, "  entry: " + videoId + " | " + title + " | hd=" + isHd + " live=" + isLive
                    + " views=" + views + " dur=" + duration + " pub=" + published.substring(0, 10));

            return "  <entry>\n" +
                "    <id>tag:youtube.com,2008:video:" + videoId + "</id>\n" +
                "    <published>" + published + "</published>\n" +
                "    <updated>" + published + "</updated>\n" +
                "    <title>" + titleX + "</title>\n" +
                "    <summary>" + descX + "</summary>\n" +
                "    <link rel=\"self\" type=\"application/atom+xml\" href=\"" + BASE + "/feeds/api/videos/" + videoId + "?v=2\"/>\n" +
            "    <link rel=\"alternate\" type=\"text/html\" href=\"" + BASE + "/watch?v=" + videoId + "\"/>\n" +
                "    <link rel=\"http://gdata.youtube.com/schemas/2007#video.related\" type=\"application/atom+xml\" href=\"" + BASE + "/feeds/api/videos/" + videoId + "/related\"/>\n" +
                "    <link rel=\"http://gdata.youtube.com/schemas/2007#video.ratings\" type=\"application/atom+xml\" href=\"" + BASE + "/feeds/api/videos/" + videoId + "/ratings\"/>\n" +
                "    <author><name>" + authorX + "</name><uri>" + BASE + "/feeds/api/users/" + channelX + "</uri></author>\n" +
                "    <yt:accessControl action=\"comment\" permission=\"allowed\"/>\n" +
                "    <yt:accessControl action=\"embed\" permission=\"allowed\"/>\n" +
                "    <yt:accessControl action=\"rate\" permission=\"allowed\"/>\n" +
                "    <yt:accessControl action=\"list\" permission=\"allowed\"/>\n" +
                "    <yt:availability status=\"public\"/>\n" +
                "    <yt:statistics favoriteCount=\"0\" viewCount=\"" + views + "\"/>\n" +
                "    <yt:rating numLikes=\"0\" numDislikes=\"0\"/>\n" +
                "    <gd:rating average=\"5.0\" max=\"5\" min=\"1\" numRaters=\"0\"/>\n" +
                "    <gd:comments><gd:feedLink rel=\"http://gdata.youtube.com/schemas/2007#comments\" href=\"" + BASE + "/feeds/api/videos/" + videoId + "/comments\"/></gd:comments>\n" +
                hdTag + liveTag + "\n" +
                "    <media:group>\n" +
                "      <media:title type=\"plain\">" + titleX + "</media:title>\n" +
                "      <media:description type=\"plain\">" + descX + "</media:description>\n" +
                "      <media:keywords>video</media:keywords>\n" +
                "      <media:category label=\"Music\" scheme=\"http://gdata.youtube.com/schemas/2007/categories.cat\">Music</media:category>\n" +
                "      <media:content url=\"" + BASE + "/video/" + videoId + ".mp4\" type=\"video/mp4\" medium=\"video\" isDefault=\"true\" expression=\"full\" duration=\"" + duration + "\" yt:format=\"18\" itag=\"18\"/>\n" +
                "      <media:thumbnail url=\"" + BASE + "/vi/" + videoId + "/3.jpg\" height=\"720\" width=\"1280\" time=\"00:00:00.000\" yt:name=\"maxresdefault\"/>\n" +
                "      <media:thumbnail url=\"" + BASE + "/vi/" + videoId + "/0.jpg\" height=\"360\" width=\"480\" time=\"00:00:00.000\" yt:name=\"hqdefault\"/>\n" +
                "      <media:thumbnail url=\"" + BASE + "/vi/" + videoId + "/2.jpg\" height=\"180\" width=\"320\" time=\"00:00:00.000\" yt:name=\"mqdefault\"/>\n" +
                "      <media:thumbnail url=\"" + BASE + "/vi/" + videoId + "/1.jpg\" height=\"90\" width=\"120\" time=\"00:00:00.000\" yt:name=\"default\"/>\n" +
                "      <media:player url=\"" + BASE + "/watch?v=" + videoId + "\"/>\n" +
                "      <media:credit role=\"uploader\" scheme=\"urn:youtube\" yt:displayName=\"" + authorX + "\">" + authorX + "</media:credit>\n" +
                "      <media:rating scheme=\"urn:simple\">nonadult</media:rating>\n" +
                "      <media:restriction relationship=\"allow\" type=\"country\">RU US DE GB</media:restriction>\n" +
                "      <media:community>\n" +
                "        <media:starRating average=\"5.0\" count=\"0\" min=\"1\" max=\"5\"/>\n" +
                "        <media:statistics views=\"" + views + "\" favorites=\"0\"/>\n" +
                "      </media:community>\n" +
                "      <yt:synopsis>" + descX + "</yt:synopsis>\n" +
                "      <yt:duration seconds=\"" + duration + "\"/>\n" +
                "      <yt:uploaded>" + uploaded + "</yt:uploaded>\n" +
                "      <yt:videoid>" + videoId + "</yt:videoid>\n" +
                "    </media:group>\n" +
                "  </entry>\n";
        } catch (Exception e) {
            Log.e(TAG, "renderEntry error: " + e.getMessage());
            return null;
        }
    }

    private static String extractChannelIdFromByline(JsonObject byline) {
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
        } catch (Exception e) {
            return "";
        }
    }

    /** "1,234,567" → 1234567, "1.2M" → 1200000, "500K" → 500000 */
    private static long parseNumber(String s) {
        if (s == null || s.isEmpty()) return 0;
        s = s.toLowerCase().replaceAll("[^0-9.km]", "");
        try {
            double mult = 1;
            if (s.endsWith("k")) { mult = 1000; s = s.substring(0, s.length() - 1); }
            else if (s.endsWith("m")) { mult = 1000000; s = s.substring(0, s.length() - 1); }
            else if (s.endsWith("b")) { mult = 1000000000; s = s.substring(0, s.length() - 1); }
            return (long)(Double.parseDouble(s) * mult);
        } catch (Exception e) {
            return 0;
        }
    }

    /** "3:45" → 225, "1:02:03" → 3723, "LIVE" → 0 */
    private static String parseDuration(String s) {
        if (s == null || s.isEmpty() || s.toUpperCase().contains("LIVE")) return "0";
        try {
            String[] parts = s.split(":");
            int seconds = 0;
            if (parts.length == 3) {
                seconds = Integer.parseInt(parts[0]) * 3600
                        + Integer.parseInt(parts[1]) * 60
                        + Integer.parseInt(parts[2]);
            } else if (parts.length == 2) {
                seconds = Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
            } else if (parts.length == 1) {
                seconds = Integer.parseInt(parts[0]);
            }
            return String.valueOf(seconds);
        } catch (Exception e) {
            return "0";
        }
    }

    /** "3 years ago" → 2022-01-01T00:00:00.000Z */
    private static String parseRelativeTime(String text) {
        if (text == null) return "2013-01-01T00:00:00.000Z";
        Matcher m = Pattern.compile("(\\d+)\\s+(second|minute|hour|day|week|month|year)s?\\s+ago")
                .matcher(text.toLowerCase());
        if (!m.find()) return "2013-01-01T00:00:00.000Z";

        int n = Integer.parseInt(m.group(1));
        String unit = m.group(2);
        long now = System.currentTimeMillis();
        long offset;
        switch (unit) {
            case "second": offset = n * 1000L; break;
            case "minute": offset = n * 60L * 1000; break;
            case "hour":   offset = n * 3600L * 1000; break;
            case "day":    offset = n * 86400L * 1000; break;
            case "week":   offset = n * 7L * 86400 * 1000; break;
            case "month":  offset = n * 30L * 86400 * 1000; break;
            case "year":   offset = n * 365L * 86400 * 1000; break;
            default:       offset = 0;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date(now - offset));
    }

    private static String getStr(JsonObject o, String k) {
        try {
            if (o.has(k) && !o.get(k).isJsonNull() && o.get(k).isJsonPrimitive()) {
                return o.get(k).getAsString();
            }
        } catch (Exception e) {}
        return "";
    }

    private static String getRunsText(JsonObject obj) {
        try {
            if (obj.has("simpleText") && !obj.get("simpleText").isJsonNull()) {
                return obj.get("simpleText").getAsString();
            }
            if (obj.has("runs")) {
                JsonArray runs = obj.getAsJsonArray("runs");
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < runs.size(); i++) {
                    JsonObject r = runs.get(i).getAsJsonObject();
                    if (r.has("text") && !r.get("text").isJsonNull()) {
                        sb.append(r.get("text").getAsString());
                    }
                }
                return sb.toString();
            }
        } catch (Exception e) {}
        return "";
    }

    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&apos;");
    }
}
