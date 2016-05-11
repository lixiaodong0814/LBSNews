package com.lbsnews.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.lbsnews.db.DBHelper;

public class DBUtils {
	private static final String TAG = "***DBUtils**";
	
	public static final String TABLE_USER = "tb_user";
	public static final String CREATE_USER = "create table " + TABLE_USER + "("
			+ "id integer primary key autoincrement, "
			+ "account text not null, "
			+ "password text not null, "
			+ "nickName text, "
			+ "sex text, "
			+ "telephone text, "
			+ "headPic blob, "
			+ "picName text, "
			+ "picPath text );"; 
	public static final String DROP_USER = "drop table if exists " + TABLE_USER + ";";
	
	private static DBHelper dbHelper;
	private static SQLiteDatabase db;

	public static final SQLiteDatabase getDataBase(Context context) {
		dbHelper = new DBHelper(context);
		if (db == null) {
			db = dbHelper.getWritableDatabase();
		}
		
		LogUtils.d(TAG, "获取数据库对象");
		return db;

	}

}
