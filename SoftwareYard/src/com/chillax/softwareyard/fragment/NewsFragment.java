package com.chillax.softwareyard.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.chillax.config.URL;
import com.chillax.pulltorefrushlistview.PullToRefreshBase;
import com.chillax.pulltorefrushlistview.PullToRefreshBase.OnRefreshListener;
import com.chillax.pulltorefrushlistview.PullToRefreshListView;
import com.chillax.softwareyard.App;
import com.chillax.softwareyard.R;
import com.chillax.softwareyard.activity.MainActivity;
import com.chillax.softwareyard.activity.NewsDetail_;
import com.chillax.softwareyard.activity.WebViewShower_;
import com.chillax.softwareyard.adapter.NewsDataAdapter;
import com.chillax.softwareyard.customview.RollViewPager;
import com.chillax.softwareyard.model.News;
import com.chillax.softwareyard.network.NewsDataLoader;
import com.chillax.softwareyard.utils.CacheUtils;
import com.chillax.softwareyard.utils.CommonUtils;
import com.chillax.softwareyard.utils.CusIntentService;
import com.chillax.softwareyard.utils.ScreenUtil;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.yalantis.phoenix.PullToRefreshView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

@EFragment(R.layout.news_fragment)
public class NewsFragment extends BaseFragment implements
        OnRefreshListener<ListView>, OnItemClickListener {

    public static final int PullToRefrushOver = 0;
    public static final int LoadMoreOver = 1;
    public static final int NO_MORE_DATA = 2;
    public static final int ON_DATA_CHANGED = 3;
    @ViewById(R.id.ptrlv)
    PullToRefreshListView mPtrlv;
    @ViewById(R.id.pgb)
    ProgressBar mPgb;
    @ViewById(R.id.pull_to_refresh)
    PullToRefreshView mPullToRefreshView;
    private ListView mListView;
    private RollViewPager mTopPager;
    private ViewGroup mHeaderView;
    private ArrayList<View> dots;
    private LinearLayout dotsLayout;
    private BaseAdapter mAdapter;
    private CacheUtils mCacheUtils;
    private Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PullToRefrushOver:
                    mAdapter.notifyDataSetChanged();
                    mPtrlv.onPullDownRefreshComplete();
                    mPullToRefreshView.setRefreshing(false);
                    break;
                case LoadMoreOver:
                    mAdapter.notifyDataSetChanged();
                    mPtrlv.onPullUpRefreshComplete();
                    break;
                case NO_MORE_DATA:
                    mPtrlv.setHasMoreData(false);
                    Toast.makeText(context, "没有更多的数据~", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    public NewsFragment() {
        super();
    }

    @AfterViews
    void inits() {
        mPullToRefreshView.setOnRefreshListener(() -> mPtrlv.doPullRefreshing(true, 0));
        mPtrlv.setPullRefreshEnabled(false);
        mPtrlv.setPullLoadEnabled(true);
        mPtrlv.setScrollLoadEnabled(false);
        mPtrlv.setOnRefreshListener(this);
        mPullToRefreshView.setRefreshing(true);
        mListView = mPtrlv.getRefreshableView();
        mHeaderView = (RelativeLayout) LayoutInflater.from(context).inflate(
                R.layout.roll_viewpager, null);
        mListView.addHeaderView(mHeaderView);
        dotsLayout = (LinearLayout) mHeaderView.findViewById(R.id.dots_ll);
        initDot(4);
        mCacheUtils=new CacheUtils(context, CacheUtils.CacheType.FOR_VIEWPAGER);
        final ArrayList<String> urlList = new ArrayList<>();
        urlList.add(mCacheUtils.getCache("roll_0_2"));
        urlList.add(mCacheUtils.getCache("roll_1_2"));
        urlList.add(mCacheUtils.getCache("roll_2_2"));
        urlList.add(mCacheUtils.getCache("roll_3_2"));
        mTopPager = new RollViewPager(context, dots, R.drawable.dot_focus,
                R.drawable.dot_normal, (position) -> {

            Intent i = WebViewShower_.intent(context).get();
            i.putExtra("URL", mCacheUtils.getCache("roll_"+position+"_2"));
            startActivity(i);
            getActivity().overridePendingTransition(
                    R.anim.slide_in_right, R.anim.slide_clam);
        });
        final ArrayList<String> imgsList = new ArrayList<>();
        imgsList.add(mCacheUtils.getCache("roll_0_1"));
        imgsList.add(mCacheUtils.getCache("roll_1_1"));
        imgsList.add(mCacheUtils.getCache("roll_2_1"));
        imgsList.add(mCacheUtils.getCache("roll_3_1"));
        mTopPager.setUriList(imgsList);
        mTopPager.startRoll();
        LinearLayout layout = (LinearLayout) mHeaderView
                .findViewById(R.id.top_news_viewpager);
        layout.addView(mTopPager);
        mAdapter = new NewsDataAdapter(context);
        SwingBottomInAnimationAdapter mAdapter2 = new SwingBottomInAnimationAdapter(
                mAdapter);
        mAdapter2.setAbsListView(mListView);
        mListView.setAdapter(mAdapter2);
        mListView.setOnItemClickListener(this);
    }

    private void initDot(int size) {
        dots = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ScreenUtil.dp2px(context, 8), ScreenUtil.dp2px(context, 8));
            params.setMargins(ScreenUtil.dp2px(context, 5), 0, ScreenUtil.dp2px(context, 5), 0);
            View dot = new View(context);
            if (i == 0) {
                dot.setBackgroundResource(R.drawable.dot_focus);
            } else {
                dot.setBackgroundResource(R.drawable.dot_normal);
            }
            dot.setLayoutParams(params);
            dots.add(dot);
            dotsLayout.addView(dot);
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        synchronized (context) {
            new NewsDataLoader(context, mHandler).execute(true);
        }
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        synchronized (context) {
            new NewsDataLoader(context, mHandler).execute(false);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        News news = App.newsList.get(position - 1);
        Intent i = NewsDetail_.intent(getActivity()).get();
        i
                .putExtra("title", news.getTitle())
                .putExtra("address", URL.zhouzhiUrl3+news.getAddress());
        context.startActivity(i);
        ((MainActivity) context).overridePendingTransition(
                R.anim.slide_in_right, R.anim.slide_clam);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //固定时间：每个周一开始检查（有个问题：周一当天可能会多次更新，不过没什么太大的问题）
        if(CommonUtils.getCurrDayOffWeek()==0){
            updateTopImages();
        }
    }

    /**
     * 开启后台服务去更新顶部的图片
     * （查看校网上图片是否有更新，如果更新了，就更新App中储存的图片链接）
     */
    private void updateTopImages() {
        Intent intent=new Intent(context, CusIntentService.class);
        intent.putExtra("task", CusIntentService.FOR_NEWSFRAG);
        context.startService(intent);
    }
    @Override
    public void onStart() {
        super.onStart();
        mTopPager.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mTopPager.onStop();
        //本来要放在MainActivity中进行的操作，由于要在后台检测是否有新消息，需要改到此处进行缓存
        CacheUtils utils=new CacheUtils(context, CacheUtils.CacheType.FOR_NEWS);
        //设置缓存
        //满足两种情况，需要设置缓存：
        //1.首次使用时，无缓存，需要设置；
        //2.数据有更新时才进行更新缓存。
        if(utils.getCache(""+0)==null){
            if(App.newsList.size()>=25){
                utils.clear();
                for(int i=0;i<25;i++){
                    utils.setCache(i+"",App.newsList.get(i).toString());
                }
            }
        }else if(App.newsList.size()>0&&!App.newsList.get(0).getTitle().equals(utils.getCache(""+0).split("::")[0])){
            if(App.newsList.size()>=25){
                utils.clear();
                for(int i=0;i<25;i++){
                    utils.setCache(i+"",App.newsList.get(i).toString());
                }
            }
        }
    }

}
