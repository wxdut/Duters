package com.chillax.softwareyard.activity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chillax.softwareyard.App;
import com.chillax.softwareyard.R;
import com.chillax.softwareyard.adapter.DownDataAdapter;
import com.chillax.softwareyard.customview.TopBar;
import com.chillax.softwareyard.model.Doc;
import com.chillax.softwareyard.utils.CommonUtils;
import com.chillax.softwareyard.utils.CusDialog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@EActivity(R.layout.download_layout)
public class DownLoadCenter extends BaseActivity implements TopBar.onTopClickedListener {
    @ViewById(R.id.topBar)
    TopBar mActionBar;
    @ViewById(R.id.list)
    ListView mLv;
    private List<Doc> dataList = App.docList;
    private DownDataAdapter mAdapter;
    private Dialog dialog;
    private int selectposition = 0;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            mAdapter.notifyDataSetChanged();
        }
    };
    @AfterViews
    void inits() {
        initDialog();
        mActionBar.setTopListener(this);
        mAdapter = new DownDataAdapter(this);
        mLv.setAdapter(mAdapter);
        mLv.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            selectposition = position;
            dialog.show();
        });
        timer.schedule(timerTask, 0, 200);
    }

    private void initDialog() {
        dialog = CusDialog.create2(this, (position) -> {
            switch (position) {
                case 0:
                    try {
                        File file = new File(dataList.get(selectposition).getLocal());
                        if (!file.exists()) {
                            Toast.makeText(DownLoadCenter.this, "文件不存在,请重新下载", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        String type = CommonUtils.suff2Type(dataList.get(selectposition).getName().split("\\.")[1]);
                        Intent intent = new Intent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(file), type);
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(DownLoadCenter.this, "未找到打开此文件的应用", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 1:
                    File file = new File(dataList.get(selectposition).getLocal());
                    if (!file.exists()) {
                        break;
                    }
                    file.delete();
                    dataList.remove(selectposition);
                    mAdapter.notifyDataSetChanged();
                    Toast.makeText(DownLoadCenter.this,"删除成功",Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    break;
            }
            dialog.dismiss();
        });
    }

    private Timer timer = new Timer();
    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            handler.obtainMessage().sendToTarget();
        }
    };

    @Override
    public void onBack(View view) {
        onBackPressed();
    }

    @Override
    public void onMore(View view) {

    }

    @Override
    public void onInit(ImageView back, TextView title, ImageView moreIv, TextView moreTv) {
        title.setText("周知文件");
    }
}
