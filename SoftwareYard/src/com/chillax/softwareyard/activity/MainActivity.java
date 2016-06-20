package com.chillax.softwareyard.activity;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;

import com.chillax.config.Constant;
import com.chillax.softwareyard.R;
import com.chillax.softwareyard.customview.ActionBar;
import com.chillax.softwareyard.customview.ActionBar.onTopBarClickedListener;
import com.chillax.softwareyard.customview.BottomMenu;
import com.chillax.softwareyard.customview.BottomMenu.onBottomMenuClickedListener;
import com.chillax.softwareyard.dao.DownDBDao;
import com.chillax.softwareyard.fragment.BaseFragment;
import com.chillax.softwareyard.fragment.NewsFragment_;
import com.chillax.softwareyard.fragment.SelfFragment;
import com.chillax.softwareyard.fragment.SelfFragment_;
import com.chillax.softwareyard.fragment.TableFragment;
import com.chillax.softwareyard.fragment.TableFragment_;
import com.chillax.softwareyard.utils.NetWorkListener;
import com.chillax.softwareyard.utils.ScreenUtil;
import com.chillax.softwareyard.utils.StatesUtils;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.umeng.update.UmengUpdateAgent;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;

/**
 * create by chillax data:2015.6.17
 *
 * @author Chillax
 */
@EActivity(R.layout.activity_main)
public class MainActivity extends FragmentActivity implements
        onTopBarClickedListener, onBottomMenuClickedListener {
    @ViewById(R.id.topBar)
    ActionBar mActionBar;
    @ViewById(R.id.bottommenu)
    BottomMenu mBottomMenu;
    private BaseFragment[] fms;
    private FragmentManager fm;
    private IntentFilter mFilter;
    private NetWorkListener mNetListener;
    private int currIndex = 0;
    private StatesUtils statesUtils;

    @AfterViews
    protected void initView() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            // create our manager instance after the content view is set
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            // enable status bar tint
            tintManager.setStatusBarTintEnabled(true);
            // enable navigation bar tint
            tintManager.setNavigationBarTintEnabled(true);
            // set a custom tint color for all system bars
            tintManager.setTintColor(getResources().getColor(R.color.actionbar_bg));
        }
        statesUtils = new StatesUtils(this);
        statesUtils.setFirstUse(false);
        statesUtils.setAppStates(true);//设置是否处于打开状态
        fm = getSupportFragmentManager();
        fms = new BaseFragment[3];
        mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mNetListener = new NetWorkListener();
        registerReceiver(mNetListener, mFilter);
        mActionBar.setOnTopBarClickedListener(this);
        mBottomMenu.setTabSelection(0);
        mBottomMenu.setOnMenuClickedListener(this);
        setTabSelection(0);
        TABLE_HEIGHT = ScreenUtil.getScreenHeight(this);
        TABLE_HEIGHT-=ScreenUtil.getStatusBarHeight(this);
        mActionBar.post(() -> {
            synchronized (MainActivity.this) {
                TABLE_HEIGHT -= mActionBar.getMeasuredHeight();
            }
        });
        mBottomMenu.post(() -> {
            synchronized (MainActivity.this) {
                TABLE_HEIGHT -= mBottomMenu.getMeasuredHeight();
            }
        });
        UmengUpdateAgent.update(this);
        Bmob.initialize(this, Constant.appId);
        // 使用推送服务时的初始化操作
        BmobInstallation.getCurrentInstallation(this).save();
        // 启动推送服务
        BmobPush.startWork(this, Constant.appId);
    }

    @Override
    public void setTabSelection(int index) {
        currIndex = index;
        mActionBar.setTableSelection(index);
        mBottomMenu.setTabSelection(index);
        FragmentTransaction ft = fm.beginTransaction();
        hideFragments(ft);
        switch (index) {
            case 0:
                if (fms[0] == null) {
                    fms[0] = new NewsFragment_();
                    ft.add(R.id.content, fms[0], "news");
                } else {
                    ft.show(fms[0]);
                }
                break;
            case 1:
                if (fms[1] == null) {
                    fms[1] = new TableFragment_();
                    ft.add(R.id.content, fms[1], "table");
                } else {
                    ft.show(fms[1]);
                }
                break;
            case 2:
                if (fms[2] == null) {
                    fms[2] = new SelfFragment_();
                    ft.add(R.id.content, fms[2], "self");
                } else {
                    ft.show(fms[2]);
                }
                break;
        }
        ft.commit();
    }

    private void hideFragments(FragmentTransaction ft) {
        for (int i = 0; i < 3; i++) {
            if (fms[i] != null) {
                ft.hide(fms[i]);
            }
        }
    }

    @Override
    public void onLogoClicked() {
//        switch (currIndex){
//            case 1:
//                TableFragment tableFrag = (TableFragment) fm.findFragmentByTag("table");
//                if (tableFrag != null) {
//                    tableFrag.refreshData(Constant.KEY_UPDATE_TABLE_DATA);
//                }
//                break;
//        }
    }
    public ActionBar getActionBar2(){
        return mActionBar;
    }
    @Override
    public void onMoreClicked(View view) {
        switch (currIndex) {
//            case 1:
//                ((TableFragment) fm.findFragmentByTag("table"))
//                        .refrushData(App.currWeek);
//                break;
            case 2:
                ((SelfFragment) fm.findFragmentByTag("self")).onMoreClicked();
                break;
        }
    }

    @Override
    public void onTitleClicked() {

    }

    @Override
    public void onSpinnerItemClicked(int position) {
        TableFragment tableFrag = (TableFragment) fm.findFragmentByTag("table");
        if (tableFrag != null) {
            tableFrag.refreshData(position);
        }
    }

//    private long lastTime = 0;
//
//    @Override
//    public void onBackPressed() {
//        if (System.currentTimeMillis() - lastTime > 2000) {
//            lastTime = System.currentTimeMillis();
//            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
//        } else {
//            super.onBackPressed();
//        }
//    }

    /**
     * 设置程序后台运行
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onDestroy() {
        new DownDBDao(this).onDestroy();
        unregisterReceiver(mNetListener);
        statesUtils.setAppStates(false);
        super.onDestroy();
    }

    //为课程表提供精确的高度
    public static int TABLE_HEIGHT;

    @Override
    protected void onStop() {
        super.onStop();
        mActionBar.refreshPopuWeek();
        onSpinnerItemClicked(statesUtils.getCurrWeekOfTerm());
    }
}