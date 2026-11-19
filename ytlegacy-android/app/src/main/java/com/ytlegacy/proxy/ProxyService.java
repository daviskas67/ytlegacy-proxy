package com.ytlegacy.proxy;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import fi.iki.elonen.NanoHTTPD;

import java.io.IOException;

public class ProxyService extends Service {

    private static final String TAG = "ProxyService";
    private static final int NOTIFICATION_ID = 1;

    private ProxyServer server;
    private boolean serverStarted = false;

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            Notification notification = buildNotification();
            startForeground(NOTIFICATION_ID, notification);
        } catch (Throwable t) {
            Log.w(TAG, "startForeground failed: " + t.getMessage());
            // Если foreground не сработал — продолжаем как обычный сервис
        }

        try {
            server = new ProxyServer();
            server.initEmptyMp4(getApplicationContext());
            server.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
            serverStarted = true;
            Log.i(TAG, "Proxy server started on 127.0.0.1:8080");
        } catch (Exception e) {
            serverStarted = false;
            Log.e(TAG, "Failed to start server: " + e.getMessage(), e);
            if (server != null) {
                try { server.stop(); } catch (Exception ignored) {}
                server = null;
            }
        }
    }

    @SuppressWarnings("deprecation")
    private Notification buildNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        if (Build.VERSION.SDK_INT >= 11) {
            // Notification.Builder.build() exists from API 16; API 11-15 use getNotification().
            Notification.Builder builder = new Notification.Builder(this)
                .setContentTitle("ytlegacy-proxy 1.7")
                .setContentText("Server running on 127.0.0.1:8080")
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentIntent(pendingIntent)
                .setOngoing(true);
            if (Build.VERSION.SDK_INT >= 16) return builder.build();
            return builder.getNotification();
        } else {
            // API 8-10 (Froyo/Gingerbread): raw Notification + setLatestEventInfo
            Notification n = new Notification(
                android.R.drawable.stat_sys_download,
                "ytlegacy-proxy",
                System.currentTimeMillis()
            );
            n.flags |= Notification.FLAG_ONGOING_EVENT;
            n.flags |= Notification.FLAG_NO_CLEAR;

            try {
                // setLatestEventInfo удалён в API 23, зовём через reflection
                java.lang.reflect.Method m = Notification.class.getMethod(
                    "setLatestEventInfo",
                    android.content.Context.class,
                    CharSequence.class,
                    CharSequence.class,
                    PendingIntent.class
                );
                m.invoke(n, this, "ytlegacy-proxy", "Server running", pendingIntent);
            } catch (Throwable t) {
                Log.w(TAG, "setLatestEventInfo failed: " + t.getMessage());
            }
            return n;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!serverStarted) {
            Log.e(TAG, "Service will stop because the local proxy did not start");
            stopSelf(startId);
            return START_NOT_STICKY;
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        serverStarted = false;
        if (server != null) server.stop();
        server = null;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
