package com.chillax.softwareyard.activity;

import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.chillax.softwareyard.R;
import com.chillax.softwareyard.fragment.LeaderItem;
import com.chillax.softwareyard.fragment.LeaderItem_;
import com.chillax.softwareyard.utils.StatesUtils;
import com.chillax.viewpagerindicator.IconPageIndicator;
import com.chillax.viewpagerindicator.IconPagerAdapter;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Chillax on 2015/8/14.
 */
@EActivity(R.layout.leader_layout)
public class FirstLeader extends FragmentActivity implements LeaderItem.onComeinClickedListener {
    @ViewById
    ViewPager vp;
    @ViewById
    IconPageIndicator indicator;
//    GestureDetector detector;

    @AfterViews
    void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // create our manager instance after the content view is set
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            // enable status bar tint
            tintManager.setStatusBarTintEnabled(true);
            // enable navigation bar tint
            tintManager.setNavigationBarTintEnabled(true);
            // set a custom tint color for all system bars
            tintManager.setTintColor(getResources().getColor(R.color.actionbar_bg));
        }
        for (int i = 0; i < 3; i++) {
            fgs[i] = LeaderItem_.builder().build();
        }
        vp.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        indicator.setViewPager(vp);
        fgs[2].setOnComeinClickedListener(this);
//        detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
//            @Override
//            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//                System.out.println(velocityY);
//                if (vp.getCurrentItem() == 2 && velocityX >200) {
//                    onComeInClicked();
//                    return true;
//                }
//                return false;
//            }
//        });
//        vp.setOnTouchListener((v,e)->detector.onTouchEvent(e));
    }

    private LeaderItem[] fgs = new LeaderItem_[3];

    @Override
    public void onComeInClicked() {
        StatesUtils utils=new StatesUtils(this);
        utils.setFirstUse(false);
        if (utils.isLogin()) {
            MainActivity_.intent(this).start();
        } else {
            LoginActivity_.intent(this).start();
        }
        finish();
        overridePendingTransition(R.anim.slide_clam, R.anim.slide_out_left);
    }

    class MyPagerAdapter extends FragmentPagerAdapter implements IconPagerAdapter {
        @Override
        public int getIconResId(int index) {
            return 0;
        }


        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fgs[position];
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            super.finishUpdate(container);
            fgs[0].setPager(0);
            fgs[1].setPager(1);
        }

        @Override
        public int getCount() {
            return fgs.length;
        }
    }

}
