package com.example.mynotebook;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.mynotebook.fragment.fragment_add;
import com.example.mynotebook.fragment.fragment_mine;
import com.example.mynotebook.fragment.fragment_record;

public class MyPagerAdapter extends FragmentPagerAdapter {
    private static final int NUM_PAGES = 3;

    public MyPagerAdapter(FragmentManager fm, String id) {
        super(fm);
    }

    private static final String[] PAGE_TITLES = {"笔记", "添加", "我的"};

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new fragment_record();
            case 1:
                return new fragment_add();
            case 2:
                return new fragment_mine();
//            case 3:
//                return new fragment_llm();
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
