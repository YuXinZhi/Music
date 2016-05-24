package com.example.music.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper implements TrackUtils.Defs {

	private String CREATE_PRAISED_TABLE, CREATE_ALLTRACKS_TABLE;

	public DataBaseHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		init();
		db.execSQL(CREATE_PRAISED_TABLE);
		db.execSQL(CREATE_ALLTRACKS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// 整个数据库升级
		db.execSQL("DROP TABLE IF EXISTS " + TB_PRAISED_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + TB_ALLTRCKS_NAME);
		onCreate(db);
	}

	private void init() {
		// 初始化SQL语句
		CREATE_PRAISED_TABLE = "CREATE TABLE" + " " + TB_PRAISED_NAME + "(_id INTEGER PRIMARY KEY AUTOINCREMENT ,"
				+ "TITLE TEXT,ARTIST TEXT,PATH TEXT,TRACK_ID LONG,ALBUM_ID LONG,DURATION LONG)";
		CREATE_ALLTRACKS_TABLE = "CREATE TABLE" + " " + TB_ALLTRCKS_NAME + "(_id INTEGER PRIMARY KEY AUTOINCREMENT ,"
				+ "TITLE TEXT,ARTIST TEXT,PATH TEXT,TRACK_ID LONG,ALBUM_ID LONG,DURATION LONG)";
	}

}
