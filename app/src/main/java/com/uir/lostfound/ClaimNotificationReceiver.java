package com.uir.lostfound;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;

import com.uir.lostfound.utils.NotificationHelper;

/**
 * ClaimNotificationReceiver — BroadcastReceiver that fires a local notification
 * when an item is claimed.
 *
 * Triggered by {@link com.uir.lostfound.utils.NotificationHelper#sendClaimedNotification}
 * via AlarmManager. Reads the owner name and item title from the Intent extras
 * and posts a notification on the {@code claim_channel} channel.
 *
 * Ownership: Mona.
 */
public class ClaimNotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String ownerName = intent.getStringExtra("owner_name");
        String itemTitle = intent.getStringExtra("item_title");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context, NotificationHelper.CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Votre item a été réclamé !")
                .setContentText(ownerName + ", quelqu'un a réclamé : " + itemTitle)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(1001, builder.build());
        }
    }
}