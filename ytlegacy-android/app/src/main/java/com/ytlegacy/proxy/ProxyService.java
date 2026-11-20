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
            server = new ProxyServer(getApplicationContext());
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

    private Notification buildNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        if (Build.VERSION.SDK_INT >= 11) {
            // Reflection avoids hard references to Notification.Builder on API 8-10.
            try {
                Class<?> builderClass = Class.forName("android.app.Notification$Builder");
                Object builder = builderClass.getConstructor(android.content.Context.class).newInstance(this);
                builderClass.getMethod("setContentTitle", CharSequence.class)
                    .invoke(builder, "ytlegacy");
                builderClass.getMethod("setContentText", CharSequence.class)
                    .invoke(builder, "Local proxy running on 127.0.0.1:8080");
                builderClass.getMethod("setSmallIcon", int.class)
                    .invoke(builder, android.R.drawable.stat_sys_download);
                builderClass.getMethod("setContentIntent", PendingIntent.class)
                    .invoke(builder, pendingIntent);
                builderClass.getMethod("setOngoing", boolean.class)
                    .invoke(builder, true);

                String finishMethod = Build.VERSION.SDK_INT >= 16 ? "build" : "getNotification";
                return (Notification) builderClass.getMethod(finishMethod).invoke(builder);
            } catch (Throwable t) {
                Log.w(TAG, "Notification.Builder unavailable: " + t.getMessage());
            }
        }

        // API 8-10 path; the same reflection path is a fallback on newer systems.
        Notification notification = new Notification(
            android.R.drawable.stat_sys_download,
            "ytlegacy",
            System.currentTimeMillis()
        );
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_NO_CLEAR;

        try {
            java.lang.reflect.Method method = Notification.class.getMethod(
                "setLatestEventInfo",
                android.content.Context.class,
                CharSequence.class,
                CharSequence.class,
                PendingIntent.class
            );
            method.invoke(notification, this, "ytlegacy",
                "Local proxy running on 127.0.0.1:8080", pendingIntent);
        } catch (Throwable t) {
            Log.w(TAG, "setLatestEventInfo unavailable: " + t.getMessage());
        }
        return notification;
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
