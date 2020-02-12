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

import com.study.ssa.UI.Adapter.ScheduleListAdapter;
import com.study.ssa.R;
import com.study.ssa.SsaSchedule.SsaScheduleManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * schedule一覧表示ダイアログ
 */
public class ScheduleListDialogFragment extends DialogFragment {

    public interface onScheduleListDialogListener {
        public void onDismiss();
    }

    public static final String KEY_DAY = "day";

    private String mDayStr;
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

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            Date date = format.parse(mDayStr);
            ScheduleListAdapter adapter = new ScheduleListAdapter(mManager.getScheduleItem(date));

            LinearLayoutManager llm = new LinearLayoutManager(getActivity());

            recyclerView.setHasFixedSize(true);

            recyclerView.setLayoutManager(llm);

            recyclerView.setAdapter(adapter);

        } catch (ParseException e) {
            //何もしない
        }
    }
}
