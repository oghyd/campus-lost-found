package com.uir.lostfound.utils;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.uir.lostfound.ClaimNotificationReceiver;

public class NotificationHelper {

    public static final String CHANNEL_ID   = "claim_channel";
    public static final String CHANNEL_NAME = "Claim Notifications";

    // Crée le canal de notification (obligatoire API 26+)
    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Notifications quand votre item est claimed");
            NotificationManager manager =
                    context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    // Programme la notification via AlarmManager (Bonus 2)
    public static void sendClaimedNotification(Context context,
                                               String ownerName,
                                               String itemTitle) {
        createNotificationChannel(context);

        Intent intent = new Intent(context, ClaimNotificationReceiver.class);
        intent.putExtra("owner_name", ownerName);
        intent.putExtra("item_title", itemTitle);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Déclenche dans 1 seconde
        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + 1000L,
                    pendingIntent
            );
        }
    }
}