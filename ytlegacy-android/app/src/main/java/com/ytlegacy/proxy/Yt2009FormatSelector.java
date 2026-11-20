package com.ytlegacy.proxy;

import java.util.Map;

/**
 * Maps yt2009's legacy fmt_stream_map entries to self-contained MP4 formats.
 * Only progressive H.264/AAC formats are accepted by the old player.
 */
public final class Yt2009FormatSelector {
    public static final class Selection {
        public final int itag;
        public final int height;
        public final String url;

        private Selection(int itag, int height, String url) {
            this.itag = itag;
            this.height = height;
            this.url = url;
        }
    }

    private Yt2009FormatSelector() {}

    public static Selection select(Map<Integer, String> formats, String quality) {
        if (formats == null || formats.isEmpty()) return null;

        int target = StreamFormatSelector.targetHeight(quality);
        Selection bestBelow = null;
        Selection bestAbove = null;

        for (Map.Entry<Integer, String> entry : formats.entrySet()) {
            if (entry == null || entry.getKey() == null || entry.getValue() == null) continue;
            int itag = entry.getKey().intValue();
            int height = heightForItag(itag);
            String url = entry.getValue().trim();
            if (height <= 0 || url.length() == 0 || height > StreamFormatSelector.MAX_HEIGHT) continue;

            Selection candidate = new Selection(itag, height, url);
            if (height <= target) {
                if (bestBelow == null || height > bestBelow.height) bestBelow = candidate;
            } else if (bestAbove == null || height < bestAbove.height) {
                bestAbove = candidate;
            }
        }

        return bestBelow != null ? bestBelow : bestAbove;
    }

    /**
     * Rewrites yt2009's private backend address to the exact instance that
     * served get_video_info. This deliberately does not pin URLs to one host.
     */
    public static String normalizeFormatUrl(String rawUrl, String sourceInstance) {
        if (rawUrl == null || rawUrl.trim().length() == 0) return "";
        String value = rawUrl.trim();
        try {
            java.net.URL url;
            if (value.startsWith("//")) value = "https:" + value;
            if (value.startsWith("/")) {
                if (sourceInstance == null || sourceInstance.length() == 0) return "";
                url = new java.net.URL(new java.net.URL(sourceInstance + "/"), value);
            } else {
                url = new java.net.URL(value);
            }

            String host = url.getHost() == null ? "" : url.getHost().toLowerCase(java.util.Locale.US);
            int port = url.getPort() < 0 ? url.getDefaultPort() : url.getPort();
            if ("68.172.220.11".equals(host) && port == 730) {
                if (sourceInstance == null || sourceInstance.length() == 0) return "";
                java.net.URL base = new java.net.URL(sourceInstance);
                return new java.net.URL(base, url.getFile()).toExternalForm();
            }
            return url.toExternalForm();
        } catch (Exception ignored) {
            return "";
        }
    }

    public static String pathAndQuery(String rawUrl) {
        try {
            java.net.URL url = new java.net.URL(rawUrl);
            String path = url.getPath();
            if (path == null || path.length() == 0) path = "/";
            return url.getQuery() == null ? path : path + "?" + url.getQuery();
        } catch (Exception ignored) {
            return "";
        }
    }

    public static int heightForItag(int itag) {
        switch (itag) {
            case 18: return 360; // MP4 H.264 + AAC
            case 59: return 480; // MP4 H.264 + AAC
            case 22: return 720; // MP4 H.264 + AAC
            default: return 0;   // Excludes FLV, WebM, video-only and audio-only formats.
        }
    }
}
