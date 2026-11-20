package com.ytlegacy.proxy;

import android.content.Context;
import android.content.SharedPreferences;

/** Small API-8-compatible storage for playback preferences. */
public final class PlaybackSettings {
    public static final String DEFAULT_QUALITY = "360p";
    private static final String PREFS_NAME = "ytlegacy_proxy_settings";
    private static final String KEY_QUALITY = "playback_quality";

    private PlaybackSettings() {}

    public static String getQuality(Context context) {
        if (context == null) return DEFAULT_QUALITY;
        try {
            SharedPreferences preferences =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String value = preferences.getString(KEY_QUALITY, DEFAULT_QUALITY);
            return isSupported(value) ? value : DEFAULT_QUALITY;
        } catch (Exception ignored) {
            return DEFAULT_QUALITY;
        }
    }

    public static boolean setQuality(Context context, String quality) {
        if (context == null || !isSupported(quality)) return false;
        try {
            SharedPreferences preferences =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            // commit() is used instead of apply(): the app supports Android API 8.
            return preferences.edit().putString(KEY_QUALITY, quality).commit();
        } catch (Exception ignored) {
            return false;
        }
    }

    private static boolean isSupported(String quality) {
        return "360p".equals(quality) || "480p".equals(quality)
            || "720p".equals(quality) || "auto".equals(quality);
    }
}
