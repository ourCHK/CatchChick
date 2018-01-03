package gionee.com.catchchick.CustomView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import gionee.com.catchchick.R;

/**
 * Created by chk on 17-12-21.
 */

public class StrengthView extends View{

    public static int DEVICE_WIDTH;
    public static int DEVICE_HEIGHT;
    int mViewWidth;
    int mViewHeight;
    int mStrengthViewHeight;
    int mStrengthViewWidth;
    int mStartX;
    int mStartY;
    int mEndX;
    int mEndY;

    Paint mPaint;
    Paint mBackgroundPaint;
    LinearGradient mLinearGradient;
    float mCurrentHeight;

    public StrengthView(Context context) {
        super(context);
        init();
    }

    public StrengthView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    void init() {
        DisplayMetrics dm = getResources().getDisplayMetrics(); //获取屏幕尺寸大小
        DEVICE_WIDTH = dm.widthPixels;
        DEVICE_HEIGHT = dm.heightPixels;

        mPaint = new Paint();
        mPaint.setStrokeWidth(5);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setStrokeWidth(10);
        mBackgroundPaint.setStyle(Paint.Style.STROKE);
        mBackgroundPaint.setColor(Color.YELLOW);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, mViewHeight - mCurrentHeight, mViewWidth, mViewHeight, mPaint);
        drawBackground(canvas);
    }


    private void drawBackground(Canvas canvas) {
        canvas.drawRoundRect(0,mViewHeight - mCurrentHeight, mViewWidth, mViewHeight,10,10,mBackgroundPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureWidth(widthMeasureSpec),measureHeight(heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = getWidth();
        mViewHeight = getHeight();
        mCurrentHeight = mViewHeight;
        mLinearGradient = new LinearGradient(0,0, mViewWidth, mViewHeight,Color.rgb(0xFF,0,0xFF), Color.rgb(0x8e,0xe5,0xee), Shader.TileMode.CLAMP);
        mPaint.setShader(mLinearGradient);
    }

    /**
     * 测量view的宽度
     * @param widthMeasureSpec
     * @return
     */
    private int measureWidth(int widthMeasureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY) {  //父亲制定大小，对应match_parent
            result = specSize;
        } else {
            result = 100;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(100,specSize);
            }
        }
        return result;
    }

    /**
     * 测量view的高度
     * @param heightMeasureSpec
     * @return
     */
    private int measureHeight(int heightMeasureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        int specSize = MeasureSpec.getSize(heightMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY) {  //父亲制定大小，对应match_parent
            result = specSize;
        } else {
            //这样，当时用wrap_content时，View就获得一个默认值200px，而不是填充整个父布局
            result = 200;
            if (specMode == MeasureSpec.AT_MOST) {  //如果说父亲给的更小的话，那么就用更小的，默认是200
                result = Math.min(200,specSize);
            }
        }
        return result;
    }

    public void setCurrentHeight(int progress) {
        this.mCurrentHeight = progress/100f * mViewHeight;
        invalidate();
    }
}
