package com.lightweh.camera2preview;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.File;

public class Player {
    private static final String TAG = Player.class.getSimpleName();

    private MediaPlayer mMediaPlayer01;
    //private SurfaceView mSurfaceView01;
    private SurfaceHolder mSurfaceHolder01;
    private boolean mSurfaceHolderCreated = false;
    private Handler mHandler;
    private String mPlayStatus = "stopped";//playing paused stopped
    private static final String PLAYING = "playing";
    private static final String PAUSED = "paused";
    private static final String STOPED = "stopped";
    //private static final int FIXED_WIDTH = 320;
    //private static final int FIXED_HEIGHT = 240;

    public Player(SurfaceHolder holder) {
        this.mSurfaceHolder01 = holder;
        initPlayer();
    }

    public void setHandler(Handler child) {
        mHandler = child;
    }

    private SurfaceHolder.Callback mSurfaceHolderCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.i(TAG, "surfaceCreated holder = " + holder);
            mSurfaceHolderCreated = true;
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Log.i(TAG, "surfaceChanged holder = " + holder + ", format = " + format + ", width = " + width + ", height = " + height);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.i(TAG, "surfaceDestroyed holder = " + holder);
            mSurfaceHolderCreated = false;
        }
    };

    private void initPlayer() {
        //mSurfaceHolder01 = mSurfaceView01.getHolder();
        mSurfaceHolder01.setKeepScreenOn(true);
        //mSurfaceHolder01.setFixedSize(FIXED_WIDTH, FIXED_HEIGHT);
        mSurfaceHolder01.addCallback(mSurfaceHolderCallback);
        mMediaPlayer01 = new MediaPlayer();
    }

    public void playerStart(String path) {
        if (STOPED.equals(mPlayStatus)) {
            File file = null;
            try {
                file = new File(path);
            } catch (Exception e) {
                Log.d(TAG, "playerStart new File Exception = " + e.getMessage());
                return;
            }
            if (file != null && file.isFile()) {
                playVideo(path);
            } else {
                Log.d(TAG, "playerStart invalid path");
            }
        } else {
            Log.d(TAG, "playerStart already");
        }
    }

    public void playerPauseOrResume() {
        if (PLAYING.equals(mPlayStatus)) {
            mPlayStatus = PAUSED;
            mMediaPlayer01.pause();
        } else if (PAUSED.equals(mPlayStatus)) {
            mPlayStatus = PLAYING;
            mMediaPlayer01.start();
        } else {
            Log.d(TAG, "playerPauseOrResume hasn't start play");
        }
    }

    public void playerSeek(int position) {
        if (PLAYING.equals(mPlayStatus) || PAUSED.equals(mPlayStatus)) {
            mMediaPlayer01.seekTo(0);
        }
    }

    public void playerStop() {
        if (PLAYING.equals(mPlayStatus) || PAUSED.equals(mPlayStatus)) {
            mPlayStatus = STOPED;
            mMediaPlayer01.stop();
            mMediaPlayer01.release();
        } else {
            Log.d(TAG, "playerStop hasn't start play");
        }
    }

    private String mPlayPath = null;

    private void playVideo(String strPath) {
        mHandler.removeCallbacks(run);
        mPlayPath = strPath;
        mHandler.postDelayed(run, 1000);
    }

    private final  Runnable run = new Runnable() {
        @Override
        public void run() {
            if (mSurfaceHolderCreated) {
                Log.d(TAG, "run surface ready");
                playVideoInRunnable();
            } else {
                Log.d(TAG, "run surface not ready");
                mHandler.removeCallbacks(run);
                mHandler.postDelayed(run, 1000);
            }
        }
    };

    private void playVideoInRunnable()
    {
        try
        {
            Log.e(TAG, "playVideo starting");
            mPlayStatus = PLAYING;
            mMediaPlayer01.setDisplay(mSurfaceHolder01);
            mMediaPlayer01.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer01.setDataSource(mPlayPath);
            mMediaPlayer01.prepare();
            mMediaPlayer01.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.e(TAG, "onPrepared " + mp);
                    mMediaPlayer01.start();
                }
            });
            mMediaPlayer01.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
            {
                @Override
                public void onCompletion(MediaPlayer arg0)
                {
                    Log.e(TAG, "onCompletion " + arg0);
                    mPlayStatus = STOPED;
                    mMediaPlayer01.release();
                }
            });
            Log.e(TAG, "playVideo started");
        } catch (Exception e)
        {
            Log.e(TAG, "playVideo Exception = " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean checkSDCard()
    {
        if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
