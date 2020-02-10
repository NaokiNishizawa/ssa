package com.study.ssa.SsaSchedule;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.study.ssa.DB.SsaOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 予定管理クラス
 * どこからでも仕様できるようにシングルトンで作成する
 */
public class SsaScheduleManager {

    private static SsaScheduleManager instance = null;

    /** 予定一覧 */
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
            try {
                Log.d("debug","i=" + String.valueOf(i));
                SsaSchedule schedule = new SsaSchedule();

                // String　→ Date
                // 予定日
                String dateStr = cursor.getString(0);
                SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd", Locale.US);
                schedule.setSchedule(format.parse(dateStr));

                // 開始時間
                String startStr = cursor.getString(1);
                format = new SimpleDateFormat("yyyy.MM.dd hh:mm");
                schedule.setStart(format.parse(startStr));

                // 終了時間
                String endStr = cursor.getString(2);
                format = new SimpleDateFormat("yyyy.MM.dd hh:mm");
                schedule.setStart(format.parse(endStr));

                // 内容
                schedule.setContent(cursor.getString(3));

                // モード
                schedule.setMode(cursor.getInt(4));

                Log.d("debug","Schedule: " + schedule.getSchedule().toString()
                 + " Start: " + schedule.getStart().toString() + " End: " + schedule.getEnd().toString()
                 + " Content: " + schedule.getContent() + " mode: " + String.valueOf(schedule.getMode()));

                mSsaScheduleList.add(schedule);
            } catch (ParseException e) {
                Log.d("debug","parse error");
            }

            cursor.moveToNext();
        }
        cursor.close();
        Log.d("debug","*********** SQL Parse End ***********");
    }
}
