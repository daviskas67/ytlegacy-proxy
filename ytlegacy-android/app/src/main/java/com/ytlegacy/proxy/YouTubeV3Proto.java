package com.ytlegacy.proxy;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Minimal protobuf wire encoder for the subset of YouTube Data API v3 used
 * by the legacy 5.0.21 client. The client requests alt=proto and expects
 * repeated response items at field 1007.
 */
public final class YouTubeV3Proto {
    private YouTubeV3Proto() {}

    public static byte[] activities(JsonArray videos, int maxResults) {
        Proto out = new Proto();
        if (videos == null) return out.bytes();
        int count = 0;
        for (int i = 0; i < videos.size() && count < maxResults; i++) {
            try {
                JsonObject video = videos.get(i).getAsJsonObject();
                String id = str(video, "videoId");
                if (!validVideoId(id)) continue;
                String channelId = str(video, "authorId");
                Proto snippet = new Proto();
                if (!channelId.isEmpty()) snippet.string(2, channelId);
                // Activity type 1 is an upload; the client filters non-video activity types.
                snippet.varint(3, 1);

                // The supplied YouTube 5.0.21 client's generated micro-protobuf
                // parser reads contentDetails.upload and upload.videoId from field
                // 10101 at both levels. Field 1 is silently skipped by that parser,
                // leaving the upload object null and crashing the feed adapter.
                Proto upload = new Proto();
                upload.string(10101, id);
                Proto details = new Proto();
                details.message(10101, upload.bytes());

                Proto activity = new Proto();
                activity.message(2, snippet.bytes());
                activity.message(3, details.bytes());
                activity.string(101, id);
                activity.string(200, "youtube#activity");
                activity.string(201, "");
                out.message(1007, activity.bytes());
                count++;
            } catch (Exception ignored) {}
        }
        return out.bytes();
    }

    public static byte[] videos(List<JsonObject> source) {
        Proto out = new Proto();
        if (source == null) return out.bytes();
        for (JsonObject data : source) {
            if (data == null) continue;
            String id = str(data, "videoId");
            if (!validVideoId(id)) continue;
            Proto snippet = new Proto();
            // These field numbers match the generated VideoSnippet parser in
            // the bundled YouTube 5.0.21 client: publishedAt=1, title=4,
            // description=5, channelTitle=7.
            snippet.string(1, str(data, "published", ""));
            snippet.string(4, str(data, "title"));
            snippet.string(5, str(data, "description"));
            snippet.string(7, str(data, "author"));

            Proto content = new Proto();
            int duration = integer(data, "lengthSeconds");
            if (duration > 0) content.string(1, "PT" + duration + "S");

            // Video resource field 4 is status (bp); field 5 is statistics
            // (bo). The feed adapter unconditionally reads statistics.viewCount
            // through Video.e().a(), so omitting field 5 leaves a null object.
            Proto status = new Proto();
            Proto stats = new Proto();
            long views = number(data, "viewCount");
            long likes = number(data, "likeCount");
            if (views > 0) stats.varint(1, views);
            if (likes > 0) stats.varint(2, likes);

            Proto item = new Proto();
            item.message(2, snippet.bytes());
            item.message(3, content.bytes());
            item.message(4, status.bytes());
            item.message(5, stats.bytes());
            item.string(101, id);
            item.string(200, "youtube#video");
            item.string(201, "");
            out.message(1007, item.bytes());
        }
        return out.bytes();
    }

    public static byte[] channels(List<JsonObject> source) {
        Proto out = new Proto();
        if (source == null) return out.bytes();
        for (JsonObject data : source) {
            if (data == null) continue;
            String id = str(data, "authorId");
            if (id.isEmpty()) continue;
            Proto snippet = new Proto();
            snippet.string(3, str(data, "author"));
            snippet.string(4, str(data, "description"));

            Proto stats = new Proto();
            long subs = number(data, "subCount");
            long views = number(data, "totalViews");
            long videos = number(data, "videoCount");
            if (subs > 0) stats.string(1, String.valueOf(subs));
            if (views > 0) stats.string(2, String.valueOf(views));
            if (videos > 0) stats.string(3, String.valueOf(videos));

            Proto item = new Proto();
            item.message(2, snippet.bytes());
            item.message(4, stats.bytes());
            item.string(101, id);
            item.string(200, "youtube#channel");
            item.string(201, "");
            out.message(1007, item.bytes());
        }
        return out.bytes();
    }

    public static List<String> parseIds(String raw, int limit) {
        List<String> ids = new ArrayList<String>();
        if (raw == null || raw.trim().isEmpty()) return ids;
        String[] parts = raw.split(",");
        for (String part : parts) {
            String id = part.trim();
            if (id.length() > 0 && !ids.contains(id)) {
                ids.add(id);
                if (ids.size() >= limit) break;
            }
        }
        return ids;
    }

    private static boolean validVideoId(String id) {
        return id != null && id.matches("[A-Za-z0-9_-]{11}");
    }

    private static String str(JsonObject obj, String key) {
        try {
            if (obj != null && obj.has(key) && !obj.get(key).isJsonNull())
                return obj.get(key).getAsString();
        } catch (Exception ignored) {}
        return "";
    }

    private static int integer(JsonObject obj, String key) {
        try { return obj.get(key).getAsInt(); } catch (Exception ignored) { return 0; }
    }

    private static long number(JsonObject obj, String key) {
        try { return obj.get(key).getAsLong(); } catch (Exception ignored) { return 0L; }
    }

    private static final class Proto {
        private final ByteArrayOutputStream out = new ByteArrayOutputStream();

        void string(int field, String value) {
            if (value == null) value = "";
            byte[] bytes;
            try { bytes = value.getBytes("UTF-8"); }
            catch (Exception e) { bytes = new byte[0]; }
            tag(field, 2);
            varintRaw(bytes.length);
            out.write(bytes, 0, bytes.length);
        }

        void message(int field, byte[] value) {
            if (value == null) value = new byte[0];
            tag(field, 2);
            varintRaw(value.length);
            out.write(value, 0, value.length);
        }

        void varint(int field, long value) {
            tag(field, 0);
            varintRaw(value);
        }

        private void tag(int field, int wireType) {
            varintRaw(((long) field << 3) | wireType);
        }

        private void varintRaw(long value) {
            while ((value & ~0x7FL) != 0L) {
                out.write((int) ((value & 0x7F) | 0x80));
                value >>>= 7;
            }
            out.write((int) value);
        }

        byte[] bytes() { return out.toByteArray(); }
    }
}
