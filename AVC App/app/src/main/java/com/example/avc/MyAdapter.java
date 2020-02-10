package com.example.avc;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

class MyAdapter extends FragmentPagerAdapter {
    private int totalTabs;

    MyAdapter(Context c, FragmentManager fm, int totalTabs) {
        super(fm);
        this.totalTabs = totalTabs;
    }
    @Nullable
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new Information();
            case 1:
                return new Testing();
            case 2:
                return new Maps();
            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return totalTabs;
    }
}