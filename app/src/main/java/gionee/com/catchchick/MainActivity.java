package gionee.com.catchchick;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class MainActivity extends Activity {


        private SurfaceHolder mSurfaceHolder;
        private SurfaceView mSurfaceView;
        private Resources mResources;
        private Canvas mCanvas;
        private Rect mRect;
        private Rect mChickRect;
        private Bitmap mBitmap;
        private int[] mChickId;

        private int mStrength;

        private String mChickName;

        private String mPackageName;

        private boolean isDown;
        private boolean isUp;

        private SparseIntArray inTask;

        private int mWidth;
        private int mHeight;

        private LruCache<String, Bitmap> mLruCache;
        private ThreadPoolExecutor executor;

        private Utils mUtils;
        private AudioManager mAudioManager;

        @SuppressLint("ClickableViewAccessibility")
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);
            mSurfaceHolder = mSurfaceView.getHolder();
            mSurfaceHolder.addCallback(callback);
            mPackageName = this.getPackageName();
            mResources = getResources();
            mChickName = "chick_";
            isDown = false;
            isUp = false;
            mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
            mUtils = new Utils(this, mAudioManager);

            inTask = new SparseIntArray();

            mChickId = new int[101];
            for (int i = 0; i < 101; i++) {
                mChickId[i] = getResId(i);
                inTask.put(i, 0);
            }


            int maxSize = (int) (Runtime.getRuntime().maxMemory()) / 2014;
            mLruCache = new LruCache<String, Bitmap>(maxSize * 4 / 5) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getByteCount() / 1024;
                }
            };

            executor = new ThreadPoolExecutor(10, 100, 1, TimeUnit.HOURS,
                    new LinkedBlockingDeque<Runnable>(5));


            mUtils.playMusic();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i=0;i<4;i++){
                        inTask.put(i, 1);
                        MyTask task = new MyTask(i);
                        executor.execute(task);
                    }
                }
            }).start();


            mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            isDown = true;
                            isUp = false;
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    int i = mStrength;
                                    while (isDown) {
                                        if (i < 101) {
                                            setStrength(i);
                                            if (mStrength <= 80) {
//                                SystemClock.sleep(50);
                                            }
                                            i++;
                                        }
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
                                            if (mStrength==0){
                                                setStrength(mStrength);
                                            }
                                            long time = (long) (5 * ratio);
//                                SystemClock.sleep(time);
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
            });
        }

        private SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                drawBitmap(mChickId[0], 0);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                mWidth = width;
                mHeight = height;
                mRect = new Rect(0, 0, mWidth, mHeight);
                mChickRect = new Rect(0, 0, mWidth, mHeight);
                drawBitmap(mChickId[0], 0);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        };


        private Bitmap getBitmap(int drawableId) {
            Drawable drawable = mResources.getDrawable(drawableId, getTheme());
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
//        return resizeBitmap(bitmap, mWidth, mHeight);
            return bitmap;
        }

        public void setStrength(final int strength) {
            if (strength > 100) {
                mStrength = 100;
            } else {
                mStrength = strength;
            }

            drawBitmap(mChickId[mStrength], mStrength);

            if (strength - 7 > 0 && isDown) {
                removeBitmapFromMemoryCache(strength - 7);
            }

            if (isDown) {
                if (strength + 3 < 101 && inTask.get(strength + 3) == 0) {
                    inTask.put(strength + 3, 1);
                    MyTask task = new MyTask(strength + 3);
                    executor.execute(task);
                }
                if (strength + 5 < 101&&inTask.get(strength + 5) == 0) {
                    inTask.put(strength + 5, 1);
                    MyTask task = new MyTask(strength + 5);
                    executor.execute(task);
                }
                if (strength + 7 < 101&&inTask.get(strength + 7) == 0) {
                    inTask.put(strength + 7, 1);
                    MyTask task = new MyTask(strength + 7);
                    executor.execute(task);
                }
            } else {
                if (strength - 3 > 0&&inTask.get(strength - 3) == 0) {
                    inTask.put(strength - 3, 1);
                    MyTask task = new MyTask(strength - 3);
                    executor.execute(task);
                }
                if (strength - 5 > 0&&inTask.get(strength - 5) == 0) {
                    inTask.put(strength -5, 1);
                    MyTask task = new MyTask(strength - 5);
                    executor.execute(task);
                }
                if (strength - 7 > 0&&inTask.get(strength - 7) == 0) {
                    inTask.put(strength - 7, 1);
                    MyTask task = new MyTask(strength - 7);
                    executor.execute(task);
                }
            }
        }

        @Override
        public void onWindowFocusChanged(boolean hasFocus) {
            super.onWindowFocusChanged(hasFocus);
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }


        private int getResId(int strength) {
            String name;
            if (strength < 10) {
                name = mChickName + ("00") + strength;
            } else if (strength < 100 && strength >= 10) {
                name = mChickName + "0" + strength;
            } else {
                name = mChickName + strength;
            }
            int resId = mResources.getIdentifier(name, "mipmap", mPackageName);
            return resId;
        }

        private synchronized void drawBitmap(int drawableId, int strength) {

            long time = System.currentTimeMillis();
            Log.d("strength", "strength : " + strength);

            mBitmap = loadBitmap(drawableId);

            Log.d("get", "get_time: " + (System.currentTimeMillis() - time)+ " strength: " + strength);
            long time2 = System.currentTimeMillis();
            try {
                mCanvas = mSurfaceHolder.lockCanvas();
                mCanvas.drawBitmap(mBitmap, mChickRect, mRect, null);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (mCanvas != null) {
                    mBitmap = null;
                    mSurfaceHolder.unlockCanvasAndPost(mCanvas);
                    Log.d("draw", "draw_time: " + (System.currentTimeMillis() - time2) + " strength: " + strength);
                }
            }
        }

        public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
            if (getBitmapFromMemCache(key) == null) {
                mLruCache.put(key, bitmap);
            }
        }

        public void removeBitmapFromMemoryCache(int strength) {
            String key = String.valueOf(mChickId[strength]);
            mLruCache.remove(key);
        }

        //
        public Bitmap getBitmapFromMemCache(String key) {
            return mLruCache.get(key);
        }

        public Bitmap loadBitmap(int drawableId) {
            final String imageKey = String.valueOf(drawableId);
            Bitmap bitmap = getBitmapFromMemCache(imageKey);
            if (bitmap != null) {
                return bitmap;
            } else {
                bitmap = getBitmap(drawableId);
                return bitmap;
            }
        }

        class MyTask implements Runnable {
            private int strength;

            public MyTask(int strength) {
                this.strength = strength;
            }

            @Override
            public void run() {
                String key = String.valueOf(mChickId[strength]);
                Bitmap bitmap = getBitmap(mChickId[strength]);
                addBitmapToMemoryCache(key, bitmap);
                inTask.put(strength, 0);
            }
        }
    }