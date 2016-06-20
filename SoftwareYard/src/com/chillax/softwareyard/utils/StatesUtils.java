package com.chillax.softwareyard.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class StatesUtils {
    private final String pfName = "Login";
    /**
     * 当前登陆状态
     */
    private final String pfName2 = "login";
    /**
     * 用户名（只有在登陆成功以后才会不为空）
     */
    private final String userName = "userName";
    /**
     * 密码（只有在登陆成功以后才会不为空）
     */
    private final String userPwd = "userPwd";
    /**
     * 是否是第一次登陆（如果是第一次登陆，就应该展示引导页）
     */
    private final String first="first";
    /**
     * 表示App是否已经处于打开状态(打开推送消息之后，需要用该值判断打开哪个页面)
     */
    private final String isOpening="isAppOpening";
    /**
     * 表示当前是第几周（如果该值小于等于0，那么表示当前为假期状态）
     */
    private final String currWeek="currWeekOfTerm";

    private SharedPreferences preferences = null;
    private Context context;

    public StatesUtils(Context context) {
        super();
        this.context = context;
        preferences = context.getSharedPreferences(pfName,
                Activity.MODE_PRIVATE);
    }

    public boolean isLogin() {
        return preferences.getBoolean(pfName2, false);
    }

    public void setLoginStates(boolean login) {
        Editor editor = preferences.edit();
        editor.putBoolean(pfName2, login);
        editor.commit();
    }
    public boolean isAppOpened(){
        if(preferences.getBoolean(isOpening,false)){
            return true;
        }else {
            return false;
        }
    }
    public void setAppStates(boolean open){
        preferences.edit().putBoolean(isOpening,open).commit();
    }
    public boolean firstUse() {
        if(preferences.getBoolean(first, true)){
            return true;
        }else {
            return false;
        }
    }
    public void setFirstUse(boolean b) {
            preferences.edit().putBoolean(first, b).commit();
    }

    public String getUserName() {
        return preferences.getString(userName, "");
    }

    public String getUserPwd() {
        return preferences.getString(userPwd, "");
    }

    public void setUserName(String name) {
        preferences.edit().putString(userName, name).commit();
    }

    public void setUserPwd(String pwd) {
        preferences.edit().putString(userPwd, pwd).commit();
    }
    public void setCurrWeekOfTerm(int curr){
        preferences.edit().putInt(currWeek, curr).commit();
    }
    public int getCurrWeekOfTerm(){
       return preferences.getInt(currWeek,7);
    }

}
