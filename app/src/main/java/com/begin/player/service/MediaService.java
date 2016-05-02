package com.begin.player.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import com.begin.player.bean.Music;

import java.io.IOException;
import java.util.LinkedList;

public class MediaService extends Service implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    private MediaPlayer mediaPlayer;
    private LinkedList<Music> list;
    private static final int MEDIATYPE_DEFAULT = 0;
    private static final int MEDIATYPE_START = 1;
    private static final int MEDIATYPE_PAUSED = 2;
    private static final int MEDIATYPE_STOPPED = 3;
    private int mediaType = MEDIATYPE_DEFAULT;
    private MyBinder binder = new MyBinder();
    private int index = 0;
    private onIndexChange callBack;
    private Boolean isPressed = false;
    private int secondaryProgress = 0;

    public class MyBinder extends Binder {
        public MediaService getService() {
            return MediaService.this;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

        try {
            list = (LinkedList<Music>) intent.getSerializableExtra("list");
        } catch (Exception e) {
            e.printStackTrace();
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);

        return binder;
    }

    public void setOnIndexChangeListener(onIndexChange callBack) {
        this.callBack = callBack;
    }

    public void setList(LinkedList<Music> list) {
        this.list = list;
    }

    public void setUrl(String url) {
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void player() {
        switch (mediaType) {
            case MEDIATYPE_DEFAULT:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        setUrl(list.get(index).getUrl());
                    }
                }).start();
                if (callBack != null)
                    callBack.onIndexChange(index);
//                start.setText("播发ing");
                mediaType = MEDIATYPE_START;
                break;
            case MEDIATYPE_START:
                mediaPlayer.pause();
//                start.setText("暂停...");
                mediaType = MEDIATYPE_PAUSED;
                break;
            case MEDIATYPE_PAUSED:
                mediaPlayer.start();
//                start.setText("播发ing");
                mediaType = MEDIATYPE_START;
                break;
        }
    }

    public void start() {
        mediaPlayer.start();
    }

    public void pause() {
        mediaPlayer.pause();
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        if (mp.isPlaying() && isPressed == false) {
            int duration = mp.getDuration();
            if (duration > 0) {
                secondaryProgress = percent * duration / 100;
            }
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (list.size() == index + 1) {
            mp.reset();
            mediaType = MEDIATYPE_DEFAULT;
        } else {
            index++;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    setUrl(list.get(index).getUrl());
                }
            }).start();
            if (callBack != null)
                callBack.onIndexChange(index);
        }
    }

    public interface onIndexChange {
        public void onIndexChange(int index);
    }

    public int getMediaType() {
        return mediaType;
    }

    public int getSecondaryProgress() {
        return secondaryProgress;
    }

    public void setIsPressed(Boolean isPressed) {
        this.isPressed = isPressed;
    }

    public int getPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public Boolean getIsPlaying() {
        return mediaPlayer.isPlaying();
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }
}
