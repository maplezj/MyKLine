package com.my.kline.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ENZ on 2016/11/25.
 */

public class LineView extends View {

    private Context context;

    private int viewSize;//获取空间的尺寸，也就是我们布局的尺寸大小（不知道理解的是否正确）
    private Paint linePaint;// 线条画笔和点画笔

    private Canvas mCanvas;

    private Path mPath;// 路径对象
    private TextPaint mTextPaint;// 文字画笔

    private List<PointF> pointFs = new ArrayList<>();// 数据列表
    private float[] rulerX, rulerY;// xy轴向刻度

    //上下左右坐标点
    private float lift;
    private float top;
    private float right;
    private float buttom;

    private float maxX;//x轴最大值
    private float maxY;//Y轴最大值

    private float spaceX, spaceY;// 刻度间隔
    private int maxCount = 7;
    private float maxX_Value = 7f;
    private float maxY_Value = 7f;

    /*
    * 绘制X和Y轴对应的文字
    * */
    String[] index_x = {"1月20日", "1月21日", "1月22日", "1月23日", "1月24日", "1月25日", "1月26日", "", ""}; // x轴数值
    String[] mIndex_y = {"35.5" , "36" , "36.5" , "37" , "37.5" , "38" , "38.5" , "39"};  // y轴数值


    public LineView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //第一步，初始化对象
        linePaint = new Paint();
        linePaint.setColor(Color.WHITE);//线条的颜色
        linePaint.setStrokeWidth(2);//线条的宽度
        linePaint.setAntiAlias(true);//取消锯齿
        linePaint.setStyle(Paint.Style.STROKE);//粗线

        //初始化Path
        mPath = new Path();

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.LINEAR_TEXT_FLAG);
        mTextPaint.setColor(Color.WHITE);

        mCanvas = new Canvas();
    }

    public LineView(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //第二步骤，我们在这里获取每个用到的坐标点和尺寸

        viewSize = w;//获取空间的尺寸
        Log.i("Text", "viewSize:" + viewSize);

        //这个是我们上下左右需要用到的坐标点
        lift = viewSize * (2 / 16f);
        top = viewSize * (2 / 16f);
        right = viewSize * (15 / 16f);
        buttom = viewSize * (8 / 16f);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 锁定画布
        canvas.save();

        //定义一个绘制X,Y轴的方法
        drawXY(canvas);

        //最后绘制我们的点和线
        drawbitmaps(canvas);

    }

    private void drawXY(Canvas canvas) {

        mPath.moveTo(lift, top);
        mPath.lineTo(lift, buttom);
        mPath.lineTo(right, buttom);

        //使用Path链接这三个坐标
        canvas.drawPath(mPath, linePaint);

        drawLines(canvas);
        // 释放画布
        canvas.restore();
    }

    private void drawLines(Canvas canvas) {
        /*
        * 1、我们需要知道X,Y轴的最大值是多少
        * 2、我们需要知道我们在X,Y轴分别有多少个点，然后每个点之间的间距是多少
        * 3、绘制网格线
        * */

        // 重置线条画笔，因为是细线，所有我这里设置了2。
        linePaint.setStrokeWidth(2);

        // 横坐标7个间隔
        int count = /*pointFs.size()*/ maxCount;

        // 计算横纵坐标刻度间隔
        spaceY = (buttom - top) / count;
        spaceX = (right - lift) / count;

        // 计算除数的值为数据长度减一，7个数据，6条线。
        int divisor = count - 1;

        // 计算横轴数据最大值
        maxX = 0;
        for (int i = 0; i < pointFs.size(); i++) {
            if (maxX < pointFs.get(i).x) {
                maxX = pointFs.get(i).x;//X轴最大坐标

            }
        }
        Log.i("Text", "maxX:--" + maxX);
        // 计算横轴最近的能被count整除的值
        int remainderX = ((int) maxX) % divisor;
        maxX = remainderX == 0 ? ((int) maxX) : divisor - remainderX + ((int) maxX);

        Log.i("Text", "maxX最终值:--" + maxX + "remainderX的值为：" + remainderX);

        // 计算纵轴数据最大值
        maxY = 0;
        for (int i = 0; i < pointFs.size(); i++) {
            if (maxY < pointFs.get(i).y) {
                maxY = pointFs.get(i).y;
            }
        }
        Log.i("Text", "maxY:--" + maxY);
        // 计算纵轴最近的能被count整除的值
        int remainderY = ((int) maxY) % divisor;

        if (remainderY == 0 && maxY == 0) {
            maxY = 0;
        } else {
            maxY = divisor - remainderY + ((int) maxY);
        }

        // 生成横轴刻度值
        rulerX = new float[count];
        for (int i = 0; i < count; i++) {
            rulerX[i] = maxX / divisor * i;
        }

        // 生成纵轴刻度值
        rulerY = new float[count];
        for (int i = 0; i < count; i++) {
            rulerY[i] = maxY / divisor * i;
        }

        // 锁定画布并设置画布透明度为100%,使线条的颜色变浅色
        int sc = canvas.saveLayerAlpha(0, 0, canvas.getWidth(), canvas.getHeight(), 255, Canvas.ALL_SAVE_FLAG);

        // 绘制横纵线段
        for (float y = buttom - spaceY; y >= top; y -= spaceY) {
            Log.i("Text", "y" + y);

            for (float x = lift; x <= right; x += spaceX) {
                Log.i("Text", "x" + x);
                /*
                 * 绘制纵向线段
                 */
                if (y == top + spaceY) {
                    canvas.drawLine(x, buttom, x, buttom + 10, linePaint);
                }

                /*
                 * 绘制横向线段
                 */
                if (x == right) {
                    canvas.drawLine(x, y, x - spaceX * count, y, linePaint);
                }
            }
        }

        // 还原画布
        canvas.restoreToCount(sc);

        int num = 0;//用于给X轴赋值
        int num_y = 0;//用于给Y轴赋值

        for (float y = buttom; y >= top; y -= spaceY) {
            for (float x = lift; x < right; x += spaceX) {
                mTextPaint.setTextSize(28);

                /*
                 * 绘制横轴刻度数值
                 */
                if (y == buttom) {
                    canvas.drawText(String.valueOf(index_x[num]), x , buttom + (top / 3), mTextPaint);
                }
                /*
                 * 绘制纵轴刻度数值
                 * 简单来说就是，Y轴上的坐标点，X轴是恒定不变的，但注意是Y轴是变化的
                 */
                if (x == lift) {
                    canvas.drawText(String.valueOf(mIndex_y[num_y]), lift - (lift / 2), y + 10, mTextPaint);
                }

                num++;
            }
            num_y++;
        }
    }

    private void drawbitmaps(Canvas canvas) {
        Bitmap mBitmap = Bitmap.createBitmap((int) (right - lift - spaceX), (int) (buttom - top - spaceY), Bitmap.Config.ARGB_8888);
        mCanvas.setBitmap(mBitmap);

        /*
        * 为画布填充一个半透明的红色
        * drawARGB(a,r,g,b)
        * a:透明度
        * r:红色
        * g:绿色
        * b:蓝色
        * */
//        mCanvas.drawARGB(55, 255, 0, 0);

        // 重置曲线
        mPath.reset();
        // 将mBitmap绘制到原来的canvas
        canvas.drawBitmap(mBitmap, lift, top + spaceY, null);

        //绘制我们的坐标点
        drawText(canvas);
    }

    private void drawText(Canvas canvas) {
        Paint pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        pointPaint.setStyle(Paint.Style.FILL);//焦点的类型
        pointPaint.setColor(Color.BLUE);//焦点的颜色

        if (pointFs.size() == 0) {
            Toast.makeText(context, "暂无数据", Toast.LENGTH_SHORT).show();
        } else {
            for (int i = 0; i < pointFs.size(); i++) {
                // 计算x坐标
                float x = mCanvas.getWidth() / maxX * pointFs.get(i).x;
                // 计算y坐标
                float y = mCanvas.getHeight() / maxY * pointFs.get(i).y;
                y = mCanvas.getHeight() - y;

                // 绘制小点点
                mCanvas.drawCircle(x, y, 6, pointPaint);

            /*
             * 如果是第一个点则将其设置为Path的起点
             */
                if (i == 0) {
                    mPath.moveTo(x, y);
                }

                Log.i("Text" , "绘制点的X坐标：" + x + "绘制点的Y坐标：" + y + "maxX值为：" + maxX);
                // 连接各点
                mPath.lineTo(x, y);
            }

            // 设置PathEffect
            linePaint.setPathEffect(new CornerPathEffect(10));

            // 重置线条宽度
            linePaint.setStrokeWidth(4);

            linePaint.setColor(Color.BLUE);
            // 将Path绘制到我们自定的Canvas上
            mCanvas.drawPath(mPath, linePaint);
        }
    }

    public synchronized void setData(List<PointF> pointFs, String signX, String signY, Activity activity) {
        /*
         * 数据为空直接GG
         */
        if (null == pointFs || pointFs.size() == 0)
            throw new IllegalArgumentException("No data to display !");

        /*
         * 控制数据长度不超过10个
         * 对于折线图来说数据太多就没必要用折线图表示了而是使用散点图
         */
//        if (pointFs.size() > 10)
//            throw new IllegalArgumentException("The data is too long to display !");

        // 设置数据并重绘视图
        this.pointFs = pointFs;
        this.context = activity;

        invalidate();
    }
}