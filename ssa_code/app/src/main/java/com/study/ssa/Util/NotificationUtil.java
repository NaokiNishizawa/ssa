package com.study.ssa.Util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import com.study.ssa.R;
import com.study.ssa.SsaSchedule.SsaSchedule;

import androidx.core.app.NotificationCompat;

/**
 * Notification操作便利クラス
 */
public class NotificationUtil {

    private static final String CHANNEL_ID = "ssa_channel_id";

    /**
     * 通知を発行する
     *
     * @param context
     * @param schedule
     * @param intent
     */
    public static void notice(Context context, SsaSchedule schedule, PendingIntent intent) {
        if(null == schedule) {
            return;
        }

        createNotificationChannel(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[]{0, 500, 250, 500, 250})
                .setLights(Color.GREEN,2000,1000)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(intent)
                .setAutoCancel(true);

        // アラームモードの場合は"「予定内容」の開始時間です"という文言に修正して通知する
        String title = schedule.getContent();
        if(SsaSchedule.MODE_ALERT == schedule.getMode()) {
            title = "「" + title + "」の開始時刻です";
        }
        builder.setContentTitle(title);

        NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, builder.build());
    }

    /**
     * 通知チャンネル作成処理
     * @param context
     */
    private static void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH; // 高

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLightColor(Color.GREEN);
            Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            channel.setSound(uri, null);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager =context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
