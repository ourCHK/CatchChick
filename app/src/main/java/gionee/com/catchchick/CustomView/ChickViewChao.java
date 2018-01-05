package gionee.com.catchchick.CustomView;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import gionee.com.catchchick.Utils;

/**
 * Created by ljc on 18-1-4.
 */

public class ChickViewChao extends SurfaceView implements Runnable, SurfaceHolder.Callback {

    private static String TAG = "ChickView";

    private Resources mResources;
    private Canvas mCanvas;
    private Rect mRect;
    private Rect mChickRect;
    private String mChickName;
    private Bitmap mBitmap;
    private int[] chickId;
    private Context mContext;

    private Utils mUtils;
    private AudioManager mAudioManager;


    private int mStrength;
    private String packageName;

    private Thread mThread;
    private SurfaceHolder mHolder;
    public boolean isDown = false;
    private boolean isUp = false;
    public boolean isRunning = false;

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
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated: getWidth(): " + getWidth() + " getHeight(): " + getHeight());
        packageName = mContext.getPackageName();
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        mUtils = new Utils(mContext, mAudioManager);
        mChickName = "chick_";
        chickId = new int[101];

        for (int i = 0; i < 101; i++) {
            chickId[i] = getResId(i);
        }
        mUtils.playMusic();
        mUtils.setVolumeAndSpeed(0);

        mThread = new Thread(this);
        isRunning = true;
        mThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged: width: " + width + " height: " + height);
        mRect = new Rect(0, 0, mWidth, mHeight);
        mChickRect = new Rect(0, 0, mWidth, mHeight);
    }


    /**
     * Stops the music
     *
     * @param holder
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRunning = false;
        mUtils.stopMusic();
    }


    /**
     * Draws the bitmap and set the volumeAndSpeed
     */
    @Override
    public void run() {
        while (isRunning) {
            if (isDown || isUp) {
                mUtils.setVolumeAndSpeed(mStrength);
                drawBitmap(chickId[mStrength]);
            } else {
                mUtils.setVolumeAndSpeed(0);
                drawBitmap(chickId[0]);
            }
        }
    }


    /**
     * Simulated finger pressure
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDown = true;
                isUp = false;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int i = 0;
                        while (isDown) {
                            setStrength(i);
                            if (mStrength <= 80) {
                                SystemClock.sleep(5);
                            }
                            i++;
                            Log.d(TAG, " isDown run : " + i + " " + isDown);
                        }
                    }
                }).start();
                break;
            case MotionEvent.ACTION_UP:
                isUp = true;
                isDown = false;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int i = mStrength;
                        while (isUp) {
                            if (mStrength > 0) {
                                setStrength(mStrength);
                                float ratio = (float) mStrength / i;
                                if (ratio <= 0) {
                                    ratio = 1;
                                }
                                mStrength--;
                                long time = (long) (5 * ratio);
                                SystemClock.sleep(time);
                            }
                        }
                    }
                }).start();
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * init the data
     */
    private void init() {
        mHolder = getHolder();
        mHolder.addCallback(this);
        mResources = getResources();
        setFocusable(true);
        setFocusableInTouchMode(true);
        setKeepScreenOn(true);

    }

    /**
     * Sets the strength
     *
     * @param strength
     */
    public void setStrength(int strength) {
        if (strength > 100) {
            mStrength = 100;
        } else {
            mStrength = strength;
        }
    }


    /**
     * Gets the Id by strength
     *
     * @param strength
     * @return
     */
    private int getResId(int strength) {
        String name;
        if (strength < 10) {
            name = mChickName + ("00") + strength;
        } else if (strength < 100 && strength >= 10) {
            name = mChickName + "0" + strength;
        } else {
            name = mChickName + strength;
        }
        int resId = mResources.getIdentifier(name, "drawable", packageName);
        return resId;
    }


    /**
     * Gets the bitmap by id
     *
     * @param drawableId
     * @return
     */
    private Bitmap getBitmap(int drawableId) {
        Drawable drawable = mResources.getDrawable(drawableId, mContext.getTheme());
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        Bitmap bitmap = bitmapDrawable.getBitmap();
        return resizeBitmap(bitmap, mWidth, mHeight);
    }


    /**
     * Draws the bitmap by id
     *
     * @param drawableId
     */
    private synchronized void drawBitmap(int drawableId) {
        mBitmap = getBitmap(drawableId);
        try {
            mCanvas = mHolder.lockCanvas();
            mCanvas.drawBitmap(mBitmap, mChickRect, mRect, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mCanvas != null) {
                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }


    /**
     * Resize the bitmap to fix the screen
     *
     * @param bitmap
     * @param newWidth
     * @param newHeight
     * @return
     */
    private Bitmap resizeBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Log.d(TAG, "resizeBitmap: width: " + width + "height: " + height + " newWidth: " + newWidth + " newHeight: " + newHeight + " scaleHeight: " + scaleHeight);

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return resBitmap;
    }
}