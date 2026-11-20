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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Public Invidious API pool. The client probes only instances listed by the
 * Invidious project and orders them by latency measured on this device.
 */
public final class InvidiousClient {

    private static final String TAG = "Invidious";
    private static final int CONNECT_TIMEOUT_MS = 2500;
    private static final int READ_TIMEOUT_MS = 5000;
    private static final int REQUEST_CONNECT_TIMEOUT_MS = 1200;
    private static final int REQUEST_READ_TIMEOUT_MS = 2500;
    private static final int MAX_REQUEST_ATTEMPTS = 2;
    private static final long DOWN_TTL_MS = 60000L;
    private static final long PROBE_INTERVAL_MS = 60000L;

    // These four are currently listed by Invidious documentation.
    private static final String[] DOCUMENTED_INSTANCES = {
        "https://invidious.nerdvpn.de",
        "https://invidious.tiekoetter.com",
        "https://yt.chocolatemoo53.com",
        "https://inv.nadeko.net"
    };

    // Community candidates supplied for this project. They are NOT trusted or
    // endorsed by Invidious. The source list used HTTP URLs; this client tries
    // HTTPS only and accepts a host only after /api/v1/stats identifies Invidious.
    private static final String[] COMMUNITY_CANDIDATES = {
        "https://157.254.18.46:14120",
        "https://www.invidious.frantisekfiala.eu",
        "https://37.114.50.170:3000",
        "https://14.225.212.141:3000",
        "https://14.224.215.173:3000",
        "https://71.115.211.25:3067",
        "https://74.102.17.26:8203",
        "https://82.67.118.109:3000",
        "https://41.193.14.3:3000",
        "https://160.236.192.102:3000",
        "https://88.210.10.145:3001",
        "https://213.55.216.127:21388",
        "https://185.231.102.75:14120",
        "https://82.69.47.60:31008",
        "https://invidious.keltechnas.com",
        "https://videos.nathanle.co",
        "https://nate-stockhaus.amishcrabs.pics",
        "https://inv.stappsworld.com",
        "https://inv.tekforge.de",
        "https://vid.justanas.xyz",
        "https://109.66.233.145",
        "https://60.241.100.198:31008",
        "https://5.253.247.182:3000",
        "https://134.195.210.128:9022",
        "https://you.dwyu.tw",
        "https://invidious.malooma.bzh",
        "https://77.168.217.209:3000",
        "https://yt.tysstech.com",
        "https://95.182.97.252:8024",
        "https://inv.violethair.org",
        "https://youtube.eduardomiguel13.pt",
        "https://tube.spookyhome.ca",
        "https://watch.dredgy.au",
        "https://invidious.culturegremlin.club",
        "https://invidiousvt.v4l3nt1nvs.ch",
        "https://inv.dashy.a3x.dn.nyx.im",
        "https://utube.cyrus-ng.com",
        "https://yt.easys-web.de",
        "https://45.158.116.183",
        "https://invidious.emby11.freeddns.org",
        "https://tube.tjvl.dev",
        "https://103.82.192.64:6889",
        "https://163.61.72.21:3000",
        "https://yt.khoavo.vndns.net:7601"
    };

    private final Object lock = new Object();
    private final List<Server> servers = new ArrayList<Server>();
    private volatile boolean probing = false;
    private volatile long lastProbeAt = 0L;
    private final ThreadLocal<Integer> lastHttpStatus = new ThreadLocal<Integer>() {
        @Override protected Integer initialValue() { return 0; }
    };

    public InvidiousClient() {
        int order = 0;
        for (String base : DOCUMENTED_INSTANCES) {
            addCandidate(base, order++);
        }
        for (String base : COMMUNITY_CANDIDATES) {
            addCandidate(base, order++);
        }
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
                // Do not create one thread per public instance: this can be a
                // large community pool and many devices are low-memory Androids.
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
                try { latch.await(70L, TimeUnit.SECONDS); } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
                synchronized (lock) {
                    lastProbeAt = System.currentTimeMillis();
                    probing = false;
                    Log.i(TAG, "Probe cycle finished: " + getHealthyCountLocked()
                        + "/" + servers.size() + " healthy; fastest=" + getFastestHostLocked());
                    lock.notifyAll();
                }
            }
        }, "invidious-probe-cycle");
        worker.setDaemon(true);
        worker.start();
    }

    private long probe(String base) {
        long start = System.nanoTime();
        JsonElement body = requestRaw(base, "/api/v1/stats", CONNECT_TIMEOUT_MS, READ_TIMEOUT_MS);
        if (body == null || !body.isJsonObject()) return -1L;
        try {
            JsonObject stats = body.getAsJsonObject();
            if (!stats.has("software") || !stats.get("software").isJsonObject()) return -1L;
            String name = getString(stats.getAsJsonObject("software"), "name", "");
            if (!"invidious".equalsIgnoreCase(name)) return -1L;
            return Math.max(1L, (System.nanoTime() - start) / 1000000L);
        } catch (Exception e) {
            return -1L;
        }
    }

    public JsonArray search(String query) {
        String q = query == null ? "" : query;
        JsonElement result = getJson("/api/v1/search?q=" + encode(q) + "&page=1&type=video&region=PL");
        return result != null && result.isJsonArray() ? result.getAsJsonArray() : null;
    }

    public JsonArray searchChannels(String query) {
        String q = query == null ? "" : query;
        JsonElement result = getJson("/api/v1/search?q=" + encode(q) + "&page=1&type=channel&region=PL");
        return result != null && result.isJsonArray() ? result.getAsJsonArray() : null;
    }

    public JsonObject video(String videoId) {
        JsonElement result = getJson("/api/v1/videos/" + encode(videoId) + "?region=PL");
        return result != null && result.isJsonObject() ? result.getAsJsonObject() : null;
    }

    /** Short, metadata-only request used by batched feed cards. */
    public JsonObject videoForFeed(String videoId) {
        JsonElement result = getJson("/api/v1/videos/" + encode(videoId) + "?region=PL",
            2, 1000, 1500, 2500L);
        return result != null && result.isJsonObject() ? result.getAsJsonObject() : null;
    }

    public JsonArray trending() {
        JsonElement result = getJson("/api/v1/trending?type=default&region=PL");
        return result != null && result.isJsonArray() ? result.getAsJsonArray() : null;
    }

    public JsonObject playlist(String playlistId, int page) {
        JsonElement result = getJson("/api/v1/playlists/" + encode(playlistId) + "?page=" + Math.max(1, page));
        return result != null && result.isJsonObject() ? result.getAsJsonObject() : null;
    }

    public JsonArray channelVideos(String channelId, int page) {
        JsonElement result = getJson("/api/v1/channels/" + encode(channelId)
            + "/videos?page=" + Math.max(1, page));
        if (result != null && result.isJsonArray()) return result.getAsJsonArray();
        if (result != null && result.isJsonObject()) {
            JsonObject body = result.getAsJsonObject();
            if (body.has("videos") && body.get("videos").isJsonArray())
                return body.getAsJsonArray("videos");
        }
        return null;
    }

    public JsonObject comments(String videoId) {
        JsonElement result = getJson("/api/v1/comments/" + encode(videoId));
        return result != null && result.isJsonObject() ? result.getAsJsonObject() : null;
    }

    public boolean isTrustedStreamUrl(String streamUrl) {
        try {
            URL url = new URL(streamUrl);
            if (!"https".equalsIgnoreCase(url.getProtocol())) return false;
            String host = url.getHost() == null ? "" : url.getHost().toLowerCase(java.util.Locale.US);
            if (host.equals("googlevideo.com") || host.endsWith(".googlevideo.com")) return true;
            int streamPort = url.getPort() < 0 ? url.getDefaultPort() : url.getPort();
            synchronized (lock) {
                for (Server server : servers) {
                    URL allowedUrl = new URL(server.base);
                    String allowedHost = allowedUrl.getHost().toLowerCase(java.util.Locale.US);
                    int allowedPort = allowedUrl.getPort() < 0
                        ? allowedUrl.getDefaultPort() : allowedUrl.getPort();
                    if (host.equals(allowedHost) && streamPort == allowedPort) return true;
                }
            }
        } catch (Exception ignored) {}
        return false;
    }

    public JsonObject channel(String channelId) {
        JsonElement result = getJson("/api/v1/channels/" + encode(channelId));
        return result != null && result.isJsonObject() ? result.getAsJsonObject() : null;
    }

    /** Short channel lookup for the legacy feed's metadata batch. */
    public JsonObject channelForFeed(String channelId) {
        JsonElement result = getJson("/api/v1/channels/" + encode(channelId),
            2, 1000, 1500, 2500L);
        return result != null && result.isJsonObject() ? result.getAsJsonObject() : null;
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
                // Never send a real request to an unprobed or unhealthy candidate.
                if (!server.probed || !server.alive || server.deadUntil > System.currentTimeMillis()) continue;
            }
            if (attempts >= maxAttempts) break;
            attempts++;
            long start = System.nanoTime();
            JsonElement result = requestRaw(server.base, path,
                connectTimeoutMs, readTimeoutMs);
            long latency = Math.max(1L, (System.nanoTime() - start) / 1000000L);
            int statusCode = lastHttpStatus.get() == null ? 0 : lastHttpStatus.get().intValue();

            if (result != null) {
                markResponsive(server, latency);
                if (!isApiError(result)) return result;
                // A valid HTTP response with an API-level error means the server is
                // reachable. Try the next instance, but do not quarantine this host.
                Log.w(TAG, server.base + " returned an API error for " + path);
                continue;
            }

            if (statusCode == 0 || statusCode == 429 || statusCode >= 500 || statusCode == 200) {
                String reason = statusCode == 200
                    ? "HTTP 200 body read/JSON parse failed for " + path
                    : "HTTP " + statusCode + " for " + path;
                markFailed(server, reason);
            } else {
                // 4xx usually means a missing/private resource, not a dead instance.
                markResponsive(server, latency);
                Log.w(TAG, server.base + " returned HTTP " + statusCode + " for " + path);
            }
        }

        if (getHealthyCount() == 0) refreshInBackground();
        return null;
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

    private boolean isApiError(JsonElement result) {
        if (result == null || result.isJsonNull()) return true;
        if (result.isJsonObject()) {
            JsonObject obj = result.getAsJsonObject();
            if (obj.has("error") && !obj.has("videoId") && !obj.has("videos")) return true;
        }
        return false;
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
        synchronized (lock) {
            copy = new ArrayList<Server>(servers);
        }
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
            connection.setRequestProperty("User-Agent", "ytlegacy-proxy/1.7 Android");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Accept-Language", "en-US,en;q=0.8");
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

    private String encode(String value) {
        try { return URLEncoder.encode(value == null ? "" : value, "UTF-8"); }
        catch (Exception e) { return ""; }
    }

    private static String getString(JsonObject obj, String key, String fallback) {
        try {
            if (obj.has(key) && !obj.get(key).isJsonNull()) return obj.get(key).getAsString();
        } catch (Exception ignored) {}
        return fallback;
    }

    private static long smooth(long previous, long current) {
        if (previous <= 0L || previous == Long.MAX_VALUE) return current;
        return (previous * 3L + current) / 4L;
    }

    private int getHealthyCountLocked() {
        int count = 0;
        long now = System.currentTimeMillis();
        for (Server server : servers) if (server.alive && server.deadUntil <= now) count++;
        return count;
    }

    private String getFastestHostLocked() {
        Server fastest = null;
        long now = System.currentTimeMillis();
        for (Server server : servers) {
            if (!server.alive || server.deadUntil > now) continue;
            if (fastest == null || server.latencyMs < fastest.latencyMs) fastest = server;
        }
        return fastest == null ? "" : fastest.base;
    }

    public int getConfiguredCount() {
        synchronized (lock) { return servers.size(); }
    }

    public int getHealthyCount() {
        awaitInitialProbe();
        synchronized (lock) { return getHealthyCountLocked(); }
    }

    public String getFastestHost() {
        synchronized (lock) { return getFastestHostLocked(); }
    }

    public long getFastestLatencyMs() {
        synchronized (lock) {
            String host = getFastestHostLocked();
            for (Server server : servers) if (server.base.equals(host)) return server.latencyMs;
            return -1L;
        }
    }

    public JsonObject getStatus() {
        JsonObject status = new JsonObject();
        status.addProperty("configuredInstances", getConfiguredCount());
        status.addProperty("healthyInstances", getHealthyCount());
        status.addProperty("fastestHost", getFastestHost());
        status.addProperty("fastestLatencyMs", getFastestLatencyMs());
        status.addProperty("probing", probing);
        status.addProperty("lastProbeAt", lastProbeAt);
        JsonArray items = new JsonArray();
        List<Server> snapshot = getOrderedServers();
        long now = System.currentTimeMillis();
        for (Server server : snapshot) {
            JsonObject item = new JsonObject();
            item.addProperty("host", server.base);
            item.addProperty("probed", server.probed);
            item.addProperty("healthy", server.alive && server.deadUntil <= now);
            item.addProperty("latencyMs", server.latencyMs == Long.MAX_VALUE ? -1L : server.latencyMs);
            item.addProperty("failures", server.failures);
            item.addProperty("lastSuccess", server.lastSuccess);
            items.add(item);
        }
        status.add("instances", items);
        return status;
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
