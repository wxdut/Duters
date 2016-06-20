package com.chillax.softwareyard.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chillax.softwareyard.R;
import com.chillax.softwareyard.customview.TopBar;
import com.chillax.softwareyard.model.ExamResult;
import com.chillax.softwareyard.model.ExamShedule;
import com.chillax.softwareyard.utils.CacheUtils;
import com.chillax.softwareyard.utils.CommonUtils;
import com.chillax.softwareyard.utils.NetworkChecker;
import com.chillax.softwareyard.utils.ScreenUtil;
import com.chillax.softwareyard.utils.StatesUtils;
import com.lidroid.xutils.util.LogUtils;
import com.yalantis.taurus.PullToRefreshView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xiao on 2015/9/28.
 */
@EActivity(R.layout.exam_shedule_layout)
public class ExamSchedule extends BaseActivity implements TopBar.onTopClickedListener, ExpandableListView.OnChildClickListener {
    @ViewById(R.id.list)
    ExpandableListView mLv;
    @ViewById(R.id.pull_to_refresh)
    PullToRefreshView mPtrv;
    @ViewById(R.id.tv_refresh)
    TextView mTv;
    //    @ViewById(R.id.refreshLv)
//    PullToRefreshExpandableListView mRefreshLv;
    @ViewById(R.id.topBar)
    TopBar mTopbar;
    BaseExpandableListAdapter mAdapter;
    List<ExamShedule> dataList = new ArrayList<>();
    CacheUtils cacheUtils;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NET_ERROR:
                    showToast("网络错误");
                    mPtrv.setRefreshing(false);
                    mTv.setVisibility(View.VISIBLE);
                    break;

            }
        }
    };

    @AfterViews
    void init() {
        mTopbar.setTopListener(this);
//        mLv = mRefreshLv.getRefreshableView();
        mLv.setOnChildClickListener(this);
        cacheUtils = new CacheUtils(this, CacheUtils.CacheType.FOR_EXAM_SCHEDULE);
        initData();
        mAdapter = new MyAdapter();
        mLv.setBackgroundResource(R.drawable.exam_score_list_bg);
        mLv.setCacheColorHint(Color.parseColor("#00000000"));
        mLv.setDivider(new ColorDrawable(Color.parseColor("#ebebeb")));
        mLv.setDividerHeight(ScreenUtil.dp2px(this, 1));
        mLv.setFooterDividersEnabled(false);
        mLv.setGroupIndicator(null);
        mLv.setAdapter(mAdapter);
//        mRefreshLv.setOnRefreshListener((refreshView) -> {
//                    refreshData();
//                    mRefreshLv.onRefreshComplete();
//                }
//        );
        mPtrv.setOnRefreshListener(() -> refreshData());
        if (dataList.size() == 0) {
            mTv.setVisibility(View.VISIBLE);
        }
    }

    private void initData() {
        int i = 0;
        String line = null;
        String[] data;
        while ((line = cacheUtils.getCache(i++ + "")) != null) {
            data = line.split("::");
            dataList.add(new ExamShedule(data[0], data[1], data[2], data[3], data[4], data[5], data[6], data[7], data[8]));
        }
    }

    @Override
    public void onBack(View view) {
        onBackPressed();
    }

    @Override
    public void onMore(View view) {
        if (((TextView) view).getText().equals("展开")) {
            ((TextView) view).setText("合并");
            toggle(true);
        } else {
            ((TextView) view).setText("展开");
            toggle(false);
        }

    }

    private void refreshData() {
        if(!NetworkChecker.IsNetworkAvailable(this))return;
        new AsyncTask<String, String, String>() {
            private String userName, userPwd;
            private StatesUtils statesUtils;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                statesUtils = new StatesUtils(ExamSchedule.this);
                userName = statesUtils.getUserName();
                userPwd = statesUtils.getUserPwd();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if(result==null)return;
                Document docs = Jsoup.parse(result);
                Elements datas = docs.getElementsByClass("odd");
                if (datas != null && datas.size() > 0) {
                    dataList.clear();
                    for (Element exam : datas) {
                        Elements exam2 = exam.getElementsByTag("td");
                        dataList.add(new ExamShedule(exam2.get(0).text(), exam2.get(1).text(), exam2.get(2).text(), exam2.get(3).text(), exam2.get(4).text(), exam2.get(5).text(), exam2.get(6).text(), exam2.get(7).text(), exam2.get(8).text()));
                    }

                    showToast("获取数据成功~");

                } else {
                    showToast("暂无考试安排~~");
                }
                mAdapter.notifyDataSetInvalidated();
                mPtrv.setRefreshing(false);
                mTv.setVisibility(View.GONE);
            }

            @Override
            protected String doInBackground(String... params) {
                String cookie = new HttpCookie("JSESSIONID", CommonUtils.md5(userName)).toString();
                StringBuffer result = new StringBuffer();
                try {
                    URL loginUrl = new URL(
                            com.chillax.config.URL.loginUrl + "?zjh=" + userName
                                    + "&mm=" + userPwd);
                    URL tableUrl = new URL(
                            com.chillax.config.URL.ksapUrl);
                    HttpURLConnection conn = (HttpURLConnection) loginUrl
                            .openConnection();
                    conn.addRequestProperty("Cookie",
                            cookie);
                    conn.setConnectTimeout(3000);
                    conn.setReadTimeout(3000);
                    InputStream is = conn.getInputStream();
                    is.close();
                    conn.disconnect();
                    HttpURLConnection conn1 = (HttpURLConnection) tableUrl
                            .openConnection();
                    conn1.addRequestProperty("Cookie",
                            cookie);
                    InputStream is1 = conn1.getInputStream();
                    BufferedReader reader1 = new BufferedReader(new InputStreamReader(
                            is1, "gbk"));
                    String lineStr1 = null;
                    while ((lineStr1 = reader1.readLine()) != null) {
                        result.append(lineStr1);
                    }
                    reader1.close();
                    is1.close();
                    conn1.disconnect();
                } catch (Exception e) {
                    LogUtils.e("扒取成绩数据失败！");
                    mHandler.obtainMessage(NET_ERROR).sendToTarget();
                    e.printStackTrace();
                    return null;
                }
                return result.toString();
            }
        }.execute();
    }

    @Override
    public void onInit(ImageView back, TextView title, ImageView moreIv, TextView moreTv) {
        title.setText("考试安排");
        moreTv.setText("展开");
        moreTv.setVisibility(View.VISIBLE);
    }

    public void toggle(boolean bool) {
        if (bool) {
            for (int i = 0; i < mAdapter.getGroupCount(); i++) {
                mLv.expandGroup(i);
            }
        } else {
            for (int i = 0; i < mAdapter.getGroupCount(); i++) {
                mLv.collapseGroup(i);
            }
        }
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

        return false;
    }

    public class MyAdapter extends BaseExpandableListAdapter {
        LayoutInflater inflater;

        public MyAdapter() {
            inflater = LayoutInflater.from(ExamSchedule.this);

        }

        @Override
        public int getGroupCount() {
            return dataList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            ExamShedule result = dataList.get(groupPosition);
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.exam_score_list_item, null);
                holder.name = (TextView) convertView.findViewById(R.id.course_name);
                holder.category = (TextView) convertView.findViewById(R.id.course_category);
                holder.arrow = (ImageView) convertView.findViewById(R.id.arrow);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.name.setText(result.getCourseName());
            if (isExpanded) {
                holder.arrow.setImageResource(R.drawable.exam_score_list_top_icon);
            } else {
                holder.arrow.setImageResource(R.drawable.exam_score_list_bottom_icon);
            }
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ExamShedule result = dataList.get(groupPosition);
            ListView lv = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.exam_socre_list_detail_list, null);
                lv = (ListView) convertView.findViewById(R.id.list);
                lv.setAdapter(new ChildAdapter(groupPosition));
                convertView.setTag(lv);
            } else {
                lv = (ListView) convertView.getTag();
                ((ChildAdapter) lv.getAdapter()).setPos(groupPosition);
            }
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    class ViewHolder {
        TextView name, category;
        ImageView arrow;
    }

    class ChildAdapter extends BaseAdapter {
        String[] childData = new String[]{"考试名：",
                "校区：",
                "教学楼：",
                "教室：",
                "课程：",
                "考试周次：",
                "考试星期：", "考试时间：", "准考证号："};
        int pos = 0;

        public ChildAdapter(int pos) {
            this.pos = pos;
        }

        public void setPos(int pos) {
            this.pos = pos;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return childData.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ExamShedule result = dataList.get(pos);
            com.chillax.softwareyard.adapter.ViewHolder holder = com.chillax.softwareyard.adapter.ViewHolder.get(ExamSchedule.this, convertView, android.R.layout.simple_list_item_1, position, null);
            holder.setText(android.R.id.text1, childData[position] + result.get(position));
            return holder.getConvertView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dataList != null && dataList.size() > 0) {
            int i = 0;
            cacheUtils.clear();
            for (ExamShedule exam : dataList) {
                cacheUtils.setCache(i + "", dataList.get(i++).toString());
            }
        }
    }
}
