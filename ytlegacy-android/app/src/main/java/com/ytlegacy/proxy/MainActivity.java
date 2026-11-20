package com.ytlegacy.proxy;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Build;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends Activity {
    private TextView accountStatus;
    private String lastDeviceCode = "";
    private String lastPickerUrl = "";
    private final Handler loginHandler = new Handler();
    private boolean loginPollActive = false;
    private boolean activityResumed = false;
    private boolean loginCompletionDialogShown = false;
    private boolean pickerOpened = false;
    private int loginPollAttempts = 0;
    private static final long LOGIN_POLL_INTERVAL_MS = 5000L;
    private static final int LOGIN_POLL_LIMIT = 180;
    private final Runnable loginPollRunnable = new Runnable() {
        @Override public void run() {
            pollLoginStatus();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityResumed = true;
        if (pickerOpened) {
            pickerOpened = false;
            restoreLoginState();
        } else if (loginPollActive) {
            loginHandler.removeCallbacks(loginPollRunnable);
            loginHandler.post(loginPollRunnable);
        } else if (lastDeviceCode.length() == 0 && isServiceRunning()) {
            restoreLoginState();
        }
    }

    @Override
    protected void onPause() {
        activityResumed = false;
        loginHandler.removeCallbacks(loginPollRunnable);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        loginHandler.removeCallbacks(loginPollRunnable);
        loginPollActive = false;
        super.onDestroy();
    }

    private void setupUI() {
        final float density = getResources().getDisplayMetrics().density;
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.setBackgroundColor(Color.BLACK);

        int padding = (int) (20 * density + 0.5f);
        layout.setPadding(padding, padding, padding, padding);

        TextView title = new TextView(this);
        title.setText("ytlegacy");
        title.setTextColor(Color.WHITE);
        title.setTextSize(30);
        title.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        titleParams.bottomMargin = (int) (20 * density + 0.5f);
        layout.addView(title, titleParams);

        final TextView status = new TextView(this);
        status.setText(isServiceRunning() ? "Proxy: RUNNING on 127.0.0.1:8080" : "Proxy: STOPPED");
        status.setTextColor(Color.GREEN);
        status.setTextSize(16);
        status.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams statusParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        statusParams.bottomMargin = (int) (18 * density + 0.5f);
        layout.addView(status, statusParams);

        Button toggleButton = new Button(this);
        toggleButton.setText("Start / Stop");
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ProxyService.class);
                if (isServiceRunning()) {
                    stopService(intent);
                    status.setText("Proxy: STOPPED");
                } else {
                    startService(intent);
                    status.setText("Proxy: RUNNING on 127.0.0.1:8080");
                }
            }
        });
        addButton(layout, toggleButton, density);

        Button copyButton = new Button(this);
        copyButton.setText("Copy IP");
        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                copyText("127.0.0.1:8080");
                Toast.makeText(MainActivity.this, "Copied!", Toast.LENGTH_SHORT).show();
            }
        });
        addButton(layout, copyButton, density);

        TextView accountTitle = new TextView(this);
        accountTitle.setText("yt2009 account");
        accountTitle.setTextColor(Color.WHITE);
        accountTitle.setTextSize(20);
        accountTitle.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams accountTitleParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        accountTitleParams.topMargin = (int) (18 * density + 0.5f);
        accountTitleParams.bottomMargin = (int) (8 * density + 0.5f);
        layout.addView(accountTitle, accountTitleParams);

        accountStatus = new TextView(this);
        accountStatus.setText("Account: not linked");
        accountStatus.setTextColor(Color.LTGRAY);
        accountStatus.setTextSize(14);
        accountStatus.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams accountStatusParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        accountStatusParams.bottomMargin = (int) (8 * density + 0.5f);
        layout.addView(accountStatus, accountStatusParams);

        Button loginButton = new Button(this);
        loginButton.setText("Connect Google account");
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                startGoogleLogin();
            }
        });
        addButton(layout, loginButton, density);

        Button checkButton = new Button(this);
        checkButton.setText("I finished sign-in — check now");
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                checkGoogleLogin(false);
            }
        });
        addButton(layout, checkButton, density);

        Button pickerButton = new Button(this);
        pickerButton.setText("Choose / change YouTube channel");
        pickerButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                checkGoogleLogin(true);
            }
        });
        addButton(layout, pickerButton, density);

        TextView privacy = new TextView(this);
        privacy.setText("Never enter your Google password on the yt2009 page. Sign in only on accounts.google.com. yt2009 then sends an authorization token to femboy.email over plain HTTP, which can be intercepted on an untrusted network.");
        privacy.setTextColor(Color.GRAY);
        privacy.setTextSize(11);
        privacy.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams privacyParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        privacyParams.topMargin = (int) (10 * density + 0.5f);
        layout.addView(privacy, privacyParams);

        setContentView(layout);
    }

    private void startGoogleLogin() {
        if (!isServiceRunning()) {
            Toast.makeText(this, "Start the proxy first", Toast.LENGTH_SHORT).show();
            return;
        }
        accountStatus.setText("Contacting femboy.email:730...");
        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    final JSONObject result = requestLocal("/api/yt2009/login/start", "POST");
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            if (!result.optBoolean("ok", false)) {
                                accountStatus.setText("Account: could not start sign-in");
                                showMessage("yt2009 sign-in", result.optString("error", "Connection failed"));
                                return;
                            }
                            lastDeviceCode = result.optString("device", "");
                            lastPickerUrl = result.optString("pickerUrl", "");
                            accountStatus.setText("Waiting for sign-in · code " + lastDeviceCode);
                            showLoginInstructions(result);
                            startLoginPolling();
                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            accountStatus.setText("Account: sign-in unavailable");
                            showMessage("yt2009 sign-in", e.getMessage());
                        }
                    });
                }
            }
        }, "yt2009-login-start").start();
    }

    private void showLoginInstructions(final JSONObject result) {
        final String code = result.optString("device", "");
        final String desktopUrl = result.optString("desktopUrl", "http://femboy.email:730/gsign");
        String message = "On a PC, open:\n" + desktopUrl
            + "\n\nEnter this device code:\n" + code
            + "\n\nFollow the sign-in steps shown by yt2009. Keep this app open: it checks the account automatically every five seconds. When yt2009 confirms the account, the app will offer to open the YouTube channel picker.";
        message += "\\n\\nThe app will detect when the account is linked and then offer the channel picker.";
        new AlertDialog.Builder(this)
            .setTitle("Step 1 of 2 · Start sign-in")
            .setMessage(message)
            .setPositiveButton("Share setup instructions", new DialogInterface.OnClickListener() {
                @Override public void onClick(DialogInterface dialog, int which) {
                    shareSetupInstructions(code, desktopUrl);
                }
            })
            .setNeutralButton("Copy device code", new DialogInterface.OnClickListener() {
                @Override public void onClick(DialogInterface dialog, int which) {
                    copyText(code);
                    Toast.makeText(MainActivity.this, "Device code copied", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Close", null)
            .show();
    }

    private void shareSetupInstructions(String code, String desktopUrl) {
        String instructions = "ytlegacy-proxy — Google account setup\\n\\n"
            + "1. On your computer, open: " + desktopUrl + "\\n"
            + "2. Enter this device code: " + code + "\\n"
            + "3. Complete the steps shown by the yt2009 page.\\n"
            + "4. Leave ytlegacy-proxy running. It will detect the linked account and offer the YouTube channel picker.\\n\\n"
            + "Use only the exact yt2009 host shown above. The upstream sign-in page explains its remaining manual step.";
        Intent send = new Intent(Intent.ACTION_SEND);
        send.setType("text/plain");
        send.putExtra(Intent.EXTRA_SUBJECT, "ytlegacy-proxy sign-in instructions");
        send.putExtra(Intent.EXTRA_TEXT, instructions);
        try {
            startActivity(Intent.createChooser(send, "Send setup instructions to your computer"));
        } catch (Exception e) {
            copyText(instructions);
            Toast.makeText(this, "Could not open sharing. Instructions copied instead.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Polls the yt2009 instance for completion of its own gsign flow.
     * This never reads browser traffic or extracts Google authorization tokens.
     */
    private void startLoginPolling() {
        loginHandler.removeCallbacks(loginPollRunnable);
        loginPollActive = true;
        loginCompletionDialogShown = false;
        loginPollAttempts = 0;
        accountStatus.setText("Waiting for sign-in · code " + lastDeviceCode + " · auto-checking");
        loginHandler.post(loginPollRunnable);
    }

    private void pollLoginStatus() {
        if (!loginPollActive || !activityResumed || isFinishing()) return;
        if (!isServiceRunning()) {
            loginPollActive = false;
            accountStatus.setText("Proxy stopped · sign-in check paused");
            return;
        }
        loginPollAttempts++;
        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    final JSONObject result = requestLocal("/api/yt2009/login/status", "GET");
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            if (!loginPollActive || !activityResumed || isFinishing()) return;
                            boolean authorized = result.optBoolean("authorized", false);
                            if (authorized) {
                                loginPollActive = false;
                                loginHandler.removeCallbacks(loginPollRunnable);
                                lastDeviceCode = result.optString("device", lastDeviceCode);
                                lastPickerUrl = result.optString("pickerUrl", lastPickerUrl);
                                int count = result.optInt("accountCount", 0);
                                accountStatus.setText("Account linked · " + count + " channel(s)");
                                if (!loginCompletionDialogShown) {
                                    loginCompletionDialogShown = true;
                                    new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Google account linked")
                                        .setMessage("yt2009 confirmed the account. Open the YouTube channel picker now?")
                                        .setPositiveButton("Select channel", new DialogInterface.OnClickListener() {
                                            @Override public void onClick(DialogInterface dialog, int which) {
                                                openChannelPicker(result);
                                            }
                                        })
                                        .setNegativeButton("Later", null)
                                        .show();
                                }
                                return;
                            }

                            if (loginPollAttempts >= LOGIN_POLL_LIMIT) {
                                loginPollActive = false;
                                accountStatus.setText("Still waiting · tap Check sign-in status");
                                return;
                            }
                            accountStatus.setText("Waiting for Google sign-in · code " + lastDeviceCode
                                + " · check " + loginPollAttempts + "/" + LOGIN_POLL_LIMIT);
                            loginHandler.postDelayed(loginPollRunnable, LOGIN_POLL_INTERVAL_MS);
                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            if (!loginPollActive || !activityResumed || isFinishing()) return;
                            if (loginPollAttempts >= LOGIN_POLL_LIMIT) {
                                loginPollActive = false;
                                accountStatus.setText("Status unavailable · tap Check sign-in status");
                            } else {
                                accountStatus.setText("Waiting for yt2009 · retrying automatically");
                                loginHandler.postDelayed(loginPollRunnable, LOGIN_POLL_INTERVAL_MS);
                            }
                        }
                    });
                }
            }
        }, "yt2009-login-poll").start();
    }

    private void restoreLoginState() {
        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    final JSONObject result = requestLocal("/api/yt2009/login/status", "GET");
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            if (isFinishing()) return;
                            lastDeviceCode = result.optString("device", "");
                            lastPickerUrl = result.optString("pickerUrl", "");
                            if (result.optBoolean("authorized", false)) {
                                int selectedCount = result.optInt("selectedCount", 0);
                                String selectedName = result.optString("selectedAccount", "");
                                if (selectedCount > 0) {
                                    accountStatus.setText("Connected · " + selectedName);
                                } else {
                                    accountStatus.setText("Account linked · choose a YouTube channel");
                                }
                            } else if (lastDeviceCode.length() == 7) {
                                startLoginPolling();
                            } else {
                                accountStatus.setText("Account: not linked");
                            }
                        }
                    });
                } catch (Exception ignored) {}
            }
        }, "yt2009-login-restore").start();
    }

    private void openChannelPicker(JSONObject result) {
        String picker = result == null ? lastPickerUrl
            : result.optString("pickerUrl", lastPickerUrl);
        if (picker.length() == 0) picker = lastPickerUrl;
        if (picker.length() == 0) {
            showMessage("Channel picker", "No account picker URL is available.");
            return;
        }
        try {
            pickerOpened = true;
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(picker)));
        } catch (Exception e) {
            pickerOpened = false;
            showMessage("Channel picker", "Could not open browser: " + e.getMessage());
        }
    }

    private void checkGoogleLogin(final boolean openPicker) {
        if (!isServiceRunning()) {
            Toast.makeText(this, "Start the proxy first", Toast.LENGTH_SHORT).show();
            return;
        }
        accountStatus.setText("Checking femboy.email:730...");
        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    final JSONObject result = requestLocal("/api/yt2009/login/status", "GET");
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            boolean authorized = result.optBoolean("authorized", false);
                            int count = result.optInt("accountCount", 0);
                            int selectedCount = result.optInt("selectedCount", 0);
                            String selected = result.optString("selectedAccount", "");
                            if (authorized) {
                                String label = selectedCount > 0
                                    ? "Connected · " + selected
                                    : "Account linked · " + count + " channel(s) · choose one";
                                accountStatus.setText(label);
                                if (openPicker) {
                                    openChannelPicker(result);
                                } else {
                                    Toast.makeText(MainActivity.this, "yt2009 account found", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                accountStatus.setText("Account: waiting for Google sign-in");
                                String error = result.optString("error", result.optString("message", "Not linked yet"));
                                if (openPicker) {
                                    showMessage("Account not linked yet", error + "\n\nComplete the sign-in on a PC, then check again.");
                                } else {
                                    Toast.makeText(MainActivity.this, error, Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            accountStatus.setText("Account: status check failed");
                            showMessage("yt2009 status", e.getMessage());
                        }
                    });
                }
            }
        }, "yt2009-login-status").start();
    }

    private JSONObject requestLocal(String path, String method) throws Exception {
        HttpURLConnection connection = null;
        try {
            URL url = new URL("http://127.0.0.1:8080" + path);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(15000);
            connection.setUseCaches(false);
            connection.setRequestProperty("Accept", "application/json");
            if ("POST".equals(method)) {
                connection.setDoOutput(true);
                OutputStream output = connection.getOutputStream();
                output.flush();
                output.close();
            }

            int status = connection.getResponseCode();
            InputStream input = status >= 400 ? connection.getErrorStream() : connection.getInputStream();
            StringBuilder body = new StringBuilder();
            if (input != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (body.length() + line.length() > 256 * 1024)
                        throw new java.io.IOException("Local response too large");
                    body.append(line);
                }
                reader.close();
            }
            if (status < 200 || status >= 300) {
                throw new java.io.IOException("Local proxy HTTP " + status + ": " + body.toString());
            }
            return new JSONObject(body.toString());
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    private void showMessage(String title, String message) {
        new AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message == null ? "Unknown error" : message)
            .setPositiveButton("OK", null)
            .show();
    }

    @SuppressWarnings("deprecation")
    private void copyText(String value) {
        if (Build.VERSION.SDK_INT >= 11) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard != null) clipboard.setPrimaryClip(ClipData.newPlainText("ytlegacy", value));
        } else {
            android.text.ClipboardManager clipboard =
                (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard != null) clipboard.setText(value);
        }
    }

    private void addButton(LinearLayout parent, Button button, float density) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = (int) (7 * density + 0.5f);
        parent.addView(button, params);
    }

    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (manager == null) return false;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service.service != null && ProxyService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
