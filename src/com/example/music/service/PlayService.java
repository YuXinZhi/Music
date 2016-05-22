package com.example.music.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.example.music.model.Track;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.widget.RemoteViews;

public class PlayService extends Service {

	// 定义通知栏播放按钮的Action
	public static final String ACTION_PREVIOUS_TRACK = "com.example.music.previous";
	public static final String ACTION_NEXT_TRACK = "com.example.music.next";
	public static final String ACTION_PLAY_TRACK = "com.example.music.play";

	private PlayServiceBinder mBinder;

	private final MediaPlayer mediaPlayer = new MediaPlayer();

	// 播放列表
	private List<Track> mPlayList;

	// 当前歌曲在列表中的位置
	private int mPosition;

	// 电话状和耳机插入状态监听
	BroadcastReceiver mHeadSetPlugBroadcastReceiver, mPhoneStateChangeListener, mNotiControlReceiver;
	// 播放状态变化的监听
	private StateChangedListener mStateChangedListener;

	// 通知栏的播放器
	private RemoteViews mRemoteView;

	// 通知栏intent
	private Intent playIntent, nextIntent;
	private PendingIntent playPendingIntent, nextPendingIntent;
	// 通知栏
	private NotificationManager mNotificationManager;
	private Notification mNotification;
	private int NOTI_ID = 1;

	// 获取播放歌曲列表
	public List<Track> getPlayList() {
		return this.mPlayList;
	}

	public void setupPLayList(List<Track> playList) {
		if (playList != null && playList.size() > 0) {
			this.mPlayList = playList;
		}
	}

	// 设置歌曲列表当前歌曲
	public void setCurrentPosition(int position) {
		this.mPosition = position;
	}

	public int getCurrentPosition() {
		return this.mPosition;
	}

	public long getCurrentTrackId() {
		if (mPlayList != null)
			return mPlayList.get(mPosition).getId();
		return -1;
	}
	
	public String getCurrentTitle() {
		if (mPlayList != null)
			return mPlayList.get(mPosition).getTitle();
		return null;
	}

	public String getCurrentArtist() {
		if (mPlayList != null)
			return mPlayList.get(mPosition).getArtist();
		return null;
	}

	public long getCurrentDuration() {
		if (mPlayList != null) {
			return mPlayList.get(mPosition).getDuration();
		}
		return -1;
	}

	public long getCurrentAlbumId() {
		if (mPlayList != null) {
			return mPlayList.get(mPosition).getAlbumId();
		}
		return -1;
	}

	public int getPlayListSize() {
		return mPlayList.size();
	}

	public String getCurrentAlbumPath() {
		String priFix = "content://media/external/audio/albumart";
		if (mPlayList != null)
			return priFix + File.separator + mPlayList.get(mPosition).getAlbumId();
		return null;
	}

	// 获得当前的背景图片
	public Bitmap getCurrentTrackArt() {
		return ImageLoader.getInstance().loadImageSync(getCurrentAlbumPath());
	}

	// 获取当前专辑封面的uri显示在通知栏
	public Uri getCurrentAlbumUri() {
		if (mPlayList != null) {
			String urlPrefix = "content://media/external/audio/albumart";
			String urlString = urlPrefix + File.separator + getPlayList().get(getCurrentPosition()).getAlbumId();
			return Uri.parse(urlString);
		}
		return null;
	}

	public String getCurrentFilePath() {
		if (mPlayList != null)
			return mPlayList.get(mPosition).getUrl();
		return null;
	}

	public void playTrack(int position) {

		mediaPlayer.reset();
		try {
			mediaPlayer.setDataSource(mPlayList.get(position).getUrl());
			mediaPlayer.prepare();
			mediaPlayer.start();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		onStateChanged();
		// updateNotification();
	}

	public void playNextTrack() {
		int listSize = mPlayList.size();
		if (mPosition == listSize - 1) {
			setCurrentPosition(0);
		} else {
			setCurrentPosition(mPosition + 1);
		}
		playTrack(mPosition);
	}

	public void playPreviousTrack() {
		int listSize = mPlayList.size();
		if (mPosition == 0) {
			setCurrentPosition(listSize - 1);
		} else {
			setCurrentPosition(mPosition - 1);
		}
		playTrack(mPosition);
	}

	// 播放 状态改变时调用
	private void onStateChanged() {
		mStateChangedListener.onPlayStateChanged();
		// new BlurImageCreater().execute();
	}

	// 监听耳机插入状态
	private void initHeadPluggedListener() {

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);

		mHeadSetPlugBroadcastReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (mediaPlayer.isPlaying())
					;
				// pausePlayer();
			}

		};

		registerReceiver(mHeadSetPlugBroadcastReceiver, intentFilter);
	}

	private void initPhoneStateChangeListener() {
		/**
		 * 动态注册IntentFilter，监听通话状态
		 */
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_CALL);
		intentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
		intentFilter.addAction(Intent.ACTION_DIAL);

		mPhoneStateChangeListener = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				// pausePlayer();
			}

		};

		registerReceiver(mPhoneStateChangeListener, intentFilter);
	}

	public class PlayServiceBinder extends Binder {
		public PlayService getService() {
			return PlayService.this;
		}
	}

	// 和Activity绑定后返回给Activity的对象
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public interface StateChangedListener {
		void onPlayStateChanged();
	}

}
