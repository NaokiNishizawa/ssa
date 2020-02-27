package com.study.ssa.CharacterData;

import android.content.Context;
import android.util.Log;

import com.study.ssa.Util.SharedPreferencesUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * キャラクター情報を管理するクラス<br>
 * どこからでもアクセスできるようにシングルトンとなっている
 */
public class CharacterInfoManager {
    private static final String READ_FILE_NAME = "characterInfo.json";
    private static final String JSON_KEY_ROOT = "characterlist";
    private static final String JSON_KEY_NAME = "name";
    private static final String JSON_KEY_PRICE = "price";
    private static final String JSON_KEY_ICON = "icon";

    private static CharacterInfoManager instance = null;

    /**
     * キャラクター一覧<br>
     * 未購入のキャラクターを含む全てのキャラクターを保持している。
     */
    private List<CharacterInfo> mAllCharacterList;

    /**
     * コンストラクタ　隠蔽
     */
    private CharacterInfoManager() {
    }

    /**
     * CharacterInfoManagerクラス取得
     *
     * @return CharacterInfoManager
     */
    public static CharacterInfoManager getInstance() {
        if(null == instance) {
            instance = new CharacterInfoManager();
        }

        return instance;
    }


    /**
     * キャラクター一覧取得
     *
     * @return キャラクター一覧
     */
    public List<CharacterInfo> getAllCharacterList(Context context) {
        updateCharacterList(context);
        return mAllCharacterList;
    }

    /**
     * 初期化処理
     *
     * @param context
     */
    public void init(Context context) {
        // assetsからcharacterInfoを作成する
        readAllCharacterDate(context);
    }

    /**
     * 全キャラクター情報を読み込む
     * @param context
     */
    private void readAllCharacterDate(Context context) {
        mAllCharacterList = new ArrayList<>();
        // assetsのjsonデータを読み込みリストに詰める
        String jsonData;
        JSONArray jsonArray;

        try {
            StringBuffer buffer = new StringBuffer();
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(context.getResources().getAssets().open(READ_FILE_NAME)));
            while (true) {
                line = br.readLine();
                if(null == line) {
                    Log.d("debug", "line is null");
                    break;
                }
                buffer.append(line);
                Log.d("debug", "line: " + line);
            }

            jsonData = buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            JSONObject rootObject = new JSONObject(jsonData);
            jsonArray = rootObject.getJSONArray(JSON_KEY_ROOT);
            for (int i = 0; i < jsonArray.length(); i++) {
                CharacterInfo info = new CharacterInfo();
                JSONObject object = jsonArray.getJSONObject(i);

                info.setName(object.getString(JSON_KEY_NAME));
                info.setPrice(object.getInt(JSON_KEY_PRICE));
                info.setIcon(object.getString(JSON_KEY_ICON));
                info.setStatus(CharacterInfo.STATUS_NOT_PURCHASED);

                Log.d("debug", "character info Name:" + info.getName() + " price:" + String.valueOf(info.getPrice()) +
                " icon:" + info.getIcon());

                mAllCharacterList.add(info);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        updateCharacterList(context);
    }

    /**
     * 選択中のキャラクター情報取得<br>
     * ただし選択中のキャラクターがない場合はnullになる
     *
     * @return 選択中のキャラクター情報 or null
     */
    public CharacterInfo getSelectedCharacterInfo() {
        CharacterInfo info = null;
        for (CharacterInfo item: mAllCharacterList) {
            if(CharacterInfo.STATUS_SELECTED == item.getStatus()) {
                info = item;
                break;
            }
        }

        return info;
    }

    /**
     * mAllCharacterListの内容を更新する
     *
     * @param context
     */
    private void updateCharacterList(Context context) {
        // preferenceからの購入済のキャラクターを読み込みステータスを設定していく
        List<String> buyCharacterNameList = SharedPreferencesUtil.getBuyCharactersList(context);
        if(null == buyCharacterNameList) {
            return;
        }

        String selectedCharacterName = SharedPreferencesUtil.getSelectedCharacterName(context);
        if(0 == selectedCharacterName.length()) {
            return;
        }

        // ステータスを更新する
        for(CharacterInfo info: mAllCharacterList) {
            for(String item: buyCharacterNameList) {
                if(item.equals(info.getName())) {
                    // 購入済は確定したので、選択中かを確認
                    if(selectedCharacterName.equals(info.getName())) {
                        // 選択中
                        info.setStatus(CharacterInfo.STATUS_SELECTED);
                    } else {
                        // 購入済
                        info.setStatus(CharacterInfo.STATUS_PURCHASED);
                    }
                }
            }
        }
    }
}
