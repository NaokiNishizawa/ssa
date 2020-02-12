package com.study.ssa;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.study.ssa.Receiver.AlarmReceiver;
import com.study.ssa.SsaSchedule.SsaSchedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * AlarmManagerへの設定などを行う便利クラス
 */
public class AlarmManagerUtil {

    /**
     * アラーム設定
     * @param context コンテキスト
     * @param schedule　登録するスケジュール
     */
    public static void setAlarm(Context context, SsaSchedule schedule) {
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        if(null == alarmMgr) {
            return;
        }

        // intentに必要な情報を詰める
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setType(schedule.getStart() + schedule.getEnd());
        intent.putExtra(SsaSchedule.INTENT_KEY_ID, schedule.getID());
        intent.putExtra(SsaSchedule.INTENT_KEY_SCHEDULE, schedule.getSchedule());
        intent.putExtra(SsaSchedule.INTENT_KEY_START, schedule.getStart());
        intent.putExtra(SsaSchedule.INTENT_KEY_END, schedule.getEnd());
        intent.putExtra(SsaSchedule.INTENT_KEY_CONTENT, schedule.getContent());
        intent.putExtra(SsaSchedule.INTENT_KEY_MODE, schedule.getMode());

        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        // アラーム送信時間
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date;
        try {
            date = format.parse(schedule.getStart());
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }

        // Date → Calendar
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // Dozeモードに入っていてもsetAlarmClockすることで眠りから起こすことができる。
        alarmMgr.setAlarmClock(new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), null), alarmIntent);
    }
}
