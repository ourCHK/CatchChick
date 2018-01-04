package gionee.com.catchchick.CustomView;


import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by ljc on 18-1-4.
 */

public class ChickViewChao extends SurfaceView implements Runnable, SurfaceHolder.Callback {

    private Canvas mCanvas;
    private Thread mThread;
    private SurfaceHolder mHolder;
    private boolean isRunning = false;


    public ChickViewChao(Context context) {
        super(context);
        init();
    }

    public ChickViewChao(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ChickViewChao(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isRunning = true;
        mThread = new Thread(this);
        mThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRunning = false;
    }

    @Override
    public void run() {
        while (isRunning) {
            draw();
        }
    }


    private void init() {
        mHolder = getHolder();
        mHolder.addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setKeepScreenOn(true);
    }

    private void draw() {
        try {
            mCanvas = mHolder.lockCanvas();
        } catch (Exception e) {

        } finally {
            if (mCanvas != null) {
                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }
}