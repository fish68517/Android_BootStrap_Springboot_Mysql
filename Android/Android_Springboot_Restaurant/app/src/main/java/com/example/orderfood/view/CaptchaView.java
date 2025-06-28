
package com.example.orderfood.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import java.util.Random;

public class CaptchaView extends View {

    private String captchaCode; // 存储生成的验证码
    private Paint paint = new Paint();
    private Random random = new Random();

    // 用于生成验证码的字符池（去除了容易混淆的 0, o, 1, i, l）
    private static final char[] CHARS = {
            '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M',
            'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };

    // 构造函数
    public CaptchaView(Context context) {
        super(context);
        init();
    }

    public CaptchaView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CaptchaView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // 设置点击监听，点击即可刷新验证码
        setOnClickListener(v -> refresh());
        paint.setAntiAlias(true); // 抗锯齿
        refresh(); // 初始化时生成第一个验证码
    }

    /**
     * 刷新验证码
     */
    public void refresh() {
        captchaCode = generateCode();
        invalidate(); // 请求重新绘制 View
    }

    /**
     * 获取当前验证码字符串，用于验证
     * @return 验证码字符串
     */
    public String getCode() {
        return captchaCode;
    }

    // 生成一个4位的随机验证码
    private String generateCode() {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            buffer.append(CHARS[random.nextInt(CHARS.length)]);
        }
        return buffer.toString();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (captchaCode == null) return;

        int width = getWidth();
        int height = getHeight();

        // 1. 绘制背景
        paint.setColor(Color.rgb(240, 243, 250)); // 淡蓝色背景
        canvas.drawRect(0, 0, width, height, paint);

        // 2. 绘制验证码字符
        int charWidth = width / 5;
        for (int i = 0; i < captchaCode.length(); i++) {
            char c = captchaCode.charAt(i);
            // 设置随机颜色、大小和样式
            paint.setColor(getRandomColor());
            paint.setTextSize(height * 0.6f + random.nextInt(20)); // 字体大小
            paint.setFakeBoldText(random.nextBoolean()); // 是否粗体

            // 设置随机位置和旋转角度
            float x = charWidth * (i + 0.5f);
            float y = height * 0.7f;
            float degree = random.nextInt(45) - 20; // 旋转角度: -20 到 25 度

            canvas.save(); // 保存画布状态
            canvas.rotate(degree, x, y);
            canvas.drawText(String.valueOf(c), x, y, paint);
            canvas.restore(); // 恢复画布状态
        }

        // 3. 绘制干扰线
        for (int i = 0; i < 5; i++) {
            paint.setColor(getRandomColor());
            paint.setStrokeWidth(3); // 线宽
            canvas.drawLine(random.nextInt(width), random.nextInt(height),
                    random.nextInt(width), random.nextInt(height), paint);
        }
    }

    // 获取一个随机的深色，让文字清晰可见
    private int getRandomColor() {
        return Color.rgb(random.nextInt(150), random.nextInt(150), random.nextInt(150));
    }
}