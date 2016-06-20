package com.chillax.softwareyard.customview;

import android.content.Context;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.TextView;

import com.chillax.softwareyard.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class RollViewPager extends ViewPager {
    private String TAG = "RollViewPager";
    private int MAX_PAGE_NUM = Integer.MAX_VALUE;//ViewPager最大页数，用于设置伪无限循环效果
    private int PAGE_CHANGE_DURATION = 4000;//ViewPager更换页面的间隔
    private int PAGE_CHANGE_SPEED = 2000;//ViewPager切换页面的速度
    private Context context;
    private int currentItem;
    private ArrayList<String> uriList;
    private ArrayList<View> dots;
    private TextView title;
    private ArrayList<String> titles;
    private int[] resImageIds;
    private int dot_focus_resId;
    private int dot_normal_resId;
    private OnPagerClickCallback onPagerClickCallback;
    private boolean isShowResImage = true;
    MyOnTouchListener myOnTouchListener;
    ViewPagerTask viewPagerTask;
    private PagerAdapter adapter;

    /**
     * 触摸时按下的点 *
     */
    PointF downP = new PointF();
    /**
     * 触摸时当前的点 *
     */
    PointF curP = new PointF();
    private int abc = 1;
    private float mLastMotionX;
    private float mLastMotionY;

    private float firstDownX;
    private float firstDownY;
    private boolean flag = false;

    private long start = 0;



    public class MyOnTouchListener implements OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            curP.x = event.getX();
            curP.y = event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    start = System.currentTimeMillis();
                    handler.removeCallbacksAndMessages(null);
                    // 记录按下时候的坐标
                    // 切记不可用 downP = curP ，这样在改变curP的时候，downP也会改变
                    downP.x = event.getX();
                    downP.y = event.getY();
                    // 此句代码是为了通知他的父ViewPager现在进行的是本控件的操作，不要对我的操作进行干扰
                    // getParent().requestDisallowInterceptTouchEvent(true);
                    break;
                case MotionEvent.ACTION_MOVE:
                    handler.removeCallbacks(viewPagerTask);
                    Log.i("d", (curP.x - downP.x) + "----" + (curP.y - downP.y));
                    // if (Math.abs(curP.x - downP.x) > Math.abs(curP.y - downP.y)
                    // && (getCurrentItem() == 0 || getCurrentItem() == getAdapter()
                    // .getCount() - 1)) {
                    // getParent().requestDisallowInterceptTouchEvent(false);
                    // } else {
                    // getParent().requestDisallowInterceptTouchEvent(false);
                    // }
                    // 此句代码是为了通知他的父ViewPager现在进行的是本控件的操作，不要对我的操作进行干扰
                    break;
                case MotionEvent.ACTION_CANCEL:
                    // getParent().requestDisallowInterceptTouchEvent(false);
                    startRoll();
                    break;
                case MotionEvent.ACTION_UP:
                    downP.x = event.getX();
                    downP.y = event.getY();
                    long duration = System.currentTimeMillis() - start;
                    if (duration <= 500 && downP.x == curP.x) {
                        onPagerClickCallback.onPagerClick(currentItem % dots.size());
                    } else {
                    }
                    startRoll();
                    break;
            }
            return true;
        }
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        final float x = ev.getX();
        final float y = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                abc = 1;
                mLastMotionX = x;
                mLastMotionY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                if (abc == 1) {
                    if (Math.abs(x - mLastMotionX) < Math.abs(y - mLastMotionY)) {
                        abc = 0;
                        getParent().requestDisallowInterceptTouchEvent(false);
                    } else {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }

                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    public void setChangeDuration(int duration) {
        if (duration > 0) {
            PAGE_CHANGE_DURATION = duration;
        }
    }

    public void setChangeSpeed(int speed, Interpolator interpolator) {
        if (speed > 0) {
            new ViewPagerScroller(context, interpolator).initViewPagerScroll(this);
        }
    }

    public class ViewPagerTask implements Runnable {
        @Override
        public void run() {
            currentItem = currentItem + 1;
            handler.obtainMessage().sendToTarget();
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            RollViewPager.this.setCurrentItem(currentItem);
            postDelayed(viewPagerTask, PAGE_CHANGE_DURATION);
        }
    };

    public RollViewPager(Context context, ArrayList<View> dots,
                         int dot_focus_resId, int dot_normal_resId,
                         OnPagerClickCallback onPagerClickCallback) {
        super(context);
        this.context = context;
        this.dots = dots;
        this.dot_focus_resId = dot_focus_resId;
        this.dot_normal_resId = dot_normal_resId;
        this.onPagerClickCallback = onPagerClickCallback;
        viewPagerTask = new ViewPagerTask();
        myOnTouchListener = new MyOnTouchListener();
        new ViewPagerScroller(context).setScrollDuration(PAGE_CHANGE_SPEED).initViewPagerScroll(this);
    }

    public void setUriList(ArrayList<String> uriList) {
        isShowResImage = false;
        this.uriList = uriList;
    }

    public void notifyDataChange() {
        adapter.notifyDataSetChanged();
    }

    public ArrayList<View> getDots() {
        return dots;
    }

    public void setDots(ArrayList<View> dots) {
        this.dots = dots;
    }

    public void setResImageIds(int[] resImageIds) {
        isShowResImage = true;
        this.resImageIds = resImageIds;
    }

    public void setTitle(TextView title, ArrayList<String> titles) {
        this.title = title;
        this.titles = titles;
        if (title != null && titles != null && titles.size() > 0)
            title.setText(titles.get(0));
    }

    private boolean hasSetAdapter = false;


    public void startRoll() {
        if (!hasSetAdapter) {
            hasSetAdapter = true;
            this.setOnPageChangeListener(new MyOnPageChangeListener());
            adapter = new ViewPagerAdapter();
            this.setAdapter(adapter);
            if (isShowResImage) {
                currentItem = MAX_PAGE_NUM / 2 / resImageIds.length * resImageIds.length;
            } else {
                currentItem = MAX_PAGE_NUM / 2 / uriList.size() * uriList.size();
            }
        }
        //用反射直接改变CurrentItem为中间值，防止卡顿
        try {
            Field field = ViewPager.class.getDeclaredField("mCurItem");
            field.setAccessible(true);
            field.setInt(this, currentItem);
            field.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        handler.postDelayed(viewPagerTask, PAGE_CHANGE_DURATION);
    }

    public void onStart() {
        stopRoll();
        handler.postDelayed(viewPagerTask, PAGE_CHANGE_DURATION);
    }
    public void onStop(){
        stopRoll();
    }
    public void stopRoll() {
        handler.removeCallbacksAndMessages(null);
    }

    class MyOnPageChangeListener implements OnPageChangeListener {
        int oldPosition = 0;

        @Override
        public void onPageSelected(int position) {
            currentItem = position;
            if (title != null)
                title.setText(titles.get(position % titles.size()));
            if (dots != null && dots.size() > 0) {
                dots.get(position % dots.size()).setBackgroundResource(dot_focus_resId);
                dots.get(oldPosition % dots.size()).setBackgroundResource(dot_normal_resId);
            }
            oldPosition = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }
    }

    class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return MAX_PAGE_NUM;
        }

        @Override
        public Object instantiateItem(View container, final int position) {
            View view = View.inflate(context, R.layout.viewpager_item, null);
            ((ViewPager) container).addView(view);
            view.setOnTouchListener(myOnTouchListener);
            ImageView imageView = (ImageView) view.findViewById(R.id.image);
            if (isShowResImage) {
                imageView.setImageResource(resImageIds[position % resImageIds.length]);
            } else {
                ImageLoader.getInstance().displayImage(uriList.get(position % uriList.size()),
                        imageView);
            }
            return view;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView((View) arg2);
        }

    }

    @Override
    protected void onDetachedFromWindow() {
        handler.removeCallbacksAndMessages(null);
        super.onDetachedFromWindow();
    }

    public interface OnPagerClickCallback {
        void onPagerClick(int position);
    }

    /**
     * ViewPager 滚动速度设置
     */
    public class ViewPagerScroller extends Scroller {
        private int mScrollDuration = 2000;             // 滑动速度

        /**
         * 设置速度速度
         *
         * @param duration
         */
        public ViewPagerScroller setScrollDuration(int duration) {
            this.mScrollDuration = duration;
            return this;
        }

        public ViewPagerScroller(Context context) {
            super(context);
        }

        public ViewPagerScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        public ViewPagerScroller(Context context, Interpolator interpolator, boolean flywheel) {
            super(context, interpolator, flywheel);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, mScrollDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            super.startScroll(startX, startY, dx, dy, mScrollDuration);
        }

        public void initViewPagerScroll(ViewPager viewPager) {
            try {
                Field mScroller = ViewPager.class.getDeclaredField("mScroller");
                mScroller.setAccessible(true);
                mScroller.set(viewPager, this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
