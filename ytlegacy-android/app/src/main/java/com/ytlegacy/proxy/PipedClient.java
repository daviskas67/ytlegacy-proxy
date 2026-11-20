package com.ytlegacy.proxy;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Read-only Piped API pool used as a secondary public source for search/trending.
 * No account credentials or Authorization headers are sent to Piped instances.
 */
public final class PipedClient {
    private static final String TAG = "Piped";
    private static final int CONNECT_TIMEOUT_MS = 2500;
    private static final int READ_TIMEOUT_MS = 5000;
    private static final int REQUEST_CONNECT_TIMEOUT_MS = 1200;
    private static final int REQUEST_READ_TIMEOUT_MS = 2500;
    private static final int MAX_REQUEST_ATTEMPTS = 2;
    private static final long DOWN_TTL_MS = 60000L;
    private static final long PROBE_INTERVAL_MS = 60000L;

    // Current public API endpoints listed by the Piped documentation.
    private static final String[] DOCUMENTED_INSTANCES = {
        "https://pipedapi.kavin.rocks",
        "https://pipedapi.leptons.xyz",
        "https://pipedapi.nosebs.ru",
        "https://pipedapi-libre.kavin.rocks",
        "https://piped-api.privacy.com.de",
        "https://pipedapi.adminforge.de",
        "https://api.piped.yt",
        "https://pipedapi.drgns.space",
        "https://pipedapi.owo.si",
        "https://pipedapi.ducks.party",
        "https://piped-api.codespace.cz",
        "https://pipedapi.reallyaweso.me",
        "https://api.piped.private.coffee",
        "https://pipedapi.darkness.services",
        "https://pipedapi.orangenet.cc"
    };

    // Additional API endpoints supplied for this project. The supplied list
    // used HTTP; only HTTPS is attempted here. Health checks decide which work.
    private static final String[] COMMUNITY_CANDIDATES = {
        "https://124.158.10.214:8081",
        "https://167.114.169.34:8081",
        "https://152.53.31.104:8091/api",
        "https://pipedapi.xorrito.info",
        "https://pa.dfjko.cc:8080",
        "https://pipedapi.stellar.afs.ovh",
        "https://pipedapi.hilles.space",
        "https://piped-api.lithome.fr",
        "https://pipedapi.lesviallon.fr",
        "https://api.piped.breidenstein24.de",
        "https://maxtube-api.lawrie.co.nz",
        "https://be-j3h84i5n40780sbmmfzq.majeol.uk",
        "https://pipedapi.hugoblomdahl.se",
        "https://pipedapi.r4vermind.org"
    };

    private final Object lock = new Object();
    private final List<Server> servers = new ArrayList<Server>();
    private volatile boolean probing = false;
    private volatile long lastProbeAt = 0L;
    private final ThreadLocal<Integer> lastHttpStatus = new ThreadLocal<Integer>() {
        @Override protected Integer initialValue() { return 0; }
    };

    public PipedClient() {
        int order = 0;
        for (String base : DOCUMENTED_INSTANCES) addCandidate(base, order++);
        for (String base : COMMUNITY_CANDIDATES) addCandidate(base, order++);
    }

    private void addCandidate(String base, int order) {
        if (base == null || !base.startsWith("https://")) return;
        for (Server server : servers) {
            if (server.base.equalsIgnoreCase(base)) return;
        }
        servers.add(new Server(base, order));
    }

    public void refreshInBackground() {
        synchronized (lock) {
            if (probing || System.currentTimeMillis() - lastProbeAt < PROBE_INTERVAL_MS) return;
            probing = true;
        }
        Thread worker = new Thread(new Runnable() {
            @Override public void run() {
                final CountDownLatch latch = new CountDownLatch(servers.size());
                // Keep the number of probe threads small for older Android devices.
                ExecutorService executor = Executors.newFixedThreadPool(6);
                for (final Server server : servers) {
                    executor.execute(new Runnable() {
                        @Override public void run() {
                            long latency = probe(server.base);
                            synchronized (lock) {
                                server.probed = true;
                                if (latency >= 0L) {
                                    server.alive = true;
                                    server.latencyMs = latency;
                                    server.deadUntil = 0L;
                                    server.lastSuccess = System.currentTimeMillis();
                                    server.failures = 0;
                                    Log.i(TAG, "Probe OK " + server.base + " " + latency + "ms");
                                } else {
                                    server.alive = false;
                                    server.deadUntil = System.currentTimeMillis() + DOWN_TTL_MS;
                                    Log.w(TAG, "Probe failed " + server.base);
                                }
                            }
                            latch.countDown();
                        }
                    });
                }
                executor.shutdown();
                try { latch.await(45L, TimeUnit.SECONDS); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                synchronized (lock) {
                    lastProbeAt = System.currentTimeMillis();
                    probing = false;
                    Log.i(TAG, "Probe cycle finished: " + getHealthyCountLocked()
                        + "/" + servers.size() + " healthy");
                    lock.notifyAll();
                }
            }
        }, "piped-probe-cycle");
        worker.setDaemon(true);
        worker.start();
    }

    private long probe(String base) {
        long start = System.nanoTime();
        JsonElement result = requestRaw(base, "/trending?region=PL", CONNECT_TIMEOUT_MS, READ_TIMEOUT_MS);
        if (result == null) return -1L;
        if (result.isJsonArray()) return Math.max(1L, (System.nanoTime() - start) / 1000000L);
        if (result.isJsonObject()) {
            JsonObject object = result.getAsJsonObject();
            if (object.has("error") || object.has("message")) return -1L;
        }
        return -1L;
    }

    public JsonArray trending() {
        JsonElement result = getJson("/trending?region=PL");
        return normalizeVideos(result);
    }

    /** Short trending lookup for the legacy home-feed bootstrap. */
    public JsonArray trendingForFeed() {
        JsonElement result = getJson("/trending?region=PL", 2, 1000, 1500, 2500L);
        return normalizeVideos(result);
    }

    /** Fetches unauthenticated stream metadata through the healthy Piped instance pool. */
    public JsonObject streams(String videoId) {
        if (!validVideoId(videoId)) return null;
        JsonElement result = getJson("/streams/" + encode(videoId));
        return result != null && result.isJsonObject() ? result.getAsJsonObject() : null;
    }

    /** Short, metadata-only request used by batched feed cards. */
    public JsonObject streamsForFeed(String videoId) {
        if (!validVideoId(videoId)) return null;
        JsonElement result = getJson("/streams/" + encode(videoId),
            2, 1000, 1500, 2500L);
        return result != null && result.isJsonObject() ? result.getAsJsonObject() : null;
    }

    public JsonArray search(String query) {
        String q = query == null ? "" : query;
        JsonElement result = getJson("/search?q=" + encode(q) + "&filter=videos");
        return normalizeVideos(result);
    }

    JsonArray normalizeVideos(JsonElement response) {
        JsonArray source = null;
        if (response != null && response.isJsonArray()) {
            source = response.getAsJsonArray();
        } else if (response != null && response.isJsonObject()) {
            JsonObject object = response.getAsJsonObject();
            if (object.has("items") && object.get("items").isJsonArray())
                source = object.getAsJsonArray("items");
        }

        if (source == null) return null;

        JsonArray normalized = new JsonArray();

        for (int i = 0; i < source.size() && normalized.size() < 50; i++) {
            try {
                if (!source.get(i).isJsonObject()) continue;
                JsonObject item = source.get(i).getAsJsonObject();
                String type = getString(item, "type", "stream");
                if (!"stream".equalsIgnoreCase(type) && !"video".equalsIgnoreCase(type)) continue;

                String id = getString(item, "videoId", "");
                if (!validVideoId(id)) id = videoIdFromUrl(getString(item, "url", ""));
                if (!validVideoId(id)) continue;

                JsonObject video = new JsonObject();
                video.addProperty("videoId", id);
                video.addProperty("title", getString(item, "title", id));
                video.addProperty("author", getString(item, "uploaderName",
                    getString(item, "uploader", "YouTube")));
                video.addProperty("authorId", channelIdFromUrl(getString(item, "uploaderUrl", "")));
                video.addProperty("lengthSeconds", getLong(item, "duration", 0L));
                video.addProperty("viewCount", getLong(item, "views", 0L));
                video.addProperty("likeCount", getLong(item, "likes", 0L));
                video.addProperty("description", getString(item, "shortDescription",
                    getString(item, "description", "")));
                normalized.add(video);
            } catch (Exception e) {
                Log.w(TAG, "Skipping Piped search/trending item: " + e.getMessage());
            }
        }
        return normalized;
    }


    // Package-private seam for local unit tests; production callers use search/trending.
    private JsonElement getJson(String path) {
        return getJson(path, MAX_REQUEST_ATTEMPTS, REQUEST_CONNECT_TIMEOUT_MS,
            REQUEST_READ_TIMEOUT_MS, 6500L);
    }

    private JsonElement getJson(String path, int maxAttempts, int connectTimeoutMs,
                                 int readTimeoutMs, long initialProbeWaitMs) {
        awaitInitialProbe(initialProbeWaitMs);
        if (System.currentTimeMillis() - lastProbeAt >= PROBE_INTERVAL_MS)
            refreshInBackground();

        List<Server> ordered = getOrderedServers();
        int attempts = 0;
        for (Server server : ordered) {
            synchronized (lock) {
                if (!server.probed || !server.alive || server.deadUntil > System.currentTimeMillis()) continue;
            }
            if (attempts >= maxAttempts) break;
            attempts++;
            long start = System.nanoTime();
            JsonElement result = requestRaw(server.base, path,
                connectTimeoutMs, readTimeoutMs);
            long latency = Math.max(1L, (System.nanoTime() - start) / 1000000L);
            int status = lastHttpStatus.get() == null ? 0 : lastHttpStatus.get().intValue();

            if (result != null && !isApiError(result)) {
                markResponsive(server, latency);
                return result;
            }
            if (status == 0 || status == 429 || status >= 500 || status == 200) {
                String reason = status == 200
                    ? "HTTP 200 body read/JSON parse failed for " + path
                    : "HTTP " + status + " for " + path;
                markFailed(server, reason);
            } else {
                markResponsive(server, latency);
                Log.w(TAG, server.base + " returned HTTP " + status + " for " + path);
            }
        }

        if (getHealthyCount() == 0) refreshInBackground();
        return null;
    }

    private void awaitInitialProbe() {
        awaitInitialProbe(6500L);
    }

    private void awaitInitialProbe(long maxWaitMs) {
        refreshInBackground();
        long deadline = System.currentTimeMillis() + Math.max(0L, maxWaitMs);
        synchronized (lock) {
            while (probing && lastProbeAt == 0L) {
                long remaining = deadline - System.currentTimeMillis();
                if (remaining <= 0L) break;
                try { lock.wait(Math.min(250L, remaining)); }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    private JsonElement requestRaw(String base, String path, int connectTimeout, int readTimeout) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        lastHttpStatus.set(0);
        try {
            URL url = new URL(base + (path.startsWith("/") ? path : "/" + path));
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(connectTimeout);
            connection.setReadTimeout(readTimeout);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("User-Agent", "ytlegacy-proxy/1.10 Android");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Accept-Language", "en-US,en;q=0.8");
            // Intentionally no Authorization, cookies, or account headers.
            int code = connection.getResponseCode();
            lastHttpStatus.set(code);
            if (code != 200) return null;

            InputStream input = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
            StringBuilder raw = new StringBuilder();
            char[] buffer = new char[4096];
            int count;
            while ((count = reader.read(buffer)) != -1) {
                if (raw.length() + count > 4 * 1024 * 1024) return null;
                raw.append(buffer, 0, count);
            }
            String body = raw.toString().trim();
            if (body.length() == 0) return null;
            return new JsonParser().parse(body);
        } catch (Exception e) {
            Log.w(TAG, "GET " + base + path + " failed: " + e.getMessage());
            return null;
        } finally {
            if (reader != null) {
                try { reader.close(); } catch (Exception ignored) {}
            }
            if (connection != null) connection.disconnect();
        }
    }

    private boolean isApiError(JsonElement result) {
        if (result == null || result.isJsonNull()) return true;
        if (result.isJsonObject()) {
            JsonObject object = result.getAsJsonObject();
            return object.has("error") || object.has("message");
        }
        return !result.isJsonArray();
    }

    private void markResponsive(Server server, long latency) {
        synchronized (lock) {
            server.probed = true;
            server.alive = true;
            server.failures = 0;
            server.deadUntil = 0L;
            server.lastSuccess = System.currentTimeMillis();
            server.latencyMs = smooth(server.latencyMs, latency);
        }
    }

    private void markFailed(Server server, String reason) {
        synchronized (lock) {
            server.probed = true;
            server.failures++;
            server.alive = false;
            long coolDown = server.failures >= 3 ? 300000L : DOWN_TTL_MS;
            server.deadUntil = System.currentTimeMillis() + coolDown;
        }
        Log.w(TAG, server.base + " marked unavailable: " + reason);
    }

    private List<Server> getOrderedServers() {
        final long now = System.currentTimeMillis();
        List<Server> copy;
        synchronized (lock) { copy = new ArrayList<Server>(servers); }
        Collections.sort(copy, new Comparator<Server>() {
            @Override public int compare(Server a, Server b) {
                boolean aa = a.alive && a.deadUntil <= now;
                boolean ba = b.alive && b.deadUntil <= now;
                if (aa != ba) return aa ? -1 : 1;
                long al = a.latencyMs <= 0L ? Long.MAX_VALUE : a.latencyMs;
                long bl = b.latencyMs <= 0L ? Long.MAX_VALUE : b.latencyMs;
                if (al == bl) return a.order - b.order;
                return al < bl ? -1 : 1;
            }
        });
        return copy;
    }

    private int getHealthyCountLocked() {
        int count = 0;
        long now = System.currentTimeMillis();
        for (Server server : servers) {
            if (server.probed && server.alive && server.deadUntil <= now) count++;
        }
        return count;
    }

    private int getConfiguredCount() {
        synchronized (lock) { return servers.size(); }
    }

    public int getHealthyCount() {
        synchronized (lock) { return getHealthyCountLocked(); }
    }

    public JsonObject getStatus() {
        JsonObject status = new JsonObject();
        status.addProperty("configuredInstances", getConfiguredCount());
        status.addProperty("healthyInstances", getHealthyCount());
        status.addProperty("probing", probing);
        status.addProperty("lastProbeAt", lastProbeAt);
        JsonArray items = new JsonArray();
        List<Server> snapshot = getOrderedServers();
        long now = System.currentTimeMillis();
        for (Server server : snapshot) {
            JsonObject item = new JsonObject();
            item.addProperty("base", server.base);
            item.addProperty("probed", server.probed);
            item.addProperty("healthy", server.probed && server.alive && server.deadUntil <= now);
            item.addProperty("latencyMs", server.latencyMs == Long.MAX_VALUE ? -1L : server.latencyMs);
            item.addProperty("failures", server.failures);
            item.addProperty("lastSuccess", server.lastSuccess);
            items.add(item);
        }
        status.add("instances", items);
        return status;
    }

    private static String encode(String value) {
        try {
            return URLEncoder.encode(value == null ? "" : value, "UTF-8");
        } catch (Exception e) {
            return "";
        }
    }

    private static String videoIdFromUrl(String raw) {
        if (raw == null) return "";
        String value = raw.trim();
        if (validVideoId(value)) return value;
        int marker = value.indexOf("v=");
        if (marker >= 0) {
            String tail = value.substring(marker + 2);
            int end = tail.indexOf('&');
            if (end >= 0) tail = tail.substring(0, end);
            if (validVideoId(tail)) return tail;
        }
        return "";
    }

    private static String channelIdFromUrl(String raw) {
        if (raw == null) return "";
        int marker = raw.indexOf("/channel/");
        if (marker < 0) return "";
        String tail = raw.substring(marker + 9);
        int end = tail.indexOf('/');
        if (end >= 0) tail = tail.substring(0, end);
        return tail.startsWith("UC") && tail.length() == 24 ? tail : "";
    }

    private static boolean validVideoId(String id) {
        return id != null && id.matches("[A-Za-z0-9_-]{11}");
    }

    private static String getString(JsonObject object, String key, String fallback) {
        try {
            if (object != null && object.has(key) && !object.get(key).isJsonNull())
                return object.get(key).getAsString();
        } catch (Exception ignored) {}
        return fallback;
    }

    private static long getLong(JsonObject object, String key, long fallback) {
        try {
            if (object != null && object.has(key) && !object.get(key).isJsonNull())
                return object.get(key).getAsLong();
        } catch (Exception ignored) {}
        return fallback;
    }

    private static long smooth(long previous, long current) {
        if (previous <= 0L || previous == Long.MAX_VALUE) return current;
        return (previous * 3L + current) / 4L;
    }

    private static final class Server {
        final String base;
        final int order;
        volatile long latencyMs = Long.MAX_VALUE;
        volatile long deadUntil = 0L;
        volatile long lastSuccess = 0L;
        volatile int failures = 0;
        volatile boolean alive = false;
        volatile boolean probed = false;

        Server(String base, int order) {
            this.base = base;
            this.order = order;
        }
    }
}
