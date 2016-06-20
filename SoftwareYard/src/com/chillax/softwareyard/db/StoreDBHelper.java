package com.chillax.softwareyard.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StoreDBHelper extends SQLiteOpenHelper {
	private static final String storeDBName = "com.chillax.softwareyard.db.StoreDBHelper";
	private static final String storeTable = "create table store("
			+ "_id integer primary key," + "_title text," + "_time varchar(20),"
			+ "_address varchar(20));";
	public StoreDBHelper(Context context) {
		super(context, storeDBName, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(storeTable);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
