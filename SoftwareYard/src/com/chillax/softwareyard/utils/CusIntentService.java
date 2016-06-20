package com.chillax.softwareyard.utils;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.chillax.softwareyard.R;
import com.chillax.softwareyard.activity.NewsDetail_;
import com.chillax.softwareyard.model.News;
import com.lidroid.xutils.util.LogUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Xiao on 2015/9/8.
 */
public class CusIntentService extends IntentService {
    private static final int ERROR_TASK = 0;
    public static final int FOR_NEWSFRAG = 1;
    public static final int FOR_USER_IMAGE_UPLOAD = 2;
    public static final int FOR_PUSH_TO_LOAD = 3;

    public CusIntentService() {
        super("com.chillax.softwareyard.utils.CusIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        switch (intent.getIntExtra("task", ERROR_TASK)) {
            case FOR_NEWSFRAG:
                //更新NewFragment顶部的ViewPager
                startTask(FOR_NEWSFRAG);
                break;
            case FOR_PUSH_TO_LOAD:
                startTask(FOR_PUSH_TO_LOAD);
                break;
            case FOR_USER_IMAGE_UPLOAD:
//                //上传用户头像
//                String[] user_images = new String[]{Path.userImage1, Path.userImage2};
//                BmobProFile.getInstance(this).uploadBatch(user_images, new UploadBatchListener() {
//                    @Override
//                    public void onSuccess(boolean isFinish, String[] fileNames, String[] urls, BmobFile[] files) {
//                        // isFinish ：批量上传是否完成
//                        // fileNames：文件名数组
//                        // urls        : url：文件地址数组
//                        // files     : BmobFile文件数组，`V3.4.1版本`开始提供，用于兼容新旧文件服务。
//                        LogUtils.e("用户头像上传成功~~");
//                        updateUserInfo();
//                        if (LogUtils.allowE) {
//                            for (int i = 0; i < fileNames.length; i++) {
//                                if (fileNames[i] != null && files[i] != null) {
//                                    LogUtils.e("文件名：" + fileNames[i]);
//                                    LogUtils.e("文件访问地址：" + files[i].getUrl());
//                                }
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onProgress(int i, int i1, int i2, int i3) {
//                        // curIndex    :表示当前第几个文件正在上传
//                        // curPercent  :表示当前上传文件的进度值（百分比）
//                        // total       :表示总的上传文件数
//                        // totalPercent:表示总的上传进度（百分比）
//                    }
//
//                    @Override
//                    public void onError(int i, String s) {
//
//                    }
//                });
                break;
        }
    }
//    private String imageUrl1;
//    private String imageUrl2;
//
//    private void updateUserInfo() {
//        MyUser currUser=BmobUser.getCurrentUser(this,MyUser.class);
//        if(currUser==null){
//            return;
//        }
//        currUser.setUserImage1(imageUrl1);
//        currUser.setUserimage2(imageUrl2);
//        new BmobUser().update(this, currUser.getObjectId(), new UpdateListener() {
//            @Override
//            public void onSuccess() {
//                LogUtils.e("用户头像信息同步成功...");
//            }
//
//            @Override
//            public void onFailure(int code, String msg) {
//                LogUtils.e("用户头像信息同步失败...");
//                CommonUtils.showToast(CusIntentService.this, "头像同步失败，请稍后重试");
//            }
//        });
//    }

    private void startTask(int task) {
        switch (task) {
            case FOR_NEWSFRAG:
                try {
                    HttpURLConnection conn = (HttpURLConnection) new URL(com.chillax.config.URL.schoolURl).openConnection();
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        StringBuffer result = new StringBuffer();
                        String line;
                        while ((line = br.readLine()) != null) {
                            result.append(line);
                        }
                        Pattern pattern = Pattern.compile("<table.*?</table>");
                        Matcher matcher = pattern.matcher(result.toString());
                        List<String> list = new ArrayList<>();
                        String table = null;
                        while (matcher.find()) {
                            table = matcher.group();
                        }
                        if (table != null) {
                            pattern = Pattern.compile("href=\"([^\"]*).*?src=\"([^\"]*)");
                            matcher = pattern.matcher(table);
                            while (matcher.find()) {
                                list.add(matcher.group(1));
                                list.add(matcher.group(2));
                            }
                            //如果list.size()>=4 说明获取成功
                            if (list.size() >= 4) {
                                CacheUtils cacheUtils = new CacheUtils(this, CacheUtils.CacheType.FOR_VIEWPAGER);
                                cacheUtils.setCache("roll_0_2", list.get(0).trim());
                                cacheUtils.setCache("roll_1_2", list.get(2).trim());
                                cacheUtils.setCache("roll_0_1", com.chillax.config.URL.zhouzhiUrl4 + list.get(1).trim());
                                cacheUtils.setCache("roll_1_1", com.chillax.config.URL.zhouzhiUrl4 + list.get(3).trim());
                            }
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case FOR_PUSH_TO_LOAD:
                //有个细节：也许App处于前台显示状态，此时缓存还未设置，但是用户确实已经刷新出新消息，这时不能进行提醒操作。
                if(CommonUtils.isApplicationShowing(this,getPackageName()))return;
                //用户处于后台时，我们进行检查新数据操作
                refreshData();
                break;
        }
    }

    private List<News> newsList = new ArrayList<>();
    private CacheUtils cacheUtils;

    private void refreshData() {

        if (NetworkChecker.IsNetworkAvailable(this)) {
            try {
                newsList.clear();
                URL url = new URL(com.chillax.config.URL.zhouzhiUrl);
                LogUtils.d(url.toString());
                Document doc = Jsoup.parse(url, 3000);
                Elements eles = doc.getElementsByTag("UL");
                Element ele = eles.get(eles.size() - 1);
                for (Element temp : ele.getElementsByTag("li")) {
                    Element e1 = temp.getElementsByTag("a").first();
                    Element e2 = temp.getElementsByTag("span").first();
                    String title = e1.attr("title").trim();
                    String time = e2.text().trim();
                    String address = e1.attr("href")
                            .substring(8, e1.attr("href").length()).trim();
                    News news = new News(title, time, address);
                    newsList.add(news);
                }
                Collections.sort(newsList, new SortByTime());
                if (cacheUtils == null) {
                    cacheUtils = new CacheUtils(this, CacheUtils.CacheType.FOR_NEWS);
                }
                String cacheFirstTitle=cacheUtils.getCache(0+"").split("::")[0];
                int curr=0;
                for (News item:newsList){
                    //如果缓存中不包含这条消息，那么则进行新消息提醒。

                    if(!item.getTitle().equals(cacheFirstTitle)){
                        //这里就应该去提醒用户有新的学生周知了
                        sendNotification(curr++,item);
                    }else {
                        break;
                    }
                }
                //如果提示了更新的消息，那么需要更新缓存，防止重复提醒
                if(!newsList.get(0).getTitle().equals(cacheFirstTitle)&&newsList.size()==25){
                    cacheUtils.clear();
                    for (int i=0;i<25;i++){
                        cacheUtils.setCache("i",newsList.get(i).toString());
                    }
                }
            } catch (Exception e) {
//				Toast.makeText(context, "学生周知数据扒取失败", 0).show();
                Log.e("class CusIntentService:", "学生周知列表数据扒取失败");
            }
        }
    }
    private void sendNotification(int curr,News news) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String tickerText = "i软院 学生周知";
        Intent intentToLaunch = new Intent(this, NewsDetail_.class);//将CustomActivity替换成自定义Activity类名
        intentToLaunch.putExtra("title",news.getTitle())
                .putExtra("time",news.getTime())
                .putExtra("address", com.chillax.config.URL.zhouzhiUrl3+news.getAddress());
        intentToLaunch.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        int requestCode = (int) SystemClock.uptimeMillis();
        PendingIntent contentIntent = PendingIntent.getActivity(this, requestCode,
                intentToLaunch, PendingIntent.FLAG_UPDATE_CURRENT);
        int smallIcon = R.drawable.ic_launcher;

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this)
                .setSmallIcon(smallIcon)
                .setContentTitle(tickerText).setTicker(tickerText)
                .setContentText(news.getTitle()).setAutoCancel(true)
                .setContentIntent(contentIntent)
                .setDefaults(Notification.DEFAULT_ALL);
        notificationManager.notify(curr, mBuilder.build());
    }

}
