package com.chillax.softwareyard.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DetailDBHelper extends SQLiteOpenHelper {
	private static final String tableDBName = "com.chillax.softwareyard.db.DetailsDBHelper";
	private static final String coursesTable = "create table details(_id integer primary key,_name text,_num varchar(5),_credit varchar(5),_category varchar(10),_teacher varchar(15),_weeks varchar(20),_day varchar(2),_room varchar(10));";

	/**
	 * num:课程序号, credit：学分, category：属性（必修或者选修）, teacher: 任课老师, weeks:上课周数,day:具体哪一天上课
	 * room：上课地点
	 */
	public DetailDBHelper(Context context) {
		super(context, tableDBName, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(coursesTable);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}
