package com.study.ssa.UI.CustomWidget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.jar.Attributes;

/**
 * 円グラフ
 */
public class ChartView extends View {

    public float rate = 0f; // 0 - 100の間で指定
    public boolean isNotClockWise = false; //false 時計回り true 反時計回り

    /**
     * コンストラクタ
     */
    public ChartView(Context context) {
        super(context);
    }

    public ChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 描画処理
     *
     * @param canvas
     */
    @Override
     protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBaseChart(canvas);
        drawValueChart(canvas);
    }

    /**
     * 円グラフの軸となる円の表示
     */
    private void drawBaseChart(Canvas canvas){
        Paint paint = new Paint();
        paint.setColor(Color.rgb(255,192,203));
        float storkeWidth = getWidth() / 8;
        paint.setStrokeWidth(storkeWidth);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        RectF rect = new RectF(storkeWidth/2, storkeWidth/2, getWidth()-storkeWidth/2, getHeight()-storkeWidth/2);
        canvas.drawArc(rect, 0f, 360f, false, paint);
    }

    /**
     * 円グラフの値を示す円(半円)の表示
     *
     * @param canvas
     */
    private void drawValueChart(Canvas canvas){
        Paint paint = new Paint();
        paint.setColor(Color.rgb(255,20,147));
        float storkeWidth = (getWidth() / 8);
        paint.setStrokeWidth(storkeWidth);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        RectF rect = new RectF(storkeWidth/2, storkeWidth/2, getWidth()-storkeWidth/2, getHeight()-storkeWidth/2);
        float angle = rate / 100 * 360; //円グラフの終了位置の指定
        if (isNotClockWise) {
            //反時計周りの場合はマイナスにする
            angle *= -1;
        }
        canvas.drawArc(rect, -90f, angle, false, paint);
    }
}
