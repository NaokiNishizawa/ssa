package com.study.ssa;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.study.ssa.SsaSchedule.SsaSchedule;
import com.study.ssa.SsaSchedule.SsaScheduleManager;

import java.text.SimpleDateFormat;

import androidx.fragment.app.Fragment;

/**
 * 予定表示用Fragment
 */
public class DisplayScheduleFragment extends Fragment {

    private View mLayout;
    private SsaScheduleManager mManager;
    private SsaSchedule mDisplaySchedule;
    private TextView mNothingScheduleText;
    private LinearLayout mDisplayScheduleLayout;

    // 予定ありの場合に必要なメンバ変数群
    private TextView mDateText;
    private TextView mScopeText;
    private TextView mContentText;
    private TextView mModeText;
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // 引数で渡ってきたinflaterを使ってレイアウトをinfrateする
        mLayout = inflater.inflate(R.layout.fragment_display_schedule, container);

        init();

        // infrateされたViewをそのまま返す
        return mLayout;
    }

    /**
     * 初期化
     */
    private void init() {
        mManager = SsaScheduleManager.getInstance();
        mManager.init(getContext());

        mNothingScheduleText = mLayout.findViewById(R.id.nothing_schedule_text);
        mDisplayScheduleLayout = mLayout.findViewById(R.id.display_schedule_layout);

        mDateText = mLayout.findViewById(R.id.display_schedule_date_text);
        mScopeText = mLayout.findViewById(R.id.display_schedule_date_scope);
        mContentText = mLayout.findViewById(R.id.display_schedule_date_content);
        mModeText = mLayout.findViewById(R.id.display_schedule_date_mode);

        updateLayout();
    }

    /**
     * レイアウトの更新
     */
    public void updateLayout() {
        SsaSchedule schedule = mManager.getNextSchedule();
        if((null != schedule) && (null != mDisplaySchedule)) {
            if(mDisplaySchedule.equals(schedule)) {
                // 更新不要
                mDisplaySchedule = schedule;
                return;
            }
        }

        mDisplaySchedule = schedule;
        // 不要なレイアウトを非表示にする
        if(null == mDisplaySchedule) {
            // 予定なし
            mNothingScheduleText.setVisibility(View.VISIBLE);
            mDisplayScheduleLayout.setVisibility(View.GONE);
        } else {
            // 予定あり
            mNothingScheduleText.setVisibility(View.GONE);
            mDisplayScheduleLayout.setVisibility(View.VISIBLE);


            String[] date = mDisplaySchedule.getSchedule().split("-");
            int month = Integer.valueOf(date[1]);
            int day = Integer.valueOf(date[2]);
            String dateStr = String.valueOf(month) + "月" + String.valueOf(day) + "日";
            mDateText.setText(dateStr);

            String scopeStr = mDisplaySchedule.getStart().split(" ")[1] + "分 ~ " + mDisplaySchedule.getEnd().split(" ")[1] + "分";
            mScopeText.setText(scopeStr);

            mContentText.setText(mDisplaySchedule.getContent());

            String modeStr = "";
            if(SsaSchedule.MODE_ALERT == mDisplaySchedule.getMode()) {
                modeStr = "現在 : アラートモード";
            } else if(SsaSchedule.MODE_TIMER == mDisplaySchedule.getMode()) {
                modeStr = "現在 : タイマーモード";
            }

            mModeText.setText(modeStr);
        }
    }
}
