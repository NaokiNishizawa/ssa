package com.study.ssa;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.study.ssa.Receiver.AlarmReceiver;
import com.study.ssa.Receiver.BootReceiver;
import com.study.ssa.UI.Adapter.CalendarAdapter;
import com.study.ssa.UI.Dialog.RegisterDialogFragment;
import com.study.ssa.UI.Dialog.ScheduleListDialogFragment;
import com.study.ssa.SsaSchedule.SsaScheduleManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends FragmentActivity
        implements RegisterDialogFragment.OnButtonClickListener, ScheduleListDialogFragment.onScheduleListDialogListener {

    private TextView mTitleText;
    private Button mPrevButton;
    private Button mNextButton;
    private CalendarAdapter mCalendarAdapter;
    private GridView mCalendarGridView;

    private SsaScheduleManager mManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Receiver初期化
        initReceiver();

        // レイアウト初期化
        initLayout();

        // DBから予定を取得する
        mManager = SsaScheduleManager.getInstance();
        mManager.init(getApplicationContext());
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
     * レイアウト初期化
     */
    private void initLayout() {
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
                // 登録ダイアログ表示処理
                ShowRegisterDialog(position);
            }
        });

        mCalendarGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // 予定一覧ダイアログ表示処理
                ShowScheduleListDialog(position);
                return true;
            }
        });

        mTitleText.setText(mCalendarAdapter.getTitle());
    }

    /**
     * 登録ダイアログ表示処理
     *
     * @param position
     */
    private void ShowRegisterDialog(int position) {
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
    private void ShowScheduleListDialog(int position) {

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

    // RegisterDialog コールバック
    @Override
    public void onRegisterButtonClick() {
        // 登録ダイアログ完了時コールバック adapterを更新する
        mCalendarAdapter.notifyDataSetChanged();
    }

    // ScheduleListDialog　コールバック
    @Override
    public void onDismiss() {
        // 予定一覧ダイアログdismiss時 adapterを更新する
        mCalendarAdapter.notifyDataSetChanged();
    }
}
