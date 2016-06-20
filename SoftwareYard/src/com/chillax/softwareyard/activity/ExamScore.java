package com.chillax.softwareyard.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chillax.config.URL;
import com.chillax.softwareyard.R;
import com.chillax.softwareyard.customview.TopBar;
import com.chillax.softwareyard.fragment.ScoreFrag;
import com.chillax.softwareyard.fragment.ScoreFrag_;
import com.chillax.viewpagerindicator.TitlePageIndicator;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Xiao on 2015/9/18.
 */
@EActivity(R.layout.exam_score_layout)
public class ExamScore extends BaseActivity implements TopBar.onTopClickedListener,ViewPager.OnPageChangeListener{


    @ViewById(R.id.topBar)
    TopBar mTopbar;
    @ViewById(R.id.vp)
    ViewPager mVp;
    @ViewById(R.id.titles)
    TitlePageIndicator mIndicator;
    ScoreFrag frag[];
    String status[];//记录展开和合并的状态
    int currPager;
    @AfterViews
    void initViews(){
        mVp.addOnPageChangeListener(this);
        mTopbar.setTopListener(this);
        frag=new ScoreFrag_[2];
        frag[0]=new ScoreFrag_();
        frag[0].setUrl(URL.bxqchengjiUrl);
        frag[1]=new ScoreFrag_();
        frag[1].setUrl(URL.allchengjiUrl);
        status=new String[2];
        status[0]="展开";
        status[1]="展开";
        mVp.setAdapter(new ScoreAdapter(getSupportFragmentManager()));
        mIndicator.setViewPager(mVp);
        mIndicator.setCurrentItem(0);

    }

    @Override
    public void onBack(View view) {
        onBackPressed();
    }

    @Override
    public void onMore(View view) {
        if (((TextView) view).getText().equals("展开")) {
            ((TextView) view).setText("合并");
            frag[currPager].toggle(true);
            status[currPager]="合并";
        } else {
            ((TextView) view).setText("展开");
            frag[currPager].toggle(false);
            status[currPager]="展开";
        }

    }
    @Override
    public void onInit(ImageView back, TextView title, ImageView moreIv, TextView moreTv) {
        title.setText("成绩查询");
        moreTv.setText("展开");
        moreTv.setVisibility(View.VISIBLE);
        this.moreTv=moreTv;
    }
    TextView moreTv;
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currPager=position;
        moreTv.setText(status[position]);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
    public class ScoreAdapter extends FragmentPagerAdapter{

        public ScoreAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return frag[position];
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return position==0?"本学期成绩":"全部成绩";
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
