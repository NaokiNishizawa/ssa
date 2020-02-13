package com.study.ssa.UI.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.study.ssa.SsaSchedule.SsaSchedule;
import com.study.ssa.UI.Adapter.ScheduleListAdapter;
import com.study.ssa.R;
import com.study.ssa.SsaSchedule.SsaScheduleManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * schedule一覧表示ダイアログ
 */
public class ScheduleListDialogFragment extends DialogFragment implements ScheduleListAdapter.onRemoveScheduleListener{

    public interface onScheduleListDialogListener {
        public void onDismiss();
    }

    public static final String KEY_DAY = "day";

    private String mDayStr;
    private Date mDate;
    private List<SsaSchedule> mBeforeList;
    private ScheduleListAdapter mAdapter;
    private SsaScheduleManager mManager;
    private onScheduleListDialogListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = new Dialog(getActivity());
        // タイトル非表示
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        // フルスクリーン
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.setContentView(R.layout.schedule_list_dialog);
        // 背景を透明にする
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mDayStr = getArguments().getString(KEY_DAY);

        mManager = SsaScheduleManager.getInstance();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            mDate = format.parse(mDayStr);
            mBeforeList = mManager.getScheduleItem(mDate);
        } catch (ParseException e) {
            // 何もしない
        }

        return dialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            this.mListener = (ScheduleListDialogFragment.onScheduleListDialogListener) activity;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        //初期化処理
        setDialogWidth();
        initScheduleList();
    }

    @Override
    public void onDetach () {
        super.onDetach();

        // 画面を開いた際のデータ内容から編集された恐れがあるため、再登録する
        for(SsaSchedule item: mBeforeList) {
            mManager.deleteSchedule(getContext(), item);
        }

        List<SsaSchedule> afterList = mAdapter.getList();
        for(SsaSchedule item: afterList) {
            mManager.addSchedule(getContext(), item);
        }

        // 最後にReadDBする
        mManager.ReadDB(getContext());

        mListener.onDismiss();
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


        LinearLayout dialog = getDialog().findViewById(R.id.schedule_list_dialog_base);
        ViewGroup.LayoutParams params = dialog.getLayoutParams();

        // Changes the height and width to the specified *pixels*
        params.width = (realSize.x/5) * 3;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        dialog.setLayoutParams(params);
    }

    /**
     * Schedule Listの初期化
     */
    private void initScheduleList() {
        RecyclerView recyclerView = getDialog().findViewById(R.id.item_list);

        if(null == mDate) {
            return;
        }

        mAdapter = new ScheduleListAdapter(getContext(), mManager.getScheduleItem(mDate), this);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(llm);

        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void doRemoveSchedule() {
        // Removeボタン押下時
        if(0 == mManager.getScheduleItem(mDate).size()) {
            // アイテムがなくなったのでダイアログを閉じる
            getDialog().dismiss();
        }
    }
}
