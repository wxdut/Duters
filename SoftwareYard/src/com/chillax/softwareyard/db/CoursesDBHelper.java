package com.chillax.softwareyard.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CoursesDBHelper extends SQLiteOpenHelper {
	private static final String tableName = "com.chillax.softwareyard.db.CoursesDBHelper";
	private static final String createTable = "create table courses(_id integer primary key,_name text,_room varchar(10),_day varchar(2),_order varchar(1));";

	public CoursesDBHelper(Context context) {
		super(context, tableName, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(createTable);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
