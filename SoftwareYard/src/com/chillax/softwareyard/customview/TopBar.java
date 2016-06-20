package com.chillax.softwareyard.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chillax.softwareyard.R;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.topbar)
public class TopBar extends RelativeLayout {

    @ViewById(R.id.back)
    ImageView backIv;
    @ViewById(R.id.title)
    TextView titleTv;
    @ViewById(R.id.moreIv)
    ImageView moreIv;
    @ViewById(R.id.moreTv)
    TextView moreTv;
    private onTopClickedListener listener;

    @Click
    void back(View view){
        if(listener!=null){
            listener.onBack(view);
        }
    }
    @Click({R.id.moreTv,R.id.moreIv})
    void more(View view){
        if(listener!=null){
            listener.onMore(view);
        }
    }

    public TopBar(Context context) {
        this(context, null);
    }

    public TopBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TopBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public interface onTopClickedListener{
        void onBack(View view);
        void onMore(View view);
        void onInit(ImageView back,TextView title,ImageView moreIv,TextView moreTv);
    }
    public void setTopListener(onTopClickedListener listener){
        if(listener!=null){
            this.listener=listener;
            listener.onInit(backIv,titleTv,moreIv,moreTv);
        }
    }
}
