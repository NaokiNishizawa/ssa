package com.study.ssa.UI;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.study.ssa.CharacterData.CharacterInfo;
import com.study.ssa.CharacterData.CharacterInfoManager;
import com.study.ssa.R;
import com.study.ssa.Receiver.AlarmReceiver;
import com.study.ssa.Receiver.BootReceiver;
import com.study.ssa.SsaSchedule.SsaSchedule;
import com.study.ssa.SsaSchedule.SsaScheduleManager;
import com.study.ssa.UI.Adapter.CalendarAdapter;
import com.study.ssa.UI.Dialog.BaseTimerDialogFragment;
import com.study.ssa.UI.Dialog.CountDownTimerDialogFragment;
import com.study.ssa.UI.Dialog.CountUpTimerDialogFragment;
import com.study.ssa.UI.Dialog.OptionDialogFragment;
import com.study.ssa.UI.Dialog.RegisterDialogFragment;
import com.study.ssa.UI.Dialog.ScheduleListDialogFragment;
import com.study.ssa.UI.Dialog.TimeSettingsDialogFragment;
import com.study.ssa.Util.SharedPreferencesUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends FragmentActivity
        implements RegisterDialogFragment.OnButtonClickListener, ScheduleListDialogFragment.onScheduleListDialogListener,
        BaseTimerDialogFragment.OnFinishClickListener, TimeSettingsDialogFragment.OnFinishClickListener,
        OptionDialogFragment.OnButtonClickListener
{

    private TextView mGetMoneyText;

    private TextView mTitleText;
    private ImageButton mPrevButton;
    private ImageButton mNextButton;
    private CalendarAdapter mCalendarAdapter;
    private GridView mCalendarGridView;

    private DisplayScheduleFragment mDisplayFragment;

    private TextView mTodayAlertScheduleCountText;
    private TextView mTodayTimerScheduleCountText;

    private SsaScheduleManager mManager;
    private Date mToday;

    /** AlarmReceiverから遷移してきたかを表す */
    private boolean mIsCalledReceiver;

    /** 表示中 TimerDialog */
    private BaseTimerDialogFragment mTimerDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Receiverから呼ばれた場合はすぐにタイマーモードに遷移したいため、遷移元を確認する
        mIsCalledReceiver = getIntent().getBooleanExtra(AlarmReceiver.KEY_CALLED_RECEIVER, false);

        // 本日のDateを保持
        Calendar calendar = Calendar.getInstance();
        mToday = calendar.getTime();
    }

    @Override
    public void onStart() {
        super.onStart();

        // DBから予定を取得する
        mManager = SsaScheduleManager.getInstance();
        mManager.init(getApplicationContext());

        // Receiver初期化
        initReceiver();

        // キャラクター画像初期化
        initSelectedCharacterImage();

        // レイアウト初期化
        initLayout();

        // 本日の予定数表示エリア初期化
        initTodayScheduleCountArea();

        // ヘッダーView群の初期化
        initHeaderView();

        // Receiverから呼ばれた場合はすぐにタイマーモードに遷移する
        if(mIsCalledReceiver) {
            // タイマーモード起動
            Log.d("debug", "called AlarmReceiver");
            SsaSchedule schedule = (SsaSchedule) getIntent().getSerializableExtra(AlarmReceiver.KEY_SCHEDULE_OBJECT);
            showCountDownTimerDialog(schedule);
        }
    }

    /**
     * 必要なReceiverを初期化する
     */
    private void initReceiver() {
        // Receiverは明示的に有効にしないとうまく動作しないことがあるため、明示的に有効にする
        // こうすることでアプリで無効にしない限り無効にならない。
        // 現状は無効にしていないため、永久的に有効
        ComponentName bootReceiver = new ComponentName(this, BootReceiver.class);
        ComponentName alarmReceiver = new ComponentName(this, AlarmReceiver.class);
        PackageManager pm = this.getPackageManager();

        pm.setComponentEnabledSetting(bootReceiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        pm.setComponentEnabledSetting(alarmReceiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    /**
     * 選択中のキャラクター画像の初期化
     */
    private void initSelectedCharacterImage() {
        CharacterInfoManager characterInfoManager = CharacterInfoManager.getInstance();
        // 選択中のキャラクターを取得する
        CharacterInfo info = characterInfoManager.getSelectedCharacterInfo();
        if(null != info) {
            ImageView imageView = findViewById(R.id.select_character_image);
            imageView.setImageResource(getResources().getIdentifier(info.getIcon(), "mipmap", getPackageName()));
        }
    }

    /**
     * レイアウト初期化
     */
    private void initLayout() {

        Button option = findViewById(R.id.option_button);
        option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // オプションダイアログ表示
                showOptionDialog();
            }
        });

        mGetMoneyText = findViewById(R.id.shojicoin_text);
        mGetMoneyText.setText(String.valueOf(SharedPreferencesUtil.getMoneyValue(this)) + getString(R.string.get_money));

        mTitleText = findViewById(R.id.titleText);
        mPrevButton = findViewById(R.id.prevButton);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarAdapter.prevMonth();
                mTitleText.setText(mCalendarAdapter.getTitle());
            }
        });
        mNextButton = findViewById(R.id.nextButton);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarAdapter.nextMonth();
                mTitleText.setText(mCalendarAdapter.getTitle());
            }
        });
        mCalendarGridView = findViewById(R.id.calendarGridView);
        mCalendarAdapter = new CalendarAdapter(this);
        mCalendarGridView.setAdapter(mCalendarAdapter);

        mCalendarGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // 本日より過去の日付を押下された時は何もしない
                if(-1 == mCalendarAdapter.getItemDate(position).compareTo(mToday)) {
                    return;
                }

                // 登録ダイアログ表示処理
                showRegisterDialog(position);
            }
        });

        mCalendarGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // 予定一覧ダイアログ表示処理
                showScheduleListDialog(position);
                return true;
            }
        });

        mTitleText.setText(mCalendarAdapter.getTitle());

        mDisplayFragment = (DisplayScheduleFragment) getSupportFragmentManager().findFragmentById(R.id.display_next_schedule);
    }

    /**
     * 本日の予定数表示エリア初期化
     */
    private void initTodayScheduleCountArea() {
        mTodayAlertScheduleCountText = findViewById(R.id.alert_schedule_count_text);
        mTodayTimerScheduleCountText = findViewById(R.id.timer_schedule_count_text);

        // 本日の予定数を表示
        mTodayAlertScheduleCountText.setText(String.valueOf(mManager.getAlertScheduleCount(mToday)));
        mTodayTimerScheduleCountText.setText(String.valueOf(mManager.getTimerScheduleCount(mToday)));
    }

    /**
     * HeaderView群の初期化
     */
    private void initHeaderView() {
        Button timerModeButton = findViewById(R.id.timer_mode_button);
        timerModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TimeSettingDialogを表示する
                showTimerSettingDialog();
            }
        });

        Button shopButton = findViewById(R.id.shop_button);
        shopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ShopActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * オプションダイアログ表示処理
     */
    private void showOptionDialog() {
        FragmentManager manager = getSupportFragmentManager();
        OptionDialogFragment dialogFragment = new OptionDialogFragment();
        dialogFragment.show(manager, "");
    }

    /**
     * 登録ダイアログ表示処理
     *
     * @param position
     */
    private void showRegisterDialog(int position) {
        FragmentManager manager = getSupportFragmentManager();
        RegisterDialogFragment dialogFragment = new RegisterDialogFragment();

        // ダイアログに必要な情報を渡す
        Bundle args = new Bundle();
        args.putString(RegisterDialogFragment.KEY_SELECT_DAY,mCalendarAdapter.getItemDateText(position));
        dialogFragment.setArguments(args);

        // 表示
        dialogFragment.show(manager, "");
    }

    /**
     * 予定一覧ダイアログ表示処理
     *
     * @param position
     */
    private void showScheduleListDialog(int position) {

        // 予定が何もない場合は何も表示しない
        String dayStr = mCalendarAdapter.getItemDateText(position);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date date;
        try {
            date = format.parse(dayStr);
        } catch (ParseException e) {
            // エラーが起きた場合は何もせずreturn まず起こらない。
            return;
        }

        if(0 == mManager.getScheduleItem(date).size()) {
            return;
        }

        // 予定があるのでダイアログ表示
        FragmentManager manager = getSupportFragmentManager();
        ScheduleListDialogFragment dialogFragment = new ScheduleListDialogFragment();

        // ダイアログに必要な情報を渡す
        Bundle args = new Bundle();
        args.putString(ScheduleListDialogFragment.KEY_DAY,dayStr);
        dialogFragment.setArguments(args);

        // 表示
        dialogFragment.show(manager, "");
    }

    /**
     * 時間設定Dialog表示処理
     */
    private void showTimerSettingDialog() {
        FragmentManager manager = getSupportFragmentManager();
        TimeSettingsDialogFragment dialogFragment = new TimeSettingsDialogFragment();

        dialogFragment.show(manager, "");
    }

    /**
     * カウントダウンダイアログ表示処理
     *
     * @param schedule
     */
    private void showCountDownTimerDialog(SsaSchedule schedule) {
        Log.d("debug", "call showCountDownTimerDialog");
        if(null == schedule) {
            Log.d("debug", "schedule is null");
            return;
        }

        if(null != mTimerDialog) {
            // すでに表示中の場合は、終了させる
            mTimerDialog.dismiss();
        }

        Log.d("debug", "schedule is non null");
        FragmentManager manager = getSupportFragmentManager();
        mTimerDialog = new CountDownTimerDialogFragment();

        // ダイアログに必要な情報を渡す
        Bundle args = new Bundle();
        args.putSerializable(CountDownTimerDialogFragment.KEY_SCHEDULE,schedule);
        mTimerDialog.setArguments(args);

        mTimerDialog.show(manager, "");
    }

    /**
     * カウントアップダイアログ表示処理
     */
    private void showCountUpTimerDialog() {
        Log.d("debug", "call ShowCountUpTimerDialog");

        if(null != mTimerDialog) {
            // すでに表示中の場合は、終了させる
            mTimerDialog.dismiss();
        }

        FragmentManager manager = getSupportFragmentManager();
        mTimerDialog = new CountUpTimerDialogFragment();
        mTimerDialog.show(manager, "");
    }

    // RegisterDialog コールバック
    @Override
    public void onRegisterButtonClick() {
        updateView();
    }

    // ScheduleListDialog　コールバック
    @Override
    public void onDismiss() {
        updateView();
    }

    /**
     * スケジュールが変更された際に必要な更新をかける処理
     */
    private void updateView() {
        // 予定一覧ダイアログdismiss時 adapterを更新する
        mCalendarAdapter.notifyDataSetChanged();

        // 本日の予約数を更新する
        mTodayAlertScheduleCountText.setText(String.valueOf(mManager.getAlertScheduleCount(mToday)));
        mTodayTimerScheduleCountText.setText(String.valueOf(mManager.getTimerScheduleCount(mToday)));

        // 直近の予定表示Fragmentを更新
        mDisplayFragment.updateLayout();
    }

    // TimeSettingDialog コールバック
    @Override
    public void onCompleteTimerSettings(SsaSchedule schedule) {
        // 設定した時間でカウントダウンダイアログを表示
        showCountDownTimerDialog(schedule);
    }

    // TimerDialog　コールバック
    @Override
    public void onFinish(int getMoney) {

        // ダイアログは処理し終えたので初期化
        mIsCalledReceiver = false;
        mTimerDialog = null;

        // 取得金額を通知
        Toast.makeText(this, "今回取得金額: " + String.valueOf(getMoney), Toast.LENGTH_LONG).show();

        // 獲得金額を更新
        int money = SharedPreferencesUtil.getMoneyValue(this);
        mGetMoneyText.setText(String.valueOf(money) + getString(R.string.get_money));
    }

    // OptionDialogコールバック
    @Override
    public void onSettingFinish() {
        Log.d("debug", "call onSettingFinish");
        // TODO BGMの設定を変更する

    }
}
