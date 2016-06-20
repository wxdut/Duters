package com.chillax.softwareyard.fragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chillax.softwareyard.R;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Chillax on 2015/8/14.
 */
@EFragment(R.layout.leader_item)
public class LeaderItem extends BaseFragment {
    @ViewById
    ImageView bg;
    @ViewById
    TextView comein;
    @Click
    void comein(){
        if(listener!=null){
            listener.onComeInClicked();
        }
    }
    private onComeinClickedListener listener;
    public void setPager(int index) {
        switch (index) {
            case 0:
                bg.setImageResource(R.drawable.leader3);
                comein.setVisibility(View.GONE);
                break;
            case 1:
                bg.setImageResource(R.drawable.leader2);
                comein.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }
    public void setOnComeinClickedListener(onComeinClickedListener listener){
        this.listener=listener;
    }
    public interface onComeinClickedListener{
        public void onComeInClicked();
    }
}
