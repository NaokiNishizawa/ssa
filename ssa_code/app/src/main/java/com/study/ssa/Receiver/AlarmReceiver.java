package com.study.ssa.Receiver;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import com.study.ssa.UI.MainActivity;
import com.study.ssa.SsaSchedule.SsaSchedule;
import com.study.ssa.SsaSchedule.SsaScheduleManager;
import com.study.ssa.Util.NotificationUtil;

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
        SsaSchedule schedule = manager.getNextScheduleIncludeNotificationItem();
        if(null == schedule) {
            // 本来ありえないパス
            return;
        }

        if((SsaSchedule.MODE_ALERT == schedule.getMode()) || (SsaSchedule.MODE_NOTIFICATION == schedule.getMode())) {
            // 通知
            Intent mainActivityIntent = new Intent(context, MainActivity.class);
            mainActivityIntent.setFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP  // 起動中のアプリがあってもこちらを優先する
                            | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED  // 起動中のアプリがあってもこちらを優先する
            );

            PendingIntent contentIntent =
                    PendingIntent.getActivity(
                            context,
                            0,
                            mainActivityIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

            // 通知を発行
            NotificationUtil.notice(context, schedule, contentIntent);

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
                startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

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
