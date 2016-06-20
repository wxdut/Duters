package com.chillax.softwareyard;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import com.chillax.config.Constant;
import com.chillax.config.Path;
import com.chillax.softwareyard.dao.DownDBDao;
import com.chillax.softwareyard.dao.StoreDBDao;
import com.chillax.softwareyard.model.Doc;
import com.chillax.softwareyard.model.News;
import com.chillax.softwareyard.utils.CacheUtils;
import com.chillax.softwareyard.utils.StatesUtils;
import com.lidroid.xutils.util.LogUtils;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.File;
import java.util.ArrayList;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;

/**
 * 程序入口，进行初始化工作
 */
public class App extends Application {
    /**
     * 这个List保存着首页中显示的学生周知的数据
     */
    public static ArrayList<News> newsList = new ArrayList<>();
    /**
     * 这个List保存着收藏中心中显示的学生周知的数据
     */
    public static ArrayList<News> storeList = new ArrayList<>();
    /**
     * 这个List保存着下载中心中显示的文件列表数据信息
     */
    public static ArrayList<Doc> docList = new ArrayList<>();
    /**
     * 每次更新加载显示的学生周知条数
     */
    public static int pageCount = 25;
    /**
     * 当前学院网上学生周知的总页数
     */
    public static int rowCount = 0;

    /**
     * 当前手机显示到了学生周知第几页的数据
     */
    public static int currPageIndex = 0;

    private DisplayImageOptions mOptions;
    private StoreDBDao mDao;
    private DownDBDao mDao2;

    @Override
    public void onCreate() {
        super.onCreate();
        initImageLoader();
        initDaos();
        initPath();
        checkVersion();
        LogUtils.allowD = true;
        LogUtils.allowE = true;
        //初始化Bmob推送设置：
        Bmob.initialize(this, Constant.appId);
        // 使用推送服务时的初始化操作
        BmobInstallation.getCurrentInstallation(this).save();
        // 启动推送服务
        BmobPush.startWork(this, Constant.appId);
    }

    private void checkVersion() {
        CacheUtils cacheUtils= new CacheUtils(this, CacheUtils.CacheType.FOR_VERSION_CACHE);
        PackageInfo info=null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
            if (info.versionCode > Integer.valueOf(cacheUtils.getCache("Version"))) {
                cacheUtils.setCache("Version",info.versionCode+"");
                new StatesUtils(this).setFirstUse(true);
            }
        } catch (Exception e) {
            new StatesUtils(this).setFirstUse(true);
            cacheUtils.setCache("Version",info.versionCode+"");
            e.printStackTrace();
        }
    }

    private void initPath() {
        try {
            File temp = new File(Path.imagesPath);
            if (!temp.exists()) {
                temp.mkdir();
            }
            temp = new File(Path.dbPath);
            if (!temp.exists()) {
                temp.mkdir();
            }
            temp = new File(Path.downloadPath);
            if (!temp.exists()) {
                temp.mkdir();
            }
            temp = new File(Path.userImage);
            if (!temp.getParentFile().exists()) {
                temp.getParentFile().mkdir();
            }
        } catch (Exception e) {

        }
    }

    private void initDaos() {
        mDao = new StoreDBDao(this);
        mDao.copyAllToList(storeList);
        mDao2 = new DownDBDao(this);
        mDao2.copyAllToList(docList);
    }

    private void initImageLoader() {
        mOptions = new DisplayImageOptions.Builder().showImageOnLoading(0) // 设置图片在下载期间显示的图片
                .showImageForEmptyUri(0)// 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(0) // 设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true)// 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true)// 设置下载的图片是否缓存在SD卡中
                .considerExifParams(true) // 是否考虑JPEG图像EXIF参数（旋转，翻转）
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)// 设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型//
                        // .delayBeforeLoading(int delayInMillis)//int
                        // delayInMillis为你设置的下载前的延迟时间
                        // 设置图片加入缓存前，对bitmap进行设置
                        // .preProcessor(BitmapProcessor preProcessor)
                .resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
                .displayer(new RoundedBitmapDisplayer(20))// 是否设置为圆角，弧度为多少
                .displayer(new FadeInBitmapDisplayer(100))// 是否图片加载好后渐入的动画时间
                .build();// 构建完成

        String cacheDir = Path.imagesPath;
        File cachePath = new File(cacheDir);// 获取到缓存的目录地址
        LogUtils.d("cacheDir:" + cacheDir);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this).discCache(new UnlimitedDiscCache(cachePath))
                // 自定义缓存路径
                .defaultDisplayImageOptions(mOptions)
                .imageDownloader(
                        new BaseImageDownloader(App.this, 5 * 1000, 30 * 1000))
                        // readTimeout(30)// 超时时间
                .writeDebugLogs() // Remove for release app
                .build();
        ImageLoader.getInstance().init(config);
    }
}
