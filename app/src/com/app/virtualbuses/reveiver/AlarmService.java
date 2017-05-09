package com.app.virtualbuses.reveiver;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.app.virtualbuses.VirtualBusesRoutes;
import com.smartprime.R;

/**
 * Created by tasol on 8/5/17.
 */

public class AlarmService extends IntentService {
    private NotificationManager alarmNotificationManager;

    public AlarmService(String name) {
        super("AlarmService");
    }
    public AlarmService() {
        super("");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String msg = intent.getStringExtra("IN_TIME");
        sendNotification("Remainder set "+msg);
    }

    private void sendNotification(String msg) {
        Log.d("AlarmService", "Preparing to send notification...: " + msg);
        alarmNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, VirtualBusesRoutes.class), 0);
        NotificationCompat.Builder alamNotificationBuilder = new NotificationCompat.Builder(
                this).setContentTitle("Alarm").setSmallIcon(R.mipmap.bus_app_icon)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg);
        alamNotificationBuilder.setContentIntent(contentIntent);

        //For vibration
        long[] v={500,1000};
        alamNotificationBuilder.setVibrate(v);

        //For Ring
//        Uri  uri=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Uri  uri=Uri.parse("android.resource://"+AlarmService.this.getPackageName()+"/"+R.raw.bus_horn);

        alamNotificationBuilder.setSound(uri);


        alarmNotificationManager.notify(1, alamNotificationBuilder.build());
        Log.d("AlarmService", "Notification sent.");
    }
}
