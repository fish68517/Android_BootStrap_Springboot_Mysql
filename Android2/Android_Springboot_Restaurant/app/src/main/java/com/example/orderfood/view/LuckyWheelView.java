package com.example.orderfood.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class LuckyWheelView extends View {
    private Paint paint;
    private Paint textPaint;
    private RectF rectF;
    private final String[] prizes = {"满100减20券", "85折优惠券", "再来一杯咖啡", "谢谢惠顾"};
    private int[] colors = {
            Color.parseColor("#FFF4E1"),
            Color.parseColor("#FFE0B2"),
            Color.parseColor("#FFF4E1"),
            Color.parseColor("#FFE0B2")
    };

    public LuckyWheelView(Context context) {
        super(context);
        init();
    }

    public LuckyWheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(30);
        textPaint.setColor(Color.parseColor("#795548"));
        textPaint.setTextAlign(Paint.Align.CENTER);

        rectF = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        int radius = Math.min(width, height) / 2;
        
        // 设置转盘范围
        rectF.set(0, 0, width, height);
        
        // 绘制扇形区域
        float startAngle = -90; // 从正上方开始
        float sweepAngle = 360f / prizes.length;
        
        for (int i = 0; i < prizes.length; i++) {
            // 绘制扇形
            paint.setColor(colors[i]);
            canvas.drawArc(rectF, startAngle, sweepAngle, true, paint);
            
            // 绘制文字
            float angle = (float) Math.toRadians(startAngle + sweepAngle / 2);
            float x = (float) (width / 2 + radius * 0.6 * Math.cos(angle));
            float y = (float) (height / 2 + radius * 0.6 * Math.sin(angle));
            
            // 旋转文字
            canvas.save();
            canvas.rotate(startAngle + sweepAngle / 2 + 90, x, y);
            canvas.drawText(prizes[i], x, y, textPaint);
            canvas.restore();
            
            startAngle += sweepAngle;
        }
    }
} 