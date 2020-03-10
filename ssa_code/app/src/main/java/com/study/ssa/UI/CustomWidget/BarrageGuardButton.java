package com.study.ssa.UI.CustomWidget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;

/**
 * 連打防止Buttonクラス
 */
public class BarrageGuardButton extends AppCompatButton {

    protected   Context mContext;
    protected OnClickListener mListener;


    public BarrageGuardButton(Context context) {
        super(context);
        mContext = context;
    }

    public BarrageGuardButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public BarrageGuardButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    public void setOnClickListener(final View.OnClickListener listener) {
        mListener = listener;

        super.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View view) {

                view.setEnabled(false);
                postDelayed(new Runnable() {
                    public void run() {
                        view.setEnabled(true);
                    }
                }, 1000L);

                clickAction();
            }
        });
    }

    /**
     * クリック時の実行アクション<br>
     * 通常時は、単純にmListenerのonClickを呼ぶ。<br>
     * 子クラスでカスタマイズしたい場合は、Overrideして実装すること
     */
    protected void clickAction() {
        mListener.onClick(BarrageGuardButton.this);
    }
}
