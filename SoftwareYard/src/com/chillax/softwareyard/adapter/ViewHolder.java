package com.chillax.softwareyard.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewHolder {
	private SparseArray<View> mViews;
	private View mConvertView;

	public ViewHolder(Context context, int layoutId, int position,
			ViewGroup parent) {
		this.mViews = new SparseArray<View>();
		mConvertView = LayoutInflater.from(context).inflate(layoutId, parent,
				false);
		mConvertView.setTag(this);
	}

	public static ViewHolder get(Context context, View convertView,
			int layoutId, int position, ViewGroup parent) {
		if (convertView != null) {
			return (ViewHolder) convertView.getTag();
		}
		return new ViewHolder(context, layoutId, position, parent);
	}

	public <T extends View> T getView(int viewId) {
		View view = mViews.get(viewId);
		if (view == null) {
			view = mConvertView.findViewById(viewId);
			mViews.put(viewId, view);
		}
		return (T) view;
	}
	public ViewHolder setVisibility(int viewId,int visi){
		getView(viewId).setVisibility(visi);
		return this;
	}
	public ViewHolder setText(int viewId, String text) {
		TextView tv = getView(viewId);
		tv.setText(text);
		return this;
	}
	public ViewHolder setImageBitmap(int viewId,Bitmap bm){
		ImageView iv = getView(viewId);
		iv.setImageBitmap(bm);
		return this;
	}
	public ViewHolder setImageResource(int viewId, int resId) {
		ImageView iv = getView(viewId);
		iv.setImageResource(resId);
		return this;
	}

	public View getConvertView() {
		return mConvertView;
	}
}
