package com.example.myreader;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class SmsReceiver extends BroadcastReceiver {



    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            if (pdus != null) {
                for (Object pdu : pdus) {
                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                    String sender = smsMessage.getOriginatingAddress();
                    String messageBody = smsMessage.getMessageBody();

                    // Display a toast for the incoming SMS
                    ToastHelper.showCustomToast(context, "SMS received from: " + sender + "\nMessage: " + messageBody,null);

                    // Check if the app has notification permission and request if necessary
                    if (hasNotificationPermission(context)) {
                        // Create and display a notification with a unique ID
                        Functions.createNotification(context, "New message from:"+sender, messageBody);
                    }
                }
            }
        }
    }

    private boolean hasNotificationPermission(Context context) {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        return notificationManagerCompat.areNotificationsEnabled();
    }


}
