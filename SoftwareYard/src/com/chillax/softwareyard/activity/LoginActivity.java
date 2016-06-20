package com.chillax.softwareyard.activity;

import android.app.Dialog;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.chillax.config.Constant;
import com.chillax.softwareyard.R;
import com.chillax.softwareyard.network.TableDataLoader;
import com.chillax.softwareyard.utils.CusDialog;
import com.chillax.softwareyard.utils.StatesUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.login_layout)
public class LoginActivity extends BaseActivity {
    private StatesUtils mUtils;
    public static final int LOGIN_SUCESS = 0;//登陆成功
    public static final int DATA_ERROR = 1;//用户名或者密码错误
    public static final int NET_ERROR = 2;//网络不通
    public static final int NET_ERROR_2 = 3;//网络正常，但是没有连接校园网
    private String userNameStr;
    private String userPwdStr;
    @ViewById(R.id.stuId)
    TextView userName;
    @ViewById(R.id.stuPwd)
    TextView userPwd;
    @ViewById
    TextView login;
    private Dialog dialog;

    @AfterViews
    void initViews() {
        mUtils = new StatesUtils(this);
        mUtils.setFirstUse(false);
        userNameStr = mUtils.getUserName();
        if (!TextUtils.isEmpty(userNameStr)) {
            userName.setText(userNameStr);
        }
        userPwdStr = mUtils.getUserPwd();
        if (!TextUtils.isEmpty(userPwdStr)) {
            userPwd.setText(userPwdStr);
        }
        initLoadingDialog();
    }

    private void initLoadingDialog() {
        dialog = CusDialog.create(this, "登陆中...");
    }

    private Handler mHandler = new Handler(Looper.myLooper()) {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOGIN_SUCESS:
//                    loginBmob();
                    dialog.cancel();
                    Toast.makeText(LoginActivity.this, "登陆成功~", Toast.LENGTH_SHORT).show();
                    mUtils.setLoginStates(true);
                    mUtils.setUserName(userNameStr);
                    mUtils.setUserPwd(userPwdStr);
                    MainActivity_.intent(LoginActivity.this).start();
                    LoginActivity.this.finish();
                    break;
                case DATA_ERROR:
                    Toast.makeText(LoginActivity.this, "用户名或密码错误~", Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                    break;
                case NET_ERROR:
                    Toast.makeText(LoginActivity.this, "网络不可用~", Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                    break;
                case NET_ERROR_2:
                    showToast("请确保正确连接到校园网");
                    dialog.cancel();
                    break;
            }
        }
    };

    @Click
    void login() {
        userNameStr = userName.getText().toString();
        userPwdStr = userPwd.getText().toString();
        if (TextUtils.isEmpty(userNameStr) || TextUtils.isEmpty(userPwdStr)) {
            Toast.makeText(this, "账号密码不可为空~", Toast.LENGTH_SHORT).show();
        } else if (!checkLegal(userNameStr, userPwdStr)) {
            Toast.makeText(this, "账号输入不合法~", Toast.LENGTH_SHORT).show();
        } else {
            mUtils.setUserName(userNameStr);
            mUtils.setUserPwd(userPwdStr);
            dialog.show();
            new TableDataLoader(this, mHandler).execute(userNameStr, userPwdStr);
        }
    }

    private boolean checkLegal(String userNameStr, String userPwdStr) {
        boolean b1 = userNameStr.length() == Constant.userNameLength ? true : false;
        boolean b2 = userNameStr.replaceAll("\\d", "").length() == 0 ? true : false;
        return b1 && b2;
    }
}
