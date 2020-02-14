package com.study.ssa.UI.Dialog;

import android.view.View;
import android.widget.TextView;

import com.study.ssa.R;

/**
 * カウントアップ用 タイマーダイアログ
 */
public class CountUpTimerDialogFragment extends BaseTimerDialogFragment {

    private int mHour;
    private int mMin;
    private int mSec;

    @Override
    protected void initChildLayout() {
        // 初期化処理

        // プログレスバーは不要なので関連UIを非表示
        mProgressRateText.setVisibility(View.INVISIBLE);
        mChartView.setVisibility(View.INVISIBLE);

        TextView titleText = getDialog().findViewById(R.id.remaining_time_text);
        titleText.setText(R.string.progress_time);

        mHour = 0;
        mMin = 0;
        mSec = 0;
    }

    @Override
    protected void countFinish() {
        addSec();
    }

    /**
     * 秒追加
     */
    private void addSec() {
        mSec++;
        if(0 == (mSec % 60)) {
            // １分
            addMin();
            mSec = 0;
        }

        // テキスト更新処理
        UpdateTime(mHour, mMin, mSec);
    }

    /**
     * 分追加
     */
    private void addMin() {
        mMin++;
        if(0 == (mMin % 60)) {
            // 1時間
            addHour();
            mMin = 0;
        }
    }

    /**
     * 時追加
     */
    private void addHour() {
        mHour++;
    }
}
