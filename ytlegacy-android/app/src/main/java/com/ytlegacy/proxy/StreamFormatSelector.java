package com.ytlegacy.proxy;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Selects one self-contained H.264/AAC MP4 stream for the legacy player.
 *
 * The old YouTube client expects a single progressive file. Separate
 * adaptive video/audio tracks are deliberately not returned because this
 * proxy does not mux them.
 */
public final class StreamFormatSelector {

    public static final int DEFAULT_HEIGHT = 360;
    public static final int MAX_HEIGHT = 720;

    private StreamFormatSelector() {}

    public static JsonObject selectProgressiveMp4(JsonArray formats, String quality) {
        return select(formats, quality, true);
    }

    /**
     * Adaptive formats are accepted only when the MIME codec list explicitly
     * includes both H.264 video and AAC audio in the same file.
     */
    public static JsonObject selectMuxedAdaptiveMp4(JsonArray formats, String quality) {
        return select(formats, quality, false);
    }

    public static int targetHeight(String quality) {
        if (quality == null || quality.trim().length() == 0) return DEFAULT_HEIGHT;
        String normalized = quality.trim().toLowerCase(java.util.Locale.US);
        if ("auto".equals(normalized) || "best".equals(normalized)) return MAX_HEIGHT;
        if (normalized.endsWith("p")) normalized = normalized.substring(0, normalized.length() - 1);
        try {
            int height = Integer.parseInt(normalized);
            if (height < 144) return DEFAULT_HEIGHT;
            return Math.min(MAX_HEIGHT, height);
        } catch (Exception ignored) {
            return DEFAULT_HEIGHT;
        }
    }

    public static boolean isMuxedLegacyMp4(JsonObject format, boolean allowKnownProgressiveItag) {
        if (format == null) return false;
        String url = getString(format, "url", "");
        if (url.length() == 0) return false;

        String mime = getString(format, "mimeType",
            getString(format, "type", ""));
        if (!mime.toLowerCase(java.util.Locale.US).startsWith("video/mp4")) return false;

        String codecs = extractCodecs(mime);
        if (codecs.length() > 0) {
            String lower = codecs.toLowerCase(java.util.Locale.US);
            // Keep the legacy device on the broadly supported H.264 + AAC path.
            return containsCodec(lower, "avc1", "avc3")
                && containsCodec(lower, "mp4a");
        }

        // Invidious formatStreams and InnerTube formats are progressive/muxed
        // by contract. If a server omits its codec list, allow only the two
        // well-known legacy MP4 formats. Never apply this to adaptiveFormats.
        if (!allowKnownProgressiveItag) return false;
        int itag = getInt(format, "itag", -1);
        return itag == 18 || itag == 59 || itag == 22;
    }

    private static JsonObject select(JsonArray formats, String quality,
                                     boolean allowKnownProgressiveItag) {
        if (formats == null || formats.size() == 0) return null;

        int target = targetHeight(quality);
        JsonObject bestBelow = null;
        JsonObject bestAbove = null;
        int bestBelowHeight = -1;
        int bestAboveHeight = Integer.MAX_VALUE;
        long bestBelowBitrate = -1L;
        long bestAboveBitrate = -1L;

        for (int i = 0; i < formats.size(); i++) {
            try {
                if (!formats.get(i).isJsonObject()) continue;
                JsonObject candidate = formats.get(i).getAsJsonObject();
                if (!isMuxedLegacyMp4(candidate, allowKnownProgressiveItag)) continue;

                int height = getHeight(candidate);
                if (height < 144 || height > MAX_HEIGHT) continue;
                long bitrate = getLong(candidate, "bitrate",
                    getLong(candidate, "averageBitrate", 0L));

                if (height <= target) {
                    if (height > bestBelowHeight
                            || (height == bestBelowHeight && bitrate > bestBelowBitrate)) {
                        bestBelow = candidate;
                        bestBelowHeight = height;
                        bestBelowBitrate = bitrate;
                    }
                } else if (height < bestAboveHeight
                        || (height == bestAboveHeight && bitrate > bestAboveBitrate)) {
                    bestAbove = candidate;
                    bestAboveHeight = height;
                    bestAboveBitrate = bitrate;
                }
            } catch (Exception ignored) {}
        }

        // Prefer the highest quality not exceeding the requested limit. If the
        // provider has no lower rendition, use its lowest compatible rendition.
        return bestBelow != null ? bestBelow : bestAbove;
    }

    public static int getHeight(JsonObject format) {
        int direct = getInt(format, "height", 0);
        if (direct > 0) return direct;
        String label = getString(format, "qualityLabel", "");
        if (label.length() == 0) label = getString(format, "resolution", "");
        java.util.regex.Matcher matcher = java.util.regex.Pattern
            .compile("(\\d{3,4})p?", java.util.regex.Pattern.CASE_INSENSITIVE)
            .matcher(label);
        if (matcher.find()) {
            try { return Integer.parseInt(matcher.group(1)); }
            catch (Exception ignored) {}
        }
        int itag = getInt(format, "itag", -1);
        if (itag == 18) return 360;
        if (itag == 59) return 480;
        if (itag == 22) return 720;
        return 0;
    }

    private static String extractCodecs(String mime) {
        if (mime == null) return "";
        java.util.regex.Matcher matcher = java.util.regex.Pattern
            .compile("codecs\\s*=\\s*[\"']([^\"']+)[\"']",
                java.util.regex.Pattern.CASE_INSENSITIVE)
            .matcher(mime);
        return matcher.find() ? matcher.group(1) : "";
    }

    private static boolean containsCodec(String codecs, String... prefixes) {
        String[] tokens = codecs.split(",");
        for (String token : tokens) {
            String value = token.trim();
            for (String prefix : prefixes) {
                if (value.startsWith(prefix)) return true;
            }
        }
        return false;
    }

    private static String getString(JsonObject object, String key, String fallback) {
        try {
            if (object.has(key) && !object.get(key).isJsonNull())
                return object.get(key).getAsString();
        } catch (Exception ignored) {}
        return fallback;
    }

    private static int getInt(JsonObject object, String key, int fallback) {
        try {
            if (object.has(key) && !object.get(key).isJsonNull())
                return object.get(key).getAsInt();
        } catch (Exception ignored) {}
        return fallback;
    }

    private static long getLong(JsonObject object, String key, long fallback) {
        try {
            if (object.has(key) && !object.get(key).isJsonNull())
                return object.get(key).getAsLong();
        } catch (Exception ignored) {}
        return fallback;
    }
}
