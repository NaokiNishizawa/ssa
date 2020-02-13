package com.study.ssa.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

/**
 * Alarmによる通知を受け取るReceiver
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // 処理
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if(pm.isInteractive()) {
            // 画面ON時
            Log.d("debug", "SCREEN_ON");
        } else {
            // 画面OFF時
            Log.d("debug", "SCREEN_OFF");
        }

        Intent startIntent =new Intent();
        startIntent.setClassName(context.getPackageName(), context.getPackageName() + ".MainActivity");
        startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(startIntent);
    }
}
