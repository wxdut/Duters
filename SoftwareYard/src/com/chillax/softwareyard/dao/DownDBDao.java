package com.chillax.softwareyard.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.chillax.softwareyard.App;
import com.chillax.softwareyard.db.DownBDHelper;
import com.chillax.softwareyard.model.Doc;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by Chillax on 2015/7/28.
 */
public class DownDBDao {
    private static final String insert = "insert into downs(_name,_size,_progress,_url,_local) values(?,?,?,?,?)";
    private static final String queryTitle = "select * from downs where _name=?";
    private DownBDHelper dbHelper;
    private List<Doc> dataList = App.docList;

    public DownDBDao(Context context) {
        dbHelper = new DownBDHelper(context);
    }

    /**
     * 向数据库中添加一个数据
     * @param doc  需要添加的数据
     */
    public void insert(Doc doc) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL(insert, new Object[]{doc.getName(), doc.getSize(), doc.getProgress(), doc.getUrl(), doc.getLocal()});
            db.close();
        }
    }

    /**
     * 判断一个文件是否存在
     * @param title 文件名
     */
    public boolean isExist(String title) {
        Cursor cursor = null;
        boolean tag = false;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            cursor = db.rawQuery(queryTitle, new String[]{title});
            tag = (cursor != null && cursor.getCount() > 0) ? true : false;
            cursor.close();
            db.close();
        }
        return tag;
    }

    /**
     * 系统刚启动时进行数据的初始化
     * @param dataList 储存着下载文件的列表
     */
    public void copyAllToList(List<Doc> dataList) {
        Cursor cursor = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db.isOpen()) {
            cursor = db.rawQuery("select * from downs", null);
            cursor.moveToLast();
            while (!cursor.isBeforeFirst()) {
                Doc doc = new Doc(cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5));
                dataList.add(doc);
                cursor.moveToPrevious();
            }
            cursor.close();
            db.close();
        }
    }

    /**
     * 当被销毁时调用该函数进行跟新数据库
     * 数据库数据顺序应该和显示顺序相反
     */
    public void onDestroy() {
        for(Doc doc : dataList){
            if(!isExist(doc.getName())){
                insert(doc);
            }
        }
    }
}
