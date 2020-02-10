package com.study.ssa.Dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.study.ssa.R;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * 予定登録ダイアログ
 */
public class RegisterDialogFragment extends DialogFragment {

    public static final String KEY_SELECT_DAY = "select_day";

    private String mRegisterDayStr;

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
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        TextView title = (TextView) getDialog().findViewById(R.id.register_day);
        title.setText(mRegisterDayStr);
    }
}
