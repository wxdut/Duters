package com.chillax.softwareyard.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.chillax.softwareyard.R;
import com.chillax.softwareyard.activity.CourseInfo_;
import com.chillax.softwareyard.activity.MainActivity;
import com.chillax.softwareyard.adapter.ViewHolder;
import com.chillax.softwareyard.dao.CoursesDBDao;
import com.chillax.softwareyard.dao.DetailDBDao;
import com.chillax.softwareyard.model.Course;
import com.chillax.softwareyard.model.Detail;
import com.chillax.softwareyard.utils.CacheUtils;
import com.chillax.softwareyard.utils.ScreenUtil;
import com.lidroid.xutils.util.LogUtils;

import java.util.ArrayList;

@SuppressLint("ValidFragment")
public class TableItemFragment extends BaseFragment {
    public static final int CODE_FOR_RESULT = 3;
    private View view;
    private int currDay;//range:0-19
    private int currWeek;//range:0-6
    private CoursesDBDao mDao;
    private DetailDBDao mDao2;
    private ListView listView;
    private BaseAdapter adapter;
    private String[] tvs = new String[10];
    private Course courses1[] = new Course[5];
    private Detail details1[] = new Detail[5];
    private String[] colors = new String[]{"#fbfbd5", "#f9cfdd", "#d9f3f5",
            "#fbd399", "#afe9fb"};
    private static int PER_ITEM_HEIGHT;
    private CacheUtils cacheUtils;

    public TableItemFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.table_fragment_day_item, null);
        contentView = view;
        //从TableFragment那里获得ListView的总高度。
        PER_ITEM_HEIGHT=TableFragment.LIST_HEIGHT;
        //去掉divider的高度
        PER_ITEM_HEIGHT-=ScreenUtil.dp2px(context,4)*colors.length-1;
        //获得每个Item的高度
        PER_ITEM_HEIGHT/=colors.length;
        findIds();
        getCoursesData();
        initDatas();
        return contentView;
    }
    private void getCoursesData() {
        for(int i=0;i<10;i++){
            tvs[i]="";
        }
        for (int i = 0; i < 5; i++) {
            if(mDao==null)mDao=new CoursesDBDao(context);
            if(mDao2==null)mDao2=new DetailDBDao(context);
            Course[] courses = mDao.getCourseData(currDay, i);
            if (courses != null && courses[0] != null) {
                for (int j = 0; courses[j] != null; j++) {
                    details1[i] = mDao2.getCourseDetail(currDay,
                            courses[j].getName(), currWeek);
                    if (details1[i] != null) {
                        courses1[i] = courses[j];
                        tvs[2 * i] = courses[j].getName();
                        tvs[2 * i + 1] = courses[j].getRoom();
                        LogUtils.d("TableDayAdapter:" + courses[j].getName()
                                + ":" + courses[j].getRoom());
                        break;
                    }
                }
            }
        }
    }

    public TableItemFragment(Context context,int currWeek, int currDay) {
        this.context = context;
        this.currDay = currDay;
        this.currWeek=currWeek;
        mDao = new CoursesDBDao(context);
        mDao2 = new DetailDBDao(context);
        cacheUtils=new CacheUtils(context, CacheUtils.CacheType.FOR_NOTE_CACHE);
    }
    public void refrush(){
        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }
    }
    private void initDatas() {
        adapter=new BaseAdapter() {
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PER_ITEM_HEIGHT);

            @Override
            public int getCount() {
                return colors.length;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder holder = ViewHolder.get(context, convertView, R.layout.table_item_list_item, position, null);
                View view = holder.getConvertView();
                view.setLayoutParams(params);
                view.setBackgroundColor(Color.parseColor(colors[position]));
                holder.setText(R.id.course_name, tvs[2 * position]).setText(R.id.course_room, tvs[2 * position + 1])
                        .setVisibility(R.id.course_note, TextUtils.isEmpty(cacheUtils.getCache((currWeek * 7 + currDay) + "_" + position)) ? View.GONE : View.VISIBLE);
                setListener(view, courses1[position], details1[position], position);
                return view;
            }
        };
        listView.setAdapter(adapter);
    }

    private void setListener(View view, final Course course, final Detail detail,final int positon) {
        view.setOnClickListener((View v)-> {

                if (detail == null) {
                    CourseInfo_.intent(context).start();
                    ((MainActivity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_clam);
                } else {
                    ArrayList<String> list = new ArrayList<>();
                    list.add(course.getName());
                    list.add(course.getRoom());
                    list.add(detail.getWeeks());
                    list.add(detail.getCredit());
                    list.add(detail.getCategory());
                    list.add(detail.getNum());
                    list.add(detail.getTeacher());
//					"课程名：","地点：","上课周：","学分：","属性：","课序号：","教师："
                    Intent intent = CourseInfo_.intent(context).get();
                    intent.putExtra("note_order",(currWeek*7+currDay)+"_"+positon);
                    intent.putStringArrayListExtra("info", list);
                    //设置该课程在一周中的第几天，在一天中的第几节课
                    intent.putExtra("order", currDay + "-" + positon);
//                    startActivity(intent);
                    getParentFragment().startActivityForResult(intent, CODE_FOR_RESULT);
                    //super.startActivityForResult(intent, ((fragment.mIndex+1)<<16) + (requestCode&0xffff));
                    ((MainActivity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_clam);
                }
        });
    }
    private void findIds() {
        listView = (ListView) contentView.findViewById(R.id.table_item_lv);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==CODE_FOR_RESULT&&resultCode== Activity.RESULT_OK){
            listView.invalidateViews();
        }
    }
}
