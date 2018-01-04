package gionee.com.catchchick.CustomView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import gionee.com.catchchick.R;

/**
 * Created by chk on 18-1-3.
 */

public class ChickView extends SurfaceView implements SurfaceHolder.Callback{

    SurfaceHolder mSurfaceHolder;

    int mViewWidth;
    int mViewHeight;
    float mStrengthViewHeight;
    float mStrengthViewWidth;
    float mCurrentHeight;

    Canvas canvas;
    Paint mPaintStrength;
    Paint mPaintFrame;
    Paint mPaintBackground;
    LinearGradient mLinearGradient; //设置渐变

    Thread mThread;
    boolean isOnRun = true;
    int sleepTime = 40; //休眠时间

    boolean isOnTouch;  //判断是够触摸屏幕
    int strength;

    Bitmap mBitmap;
    int mBitmapWidth;
    int mBitmapHeight;
    int whichFrame = 0;
    boolean isMoveLeft = true;

    public ChickView(Context context) {
        super(context);
        init();
    }

    public ChickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);

        mPaintStrength = new Paint();
        mPaintStrength.setStrokeWidth(5);
        mPaintStrength.setAntiAlias(true);
        mPaintStrength.setStyle(Paint.Style.FILL);

        mPaintFrame = new Paint();
        mPaintFrame.setStrokeWidth(20);
        mPaintFrame.setStyle(Paint.Style.STROKE);
        mPaintFrame.setColor(Color.parseColor("#FFA500"));
        mPaintFrame.setAntiAlias(true);

        mPaintBackground = new Paint();
        mPaintBackground.setStrokeWidth(5);
        mPaintBackground.setStyle(Paint.Style.FILL);
        mPaintBackground.setColor(Color.parseColor("#FF7F24"));
        mPaintBackground.setAntiAlias(true);

        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.run);
        mBitmapWidth = mBitmap.getWidth()/8;
        mBitmapHeight = mBitmap.getHeight();

        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isOnRun) {
                    onMyDraw();
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
//        onMyDraw();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mViewWidth = width;
        mViewHeight = height;
        mStrengthViewHeight = height*3/5 - 20;
        mStrengthViewWidth = width/10;
        Log.i("ChickView",mStrengthViewWidth+" "+mStrengthViewHeight);
        mLinearGradient = new LinearGradient(0,0, mStrengthViewWidth, mStrengthViewHeight,
                Color.parseColor("#FF00FF"), Color.parseColor("#8EE5EE"), Shader.TileMode.CLAMP);
        mPaintStrength.setShader(mLinearGradient);
//        onMyDraw();
        if (!mThread.isAlive())
            mThread.start();
        Log.i("ChickView","SurfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isOnRun = false;
    }

    /**
     * 绘图方法
     */
    public void onMyDraw() {
        setCurrentHeightWithTouch();
        setWhichFrameWidthTouch();
        canvas = mSurfaceHolder.lockCanvas();
        if (canvas != null) {
            canvas.drawColor(Color.BLACK);
            drawBackground(canvas);
            drawStrength(canvas);
            drawFrame(canvas);
            drawChick(canvas);
            mSurfaceHolder.unlockCanvasAndPost(canvas);
        }

    }

    //绘制力量大小
    private void drawStrength(Canvas canvas) {
        canvas.save();
        canvas.translate(10,mViewHeight/5);
        if (mCurrentHeight >= 1) {
            canvas.drawRect(0,mStrengthViewHeight-mCurrentHeight,mStrengthViewWidth,mStrengthViewHeight-10, mPaintStrength);
        }
        canvas.restore();
    }

    //绘制背景
    private void drawBackground(Canvas canvas) {
        canvas.save();
        canvas.translate(10,mViewHeight/5-10);
        canvas.drawRoundRect(0,0,mStrengthViewWidth,mStrengthViewHeight,10,10,mPaintBackground);
        canvas.restore();
    }

    //绘制边框
    private void drawFrame(Canvas canvas) {
        canvas.save();
        canvas.translate(10,mViewHeight/5-10);
        canvas.drawRoundRect(0,0, mStrengthViewWidth, mStrengthViewHeight,10,10, mPaintFrame);
        canvas.restore();
    }

    private void drawChick(Canvas canvas) {
        canvas.save();
        canvas.translate(mViewWidth/4,mViewHeight*2/5);
        canvas.clipRect(0,0,mBitmapWidth,mBitmapHeight);
        canvas.drawBitmap(mBitmap,-whichFrame * mBitmapWidth,0,mPaintBackground);
        canvas.restore();
    }

    /**
     * 根据力量大小设置力量条高度
     */
    public void setCurrentHeight(int strength) {
        mCurrentHeight = strength/100f * mStrengthViewHeight;
    }

    public void setCurrentHeightWithTouch() {
        if (isOnTouch) {
            if (strength < 100){
                strength += 1;
            }
        } else {
            if (strength > 0){
                strength--;
            }
        }
//        if (strength == 30)
//            sleepTime = 30;
//        if (sleepTime == 60)
//            sleepTime = 20;
//        if (sleepTime == 100)
//            sleepTime = 10;
        setCurrentHeight(strength);
    }

    public void setWhichFrameWidthTouch() {
        if (isOnTouch) {
            if (isMoveLeft) {
                if (whichFrame++ >= 7) {
                    isMoveLeft = false;
                    whichFrame = 7;
                }
            } else {
                if (--whichFrame < 0) {
                    isMoveLeft = true;
                    whichFrame = 0;
                }
            }
        }
//        else {
//            if (whichFrame != 0) {
//                whichFrame--;
//            }
//        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isOnTouch = true;
//                if (isMoveLeft) {
//                    if (whichFrame++ >= 7) {
//                        isMoveLeft = false;
//                        whichFrame = 7;
//                    }
//                } else {
//                    if (--whichFrame < 0) {
//                        isMoveLeft = true;
//                        whichFrame = 0;
//                    }
//                }
                break;
            case MotionEvent.ACTION_UP:
                isOnTouch = false;
                break;
        }
        return true;
    }
}
