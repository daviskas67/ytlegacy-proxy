package com.ytlegacy.proxy;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

public class InnerTubeClient {

    private static final String TAG = "InnerTube";

    private static final String CLIENT_NAME_BODY = "ANDROID_VR";
    private static final String CLIENT_NAME_HEADER = "28";
    private static final String CLIENT_VERSION = "1.60.19";
    private static final String USER_AGENT =
            "com.google.android.apps.youtube.vr.oculus/1.60.19 (Linux; U; Android 12; en_US; Quest 3 Build/SQ3A.220705.004) gzip";

    private final Gson gson = new Gson();
    private static boolean tlsInitialized = false;

    public InnerTubeClient() {
        initTls();
    }

    /**
     * Uses the platform trust store and the default hostname verifier.
     * Never accept arbitrary certificates or hostnames.
     */
    private static synchronized void initTls() {
        if (tlsInitialized) return;
        tlsInitialized = true;
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, null, null);
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            Log.i(TAG, "TLS initialized with platform certificate validation");
        } catch (Exception e) {
            Log.e(TAG, "TLS init failed: " + e.getMessage());
        }
    }

    public JsonObject player(String videoId) {
        return post("/youtubei/v1/player", buildPlayerBody(videoId, "ANDROID_VR"));
    }

    public JsonObject playerIos(String videoId) {
        return post("/youtubei/v1/player", buildPlayerBody(videoId, "IOS"));
    }

    public JsonObject playerTV(String videoId) {
        return post("/youtubei/v1/player", buildPlayerBody(videoId, "TVHTML5_SIMPLY_EMBEDDED_PLAYER"));
    }

    public JsonObject playerWebEmbedded(String videoId) {
        return post("/youtubei/v1/player", buildPlayerBody(videoId, "WEB_EMBEDDED_PLAYER"));
    }

    public JsonObject playerMweb(String videoId) {
        return post("/youtubei/v1/player", buildPlayerBody(videoId, "MWEB"));
    }

    public JsonObject search(String query) {
        return post("/youtubei/v1/search", buildSearchBody(query));
    }

    public JsonObject browse(String browseId) {
        return post("/youtubei/v1/browse", buildBrowseBody(browseId));
    }

    public JsonObject searchChannels(String query) {
        JsonObject body = new JsonObject();
        body.add("context", buildContext());
        body.addProperty("query", query);
        body.addProperty("params", "EgIQAg==");  // type: channel
        return post("/youtubei/v1/search", body.toString());
    }

    private String buildPlayerBody(String videoId, String clientName) {
        JsonObject body = new JsonObject();
        JsonObject ctx = new JsonObject();
        JsonObject client = new JsonObject();
        if ("IOS".equals(clientName)) {
            client.addProperty("clientName", "IOS");
            client.addProperty("clientVersion", "19.29.1");
            client.addProperty("deviceMake", "Apple");
            client.addProperty("deviceModel", "iPhone16,2");
            client.addProperty("osName", "iPhone");
            client.addProperty("osVersion", "17.5.1.21F90");
        } else if ("TVHTML5_SIMPLY_EMBEDDED_PLAYER".equals(clientName)) {
            client.addProperty("clientName", "TVHTML5_SIMPLY_EMBEDDED_PLAYER");
            client.addProperty("clientVersion", "2.0");
            client.addProperty("hl", "en");
            client.addProperty("gl", "US");
            JsonObject thirdParty = new JsonObject();
            thirdParty.addProperty("embedUrl", "https://www.youtube.com/");
            JsonObject ctxT = new JsonObject();
            ctxT.add("client", client);
            ctxT.add("thirdParty", thirdParty);
            body.add("context", ctxT);
            body.addProperty("videoId", videoId);
            body.addProperty("contentCheckOk", true);
            body.addProperty("racyCheckOk", true);
            return body.toString();
        } else if ("WEB_EMBEDDED_PLAYER".equals(clientName)) {
            client.addProperty("clientName", "WEB_EMBEDDED_PLAYER");
            client.addProperty("clientVersion", "1.20241201.00.00");
        } else if ("MWEB".equals(clientName)) {
            client.addProperty("clientName", "MWEB");
            client.addProperty("clientVersion", "2.20241126.01.00");
        } else {
            client.addProperty("clientName", "ANDROID_VR");
            client.addProperty("clientVersion", "1.60.19");
            client.addProperty("deviceMake", "Oculus");
            client.addProperty("deviceModel", "Quest 3");
            client.addProperty("androidSdkVersion", 32);
            client.addProperty("osName", "Android");
            client.addProperty("osVersion", "12");
        }
        client.addProperty("hl", "en");
        client.addProperty("gl", "US");
        ctx.add("client", client);
        body.add("context", ctx);
        body.addProperty("videoId", videoId);
        body.addProperty("contentCheckOk", true);
        body.addProperty("racyCheckOk", true);
        return body.toString();
    }

    private String buildSearchBody(String query) {
        JsonObject body = new JsonObject();
        body.add("context", buildContext());
        body.addProperty("query", query);
        body.addProperty("params", "EgIQAQ==");
        return body.toString();
    }

    private String buildBrowseBody(String browseId) {
        JsonObject body = new JsonObject();
        body.add("context", buildContext());
        body.addProperty("browseId", browseId);
        return body.toString();
    }

    private JsonObject buildContext() {
        JsonObject context = new JsonObject();
        JsonObject client = new JsonObject();
        client.addProperty("clientName", CLIENT_NAME_BODY);
        client.addProperty("clientVersion", CLIENT_VERSION);
        client.addProperty("deviceMake", "Oculus");
        client.addProperty("deviceModel", "Quest 3");
        client.addProperty("androidSdkVersion", 32);
        client.addProperty("osName", "Android");
        client.addProperty("osVersion", "12");
        client.addProperty("hl", "en");
        client.addProperty("gl", "US");
        context.add("client", client);
        return context;
    }

    private JsonObject post(String endpoint, String jsonBody) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL("https://www.youtube.com" + endpoint);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("User-Agent", USER_AGENT);
            conn.setRequestProperty("X-YouTube-Client-Name", CLIENT_NAME_HEADER);
            conn.setRequestProperty("X-YouTube-Client-Version", CLIENT_VERSION);
            conn.setRequestProperty("Accept-Language", "en-US,en;q=0.9");
            conn.setDoOutput(true);
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(20000);

            OutputStream os = conn.getOutputStream();
            try {
                os.write(jsonBody.getBytes("UTF-8"));
            } finally {
                os.close();
            }

            int code = conn.getResponseCode();
            Log.i(TAG, endpoint + " -> HTTP " + code);
            if (code != 200) {
                Log.e(TAG, "Non-200 response for " + endpoint);
                return null;
            }

            BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), "UTF-8"));
            try {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                String raw = sb.toString();
                Log.d(TAG, "Response size: " + raw.length() + " bytes");
                return new JsonParser().parse(raw).getAsJsonObject();
            } finally {
                br.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Request failed: " + e.getMessage());
            return null;
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    // ==== NEXT endpoint (для related и comments) ====
    public JsonObject next(String videoId) {
        return post("/youtubei/v1/next", buildNextBody(videoId));
    }

    public JsonObject nextContinuation(String continuation) {
        return post("/youtubei/v1/next", buildContinuationBody(continuation));
    }

    private String buildNextBody(String videoId) {
        JsonObject body = new JsonObject();
        body.add("context", buildContext());
        body.addProperty("videoId", videoId);
        return body.toString();
    }

    private String buildContinuationBody(String token) {
        JsonObject body = new JsonObject();
        body.add("context", buildContext());
        body.addProperty("continuation", token);
        return body.toString();
    }

}
