package gionee.com.catchchick;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.RequiresApi;

/**
 * Created by ljc on 18-1-3.
 */

public class Utils {
    private AudioManager mAudioManager;
    private MediaPlayer mMediaPlayer;
    private Context mContext;

    public Utils(Context context, AudioManager audioManager) {
        mAudioManager = audioManager;
        mContext = context;
    }

    /**
     *Gets the volume of the system.
     */
    public int getVolume() {
        int volume;
        volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        return volume;
    }

    /**
     *Sets the volume on the background music.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setVolumeAndSpeed(int strength) {
        float ratio;
        ratio = Math.round(strength / 10) / 10f;
        mMediaPlayer.setVolume(ratio, ratio);

        float speed;
        if (strength > 60) {
            speed = (float) strength / 60f;
        } else {
            speed = 1f;
        }
        mMediaPlayer.getPlaybackParams().setSpeed(speed);
    }

    /**
     *Plays the background music.
     */
    public void playMusic(){
        mMediaPlayer = MediaPlayer.create(mContext, R.raw.crow);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();
    }

    /**
     *Stops the background music and release the source.
     */
    public void stopMusic() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
    }
}

