package gionee.com.catchchick.CustomView;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.SystemClock;
import android.util.AttributeSet;
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
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mCanvas != null) {
                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }
}