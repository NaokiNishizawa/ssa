package com.study.ssa.UI.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.study.ssa.R;
import com.study.ssa.Util.SharedPreferencesUtil;
import com.study.ssa.UI.CustomWidget.ChartView;

import java.util.Timer;
import java.util.TimerTask;

import androidx.fragment.app.DialogFragment;

/**
 * タイマーモードダイアログ ベースクラス<br>
 * 本ダイアログを開いている間はスリープしないようにしている
 */
public abstract class BaseTimerDialogFragment extends DialogFragment {

    /** 15ふん経過した */
    private static final int FIFTEENTH_COUNT = 60 * 15;

    /** 15分ごとに取得できるお金 */
    private static final int ONCE_GET_MONEY = 10;

    private Handler mHandler;
    private Timer mTimer;
    private int mCount; // 15分経過したかをカウントする変数

    private PowerManager mPowerManager;

    private int mGetMoney = 0;
    private TextView mGetMoneyText;

    private  int mProgressRate;
    private OnFinishClickListener mListener;

    protected TextView mTimeText;
    protected ChartView mChartView;
    protected TextView mProgressRateText;

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
        // 画面OFFにしないようにする
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            this.mListener = (BaseTimerDialogFragment.OnFinishClickListener) getActivity();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        //初期化処理
        setDialogWidth();

        initTimeText();

        initPowerManager();

        initChartView();

        initRetirementText();

        initGetMoneyText();

        initChildLayout();

        startCount();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("debug", "onStop");

        stopCount();

        // 一度でもstopにした場合(アプリをバックグランドに移動するなど)そこで終了
        getDialog().dismiss();

        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onDetach () {
        super.onDetach();
        if(null != mListener) {
            mListener.onFinish();
        }
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
     * タイムTextの初期化
     */
    private void initTimeText() {
        mTimeText = getDialog().findViewById(R.id.time_text);
    }

    /**
     * ChartView関連View 初期化
     */
    private void initChartView() {
        mChartView = getDialog().findViewById(R.id.chartView);
        mProgressRateText = getDialog().findViewById(R.id.progress_rate_text);

        // 初期値は0%
        mProgressRate = 0;
        mProgressRateText.setText(String.valueOf(mProgressRate) + getString(R.string.progress_rate));
        drawChart();
    }

    /**
     * PowerManagerの初期化
     */
    private void initPowerManager() {
        mPowerManager = (PowerManager) getContext().getSystemService(Context.POWER_SERVICE);
    }

    /**
     * 秒数カウント　開始
     */
    private void startCount() {
        mHandler = new Handler();

        // タイマーがすでに走っていたら、一度そのタイマーを止めて作り直す
        if(null != mTimer) {
            stopCount();
        }

        mTimer = new Timer();
        mCount = 0;

        CountTimerTask timerTask = new CountTimerTask();

        // スケジュールを設定 1000msec = 1秒
        mTimer.schedule(timerTask, 0, 1000);
    }

    /**
     * 秒数カウント　終了
     */
    private void stopCount() {
        if(null != mTimer) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    /**
     * リタイアテキスト初期化
     */
    private void initRetirementText() {
        TextView retirementText = getDialog().findViewById(R.id.retirement_text);
        retirementText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // リタイア
                getDialog().dismiss();
            }
        });
    }

    /**
     * 取得確定テキスト初期化
     */
    private void initGetMoneyText() {
        mGetMoney = 0;
        mGetMoneyText = getDialog().findViewById(R.id.get_money_text);

        mGetMoneyText.setText(String.valueOf(mGetMoney) + "$");
    }

    /**
     * 賞金を追加する
     */
    private void addMoney() {
        mGetMoney = mGetMoney + ONCE_GET_MONEY;
        mGetMoneyText.setText(String.valueOf(mGetMoney) + "$");

        // 賞金を保存する
        SharedPreferencesUtil.addMoneyValue(getContext(), ONCE_GET_MONEY);
    }

    /**
     * ChartView描画
     */
    private void drawChart() {
        mChartView.rate = mProgressRate;
        mChartView.requestLayout();
    }

    /**
     * 時間更新
     *
     * @param hour　時
     * @param min 分
     * @param sec 秒
     */
    protected void UpdateTime(int hour, int min, int sec) {
        String updateTimeStr = String.format("%1$02d:%2$02d:%3$02d", hour, min, sec);
        mTimeText.setText(updateTimeStr);
    }

    /**
     * 進捗プログレス更新
     */
    protected void UpdateProgress(int progressRate) {
        mProgressRate = progressRate;
        mProgressRateText.setText(String.valueOf(mProgressRate) + getString(R.string.progress_rate));
        drawChart();
    }

    /**
     * 子Dialog　初期化処理
     */
    protected abstract void initChildLayout();

    /**
     * 画面を開いてから1秒おきに呼ばれる<br>
     * 子クラスでやりたいことを記載すること
     */
    protected abstract void countFinish();


    // インナークラス
    private class CountTimerTask extends TimerTask {

        @Override
        public void run() {
            mHandler.post(new Runnable() {
                public void run() {

                    // 画面を意図的にOFFにされていないかを確認する
                    if(!mPowerManager.isInteractive()) {
                        // 意図的(電源ボタン押下など)に画面をOFFしている
                        getDialog().dismiss();
                        return;
                    }

                    mCount++;
                    if(5 == mCount) {
                        addMoney();
                        mCount = 0;
                    }

                    countFinish();
                    Log.d("debug", "Timer running");
                }
            });
        }
    }
}
