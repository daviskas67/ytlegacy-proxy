package com.ytlegacy.proxy;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;

/**
 * yt2009 gsign account-linking helper.
 *
 * The Google sign-in page and account picker are hosted by the explicitly
 * configured yt2009 instance. This app stores only the short device code;
 * the upstream yt2009 instance stores the resulting account session.
 */
public final class Yt2009AccountManager {
    private static final String TAG = "Yt2009Account";
    public static final String INSTANCE = "http://femboy.email:730";
    private static final String PREFS = "yt2009_account";
    private static final String KEY_DEVICE = "device";
    private static final String KEY_AUTHORIZED = "authorized";
    private static final String ALPHABET = "0123456789abcde";
    private static final int CONNECT_TIMEOUT_MS = 7000;
    private static final int READ_TIMEOUT_MS = 12000;

    private final SharedPreferences preferences;
    private final SecureRandom random = new SecureRandom();

    public Yt2009AccountManager(Context context) {
        Context app = context == null ? null : context.getApplicationContext();
        preferences = app == null ? null
            : app.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public JSONObject startLogin() throws Exception {
        if (preferences == null) throw new IllegalStateException("Account storage unavailable");

        // yt2009's gsign device code is limited to seven alphanumeric characters.
        String device = generateDeviceCode();
        HttpURLConnection connection = null;
        try {
            URL url = new URL(INSTANCE + "/gsign?device=" + device);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
            connection.setReadTimeout(READ_TIMEOUT_MS);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Linux; Android 4.4.2; Mobile) AppleWebKit/537.36 Chrome/80.0 Mobile Safari/537.36");
            connection.setRequestProperty("Accept", "text/html,*/*;q=0.8");

            int status = connection.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                throw new java.io.IOException("yt2009 /gsign returned HTTP " + status);
            }

            String page = readLimited(connection.getInputStream(), 256 * 1024);
            if (page == null || !page.contains(device)) {
                throw new java.io.IOException("yt2009 did not initialize the requested device code");
            }

            preferences.edit()
                .putString(KEY_DEVICE, device)
                .putBoolean(KEY_AUTHORIZED, false)
                .commit();

            JSONObject result = new JSONObject();
            result.put("ok", true);
            result.put("device", device);
            result.put("instance", INSTANCE);
            result.put("desktopUrl", INSTANCE + "/gsign");
            result.put("pickerUrl", accountPickerUrl(device));
            result.put("message", "Open the desktop URL on a PC, enter the device code, complete yt2009 sign-in, then return here to check the status.");
            return result;
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    /**
     * Checks the account through yt2009's own device-list endpoint.
     * A 200 response means the gsign flow has stored an account for this code.
     */
    public JSONObject checkStatus() {
        JSONObject result = new JSONObject();
        String device = getDeviceId();
        try {
            result.put("device", device == null ? "" : device);
            result.put("instance", INSTANCE);
            result.put("pickerUrl", device == null ? "" : accountPickerUrl(device));

            if (device == null || device.length() != 7) {
                result.put("ok", true);
                result.put("authorized", false);
                result.put("accountCount", 0);
                result.put("message", "No login session has been started yet.");
                return result;
            }

            HttpURLConnection connection = null;
            try {
                URL url = new URL(INSTANCE + "/gsign_get_devices");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
                connection.setReadTimeout(READ_TIMEOUT_MS);
                connection.setUseCaches(false);
                connection.setInstanceFollowRedirects(false);
                connection.setRequestProperty("User-Agent", "ytlegacy-proxy/1.11 Android");
                connection.setRequestProperty("device", device);

                int status = connection.getResponseCode();
                if (status != HttpURLConnection.HTTP_OK) {
                    preferences.edit().putBoolean(KEY_AUTHORIZED, false).commit();
                    result.put("ok", true);
                    result.put("authorized", false);
                    result.put("accountCount", 0);
                    result.put("message", "yt2009 has not linked an account to this device code yet.");
                    return result;
                }

                String body = readLimited(connection.getInputStream(), 512 * 1024);
                JSONArray accounts = new JSONArray(body == null || body.trim().length() == 0
                    ? "[]" : body);
                int selectedCount = 0;
                String selectedName = "";
                for (int i = 0; i < accounts.length(); i++) {
                    JSONObject account = accounts.optJSONObject(i);
                    if (account != null && account.optBoolean("selected", false)) {
                        selectedCount++;
                        selectedName = account.optString("name", "");
                        if (selectedName.length() == 0)
                            selectedName = account.optString("handle", "");
                    }
                }

                boolean authorized = accounts.length() > 0;
                preferences.edit().putBoolean(KEY_AUTHORIZED, authorized).commit();
                result.put("ok", true);
                result.put("authorized", authorized);
                result.put("accountCount", accounts.length());
                result.put("selectedCount", selectedCount);
                result.put("selectedAccount", selectedName);
                result.put("message", authorized
                    ? "Google account linked. Open the account picker to choose the YouTube channel."
                    : "Google sign-in completed, but yt2009 did not return any YouTube channels.");
                return result;
            } finally {
                if (connection != null) connection.disconnect();
            }
        } catch (Exception e) {
            Log.w(TAG, "Account status check failed: " + e.getMessage());
            try {
                result.put("ok", false);
                result.put("authorized", false);
                result.put("accountCount", 0);
                result.put("error", e.getMessage() == null ? "Connection failed" : e.getMessage());
            } catch (Exception ignored) {}
            return result;
        }
    }

    public String getDeviceId() {
        return preferences == null ? null : preferences.getString(KEY_DEVICE, null);
    }

    public boolean isAuthorized() {
        return preferences != null
            && preferences.getBoolean(KEY_AUTHORIZED, false)
            && getDeviceId() != null;
    }

    public String getAccountPickerUrl() {
        String device = getDeviceId();
        return device == null ? "" : accountPickerUrl(device);
    }

    public static String accountPickerUrl(String device) {
        return INSTANCE + "/mobile/mobilehelper/account_picker.html?device=" + device;
    }

    private String generateDeviceCode() {
        StringBuilder value = new StringBuilder(7);
        for (int i = 0; i < 7; i++) {
            value.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return value.toString();
    }

    private static String readLimited(InputStream input, int maximumBytes) throws Exception {
        if (input == null) return "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
        StringBuilder output = new StringBuilder();
        char[] buffer = new char[2048];
        int count;
        try {
            while ((count = reader.read(buffer)) != -1) {
                if (output.length() + count > maximumBytes) {
                    throw new java.io.IOException("yt2009 response exceeded size limit");
                }
                output.append(buffer, 0, count);
            }
        } finally {
            reader.close();
        }
        return output.toString();
    }
}
