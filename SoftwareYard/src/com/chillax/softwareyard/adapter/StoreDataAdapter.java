package com.chillax.softwareyard.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.chillax.swipelistview.SwipeListView;
import com.chillax.softwareyard.App;
import com.chillax.softwareyard.R;
import com.chillax.softwareyard.model.News;

import java.util.List;

public class StoreDataAdapter extends BaseAdapter {

	private Context mContext;
	private List<News> storeList = App.storeList;

	public StoreDataAdapter(Context context) {
		mContext = context;
	}

	@Override
	public int getCount() {
		return storeList.size();
	}

	@Override
	public Object getItem(int position) {
		return storeList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = ViewHolder.get(mContext, convertView,
				R.layout.front_back_store, position, parent);
		((SwipeListView) parent).recycle(holder.getConvertView(), position);
		News news = storeList.get(position);
		holder.setText(R.id.title, news.getTitle()).setText(R.id.time,
				news.getTime());
		return holder.getConvertView();
		/*ViewHolder holder = null;
		News news = storeList.get(position);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.front_back_store, null);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		((SwipeListView) parent).recycle(convertView, position);
		holder.title.setText(news.getTitle());
		holder.time.setText(news.getTime());
		return convertView;*/
	}

//	class ViewHolder {
//		TextView title;
//		TextView time;
//	}
}
