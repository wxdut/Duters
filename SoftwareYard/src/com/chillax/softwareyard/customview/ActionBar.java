package com.chillax.softwareyard.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chillax.softwareyard.R;
import com.chillax.softwareyard.utils.ScreenUtil;
import com.chillax.softwareyard.utils.StatesUtils;
import com.nineoldandroids.animation.ObjectAnimator;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.actionbar)
public class ActionBar extends RelativeLayout implements AdapterView.OnItemClickListener {

    @ViewById(R.id.topBar_logo)
    ImageView logo;
    @ViewById(R.id.topBar_title)
    TextView title;
    @ViewById(R.id.topBar_more)
    ImageView more;
    @ViewById(R.id.topBar_spinner)
    TextView spinner;
    @ViewById(R.id.topBar_table)
    ViewGroup forTableVG;
    @ViewById(R.id.topBar_arrow)
    ImageView arrow;
    private onTopBarClickedListener mListener;
    private Context mContext;
    private PopupWindow pw;
    private ListView pw_lv;
    private String[] weeks = new String[]{"刷新课表","第一周", "第二周", "第三周", "第四周", "第五周",
            "第六周", "第七周", "第八周", "第九周", "第十周", "第十一周", "第十二周", "第十三周", "第十四周",
            "第十五周", "第十六周", "第十七周", "第十八周", "第十九周", "第二十周"};

    public ActionBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, 0);
        mContext = context;
    }
    @AfterViews
    void initView() {
//        try{
//            spinner.setText(weeks[TableFragment.currWeek]);
//        }catch (Exception e){
//            spinner.setText("假期");
//        }
        statesUtils=new StatesUtils(mContext);
        refreshPopuWeek();
    }

    public ActionBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ActionBar(Context context) {
        this(context, null, 0);
    }

    @Click
    void topBar_logo() {
        if (mListener != null) {
            mListener.onLogoClicked();
        }
    }

    @Click
    void topBar_title() {
        if (mListener != null) {
            mListener.onTitleClicked();
        }
    }

    @Click
    void topBar_more(View view) {
        if (mListener != null) {
            mListener.onMoreClicked(more);
        }
    }

    @Click(R.id.topBar_table)
    void forTableVG(View view) {
        if (pw == null) {
            pw_lv = new ListView(mContext);
            pw_lv.setAdapter(new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, weeks));
            pw_lv.setBackgroundResource(R.drawable.spinner_bg);
            pw = new PopupWindow(pw_lv, ScreenUtil.dp2px(mContext, 100), ScreenUtil.dp2px(mContext, 300));
            pw.setBackgroundDrawable(getResources().getDrawable(R.drawable.news_detail_popu_bg));
            pw.setFocusable(true);
            pw_lv.setOnItemClickListener(this);
            pw.setOutsideTouchable(false);
            pw.setOnDismissListener(() ->
                            ObjectAnimator.ofFloat(arrow, "rotation", 180, 0).setDuration(200).start()
            );
        }
        pw.showAsDropDown(this, forTableVG.getLeft(), 0);
        ObjectAnimator.ofFloat(arrow,"rotation",0,180).setDuration(200).start();
    }

    public void setTitleText(String text) {
        title.setText(text);
    }

    public void setOnTopBarClickedListener(onTopBarClickedListener listener) {
        mListener = listener;
    }

    public void initTopBar(String tag) {
        more.setVisibility(View.INVISIBLE);
        if (tag.equals("StoreCenter")) {
            title.setText("收藏中心");
        } else if (tag.equals("NewsDetail")) {
            title.setText("学生周知详情页");
            more.setVisibility(View.VISIBLE);
            more.setImageResource(R.drawable.more);
        } else if (tag.equals("DownLoadCenter")) {
            title.setText("下载中心");
        } else if (tag.equals("WebViewShower")) {
            title.setText("");
        } else if (tag.equals("SettingCenter")) {
            title.setText("设置中心");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mListener != null) {
            spinner.setText(weeks[position]);
            mListener.onSpinnerItemClicked(position==0?-1:position-1);
        }
        if(pw.isShowing()){
            pw.dismiss();
        }
    }
    private StatesUtils statesUtils;
    public void refreshPopuWeek() {
        spinner.setText(weeks[1+statesUtils.getCurrWeekOfTerm()]);
    }

    public interface onTopBarClickedListener {
        void onLogoClicked();

        void onSpinnerItemClicked(int position);

        void onTitleClicked();

        void onMoreClicked(View view);
    }

    public void setCurrWeek(int currWeek) {
        try {
            spinner.setText(weeks[currWeek+1]);
        }catch (Exception e){

        }
    }

    public void setTableSelection(int index) {
        switch (index) {
            case 0:
                logo.setVisibility(View.INVISIBLE);
                title.setText("学生周知");
                more.setVisibility(View.GONE);
                forTableVG.setVisibility(View.GONE);
                break;
            case 1:
                logo.setVisibility(View.INVISIBLE);
                logo.setImageResource(R.drawable.refresh_icon);
                title.setText("课程表");
                more.setVisibility(View.GONE);
                spinner.setVisibility(View.VISIBLE);
                forTableVG.setVisibility(View.VISIBLE);
                break;
            case 2:
                logo.setVisibility(View.GONE);
                title.setText("个人中心");
                more.setVisibility(View.VISIBLE);
                more.setImageResource(R.drawable.more);
                forTableVG.setVisibility(View.GONE);
                break;
        }
    }

}
