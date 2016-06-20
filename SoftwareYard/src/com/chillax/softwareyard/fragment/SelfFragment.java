package com.chillax.softwareyard.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.chillax.config.Path;
import com.chillax.softwareyard.R;
import com.chillax.softwareyard.activity.DownLoadCenter_;
import com.chillax.softwareyard.activity.ExamSchedule_;
import com.chillax.softwareyard.activity.ExamScore_;
import com.chillax.softwareyard.activity.MainActivity;
import com.chillax.softwareyard.activity.SettingCenter_;
import com.chillax.softwareyard.activity.StoreCenter_;
import com.chillax.softwareyard.activity.TuLingRobot_;
import com.chillax.softwareyard.activity.ZoomImage;
import com.chillax.softwareyard.adapter.ViewHolder;
import com.chillax.softwareyard.utils.CommonUtils;
import com.chillax.softwareyard.utils.CusDialog;
import com.chillax.softwareyard.utils.CusIntentService;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@EFragment(R.layout.self_fragment)
public class SelfFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    @ViewById(R.id.self_list)
    ListView listView;
    @ViewById(R.id.userImage)
    ImageView userImageView;
    @ViewById(R.id.user_layout)
    RelativeLayout userLayout;
    BitmapFactory.Options options;
    Dialog dialog;
    private List<String> list = new ArrayList<>();
    private int[] images = new int[]{R.drawable.self_schedule,R.drawable.chengji, R.drawable.self_file, R.drawable.self_store, R.drawable.self_robot};
    public static Bitmap userImageBitmap;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                ExamSchedule_.intent(this).start();
                ((MainActivity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_clam);
                break;
            case 1:
                ExamScore_.intent(this).start();
                ((MainActivity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_clam);
                break;
            case 2:
                DownLoadCenter_.intent(this).start();
                ((MainActivity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_clam);
                break;
            case 3:
                StoreCenter_.intent(this).start();
                ((MainActivity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_clam);
                break;
            case 4:
                TuLingRobot_.intent(this).start();
                ((MainActivity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_clam);
        }
    }

    @AfterViews
    void inits() {
        list.add("考试安排");
        list.add("成绩查询");
        list.add("周知文件");
        list.add("收藏中心");
        list.add("软院萌妹");
        options = new BitmapFactory.Options();
        options.inSampleSize = 6;
        listView.setAdapter(new MyAdapter());
        listView.setOnItemClickListener(this);
        File file = new File(Path.userImage1);
        if (file.exists()) {
//            new BitmapUtils(context).display(userImageView, Path.userImage1);
            userImageBitmap = BitmapFactory.decodeFile(Path.userImage1, options);
            userImageView.setImageBitmap(userImageBitmap);
        }
        file = new File(Path.userImage2);
        if (file.exists()) {
//            xUtils.display(userLayout, Path.userImage2);
            new BitmapDrawable(BitmapFactory.decodeFile(Path.userImage2, options));
            userLayout.setBackgroundDrawable((Drawable) new BitmapDrawable(CommonUtils.blurImageAmeliorate(BitmapFactory.decodeFile(Path.userImage2, options))));
//            userLayout.setBackground();
        }

    }

    private void checkUerMsg() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {

                return null;
            }
        }.execute();
    }

    @Click
    void userImage() {
        showDialog();
    }

    //from MainAcivity:
    public void onMoreClicked() {
        SettingCenter_.intent(this).startForResult(LOGOUT_REQUEST_CODE);
        ((MainActivity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_clam);
    }

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return list.size();
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
            ViewHolder holder = ViewHolder.get(context, convertView, R.layout.self_list_item, position, parent);
            holder.setText(R.id.self_item_text, list.get(position)).setImageResource(R.id.self_item_iamge, images[position]);
            return holder.getConvertView();
        }
    }


    private void showDialog() {
        dialog = CusDialog.create(context, pos -> {
            dialog.dismiss();
            switch (pos) {
                case 0:
//                    Intent intentFromGallery = new Intent();
//                    intentFromGallery.setType("image/*"); // 设置文件类型
//                    intentFromGallery
//                            .setAction(Intent.ACTION_GET_CONTENT);
//                    intentFromGallery.putExtra("return-data", true);
//                    startActivityForResult(intentFromGallery,
//                            IMAGE_REQUEST_CODE);
                    Intent intent = new Intent();
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/*");
//根据版本号不同使用不同的Action
                    if (Build.VERSION.SDK_INT < 19) {
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                    } else {
                        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                    }
                    startActivityForResult(intent, IMAGE_REQUEST_CODE);
//                    Intent intent = new Intent(Intent.ACTION_PICK, null);
//                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                            "image/*");
//                    try {
//                        startActivityForResult(intent, IMAGE_REQUEST_CODE);
//
//                    } catch (android.content.ActivityNotFoundException e) {
//                        CommonUtils.showToast(context,"未找到相册");
//                    }
                    break;
                case 1:
                    Intent intentFromCapture = new Intent(
                            MediaStore.ACTION_IMAGE_CAPTURE);
                    if (CommonUtils.hasSdcard()) {
                        intentFromCapture.putExtra(
                                MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(new File(userImage)));
                    }
                    startActivityForResult(intentFromCapture,
                            CAMERA_REQUEST_CODE);
                    break;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //结果码不等于取消时候
        if (resultCode != 0) {
            switch (requestCode) {
                case IMAGE_REQUEST_CODE:
//                    Cursor cursor = ((MainActivity) context).managedQuery(data.getData(), new String[]{MediaStore.Images.Media.DATA}, null, null, null);
//                    int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//                    cursor.moveToFirst();
//                    LogUtils.d("ChoosedImagePath:"+cursor.getString(index));
//                    startPhotoZoom(cursor.getString(index));
                    startPhotoZoom(CommonUtils.getPath(context, data.getData()));
                    break;
                case CAMERA_REQUEST_CODE:
                    if (CommonUtils.hasSdcard()) {
                        startPhotoZoom(userImage);
                    } else {
                        Toast.makeText(context, "未找到存储卡，无法存储照片！",
                                Toast.LENGTH_LONG).show();
                    }

                    break;
                case RESULT_REQUEST_CODE:
                    if (resultCode == Activity.RESULT_OK) {
                        setImageToView();
                    }
                    break;
                case LOGOUT_REQUEST_CODE:
                    ((MainActivity) context).finish();
                    break;
            }
        }
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(String uri) {
        Intent intent = new Intent();
        intent.setClass(context, ZoomImage.class);
        intent.putExtra("path", uri);
        startActivityForResult(intent, RESULT_REQUEST_CODE);
    }

    /**
     * 设置图片
     */
    private void setImageToView() {
        userImageBitmap = BitmapFactory.decodeFile(Path.userImage1, options);
//        userImageBitmap= BitmapFactory.decodeFile(Path.userImage1, options,true);
        userImageView.setImageBitmap(userImageBitmap);
        userLayout.setBackgroundDrawable((Drawable) new BitmapDrawable(CommonUtils.blurImageAmeliorate(BitmapFactory.decodeFile(Path.userImage2, options))));
        Intent intent = new Intent(context, CusIntentService.class);
        intent.putExtra("task", CusIntentService.FOR_USER_IMAGE_UPLOAD);
        context.startService(intent);
    }

    private static final String userImage = Path.userImage;

    /* 请求码 */
    private static final int IMAGE_REQUEST_CODE = 0;//选择本地照片
    private static final int CAMERA_REQUEST_CODE = 1;//拍照
    private static final int RESULT_REQUEST_CODE = 2;//剪切图片
    private static final int LOGOUT_REQUEST_CODE = 3;//注销

}
