package com.chillax.softwareyard.activity;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chillax.softwareyard.R;
import com.chillax.softwareyard.customview.TopBar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.text.SimpleDateFormat;

/**
 * 反馈界面和图灵机器人界面雷同，这里直接使用图灵机器人的界面
 * Created by Chillax on 2015/8/8.
 */
@EActivity(R.layout.feedback_layout)
public class FeedBack extends BaseActivity implements TopBar.onTopClickedListener {

    @ViewById(R.id.topBar)
    TopBar mTopbar;

    @AfterViews
    void initViews(){
        mTopbar.setTopListener(this);
    }
    @Override
    public void onBack(View view) {
        onBackPressed();
    }

    @Override
    public void onMore(View view) {

    }

    @Override
    public void onInit(ImageView back, TextView title, ImageView moreIv, TextView moreTv) {
        title.setText("用户反馈");
    }
}
