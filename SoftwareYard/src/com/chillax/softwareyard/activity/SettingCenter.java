package com.chillax.softwareyard.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.chillax.config.Path;
import com.chillax.softwareyard.R;
import com.chillax.softwareyard.adapter.ViewHolder;
import com.chillax.softwareyard.customview.ActionBar;
import com.chillax.softwareyard.dao.CoursesDBDao;
import com.chillax.softwareyard.dao.DetailDBDao;
import com.chillax.softwareyard.utils.CacheUtils;
import com.chillax.softwareyard.utils.CusDialog;
import com.chillax.softwareyard.utils.StatesUtils;
import com.lidroid.xutils.util.LogUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chillax on 2015/8/11.
 */
@EActivity(R.layout.settingcenter_layout)
public class SettingCenter extends BaseActivity implements ActionBar.onTopBarClickedListener,
        AdapterView.OnItemClickListener, DialogInterface.OnClickListener {
    @ViewById(R.id.topBar)
    ActionBar mActionBar;
    @ViewById(R.id.listview)
    ListView mLv;
    private List<String> settingData = new ArrayList<>();
    private CoursesDBDao mDao;
    private DetailDBDao mDao2;
    private Dialog dialog;
    private StatesUtils utils;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            dialog.cancel();
            Toast.makeText(SettingCenter.this, "注销成功", Toast.LENGTH_SHORT).show();
            utils.setLoginStates(false);
            setResult(Activity.RESULT_OK);
            LoginActivity_.intent(SettingCenter.this).start();
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    };

    @AfterViews
    void inits() {
        mActionBar.initTopBar("SettingCenter");
        mActionBar.setOnTopBarClickedListener(this);
        settingData.add("系统消息");
        settingData.add("用户反馈");
        settingData.add("关于我们");
        settingData.add("注销登录");
        mLv.setAdapter(new SettingAdapter());
        mLv.setOnItemClickListener(this);
        mDao = new CoursesDBDao(this);
        mDao2 = new DetailDBDao(this);
        utils = new StatesUtils(this);
        initDialog();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i) {
            case 0:
                Toast.makeText(this, "暂无系统消息", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                Intent intent = FeedBack_.intent(this).get();
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_clam);
                break;
            case 2:
                startActivity(new Intent(this, AboutAuthor.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_clam);
                break;
            case 3:
                dialog.show();
                break;
        }
    }
    class SettingAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return settingData.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder = ViewHolder.get(SettingCenter.this, view, android.R.layout.simple_list_item_1, i, null);
            holder.setText(android.R.id.text1, settingData.get(i));
            return holder.getConvertView();
        }
    }

    public void logout() {
        dialog.show();
        new Thread(()->{
                try {
                    mDao.clear();
                    mDao2.clear();
                    new CacheUtils(this, CacheUtils.CacheType.FOR_NOTE_CACHE).clear();
                    new CacheUtils(this, CacheUtils.CacheType.FOR_EXAM_RESULT).clear();
                    new CacheUtils(this, CacheUtils.CacheType.FOR_EXAM_RESULT_ALL).clear();
                    new CacheUtils(this, CacheUtils.CacheType.FOR_EXAM_SCHEDULE).clear();
                    File file=new File(Path.userImage);
                    if(file.exists()){
                        file.delete();
                    }
                    file=new File(Path.userImage1);
                    if(file.exists()){
                        file.delete();
                    }
                    file=new File(Path.userImage2);
                    if(file.exists()){
                        file.delete();
                    }
                    handler.obtainMessage().sendToTarget();
                } catch (Exception e) {
                    LogUtils.e("注销失败");
                }
        }).start();
    }

    private void initDialog() {
        dialog = new AlertDialog.Builder(this).setTitle("注销确认").setMessage("您确认要注销登录吗？")
                .setCancelable(false).setPositiveButton("确认", this).setNegativeButton("取消", this).create();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        dialog.cancel();
        switch (i) {
            case DialogInterface.BUTTON_NEGATIVE:
                break;
            case DialogInterface.BUTTON_POSITIVE:
                dialog = CusDialog.create(this, "注销中");
                logout();
                break;
            default:
                break;
        }
    }

    @Override
    public void onLogoClicked() {
        onBackPressed();
    }

    @Override
    public void onSpinnerItemClicked(int position) {
    }

    @Override
    public void onTitleClicked() {

    }

    @Override
    public void onMoreClicked(View view) {
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_clam, R.anim.slide_out_right);
    }
}
