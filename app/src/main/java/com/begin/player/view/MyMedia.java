package com.begin.player.view;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.begin.player.bean.Music;
import com.begin.player.service.MediaService;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Begin on 16/4/9.
 */
public class MyMedia implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private Button last;
    private Button start;
    private Button next;
    private TextView nowTime;
    private TextView maxTime;
    private SeekBar seekBar;
    //    private static final int MEDIATYPE_DEFAULT = 0;
//    private static final int MEDIATYPE_START = 1;
//    private static final int MEDIATYPE_PAUSED = 2;
//    private static final int MEDIATYPE_STOPPED = 3;
//    private int mediaType = MEDIATYPE_DEFAULT;
//    private int index = 0;
    private MediaService mediaService;
    private MediaService.onIndexChange callBack;
    private LinkedList<Music> list;
    private boolean mBound = false;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mediaService = ((MediaService.MyBinder) service).getService();
            mediaService.setOnIndexChangeListener(callBack);
            mediaService.setList(list);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    public MyMedia(Context context, SeekBar seekBar, Button start) {
        this.start = start;
        this.seekBar = seekBar;
        seekBar.setOnSeekBarChangeListener(this);
        start.setTag(2);
        start.setOnClickListener(this);

        context.bindService(new Intent(context, MediaService.class), serviceConnection, Context.BIND_AUTO_CREATE);

//        mediaPlayer = new MediaPlayer();
//        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        mediaPlayer.setOnBufferingUpdateListener(this);
//        mediaPlayer.setOnPreparedListener(this);
//        mediaPlayer.setOnCompletionListener(this);

        (new Timer()).schedule(timerTask, 0, 500);
    }

    public void setTimeView(TextView nowTime, TextView maxTime) {
        this.nowTime = nowTime;
        this.maxTime = maxTime;
    }

    public void setButton(Button last, Button next) {
        this.next = next;
        this.last = last;
        last.setTag(1);
        last.setOnClickListener(this);
        next.setTag(3);
        next.setOnClickListener(this);
    }

//    public MyMedia(Context context, AttributeSet attrs) {
//        this(context, attrs, 0);
//    }
//
//    public MyMedia(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//
//        this.setOrientation(VERTICAL);
//
//        LinearLayout l = new LinearLayout(context);
//        l.setOrientation(HORIZONTAL);
//        l.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//
//        nowTime = new TextView(context);
//        maxTime = new TextView(context);
//        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        layoutParams.weight = 1;
//        layoutParams.gravity = Gravity.LEFT;
//        nowTime.setLayoutParams(layoutParams);
//        nowTime.setText("00:00");
//        layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        layoutParams.gravity = Gravity.RIGHT;
//        maxTime.setLayoutParams(layoutParams);
//        maxTime.setText("00:00");
//        l.addView(nowTime);
//        l.addView(maxTime);
//        this.addView(l);
//
//        seekBar = new SeekBar(context);
//        seekBar.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        seekBar.setOnSeekBarChangeListener(this);
//        this.addView(seekBar);
//
//        l = new LinearLayout(context);
//        l.setOrientation(HORIZONTAL);
//        l.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//
//        next = new Button(context);
//        layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        layoutParams.weight = 1;
//        next.setLayoutParams(layoutParams);
//        next.setText("下一首");
//        next.setTag(3);
//        next.setOnClickListener(this);
//        last = new Button(context);
//        layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        layoutParams.weight = 1;
//        last.setLayoutParams(layoutParams);
//        last.setText("上一首");
//        last.setTag(1);
//        last.setOnClickListener(this);
//        start = new Button(context);
//        layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        layoutParams.weight = 1;
//        start.setLayoutParams(layoutParams);
//        start.setText("播放");
//        start.setTag(2);
//        start.setOnClickListener(this);
//        l.addView(last);
//        l.addView(start);
//        l.addView(next);
//        this.addView(l);
//    }

    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            if (mediaService == null)
                return;
            if (mediaService.getIsPlaying() && seekBar.isPressed() == false) {
                handler.sendEmptyMessage(0);
            }
            mediaService.setIsPressed(seekBar.isPressed());
            handler.sendEmptyMessage(1);
        }
    };

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0) {
                int position = mediaService.getMediaPlayer().getCurrentPosition();
                int duration = mediaService.getMediaPlayer().getDuration();
                if (duration > 0) {
                    seekBar.setMax(duration);
                    seekBar.setProgress(position);
                    if (maxTime != null)
                        maxTime.setText(showTime(duration));
                    if (nowTime != null)
                        nowTime.setText(showTime(position));
                }
            }
            if (msg.what == 1) {
                seekBar.setSecondaryProgress(mediaService.getSecondaryProgress());
                switch (mediaService.getMediaType()) {
                    case MediaService.MEDIATYPE_DEFAULT:
                        break;
                    case MediaService.MEDIATYPE_START:
                        start.setText("播放ing");
                        break;
                    case MediaService.MEDIATYPE_PAUSED:
                        start.setText("暂停...");
                        break;
                    case MediaService.MEDIATYPE_STOPPED:
                        break;
                }
            }
        }
    };

    public void setIndex(int index) {
        mediaService.setIndex(index);
    }

    public void setList(LinkedList<Music> list) {
        this.list = list;
    }

    //    public void setUrl(String url) {
//        mediaPlayer.reset();
//        try {
//            mediaPlayer.setDataSource(url);
//            mediaPlayer.prepare();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public MediaPlayer getMediaPlayer() {
//        return mediaPlayer;
//    }
//
    public void start() {
        mediaService.start();
    }

    public void pause() {
        mediaService.pause();
    }

    public void stop() {
        if (mediaService != null) {
            mediaService.stop();
            mediaService.unbindService(serviceConnection);
        }
    }
//
//
//    @Override
//    public void onBufferingUpdate(MediaPlayer mp, int percent) {
//        if (mp.isPlaying() && seekBar.isPressed() == false) {
//            int duration = mp.getDuration();
//            if (duration > 0) {
//                seekBar.setSecondaryProgress(percent * duration / 100);
//            }
//        }
//    }
//
//    @Override
//    public void onPrepared(MediaPlayer mp) {
//        mp.start();
//    }
//
//    @Override
//    public void onCompletion(MediaPlayer mp) {
//        if (list.size() == index + 1) {
//            mp.reset();
//            start.setText("暂停...");
//            mediaType = MEDIATYPE_DEFAULT;
//        } else {
//            index++;
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    setUrl(list.get(index).getUrl());
//                }
//            }).start();
//            if (callBack != null)
//                callBack.onIndexChange(index);
//        }
//    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mediaService.getMediaPlayer().seekTo(seekBar.getProgress());
    }

    public String showTime(int time) {
        time /= 1000;
        int minute = time / 60;
        int hour = minute / 60;
        int second = time % 60;
        minute %= 60;
        return String.format("%02d:%02d", minute, second);
    }

    @Override
    public void onClick(View v) {
        switch ((int) v.getTag()) {
            case 1:
                last();
                break;
            case 2:
                mediaService.player();
                break;
            case 3:
                next();
                break;
        }
    }

    private void last() {
        mediaService.last();
    }

    private void next() {
        mediaService.next();
    }

    public void setOnIndexChangeListener(MediaService.onIndexChange callBack) {
        this.callBack = callBack;
    }
}
