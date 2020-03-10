package com.study.ssa.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.study.ssa.CharacterData.CharacterInfo;
import com.study.ssa.CharacterData.CharacterInfoManager;
import com.study.ssa.R;
import com.study.ssa.UI.Adapter.CharacterListAdapter;
import com.study.ssa.Util.SharedPreferencesUtil;

import java.util.List;

/**
 * ショップ画面
 */
public class ShopActivity extends Activity implements CharacterListAdapter.onClickListener{

    private TextView mSelectedCharacterName;
    private TextView mMoney;
    private CharacterInfoManager mManager;
    private CharacterListAdapter mAdapter;

    private MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        // 初期化
        init();
    }

    @Override
    public void onStart() {
        super.onStart();
        initMediaPlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMediaPlayer.pause();
        mMediaPlayer.release();
        mMediaPlayer = null;
    }

    /**
     * 初期化処理
     */
    private void init() {
        initHeader();
        initCharacterList();
        initHomeButton();
    }

    /**
     * ヘッダー郡の初期化
     */
    private void initHeader() {
        // 現在選択中のマスコットを取得して表示する
        setSelectedCharacterNameText();

        // 所持コインを取得し表示する
        setGetMoney();
    }


    /**
     * Character Listの初期化
     */
    private void initCharacterList() {
        RecyclerView recyclerView = findViewById(R.id.sp_character_list);

        mManager = CharacterInfoManager.getInstance();
        List<CharacterInfo> list = mManager.getAllCharacterList(this);
         if(null == list) {
            return;
        }

        mAdapter = new CharacterListAdapter(this, list, this);

        LinearLayoutManager llm = new LinearLayoutManager(this);

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(llm);

        recyclerView.setAdapter(mAdapter);
    }

    /**
     * ホームボタン初期化処理
     */
    private void initHomeButton() {
        Button home = findViewById(R.id.sp_go_home_button);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Activityを終了する
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * MediaPlayerの初期化
     */
    private void initMediaPlayer() {
        mMediaPlayer = MediaPlayer.create(this, R.raw.shop);

        if(!SharedPreferencesUtil.getSoundEnabled(this)) {
            // 音楽無効時は何もしない
            return;
        }

        mMediaPlayer.setLooping(true);

        float volume = SharedPreferencesUtil.getBGMValue(this) / 100f;
        mMediaPlayer.setVolume(volume, volume);
        mMediaPlayer.start();
    }

    /**
     * TextViewに現在選択中のキャラクター名を設定する
     */
    private void setSelectedCharacterNameText() {
        if(null == mSelectedCharacterName) {
            mSelectedCharacterName = findViewById(R.id.sp_selected_character_name);
        }

        String name = SharedPreferencesUtil.getSelectedCharacterName(this);
        if(0 == name.length()) {
            // 選択中のキャラクターなし
            mSelectedCharacterName.setText(R.string.character_nothing);
        } else {
            mSelectedCharacterName.setText(name);
        }
    }

    /**
     * 現在取得金額を設定する
     */
    private void setGetMoney() {
        if(null == mMoney) {
            mMoney = findViewById(R.id.sp_shojicoin_text);
        }

        mMoney.setText(String.valueOf(SharedPreferencesUtil.getMoneyValue(this)) + getString(R.string.get_money));
    }

    @Override
    public void BuyCharacter(CharacterInfo info) {

        // 未購入キャラクターか確認する
        if(CharacterInfo.STATUS_NOT_PURCHASED == info.getStatus()) {
            // 購入可能か確認
            if(isAvailableForPurchase(info)) {
                // 購入可能か最終確認
                showConfirmDialog(info);
            } else {
                // 購入不可能の旨をToastで表示して終了
                Toast.makeText(this, R.string.unpurchasable, Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            doSound(R.raw.button);
            updateUI(info);
        }
    }

    /**
     * 引数のキャラクターが購入可能か
     *
     * @param info
     * @return true 可能 / false 不可能
     */
    private boolean isAvailableForPurchase(CharacterInfo info) {
        boolean result = false;
        int havingMoney = SharedPreferencesUtil.getMoneyValue(this);

        if(info.getPrice() <= havingMoney) {
            // 購入可能
            result = true;
        }

        return result;
    }

    /**
     * キャラクター購入時確認ダイアログ表示
     * @param info
     */
    private void showConfirmDialog(final CharacterInfo info) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String name = info.getName() + getString(R.string.confirm_buy_message);
        builder.setMessage(name);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 購入処理
                // 金額を減らす
                SharedPreferencesUtil.decreaseMoneyValue(getApplicationContext(), info.getPrice());
                setGetMoney();

                doSound(R.raw.coin);
                updateUI(info);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // nothing
            }
        });
        builder.create().show();
    }

    /**
     * UI更新処理
     *
     * @param info
     */
    private void updateUI(CharacterInfo info) {
        // 選択中に変更する
        SharedPreferencesUtil.setSelectedCharacterName(this, info.getName());

        // 購入されたため、UIを更新する
        mAdapter.setList(mManager.getAllCharacterList(this));
        mAdapter.notifyDataSetChanged();

        setSelectedCharacterNameText();
    }

    /**
     * ボタン実行サウンドの実行
     *
     * @param soundId
     */
    private void doSound(int soundId) {
        if(SharedPreferencesUtil.getSoundEnabled(this)) {
            MediaPlayer mp = MediaPlayer.create(this, soundId);
            float volume = SharedPreferencesUtil.getSEValue(this) / 100f;
            mp.setVolume(volume, volume);
            mp.start();
        }
    }
}
