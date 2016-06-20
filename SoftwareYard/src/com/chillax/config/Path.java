package com.chillax.config;

import android.os.Environment;

/**
 * Created by Chillax on 2015/7/26.
 */
public class Path {
    private static String basePath=Environment.getExternalStorageDirectory()+"/SoftWareYard";
    public static String imagesPath= basePath+"/images";
    public static String dbPath=basePath+"/dbs";
    public static String downloadPath=basePath+"/docs";
    public static String userImage1=basePath+"/user/userImage1.isw";
    public static String userImage2=basePath+"/user/userImage2.isw";
    public static String userImage=basePath+"/user/userImage.isw";
}
