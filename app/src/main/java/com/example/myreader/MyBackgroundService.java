package com.example.myreader;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class MyBackgroundService extends Service {

    private static final String TAG = "MyBackgroundService";
    private static final int NOTIFICATION_ID = 1000;
    private static final int NETWORK_ERROR_NOTIFICATION_ID = 1001;
    private static final String CHANNEL_ID = "MyBackgroundServiceChannel";
    private static final String NETWORK_ERROR_CHANNEL_ID = "NetworkErrorChannel";

    private BroadcastReceiver connectivityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                boolean isConnected = isNetworkConnected();
                if (isConnected) {
                    // Network is connected, remove the network error notification
                    removeNetworkErrorNotification();
                } else {
                    // Network is disconnected, show the network error notification
                    showNetworkErrorNotification();
                }
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service created");
        registerConnectivityReceiver();
        // Start the service as a foreground service with the background service notification
        startForeground(NOTIFICATION_ID, createBackgroundNotification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service destroyed");
        unregisterConnectivityReceiver();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void registerConnectivityReceiver() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectivityReceiver, filter);
    }

    private void unregisterConnectivityReceiver() {
        unregisterReceiver(connectivityReceiver);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void showNetworkErrorNotification() {
        // Create a notification for network error
        Notification notification = new NotificationCompat.Builder(this, NETWORK_ERROR_CHANNEL_ID)
                .setContentTitle("Network Error")
                .setContentText("Please check your network connection.")
                .setSmallIcon(R.drawable.icon)
                .setAutoCancel(true) // Automatically remove the notification when clicked
                .build();

        // Show the network error notification
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.notify(NETWORK_ERROR_NOTIFICATION_ID, notification);
    }

    private void removeNetworkErrorNotification() {
        // Remove the network error notification
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.cancel(NETWORK_ERROR_NOTIFICATION_ID);
    }

    // Create a notification channel for the background service (required for Android 8.0 and above)
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

    // Create a notification for the background service
    private Notification createBackgroundNotification() {
        createNotificationChannel();

        // Create an intent to open MainActivity
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        mainActivityIntent.setAction(Intent.ACTION_MAIN);
        mainActivityIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, mainActivityIntent, PendingIntent.FLAG_IMMUTABLE);

        // Create a notification for the foreground service with the PendingIntent
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("App is running")
                .setContentText("App is running in the background")
                .setSmallIcon(R.drawable.icon)
                .setContentIntent(pendingIntent) // Set the PendingIntent to open MainActivity
                .setOngoing(true) // Make the notification sticky
                .build();
    }

    // Create a notification channel for network errors
    private void createNetworkErrorNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Network Error Channel";
            String description = "Channel for Network Error Notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH; // High importance for network error notifications
            NotificationChannel channel = new NotificationChannel(NETWORK_ERROR_CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Initialize the notification channels
    private void initializeNotificationChannels() {
        createNotificationChannel();
        createNetworkErrorNotificationChannel();
    }
}
