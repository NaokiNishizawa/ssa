package com.study.ssa.UI.Adapter;

import android.app.TimePickerDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

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

    private Context mContext;
    private List<SsaSchedule> mList;
    private onRemoveScheduleListener mListener;

    public interface onRemoveScheduleListener {
        public void doRemoveSchedule();
    }

    /**
     * コンストラクタ
     *
     * @param context
     * @param list
     * @param listener
     */
    public ScheduleListAdapter(Context context, List<SsaSchedule> list, onRemoveScheduleListener listener)  {
        mContext = context;
        mList = list;
        mListener = listener;
    }

    @Override
    public ScheduleListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_list_dialog_row, parent,false);
        ScheduleListViewHolder vh = new ScheduleListViewHolder(inflate);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ScheduleListViewHolder holder, int position) {
        SsaSchedule item = mList.get(position);
        holder.mSchedule = item;
        holder.mDayView.setText(item.getSchedule());
        holder.mContentView.setText(item.getContent());
        holder.mContentView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 入力規則などはないため、今は何もしない
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 入力規則などはないため、今は何もしない
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 入力された内容をデータクラスに即時反映
                holder.mSchedule.setContent(s.toString());
            }
        });

        holder.mStartTimeView.setText(item.getStart().split(" ")[1]);
        holder.mStartTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] time = holder.mStartTimeView.getText().toString().split(":");
                final int hour = Integer.valueOf(time[0]);
                int min = Integer.valueOf(time[1]);

                TimePickerDialog dialog = new TimePickerDialog(
                        mContext,
                        new TimePickerDialog.OnTimeSetListener(){
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                holder.mStartTimeView.setText(String.format("%02d:%02d", hourOfDay, minute));
                                Log.d("debug", holder.mStartTimeView.getText().toString());

                                // Scheduleクラスに保存
                                holder.mSchedule.setStart((holder.mDayView.getText().toString() + " " + holder.mStartTimeView.getText().toString()));
                            }
                        },
                        hour,min,true);
                dialog.show();
            }
        });

        holder.mEndTimeView.setText(item.getEnd().split(" ")[1]);
        holder.mEndTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] time = holder.mEndTimeView.getText().toString().split(":");
                final int hour = Integer.valueOf(time[0]);
                int min = Integer.valueOf(time[1]);

                TimePickerDialog dialog = new TimePickerDialog(
                        mContext,
                        new TimePickerDialog.OnTimeSetListener(){
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                holder.mEndTimeView.setText(String.format("%02d:%02d", hourOfDay, minute));
                                Log.d("debug", holder.mEndTimeView.getText().toString());

                                // Scheduleクラスに保存
                                holder.mSchedule.setEnd((holder.mDayView.getText().toString() + " " + holder.mEndTimeView.getText().toString()));
                                notifyDataSetChanged();
                            }
                        },
                        hour,min,true);
                dialog.show();
            }
        });

        // モードにあったアイコンボタンを表示する
        if(SsaSchedule.MODE_ALERT == item.getMode()) {
            holder.mAlertImage.setVisibility(View.VISIBLE);
            holder.mAlertImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // タイマーモードに変更
                    holder.mSchedule.setMode(SsaSchedule.MODE_TIMER);
                    notifyDataSetChanged();
                }
            });
            holder.mTimerImage.setVisibility(View.GONE);
        } else if(SsaSchedule.MODE_TIMER == item.getMode()) {
            holder.mAlertImage.setVisibility(View.GONE);
            holder.mTimerImage.setVisibility(View.VISIBLE);
            holder.mTimerImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // アラートモードに変更
                    holder.mSchedule.setMode(SsaSchedule.MODE_ALERT);
                    notifyDataSetChanged();
                }
            });
        } else {
            // 何もしない
        }

        holder.mRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 予定を削除
                SsaScheduleManager manager = SsaScheduleManager.getInstance();
                manager.deleteScheduleAndReadDB(mContext, holder.mSchedule);

                mList.remove(holder.mSchedule);
                mListener.doRemoveSchedule();
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public List<SsaSchedule> getList() {
        return mList;
    }
}
