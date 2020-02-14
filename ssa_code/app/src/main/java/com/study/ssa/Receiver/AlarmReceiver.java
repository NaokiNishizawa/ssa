package com.study.ssa.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import com.study.ssa.SsaSchedule.SsaSchedule;
import com.study.ssa.SsaSchedule.SsaScheduleManager;

/**
 * Alarmによる通知を受け取るReceiver
 */
public class AlarmReceiver extends BroadcastReceiver {
    public static final String KEY_CALLED_RECEIVER = "called_receiver";
    public static final String KEY_SCHEDULE_OBJECT = "schedule";
    public static final boolean CALLED_RECEIVER = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        // 今回の対象のScheduleを取得する
        SsaScheduleManager manager = SsaScheduleManager.getInstance();
        manager.init(context);
        SsaSchedule schedule = manager.getNextSchedule();
        if(null == schedule) {
            // 本来ありえないパス
            return;
        }

        if(SsaSchedule.MODE_ALERT == schedule.getMode()) {
            // TODO 通知処理

        } else if(SsaSchedule.MODE_TIMER == schedule.getMode()) {
            // 画面状態を確認し、画面がONの時はアプリを起動する
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if(pm.isInteractive()) {
                // 画面ON時
                Log.d("debug", "SCREEN_ON");

                // 必要な情報を詰める
                Intent startIntent =new Intent();
                startIntent.setClassName(context.getPackageName(), context.getPackageName() + ".MainActivity");
                startIntent.putExtra(KEY_CALLED_RECEIVER, CALLED_RECEIVER);
                startIntent.putExtra(KEY_SCHEDULE_OBJECT, schedule);

                // このフラグがないとMain画面が起動できない
                startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                context.startActivity(startIntent);

            } else {
                // 画面OFF時
                Log.d("debug", "SCREEN_OFF");
                return;
            }
        } else {
            // 何もしない
            return;
        }

        // もう直近のscheduleは用済みなので削除する
        manager.deleteSchedule(context, schedule);
    }
}
