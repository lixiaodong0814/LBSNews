package com.lbsnews.db;

import com.lbsnews.utils.DBUtils;
import com.lbsnews.utils.LogUtils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	private static final String TAG = "*****DBHelper******";
	
	static final String DATABASE_NAME = "LBSNews.db";
	static final int DATABASE_VERSION = 1;
	
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DBUtils.CREATE_USER);
		LogUtils.i(TAG, "create table ³É¹¦");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(DBUtils.DROP_USER);
		onCreate(db);
	}

}
