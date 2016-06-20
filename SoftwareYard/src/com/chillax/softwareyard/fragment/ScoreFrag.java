package com.chillax.softwareyard.fragment;

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
import com.chillax.softwareyard.model.ExamResult;
import com.chillax.softwareyard.utils.CacheUtils;
import com.chillax.softwareyard.utils.CommonUtils;
import com.chillax.softwareyard.utils.NetworkChecker;
import com.chillax.softwareyard.utils.ScreenUtil;
import com.chillax.softwareyard.utils.StatesUtils;
import com.lidroid.xutils.util.LogUtils;
import com.yalantis.taurus.PullToRefreshView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
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
 * Created by MAC on 15/10/22.
 */
@EFragment(R.layout.exam_socore_frag_layout)
public class ScoreFrag extends BaseFragment implements ExpandableListView.OnChildClickListener {
    @ViewById(R.id.list)
    ExpandableListView mLv;
    @ViewById(R.id.pull_to_refresh)
    PullToRefreshView mPtrv;
    @ViewById(R.id.tv_refresh)
    TextView mTv;
    BaseExpandableListAdapter mAdapter;
    List<ExamResult> dataList = new ArrayList<>();
    CacheUtils cacheUtils;
    String mUrl = null;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NET_ERROR:
                    showToast("网络错误");
                    mPtrv.setRefreshing(false);
                    if(dataList.size()==0){
                        mTv.setVisibility(View.VISIBLE);
                    }else {
                        mTv.setVisibility(View.INVISIBLE);
                    }
                    break;

            }
        }
    };

    @AfterViews
    void init() {
        //        mLv = mRefreshLv.getRefreshableView();
        mLv.setOnChildClickListener(this);
        if(mUrl.equals(com.chillax.config.URL.bxqchengjiUrl)){
            cacheUtils = new CacheUtils(context, CacheUtils.CacheType.FOR_EXAM_RESULT);
        }
        else {
            cacheUtils = new CacheUtils(context, CacheUtils.CacheType.FOR_EXAM_RESULT_ALL);
        }
        initData();
        mAdapter = new MyAdapter();
        mLv.setBackgroundResource(R.drawable.exam_score_list_bg);
        mLv.setCacheColorHint(Color.parseColor("#00000000"));
        mLv.setDivider(new ColorDrawable(Color.parseColor("#ebebeb")));
        mLv.setDividerHeight(ScreenUtil.dp2px(context, 1));
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

    public void setUrl(String url) {
        this.mUrl = url;
    }

    private void initData() {
        int i = 0;
        String line;
        String[] data;
        while ((line = cacheUtils.getCache(i++ + "")) != null) {
            data = line.split("::");
            dataList.add(new ExamResult(data[0], data[1], data[2], data[3], data.length == 5 ? data[4] : ""));
        }
    }

    private void refreshData() {
        if(!NetworkChecker.IsNetworkAvailable(context))return;
        new AsyncTask<String, String, String>() {
            private String userName, userPwd;
            private StatesUtils statesUtils;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                statesUtils = new StatesUtils(context);
                userName = statesUtils.getUserName();
                userPwd = statesUtils.getUserPwd();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (result==null)return;
                Document docs = Jsoup.parse(result);
                Elements datas = docs.getElementsByClass("odd");
                if (datas != null && datas.size() > 0) {
                    dataList.clear();
                    for (Element exam : datas) {
                        Elements exam2 = exam.getElementsByTag("td");
                        dataList.add(new ExamResult(exam2.get(2).text(), exam2.get(5).text(), exam2.get(1).text(), exam2.get(4).text(), exam2.get(6).text()));
                    }

                    showToast("获取数据成功~");

                } else {
                    showToast("暂无考试成绩信息~");
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
                            mUrl);
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
            inflater = LayoutInflater.from(context);

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
            ExamResult result = dataList.get(groupPosition);
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
            holder.name.setText(result.getName());
            String score = result.getScore().replaceAll("\\s", "");
            holder.category.setText(TextUtils.isEmpty(score) ? "无结果" : (Double.valueOf(score) < 60 ? "未通过" : "已通过"));
            if (isExpanded) {
                holder.arrow.setImageResource(R.drawable.exam_score_list_top_icon);
            } else {
                holder.arrow.setImageResource(R.drawable.exam_score_list_bottom_icon);
            }
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ExamResult result = dataList.get(groupPosition);
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
        String[] childData = new String[]{"  课程名：    ",
                "  课程属性：",
                "  课序号：    ",
                "  学分：        ",
                "  成绩：        "};
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
            ExamResult result = dataList.get(pos);
            com.chillax.softwareyard.adapter.ViewHolder holder = com.chillax.softwareyard.adapter.ViewHolder.get(context, convertView, android.R.layout.simple_list_item_1, position, null);
            holder.setText(android.R.id.text1, childData[position] + result.get(position));
            return holder.getConvertView();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dataList != null && dataList.size() > 0) {
            int i = 0;
            cacheUtils.clear();
            for (ExamResult exam : dataList) {
                cacheUtils.setCache(i + "", dataList.get(i++).toString());
            }
        }
    }
}
