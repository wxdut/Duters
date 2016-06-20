package com.chillax.softwareyard.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.chillax.softwareyard.db.DetailDBHelper;
import com.chillax.softwareyard.model.Detail;

public class DetailDBDao {
    private static final String insert = "insert into details(_name,_num,_credit,_category,_teacher,_weeks,_day,_room) values(?,?,?,?,?,?,?,?)";
    private DetailDBHelper dbHelper;

    public DetailDBDao(Context context) {
        dbHelper = new DetailDBHelper(context);
    }

    public void insert(Detail course) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL(insert, new Object[]{course.getName(),
                    course.getNum(), course.getCredit(), course.getCategory(),
                    course.getTeacher(), course.getWeeks(), course.getDay(),
                    course.getRoom()});
            db.close();
        }
    }

    public boolean isEmpty() {
        boolean empty = true;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            if (db.rawQuery("select * from details", null).getCount() > 0) {
                empty = false;
            }
            db.close();
        }
        return empty;
    }

    public Detail getCourseDetail(int day, String name, int currWeek) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor c = db.rawQuery(
                    "select * from details where _day=? and _name=?",
                    new String[]{(day + 1) + "", name});
            if (c != null && c.getCount() > 0) {
                String[] time;
                c.moveToFirst();
                while (!c.isAfterLast()) {
                    System.out.println(c.getString(6));
                    time = c.getString(6).split("-");
                    if (time.length == 1) {
                        if (Integer.parseInt(time[0]) == currWeek + 1) {
                            Detail detail = new Detail(c.getString(1),
                                    c.getString(2), c.getString(3), c.getString(4),
                                    c.getString(5), c.getString(6), c.getString(7),
                                    c.getString(8));
                            c.close();
                            db.close();
                            return detail;
                        }
                    } else if(Integer.parseInt(time[0]) <= currWeek + 1
                            && currWeek + 1 <= Integer.parseInt(time[1])) {
                        Detail detail = new Detail(c.getString(1),
                                c.getString(2), c.getString(3), c.getString(4),
                                c.getString(5), c.getString(6), c.getString(7),
                                c.getString(8));
                        c.close();
                        db.close();
                        return detail;
                    }
                    c.moveToNext();
                }
            }
            db.close();
        }
        return null;
    }

    public void clear() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db.isOpen()) {
            db.execSQL("delete from details");
            db.close();
        }
    }
}
