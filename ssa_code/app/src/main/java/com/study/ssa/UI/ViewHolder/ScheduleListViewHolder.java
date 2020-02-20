package com.study.ssa.UI.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.study.ssa.R;
import com.study.ssa.SsaSchedule.SsaSchedule;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Schedule ListのViewHolderクラス
 */
public class ScheduleListViewHolder extends RecyclerView.ViewHolder{

    public SsaSchedule mSchedule;
    public TextView mDayView;
    public EditText mContentView;
    public TextView mStartTimeView;
    public TextView mEndTimeView;
    public ImageView mAlertImage;
    public ImageView mTimerImage;
    public Button mRemoveButton;
    /**
     * コンストラクタ
     * @param itemView
     */
    public ScheduleListViewHolder(View itemView) {
        super(itemView);

        mDayView = itemView.findViewById(R.id.schedule_list_day);
        mContentView = itemView.findViewById(R.id.schedule_list_content);
        mStartTimeView = itemView.findViewById(R.id.schedule_start_time_text);
        mEndTimeView = itemView.findViewById(R.id.schedule_end_time_text);
        mAlertImage = itemView.findViewById(R.id.schedule_alert_icon);
        mTimerImage = itemView.findViewById(R.id.schedule_timer_icon);
        mRemoveButton = itemView.findViewById(R.id.remove_button);
    }
}
