package com.study.ssa.SsaSchedule;

import java.io.Serializable;
import java.util.Date;

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
                (mEnd.equals(obj.getEnd())) && (mContent.equals(obj.getContent()))) {
            result = true;
        }

        return result;
    }
}
