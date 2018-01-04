package gionee.com.catchchick.CustomView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Created by chk on 17-12-21.
 */

public class StrengthView extends View{

    public static int DEVICE_WIDTH;
    public static int DEVICE_HEIGHT;
    int mViewWidth;
    int mViewHeight;

    float mStrengthViewHeight;
    float mStrengthViewWidth;
    float mCurrentHeight;

    Paint mPaintStrength;
    Paint mPaintFrame;
    Paint mPaintBackground;
    LinearGradient mLinearGradient; //设置渐变

    Handler mHandler;

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

        mPaintStrength = new Paint();
        mPaintStrength.setStrokeWidth(5);
        mPaintStrength.setAntiAlias(true);

        mPaintFrame = new Paint();
        mPaintFrame.setStrokeWidth(20);
        mPaintFrame.setStyle(Paint.Style.STROKE);
        mPaintFrame.setColor(Color.rgb(0xff,0xa5,0));
        mPaintFrame.setAntiAlias(true);

        mPaintBackground = new Paint();
        mPaintBackground.setStrokeWidth(5);
        mPaintBackground.setStyle(Paint.Style.FILL);
        mPaintBackground.setColor(Color.rgb(0xFF,0x7f,0x24));
        mPaintBackground.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);
        drawStrength(canvas);
        drawFrame(canvas);
    }

    private void drawStrength(Canvas canvas) {
        canvas.save();
        canvas.translate(0,mViewHeight/6);
        canvas.drawRoundRect(0,mStrengthViewHeight-mCurrentHeight,mViewWidth,mStrengthViewHeight,40,20, mPaintStrength);
        canvas.restore();
    }

    private void drawBackground(Canvas canvas) {
        canvas.save();
        canvas.translate(0,mViewHeight/6);
        canvas.drawRoundRect(0,0,mViewWidth,mStrengthViewHeight,10,10,mPaintBackground);
        canvas.restore();
    }

    //绘制边框
    private void drawFrame(Canvas canvas) {
        canvas.save();
        canvas.translate(0,mViewHeight/6);
        canvas.drawRoundRect(0,0, mViewWidth, mStrengthViewHeight,10,10, mPaintFrame);
        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureWidth(widthMeasureSpec),measureHeight(heightMeasureSpec));
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = getWidth();
        mViewHeight = getHeight();

        mStrengthViewHeight = mViewHeight * 2/3f;
        mStrengthViewWidth = mViewWidth;
        mCurrentHeight = mStrengthViewHeight/2;

        mLinearGradient = new LinearGradient(0,0, mViewWidth, mViewHeight,Color.rgb(0xFF,0,0xFF), Color.rgb(0x8e,0xe5,0xee), Shader.TileMode.CLAMP);
        mPaintStrength.setShader(mLinearGradient);
    }

    /**
     * 测量view的宽度
     * @param widthMeasureSpec
     * @return
     */
    private int measureWidth(int widthMeasureSpec) {
        int result;
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

    /**
     * 设置力量大小
     * @param strength
     */
    public void setStrength(int strength) {
        mCurrentHeight = strength/100f * mStrengthViewHeight;
        invalidate();
    }

    /**
     * 设置Handler用于和主线程进行通信
     * @param handler
     */
    public void setHandler(Handler handler) {
        mHandler = handler;
    }

}
