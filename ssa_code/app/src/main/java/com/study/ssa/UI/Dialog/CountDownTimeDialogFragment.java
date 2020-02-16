package com.study.ssa.UI.Dialog;

import android.app.Dialog;
import android.os.Bundle;

import com.study.ssa.SsaSchedule.SsaSchedule;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * カウントダウン用 タイマーダイアログ
 */
public class CountDownTimeDialogFragment extends BaseTimerDialogFragment {

    public static final String KEY_SCHEDULE = "schedule";

    private long mDayDiff;
    private int mHour;
    private int mMin;
    private int mSec;

    private SsaSchedule mSchedule;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mSchedule = (SsaSchedule) getArguments().getSerializable(KEY_SCHEDULE);

        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    protected void initChildLayout() {
        // 時間差を計算
        CalculateDaysDifference();
        updateTime(mHour, mMin, mSec);
    }

    @Override
    protected void countFinish() {
        if((0 == mHour) && (0 == mMin) && (0 == mSec)) {
            getDialog().dismiss();
            return;
        }

        // 残り時間を計算
        decreaseSec();
        CalculateProgressRate();
    }

    /**
     * 時間差を計算する
     */
    private void CalculateDaysDifference() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);

        try {
            // From
            Date from = format.parse(mSchedule.getStart());
            Date to = format.parse(mSchedule.getEnd());

            // 日付をlong値に変換します。
            long dateTimeTo = to.getTime();
            long dateTimeFrom = from.getTime();

            // 差分の時間を算出します。 秒
            mDayDiff = (dateTimeTo - dateTimeFrom) / 1000;

            mSec = (int) mDayDiff % 60;
            mMin = (int) (mDayDiff / 60) % 60;
            mHour = (int) (mDayDiff / 3600);

        } catch (ParseException e) {
            e.printStackTrace();

            // 何もできないのでダイアログを閉じる
            getDialog().dismiss();
        }
    }

    /**
     * 進捗率を計算
     */
    private void CalculateProgressRate() {
        double currentProgressTime = (mHour * 3600.0) + (mMin * 60.0) + mSec;

        // 精確な計算をするためにBigDecimalをしよう
        BigDecimal bdCurrentProgressTime = new BigDecimal(currentProgressTime);
        BigDecimal bdDayDiff = new BigDecimal(mDayDiff);
        double result = bdCurrentProgressTime.divide(bdDayDiff, 4, BigDecimal.ROUND_HALF_UP).doubleValue();

        int progress = 100 - (int) (result * 100);

        // 分母が大きいと計算上の四捨五入で終了間際で100になってしまうパターンが存在するため、
        // 本当に一致している時以外は99%表示とする
        if((100 == progress) && (bdCurrentProgressTime != bdDayDiff)) {
            progress = 99;
        }

        updateProgress(progress);
    }

    /**
     * 秒 減少
     */
    private void decreaseSec() {
        mSec--;
        if(-1 == mSec) {
            decreaseMin();
            mSec = 59;
        }

        // テキスト更新
        updateTime(mHour, mMin, mSec);
    }

    /**
     * 分 減少
     */
    private void decreaseMin() {
        mMin--;
        if(-1 == mMin) {
            decreaseHour();
            mMin = 59;
        }
    }

    /**
     * 時間 減少
     */
    private void decreaseHour() {
        mHour--;
        if(-1 == mHour) {
            // 何もしない
            mHour = 0;
        }
    }
}
