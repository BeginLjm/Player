package com.begin.player.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.begin.player.R;
import com.begin.player.bean.Music;
import com.begin.player.service.MediaService;
import com.begin.player.view.MyMedia;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, MediaService.onIndexChange, SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private ListView mList;
    String url1 = "http://119.29.98.71/5.mp3";
    String url2 = "http://119.29.98.71/6.mp3";
    String url3 = "http://119.29.98.71/7.mp3";
    private LinkedList<Music> list;
    private MyAdapter adapter;
    private TextView mTvNowTime;
    private TextView mTvMaxTime;
    private SeekBar mSeekBar;
    private Button mBtLast;
    private Button mBtPlay;
    private Button mBtNext;
    private MediaService mediaService;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mediaService = ((MediaService.MyBinder) service).getService();
            mediaService.setOnIndexChangeListener(MainActivity.this);
            mediaService.setList(list);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        mSeekBar.setOnSeekBarChangeListener(this);
        mBtPlay.setOnClickListener(this);

        bindService(new Intent(MainActivity.this, MediaService.class), serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        (new Timer()).schedule(timerTask, 0, 500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }

    private void init() {
        mTvNowTime = (TextView) findViewById(R.id.tv_now_time);
        mTvMaxTime = (TextView) findViewById(R.id.tv_max_time);
        mSeekBar = (SeekBar) findViewById(R.id.seek_bar);
        mBtLast = (Button) findViewById(R.id.bt_last);
        mBtPlay = (Button) findViewById(R.id.bt_play);
        mBtNext = (Button) findViewById(R.id.bt_next);

        mList = (ListView) findViewById(R.id.list);

        list = new LinkedList<>();
        list.add(new Music("5", url1, false));
        list.add(new Music("6", url2, false));
        list.add(new Music("7", url3, false));
        adapter = new MyAdapter();
        mList.setAdapter(adapter);
        mList.setOnItemClickListener(this);
    }


    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            if (mediaService == null)
                return;
            if (mediaService.getIsPlaying() && mSeekBar.isPressed() == false) {
                handler.sendEmptyMessage(0);
            }
            mediaService.setIsPressed(mSeekBar.isPressed());
            handler.sendEmptyMessage(1);
        }
    };

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0) {
                int position = mediaService.getMediaPlayer().getCurrentPosition();
                int duration = mediaService.getMediaPlayer().getDuration();
                if (duration > 0) {
                    mSeekBar.setMax(duration);
                    mSeekBar.setProgress(position);
                    if (mTvMaxTime != null)
                        mTvMaxTime.setText(showTime(duration));
                    if (mTvNowTime != null)
                        mTvNowTime.setText(showTime(position));
                }
            }
            if (msg.what == 1) {
                mSeekBar.setSecondaryProgress(mediaService.getSecondaryProgress());
                switch (mediaService.getMediaType()) {
                    case MediaService.MEDIATYPE_DEFAULT:
                        mBtPlay.setText("停止");
                        break;
                    case MediaService.MEDIATYPE_START:
                        mBtPlay.setText("播放ing");
                        break;
                    case MediaService.MEDIATYPE_PAUSED:
                        mBtPlay.setText("暂停...");
                        break;
                    case MediaService.MEDIATYPE_STOPPED:
                        mBtPlay.setText("停止");
                        break;
                }
            }
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mediaService.setIndex(position);
    }


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

    @Override
    public void onIndexChange(int index) {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setPlayer(false);
        }
        list.get(index).setPlayer(true);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_last:
                mediaService.last();
                break;
            case R.id.bt_play:
                mediaService.player();
                break;
            case R.id.bt_next:
                mediaService.next();
                break;
        }
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(MainActivity.this);
            textView.setText(list.get(position).getName());
            if (list.get(position).getPlayer()) {
                textView.setTextColor(0x12332111);
            }
            textView.setPadding(10, 10, 10, 10);
            return textView;
        }
    }

    public String showTime(int time) {
        time /= 1000;
        int minute = time / 60;
        int hour = minute / 60;
        int second = time % 60;
        minute %= 60;
        return String.format("%02d:%02d", minute, second);
    }
}
