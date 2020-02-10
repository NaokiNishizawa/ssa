package com.study.ssa.SsaSchedule;

import java.util.Date;

/**
 * スケジュールクラス<br>
 * 予定一つに対して一つの本クラスが対応する
 */
public class SsaSchedule {

    private Date mSchedule; // 予定日
    private Date mStart; // 開始時間
    private Date mEnd; // 終了時間
    private String mContent; // 内容
    private int mMode; // モード

    // 各種セッター・ゲッター
    public Date getSchedule() {
        return mSchedule;
    }

    public void setSchedule(Date mSchedule) {
        this.mSchedule = mSchedule;
    }

    public Date getStart() {
        return mStart;
    }

    public void setStart(Date mStart) {
        this.mStart = mStart;
    }

    public Date getEnd() {
        return mEnd;
    }

    public void setEnd(Date mEnd) {
        this.mEnd = mEnd;
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
