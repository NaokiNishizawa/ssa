package com.study.ssa.UI.CustomWidget;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatButton;

import com.study.ssa.R;
import com.study.ssa.Util.SharedPreferencesUtil;

/**
 * クリック時にSoundを発生させる独自Buttonクラス
 */
public class ClickSoundButton extends BarrageGuardButton {

    private int mSoundId = R.raw.button; // デフォルトサウンド

    public ClickSoundButton(Context context) {
        super(context);
    }

    public ClickSoundButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ClickSoundButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
/*
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            if (mListener != null) {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mListener.onClick(ClickSoundButton.this);
                    }
                },100);

                if(SharedPreferencesUtil.getSoundEnabled(mContext)) {
                    if(0 != mSoundId) {
                        // ClickSoundを発生させる
                        MediaPlayer mp = MediaPlayer.create(mContext, mSoundId);
                        float volume = SharedPreferencesUtil.getSEValue(mContext) / 100f;
                        mp.setVolume(volume, volume);
                        mp.start();
                    }
                }
            }
        }

        return super.dispatchTouchEvent(ev);
    }
    */

    @Override
    protected void clickAction() {
        if (mListener != null) {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mListener.onClick(ClickSoundButton.this);
                    }
                },100);

            if(SharedPreferencesUtil.getSoundEnabled(mContext)) {
                if(0 != mSoundId) {
                    // ClickSoundを発生させる
                    MediaPlayer mp = MediaPlayer.create(mContext, mSoundId);
                    float volume = SharedPreferencesUtil.getSEValue(mContext) / 100f;
                    mp.setVolume(volume, volume);
                    mp.start();
                }
            }
        }
    }

    /**
     * Click Soundを設定する
     *
     * @param soundId
     */
    public void setClickSound(int soundId) {
        mSoundId = soundId;
    }
}
