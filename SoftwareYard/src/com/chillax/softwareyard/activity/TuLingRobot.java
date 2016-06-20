package com.chillax.softwareyard.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.chillax.config.Path;
import com.chillax.config.URL;
import com.chillax.softwareyard.R;
import com.chillax.softwareyard.adapter.ViewHolder;
import com.chillax.softwareyard.customview.TopBar;
import com.chillax.softwareyard.model.TuLingList;
import com.chillax.softwareyard.model.TuLingMsg;
import com.chillax.softwareyard.utils.CommonUtils;
import com.chillax.softwareyard.utils.StatesUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.util.LogUtils;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by Chillax on 2015/8/8.
 */
@EActivity(R.layout.tuling_layout)
public class TuLingRobot extends BaseActivity implements TopBar.onTopClickedListener {
    private String url;
    private List<String> chatList = new ArrayList<>();
    private ChatAdapter adapter;
    private RequestQueue resQuere;
    private String[] welcomeWords;
    BitmapFactory.Options options=new BitmapFactory.Options();
    private Bitmap user_bitmap;
    @ViewById(R.id.topbar)
    TopBar topbar;

    @ViewById(R.id.tuling_listview)
    ListView lv;
    @ViewById(R.id.tuling_et)
    EditText edit;
    @ViewById(R.id.tuling_tv)
    TextView send;
    @Click
    void tuling_tv() {
        String message = edit.getText().toString().trim();
        if (!TextUtils.isEmpty(message)) {
            edit.setText("");
            chatList.add("right-" + message);
            adapter.notifyDataSetChanged();
            lv.setSelection(chatList.size() - 1);
            handler.obtainMessage(0,message).sendToTarget();
        }
    }
    private Gson gson=new Gson();
    private TuLingMsg<TuLingList> tuLingMsg;

    @AfterViews
    void init() {
        topbar.setTopListener(this);
        handlerThread.start();
        handler= new Handler(handlerThread.getLooper()){
            @Override
            public void handleMessage(Message msg) {
                try{
                    resQuere.add(new JsonObjectRequest(Request.Method.GET, url + URLEncoder.encode((String) msg.obj, "utf-8"), null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            LogUtils.d("Tuling::::" + jsonObject);
                            Type objectType = new TypeToken<TuLingMsg<TuLingList[]>>() {}.getType();
                            tuLingMsg =gson.fromJson(jsonObject.toString(),objectType);
                            chatList.add("left-"+ tuLingMsg);
                            adapter.notifyDataSetChanged();
                            lv.setSelection(chatList.size()-1);
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            chatList.add("left-"+"亲爱的，我没理解您的意思，您能再说一遍吗？");
                            adapter.notifyDataSetChanged();
                        }
                    }));
                }catch (Exception e){
                    LogUtils.e("error:TuLingRobot");
                }
            }
        };
        StatesUtils utils=new StatesUtils(this);
        url=URL.tulingApi+"userid="+ CommonUtils.md5(utils.getUserName())+"&info=";
        File imageFile=new File(Path.userImage1);
        if(imageFile.exists()){
            user_bitmap=BitmapFactory.decodeFile(Path.userImage1,options);
        }
        welcomeWords = this.getResources()
                .getStringArray(R.array.welcome_tips);
        chatList.add("left-"+welcomeWords[(int)(Math.random()*welcomeWords.length)]);
        adapter = new ChatAdapter();
        SwingBottomInAnimationAdapter mAdapter2 = new SwingBottomInAnimationAdapter(
                adapter);
        mAdapter2.setAnimationDurationMillis(300);
        mAdapter2.setAbsListView(lv);
        lv.setAdapter(mAdapter2);
        resQuere = Volley.newRequestQueue(this);
    }

    @Override
    public void onBack(View view) {
        onBackPressed();
    }

    @Override
    public void onMore(View view) {
        chatList.clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onInit(ImageView back, TextView title, ImageView moreIv, TextView moreTv) {
        title.setText("软院萌妹");
        moreTv.setText("清屏");
        moreTv.setVisibility(View.VISIBLE);
    }

    class ChatAdapter extends BaseAdapter {

        Random random=new Random(System.currentTimeMillis());
        int[] girls=new int[]{R.drawable.girl1,R.drawable.girl2,R.drawable.girl3,R.drawable.girl5,R.drawable.girl4};
        int nextInt=random.nextInt(girls.length);
        @Override
        public int getCount() {
            return chatList.size();
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
            String[] message=chatList.get(position).split("-");
            ViewHolder holder=ViewHolder.get(TuLingRobot.this,convertView,R.layout.tuling_list_item,position,parent);
            if(message[0].equals("left")){
                holder.setVisibility(R.id.left,View.VISIBLE).setVisibility(R.id.right,View.GONE).setText(R.id.left_text,message[1]).setImageResource(R.id.left_image,girls[nextInt]);
            }else {
                holder.setVisibility(R.id.left,View.GONE).setVisibility(R.id.right,View.VISIBLE).setText(R.id.right_text,message[1]);
                if(user_bitmap!=null){
//                    xUtils.display(holder.getView(R.id.right_image),Path.userImage1);
                    holder.setImageBitmap(R.id.right_image,user_bitmap);
                }
            }
            holder.setText(R.id.top_time, df.format(new Date()));
            return holder.getConvertView();
        }
    }
    private SimpleDateFormat df=new SimpleDateFormat("HH:mm:ss");
    private HandlerThread handlerThread=new HandlerThread("volley");
    private Handler handler;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handlerThread=null;
        resQuere.cancelAll(this);
    }

}
