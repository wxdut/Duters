package com.chillax.softwareyard.bmob;

import cn.bmob.v3.BmobUser;

/**
 * Created by Xiao on 2015/9/17.
 */
public class MyUser extends BmobUser{
    /**
     * 用户的小头像
     */
    private String userImage1;
    /**
     * 用户的大头像
     */
    private String userimage2;

    public String getUserImage1() {
        return userImage1;
    }

    public void setUserImage1(String userImage1) {
        this.userImage1 = userImage1;
    }

    public String getUserimage2() {
        return userimage2;
    }

    public void setUserimage2(String userimage2) {
        this.userimage2 = userimage2;
    }
}
