package com.study.ssa.CharacterData;

import android.content.Context;
import android.util.Log;

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
     * 購入済みのキャラクター一覧
     */
    private List<CharacterInfo> mBuyCharacterList;

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
    public List<CharacterInfo> getAllCharacterList() {
        return mAllCharacterList;
    }

    /**
     * 購入済みのキャラクター一覧<br>
     * 何も購入してない場合は、nullが返る
     *
     * @return キャラクター一覧 or null
     */
    public List<CharacterInfo> getBuyCharacterList() {
        return mBuyCharacterList;
    }

    /**
     * 初期化処理
     *
     * @param context
     */
    public void init(Context context) {
        // assetsからcharacterInfoを作成する
        readCharacterDate(context);
    }

    /**
     * キャラクター情報を読み込む
     * @param context
     */
    private void readCharacterDate(Context context) {
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

                Log.d("debug", "character info Name:" + info.getName() + " price:" + String.valueOf(info.getPrice()) +
                " icon:" + info.getIcon());

                mAllCharacterList.add(info);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
    }
}
