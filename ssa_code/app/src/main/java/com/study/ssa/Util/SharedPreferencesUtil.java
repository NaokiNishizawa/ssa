package com.study.ssa.Util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences操作便利クラス
 */
public class SharedPreferencesUtil {

    private static final String KEY_DATA_NAME = "SsaPreference";
    private static final String KEY_MONEY = "Money";
    private static final String KEY_APP_VERSION = "AppVersion";

    /**
     * 取得金額の値を保存する
     *
     * @param context　コンテキスト
     * @param money　取得金額
     */
    public static void setMoneyValue(Context context, int money) {
        SharedPreferences data = context.getSharedPreferences(KEY_DATA_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();
        editor.putInt(KEY_MONEY, money);
        editor.apply();
    }

    /**
     * 取得金額の値を取得する
     *
     * @param context　コンテキスト
     * @return 取得金額
     */
    public static int getMoneyValue(Context context) {
        SharedPreferences data = context.getSharedPreferences(KEY_DATA_NAME, Context.MODE_PRIVATE);
        return data.getInt(KEY_MONEY, 0);
    }

    /**
     * 取得金額に金額を追加し保存する
     *
     * @param context　コンテキスト
     * @param addMoney 追加金額
     */
    public static void addMoneyValue(Context context, int addMoney) {
        int current = getMoneyValue(context);
        setMoneyValue(context, (current + addMoney));
    }

    /**
     * 取得金額から金額を減らし保存する
     *
     * @param context　コンテキスト
     * @param decreaseMoney 減少金額
     */
    public static final void decreaseMoneyValue(Context context, int decreaseMoney) {
        int current = getMoneyValue(context);
        setMoneyValue(context, (current - decreaseMoney));
    }

    /**
     * アプリVersionをセットする
     *
     * @param context
     * @param appVersion
     */
    public static final void setAppVersion(Context context, int appVersion) {
        SharedPreferences data = context.getSharedPreferences(KEY_DATA_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();
        editor.putInt(KEY_APP_VERSION, appVersion);
        editor.apply();
    }

    /**
     * アプリVersionを取得する
     *
     * @param context
     * @return アプリVersion
     */
    public static final int setAppVersion(Context context) {
        SharedPreferences data = context.getSharedPreferences(KEY_DATA_NAME, Context.MODE_PRIVATE);
        return data.getInt(KEY_APP_VERSION, 1);
    }
}
