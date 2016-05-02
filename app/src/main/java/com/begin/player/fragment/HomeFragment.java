package com.begin.player.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.begin.player.R;
import com.begin.player.activity.MainActivity;
import com.begin.player.service.MediaService;

public class HomeFragment extends Fragment {

    String[] titles = {"首页", "动态"};
    Fragment[] fragments = new Fragment[2];
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = View.inflate(getActivity(), R.layout.fragment_home, null);

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

        getActivity().bindService(new Intent(getActivity(), MediaService.class), serviceConnection, Context.BIND_AUTO_CREATE);

        return view;
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
