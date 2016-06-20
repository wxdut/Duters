package com.chillax.softwareyard.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.chillax.config.Path;
import com.chillax.softwareyard.App;
import com.chillax.softwareyard.R;
import com.chillax.softwareyard.adapter.ViewHolder;
import com.chillax.softwareyard.customview.ActionBar;
import com.chillax.softwareyard.customview.ActionBar.onTopBarClickedListener;
import com.chillax.softwareyard.model.Doc;
import com.chillax.softwareyard.model.News;
import com.chillax.softwareyard.network.NewsImageGetter;
import com.chillax.softwareyard.utils.CommonUtils;
import com.chillax.softwareyard.utils.CusDialog;
import com.chillax.softwareyard.utils.DownLoadService;
import com.chillax.softwareyard.utils.NetworkChecker;
import com.chillax.softwareyard.utils.ScreenUtil;
import com.chillax.softwareyard.utils.StatesUtils;
import com.chillax.swipebacklayout.SwipeBackLayout;
import com.chillax.swipebacklayout.app.SwipeBackActivity;
import com.lidroid.xutils.util.LogUtils;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * 该类用来显示具体的一个学生周知的具体内容，并提供分享，收藏，下载等功能。
 * 本类接受且只接受三个参数：
 * String title, time, address。
 * 其中，time:周知的标题
 * address:必须是完整路径
 */
@EActivity(R.layout.news_detail)
public class NewsDetail extends SwipeBackActivity implements
        onTopBarClickedListener, AdapterView.OnItemClickListener {

    private int POPU_WIDTH = 0;
    private int POPU_HEIGHT = 0;
    @ViewById
    TextView content;
    @ViewById(R.id.topBar)
    ActionBar actionBar;
    @ViewById
    ScrollView scrollView;
    @ViewById(R.id.file_list)
    ListView fileList;
    @ViewById
    ProgressBar pgb;
    private SwipeBackLayout sbl;
    private FileAdapter adapter;
    private List<Doc> files;
    private String fileUrl;
    private PopupWindow popup;
    private ArrayList<News> storeList;
    private String title, address;
    private TextView[] tvs = new TextView[20];

    @AfterViews
    void initView() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            actionBar.setPadding(0, ScreenUtil.getStatusBarHeight(this), 0, 0);
            // create our manager instance after the content view is set
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            // enable status bar tint
            tintManager.setStatusBarTintEnabled(true);
            // enable navigation bar tint
            tintManager.setNavigationBarTintEnabled(true);
            // set a custom tint color for all system bars
            tintManager.setTintColor(getResources().getColor(R.color.actionbar_bg));
        }

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                tvs[msg.what].setText(msg.obj.toString());
                adapter.notifyDataSetChanged();
            }
        };
        title = getIntent().getStringExtra("title");
        if (!TextUtils.isEmpty(title)) {
            title = title.trim();
        }
        address = getIntent().getStringExtra("address").trim();
        LogUtils.d("正在打开:" + address);
        actionBar.initTopBar("NewsDetail");
        actionBar.setOnTopBarClickedListener(this);
        if (NetworkChecker.IsNetworkAvailable(this)) {
            new HtmlTask().execute();
            storeList = App.storeList;
            sbl = getSwipeBackLayout();
            sbl.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
            popuDate.add("收藏");
            popuDate.add("分享");
            popuDate.add("下载");
            ListView lv = new ListView(this);
            lv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            lv.setAdapter(new PopuListAdapter());
            setListViewSizeBasedOnChildren(lv);
            POPU_WIDTH = lv.getLayoutParams().width;
            POPU_HEIGHT = lv.getLayoutParams().height;
            popup = new PopupWindow(lv, POPU_WIDTH, POPU_HEIGHT);
            popup.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
            popup.setFocusable(true);
            popup.setOutsideTouchable(false);
            lv.setOnItemClickListener(this);
            files = new ArrayList();
            adapter = new FileAdapter();
            fileList.setAdapter(adapter);
        }
        Intent intent = new Intent(this, DownLoadService.class);
        startService(intent);
        myConn = new MyConn();
        bindService(intent, myConn, BIND_AUTO_CREATE);
    }

    private MyConn myConn;
    private DownLoadService.MyBinder myBinder;

    class MyConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder = (DownLoadService.MyBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            unbindService(myConn);
            myConn = null;
        }
    }

    @Override
    protected void onDestroy() {
        unbindService(myConn);
        myConn = null;
        myBinder = null;
        super.onDestroy();
    }

    private List<String> popuDate = new ArrayList<>();
    private int[] popuIds = new int[]{R.drawable.store, R.drawable.share, R.drawable.download};

    class PopuListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return popuDate.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = new ViewHolder(NewsDetail.this, R.layout.newdetail_popu, position, parent);
            holder.setText(R.id.text, popuDate.get(position)).setImageResource(R.id.image, popuIds[position]);
            return holder.getConvertView();
        }
    }

    private void setListViewSizeBasedOnChildren(ListView listView) {
        ListAdapter homeAdapter = listView.getAdapter();
        if (homeAdapter == null) {
            return;
        }
        int totalHeight = 0, totalWidth = 0;
        for (int i = 0, len = homeAdapter.getCount(); i < len; i++) {
            View listItem = homeAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalWidth = listItem.getMeasuredWidth();
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.width = totalWidth;
        params.height = totalHeight + (listView.getDividerHeight() * (homeAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    private void setListViewHeightBasedOnChildren(ListView lv) {
        ListAdapter homeAdapter = lv.getAdapter();
        if (homeAdapter == null) {
            return;
        }
        int totalHeigth = 0;
        for (int i = 0, len = homeAdapter.getCount(); i < len; i++) {
            View item = homeAdapter.getView(i, null, lv);
            item.measure(0, 0);
            totalHeigth += item.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = lv.getLayoutParams();
        params.height = totalHeigth + (lv.getDividerHeight() * (homeAdapter.getCount() - 1));
        lv.setLayoutParams(params);
    }

    class FileAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return files.size();
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
            final String[] name = files.get(position).getName().split("\\.");
            fileUrl = files.get(position).getUrl();
            final ViewHolder holder;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.file_list_item, null);
                holder = new ViewHolder();
                holder.iv = (ImageView) convertView.findViewById(R.id.file_image);
                holder.tv_name = (TextView) convertView.findViewById(R.id.file_name);
                holder.tv_size = (TextView) convertView.findViewById(R.id.file_size);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            tvs[position] = holder.tv_size;
            myBinder.getFileSize(holder.tv_size, fileUrl, handler, position);
            setImageResource(holder.iv, name[1]);
            holder.tv_name.setText(name[0]);
            convertView.setOnClickListener((View v) -> {
                showDialog1(position);
            });
            return convertView;
        }

        class ViewHolder {
            ImageView iv;
            TextView tv_name;
            TextView tv_size;
        }
    }

    private Dialog dialog;
    private Dialog dialog2;

    private void showDialog2() {
        dialog2 = CusDialog.create(this, "下载中，请稍候...");
        dialog2.show();
    }

    private void openFile(String fileName) {
        try {
            File file = new File(Path.downloadPath + "/" + fileName);
            String type = CommonUtils.suff2Type(fileName.split("\\.")[1]);
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), type);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "未找到打开此文件的应用", Toast.LENGTH_SHORT).show();
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            dialog2.dismiss();
            openFile(intent.getStringExtra("name"));
        }
    };

    private void showDialog1(int position) {
        dialog = CusDialog.create(this, pos -> {
            dialog.dismiss();
            switch (pos) {
                case 0:
                    switch (myBinder.putDownLoadTask(fileUrl, files.get(position).getName())) {
                        case DownLoadService.DOWNLOAD_OK:
                            showDialog2();
                            IntentFilter filter = new IntentFilter("com.chillax.softwareyard.utils.DownLoadService");
                            registerReceiver(receiver, filter);
                            break;
                        case DownLoadService.DOWNLOAD_REPEAT:
                            openFile(files.get(position).getName());
                            break;
                        case DownLoadService.NET_ERROR:
                            Toast.makeText(NewsDetail.this, "网络错误,请检查网络", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;
                case 1:
                    switch (myBinder.putDownLoadTask(fileUrl, files.get(position).getName())) {
                        case DownLoadService.DOWNLOAD_OK:
                            Toast.makeText(NewsDetail.this, "下载中,请到下载中心中查看", Toast.LENGTH_SHORT).show();
                            break;
                        case DownLoadService.DOWNLOAD_REPEAT:
                            Toast.makeText(NewsDetail.this, "已下载过,不能重复下载", Toast.LENGTH_SHORT).show();
                            break;
                        case DownLoadService.NET_ERROR:
                            Toast.makeText(NewsDetail.this, "网络错误,请检查网络", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;
            }
            dialog.dismiss();
        }, new String[]{"下载并打开", "仅下载", "取消"});
    }

    private Handler handler;

    private void setImageResource(ImageView iv, String suf) {
        switch (suf) {
            case "docx":
                iv.setImageResource(R.drawable.file_word);
                break;
            case "xlsx":
                iv.setImageResource(R.drawable.file_excel);
                break;
            case "pptx":
                iv.setImageResource(R.drawable.file_ppt);
                break;
            default:
                iv.setImageResource(R.drawable.file_word);
                break;
        }
    }

    class HtmlTask extends AsyncTask<String, Void, String> {

        private Pattern pattern;
        private Matcher matcher;
        private URL url;

        @Override
        protected void onPreExecute() {
            try {
                pattern = Pattern.compile("\\d{4}.*:\\d{2}");
                url = new URL(address);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String htmlStr = null;
            try {
                Document docs = Jsoup.parse(url, 3000);
                Element doc = docs.getElementsByClass("lh_15per").first();
                Elements temp = doc.getElementsByClass("mb_5");
                if (temp.size() != 0) {
                    matcher = pattern.matcher(temp.first().text());
                    if (matcher.find()) {
                        doc.getElementsByClass("mb_5").first()
                                .text(matcher.group());
                    }
                }
                temp = doc.getElementsByClass("f13");
                if (temp.size() == 2) {
                    matcher = pattern.matcher(temp.get(1).text());
                    if (matcher.find()) {
                        doc.getElementsByClass("f13").get(1)
                                .text(matcher.group());
                    }
                }
                int num = doc.getElementsByTag("p").size();
                doc.getElementsByTag("p").get(num - 1).text("");
                doc.getElementsByTag("p").get(num - 2).text("");
                htmlStr = doc.html();
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.e("获取学生周知详情数据失败");
            }
            return htmlStr;
        }

        @Override
        protected void onPostExecute(String htmlStr) {
            if (TextUtils.isEmpty(htmlStr)) return;
            Document doc = Jsoup.parse(htmlStr);
            if (doc.getElementsByTag("ul").size() != 0) {
                Elements lis = doc.getElementsByTag("ul").first()
                        .getElementsByTag("li");
                for (Element li : lis) {
                    Element a = li.getElementsByTag("a").first();
                    String url = a.attr("href");
                    fileUrl = (com.chillax.config.URL.zhouzhiUrl4
                            + url.substring(6, url.length()));
                    files.add(new Doc(a.text(), null, null, fileUrl, null));
                }
                adapter.notifyDataSetChanged();
                doc.getElementsByTag("ul").first().text("");
            }
            //去除注释，注：?s：点号通配模式，防止有换行符
            content.setText(Html.fromHtml(doc.html().replaceAll("(?s:<!--.*?-->)", ""), new NewsImageGetter(
                    NewsDetail.this, content), null));
            adapter.notifyDataSetChanged();
            setListViewHeightBasedOnChildren(fileList);
            pgb.setVisibility(View.GONE);
        }

    }

    private void showShare() {
        String url = address;
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.share));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(url);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(title);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        oks.setImageUrl("http://file.bmob.cn/M02/B8/3E/oYYBAFYBDpqAaSPLAAEmJPhNNZA006.png");
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(url);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(url);

// 启动分享GUI
        oks.show(this);
    }

    @Override
    public void onLogoClicked() {
        onBackPressed();
    }

    @Override
    public void onTitleClicked() {

    }

    @Override
    public void onMoreClicked(View view) {
        if (popup.isShowing()) {
            popup.dismiss();
        } else {
            popup.showAsDropDown(actionBar, actionBar.getWidth() - POPU_WIDTH - ScreenUtil.dp2px(this, 5), 0);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_clam, R.anim.slide_out_right);
    }

    @Override
    public void finish() {
        try {
            unregisterReceiver(receiver);
        } catch (Exception e) {

        }
        if (!new StatesUtils(this).isAppOpened()) {
            Welcome_.intent(this).start();
            super.finish();
        }
        super.finish();

    }

    @Override
    public void onSpinnerItemClicked(int position) {

    }

    /**
     * 重写父类方法，接收“未找到相应应用”的异常，并给予用户友好的提示。
     *
     * @param intent
     */
    @Override
    public void startActivity(Intent intent) {
        try {
            super.startActivity(intent);
        } catch (Exception e) {
            String action = intent.getAction();
            if (action.contains("mailto")) {
                Toast.makeText(this, "未安装可以打开邮件的应用", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "未安装相应的应用", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        onMoreClicked(null);
        switch (i) {
            case 0:
                News news = new News(title, new SimpleDateFormat("yyyy-MM-dd").format(new Date()), address.replaceAll(com.chillax.config.URL.zhouzhiUrl3, ""));
                if (TextUtils.isEmpty(title)) {
                    Toast.makeText(NewsDetail.this, "此项不支持收藏~", Toast.LENGTH_SHORT).show();
                } else if (storeList.contains(news)) {
                    Toast.makeText(NewsDetail.this, "已收藏过~", Toast.LENGTH_SHORT).show();
                } else {
                    storeList.add(news);
                    Toast.makeText(NewsDetail.this, "收藏成功~", Toast.LENGTH_SHORT).show();
                }
                break;
            case 1:
                showShare();
                break;
            case 2:
                DownLoadCenter_.intent(NewsDetail.this).start();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_clam);
                break;
            default:
                break;
        }
    }

}
