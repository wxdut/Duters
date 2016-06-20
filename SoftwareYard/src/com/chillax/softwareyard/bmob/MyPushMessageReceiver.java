package com.chillax.softwareyard.bmob;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.chillax.config.URL;
import com.chillax.softwareyard.R;
import com.chillax.softwareyard.activity.NewsDetail_;
import com.chillax.softwareyard.utils.CusIntentService;
import com.chillax.softwareyard.utils.StatesUtils;
import com.lidroid.xutils.util.LogUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Xiao on 2015/9/16.
 */
public class MyPushMessageReceiver extends BroadcastReceiver {
    private Context context;
    private Intent i;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        if (intent.getAction().equals("cn.bmob.push.action.MESSAGE")) {
            String msg = intent.getStringExtra("msg");
            //先确定是否是云端自动调用的周数变更推送：
            if(msg.matches("\\{\"alert\":\"\\d*?\"\\}")){
                //此推送每隔一个小时进行一次，该推送有两个目的：
                //1.设置当前周数。（这里需要减一，应用中全部是从零开始计算的）
                new StatesUtils(context).setCurrWeekOfTerm(Integer.valueOf(msg.replaceAll("[^\\d]","").trim())-1);
                LogUtils.e("收到来自服务器的推送消息，推送目的：更改当前周数："+Integer.valueOf(msg.replaceAll("[^\\d]","").trim()));
                //2.后台检查学生周知列表的更新，如果有更新，则提示用户更新。
                Intent i =new Intent(context, CusIntentService.class);
                i.putExtra("task",CusIntentService.FOR_PUSH_TO_LOAD);
                context.startService(i);
                return;
            }
            //接收到来自Bmob的推送消息之后，进行相应的处理：
            //消息接收格式为：“标题  网址”//这里的address是完整的路径名
            Pattern pattern = Pattern.compile("alert\":\"(.*?)\"[}]$");
            Matcher matcher = pattern.matcher(msg);
            try {
                matcher.find();
                String[] data = matcher.group(1).split("\\\\r\\\\n");
                String title = data[0];
                String address = data[1].replaceAll("\\\\", "");
                if (!(TextUtils.isEmpty(title) | TextUtils.isEmpty(address))) {
                    if (address.contains(URL.zhouzhiUrl4)) {
                        i = NewsDetail_.intent(context).get();
                        i.putExtra("title", title);
                        i.putExtra("address", address);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        sendNotification(title);
                    }
                }
            } catch (Exception e) {
                LogUtils.e("Bmob消息推送数据格式有误");
                e.printStackTrace();
            }
            LogUtils.e("Received a msg from Bmob:" + msg);
        }
    }

    private void sendNotification(String content) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String tickerText = "i 软院新消息";
        Intent intentToLaunch = i;
        int requestCode = (int) SystemClock.uptimeMillis();
        PendingIntent contentIntent = PendingIntent.getActivity(context, requestCode,
                intentToLaunch, PendingIntent.FLAG_UPDATE_CURRENT);

        int smallIcon = R.drawable.ic_launcher;

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context)
                .setSmallIcon(smallIcon)
                .setContentTitle(tickerText).setTicker(tickerText)
                .setContentText(content).setAutoCancel(true)
                .setContentIntent(contentIntent);
        notificationManager.notify(0, mBuilder.build());
    }
}