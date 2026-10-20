package com.ytlegacy.proxy;

public class ThumbnailHelper {
    /**
     * YouTube CDN имена превьюшек:
     *  0.jpg = hqdefault (480x360)
     *  1.jpg = sddefault (640x480)   -- на самом деле mqdefault в старом API
     *  2.jpg = mqdefault (320x180)
     *  3.jpg = maxresdefault (1280x720)
     *  4.jpg = default (120x90)
     *
     * В реальности на i.ytimg.com:
     *  0.jpg = hqdefault
     *  1.jpg = sddefault (640x480)
     *  2.jpg = mqdefault
     *  3.jpg = maxresdefault
     *  4.jpg = default (120x90)
     */
    public static String getCdnName(int index) {
        switch (index) {
            case 0: return "hqdefault";
            case 1: return "sddefault";
            case 2: return "mqdefault";
            case 3: return "maxresdefault";
            case 4: return "default";
            default: return "hqdefault";
        }
    }
}
