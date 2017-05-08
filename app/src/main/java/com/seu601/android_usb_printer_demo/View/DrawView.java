package com.seu601.android_usb_printer_demo.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by ylx on 2017/5/3.
 */

public class DrawView extends View {
    float preX;
    float preY;
    private Path path;
    public Paint paint = null;
    int VIEW_WIDTH = 1100;
    int VIEW_HEIGHT = 1200;
    // 定义一个内存中的图片，该图片将作为缓冲区
    public Bitmap cacheBitmap = null;
    // 定义cacheBitmap上的Canvas对象
    Canvas cacheCanvas = null;

    public DrawView(Context context, AttributeSet set) {

        super(context, set);
        // 创建一个与该View相同大小的缓存区
//        Log.e(new StringBuilder().append(VIEW_WIDTH).toString(), new StringBuilder().append(VIEW_HEIGHT).toString());
//        Log.e(new StringBuilder().append(getRootView().getRootView().getWidth()).toString(), new StringBuilder().append(getRootView().getRootView().getHeight()).toString());
        cacheBitmap = Bitmap.createBitmap(VIEW_WIDTH, VIEW_HEIGHT,
                Bitmap.Config.ARGB_8888);
        cacheCanvas = new Canvas();
        path = new Path();
        // 设置cacheCanvas将会绘制到内存中的cacheBitmap上
        cacheCanvas.setBitmap(cacheBitmap);// ylxNOTICE (2017/5/3)  设置cacheCanvas将会绘制到内存中的cacheBitmap上
        // 设置画笔的颜色
        paint = new Paint(Paint.DITHER_FLAG);
        paint.setColor(Color.RED);
        // 设置画笔风格
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
        // 反锯齿
        paint.setAntiAlias(true);
        paint.setDither(true);
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 获取拖动事件的发生位置
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);
                preX = x;
                preY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                path.quadTo(preX, preY, x, y);
                preX = x;
                preY = y;
                break;
            case MotionEvent.ACTION_UP:
                cacheCanvas.drawPath(path, paint); // ①
                path.reset();
                break;
        }
        invalidate();
        // 返回true表明处理方法已经处理该事件
        return true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        Paint bmpPaint = new Paint();
        // 将cacheBitmap绘制到该View组件上
        canvas.drawBitmap(cacheBitmap, 0, 0, bmpPaint); // ②
        // 沿着path绘制
        canvas.drawPath(path, paint);
    }

}
