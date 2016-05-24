package com.example.music.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.music.MainActivity;
import com.example.music.R;
import com.example.music.model.Track;
import com.example.music.receiver.TrackNextReceiver;
import com.example.music.receiver.TrackPlayReceiver;
import com.example.music.utils.QueryTools;
import com.example.music.utils.TrackUtils;
import com.example.music.utils.TrackUtils.Defs;
import com.example.music.views.BitmapToBlur;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

public class PlayService extends Service implements Defs {

	// 定义通知栏播放按钮的Action
	public static final String ACTION_PREVIOUS_TRACK = "com.example.music.previous";
	public static final String ACTION_NEXT_TRACK = "com.example.music.next";
	public static final String ACTION_PLAY_TRACK = "com.example.music.play";
	SharedPreferences sp;
	private static int lastPosition = -1;
	// 循环模式
	private static int mode;
	private PlayServiceBinder mBinder = new PlayServiceBinder();

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
	private Intent playIntent, nextIntent, previousIntent;
	private PendingIntent playPendingIntent, nextPendingIntent;
	// 通知栏
	private NotificationManager mNotificationManager;
	private Notification mNotification;
	private int NOTI_ID = 1;

	private Activity mActivityCallback;

	private QueryTools mQueryTools;

	private ExecutorService mProgressUpdatedListener = Executors.newSingleThreadExecutor();

	@Override
	public void onCreate() {
		super.onCreate();
		initHeadPluggedListener();
		initPhoneStateChangeListener();
		setNotiControlReceiver();
		initNotification();

		mediaPlayer.setOnCompletionListener(mOnCompletionListener);
		mQueryTools = new QueryTools(this);
		// 开始更新进度的线程

		// mProgressUpdatedListener.execute(mPublishProgressRunnable);
	}

	// 和Activity绑定后返回给Activity的对象
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onDestroy() {
		// 解除监听
		unregisterReceiver(mHeadSetPlugBroadcastReceiver);
		unregisterReceiver(mPhoneStateChangeListener);
		unregisterReceiver(mNotiControlReceiver);
		super.onDestroy();
	}

	public OnCompletionListener mOnCompletionListener = new OnCompletionListener() {
		@Override
		public void onCompletion(MediaPlayer mp) {
			playNextTrack();
		}
	};

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
		updateNotification();
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

	public void stopPLayer() {
		mediaPlayer.stop();
		mediaPlayer.release();
		onStateChanged();
		updateNotification();
	}

	public void pausePlayer() {
		mediaPlayer.pause();
		onStateChanged();
		updateNotification();
	}

	// 重新开始
	public void resumePlayer() {
		mediaPlayer.start();
		onStateChanged();
		updateNotification();
	}

	// 判断是否正在播放
	public boolean getIsPlaying() {
		return mediaPlayer.isPlaying();
	}

	// 收藏
	public boolean onPraisedBtnPressed() {

		boolean hadPraised = mQueryTools.checkIfHasAsFavourite(getCurrentTrackId(), DB_TRACK_NAME, TB_PRAISED_NAME, 1);

		if (!hadPraised) {
			addCurrentToDataBase();
			return true;
		} else {
			mQueryTools.removeTrackFrmDatabase(getCurrentTrackId(), DB_TRACK_NAME, TB_PRAISED_NAME, 1);
			return false;
		}

	}

	public boolean checkIfPraised() {
		return mQueryTools.checkIfHasAsFavourite(getCurrentTrackId(), DB_TRACK_NAME, TB_PRAISED_NAME, 1);
	}

	// 播放 状态改变时调用
	private void onStateChanged() {
		mStateChangedListener.onPlayStateChanged();
		new BlurImageCreater().execute();
	}

	// 把歌曲信息添加到收藏列表的数据库中
	private void addCurrentToDataBase() {
		ContentValues values = new ContentValues();
		values.put("TITLE", getCurrentTitle());
		values.put("ARTIST", getCurrentArtist());
		values.put("PATH", getCurrentFilePath());
		values.put("TRACK_ID", getCurrentTrackId());
		values.put("ALBUM_ID", getCurrentAlbumId());
		values.put("DURATION", getCurrentDuration());
		mQueryTools.addToDb(values, DB_TRACK_NAME, TB_PRAISED_NAME, 1);
	}

	// 监听耳机插入状态
	private void initHeadPluggedListener() {

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);

		mHeadSetPlugBroadcastReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (mediaPlayer.isPlaying())
					pausePlayer();
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
				pausePlayer();
			}

		};

		registerReceiver(mPhoneStateChangeListener, intentFilter);
	}

	public class PlayServiceBinder extends Binder {
		public PlayService getService() {
			return PlayService.this;
		}
	}

	// 凡是实现该StateChangedListener的都可以监听
	public void setActivityCallback(StateChangedListener stateChangedListener) {
		// 监听activity状态变化
		mStateChangedListener = stateChangedListener;
		mActivityCallback = (Activity) stateChangedListener;
	}

	// Service内部接口，目的是监听播放页面状态的改变
	public interface StateChangedListener {
		void onPlayStateChanged();

		void onPublish(int progress);
	}

	// 初始化通知栏
	private void initNotification() {

		mRemoteView = new RemoteViews(getPackageName(), R.layout.layout_notification);

		// 构造通知栏
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setContent(mRemoteView)
				.setContentTitle(getCurrentTitle()).setContentText(getCurrentTitle()).setTicker(getCurrentTitle())
				.setSmallIcon(R.drawable.ic_launcher).setOngoing(false);

		if (playIntent == null) {
			playIntent = new Intent(this, TrackPlayReceiver.class);
		}
		if (nextIntent == null) {
			nextIntent = new Intent(this, TrackNextReceiver.class);
		}
		if (playPendingIntent == null) {
			playPendingIntent = PendingIntent.getBroadcast(this, 0, playIntent, 0);
		}
		if (nextPendingIntent == null) {
			nextPendingIntent = PendingIntent.getBroadcast(this, 0, nextIntent, 0);
		}

		// 监听通知栏按钮
		mRemoteView.setOnClickPendingIntent(R.id.btn_noti_next, nextPendingIntent);
		mRemoteView.setOnClickPendingIntent(R.id.btn_noti_pause, playPendingIntent);
		// 通知
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotification = mBuilder.build();
		mNotificationManager.notify(NOTI_ID, mNotification);
	}

	/**
	 * 更新通知栏内容
	 */
	public void updateNotification() {

		Log.i("music", "update notification" + getCurrentPosition());

		mRemoteView.setTextViewText(R.id.noti_title, getCurrentTitle());

		if (getIsPlaying()) {
			mRemoteView.setImageViewResource(R.id.btn_noti_pause, R.drawable.notification_pause);
		} else {
			mRemoteView.setImageViewResource(R.id.btn_noti_pause, R.drawable.notification_play);
		}

		mRemoteView.setImageViewUri(R.id.iv_art_noti, getCurrentAlbumUri());

		mNotificationManager.notify(NOTI_ID, mNotification);
	}

	public void cancelNoti() {
		mNotificationManager.cancel(NOTI_ID);
	}

	/**
	 * 设置通知栏BroadcastReceiver
	 */
	private void setNotiControlReceiver() {

		mNotiControlReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {

				String action = intent.getAction();

				if (action.equals(ACTION_NEXT_TRACK)) {
					playNextTrack();
				}
				if (action.equals(ACTION_PLAY_TRACK)) {
					if (getIsPlaying()) {
						pausePlayer();
					} else {
						resumePlayer();
					}
				}
			}
		};

		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_NEXT_TRACK);
		filter.addAction(ACTION_PLAY_TRACK);
		registerReceiver(mNotiControlReceiver, filter);
	}

	// 异步获得产生模糊背景图像
	final class BlurImageCreater extends AsyncTask<Void, Void, Drawable> {

		@Override
		protected Drawable doInBackground(Void... arg0) {

			return getBluredCurrentArt();
		}

		@Override
		protected void onPostExecute(Drawable result) {

			notifyBlurIsReady(result);
			super.onPostExecute(result);
		}
	}

	// 获得当前模糊化的背景图片
	public Drawable getBluredCurrentArt() {
		Bitmap bm = getCurrentTrackArt();
		if (bm == null) {
			bm = BitmapFactory.decodeResource(getResources(), R.drawable.default_artist);
		}
		return BitmapToBlur.BoxBlurFilter(bm);
	}

	private void notifyBlurIsReady(Drawable drawable) {
		// MainActivity中获取设置背景图片，通知服务来处理背景图片
		((MainActivity) mActivityCallback).onBlurReady(drawable);
	}

	/**
	 * 更新进度的线程
	 */
	private Runnable mPublishProgressRunnable = new Runnable() {
		@Override
		public void run() {
			for (;;) {
				if (mediaPlayer != null && mediaPlayer.isPlaying() && mStateChangedListener != null) {
					mStateChangedListener.onPublish(mediaPlayer.getCurrentPosition());
				}
				SystemClock.sleep(1000);
			}
		}
	};

	// 进度条滑动到指定位置，只有在播放时才有效
	public void seek(int progress) {
		if (!getIsPlaying())
			return;
		mediaPlayer.seekTo(progress);
	}

}
