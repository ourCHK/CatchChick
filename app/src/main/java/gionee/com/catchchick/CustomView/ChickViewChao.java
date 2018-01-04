package gionee.com.catchchick.CustomView;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import gionee.com.catchchick.R;
import gionee.com.catchchick.Utils;

/**
 * Created by ljc on 18-1-4.
 */

public class ChickViewChao extends SurfaceView implements Runnable, SurfaceHolder.Callback {

    private Resources mResources;
    private Canvas mCanvas;
    private Rect mRect;
    private Object[] drink = {"drink_", 81};
    private Context mContext;

    private Utils mUtils;
    private AudioManager mAudioManager;


    private int mStrength;
    private String packageName;

    private Thread mThread;
    private SurfaceHolder mHolder;
    public boolean isDown = false;
    public boolean isUp = false;

    private int mWidth;
    private int mHeight;

    Canvas canvas;
    Paint mPaintStrength;
    Paint mPaintFrame;
    Paint mPaintBackground;
    LinearGradient mLinearGradient; //设置渐变

    private int mViewWidth;
    private int mViewHeight;
    private float mStrengthViewHeight;
    private float mStrengthViewWidth;
    private float mCurrentHeight;

    public ChickViewChao(Context context) {
        super(context);
        mContext = context;
        init();

    }

    public ChickViewChao(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public ChickViewChao(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mThread = new Thread(this);

        mWidth = getWidth();
        mHeight = getHeight();
        mRect = new Rect(0, 0, mWidth, mHeight);

        mViewWidth = mWidth;
        mViewHeight = mHeight;
        mStrengthViewHeight = mHeight*3/5 - 20;
        mStrengthViewWidth = mWidth/10;
        Log.i("ChickView",mStrengthViewWidth+" "+mStrengthViewHeight);
        mLinearGradient = new LinearGradient(0,0, mStrengthViewWidth, mStrengthViewHeight,
                Color.parseColor("#FF00FF"), Color.parseColor("#8EE5EE"), Shader.TileMode.CLAMP);
        mPaintStrength.setShader(mLinearGradient);

        drawBitmap(R.drawable.drink_00);
        mUtils.playMusic();
        mUtils.setVolumeAndSpeed(0);



    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    /**
     * Stops the music
     * @param holder
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isDown = false;
        isUp = false;
        mUtils.stopMusic();
    }

    /**
     * Draws the bitmap and set the volumeAndSpeed
     */
    @Override
    public void run() {
        while (isDown) {
            int id = getResId(mStrength);
            mUtils.setVolumeAndSpeed(mStrength);
            drawBitmap(id);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isUp = false;
                isDown = true;
                mThread.start();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int i = 0;
                        while (isDown) {
                            setStrength(i);
                            setCurrentHeight(i);
                            SystemClock.sleep(60);
                            i++;
                        }
                    }
                }).start();
                break;
            case MotionEvent.ACTION_UP:
                isDown = false;
                isUp = true;
                mUtils.setVolumeAndSpeed(0);
                setCurrentHeight(0);
                int id = getResId(0);
                drawBitmap(id);
                break;
            default:
                break;

        }
        return true;
    }

    /**
     * init the view
     */
    private void init() {
        mHolder = getHolder();
        mHolder.addCallback(this);
        mResources = getResources();
        setFocusable(true);
        setFocusableInTouchMode(true);
        setKeepScreenOn(true);
        packageName = mContext.getPackageName();
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        mUtils = new Utils(mContext, mAudioManager);

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
    }

    /**
     * Sets the strength
     * @param strength
     */
    public void setStrength(int strength) {
        if (strength > 80) {
            mStrength = 80;
        } else {
            mStrength = strength;
        }
    }

    /**
     * Gets the Id by strength
     * @param strength
     * @return
     */
    private int getResId(int strength) {
        String name = strength < 10 ? drink[0].toString() + 0 + strength : drink[0].toString() + strength;
        int resId = mResources.getIdentifier(name, "drawable", packageName);
        return resId;
    }

    /**
     * Gets the bitmap by id
     * @param id
     * @return
     */
    private Bitmap getBitmap(int id) {
        Drawable drawable = mResources.getDrawable(id, null);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        Bitmap bitmap = bitmapDrawable.getBitmap();
        return bitmap;
    }

    /**
     * Draws the bitmap by id
     * @param id
     */
    private synchronized void drawBitmap(int id) {
        Bitmap bitmap = getBitmap(id);
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        Rect rect = new Rect(0, 0, bitmapWidth, bitmapHeight);

        try {
            mCanvas = mHolder.lockCanvas();
            mCanvas.drawBitmap(bitmap, rect, mRect, null);

            drawBackground(mCanvas);
            drawStrength(mCanvas);
            drawFrame(mCanvas);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mCanvas != null) {
                mHolder.unlockCanvasAndPost(mCanvas);
            }
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

    /**
     * 根据力量大小设置力量条高度
     */
    public void setCurrentHeight(int strength) {
        if (strength >= 100)
            strength = 100;
        mCurrentHeight = strength/100f * mStrengthViewHeight;
        Log.i("ChickViewChoa",mCurrentHeight+"  "+strength);
    }

}