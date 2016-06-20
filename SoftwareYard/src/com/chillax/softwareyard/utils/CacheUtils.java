package com.chillax.softwareyard.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class CacheUtils {
    private SharedPreferences preferences;

    public CacheUtils(Context context, CacheType type) {
        switch (type) {
            case FOR_VIEWPAGER:
                preferences = context.getSharedPreferences("VP_CACHE", 0);
                break;
            case FOR_NEWS:
                preferences=context.getSharedPreferences("NEWS_CACHE",0);
                break;
            case FOR_EXAM_RESULT:
                preferences=context.getSharedPreferences("EXAM_RESULT",0);
                break;
            case FOR_EXAM_SCHEDULE:
                preferences=context.getSharedPreferences("FOR_EXAM_SCHEDULE",0);
                break;
            case FOR_NOTE_CACHE:
                preferences=context.getSharedPreferences("FOR_NOTE_CACHE",0);
                break;
            case FOR_VERSION_CACHE:
                preferences=context.getSharedPreferences("FOR_VERSION_CACHE",0);
                break;
            case FOR_EXAM_RESULT_ALL:
                preferences=context.getSharedPreferences("FOR_EXAM_RESULT_ALL",0);
        }
    }

    public void clear() {
        preferences.edit().clear().commit();
    }

    /**
     * 设置枚举类型：
     * FOR_VIEWPAGER：
     * 设置首页轮播图的图片链接以及点进去之后的链接。
     * 设置首页轮播图的图片链接以及点进去之后的链接。
     * key：roll_第几张图片(0-3)_图片地址或者点击之后的链接（0或1）
     * eg:第一张轮播图的图片链接为：roll_0_1;
     * 第一张轮播图点击之后的链接：roll_0_2;
     * value：urlStr,链接地址.
     *
     * FOR_NEWS:学生周知缓存
     * key:常量，固定为newscache。range:0~24
     * value:首页加载的HTML数据，未完全解析的数据。
     *
     * FOR_EXAM_RESULT:成绩数据缓存
     * key:
     *
     * FOR_EXAM_RESULT_ALL:全部考试成绩缓存
     * key:
     *
     * FOR_EXAM_SCHEDULE:考试安排数据缓存
     * key:
     *
     * FOR_NOTE_CACHE:课程备注设置的缓存：
     * v1.3之后，课表采用循环显示的方法，简化了备注信息的保存于还原。
     * key：一个int值，a_b  a的范围：0~20*7-1；b的范围：0-4；
     * value:定位到该时间点的缓存内容。
     *
     * FOR_VERSION_CACHE:版本更新标志
     * key：constant "Version"
     * value:
     */
    public enum CacheType {
        FOR_VIEWPAGER, FOR_NEWS,FOR_EXAM_RESULT,FOR_EXAM_RESULT_ALL,FOR_EXAM_SCHEDULE,FOR_NOTE_CACHE,FOR_VERSION_CACHE
    }
    public void setCache(String key, String value) {
        preferences.edit().putString(key, value).commit();
    }

    public String getCache(String key) {
        return preferences.getString(key, null);
    }
}

