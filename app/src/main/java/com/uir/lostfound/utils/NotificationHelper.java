package com.uir.lostfound.utils;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.uir.lostfound.ClaimNotificationReceiver;

/**
 * NotificationHelper — utility class for creating notification channels and scheduling
 * "item claimed" notifications via AlarmManager.
 *
 * On API 26+ a notification channel ({@code claim_channel}) must be created before
 * any notification can be posted. {@link #createNotificationChannel(Context)} is
 * idempotent and safe to call multiple times.
 *
 * {@link #sendClaimedNotification(Context, String, String)} schedules a broadcast to
 * {@link com.uir.lostfound.ClaimNotificationReceiver} via AlarmManager (fires in ~1 second).
 *
 * Ownership: Mona.
 */
public class NotificationHelper {

    public static final String CHANNEL_ID   = "claim_channel";
    public static final String CHANNEL_NAME = "Claim Notifications";

    /**
     * Registers the claim notification channel with the system.
     * Required on API 26+; no-op on earlier versions.
     */
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

    /**
     * Schedules a broadcast to {@link com.uir.lostfound.ClaimNotificationReceiver}
     * via AlarmManager (RTC_WAKEUP, fires in 1 second).
     *
     * @param context   Android context (Activity or Application).
     * @param ownerName name of the item's owner, shown in the notification body.
     * @param itemTitle title of the item that was claimed.
     */
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