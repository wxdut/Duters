package com.chillax.softwareyard.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.Html.ImageGetter;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.chillax.softwareyard.R;
import com.chillax.softwareyard.utils.CommonUtils;
import com.lidroid.xutils.util.LogUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class NewsImageGetter implements ImageGetter {

	private Context mContext;
	private TextView tv;
	private WindowManager mManager;

	public NewsImageGetter(Context context, TextView tv) {
		mContext = context;
		this.tv = tv;
		mManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
	}

	@Override
	public Drawable getDrawable(String source) {
		if (!source.startsWith("http:")) {
			source = "http://ssdut.dlut.edu.cn/"
					+ source.substring(6, source.length());
		}
		// LogUtils.d("imageurl:" + source);
		final String imageUrl = source;
		String imageName = CommonUtils.md5(source);
		String sdcardPath = Environment.getExternalStorageDirectory()
				.toString();
		String savePath = sdcardPath + "/" + "SoftYard" + "/" + "images/"
				+ imageName;
		final File file = new File(savePath);
		final File saveFile = new File(file.getParent());
		if (file.exists()) {
			Drawable drawable = Drawable.createFromPath(savePath);
			int width = mManager.getDefaultDisplay().getWidth() - 100;
			int height = 400;
			height = drawable.getIntrinsicHeight() * width
					/ drawable.getIntrinsicWidth();
			drawable.setBounds(0, 0, width, height);
			return drawable;
		}
		new AsyncTask<String, String, String>() {

			@Override
			protected String doInBackground(String... params) {
				ImageLoader.getInstance().loadImage(imageUrl,
						new SimpleImageLoadingListener() {
							@Override
							public void onLoadingComplete(String imageUri,
									View view, Bitmap loadedImage) {
								super.onLoadingComplete(imageUri, view,
										loadedImage);
								try {
									if (!saveFile.exists()) {
										saveFile.mkdirs();
									}
									file.createNewFile();
									BufferedOutputStream bos = new BufferedOutputStream(
											new FileOutputStream(file));
									loadedImage.compress(
											Bitmap.CompressFormat.PNG, 80, bos);
									bos.flush();
									bos.close();
								} catch (Exception e) {
									LogUtils.e("ImageLoader Error");
									e.printStackTrace();
								}
							}
						});
				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				tv.invalidate();
			}
		}.execute();
		Drawable drawable = mContext.getResources().getDrawable(
				R.drawable.ic_launcher);
		return drawable;
	}
}