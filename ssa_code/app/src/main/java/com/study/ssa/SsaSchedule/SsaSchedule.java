package com.study.ssa.SsaSchedule;

import android.util.Log;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.Nullable;

/**
 * スケジュールクラス<br>
 * 予定一つに対して一つの本クラスが対応する
 */
public class SsaSchedule implements Serializable {

    /** モード：未選択 */
    public static final int MODE_NOTHING = 0;
    /** モード：アラート */
    public static final int MODE_ALERT= 1;
    /** モード：タイマー */
    public static final int MODE_TIMER = 2;
    /** モード：通知 */
    public static final int MODE_NOTIFICATION = 3;

    /** intent key id */
    public static final String INTENT_KEY_ID = "id";
    /** intent key schedule */
    public static final String INTENT_KEY_SCHEDULE = "schedule";
    /** intent key start */
    public static final String INTENT_KEY_START = "start";
    /** intent key end */
    public static final String INTENT_KEY_END = "end";
    /** intent key  content*/
    public static final String INTENT_KEY_CONTENT = "content";
    /** intent key mode */
    public static final String INTENT_KEY_MODE = "mode";

    private int mID; // ID
    private String mSchedule; // 予定日
    private String mStart; // 開始時間
    private String mEnd; // 終了時間
    private String mContent; // 内容
    private int mMode = MODE_NOTHING; // モード

    // 各種セッター・ゲッター

    public int getID() {
        return mID;
    }

    public void setID(int id) {
        this.mID = id;
    }

    public String getSchedule() {
        return mSchedule;
    }

    public void setSchedule(String schedule) {
        this.mSchedule = schedule;
    }

    public String getStart() {
        return mStart;
    }

    public void setStart(String start) {
        this.mStart = start;
    }

    public String getEnd() {
        return mEnd;
    }

    public void setEnd(String end) {
        this.mEnd = end;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String mContent) {
        this.mContent = mContent;
    }

    public int getMode() {
        return mMode;
    }

    public void setMode(int mMode) {
        this.mMode = mMode;
    }

    public SsaSchedule clone() {
        SsaSchedule clone = new SsaSchedule();

        clone.setID(mID);
        clone.setSchedule(mSchedule);
        clone.setStart(mStart);
        clone.setEnd(mEnd);
        clone.setContent(mContent);
        clone.setMode(mMode);

        return clone;
    }

    public boolean equals(SsaSchedule obj) {
        boolean result = false;

        if((mSchedule.equals(obj.getSchedule())) && (mStart.equals(obj.getStart())) &&
                (mEnd.equals(obj.getEnd())) && (mContent.equals(obj.getContent())) && (mMode == obj.getMode())) {
            result = true;
        }

        return result;
    }

    /**
     * 比較 第一引数の日付が第二引数の日付に対して過去・現在・未来の日付かを返す
     *
     * @param beforeStr
     * @param afterStr
     * @return -1 過去の日付 / 0 等しい / 1　未来の日付 / -100 エラー
     */
    public int compareTo(String beforeStr, String afterStr) {
        int result = -100;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            Date before = format.parse(beforeStr);
            Date after = format.parse(afterStr);

            result = after.compareTo(before);

            if(0 == result) {
                // 時間で比較
                int beforeHour = Integer.valueOf(beforeStr.split(" ")[1].split(":")[0]);
                int beforeMin = Integer.valueOf(beforeStr.split(" ")[1].split(":")[1]);
                int afterHour = Integer.valueOf(afterStr.split(" ")[1].split(":")[0]);
                int afterMin = Integer.valueOf(afterStr.split(" ")[1].split(":")[1]);

                if(beforeHour < afterHour) {
                    result = 1;
                } else if(beforeHour > afterHour) {
                    result = -1;
                } else {
                    // hourが等しい時はminで判断
                    if(beforeMin < afterMin) {
                        result = 1;
                    } else if(beforeMin > afterMin) {
                        result = -1;
                    } else {
                        // 全く同じ時間
                        result = 0;
                    }
                }
            }
        } catch (ParseException e) {
            return result;
        }

        return  result;
    }

    /*
    // debug テストコード
    public void compareToTest() {
        Log.d("debug", "***** compareToTest Start *****");
        int result;

        // 年違い
        String beforeStr = "2020-02-15 10:10";
        String afterStr = "2021-02-15 10:10";
        result = compareTo(beforeStr, afterStr);
        Log.d("debug", "年違い　期待値:1　結果:" + String.valueOf(result));
        result = compareTo(afterStr, beforeStr);
        Log.d("debug", "年違い　期待値:-1　結果:" + String.valueOf(result));

        // 日違い
        beforeStr = "2020-02-15 10:10";
        afterStr = "2020-02-16 10:10";
        result = compareTo(beforeStr, afterStr);
        Log.d("debug", "日違い 期待値:1 結果:" + String.valueOf(result));
        result = compareTo(afterStr, beforeStr);
        Log.d("debug", "日違い 期待値:-1 結果:" + String.valueOf(result));

        // 時間違い hour
        beforeStr = "2020-02-15 10:10";
        afterStr = "2020-02-15 11:10";
        result = compareTo(beforeStr, afterStr);
        Log.d("debug", "時間違い hour 期待値:1 結果:" + String.valueOf(result));
        result = compareTo(afterStr, beforeStr);
        Log.d("debug", "時間違い hour 期待値:-1 結果:" + String.valueOf(result));

        // 時間違い min
        beforeStr = "2020-02-15 10:10";
        afterStr = "2020-02-15 10:11";
        result = compareTo(beforeStr, afterStr);
        Log.d("debug", "時間違い min 期待値:1 結果:" + String.valueOf(result));
        result = compareTo(afterStr, beforeStr);
        Log.d("debug", "時間違い min 期待値:-1 結果:" + String.valueOf(result));

        // 等しい
        beforeStr = "2020-02-15 10:10";
        afterStr = "2020-02-15 10:10";
        result = compareTo(beforeStr, afterStr);
        Log.d("debug", "等しい 期待値:0 結果:" + String.valueOf(result));
    }
    */
}
