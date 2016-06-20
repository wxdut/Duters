package com.chillax.softwareyard.network;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.chillax.softwareyard.App;
import com.chillax.softwareyard.fragment.NewsFragment;
import com.chillax.softwareyard.model.News;
import com.chillax.softwareyard.utils.NetworkChecker;
import com.chillax.softwareyard.utils.SortByTime;
import com.lidroid.xutils.util.LogUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 对学生周知列表数据拉取并处理
 */
public class NewsDataLoader extends AsyncTask<Boolean, Void, Void> {

	private List<News> dataList = App.newsList;
	private List<News> tempList=new ArrayList<>();
	private Context context;
	private Handler handler;
	private boolean tag;

	public NewsDataLoader(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
	}

	@Override
	protected Void doInBackground(Boolean... booleans) {
		tag = booleans[0];
			if (tag == true) {
				refreshData();
				if(tempList.size()!=0){
					Collections.sort(tempList, SortByTime.getInstance());
					dataList.clear();
					for(News news:tempList){
						dataList.add(news);
					}
				}
			} else {
				loadMoreData();
				Collections.sort(dataList, SortByTime.getInstance());
			}
		return null;
	}

	private void loadMoreData() {
		if (NetworkChecker.IsNetworkAvailable(context)) {
			try {
				App.currPageIndex++;
				if (App.rowCount == 0) {
					App.rowCount = getRowCount();
				}
				int totalPages = App.rowCount / App.pageCount + 1;
				if (totalPages <= App.currPageIndex) {
					handler.obtainMessage(NewsFragment.NO_MORE_DATA);
					return;
				}
				URL url = new URL(com.chillax.config.URL.zhouzhiUrl2
						+ (totalPages - App.currPageIndex) + ".htm");
				LogUtils.d("otherUrl:" + url.toString());
				Document doc = Jsoup.parse(url, 5200);
				Elements eles = doc.getElementsByTag("UL");
				Element ele = eles.get(eles.size() - 1);
				Elements lis = ele.getElementsByTag("li");
				int start = App.pageCount - App.rowCount % App.pageCount;
				String title, address, time;
				int max = getMaxPageCount(App.currPageIndex);
				for (int i = start; i < start + App.pageCount && i < max; i++) {
					Element e1 = lis.get(i);
					Element e2 = e1.getElementsByTag("a").first();
					Element e3 = e1.getElementsByTag("span").first();
					title = e2.attr("title").trim();
					address = e2.attr("href").trim();
					time = e3.text().trim();
					// LogUtils.d(title + ":" + time + ":" + address);
					News news = new News(title, time, address);
					dataList.add(news);
				}

			} catch (Exception e) {
//				Toast.makeText(context, "数据加载失败", Toast.LENGTH_SHORT).show();
				Log.e("class NewsData:", "学生周知列表数据扒取失败");
				e.printStackTrace();
			}
		}
	}

	private int getMaxPageCount(int currPageIndex) {
		if (currPageIndex == 1
				|| currPageIndex == App.rowCount / App.pageCount + 1) {
			return 25;
		}
		return 50;
	}

	@Override
	protected void onPostExecute(Void result) {
		if (tag == true) {
			handler.obtainMessage(NewsFragment.PullToRefrushOver)
					.sendToTarget();
		} else {
			handler.obtainMessage(NewsFragment.LoadMoreOver).sendToTarget();
		}
	}

	private void refreshData() {
		if (NetworkChecker.IsNetworkAvailable(context)) {
			try {
				tempList.clear();
				URL url = new URL(com.chillax.config.URL.zhouzhiUrl);
				LogUtils.d(url.toString());
				Document doc = Jsoup.parse(url, 3000);
				Elements eles = doc.getElementsByTag("UL");
				Element ele = eles.get(eles.size() - 1);
				for (Element temp : ele.getElementsByTag("li")) {
					Element e1 = temp.getElementsByTag("a").first();
					Element e2 = temp.getElementsByTag("span").first();
					String title = e1.attr("title").trim();
					String time = e2.text().trim();
					String address = e1.attr("href")
							.substring(8, e1.attr("href").length()).trim();
					News news = new News(title, time, address);
					tempList.add(news);
					// LogUtils.d(title + ":" + time + ":" + address);
				}
				App.currPageIndex = 0;
			} catch (Exception e) {
//				Toast.makeText(context, "学生周知数据扒取失败", 0).show();
				Log.e("class NewsData:", "学生周知列表数据扒取失败");
			}
		}
	}

	public int getRowCount() {
		try {
			URL url = new URL(com.chillax.config.URL.zhouzhiUrl);
			if (NetworkChecker.IsNetworkAvailable(context)) {
				Document doc = Jsoup.parse(url, 5000);
				Matcher matcher= Pattern.compile("共\\d{1,4}条").matcher(doc.toString());
				int total=0;
				if(matcher.find()){
					total=Integer.valueOf(matcher.group().replaceAll("[^\\d]",""));
				}
				LogUtils.d("Loader,pageCounts:" + total);
				return total;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
}
