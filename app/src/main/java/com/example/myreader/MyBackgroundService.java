package com.example.myreader;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class MyBackgroundService extends Service {

    private static final String TAG = "MyBackgroundService";
    private static final int NOTIFICATION_ID = 1000;
    private static final String CHANNEL_ID = "MyBackgroundServiceChannel";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started");

        // Create a notification channel (required for Android 8.0 and above)
        createNotificationChannel();

        // Create an intent to open MainActivity
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        mainActivityIntent.setAction(Intent.ACTION_MAIN);
        mainActivityIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, mainActivityIntent, PendingIntent.FLAG_IMMUTABLE);

        // Create a notification for the foreground service with the PendingIntent
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("App is running")
                .setContentText("App is running in the background")
                .setSmallIcon(R.drawable.icon)
                .setContentIntent(pendingIntent) // Set the PendingIntent to open MainActivity
                .setOngoing(true) // Make the notification sticky
                .build();

        // Start the service as a foreground service with the notification
        startForeground(NOTIFICATION_ID, notification);

        // Your background service logic here

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service destroyed");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Create a notification channel for the foreground service (required for Android 8.0 and above)
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "MyBackgroundService Channel";
            String description = "Channel for MyBackgroundService";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
