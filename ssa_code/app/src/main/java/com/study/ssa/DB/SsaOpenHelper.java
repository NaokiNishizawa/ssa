package com.study.ssa.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * SSA SQL操作クラス
 */
public class SsaOpenHelper extends SQLiteOpenHelper {

    // データーベースのバージョン
    private static final int DATABASE_VERSION = 1;

    // データーベース名
    public static final String DATABASE_NAME = "SSADB.db";
    public static final String TABLE_NAME = "ssadb";
    public static final String _ID = "_id";
    public static final String COLUMN_NAME_SCHEDULE_DAY = "schedule";
    public static final String COLUMN_NAME_START = "startTime";
    public static final String COLUMN_NAME_END = "endTime";
    public static final String COLUMN_NAME_CONTENT = "content";
    public static final String COLUMN_NAME_MODE = "mode";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME_SCHEDULE_DAY + " TEXT," +
                    COLUMN_NAME_START + " TEXT," +
                    COLUMN_NAME_END + " TEXT," +
                    COLUMN_NAME_CONTENT + " TEXT," +
                    COLUMN_NAME_MODE + " INTEGER)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    /**
     * コンストラクト
     *
     * @param context
     */
    public SsaOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // テーブル作成
        // SQLiteファイルがなければSQLiteファイルが作成される
        db.execSQL(
                SQL_CREATE_ENTRIES
        );

        Log.d("debug", "onCreate(SQLiteDatabase db)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // アップデートの判別
        db.execSQL(
                SQL_DELETE_ENTRIES
        );
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
