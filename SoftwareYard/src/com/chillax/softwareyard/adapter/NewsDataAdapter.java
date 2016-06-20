package com.chillax.softwareyard.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.chillax.softwareyard.App;
import com.chillax.softwareyard.R;
import com.chillax.softwareyard.model.News;

import java.util.List;

public class NewsDataAdapter extends BaseAdapter {

	private Context mContext;
	private List<News> dataList = App.newsList;
	private int listitem = 0;
	private int[] res = new int[] { R.drawable.listitem1, R.drawable.listitem2,
			R.drawable.listitem3 };

	public NewsDataAdapter(Context context) {
		mContext = context;
	}

	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return dataList.get(position).hashCode();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = ViewHolder.get(mContext, convertView,
				R.layout.zhouzhi_item, position, parent);
		News news = dataList.get(position);
		holder.setText(R.id.title, news.getTitle())
				.setText(R.id.time, news.getTime())
				.setImageResource(R.id.image, res[listitem++ % 3]);
		return holder.getConvertView();
	}
}
