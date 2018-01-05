package gionee.com.catchchick;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import gionee.com.catchchick.CustomView.ChickView;

import gionee.com.catchchick.CustomView.ChickViewChao;

public class MainActivity extends Activity {

    private ChickView mChickView;

    private ChickViewChao mChickViewChao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mChickView = new ChickView(this);
//        setContentView(mChickView);
        mChickViewChao = new ChickViewChao(this);
        setContentView(mChickViewChao);
//        setContentView(R.layout.activity_main);
//        mChickViewChao = (ChickViewChao)findViewById(R.id.chickView);
        mChickViewChao.isRunning = true;
        mChickViewChao.isDown = true;
        mChickViewChao.setStrength(50);
        new Thread(new Runnable() {
            int i=0;
            @Override
            public void run() {

                while (i<80){
                    mChickViewChao.setStrength(i);
                    i++;
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (i==79){
                        mChickViewChao.isDown = false;
                    }
                }
            }
        }).start();
    }

    /**
     * Sets the activity with an immersive experience
     *
     * @param hasFocus
     */
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
}
