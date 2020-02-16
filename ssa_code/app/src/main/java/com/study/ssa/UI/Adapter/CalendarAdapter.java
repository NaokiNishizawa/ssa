package com.study.ssa.UI.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.study.ssa.DateManager;
import com.study.ssa.R;
import com.study.ssa.SsaSchedule.SsaScheduleManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * カレンダーのAdapterクラス
 */
public class CalendarAdapter extends BaseAdapter {
    private List<Date> dateArray = new ArrayList<>();
    private Context mContext;
    private DateManager mDateManager;
    private LayoutInflater mLayoutInflater;

    private SsaScheduleManager mManager;

    //カスタムセルを拡張したらここでWigetを定義
    private static class ViewHolder {
        public TextView dateText;
        public ImageView todayMark;

        public ImageView alertMark;
        public TextView alertCount;

        public ImageView timerMark;
        public TextView timerCount;
    }

    public
    CalendarAdapter(Context context){
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mDateManager = new DateManager();
        dateArray = mDateManager.getDays();
        mManager = SsaScheduleManager.getInstance();
    }

    @Override
    public int getCount() {
        return dateArray.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.calendar_cell, null);
            holder = new ViewHolder();
            holder.dateText = convertView.findViewById(R.id.dateText);
            holder.todayMark = convertView.findViewById(R.id.today_mark_icon);

            holder.alertMark = convertView.findViewById(R.id.alert_icon);
            holder.alertCount = convertView.findViewById(R.id.alert_count_text);

            holder.timerMark = convertView.findViewById(R.id.timer_icon);
            holder.timerCount = convertView.findViewById(R.id.timer_count_text);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        //セルのサイズを指定
        float dp = mContext.getResources().getDisplayMetrics().density;
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(parent.getWidth()/7 - (int)dp, (parent.getHeight() - (int)dp * mDateManager.getWeeks() ) / mDateManager.getWeeks());
        convertView.setLayoutParams(params);

        //日付のみ表示させる
        SimpleDateFormat dateFormat = new SimpleDateFormat("d", Locale.US);
        holder.dateText.setText(dateFormat.format(dateArray.get(position)));

        //当月以外のセルをグレーアウト
        if (mDateManager.isCurrentMonth(dateArray.get(position))){
            convertView.setBackgroundColor(Color.WHITE);
        }else {
            convertView.setBackgroundColor(Color.LTGRAY);
        }

        //日曜日を赤、土曜日を青に
        int colorId;
        switch (mDateManager.getDayOfWeek(dateArray.get(position))){
            case 1:
                colorId = Color.RED;
                break;
            case 7:
                colorId = Color.BLUE;
                break;

            default:
                colorId = Color.BLACK;
                break;
        }
        holder.dateText.setTextColor(colorId);

        // 当日の場合は、当日マークを表示する
        if(mDateManager.isToday(dateArray.get(position))) {
            holder.todayMark.setVisibility(View.VISIBLE);
        } else {
            holder.todayMark.setVisibility(View.GONE);
        }

        // アラートに設定されている予定数を取得して１個以上ならアイコンとテキストを表示する
        int alertCount = mManager.getAlertScheduleCount(dateArray.get(position));
        if(0 != alertCount) {
            // 1個以上ある場合はアイコンとテキストを表示する
            holder.alertMark.setVisibility(View.VISIBLE);
            holder.alertCount.setVisibility(View.VISIBLE);
            holder.alertCount.setText(String.valueOf(alertCount));
        } else {
            // 0の場合はアイコンとテキストを非表示
            holder.alertMark.setVisibility(View.INVISIBLE);
            holder.alertCount.setVisibility(View.INVISIBLE);
        }

        // タイマーに設定されている予定数を取得して１個以上ならアイコンとテキストを表示する
        int timerCount = mManager.getTimerScheduleCount(dateArray.get(position));
        if(0 != timerCount) {
            // 1個以上ある場合はアイコンとテキストを表示する
            holder.timerMark.setVisibility(View.VISIBLE);
            holder.timerCount.setVisibility(View.VISIBLE);
            holder.timerCount.setText(String.valueOf(timerCount));
        } else {
            // 0の場合はアイコンとテキストを非表示
            holder.timerMark.setVisibility(View.INVISIBLE);
            holder.timerCount.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    /**
     * positionに設定されている日付を返す
     * @param position
     * @return 日付 yyyy-MM-dd
     */
    public String getItemDateText(int position) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return format.format(dateArray.get(position));
    }

    /**
     * positionに設定されている日付を返す
     *
     * @param position
     * @return 日付
     */
    public Date getItemDate(int position) {
        return dateArray.get(position);
    }

    //表示月を取得
    public String getTitle(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM", Locale.US);
        return format.format(mDateManager.mCalendar.getTime());
    }

    //翌月表示
    public void nextMonth(){
        mDateManager.nextMonth();
        dateArray = mDateManager.getDays();
        this.notifyDataSetChanged();
    }

    //前月表示
    public void prevMonth(){
        mDateManager.prevMonth();
        dateArray = mDateManager.getDays();
        this.notifyDataSetChanged();
    }
}
