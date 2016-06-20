package com.chillax.softwareyard.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.chillax.config.Path;
import com.chillax.softwareyard.R;
import com.chillax.softwareyard.utils.CommonUtils;
import com.chillax.softwareyard.utils.ScreenUtil;

import java.io.IOException;

/**
 * Created by Chillax on 2015/8/14.
 */
public class MyImageView extends ImageView {

    public class Options {
        public int stroke = 2;//描边的宽度
        public int width ;//截图的宽度
        public int height;//截图的高度
        public int color = Color.parseColor("#00ff00");
    }
    private Bitmap result;
    int width ;
    int height ;
    private Paint paint;
    private Options options = new Options();
    public Options getOptions(){
        return options;
    }
    public void clip(){
        clip=true;
        invalidate();
        clip1();
        clip2();
    }
    private void clip2(){
        Bitmap bitmap=Bitmap.createBitmap(result, (width - options.height) / 2, 0, options.height, options.height);
        try {
            CommonUtils.writeBitmap2File(bitmap,Path.userImage1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void clip1(){
        setDrawingCacheEnabled(true);
        Bitmap bp=getDrawingCache();
//        System.out.println((width-options.width)/2);
//        System.out.println((height - options.height) / 2);
//        System.out.println(options.width+"L"+options.height);
        result=Bitmap.createBitmap(bp,0,(height-options.height)/2, options.width, options.height);
        setDrawingCacheEnabled(false);
        try {
            CommonUtils.writeBitmap2File(result,Path.userImage2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private boolean clip=false;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(!clip){
            width=getWidth();
            height=getHeight();
            options.width=width;
            paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(options.stroke);
            paint.setColor(options.color);
            //绘制方形框
            canvas.drawRect(0, (height-options.height)/2, width, (height+options.height)/2, paint);
            //绘制圆形框
            canvas.drawCircle(width / 2, height / 2, options.height/2, paint);
        }

    }
    private Context context;
    public MyImageView(Context context) {
        this(context, null);
    }

    public MyImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        options.height= ScreenUtil.dp2px(context, 200);//200dp
        TypedArray ta=context.obtainStyledAttributes(attrs, R.styleable.MyImageView);
        options.color=Color.parseColor(ta.getString(R.styleable.MyImageView_color));
        options.stroke=(int)ta.getDimension(R.styleable.MyImageView_stroke,2);
        ta.recycle();
    }

}
