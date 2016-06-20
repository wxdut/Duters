package com.chillax.softwareyard.utils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.widget.TextView;
import android.widget.Toast;

import com.chillax.config.Path;
import com.chillax.softwareyard.App;
import com.chillax.softwareyard.model.Doc;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangx_000 on 2015/8/26 0026.
 */
public class DownLoadService extends Service {
    private List<Doc> dataList = App.docList;
    private DecimalFormat df = new DecimalFormat("0.0");
    private List<String> sized = new ArrayList<>();
    private Map<String,String> downed=new HashMap<>();

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    private int putDownLoadTask(String url,String fileName) {
        if (!NetworkChecker.IsNetworkAvailable(this)) {
            return NET_ERROR;
        }else if(containName(fileName)){
            return DOWNLOAD_REPEAT;
        }
        downed.put(fileName, url);
        downloadFile(url,fileName);
        return DOWNLOAD_OK;
    }

    /**
     * 判断下载列表中是否还有该文件，防止重复下载
     */
    private boolean containName(String name) {
        if(downed.containsKey(name)){
            return true;
        }
        for (Doc doc : App.docList) {
            if (doc.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 下载文件
     *
     * @param fileUrl  文件下载地址
     * @param fileName 文件名(含后缀名)
     */
    private void downloadFile(final String fileUrl, final String fileName) {
        if (!NetworkChecker.IsNetworkAvailable(this)) {
            return;
        }
        RequestParams params = new RequestParams();
        params.addHeader("Referer", fileUrl);
//        HttpHandler httpHandler=new HttpUtils().download(HttpRequest.HttpMethod.GET, fileUrl, Path.downloadPath + "/" + fileName, params, false, false, new RequestCallBack<File>() {
//            Doc doc = null;
//
//            @Override
//            public void onLoading(long total, long current, boolean isUploading) {
//                super.onLoading(total, current, isUploading);
//                if (doc == null) {
//                    String size = df.format(total / 1024.0);
//                    doc = new Doc(fileName, size, (int)(100 * current / total/1024) + "", fileUrl, Path.downloadPath + "/" + fileName);
//                    dataList.add(doc);
//                }
//                doc.setProgress((100 * current / total) + "");
//            }
//
//            @Override
//            public void onSuccess(ResponseInfo<File> responseInfo) {
//                Intent intent=new Intent();
//                intent.putExtra("name",fileName);
//                intent.setAction("com.chillax.softwareyard.utils.DownLoadService");
//                sendBroadcast(intent);
//            }
//
//            @Override
//            public void onFailure(HttpException e, String s) {
//                Toast.makeText(DownLoadService.this, "下载失败，请检查网络后重试", Toast.LENGTH_SHORT).show();
//            }
//        });
    }
    private void getFileSize(final TextView tv, final String url, Handler handler, int position) {
        if (sized.contains(url)) {
            return;
        }
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                    conn.setRequestProperty("Referer", url);
                    String size = df.format(conn.getContentLength() / 1024.0) + " kb";
                    handler.obtainMessage(position, size).sendToTarget();
                } catch (Exception e) {
                }
                return null;
            }
        }.execute();
    }

    public static final int DOWNLOAD_OK = 0;
    public static final int DOWNLOAD_REPEAT = 1;
    public static final int NET_ERROR = 2;

    /**
     * Service对象的中间人。
     */
    public class MyBinder extends Binder {
        public int putDownLoadTask(String url, String fileName) {
            return DownLoadService.this.putDownLoadTask(url, fileName);
        }
        public void getFileSize(final TextView tv, final String url, Handler handler, int position) {
            DownLoadService.this.getFileSize(tv,url,handler,position);
        }
    }

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
}
