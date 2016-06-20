package com.chillax.softwareyard.activity;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.chillax.config.URL;
import com.chillax.softwareyard.App;
import com.chillax.softwareyard.R;
import com.chillax.softwareyard.model.News;
import com.chillax.softwareyard.utils.CacheUtils;
import com.chillax.softwareyard.utils.StatesUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

@EActivity(R.layout.welcome_layout)
public class Welcome extends BaseActivity {
    private Handler handler = new Handler(Looper.myLooper()) {
        public void handleMessage(Message msg) {
            StatesUtils utils = new StatesUtils(Welcome.this);
            initRollImagesData();
            if (utils.firstUse()) {
                FirstLeader_.intent(Welcome.this).start();
            } else if (utils.isLogin()) {
                MainActivity_.intent(Welcome.this).start();
            } else {
                LoginActivity_.intent(Welcome.this).start();
            }
            overridePendingTransition(R.anim.slide_in_right,
                    R.anim.slide_out_left);
            Welcome.this.finish();
        }
    };

    private void initRollImagesData() {
        CacheUtils utils = new CacheUtils(this, CacheUtils.CacheType.FOR_VIEWPAGER);
        if (utils.getCache("roll_0_1") == null) {
            utils.setCache("roll_0_1", URL.first1);
            utils.setCache("roll_0_2", URL.first2);
            utils.setCache("roll_1_1", URL.second1);
            utils.setCache("roll_1_2", URL.second2);
            utils.setCache("roll_2_1", URL.Club1);
            utils.setCache("roll_2_2", URL.Club2);
            utils.setCache("roll_3_1", URL.Company1);
            utils.setCache("roll_3_2", URL.Company2);
        }
    }

    @AfterViews
    public void initViews() {
        new Thread(() -> {

            try {
                long preTime = System.currentTimeMillis();
                initDatasInWelcome();
                if (System.currentTimeMillis() - preTime < 2000) {
                    Thread.sleep(2000 + preTime
                            - System.currentTimeMillis());
                }
                handler.obtainMessage().sendToTarget();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }).start();
    }

    /**
     * Welcome界面的两三秒时间，可以用来预加载一些缓存数据，上面那个开线程获取当前周数，需要替换掉。
     */
    public void initDatasInWelcome() {
        //1.首先第一步，从SharePreference中获取上次缓存的列表数据
        CacheUtils utils = new CacheUtils(this, CacheUtils.CacheType.FOR_NEWS);
        String cache;
        String[] news;
        for (int i = 0; i < 25; i++) {
            cache = utils.getCache(i + "");
            if (cache != null) {
                news = cache.split("::");
                App.newsList.add(new News(news[0], news[1], news[2]));
            }
        }
    }

}
