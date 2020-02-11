package com.study.ssa.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.study.ssa.R;
import com.study.ssa.SsaSchedule.SsaSchedule;
import com.study.ssa.ViewHolder.ScheduleListViewHolder;

import java.text.SimpleDateFormat;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Shedule ListのAdapterクラス
 */
public class ScheduleListAdapter extends RecyclerView.Adapter<ScheduleListViewHolder> {

    private List<SsaSchedule> mList;

    /**
     * コンストラクタ
     * @param list
     */
    public ScheduleListAdapter(List<SsaSchedule> list) {
        mList = list;
    }

    @Override
    public ScheduleListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_list_dialog_row, parent,false);
        ScheduleListViewHolder vh = new ScheduleListViewHolder(inflate);
        return vh;
    }

    @Override
    public void onBindViewHolder(ScheduleListViewHolder holder, int position) {
        SsaSchedule item = mList.get(position);
        holder.mDayView.setText(item.getSchedule());
        holder.mContentView.setText(item.getContent());

        holder.mStartTimeView.setText(item.getStart().split(" ")[1]);
        holder.mEndTimeView.setText(item.getEnd().split(" ")[1]);

        // モードにあったアイコンボタンを表示する
        if(SsaSchedule.MODE_ALERT == item.getMode()) {
            holder.mAlertImage.setVisibility(View.VISIBLE);
            holder.mTimerImage.setVisibility(View.GONE);
        } else {
            holder.mAlertImage.setVisibility(View.GONE);
            holder.mTimerImage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
