package com.example.music.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {

	private String TABLE_NAME = TrackUtils.TB_PRAISED_NAME;
	private String CREATE_TABLE;

	public DataBaseHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		init();
		db.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}

	private void init() {
		// 初始化SQL语句
		CREATE_TABLE = "CREATE TABLE" + " " + TABLE_NAME + "(_id INTEGER PRIMARY KEY AUTOINCREMENT ,"
				+ "TITLE TEXT,ARTIST TEXT,PATH TEXT,TRACK_ID LONG,ALBUM_ID LONG,DURATION LONG)";
	}

}
