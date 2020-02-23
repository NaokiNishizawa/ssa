package com.study.ssa;

import android.app.Application;
import android.util.Log;

import com.study.ssa.CharacterData.CharacterInfoManager;

/**
 * アプリケーションクラス
 */
public class SsaApplication extends Application {
    public void onCreate() {
        Log.d("debug", "create application");
        super.onCreate();

        CharacterInfoManager manager = CharacterInfoManager.getInstance();
        // 初期化しておく
        manager.init(getApplicationContext());
    }
}
