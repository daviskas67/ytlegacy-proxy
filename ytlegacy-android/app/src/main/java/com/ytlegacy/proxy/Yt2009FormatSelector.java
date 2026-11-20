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

    public static int heightForItag(int itag) {
        switch (itag) {
            case 18: return 360; // MP4 H.264 + AAC
            case 59: return 480; // MP4 H.264 + AAC
            case 22: return 720; // MP4 H.264 + AAC
            default: return 0;   // Excludes FLV, WebM, video-only and audio-only formats.
        }
    }
}
