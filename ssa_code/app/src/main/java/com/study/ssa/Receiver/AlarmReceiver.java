package com.study.ssa.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.study.ssa.MainActivity;

/**
 * Alarmによる通知を受け取るReceiver
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO 処理
        Intent startIntent =new Intent();
        intent.setClassName(context.getPackageName(), context.getPackageName() + ".MainActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
