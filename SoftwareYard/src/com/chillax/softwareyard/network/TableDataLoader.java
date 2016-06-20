package com.chillax.softwareyard.network;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;

import com.chillax.softwareyard.activity.LoginActivity;
import com.chillax.softwareyard.dao.CoursesDBDao;
import com.chillax.softwareyard.dao.DetailDBDao;
import com.chillax.softwareyard.model.Course;
import com.chillax.softwareyard.model.Detail;
import com.chillax.softwareyard.utils.CommonUtils;
import com.chillax.softwareyard.utils.NetworkChecker;
import com.lidroid.xutils.util.LogUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TableDataLoader extends AsyncTask<String, String, String> {
	private String LogTag = "TableDataLoader--->";
	public String[][] coursesDatas = new String[6][7];
	private Handler mHandler;
	private Context mContext;
	private DetailDBDao mDao;
	private CoursesDBDao mDao2;
	private int currhang = 0;

	public TableDataLoader(Context context, Handler handler) {
		mHandler = handler;
		mContext = context;
		mDao = new DetailDBDao(context);
		mDao2 = new CoursesDBDao(context);
	}

	@Override
	protected void onPreExecute() {
		if (!NetworkChecker.IsNetworkAvailable(mContext)) {
			mHandler.obtainMessage(LoginActivity.NET_ERROR).sendToTarget();
			cancel(true);
		}
	}

	@Override
	protected void onPostExecute(String result) {
		synchronized (mContext) {
			if(result.contains("登录超时")){
				mHandler.obtainMessage(LoginActivity.DATA_ERROR).sendToTarget();
				return;
			}
			Document doc = Jsoup.parse(result);
			Element courses = doc.getElementById("kbxx");
			if (courses == null) {
				mHandler.obtainMessage(LoginActivity.NET_ERROR_2).sendToTarget();
			} else {
				getUsefulData(result);
				mHandler.obtainMessage(LoginActivity.LOGIN_SUCESS)
						.sendToTarget();
			}
		}
	}

	private void getUsefulData(String htmlStr) {
		Document doc = Jsoup.parse(htmlStr);
		Element courses = doc.getElementById("kbxx");
		Element details = doc.getElementsByClass("titleTop2").get(1);
		getTables(courses);
		getDetails(details);
	}

	private List<Detail> list = new ArrayList<>();

	private void getDetails(Element details) {
		Elements first = details.getElementById("user").getElementsByTag("tr");
		Pattern weekPattern=Pattern.compile("\\d+-\\d+");
		int i = 0;
		for (Element ele : first) {
			if (i++ == 0)
				continue;
			Elements second = ele.getElementsByTag("td");
			if (second.size() == 18) {
				Detail detail = new Detail();
				detail.setName(second
						.get(2)
						.text()
						.replaceAll("\\s",""));
				detail.setNum(second.get(3).text().trim());
				detail.setCredit(second.get(4).text());
				detail.setCategory(second.get(5).text());
				detail.setTeacher(second.get(7).text());
				//坑爹的教务处的排版的原因，这里需要加一个判断：
				String weeksStr=second.get(11).text();
				Matcher weekMatcher=weekPattern.matcher(weeksStr);
				while (weekMatcher.find()){
					detail.setWeeks(weekMatcher.group());
					detail.setDay(second.get(12).text().replaceAll("[^0-9]", ""));
					//在Course里已经有了Room,这里没必要再设置一次
//				detail.setRoom((second.get(16).text() + second.get(17).text())
//						.replaceAll("教学楼", "").replaceAll(" ", "")
//						.replaceAll("AA", "A").replaceAll("BB", "B")
//						.replaceAll("CC", "C"));
					list.add(detail);
				}
			} else {
				Detail pre = list.get(list.size() - 1);
				Detail details1 = new Detail(pre.getName(), pre.getNum(),
						pre.getCredit(), pre.getCategory(), pre.getTeacher(),
						second.first().text().replaceAll("[^0-9-]", ""),
						second.get(1).text().replaceAll("[^0-9]", ""),
						pre.getRoom());
				list.add(details1);
			}
		}
		for (Detail detail : list) {
			mDao.insert(detail);
		}
	}

	private void getTables(Element detail) {
		currhang=0;
		Elements datas = detail.getElementsByAttributeValue("bgcolor",
				"#FFFFFF");
		int i = 0;
		for (Element each : datas) {
			++i;
			if (i % 5 == 0 || i % 2 == 0) {
				continue;
			}
			Elements data = each.getElementsByTag("td");
			if (i % 5 == 1) {
				for (int k = 2; k < data.size(); k++) {
					coursesDatas[currhang][k - 2] = data.get(k).text();
				}
			} else {
				for (int k = 1; k < data.size(); k++) {
					coursesDatas[currhang][k - 1] = data.get(k).text();
				}
			}
			currhang++;
		}
		String name, room=null;
		int day, order;
		Pattern pattern=Pattern.compile("\\s\\b(.*?)_");
//		Pattern pattern2=Pattern.compile("\\((.*?)\\)");
		Pattern pattern2=Pattern.compile("\\((.*?)\\)");
		for (day = 0; day < 7; day++) {
			for (order = 0; order < 5; order++) {
				Matcher matcher=pattern.matcher(coursesDatas[order][day]);
				Matcher matcher2=pattern2.matcher(coursesDatas[order][day]);
				while (matcher.find()){
					name=matcher.group(1);
					matcher2.find();
					room=matcher2.group(1);
					if(room.length()<4){
						matcher2.find();
						room=matcher2.group(1);
					}
					Course course = new Course(name, room, day
							+ "", order + "");
					mDao2.insert(course);
				}
			}
		}
//		for (day = 0; day < 7; day++) {
//			for (order = 0; order < 5; order++) {
//				name = coursesDatas[order][day].split("_")[0];
//				if (!name.equals(coursesDatas[order][day])) {
//					Matcher matcher=pattern.matcher(coursesDatas[order][day]);
//					while(matcher.find()){
//						room=matcher.group();
//					}
//					Course course = new Course(name.replaceAll(
//							"[^(a-zA-Z0-9\\u4e00-\\u9fa5)-_]", ""), room, day
//							+ "", order + "");
//					mDao2.insert(course);
//				}
//			}
//		}
	}

	@Override
	protected String doInBackground(String... params) {
		String userName = params[0];
		String userPwd = params[1];
		LogUtils.d(LogTag + userName + ":" + userPwd);
		String cookie = new HttpCookie("JSESSIONID", CommonUtils.md5(userName)).toString();
		StringBuffer result = new StringBuffer();
		try {
			URL loginUrl = new URL(
					com.chillax.config.URL.loginUrl + "?zjh=" + userName
							+ "&mm=" + userPwd);
			URL tableUrl = new URL(
					com.chillax.config.URL.tableUrl);
			HttpURLConnection conn = (HttpURLConnection) loginUrl
					.openConnection();
			conn.addRequestProperty("Cookie",
					cookie);
			conn.setConnectTimeout(3000);
			conn.setReadTimeout(3000);
			InputStream is = conn.getInputStream();
			is.close();
			conn.disconnect();
			HttpURLConnection conn1 = (HttpURLConnection) tableUrl
					.openConnection();
			conn1.addRequestProperty("Cookie",
					cookie);
			InputStream is1 = conn1.getInputStream();
			BufferedReader reader1 = new BufferedReader(new InputStreamReader(
					is1, "gbk"));
			String lineStr1 = null;
			while ((lineStr1 = reader1.readLine()) != null) {
				result.append(lineStr1);
			}
			reader1.close();
			is1.close();
			conn1.disconnect();
		} catch (Exception e) {
			LogUtils.e("扒取课程表数据失败！");
			e.printStackTrace();
		}
		return result.toString();
	}

}
