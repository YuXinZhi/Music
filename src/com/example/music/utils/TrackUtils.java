package com.example.music.utils;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import com.example.music.R;
import com.example.music.model.Track;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;
import android.util.Log;
import android.provider.MediaStore.MediaColumns;

public class TrackUtils {

	public interface Defs {
		// 收藏的歌曲数据库名和表名
		public static final String DB_TRACK_NAME = "DB_TRACK_NAME";
		public static final String TB_PRAISED_NAME = "TABLE_PRAISED";

		public static final String TB_ALLTRCKS_NAME = "TABLE_ALLTRCKS_NAME";

		public static final String MYSP = "com.example.music.sharedpreferences";
		public static final String LASTPOSITION = "LASTPOSITION";
		public static final int MODE_ALL = 0;
		public static final int MODE_SINGGLE = 1;
		public static final int MODE_SHUFFLE = 2;
	}

	// 查询外部存储
	/**
	 * 访问sdcard中的音频文件的URI为MediaStore.Audio.Media.EXTERNAL_CONTENT_URI，
	 * 为了使播放列表显示所以音乐文件的信息，这里需要查询sdcard里的音频文件，并把查询到的信息保存在Cursor中
	 */
	public static List<Track> getTrackList(Context c) {

		List<Track> list = new ArrayList<Track>();
		ContentResolver cr = c.getContentResolver();

		// "content://" + "media" + "/";
		// volumeName + "/file"
		Cursor cursor = cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
				MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

		if (cursor != null && cursor.getCount() > 0) {
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
				String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaColumns.TITLE));

				String singer = cursor.getString(cursor.getColumnIndexOrThrow(AudioColumns.ARTIST));

				int time = cursor.getInt(cursor.getColumnIndexOrThrow(AudioColumns.DURATION));
				// time = time / 60000;

				String duration = TrackUtils.makeTimeString(c, time);
				String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaColumns.DISPLAY_NAME));
				//
				// String suffix = name
				// .substring(name.length() - 4, name.length());

				String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaColumns.DATA));
				String album = cursor.getString(cursor.getColumnIndexOrThrow(AudioColumns.ALBUM));
				long albumid = cursor.getLong(cursor.getColumnIndex(AudioColumns.ALBUM_ID));

				if (url.endsWith(".mp3") || url.endsWith(".MP3")) {
					Track track = new Track();
					track.setTitle(title);
					track.setArtist(singer);
					track.setId(id);
					track.setUrl(url);
					track.setAlbumId(albumid);
					track.setDuration(time);
					list.add(track);
				}
			}
		}
		// 关闭Cursor
		try {
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
			return list;
		}
		return list;
	}

	private static StringBuilder sFormatBuilder = new StringBuilder();
	private static Formatter sFormatter = new Formatter(sFormatBuilder, Locale.getDefault());
	private static final Object[] sTimeArgs = new Object[5];

	/**
	 * 格式化歌曲时间
	 * 
	 * @param context
	 * @param duration
	 *            以毫秒为单位
	 * @return
	 */
	public static String makeTimeString(Context context, long duration) {
		// 毫秒转换成秒
		duration = duration / 1000;
		String durationformat = context
				.getString(duration < 3600 ? R.string.durationformatshort : R.string.durationformatlong);

		/*
		 * Provide multiple arguments so the format can be changed easily by
		 * modifying the xml.
		 */
		sFormatBuilder.setLength(0);

		final Object[] timeArgs = sTimeArgs;
		timeArgs[0] = duration / 3600;
		timeArgs[1] = duration / 60;
		timeArgs[2] = (duration / 60) % 60;
		timeArgs[3] = duration;
		timeArgs[4] = duration % 60;

		return sFormatter.format(durationformat, timeArgs).toString();
	}

}
