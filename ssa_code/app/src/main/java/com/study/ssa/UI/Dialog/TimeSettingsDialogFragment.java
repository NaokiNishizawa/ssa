package com.study.ssa.UI.Dialog;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.study.ssa.R;
import com.study.ssa.SsaSchedule.SsaSchedule;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.fragment.app.DialogFragment;

/**
 * タイマーモード　マニュアル設定時の設定時刻ダイアログ
 */
public class TimeSettingsDialogFragment extends DialogFragment {

    private Date mToday;
    private String mTodayYYYYMMDD;
    private SsaSchedule mSchedule;
    private TextView mStartText;
    private TextView mEndText;
    private OnFinishClickListener mListener;

    public interface OnFinishClickListener {
        public void onCompleteTimerSettings(SsaSchedule schedule);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = new Dialog(getActivity());
        // タイトル非表示
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        // フルスクリーン
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.setContentView(R.layout.time_settings_dialog);
        // 背景を透明にする
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // 画面OFFにしないようにする
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mSchedule = new SsaSchedule();

        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            this.mListener = (TimeSettingsDialogFragment.OnFinishClickListener) getActivity();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        //初期化処理
        setDialogWidth();

        setToday();

        initStartText();

        initEndText();

        initStartButton();
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


        LinearLayout dialog = getDialog().findViewById(R.id.timer_Setting_dialog_base_layout);
        ViewGroup.LayoutParams params = dialog.getLayoutParams();

        // Changes the height and width to the specified *pixels*
        params.width = (realSize.x/5) * 3;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        dialog.setLayoutParams(params);
    }

    /**
     * 本日のDateを取得し設定する
     */
    private void setToday() {
        Calendar calendar = Calendar.getInstance();
        mToday = calendar.getTime();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        mTodayYYYYMMDD = format.format(mToday);
        mSchedule.setSchedule(mTodayYYYYMMDD);
    }

    /**
     * 開始時間テキスト初期化
     */
    private void initStartText() {
        mStartText = getDialog().findViewById(R.id.ts_start_time_text);

        // 現在時刻を設定する
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        String startSrt = format.format(mToday);

        mStartText.setText(startSrt);
        mSchedule.setStart(mTodayYYYYMMDD + " " + startSrt);
    }

    /**
     * 終了時間テキスト初期化
     */
    private void initEndText() {
        mEndText = getDialog().findViewById(R.id.ts_end_time_text);
        mEndText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] time = mEndText.getText().toString().split(":");
                final int hour = Integer.valueOf(time[0]);
                int min = Integer.valueOf(time[1]);

                TimePickerDialog dialog = new TimePickerDialog(
                        getActivity(),
                        new TimePickerDialog.OnTimeSetListener(){
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                mEndText.setText(String.format("%02d:%02d", hourOfDay, minute));
                                Log.d("debug", mEndText.getText().toString());

                                mSchedule.setEnd(mTodayYYYYMMDD + " " + mEndText.getText().toString());
                            }
                        },
                        hour,min,true);
                dialog.show();
            }
        });

        mEndText.setText("00:00"); // 初期値は00:00
        mSchedule.setEnd(mTodayYYYYMMDD + " " + "00:00");
    }

    /**
     * 開始ボタン初期化
     */
    private void initStartButton() {
        Button startBtn = getDialog().findViewById(R.id.ts_start_button);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 開始する
                doStart();
            }
        });
    }

    /**
     * 開始
     */
    private void doStart() {
        // 正しい予定か確認する
        if((-1 == mSchedule.compareTo(mSchedule.getStart(), mSchedule.getEnd())) ||
                (0 == mSchedule.compareTo(mSchedule.getStart(), mSchedule.getEnd()))) {
            // 時間設定が正しくないため、その旨を通知し保存も何もしない
            Toast.makeText(getContext(), getString(R.string.error_time), Toast.LENGTH_LONG).show();
            return;
        }

        if(null != mListener) {
            mListener.onCompleteTimerSettings(mSchedule);
        }

        dismiss();
    }
}
