package com.study.ssa;

import android.app.Application;
import android.util.Log;

import com.study.ssa.CharacterData.CharacterInfo;
import com.study.ssa.CharacterData.CharacterInfoManager;
import com.study.ssa.Util.SharedPreferencesUtil;

import java.util.List;

/**
 * アプリケーションクラス
 */
public class SsaApplication extends Application {
    public void onCreate() {
        Log.d("debug", "create application");
        super.onCreate();

        SharedPreferencesUtil.addMoneyValue(getApplicationContext(), 10000); // TODO debug

        CharacterInfoManager manager = CharacterInfoManager.getInstance();
        // 初期化しておく
        manager.init(getApplicationContext());
    }
}
