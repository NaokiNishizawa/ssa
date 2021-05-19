package com.study.ssa.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.study.ssa.Util.AlarmManagerUtil;
import com.study.ssa.SsaSchedule.SsaSchedule;
import com.study.ssa.SsaSchedule.SsaScheduleManager;

import java.util.List;

/**
 * 端末再起動時に発行されるReceiver<br>
 * AlarmManagerに設定した内容が再起動によって揮発してしまうので、再設定する
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("debug", "call ssa BootReceiver");

        // 再起動時はアラームを再設定
        SsaScheduleManager manager = SsaScheduleManager.getInstance();
        manager.init(context);
        List<SsaSchedule> list = manager.getAllSchedule();

        if(0 == list.size()) {
            return;
        }

        for(int i=0; i < list.size(); i++) {
            AlarmManagerUtil.setAlarm(context, list.get(i));
        }
    }
}
