package org.edx.mobile.eliteu.wight;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.view.View;

public class RoundedProgressBar extends View {

    private int mViewWidth;
    private int mViewHeight;
    private int mMaxProgress = 100;//最大进度
    private int mProgress = 0;//当前进度

    Paint onDrawPaint;
    Xfermode xfermode;

    private String colorBcg;
    private String colorProgress;

    public RoundedProgressBar(Context context) {
        this(context, null);
    }

    public RoundedProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundResource(0);//移除设置的背景资源

        xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP);

        onDrawPaint = new Paint();
        onDrawPaint.setFilterBitmap(false);

        colorBcg = "#d8d8d8";
        colorProgress = "#8cc34a";
        //需要注意的是，如果颜色设置透明度，叠加后的图也会因为设置的透明度而变化，
        //且会因 xfermode 的各种模式而受到影响，导致跟预期不同
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mViewWidth = getMeasuredWidth();
        mViewHeight = getMeasuredHeight();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = (int) (mViewWidth * (mProgress * 1.0f / mMaxProgress) + 0.5f);
//        int saved = canvas.saveLayer(0,0,mViewWidth,mViewHeight, null, Canvas.ALL_SAVE_FLAG); //设定生效的区域
        int saved = canvas.saveLayer(null, null, Canvas.ALL_SAVE_FLAG);
        onDrawPaint.setColor(Color.parseColor(colorBcg));
        canvas.drawRoundRect(new RectF(0, 0, mViewWidth, mViewHeight), mViewHeight / 2, mViewHeight / 2, onDrawPaint);
        onDrawPaint.setXfermode(xfermode);
        onDrawPaint.setColor(Color.parseColor(colorProgress));
        canvas.drawRoundRect(new RectF(width-mViewWidth, 0, width, mViewHeight), mViewHeight / 2, mViewHeight / 2, onDrawPaint);
        onDrawPaint.setXfermode(null);
        canvas.restoreToCount(saved);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
        invalidate();
    }

    /**
     * 设置当前进度
     *
     * @param progress
     */
    public void setProgress(int progress) {
        progress = progress >= 0 ? progress : 0;
        progress = progress <= mMaxProgress ? progress : mMaxProgress;
        mProgress = progress;
        invalidate();
    }

    /**
     * 设置最大进度
     *
     * @param maxProgress
     */
    public void setMaxProgress(int maxProgress) {
        mMaxProgress = maxProgress;
        invalidate();
    }

    public void setColorBcg(String co) {
        colorBcg = co;
        invalidate();
    }

    public void setColorProgress(String col) {
        colorProgress = col;
        invalidate();
    }
}
