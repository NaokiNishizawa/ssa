package com.study.ssa.UI.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.study.ssa.R;
import com.study.ssa.SsaSchedule.SsaSchedule;
import com.study.ssa.SsaSchedule.SsaScheduleManager;
import com.study.ssa.UI.ViewHolder.ScheduleListViewHolder;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Shedule ListのAdapterクラス
 */
public class ScheduleListAdapter extends RecyclerView.Adapter<ScheduleListViewHolder> {

    private List<SsaSchedule> mList;
    private SsaSchedule mItem;

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
        mItem = mList.get(position);
        holder.mDayView.setText(mItem.getSchedule());
        holder.mContentView.setText(mItem.getContent());

        holder.mStartTimeView.setText(mItem.getStart().split(" ")[1]);
        holder.mEndTimeView.setText(mItem.getEnd().split(" ")[1]);

        // モードにあったアイコンボタンを表示する
        if(SsaSchedule.MODE_ALERT == mItem.getMode()) {
            holder.mAlertImage.setVisibility(View.VISIBLE);
            holder.mTimerImage.setVisibility(View.GONE);
        } else {
            holder.mAlertImage.setVisibility(View.GONE);
            holder.mTimerImage.setVisibility(View.VISIBLE);
        }

        holder.mRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 予定を削除
                SsaScheduleManager manager = SsaScheduleManager.getInstance();
                manager.deleteScheduleAndReadDB(mItem);

                mList.remove(mItem);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
