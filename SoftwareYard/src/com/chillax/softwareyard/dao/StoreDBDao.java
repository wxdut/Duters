package com.chillax.softwareyard.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.chillax.softwareyard.App;
import com.chillax.softwareyard.db.StoreDBHelper;
import com.chillax.softwareyard.model.News;

import java.util.List;

public class StoreDBDao {
	private static final String insert = "insert into store(_title,_time,_address) values(?,?,?)";
	private static final String queryTitle = "select * from store where _title=?";
	private StoreDBHelper dbHelper;
	private List<News> dataList = App.newsList;

	public StoreDBDao(Context context) {
		dbHelper = new StoreDBHelper(context);
	}

	public void insert(News news) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.execSQL(insert, new Object[] { news.getTitle(), news.getTime(),
					news.getAddress() });
			db.close();
		}
	}

	public boolean isExist(String title) {
		Cursor cursor = null;
		boolean tag = false;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			cursor = db.rawQuery(queryTitle, new String[] { title });
			tag = (cursor != null && cursor.getCount() > 0) ? true : false;
			if (cursor != null) {
				cursor.close();
			}
			db.close();
		}
		return tag;
	}

	public void copyAllToList(List<News> dataList) {
		Cursor cursor = null;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		if (db.isOpen()) {
			cursor = db.rawQuery("select * from store", null);
			cursor.moveToLast();
			while (!cursor.isBeforeFirst()) {
				News news = new News(cursor.getString(1), cursor.getString(2),
						cursor.getString(3));
				dataList.add(news);
				cursor.moveToPrevious();
			}
			cursor.close();
			db.close();
		}
	}

	public void onDestroy() {
		int j = 0;
		if (dataList.size() >= 25) {
			for (int i = 24; i >= 0; i--) {
				if (!isExist(dataList.get(i).getTitle())) {
					insert(dataList.get(i));
					j++;
				} else {
					break;
				}
			}
			if (j != 20) {
				for (int i = 0; i < j; i++) {
					deleteFirst();
				}
			}
		}
	}

	private void deleteFirst() {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			Cursor c = db.rawQuery("select * from store", null);
			c.moveToFirst();
			String id = c.getString(0);
			db.execSQL("delete from store where _id=?", new Object[] { id });
			db.close();
		}
	}

	public void clear() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		if (db.isOpen()) {
			db.execSQL("delete from store");
			db.close();
		}
	}
}
