package com.chillax.softwareyard.activity;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chillax.softwareyard.R;
import com.chillax.softwareyard.customview.MyImageView;
import com.chillax.softwareyard.customview.ActionBar;
import com.chillax.softwareyard.utils.CusDialog;
import com.lidroid.xutils.BitmapUtils;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Chillax on 2015/8/14.
 */
public class ZoomImage extends BaseActivity implements View.OnClickListener{
    private MyImageView iv;
    private ImageView back;
    private TextView ok;
    private PhotoViewAttacher attacher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zoom_image_layout);
        initBitmap();
        initPhotoViews();
    }

    private void initPhotoViews(){
        attacher=new PhotoViewAttacher(iv);
        attacher.setAllowParentInterceptOnEdge(true);
    }
    private void initBitmap(){
        iv=(MyImageView)findViewById(R.id.iv);
        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(this);
        ok=(TextView)findViewById(R.id.ok);
        ok.setOnClickListener(this);
        BitmapUtils utils=new BitmapUtils(this);
        utils.flushCache();
        utils.clearCache();
        utils.display(iv,getIntent().getStringExtra("path"));

    }

    @Override
    public void onClick(View view) {
        if(view.getId()==ok.getId()){
            iv.clip();
            setResult(Activity.RESULT_OK);
        }
        onBackPressed();
    }
}
