package com.study.ssa.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.study.ssa.R;
import com.study.ssa.SsaSchedule.SsaSchedule;
import com.study.ssa.SsaSchedule.SsaScheduleManager;

/**
 * 予定登録ダイアログ
 */
public class RegisterDialogFragment extends DialogFragment {

    public interface OnButtonClickListener {
        public void onRegisterButtonClick();
    }

    public static final String KEY_SELECT_DAY = "select_day";

    private String mRegisterDayStr;
    private SsaSchedule mSchedule; // 予定データ保持クラス
    private OnButtonClickListener mOnDialogButtonClickListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = new Dialog(getActivity());
        // タイトル非表示
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        // フルスクリーン
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.setContentView(R.layout.register_dialog);
        // 背景を透明にする
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mRegisterDayStr = getArguments().getString(KEY_SELECT_DAY);

        initSchedule();

        return dialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            this.mOnDialogButtonClickListener = (OnButtonClickListener) activity;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        //初期化処理
        initTitleText();

        initContentEditText();

        initRangeText();

        initIconButton();

        // 登録ボタン
        initRegisterButton();
    }

    /**
     * データ保持クラス初期化
     */
    private void initSchedule() {
        mSchedule = new SsaSchedule();
        mSchedule.setSchedule(mRegisterDayStr);
    }

    /**
     * 予定入力EditText初期化
     */
    private void initContentEditText() {
        EditText editText = getDialog().findViewById(R.id.content_edit_text);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 入力規則などはないため、今は何もしない
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 入力規則などはないため、今は何もしない
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 入力された内容をデータクラスに即時反映
                mSchedule.setContent(s.toString());
            }
        });
    }

    /**
     * タイトル文字列の初期化
     */
    private void initTitleText() {
        TextView title = getDialog().findViewById(R.id.register_day);
        title.setText(mRegisterDayStr);
    }

    /**
     * 開始時間、終了時間テキストの初期化
     */
    private void initRangeText() {
        // テキスト押下時にTimePickerDialogを表示する
        //　開始時間
        final TextView startTimeText = getDialog().findViewById(R.id.start_time_text);
        startTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] time = startTimeText.getText().toString().split(":");
                final int hour = Integer.valueOf(time[0]);
                int min = Integer.valueOf(time[1]);

                TimePickerDialog dialog = new TimePickerDialog(
                        getActivity(),
                        new TimePickerDialog.OnTimeSetListener(){
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                startTimeText.setText(String.format("%02d:%02d", hourOfDay, minute));
                                Log.d("debug", startTimeText.getText().toString());

                                // Scheduleクラスに保存
                                mSchedule.setStart((mRegisterDayStr + " " + startTimeText.getText().toString()));
                            }
                        },
                        hour,min,true);
                dialog.show();
            }
        });

        // 終了時間
        final TextView endTimeText = getDialog().findViewById(R.id.end_time_text);
        endTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] time = endTimeText.getText().toString().split(":");
                final int hour = Integer.valueOf(time[0]);
                int min = Integer.valueOf(time[1]);

                TimePickerDialog dialog = new TimePickerDialog(
                        getActivity(),
                        new TimePickerDialog.OnTimeSetListener(){
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                endTimeText.setText(String.format("%02d:%02d", hourOfDay, minute));
                                Log.d("debug", endTimeText.getText().toString());

                                // Scheduleクラスに保存
                                mSchedule.setEnd((mRegisterDayStr + " " + endTimeText.getText().toString()));
                            }
                        },
                        hour,min,true);
                dialog.show();
            }
        });
    }

    /**
     * アイコンボタン初期化
     */
    private void initIconButton() {
        ImageButton alertImageButton = getDialog().findViewById(R.id.alert_icon_button);
        alertImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // アラームボタンクリック
                switchClick(R.id.alert_icon_button);
            }
        });

        ImageButton timerImageButton = getDialog().findViewById(R.id.timer_icon_button);
        timerImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // タイマーボタンクリック
                switchClick(R.id.timer_icon_button);
            }
        });

        // 初期値はアラームモードとしておく
        alertImageButton.performClick();

    }

    /**
     * アラームボタンとタイマーボタンの選択状態を変更する
     * @param id
     */
    private void switchClick(int id) {
        View selectedAlert = getDialog().findViewById(R.id.selecte_alert);
        View selectedTimer = getDialog().findViewById(R.id.selected_timer);

        if((R.id.alert_icon_button) == id) {
            Log.d("debug", "clicked alert");
            mSchedule.setMode(SsaSchedule.MODE_ALERT);

            selectedAlert.setVisibility(View.VISIBLE);
            selectedTimer.setVisibility(View.INVISIBLE);
        } else {
            Log.d("debug", "clicked timer");
            mSchedule.setMode(SsaSchedule.MODE_TIMER);

            selectedAlert.setVisibility(View.INVISIBLE);
            selectedTimer.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 登録ボタン初期化
     */
    private void initRegisterButton() {
        Button registerButton = getDialog().findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 予定保存処理
                SsaScheduleManager manager = SsaScheduleManager.getInstance();
                manager.addSchedule(mSchedule);
                mOnDialogButtonClickListener.onRegisterButtonClick();
                getDialog().dismiss();
            }
        });
    }
}
