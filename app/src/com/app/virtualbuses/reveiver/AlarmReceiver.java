package com.app.virtualbuses.reveiver;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Created by tasol on 9/5/17.
 */

public class AlarmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {

        Log.v("@@@WWE", "From Reciever");
        String msg = intent.getStringExtra("IN_TIME");
        Log.v("@@@WWE", " Message from Reciever" + msg);

        if(msg.equals("start")){
            ComponentName comp = new ComponentName(context.getPackageName(),
                    AlarmService.class.getName());
            startWakefulService(context, (intent.setComponent(comp)));
            setResultCode(Activity.RESULT_OK);
        }

    }
}
