package com.study.ssa.SsaSchedule;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import com.study.ssa.Util.AlarmManagerUtil;
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
     * @param context コンテキスト
     */
    public void init(Context context) {
        initDB(context);
    }

    /**
     * DB初期化
     */
    private void initDB(Context context) {
        mHelper = new SsaOpenHelper(context);
        ReadDB(context);
    }

    /**
     * 引数で渡されたscheduleの10分前通知用scheduleを作成する
     *
     * @param baseSchedule　ベースとするschedule
     * @return 引数のscheduleの10分前に通知するためのschedule
     */
    public SsaSchedule createNotificationSchedule(SsaSchedule baseSchedule) {
        SsaSchedule notificationSchedule = new SsaSchedule();
        notificationSchedule = new SsaSchedule();

        // 開始時間から-10分した時間をstartに設定する
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            Date baseStartData = format.parse(baseSchedule.getStart());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(baseStartData);

            // 自力で計算する。というのもCalenderクラスのaddで-10分しようとしたが、どうしてか正しい値が計算できなかったため渋々・・・
            int hour = Integer.valueOf(baseSchedule.getStart().split(" ")[1].split(":")[0]);
            int min = Integer.valueOf(baseSchedule.getStart().split(" ")[1].split(":")[1]);

            // 普通に10分引けるパターン
            if(min >= 10) {
                min = min - 10;
            } else {
                // hourも一緒に引くパターン
                if(1 <= hour) {
                    hour--;
                    min = (min + 60) - 10;
                } else {
                    // 日付まで変更しなければいけないパターン　例:yyyy-MM-dd 00:09など
                    calendar.add(Calendar.DATE, -1);
                    hour = 23;
                    min = (min + 60) - 10;
                }
            }

            String notificationDateStr = format.format(calendar.getTime());
            String notificationScheduleStartStr = notificationDateStr + " " + String.valueOf(hour) + ":" + String.valueOf(min);
            notificationSchedule.setStart(notificationScheduleStartStr);
            notificationSchedule.setEnd(baseSchedule.getStart());

            // スタートが変わったことにより予定日が変わった可能性があるため改めて設定
            notificationSchedule.setSchedule(notificationDateStr);

            // 内容も事前通知であること旨のメッセージに変換
            notificationSchedule.setContent("予定(" + baseSchedule.getContent() + ")の10分前です");

            // モードは必ず通知モード
            notificationSchedule.setMode(SsaSchedule.MODE_NOTIFICATION);

        } catch (ParseException e) {
            // 何もしない
            return baseSchedule;
        }

        return  notificationSchedule;
    }

    /**
     * 引数でもらった日に登録されているアラームモードのスケジュール数を取得する
     * @param date　日付
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
     * 引数でもらった日に登録されているアラームモードのスケジュールの一覧を取得する<br>
     * [注意] 何も登録されていなかった場合はnullが返る
     * @param date
     * @return　アラームモードのスケジュールの一覧 or null
     */
    public List<SsaSchedule> getAlertScheduleItem(Date date) {
        List<SsaSchedule> list = new ArrayList<>();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String dateStr = format.format(date);

        for(SsaSchedule schedule: mSsaScheduleList) {
            SsaSchedule addSchedule;
            if((dateStr.equals(schedule.getSchedule())) && (SsaSchedule.MODE_ALERT == schedule.getMode())) {
                addSchedule = schedule.clone();
                list.add(addSchedule);
            }
        }

        return list;
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
     * 引数でもらった日に登録されているタイマーモードのスケジュールの一覧を取得する<br>
     * [注意] 何も登録されていなかった場合はnullが返る
     * @param date
     * @return　タイマーモードのスケジュールの一覧 or null
     */
    public List<SsaSchedule> getTimerScheduleItem(Date date) {
        List<SsaSchedule> list = new ArrayList<>();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String dateStr = format.format(date);

        for(SsaSchedule schedule: mSsaScheduleList) {
            SsaSchedule addSchedule;
            if((dateStr.equals(schedule.getSchedule())) && (SsaSchedule.MODE_TIMER == schedule.getMode())) {
                addSchedule = schedule.clone();
                list.add(addSchedule);
            }
        }

        return list;
    }

    /**
     * 引数でもらった日に登録されているスケジュールの一覧を取得する(アラートもタイマーも含む)<br>
     * [注意] 何も登録されていなかった場合はnullが返る
     * @param date
     * @return　スケジュールの一覧 or null
     */
    public List<SsaSchedule> getScheduleItem(Date date) {
        List<SsaSchedule> list = new ArrayList<>();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String dateStr = format.format(date);

        for(SsaSchedule schedule: mSsaScheduleList) {
            SsaSchedule addSchedule;
            if((dateStr.equals(schedule.getSchedule())) && (SsaSchedule.MODE_NOTIFICATION != schedule.getMode())) {
                addSchedule = schedule.clone();
                list.add(addSchedule);
            }
        }

        return list;
    }

    /**
     * 直近の予定を取得する。10分前の通知用のScheduleも返す<br>
     * 予定がない時はnullが返る
     */
    public SsaSchedule getNextScheduleIncludeNotificationItem() {
        SsaSchedule schedule = null;

        if(0 != mSsaScheduleList.size()) {
            schedule = mSsaScheduleList.get(0);
        }

        return schedule;
    }

    /**
     * 直近の予定を取得する<br>
     * 予定がない時はnullが返る
     */
    public SsaSchedule getNextSchedule() {
        SsaSchedule schedule = null;

        if(0 != mSsaScheduleList.size()) {
            for (SsaSchedule item: mSsaScheduleList) {
                if(SsaSchedule.MODE_NOTIFICATION != item.getMode()) {
                    schedule = item;
                    break;
                }
            }
        }

        return schedule;
    }

    /**
     * 引数でもらった予定と同じ内容のscheduleを返す<br>
     * [注意]ない時はnullが返る
     *
     * @param base 検索したい予定
     * @return 検索したい予定と同じ内容予定 or null
     */
    public SsaSchedule getEqualSchedule(SsaSchedule base) {

        if(0 == mSsaScheduleList.size()) {
            return null;
        }
        SsaSchedule equal = null;
        for(SsaSchedule item: mSsaScheduleList) {
            if(base.equals(item)) {
                equal = item;
            }
        }

        return equal;
    }

    /**
     * 予定を削除する
     *
     * @param context コンテキスト
     * @param delete　削除する予定
     */
    public void deleteSchedule(Context context, SsaSchedule delete) {
        Log.d("debug", "deleteSchedule");
        SsaSchedule registerNotification = null;
        mDB.beginTransaction();
        try {
            mDB.delete(SsaOpenHelper.TABLE_NAME, SsaOpenHelper._ID + "=?", new String[]{String.valueOf(delete.getID())});
            SsaSchedule notification = createNotificationSchedule(delete);
            registerNotification = getEqualSchedule(notification);
            if(null != registerNotification) {
                // 10分前の通知も削除する
                mDB.delete(SsaOpenHelper.TABLE_NAME, SsaOpenHelper._ID + "=?", new String[]{String.valueOf(registerNotification.getID())});
            }
            mDB.setTransactionSuccessful();
        } finally {
            mDB.endTransaction();
        }

        // アラームを削除
        AlarmManagerUtil.deleteAlarm(context, delete);

        if(null != registerNotification) {
            // 10分前の通知アラームを削除
            AlarmManagerUtil.deleteAlarm(context, registerNotification);
        }
    }

    /**
     * 予定を削除 & DBの再取得
     *
     * @param context コンテキスト
     * @param delete　削除する予定
     */
    public void deleteScheduleAndReadDB(Context context, SsaSchedule delete) {
        deleteSchedule(context, delete);

        // 再取得
        ReadDB(context);
    }

    /**
     * 全ての予定を取得する<br>
     *
     * @return 予定一覧
     */
    public List<SsaSchedule> getAllSchedule() {
        return mSsaScheduleList;
    }

    /**
     * 予定追加
     *
     * @param  context コンテキスト
     * @param add 追加する予定
     */
    public void addSchedule(Context context, SsaSchedule add) {
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

        // アラームにも追加
        AlarmManagerUtil.setAlarm(context, add);
    }

    /**
     * 予定追加 & DBの再取得
     *
     * @param  context コンテキスト
     * @param add　追加する予定
     */
    public void addScheduleAndReadDB(Context context, SsaSchedule add) {
        addSchedule(context, add);

        // 再取得
        ReadDB(context);
    }

    /**
     * DBから全データを読み込みmSsaScheduleListに追加する<br>
     * mSsaScheduleListは予定が早い順に追加されている<br>
     * [注意] ソートなどを行っているため呼び出しは必要最小限にすること
     *
     * @param context コンテキスト
     */
    public void ReadDB(Context context) {

        mDB = mHelper.getReadableDatabase();

        Log.d("debug","**********Cursor");

        Cursor cursor = mDB.query(
                SsaOpenHelper.TABLE_NAME,
                new String[] { SsaOpenHelper._ID,
                        SsaOpenHelper.COLUMN_NAME_SCHEDULE_DAY,
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

            // ID
            schedule.setID(cursor.getInt(0));

            // 予定日
            schedule.setSchedule(cursor.getString(1));

            // 開始時間
            schedule.setStart(cursor.getString(2));

            // 終了時間
            schedule.setEnd(cursor.getString(3));

            // 内容
            schedule.setContent(cursor.getString(4));

            // モード
            schedule.setMode(cursor.getInt(5));

            Log.d("debug", "ID: " + String.valueOf(schedule.getID())
                    + "Schedule: " + schedule.getSchedule()
                    + " Start: " + schedule.getStart() + " End: " + schedule.getEnd()
                    + " Content: " + schedule.getContent() + " mode: " + String.valueOf(schedule.getMode()));

            mSsaScheduleList.add(schedule);

            cursor.moveToNext();
        }

        cursor.close();
        Log.d("debug","*********** SQL Parse End ***********");

        // 本日より古い予定は全て削除する
        removeOldSchedule(context);

        // 最後にソートして終了
        SortScheduleList();
    }

    /**
     * 現在より古い予定は全て削除する
     *
     * @param context コンテキスト
     */
    private void removeOldSchedule(Context context) {
        ArrayList<SsaSchedule> futureScheduleList = new ArrayList<>();

        printSsaScheduleList(mSsaScheduleList); // for debug

        // 現在のDateを取得
        // compareToで本日の予定が正しく判定されるために一度DateをStringに変換し、Dateを作成している
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String todayStr = format.format(Calendar.getInstance().getTime());
        Date today;
        try {
            today = format.parse(todayStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }


        for(int i=0; i<mSsaScheduleList.size(); i++) {
            SsaSchedule schedule = mSsaScheduleList.get(i);

            try {
                Date scheduleDate = format.parse(schedule.getSchedule());

                if(-1 == scheduleDate.compareTo(today)) {
                    // 古いデータのためDBへの削除処理
                    deleteSchedule(context, schedule);
                } else if (0 == scheduleDate.compareTo(today)) {
                    // 本日の予定の場合は、開始時間が現在時刻よりも古い場合は削除
                    // 本来はDateクラスのcompareToで比較したいが、現在時刻より過去のものも1を返すため自力で比較している

                    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
                    String currentTimeStr = sf.format(Calendar.getInstance().getTime());
                    String[] currentTimeList = currentTimeStr.split(" ")[1].split(":");
                    String[] scheduleStartDateList = schedule.getStart().split(" ")[1].split(":");

                    if(Integer.valueOf(currentTimeList[0]) < Integer.valueOf(scheduleStartDateList[0])) {
                        // 本日の予定でも未来の予定
                        futureScheduleList.add(schedule);
                    } else if((Integer.valueOf(currentTimeList[0]) == Integer.valueOf(scheduleStartDateList[0])) &&
                            (Integer.valueOf(currentTimeList[1]) <= Integer.valueOf(scheduleStartDateList[1]))){
                        // 本日の予定でも未来の予定
                        futureScheduleList.add(schedule);
                    } else {
                        // 古いデータのためDBへの削除処理
                        deleteSchedule(context, schedule);
                    }
                } else {
                    // 未来の予定
                    futureScheduleList.add(schedule);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // mSsaScheduleListを更新
        mSsaScheduleList.clear();
        mSsaScheduleList.addAll(futureScheduleList);

        printSsaScheduleList(mSsaScheduleList); // for debug
    }

    /**
     * mSsaScheduleListの内容を早い順にソートする
     */
    private void SortScheduleList() {

        LinkedList<SsaSchedule> mSortList = new LinkedList<>();

        printSsaScheduleList(mSsaScheduleList); // for debug

        // ソート処理
        for(int i=0; i < mSsaScheduleList.size(); i++) {
            SsaSchedule addSchedule = mSsaScheduleList.get(i);

            if(0 == mSortList.size()) {
                // 何も考えずに先頭に追加
                mSortList.add(0,addSchedule);
            } else {
                // 挿入場所
                int index = 0;
                // mSortListの上から確認していき適切な場所に挿入するため、挿入場所を探索する
                for(int j=0; j < mSortList.size(); j++) {
                    SsaSchedule schedule = mSortList.get(j);

                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    try {
                        Date scheduleDate = format.parse(schedule.getSchedule());
                        Date addScheduleDate = format.parse(addSchedule.getSchedule());

                        if(-1 == addScheduleDate.compareTo(scheduleDate)) {
                            // 今追加されているものよりも古いデータのため、この場所に挿入する
                            break;
                        } else if(0 == addScheduleDate.compareTo(scheduleDate)){
                            // 同じ日なので hh:mmで比較する
                            String[] scheduleDateList = schedule.getStart().split(" ")[1].split(":");
                            String[] addScheduleDateList = addSchedule.getStart().split(" ")[1].split(":");

                            if(Integer.valueOf(addScheduleDateList[0]) < Integer.valueOf(scheduleDateList[0])) {
                                // hhで比較
                                // 今追加されていものよりも古いデータのためこの場所に挿入する
                                break;
                            } else if((Integer.valueOf(addScheduleDateList[0]) == Integer.valueOf(scheduleDateList[0])) &&
                                        Integer.valueOf(addScheduleDateList[1]) <= Integer.valueOf(scheduleDateList[1])) {
                                // hhが同じ時間だったため、mmで比較
                                // 今追加されていものよりも古いデータのためこの場所に挿入する
                                break;
                            } else {
                                // 先の予定のため何もしない
                            }
                        } else {
                            // 先の予定のため何もしない
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                        break;
                    }

                    index++;
                }

                mSortList.add(index, addSchedule);
            }
        }

        // mSsaScheduleListを更新
        mSsaScheduleList.clear();
        mSsaScheduleList.addAll(mSortList);

        printSsaScheduleList(mSsaScheduleList); // for debug
    }

    // for debug
    private void printSsaScheduleList(List<SsaSchedule> list) {

        if(0 == list.size()) {
            return;
        }

        Log.d("debug", "***** printSsaScheduleList start  *****");
        for(int i=0; i< list.size(); i++) {
            SsaSchedule schedule = list.get(i);
            Log.d("debug", "id: " + String.valueOf(schedule.getID())
            + "schedule: " + schedule.getSchedule() + "start: " + schedule.getStart()
            + "end: " + schedule.getEnd() + "content: " + schedule.getContent()
            + "mode: " + String.valueOf(schedule.getMode()));
        }
        Log.d("debug", "***** printSsaScheduleList end  *****");
    }
}
