package com.jake.reviewquest;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static com.jake.reviewquest.MainActivity.CONATCT_NAME_EXTRA;
import static com.jake.reviewquest.MainActivity.NOTE_CHANNEL_ID_SENT;
import static com.jake.reviewquest.MainActivity.PHONE_NUM_EXTRA;
import static com.jake.reviewquest.MainActivity.RANDOM_INT_EXTRA;
import static com.jake.reviewquest.MainActivity.SENT;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //String phoneNumber = intent.getStringExtra(PHONE_NUM_EXTRA);
       // String  contactName = intent.getStringExtra(CONATCT_NAME_EXTRA);
        int randomNumber = intent.getIntExtra(RANDOM_INT_EXTRA, -1);
        Intent notifyIntent = new Intent(context, MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        if(intent.getAction().equals(SENT))
        {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTE_CHANNEL_ID_SENT);
            builder.setSmallIcon(R.drawable.ic_notification_icon)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            Log.i("NOTIFICATION RECEIVER", "NOTIFICATION POPPED UP");
            if(getResultCode() == Activity.RESULT_OK) {
                builder.setContentTitle("Review Quest")
                        .setContentText("Tap here to return to Review Quest");
//                        .setStyle(new NotificationCompat.BigTextStyle()
//                                .bigText("Tap here to return to Review Quest"));
            }
//            else {
//                builder.setContentTitle("Review Request Failed to Sent to " + contactName)
//                        .setStyle(new NotificationCompat.BigTextStyle()
//                                .bigText("Message Failed Sent to " + contactName + " - " + phoneNumber + " is an Invalid Phone Number"));
//            }
            builder.setContentIntent(pendingIntent);
            builder.setAutoCancel(true);
            notificationManager.notify(randomNumber, builder.build());
        }
    }
}