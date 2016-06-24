package com.example.music.utils;

import java.util.ArrayList;

import com.example.music.model.Track;
import com.example.music.utils.TrackUtils.Defs;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class QueryTools implements Defs {

	private String TAG = "QueryTools";
	private Context mContext;
	private ArrayList<Track> resultList = null;
	private DataBaseHelper helper;
	private SQLiteDatabase database;
	private Cursor cursor;

	public QueryTools(Context Context) {
		super();
		this.mContext = Context;
	}

	// 查询所有的歌曲
	/**
	 * 
	 * @param dbName
	 *            数据库名
	 * @param tableName
	 *            表名
	 * @param dbVersion
	 * @param order
	 * @param limitCount
	 *            一次查询的数量
	 * @return
	 */
	public ArrayList<Track> getListFrmDataBase(String dbName, String tableName, int dbVersion, String order,
			boolean limitCount) {

		try {

			helper = new DataBaseHelper(mContext, dbName, null, dbVersion);
			database = helper.getWritableDatabase();
			cursor = database.query(tableName, null, null, null, null, null, order);
			cursor.moveToFirst();
			Log.i(TAG, cursor.getCount() + "::::::::::::::::::::::");
			resultList = new ArrayList<Track>();

			// TITLE TEXT,ARTIST TEXT,ALBUM TEXT,DURATION TEXT,LETTER TEXT,
			// PATH TEXT,TRACK_ID INTEGER,ALBUM_ID INTEGER,DURATION LONG
			while (!cursor.isAfterLast() && cursor.getCount() > 0) {
				Log.i(TAG, "begin---------finding in db------------>>>>>>>>>>>>>>>>>>>");
				Track track = new Track();
				track.setTitle(cursor.getString(1));
				track.setArtist(cursor.getString(2));
				track.setUrl(cursor.getString(3));
				track.setId(cursor.getLong(4));
				track.setAlbumId(cursor.getLong(5));
				track.setDuration(cursor.getLong(6));
				resultList.add(track);
				// 默认的限制是10条数据
				if (limitCount) {
					if (resultList.size() > 12) {
						return resultList;
					}
				}
				cursor.moveToNext();
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			// 创建数据库发生异常
			++dbVersion;
			helper.onUpgrade(database, --dbVersion, dbVersion);
		}
		Log.i(TAG, resultList.size() + "::::resultList::::::::::::::::::");
		return resultList;
	}

	// 检查歌曲是否在数据库中
	public boolean checkIfHasAsFavourite(long track_id, String dbName, String tableName, int dbVersion) {
		try {

			helper = new DataBaseHelper(mContext, dbName, null, dbVersion);
			database = helper.getWritableDatabase();
			cursor = database.query(tableName, null, "TRACK_ID=" + track_id, null, null, null, "TITLE DESC");
			if (cursor.getCount() > 0) {
				return true;
			}

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			++dbVersion;
			helper.onUpgrade(database, --dbVersion, dbVersion);
		}
		return false;
	}

	// 检查歌曲是否在数据库中
	public boolean checkIfInDb(long track_id, String dbName, String tableName, int dbVersion) {
		try {

			helper = new DataBaseHelper(mContext, dbName, null, dbVersion);
			database = helper.getWritableDatabase();
			cursor = database.query(tableName, null, "TRACK_ID=" + track_id, null, null, null, "TITLE DESC");
			if (cursor.getCount() > 0) {
				return true;
			}

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			++dbVersion;
			helper.onUpgrade(database, --dbVersion, dbVersion);
		}
		return false;
	}

	// 检查歌曲是否在数据库中
	public Track getTrack(long track_id, String dbName, String tableName, int dbVersion) {
		try {
			Track track;
			helper = new DataBaseHelper(mContext, dbName, null, dbVersion);
			database = helper.getWritableDatabase();
			cursor = database.query(tableName, null, "TRACK_ID=" + track_id, null, null, null, "TITLE DESC");
			if (cursor.getCount() > 0) {
				track = new Track();
				track.setTitle(cursor.getString(1));
				track.setArtist(cursor.getString(2));
				track.setUrl(cursor.getString(3));
				track.setId(cursor.getLong(4));
				track.setAlbumId(cursor.getLong(5));
				track.setDuration(cursor.getLong(6));
				return track;
			} else {
				return null;
			}

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			++dbVersion;
			helper.onUpgrade(database, --dbVersion, dbVersion);
		}
		return null;
	}

	public void removeTrackFrmDatabase(long track_id, String dbName, String tableName, int dbVersion) {
		try {
			helper = new DataBaseHelper(mContext, dbName, null, dbVersion);
			database = helper.getWritableDatabase();
			int d = database.delete(tableName, "TRACK_ID=?", new String[] { track_id + "" });
			Log.v("db_delete number", d + "");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			++dbVersion;
			helper.onUpgrade(database, --dbVersion, dbVersion);
		}
	}

	public void addToDb(ContentValues values, String dbName, String tableName, int dbVersion) {
		helper = new DataBaseHelper(mContext, dbName, null, dbVersion);
		database = helper.getWritableDatabase();
		database.insert(tableName, null, values);
	}

}
