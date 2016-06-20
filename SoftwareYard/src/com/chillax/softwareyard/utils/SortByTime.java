package com.chillax.softwareyard.utils;

import com.chillax.softwareyard.model.News;

import java.util.Comparator;

/**
 * 该类实现了对Time的排序
 * Created by Chillax on 2015/7/26.
 */
public class SortByTime implements Comparator {
    private static SortByTime instance=new SortByTime();
    public static synchronized SortByTime getInstance(){
        return instance;
    }
    public SortByTime(){}
    @Override
    public int compare(Object lhs, Object rhs) {
        return TimeUtils.compareTimes(((News)lhs).getTime(),((News)rhs).getTime());
    }
}
