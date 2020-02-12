package com.study.ssa.UI.CustomWidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.study.ssa.R;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 高さを自動計算するRecyclerView
 */
public class MesuredRecyclerView extends RecyclerView {
    private int mMaxHeight;

    public MesuredRecyclerView(Context context) {
        super(context);
    }
    public MesuredRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }
    public MesuredRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }
    private void initialize(Context context, AttributeSet attrs) {
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.MaxHeightScrollView);
        mMaxHeight = arr.getLayoutDimension(R.styleable.MaxHeightScrollView_maxHeight, mMaxHeight);
        arr.recycle();
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mMaxHeight > 0) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxHeight, MeasureSpec.AT_MOST);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
