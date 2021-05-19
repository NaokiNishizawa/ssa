package com.study.ssa.UI.Dialog;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.study.ssa.R;
import com.study.ssa.SsaSchedule.SsaSchedule;
import com.study.ssa.SsaSchedule.SsaScheduleManager;

/**
 * 予定登録ダイアログ
 */
public class RegisterDialogFragment extends DialogFragment {

    public interface OnButtonClickListener {
        public void onRegisterButtonClick();
        public void onRegisterDialogCancel();
    }

    public static final String KEY_SELECT_DAY = "select_day";
    private static final String DEFUALT_TIME = "00:00";

    private String mRegisterDayStr;
    private SsaSchedule mSchedule; // 予定データ保持クラス
    private boolean mIsNotificationTenMin = false; // 10分前に通知するかを表すflag
    private OnButtonClickListener mListener;

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
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            this.mListener = (OnButtonClickListener) getActivity();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        //初期化処理

        setDialogWidth();

        initTitleText();

        initContentEditText();

        initRangeText();

        initIconButton();

        initNotificationButton();

        // 登録ボタン
        initRegisterButton();
    }

    @Override
    public void onDetach () {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if(null != mListener) {
            mListener.onRegisterDialogCancel();
        }


    }

    /**
     * データ保持クラス初期化
     */
    private void initSchedule () {
        mSchedule = new SsaSchedule();
        mSchedule.setSchedule(mRegisterDayStr);
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


        LinearLayout dialog = getDialog().findViewById(R.id.regster_dialog_base_layout);
        ViewGroup.LayoutParams params = dialog.getLayoutParams();

        // Changes the height and width to the specified *pixels*
        params.width = (realSize.x/5) * 3;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;

        dialog.setLayoutParams(params);
    }

    /**
     * タイトル文字列の初期化
     */
    private void initTitleText() {
        TextView title = getDialog().findViewById(R.id.register_day);
        title.setText(mRegisterDayStr);
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

        // 初期値として""を登録しておく
        mSchedule.setContent("");
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
        // 初期値として00:00を登録しておく
        mSchedule.setStart(mRegisterDayStr + " " + DEFUALT_TIME);

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

        // 初期値として00:00を登録しておく
        mSchedule.setEnd(mRegisterDayStr + " " + DEFUALT_TIME);
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
     * 10分前に通知するチェックボタンの初期化処理
     */
    private void initNotificationButton() {
        mIsNotificationTenMin = false; //デフォルトでは事前通知はなし
        final CheckedTextView checkedTextView = getDialog().findViewById(R.id.before_notification_check);
        checkedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkedTextView.toggle();
                mIsNotificationTenMin = !mIsNotificationTenMin;
            }
        });
     }

    /**
     * 登録ボタン初期化
     */
    private void initRegisterButton() {
        Button registerButton = getDialog().findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 正しい予定か確認する
                if((-1 == mSchedule.compareTo(mSchedule.getStart(), mSchedule.getEnd())) ||
                        (0 == mSchedule.compareTo(mSchedule.getStart(), mSchedule.getEnd()))) {
                    // 時間設定が正しくないため、その旨を通知し保存も何もしない
                    Toast.makeText(getContext(), getString(R.string.error_time), Toast.LENGTH_LONG).show();
                    return;
                }

                SsaScheduleManager manager = SsaScheduleManager.getInstance();

                // 現在時刻より新しい時間かを確認する
                if(manager.isOldSchedule(mSchedule)) {
                    // 古い予定のため、登録時間が正しくない旨のメッセージを表示して、保存も何もしない
                    Toast.makeText(getContext(), getString(R.string.error_time), Toast.LENGTH_LONG).show();
                    return;
                }

                // 開始時刻が重複していないか確認する
                 if(manager.isBooking(mSchedule)) {
                     // 重複しているため、その旨を通知して保存も何もしない
                     Toast.makeText(getContext(), getString(R.string.error_booking_schedule), Toast.LENGTH_LONG).show();
                     return;
                 }


                // 予定保存処理
                if(mIsNotificationTenMin) {
                    // 10分前予定保存処理
                    SsaSchedule notification = manager.createNotificationSchedule(mSchedule);
                    manager.addSchedule(getContext(), notification);
                }

                manager.addScheduleAndReadDB(getContext(), mSchedule);

                mListener.onRegisterButtonClick();
                getDialog().dismiss();
            }
        });
    }
}
