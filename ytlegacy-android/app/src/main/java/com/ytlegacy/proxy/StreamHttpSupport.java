package com.ytlegacy.proxy;

import java.net.HttpURLConnection;

/** Small HTTP helpers shared by stream routes and JVM tests. */
public final class StreamHttpSupport {
    private static final int MAX_RANGE_HEADER_LENGTH = 128;

    private StreamHttpSupport() {}

    public static void applyRangeHeader(HttpURLConnection connection, String range) {
        if (connection == null || range == null) return;
        String value = range.trim();
        if (value.length() == 0 || value.length() > MAX_RANGE_HEADER_LENGTH
                || value.indexOf('\r') >= 0 || value.indexOf('\n') >= 0) return;
        if (!value.regionMatches(true, 0, "bytes=", 0, 6)) return;
        connection.setRequestProperty("Range", value);
    }

    public static boolean isSuccessfulStreamStatus(int statusCode) {
        return statusCode == HttpURLConnection.HTTP_OK
            || statusCode == HttpURLConnection.HTTP_PARTIAL;
    }

    public static boolean isPartialContent(int statusCode) {
        return statusCode == HttpURLConnection.HTTP_PARTIAL;
    }

    public static boolean isRangeNotSatisfiable(int statusCode) {
        return statusCode == 416;
    }

    public static boolean isPlayableContentType(String contentType) {
        if (contentType == null) return false;
        String value = contentType.trim().toLowerCase(java.util.Locale.US);
        return value.startsWith("video/")
            || value.startsWith("application/octet-stream");
    }

    public static String responseHeader(HttpURLConnection connection,
                                        String name, String fallback) {
        if (connection == null || name == null) return fallback;
        String value = connection.getHeaderField(name);
        return value == null || value.trim().length() == 0 ? fallback : value;
    }

    public static boolean isSafeRedirectTarget(java.net.URL url) {
        if (url == null || url.getHost() == null) return false;
        String protocol = url.getProtocol() == null ? "" : url.getProtocol().toLowerCase(java.util.Locale.US);
        String host = url.getHost().toLowerCase(java.util.Locale.US);
        if ("https".equals(protocol)
                && (host.equals("googlevideo.com") || host.endsWith(".googlevideo.com"))) return true;
        int port = url.getPort() < 0 ? url.getDefaultPort() : url.getPort();
        return ("http".equals(protocol) || "https".equals(protocol))
            && host.equals("femboy.email") && port == 730;
    }
}
