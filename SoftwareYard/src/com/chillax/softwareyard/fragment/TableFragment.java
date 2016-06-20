package com.chillax.softwareyard.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.chillax.softwareyard.R;
import com.chillax.softwareyard.activity.MainActivity;
import com.chillax.softwareyard.adapter.TableDayAdapter;
import com.chillax.softwareyard.adapter.ViewHolder;
import com.chillax.softwareyard.dao.CoursesDBDao;
import com.chillax.softwareyard.dao.DetailDBDao;
import com.chillax.softwareyard.network.TableDataLoader;
import com.chillax.softwareyard.utils.CommonUtils;
import com.chillax.softwareyard.utils.CusDialog;
import com.chillax.softwareyard.utils.NetworkChecker;
import com.chillax.softwareyard.utils.ScreenUtil;
import com.chillax.softwareyard.utils.StatesUtils;
import com.chillax.viewpagerindicator.TitlePageIndicator;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

@EFragment(R.layout.table_fragment_day)
public class TableFragment extends BaseFragment implements OnPageChangeListener {
    private TableDayAdapter mAdapter;
    private LeftAdapter leftAdapter;
    public static int LIST_HEIGHT = 0;
    private int PER_ITEM_HEIGHT;
    @ViewById(R.id.titles)
    TitlePageIndicator mIndicator;
    @ViewById(R.id.pager)
    ViewPager mPager;
    @ViewById(R.id.leftLv)
    ListView mLeftLv;
    /**
     * 课程表当前周数（0-19）
     */
    private int currWeek = 0;

    @AfterViews
    void initViews() {
        statesUtils = new StatesUtils(context);
        currWeek = statesUtils.getCurrWeekOfTerm();
        PER_ITEM_HEIGHT = MainActivity.TABLE_HEIGHT;
        mIndicator.post(() -> {
            //减去Indicator的高度
            PER_ITEM_HEIGHT -= mIndicator.getMeasuredHeight();
            LIST_HEIGHT = PER_ITEM_HEIGHT;
            //减去Divider的高度
            PER_ITEM_HEIGHT -= ScreenUtil.dp2px(context, 1) * (leftTimes.length - 1);
            PER_ITEM_HEIGHT /= leftTimes.length;
            mAdapter = new TableDayAdapter(getChildFragmentManager(), context);
            mPager.setAdapter(mAdapter);
            mIndicator.setViewPager(mPager);
            //计算当前应该显示在第几页:当前周数*当前星期数
            int currIndex = currWeek * 7 + CommonUtils.getCurrDayOffWeek();
//            refrectThePager(currIndex);
            mIndicator.setCurrentItem(currIndex);
            leftAdapter = new LeftAdapter();
            mLeftLv.setAdapter(leftAdapter);
            mIndicator.setOnPageChangeListener(this);
        });
    }

//    private void refrectThePager(int curr) {
//        //用反射设置ViewPager遵循当前周数循环
//        try {
//            Field field = ViewPager.class.getDeclaredField("mCurItem");
//            field.setAccessible(true);
//            field.setInt(mPager, curr);
//            field.setAccessible(false);
//            mPager.addOnPageChangeListener(this);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static final int LOGIN_SUCESS = 0;
    public static final int DATA_ERROR = 1;
    public static final int NET_ERROR = 2;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            loadingDialog.dismiss();
            switch (msg.what) {
                case LOGIN_SUCESS:
                    currWeek = statesUtils.getCurrWeekOfTerm();
                    refreshData(currWeek);
                    CommonUtils.showToast(context, "课表更新成功");
                    ((MainActivity) context).getActionBar2().setCurrWeek(currWeek);
                    break;
                case NET_ERROR:
                    CommonUtils.showToast(context, "网络不可用");
                    break;
                case DATA_ERROR:
                    CommonUtils.showToast(context, "账号或密码有误");
                    break;
            }
        }
    };
    private String[] leftTimes = new String[]{"08:00", "08:50", "09:50", "10:40", "13:30", "14:20", "15:10", "16:00", "18:00", "18:50"};
    private String[] leftOrders = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    private int oldPos = 0;


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


    }

    @Override
    public void onPageSelected(int position) {
        if (position % 7 == 0 && oldPos + 1 == position) {
            //周数加一
            currWeek++;
            ((MainActivity) context).getActionBar2().setCurrWeek(currWeek);
        } else if (position % 7 == 6 && oldPos - 1 == position) {
            //周数减一
            currWeek--;
            ((MainActivity) context).getActionBar2().setCurrWeek(currWeek);
        }
        oldPos = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class LeftAdapter extends BaseAdapter {
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.WRAP_CONTENT, PER_ITEM_HEIGHT);

        @Override
        public int getCount() {
            return leftOrders.length;
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
            ViewHolder holder = ViewHolder.get(context, convertView, R.layout.table_left_item, position, null);
            holder.setText(R.id.text1, leftTimes[position]).setText(R.id.text2, leftOrders[position]);
            holder.getConvertView().setLayoutParams(params);
            ((LinearLayout) holder.getConvertView()).setGravity(Gravity.CENTER);
            return holder.getConvertView();
        }
    }


    public TableDayAdapter getAdapter() {
        return mAdapter;
    }

    public void refreshData(int i) {
        if (i !=-1) {
            currWeek = i;
//            refrectThePager(currWeek * 7);
            ((MainActivity) context).getActionBar2().setCurrWeek(currWeek);
            mIndicator.setCurrentItem(currWeek * 7 + CommonUtils.getCurrDayOffWeek());
        } else {
            updateTableData();
        }
    }

    private Dialog loadingDialog;
    private StatesUtils statesUtils;

    private void updateTableData() {
        if (NetworkChecker.IsNetworkAvailable(context)) {
            loadingDialog = CusDialog.create(context, "刷新课表中...");
            loadingDialog.show();
            logout();
        } else {
            CommonUtils.showToast(context, "网络不可用");
        }
    }

    public void logout() {
        new Thread(() -> {
            try {
                //突然发现直接重复登陆也可以获取数据，就注释掉了。。
//                String cookie = new HttpCookie("JSESSIONID", CommonUtils.md5(statesUtils.getUserName())).toString();
//                HttpURLConnection conn = (HttpURLConnection) new URL(com.chillax.config.URL.logoutUrl).openConnection();
//                conn.setRequestProperty("Cookie", cookie);
//                conn.setConnectTimeout(3000);
//                conn.setReadTimeout(3000);
//                InputStream is = conn.getInputStream();
//                is.close();
//                conn.disconnect();
                new CoursesDBDao(context).clear();
                new DetailDBDao(context).clear();
                new TableDataLoader(context, mHandler).execute(statesUtils.getUserName(), statesUtils.getUserPwd());
            } catch (Exception e) {
                e.printStackTrace();
                mHandler.obtainMessage(NET_ERROR).sendToTarget();
            }

        }).start();
    }

    /**
     * 其父类Fragment.java中没有实现该方法。
     * 这说明Android中只提供了Activity->Fragment的消息传递，没有提供Fragment->ChildFragment的消息传递，我们需要自己写代码实现
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TableItemFragment.CODE_FOR_RESULT:
                mAdapter.refreshNote(oldPos);
        }
    }

}
