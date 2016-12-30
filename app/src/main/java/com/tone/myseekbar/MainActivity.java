package com.tone.myseekbar;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private SeekBar seekBar;
    private MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        seekBar = (SeekBar) findViewById(R.id.seekabr);
        initMediaPlaper("http://media.vtibet.com/masvod/public/2015/01/04/20150104_14ab2c82dd7_r1_64k.mp4");
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0)
                seekBar.setMax(mMediaPlayer.getDuration());
            if (msg.what == 1)
                seekBar.setProgress(mMediaPlayer.getCurrentPosition());
            if (msg.what == 2) {
                int max = mMediaPlayer.getDuration();
                int m = (int) (msg.arg1*max/100f);
                seekBar.setSecondaryProgress(m);
            }
        }
    };

    private void startBar() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    handler.sendEmptyMessageDelayed(1, 1000);
                }
            }
        });
        thread.start();
    }

    private void initMediaPlaper(String path) {
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnPreparedListener(onPreparedListener);
            mMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mp, int percent) {
                    Log.d("MainActivity", "onBufferingUpdate: " + percent);
                    Message m = handler.obtainMessage(2);
                    m.what = 2;
                    m.arg1 = percent;
                    handler.sendMessageDelayed(m, 1000);
                }
            });
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.prepareAsync();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            try {
                mediaPlayer.start();
                handler.sendEmptyMessage(0);
                startBar();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
