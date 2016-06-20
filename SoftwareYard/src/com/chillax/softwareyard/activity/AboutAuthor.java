package com.chillax.softwareyard.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chillax.softwareyard.R;
import com.chillax.softwareyard.customview.TopBar;

/**
 * Created by Chillax on 2015/8/13.
 */
public class AboutAuthor extends BaseActivity implements TopBar.onTopClickedListener{
    private TopBar topBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aboutauthor_layout);
        topBar=(TopBar)findViewById(R.id.topBar);
        topBar.setTopListener(this);
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
        title.setText("关于我们");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_clam,R.anim.slide_out_right);
    }
}
