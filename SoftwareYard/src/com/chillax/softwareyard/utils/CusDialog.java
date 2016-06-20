package com.chillax.softwareyard.utils;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.chillax.softwareyard.R;

import java.lang.reflect.Type;

/**
 * Created by Chillax on 2015/8/17.
 */
public abstract class CusDialog{
    /**
     * LoginDialog
     */
    public static Dialog create(Context context,String msg){
        View view=LayoutInflater.from(context).inflate(R.layout.login_dialog_layout,null);
        ((TextView)view.findViewById(R.id.msgTv)).setText(TextUtils.isEmpty(msg)?"":msg);
        Dialog dialog=new Dialog(context,R.style.cus_dialog_style);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    /**
     * 构造入口函数
     */
    public  Dialog builder(Context context,int layoutId,DialogType type){
        if(context==null|layoutId==0|type==null){
            throw new NullPointerException("CusDialog create failed.......");
        }
        View view=LayoutInflater.from(context).inflate(layoutId,null);
        Dialog dialog=null;
        switch (type){
            case DIALOG_NO_BG:
                dialog=new Dialog(context,R.style.cus_dialog_style);
                break;
            case DIALOG_WHITE_RECT_BG:
                dialog=new Dialog(context,R.style.cus_dialog_style2);
                break;
        }
        initDialog(view);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }
    /**
     * ChooseDialog
     * 当用户头像被点击之后出现
     */
    public static Dialog create(Context context,final onItemChoosedListener listener,String...params){
        View view = LayoutInflater.from(context).inflate(R.layout.photo_choose_dialog, null);
        final Button b1=(Button)view.findViewById(R.id.item1);
        final Button b2=(Button)view.findViewById(R.id.item2);
        final Button b3=(Button)view.findViewById(R.id.item3);
        if(params.length==3){
            b1.setText(params[0]);
            b2.setText(params[1]);
            b3.setText(params[2]);
        }
        View.OnClickListener lis=(View v)-> {
            if (listener != null) {
                if (v.getId() == b1.getId()) {
                    listener.onItemChoosed(0);
                } else if (v.getId() == b2.getId()) {
                    listener.onItemChoosed(1);
                } else {
                    listener.onItemChoosed(2);
                }
            }
        };
        b1.setOnClickListener(lis);
        b2.setOnClickListener(lis);
        b3.setOnClickListener(lis);
        Dialog dialog = new Dialog(context, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        // 设置显示动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = context.getResources().getDisplayMetrics().heightPixels;
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        return dialog;
    }
    /**
     * ChooseDialog
     * 当下载的文件被点击之后出现
     */
    public static Dialog create2(Context context, final onItemChoosedListener listener,String...params){
        View view = LayoutInflater.from(context).inflate(R.layout.down_file_click_dialog, null);
        final Button b1=(Button)view.findViewById(R.id.item1);
        final Button b2=(Button)view.findViewById(R.id.item2);
        final Button b3=(Button)view.findViewById(R.id.item3);
        View.OnClickListener lis=(View v)-> {
            if (listener != null) {
                if (v.getId() == b1.getId()) {
                    listener.onItemChoosed(0);
                } else if (v.getId() == b2.getId()) {
                    listener.onItemChoosed(1);
                } else {
                    listener.onItemChoosed(2);
                }
            }
        };
        b1.setOnClickListener(lis);
        b2.setOnClickListener(lis);
        b3.setOnClickListener(lis);
        Dialog dialog = new Dialog(context, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ScreenUtil.dp2px(context,200),
                ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        // 设置显示动画
//        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }
    public interface onItemChoosedListener{
        public void onItemChoosed(int position);
    }

    /**
     * 枚举类型：
     * DIALOG_NO_BG：灰色背景。
     * DIALOG_WHITE_RECT_BG：有背景，背景为一个白色的矩形
     */
    public enum DialogType{
        DIALOG_NO_BG,DIALOG_WHITE_RECT_BG
    }
    public abstract  void initDialog(View view);
}
