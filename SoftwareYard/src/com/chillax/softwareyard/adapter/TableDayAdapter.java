package com.chillax.softwareyard.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.chillax.softwareyard.fragment.TableItemFragment;

import java.util.ArrayList;
import java.util.List;

public class TableDayAdapter extends FragmentPagerAdapter{

    private String[] days = new String[]{"星期一", "星期二", "星期三", "星期四", "星期五",
            "星期六", "星期日"};
    private Context context;
    public TableDayAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
//        fraglist = new ArrayList<>();
//        for (int i = 0; i < 7; i++) {
//            TableItemFragment bf = new TableItemFragment(context, i);
//            fraglist.add(bf);
//        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return days[position % 7];
    }

    @Override
    public Fragment getItem(int arg0) {
        return new TableItemFragment(context, arg0 / 7, arg0 % 7);
    }

    @Override
    public int getCount() {
        return 20 * 7;
    }
    public void refreshNote(int currPage){
        ((TableItemFragment)getItem(currPage)).refrush();
    }

}