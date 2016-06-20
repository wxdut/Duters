package com.chillax.softwareyard.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Chillax on 2015/7/28.
 */
public class DownBDHelper extends SQLiteOpenHelper {
    private static final String downDBName = "com.chillax.softwareyard.db.DownBDHelper";
    private static final String downTable = "create table downs("
            + "_id integer primary key," + "_name text," + "_size varchar(20)," +
            "_progress varchar(20),_url text," +
            "_local text);";

    public DownBDHelper(Context context) {
        super(context, downDBName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(downTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
