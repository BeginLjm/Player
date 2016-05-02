package com.begin.player.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.begin.player.R;
import com.begin.player.activity.MainActivity;
import com.begin.player.bean.Music;
import com.begin.player.service.MediaService;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, View.OnClickListener, MediaService.onIndexChange {

    String[] titles = {"首页", "动态"};
    Fragment[] fragments = new Fragment[2];
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private View view;
    private SeekBar mSeekBar;
    private Button mBtNext;
    private Button mBtPlay;
    private TextView mTvTitle;
    private MediaService mediaService;
    String url1 = "http://119.29.98.71/5.mp3";
    String url2 = "http://119.29.98.71/6.mp3";
    String url3 = "http://119.29.98.71/7.mp3";
    private LinkedList<Music> list;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mediaService = ((MediaService.MyBinder) service).getService();
            mediaService.setList(list);
            mediaService.setOnIndexChangeListener(HomeFragment.this);
            mTvTitle.setText(mediaService.getList().get(mediaService.getIndex()).getName());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };
    private Timer timer = new Timer();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = View.inflate(getActivity(), R.layout.fragment_home, null);

        mTabLayout = (TabLayout) view.findViewById(R.id.tab_home);
        mViewPager = (ViewPager) view.findViewById(R.id.vp_home);

        fragments[0] = new ListFragment();
        fragments[1] = new DynamicFragment();
        mViewPager.setAdapter(new MyAdapter(getFragmentManager(), titles, fragments));
        mTabLayout.setupWithViewPager(mViewPager);

        view.findViewById(R.id.view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

        init();

        getActivity().bindService(new Intent(getActivity(), MediaService.class), serviceConnection, Context.BIND_AUTO_CREATE);

        timer.schedule(timerTask, 0, 500);

        return view;
    }

    private void init() {
        mBtPlay = (Button) view.findViewById(R.id.bt_play);
        mBtNext = (Button) view.findViewById(R.id.bt_next);
        mTvTitle = (TextView) view.findViewById(R.id.tv_title);
        mSeekBar = (SeekBar) view.findViewById(R.id.seek_bar);

        mSeekBar.setOnSeekBarChangeListener(this);
        mBtPlay.setOnClickListener(this);
        mBtNext.setOnClickListener(this);

        list = new LinkedList<>();
        list.add(new Music("5", url1, false));
        list.add(new Music("6", url2, false));
        list.add(new Music("7", url3, false));
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
    public void onResume() {
        super.onResume();
        if (mediaService != null)
            mediaService.setOnIndexChangeListener(HomeFragment.this);
    }

    @Override
    public void onPause() {
        super.onPause();
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_play:
                mediaService.player();
                break;
            case R.id.bt_next:
                mediaService.next();
                break;
        }
    }

    @Override
    public void onIndexChange(int index) {
        Log.d("index", index + "===========");
        mTvTitle.setText(mediaService.getList().get(index).getName());
    }

    class MyAdapter extends FragmentStatePagerAdapter {
        String[] titles;
        Fragment[] fragments;

        public MyAdapter(FragmentManager fm, String[] titles, Fragment[] fragments) {
            super(fm);
            this.titles = titles;
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}
