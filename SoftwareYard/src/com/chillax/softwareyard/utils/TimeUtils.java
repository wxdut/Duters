package com.chillax.softwareyard.utils;

import android.text.TextUtils;

/**
 * Time处理类，可以处理学生周知时间的先后顺序，以便于排序
 * 可以处理的时间格式为：
 * eg：2015-07-26
 * Created by Chillax on 2015/7/26.
 */
public class TimeUtils {
    private static String[] eles1;
    private static String[] eles2;
    private static String timeStr1,timeStr2;

    /**
     * 该函数用来比较两个符合一定规则时间字符串的大小比较
     * @param time1 Time1
     * @param time2 Time2
     * @return 0：相等
     *         1：time1<time2
     *         -1: time1>time2
     * 注意这里的结果是反着的，为了能让News列表时间大到小排列
     */
    public static int compareTimes(String time1,String time2){
        timeStr1=time1.replaceAll("[^0-9-]", "");
        timeStr2=time2.replaceAll("[^0-9-]", "");
        if (!(isLegal(timeStr1)&&isLegal(timeStr2))) {
            throw new IllegalArgumentException("utils...TimeUtils...compareTimes()");
        }
        if (timeStr1.equals(timeStr2)){
            return 0;
        }
        eles1=timeStr1.split("-");
        eles2=timeStr2.split("-");
        return compare(Integer.valueOf(eles1[0].trim()), Integer.valueOf(eles2[0].trim()), 0);
    }
    private static int compare(int t1,int t2,int c){
        if(t1-t2==0){
            return compare(Integer.valueOf(eles1[c+1]),Integer.valueOf(eles2[c+1]),c+1);
        }else if(t1-t2>0){
            return -1;
        }else{
            return 1;
        }
    }
    private static boolean isLegal(String time){
        if(TextUtils.isEmpty(time)){
            return false;
        }
        String[] eles=time.split("-");
        if (eles.length!=3|eles[0].trim().length()==0|eles[1].trim().length()==0|eles[2].trim().length()==0) {
            return false;
        }
        return true;
    }
}
