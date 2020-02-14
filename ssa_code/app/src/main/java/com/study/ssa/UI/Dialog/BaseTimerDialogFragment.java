package com.study.ssa.UI.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.study.ssa.R;
import com.study.ssa.UI.CustomWidget.ChartView;

import androidx.fragment.app.DialogFragment;

/**
 * タイマーモードダイアログ ベースクラス<br>
 * 本画面を開いている際はスリープしないようにしている
 */
public class BaseTimerDialogFragment extends DialogFragment {

    protected ChartView mChartView;
    protected TextView mProgressRateText;
    protected int mProgressRate;

    protected Handler mHandler;
    protected OnFinishClickListener mListener;

    public interface OnFinishClickListener {
        public void onFinish();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = new Dialog(getActivity());
        // タイトル非表示
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        // フルスクリーン
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.setContentView(R.layout.timer_dialog);
        // 背景を透明にする
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return dialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            this.mListener = (BaseTimerDialogFragment.OnFinishClickListener) activity;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        //初期化処理

        setDialogWidth();

        initChartView();
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


        LinearLayout dialog = getDialog().findViewById(R.id.timer_dialog_base_layout);
        ViewGroup.LayoutParams params = dialog.getLayoutParams();

        // Changes the height and width to the specified *pixels*
        params.width = (realSize.x/5) * 3;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;

        dialog.setLayoutParams(params);
    }

    /**
     * ChartView関連View 初期化
     */
    private void initChartView() {
        mChartView = getDialog().findViewById(R.id.chartView);
        mProgressRateText = getDialog().findViewById(R.id.progress_rate_text);

        // 初期値は0%
        mProgressRate = 75;
        mProgressRateText.setText(String.valueOf(mProgressRate) + getString(R.string.progress_rate));
        drawChart();
    }

    /**
     * ChartView描画
     */
    private void drawChart() {
        mChartView.rate = mProgressRate;
        mChartView.requestLayout();
    }
}
