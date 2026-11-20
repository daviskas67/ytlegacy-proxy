package com.ytlegacy.proxy;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.net.URL;

/** Selects a self-contained H.264/AAC MP4 from a Piped /streams response. */
public final class PipedStreamSelector {

    public static final class Selection {
        public final String url;
        public final String proxyUrl;
        public final int height;
        public final int width;
        public final String codec;
        public final String mimeType;

        private Selection(String url, String proxyUrl, int height, int width,
                          String codec, String mimeType) {
            this.url = url;
            this.proxyUrl = proxyUrl;
            this.height = height;
            this.width = width;
            this.codec = codec;
            this.mimeType = mimeType;
        }
    }

    private PipedStreamSelector() {}

    public static Selection select(JsonObject response, String quality) {
        if (response == null || !response.has("videoStreams")
                || !response.get("videoStreams").isJsonArray()) return null;

        String proxyUrl = getString(response, "proxyUrl", "");
        if (!isHttpsUrl(proxyUrl)) return null;

        JsonArray streams = response.getAsJsonArray("videoStreams");
        int target = StreamFormatSelector.targetHeight(quality);
        Selection below = null;
        Selection above = null;

        for (int i = 0; i < streams.size(); i++) {
            try {
                if (!streams.get(i).isJsonObject()) continue;
                JsonObject item = streams.get(i).getAsJsonObject();

                String url = getString(item, "url", "");
                String mime = getString(item, "mimeType", "");
                String codec = getString(item, "codec", "").toLowerCase(java.util.Locale.US);
                String format = getString(item, "format", "");
                int height = getInt(item, "height", StreamFormatSelector.getHeight(item));
                int width = getInt(item, "width", 0);

                // Do not infer audio availability from a URL or codec. Piped's
                // videoOnly=false explicitly marks a muxed stream.
                if (!item.has("videoOnly") || item.get("videoOnly").isJsonNull()
                        || item.get("videoOnly").getAsBoolean()) continue;
                if (!mime.toLowerCase(java.util.Locale.US).startsWith("video/mp4")) continue;
                if (!"MPEG_4".equalsIgnoreCase(format)) continue;
                if (!(codec.startsWith("avc1") || codec.startsWith("avc3"))) continue;
                if (!isHttpsUrl(url) || !sameOrigin(proxyUrl, url)) continue;
                if (height < 144 || height > StreamFormatSelector.MAX_HEIGHT) continue;

                Selection candidate = new Selection(url, proxyUrl, height, width, codec, mime);
                if (height <= target) {
                    if (below == null || height > below.height) below = candidate;
                } else if (above == null || height < above.height) {
                    above = candidate;
                }
            } catch (Exception ignored) {}
        }
        return below != null ? below : above;
    }

    public static boolean isTrustedStreamUrl(String proxyUrl, String streamUrl) {
        return isHttpsUrl(proxyUrl) && isHttpsUrl(streamUrl) && sameOrigin(proxyUrl, streamUrl);
    }

    private static boolean isHttpsUrl(String value) {
        try {
            URL url = new URL(value);
            return "https".equalsIgnoreCase(url.getProtocol())
                && url.getHost() != null && url.getHost().length() > 0
                && url.getUserInfo() == null && url.getRef() == null;
        } catch (Exception ignored) {
            return false;
        }
    }

    private static boolean sameOrigin(String first, String second) {
        try {
            URL a = new URL(first);
            URL b = new URL(second);
            int aPort = a.getPort() < 0 ? a.getDefaultPort() : a.getPort();
            int bPort = b.getPort() < 0 ? b.getDefaultPort() : b.getPort();
            return a.getProtocol().equalsIgnoreCase(b.getProtocol())
                && a.getHost().equalsIgnoreCase(b.getHost()) && aPort == bPort;
        } catch (Exception ignored) {
            return false;
        }
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
}
