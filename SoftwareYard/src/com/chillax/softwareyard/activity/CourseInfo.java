package com.chillax.softwareyard.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chillax.softwareyard.R;
import com.chillax.softwareyard.customview.TopBar;
import com.chillax.softwareyard.utils.CacheUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.List;

/**
 * Created by Chillax on 2015/8/14.
 */
@EActivity(R.layout.course_detail_layout)
public class CourseInfo extends BaseActivity implements TopBar.onTopClickedListener {
    @ViewById(R.id.list_left)
    ListView leftView;
    @ViewById(R.id.list_right)
    ListView rightView;
    @ViewById(R.id.topbar)
    TopBar topBar;
    CacheUtils cacheUtils;
    ArrayAdapter rightAdapter;
    List<String> rightDatas;
    Dialog myDialog;
    String courseName; //保存课程名，用于缓存备注信息
    @AfterViews
    void init() {
        topBar.setTopListener(this);
        leftView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, leftDatas));
        rightDatas = getIntent().getStringArrayListExtra("info");
        if (rightDatas != null&&rightDatas.size()>0) {
            courseName=rightDatas.get(0);
            cacheUtils = new CacheUtils(this, CacheUtils.CacheType.FOR_NOTE_CACHE);
            String note = cacheUtils.getCache(getIntent().getStringExtra("note_order"));
            if (!TextUtils.isEmpty(note)) {
                rightDatas.add(note);
            }
            rightAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, rightDatas);
            rightView.setAdapter(rightAdapter);
            myDialog = new MyDialog(this);
        }
    }

    private String[] leftDatas = new String[]{"课程名：", "地点：", "上课周：", "学分：", "属性：", "课序号：", "教师：", "备注："};

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_clam, R.anim.slide_out_right);
    }

    @Override
    public void onBack(View view) {
        onBackPressed();
    }

    @Override
    public void onMore(View view) {
        if (view.getClass() == TextView.class) {
            if (cacheUtils != null) {
                myDialog.show();
            }
        }
    }

    @Override
    public void onInit(ImageView back, TextView title, ImageView moreIv, TextView moreTv) {
        title.setText("课程详情");
        moreTv.setText("更改备注");
        moreTv.setVisibility(View.VISIBLE);
    }

    class MyDialog extends Dialog implements View.OnClickListener{
        Button cancel, ok;
        EditText note;
        public MyDialog(Context context) {
            super(context, R.style.transparentFrameWindowStyle);
            setContentView(R.layout.course_info_note_dialog);
            setCancelable(false);
            setCanceledOnTouchOutside(false);
            Window window = getWindow();
            WindowManager.LayoutParams wl = window.getAttributes();
            wl.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            // 设置显示位置
            onWindowAttributesChanged(wl);
            cancel = (Button) findViewById(R.id.cancel);
            ok = (Button) findViewById(R.id.ok);
            note= (EditText) findViewById(R.id.note);
            cancel.setOnClickListener(this);
            ok.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            String noteStr = note.getText().toString();
            if (v == ok) {
                cacheUtils.setCache(getIntent().getStringExtra("note_order"),noteStr);
                if(rightDatas.size()==leftDatas.length){
                    rightDatas.remove(rightDatas.size()-1);
                    rightDatas.add(rightDatas.size(),noteStr);
                }else {
                    rightDatas.add(noteStr);
                }
                rightAdapter.notifyDataSetChanged();
                setResult(Activity.RESULT_OK);
            }
            dismiss();
            note.setText("");
        }
    }
}
