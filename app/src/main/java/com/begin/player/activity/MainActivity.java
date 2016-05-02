package com.begin.player.activity;

import android.os.Bundle;
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
import com.begin.player.view.MyMedia;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, MyMedia.onIndexChange {

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
    private MyMedia myMedia;
//    private TextView mBtStart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

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

    @Override
    protected void onResume() {
        super.onResume();
        myMedia = MyMedia.getInstance(MainActivity.this, mSeekBar, mBtPlay);
        myMedia.setButton(mBtLast, mBtNext);
        myMedia.setTimeView(mTvNowTime, mTvMaxTime);
        myMedia.setList(list);
        myMedia.setOnIndexChangeListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        myMedia.setIndex(position);
    }

    @Override
    public void onIndexChange(int index) {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setPlayer(false);
        }
        list.get(index).setPlayer(true);
        adapter.notifyDataSetChanged();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myMedia != null) {
            myMedia.stop();
            myMedia = null;
        }
    }
}
