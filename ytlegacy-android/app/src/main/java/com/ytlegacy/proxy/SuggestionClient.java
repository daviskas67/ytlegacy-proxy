package com.ytlegacy.proxy;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

/** Lightweight, bounded YouTube search-suggestion client. */
public final class SuggestionClient {

    private static final String ENDPOINT =
        "https://suggestqueries.google.com/complete/search?client=youtube&ds=yt&hjson=t&q=";
    private static final long CACHE_TTL_MS = 45000L;
    private static final int MAX_RESPONSE_CHARS = 131072;

    private static final Map<String, CacheEntry> CACHE =
        new LinkedHashMap<String, CacheEntry>(32, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, CacheEntry> eldest) {
                return size() > 32;
            }
        };

    private SuggestionClient() {}

    public static String fetchJson(String query) {
        String q = query == null ? "" : query.trim();
        if (q.length() == 0) return "[]";
        if (q.length() > 128) q = q.substring(0, 128);

        long now = System.currentTimeMillis();
        synchronized (CACHE) {
            CacheEntry cached = CACHE.get(q);
            if (cached != null && now - cached.timestamp < CACHE_TTL_MS) {
                return cached.json;
            }
        }

        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            String encoded = URLEncoder.encode(q, "UTF-8");
            URL url = new URL(ENDPOINT + encoded);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Linux; Android 4.4) AppleWebKit/537.36 Chrome/120.0 Mobile Safari/537.36");
            connection.setRequestProperty("Accept", "application/json");
            if (connection.getResponseCode() != 200) return "[]";

            InputStream input = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
            StringBuilder body = new StringBuilder();
            char[] buffer = new char[2048];
            int count;
            while ((count = reader.read(buffer)) != -1) {
                int remaining = MAX_RESPONSE_CHARS - body.length();
                if (remaining <= 0) return "[]";
                body.append(buffer, 0, Math.min(count, remaining));
                if (body.length() >= MAX_RESPONSE_CHARS) return "[]";
            }

            String raw = body.toString().trim();
            JsonElement parsed = new JsonParser().parse(raw);
            if (!parsed.isJsonArray()) return "[]";
            String json = parsed.toString();
            synchronized (CACHE) {
                CACHE.put(q, new CacheEntry(json, System.currentTimeMillis()));
            }
            return json;
        } catch (Exception e) {
            android.util.Log.w("Suggestions", "Lookup failed: " + e.getMessage());
            return "[]";
        } finally {
            if (reader != null) {
                try { reader.close(); } catch (Exception ignored) {}
            }
            if (connection != null) connection.disconnect();
        }
    }

    private static final class CacheEntry {
        final String json;
        final long timestamp;

        CacheEntry(String json, long timestamp) {
            this.json = json;
            this.timestamp = timestamp;
        }
    }
}
