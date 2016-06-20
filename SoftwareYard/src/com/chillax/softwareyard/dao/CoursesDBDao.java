package com.chillax.softwareyard.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.chillax.softwareyard.db.CoursesDBHelper;
import com.chillax.softwareyard.model.Course;

public class CoursesDBDao {
	private static final String insert = "insert into courses(_name,_room,_day,_order) values(?,?,?,?)";
	private CoursesDBHelper dbHelper;

	public CoursesDBDao(Context context) {
		dbHelper = new CoursesDBHelper(context);
	}

	public void insert(Course course) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.execSQL(insert,
					new Object[] { course.getName(), course.getRoom(),
							course.getDay().trim(), course.getOrder().trim() });
			db.close();
		}
	}

	public boolean isEmpty() {
		boolean empty = true;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			if (dbHelper.getReadableDatabase()
					.rawQuery("select * from courses", null).getCount() > 0) {
				empty = false;
			}
			db.close();
		}
		return empty;
	}

	public Course[] getCourseData(int day, int order) {
		Cursor c = null;
		try {
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			if (db.isOpen()) {
				c = dbHelper.getReadableDatabase().rawQuery(
						"select * from courses where _day=? and _order=?",
						new String[] { day + "", order + "" });
				Course cc[] = new Course[4];
				if (c != null && c.getCount() > 0) {
					c.moveToFirst();
					for (int i = 0; !c.isAfterLast(); i++, c.moveToNext()) {
						cc[i] = new Course(c.getString(1), c.getString(2), day
								+ "", order + "");
					}
				}
				db.close();
				return cc;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return null;
	}

	public void clear() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		if (db.isOpen()) {
			db.execSQL("delete from courses");
			db.close();
		}
	}
}
