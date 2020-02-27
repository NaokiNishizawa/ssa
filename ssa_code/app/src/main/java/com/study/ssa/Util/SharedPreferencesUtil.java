package com.study.ssa.Util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Arrays;
import java.util.List;

/**
 * SharedPreferences操作便利クラス
 */
public class SharedPreferencesUtil {

    private static final String KEY_DATA_NAME = "SsaPreference";
    private static final String KEY_SELECTED_CHARACTER = "SelectedCharacter";
    private static final String KEY_BUY_CHARACTER = "BuyCharacter";
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
    public static void decreaseMoneyValue(Context context, int decreaseMoney) {
        int current = getMoneyValue(context);
        setMoneyValue(context, (current - decreaseMoney));
    }

    /**
     * 選択中キャラクター名を保存する
     * @param context
     * @param characterName
     */
    public static void setSelectedCharacterName(Context context, String characterName) {
        SharedPreferences data = context.getSharedPreferences(KEY_DATA_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();
        editor.putString(KEY_SELECTED_CHARACTER, characterName);
        editor.apply();

        // 購入済のキャラクターとしても追記して置く
        String beforeStr = getBuyCharacterNames(context);
        // TODO 重複チック

        setBuyCharacterNames(context, beforeStr + characterName + ",");
    }

    /**
     * 選択中のキャラクター名を取得する
     * @param context
     * @return 選択中のキャラクター名
     */
    public static String getSelectedCharacterName(Context context) {
        SharedPreferences data = context.getSharedPreferences(KEY_DATA_NAME, Context.MODE_PRIVATE);
        return data.getString(KEY_SELECTED_CHARACTER, "");
    }

    /**
     * 購入済のキャラクター一覧<br>
     * ただし購入済のキャラクターがない場合はnullが返る
     *
     * @param context
     * @return 購入済のキャラクター一覧 or null
     */
    public static List<String> getBuyCharactersList(Context context) {
        SharedPreferences data = context.getSharedPreferences(KEY_DATA_NAME, Context.MODE_PRIVATE);
        String characterNames = data.getString(KEY_BUY_CHARACTER, "");

        if(0 == characterNames.length()) {
            // 何もない場合はnullを返す
            return null;
        }

        // 文字列は","で区切られているのでまず一番最後についている不要な","を取り除く
        String base = characterNames.substring(0, characterNames.length() -1);
        String[] names = base.split(",");

        return Arrays.asList(names);
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

    /**
     * 購入済のキャラクター名一覧を保存する
     * @param context
     * @param characterNames
     */
    private static void setBuyCharacterNames(Context context, String characterNames) {
        SharedPreferences data = context.getSharedPreferences(KEY_DATA_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();
        editor.putString(KEY_BUY_CHARACTER, characterNames);
        editor.apply();
    }

    /**
     * 購入済のキャラクター名一覧(,で区切り一行にした文字列)
     * @param context
     * @return ,区切りで一行にしたキャラクター名の一覧 ex A,B,C
     */
    private static String getBuyCharacterNames(Context context) {
        SharedPreferences data = context.getSharedPreferences(KEY_DATA_NAME, Context.MODE_PRIVATE);
        return data.getString(KEY_BUY_CHARACTER, "");
    }

}
