package com.study.ssa.UI.CustomWidget;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;

import com.study.ssa.R;
import com.study.ssa.Util.SharedPreferencesUtil;

/**
 * クリック時にSoundを発生させる独自Buttonクラス
 */
public class ClickSoundButton extends BarrageGuardButton {

    private int mSoundId = R.raw.button; // デフォルトサウンド
    private MediaPlayer mMediaPlayer;

    public ClickSoundButton(Context context) {
        super(context);
    }

    public ClickSoundButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ClickSoundButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void clickAction() {
        if (mListener != null) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    mListener.onClick(ClickSoundButton.this);
                    if(null != mMediaPlayer) {
                        mMediaPlayer.reset();
                        mMediaPlayer.release();
                        mMediaPlayer = null;
                    }
                }
            },100);

            if(SharedPreferencesUtil.getSoundEnabled(mContext)) {
                if(0 != mSoundId) {
                    // ClickSoundを発生させる
                    mMediaPlayer = MediaPlayer.create(mContext, mSoundId);
                    float volume = SharedPreferencesUtil.getSEValue(mContext) / 100f;
                    mMediaPlayer.setVolume(volume, volume);
                    mMediaPlayer.start();
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
