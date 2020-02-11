package com.study.ssa.SsaSchedule;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.study.ssa.DB.SsaOpenHelper;

/**
 * 予定管理クラス
 * どこからでも仕様できるようにシングルトンで作成する
 */
public class SsaScheduleManager {

    private static SsaScheduleManager instance = null;

    /** 予定一覧 予定が早い順になっている*/
    private List<SsaSchedule> mSsaScheduleList = null;

    /* SQL操作クラス */
    private SsaOpenHelper mHelper;
    private SQLiteDatabase mDB;

    // 隠蔽コンストラクタ
    private SsaScheduleManager() {}

    public static SsaScheduleManager getInstance() {
        if (instance == null) {
            instance = new SsaScheduleManager();
        }
        return instance;
    }

    /**
     * 初期化
     * @param context
     */
    public void init(Context context) {
        initDB(context);
    }

    /**
     * DB初期化
     */
    private void initDB(Context context) {
        mHelper = new SsaOpenHelper(context);
        ReadDB();
    }

    /**
     * 引数でもらった日に登録されているアラームモードのスケジュール数を取得する
     * @param date
     * @return　アラームモードのスケジュール数
     */
    public int getAlertScheduleCount(Date date) {
        int count = 0;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String dateStr = format.format(date);

        for(SsaSchedule schedule: mSsaScheduleList) {
            if((dateStr.equals(schedule.getSchedule())) && (SsaSchedule.MODE_ALERT == schedule.getMode())) {
                count++;
            }
        }
        return count;
    }

    /**
     * 引数でもらった日に登録されているタイマーモードのスケジュール数を取得する
     * @param date
     * @return　タイマーモードのスケジュール数
     */
    public int getTimerScheduleCount(Date date) {
        int count = 0;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String dateStr = format.format(date);

        for(SsaSchedule schedule: mSsaScheduleList) {
            if((dateStr.equals(schedule.getSchedule())) && (SsaSchedule.MODE_TIMER == schedule.getMode())) {
                count++;
            }
        }
        return count;
    }

    /**
     * 予定追加
     * @param add
     */
    public void addSchedule(SsaSchedule add) {
        mSsaScheduleList.add(add);

        // DBへの書き込み
        ContentValues values = new ContentValues();
        values.put(SsaOpenHelper.COLUMN_NAME_SCHEDULE_DAY, add.getSchedule());
        values.put(SsaOpenHelper.COLUMN_NAME_START, add.getStart());
        values.put(SsaOpenHelper.COLUMN_NAME_END, add.getEnd());
        values.put(SsaOpenHelper.COLUMN_NAME_CONTENT, add.getContent());
        values.put(SsaOpenHelper.COLUMN_NAME_MODE, add.getMode());

        mDB = mHelper.getWritableDatabase();
        long ret;
        try {
            ret = mDB.insert(SsaOpenHelper.TABLE_NAME, null, values);
        } finally {
            mDB.close();
        }
        if (ret == -1) {
            Log.e("error", "insert failed");
        } else {
            Log.e("error", "insert success");
        }

        // 再取得
        ReadDB();
    }

    /**
     * DBから全データを読み込みmSsaScheduleListに追加する
     * mSsaScheduleListは予定が早い順に追加されている
     */
    private void ReadDB() {

        mDB = mHelper.getReadableDatabase();

        Log.d("debug","**********Cursor");

        Cursor cursor = mDB.query(
                SsaOpenHelper.TABLE_NAME,
                new String[] { SsaOpenHelper.COLUMN_NAME_SCHEDULE_DAY,
                        SsaOpenHelper.COLUMN_NAME_START,
                        SsaOpenHelper.COLUMN_NAME_END,
                        SsaOpenHelper.COLUMN_NAME_CONTENT,
                        SsaOpenHelper.COLUMN_NAME_MODE},
                null,
                null,
                null,
                null,
                null
        );

        cursor.moveToFirst();
        mSsaScheduleList = new ArrayList<SsaSchedule>();
        Log.d("debug","*********** SQL Parse Start ***********");
        for (int i = 0; i < cursor.getCount(); i++) {

            Log.d("debug","i=" + String.valueOf(i));
            SsaSchedule schedule = new SsaSchedule();

            // String　→ Date
            // 予定日
            schedule.setSchedule(cursor.getString(0));

            // 開始時間
            schedule.setStart(cursor.getString(1));

            // 終了時間
            schedule.setEnd(cursor.getString(2));

            // 内容
            schedule.setContent(cursor.getString(3));

            // モード
            schedule.setMode(cursor.getInt(4));

            Log.d("debug","Schedule: " + schedule.getSchedule().toString()
                    + " Start: " + schedule.getStart().toString() + " End: " + schedule.getEnd().toString()
                    + " Content: " + schedule.getContent() + " mode: " + String.valueOf(schedule.getMode()));
            mSsaScheduleList.add(schedule);

            cursor.moveToNext();
        }

        cursor.close();
        Log.d("debug","*********** SQL Parse End ***********");

        // 最後にソートして終了
        SortScheduleList();
    }

    /**
     * mSsaScheduleListの内容を早い順にソートする
     */
    private void SortScheduleList() {
        // TODO ソート処理
    }
}
