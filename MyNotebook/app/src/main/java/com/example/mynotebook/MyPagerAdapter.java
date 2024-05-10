package com.example.mynotebook;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.mynotebook.fragment.AddFragment;
import com.example.mynotebook.fragment.LLMFragment;
import com.example.mynotebook.fragment.MineFragment;
import com.example.mynotebook.fragment.RecordFragment;

public class MyPagerAdapter extends FragmentPagerAdapter {
    private static final int NUM_PAGES = 4;

    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    private static final String[] PAGE_TITLES = {"笔记", "添加", "智能", "我的"};

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new RecordFragment();
            case 1:
                return new AddFragment();
            case 2:
                return new LLMFragment();
            case 3:
                return new MineFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return PAGE_TITLES[position];
    }
}
