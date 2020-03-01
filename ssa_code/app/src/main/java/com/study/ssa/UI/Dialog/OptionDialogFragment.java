package com.study.ssa.UI.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.Switch;

import androidx.fragment.app.DialogFragment;

import com.study.ssa.R;
import com.study.ssa.UI.CustomWidget.EqualWidthHeightTextView;
import com.study.ssa.Util.SharedPreferencesUtil;

/**
 * オプションダイアログ
 */
public class OptionDialogFragment extends DialogFragment {

    public interface OnButtonClickListener {
        public void onSettingFinish();
    }

    private OnButtonClickListener mOnButtonClickListener;
    private Switch mSoundSwitch;
    private SeekBar mBGMSeekBar;
    private SeekBar mSESeekBar;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = new Dialog(getActivity());
        // タイトル非表示
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        // フルスクリーン
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.setContentView(R.layout.option_dialog);
        // 画面外を押下してもキャンセルしない
        this.setCancelable(false);
        // 背景を透明にする
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            this.mOnButtonClickListener = (OnButtonClickListener) getActivity();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        //初期化処理
        setDialogWidth();

        initCancelButton();

        initSoundSwitch();

        initBGMSeekBar();

        initSESeekBar();

        initApplyBtn();
    }

    /**
     * ダイアログの幅を設定する
     */
    private void setDialogWidth() {
        // ダイアログの幅は画面の5分の3とする

        WindowManager wm = (WindowManager)getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display disp = wm.getDefaultDisplay();

        Point realSize = new Point();
        disp.getRealSize(realSize);


        FrameLayout dialog = getDialog().findViewById(R.id.option_dialog_base_layout);
        ViewGroup.LayoutParams params = dialog.getLayoutParams();

        // Changes the height and width to the specified *pixels*
        params.width = (realSize.x/5) * 3;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        dialog.setLayoutParams(params);
    }

    /**
     * キャンセルボタン初期化
     */
    private void initCancelButton() {
        EqualWidthHeightTextView cancel = getDialog().findViewById(R.id.option_dialog_cancel_button);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // キャンセル
                getDialog().cancel();
            }
        });
    }

    /**
     * SoundSwitch初期化
     */
    private void initSoundSwitch() {
         mSoundSwitch = getDialog().findViewById(R.id.option_sound_toggle_switch);

         // Preferenceから復元
        boolean isEnabled = SharedPreferencesUtil.getSoundEnabled(getContext());
        mSoundSwitch.setChecked(isEnabled);
    }

    /**
     * BGM SeekBar初期化
     */
    private void initBGMSeekBar() {
        mBGMSeekBar = getDialog().findViewById(R.id.option_bgm_seekbar);

        // Preferenceから復元
        int value = SharedPreferencesUtil.getBGMValue(getContext());
        mBGMSeekBar.setProgress(value);
    }

    /**
     * SE SeekBar初期化
     */
    private void initSESeekBar() {
        mSESeekBar = getDialog().findViewById(R.id.option_se_seekbar);

        // Preferenceから復元
        int value = SharedPreferencesUtil.getSEValue(getContext());
        mSESeekBar.setProgress(value);
    }

    /**
     * 適用ボタン初期化
     */
    private void initApplyBtn() {
        Button apply = getDialog().findViewById(R.id.option_apply_button);
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 適用ボタン押下

                // ShardPreferenceに保存
                saveOptionSettings();

                // 画面に通知
                if(null != mOnButtonClickListener) {
                    mOnButtonClickListener.onSettingFinish();
                }

                getDialog().dismiss();
            }
        });
    }

    /**
     * オプション設定を保存する
     */
    private void saveOptionSettings() {
        // Soundの有効無効を保存
        SharedPreferencesUtil.setSoundEnabled(getContext(), mSoundSwitch.isChecked());

        // BGMの値を保存
        SharedPreferencesUtil.setBGMValue(getContext(), mBGMSeekBar.getProgress());

        // SEの値を保存
        SharedPreferencesUtil.setSEValue(getContext(), mSESeekBar.getProgress());
    }
}
