package com.study.ssa.SsaSchedule;

import java.util.Date;

/**
 * スケジュールクラス<br>
 * 予定一つに対して一つの本クラスが対応する
 */
public class SsaSchedule {

    /** モード：未選択 */
    public static final int MODE_NOTHING = 0;
    /** モード：アラート */
    public static final int MODE_ALERT= 1;
    /** モード：タイマー */
    public static final int MODE_TIMER = 2;


    private String mSchedule; // 予定日
    private String mStart; // 開始時間
    private String mEnd; // 終了時間
    private String mContent; // 内容
    private int mMode = MODE_NOTHING; // モード

    // 各種セッター・ゲッター
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
}
