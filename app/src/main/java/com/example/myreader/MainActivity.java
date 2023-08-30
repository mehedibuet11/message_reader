package com.example.myreader;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_CODE = 101;
    private static final int NOTIFICATION_PERMISSION_CODE = 102;

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check and request SMS permissions if needed
        if (checkSmsPermission()) {
            // SMSReceiver will display incoming messages as toasts.
            if (checkNotificationPermission()) {
                ToastHelper.showCustomToast(this,"Your app is running",null);
                Functions.createNotification(MainActivity.this,"Watching","Your app is running");
            } else {
                requestNotificationPermission();
            }
        } else {
            requestSmsPermission();
        }
    }

    @Override
    public void onBackPressed() {
        // Display a confirmation dialog when the back button is pressed
        new AlertDialog.Builder(this)
                .setTitle("Exit Confirmation")
                .setIcon(R.drawable.baseline_exit_to_app_24)
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes,Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Finish the activity and exit the app
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Dismiss the dialog and do nothing
                        dialog.dismiss();
                    }
                })
                .show();
    }


    // Check if SMS permissions are granted
    private boolean checkSmsPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    // Request SMS permissions
    private void requestSmsPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, SMS_PERMISSION_CODE);
    }

    // Check if notification permissions are granted
    private boolean checkNotificationPermission() {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        return notificationManagerCompat.areNotificationsEnabled();
    }

    // Request notification permissions
    private void requestNotificationPermission() {
        Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        startActivityForResult(intent, NOTIFICATION_PERMISSION_CODE);
    }

    // Handle permission request results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Now, check and request notification permission
                if (checkNotificationPermission()) {
                    //  send a test notification:
                    Functions.createNotification(MainActivity.this,"Watching","Your app is running");

                } else {
                    requestNotificationPermission();
                }
            } else {
                ToastHelper.showCustomToast(this, "SMS permission denied. The app may not work correctly.", null);
            }
        }
    }

    // Handle notification permission request results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (checkNotificationPermission()) {
                // Notification permission granted
                Functions.createNotification(MainActivity.this,"Watching","Your app is running");
            } else {
                ToastHelper.showCustomToast(this,"Notification permission denied. Some features may not work.",null);
            }
        }
    }


}
