package com.example.music;

import java.util.ArrayList;
import java.util.List;

import com.dk.animation.SwitchAnimationUtil;
import com.dk.animation.SwitchAnimationUtil.AnimationType;
import com.example.music.fragement.AllTracksFragment;
import com.example.music.fragement.Base;
import com.example.music.fragement.MenuDrawer;
import com.example.music.fragement.Online;
import com.example.music.fragement.Player;
import com.example.music.fragement.Praised;
import com.example.music.service.PlayService;
import com.example.music.service.PlayService.PlayServiceBinder;
import com.example.music.service.PlayService.StateChangedListener;
import com.example.music.utils.TrackUtils;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import android.app.ActionBar;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/**
 * 主界面
 *
 */
public class MainActivity extends FragmentActivity implements OnClickListener, StateChangedListener {

	private final static int SEEKBARMAXVALUE = 200;

	// 绑定服务
	private ServiceConnection mServiceConnection;
	private PlayService mPlayService;

	// 导航栏适配器
	private SectionsPagerAdapter mSectionsPagerAdapter;
	// Actionbar中的图标
	private ImageView mLogoImageView, mCdImageView, mLocalImageView, mFavouriteImageView, mInternetImageView;
	private List<ImageView> mNaViews;

	// ViewPager中的各个子页面
	private List<Base> mFragments;

	private Animation mAnimation, mAnimationFade;

	private ViewPager mViewPager;

	// 底部控制栏控制按钮
	private ImageButton mPlayButton, mNextButton, mPreviousButton, mPraiseButton;
	private TextView titleTextView, artistTextView, mCurrentTime, mTotalTime;
	private ImageView mArtImageView;

	// 根布局
	private RelativeLayout mRootLayout;

	// 抽屉式菜单
	private MenuDrawer mNavigationDrawerFragment;
	private DrawerLayout mDrawerLayout = null;

	private SeekBar mSeekBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 音量控制键控制音乐音量
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		setContentView(R.layout.activity_main);
		if (mRootLayout == null) {
			mRootLayout = (RelativeLayout) findViewById(R.id.root_layout);
		}

		// 自定义Actionbar
		styleActionBar();

		// 初始化按钮
		findControlButtons();
		initImageLoader(this);

		// 初始化子页面
		initPages();

		bindToService();

		startService();

		new SwitchAnimationUtil().startAnimation(getWindow().getDecorView(), AnimationType.SCALE);

		initAnim();

		// 初始化菜单界面
		initDrawer();
	}

	private void initDrawer() {
		mNavigationDrawerFragment = (MenuDrawer) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
		// 设置菜单布局
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

		// 初始化菜单
		mDrawerLayout = mNavigationDrawerFragment.mDrawerLayout;
	}

	// 呼出菜单
	private void toogleDrawer() {

		if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
			mDrawerLayout.closeDrawer(GravityCompat.START);
		} else {
			mDrawerLayout.openDrawer(GravityCompat.START);
		}

	}

	@Override
	protected void onResume() {
		if (mPlayService != null)
			onPlayStateChanged();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// 退出时，解除与服务的绑定
		unbindService(mServiceConnection);
		super.onDestroy();
	}

	/**
	 * 应用组件(客户端)可以调用bindService()绑定到一个service．Android系统之后调用service的onBind()
	 * 方法，它返回一个用来与service交互的IBinder．
	 */
	private void bindToService() {
		mServiceConnection = new ServiceConnection() {

			@Override
			public void onServiceDisconnected(ComponentName name) {

			}

			// 系统调用这个来传送在service的onBind()中返回的IBinder．
			@Override
			public void onServiceConnected(ComponentName name, IBinder iBinder) {
				// PlayServiceBinder在service中定义,传递Service的对象
				PlayServiceBinder playBinder = (PlayServiceBinder) iBinder;
				mPlayService = playBinder.getService();
				// Log.i("music", mPlayService.toString());

				// 告诉服务监听MainActivity
				mPlayService.setActivityCallback(MainActivity.this);

				// 服务连接后更新页面更新页面
				initPager();
				onPlayStateChanged();
			}

		};

		final Intent intent = new Intent();
		intent.setClass(MainActivity.this, PlayService.class);
		boolean b = bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
		System.out.println(b);
	}

	void startService() {
		final Intent intent = new Intent();
		intent.setClass(MainActivity.this, PlayService.class);
		startService(intent);
	}

	// fragment获取Service对象
	public PlayService getServiceCallback() {
		return mPlayService;
	}

	private void styleActionBar() {
		// 设置Actionbar属性
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setCustomView(R.layout.cust_action_bar);

		// 4个图标
		mLogoImageView = (ImageView) findViewById(R.id.app_icon);
		mCdImageView = (ImageView) findViewById(R.id.cd);
		mFavouriteImageView = (ImageView) findViewById(R.id.favour);
		mLocalImageView = (ImageView) findViewById(R.id.local);
		mInternetImageView = (ImageView) findViewById(R.id.internet);

		// 设置图标监听器
		mLogoImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 弹出菜单
				// toogleDrawer();
				Intent intent = new Intent(MainActivity.this, PlayActivity.class);
				startActivity(intent);
			}
		});

		mInternetImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mViewPager.setCurrentItem(3, true);
			}
		});

		mFavouriteImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mViewPager.setCurrentItem(2, true);
			}
		});

		mLocalImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mViewPager.setCurrentItem(1, true);
			}
		});

		mCdImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mViewPager.setCurrentItem(0, true);
			}
		});

		// 右侧4个导航图标
		mNaViews = new ArrayList<ImageView>();
		mNaViews.add(mCdImageView);
		mNaViews.add(mLocalImageView);
		mNaViews.add(mFavouriteImageView);
		mNaViews.add(mInternetImageView);

	}

	private void initPages() {
		mFragments = new ArrayList<Base>();
		mFragments.add(new Player());
		mFragments.add(new AllTracksFragment());
		mFragments.add(new Praised());
		mFragments.add(new Online());
	}

	@SuppressWarnings("deprecation")
	private void initPager() {
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
		// 设置ViewPager
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// Actionbar中被选中的图标和ViewPager页面同步更新
				updateNaviItem(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		mViewPager.startAnimation(mAnimation);
	}

	@SuppressWarnings("deprecation")
	private void updateNaviItem(int position) {
		// 设置没有被选中的图标背景
		for (int i = 0; i < mNaViews.size(); i++) {
			if (position != i)
				mNaViews.get(i).setBackground(getResources().getDrawable(R.drawable.pressed_to));
		}
		// 设置选中的图标背景
		mNaViews.get(position).setBackground(getResources().getDrawable(R.drawable.seleted));

	}

	// 顶部适配器
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return mFragments.get(position);
		}

		@Override
		public int getCount() {
			return mFragments.size();
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			// DO NOTHING
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return "MUSIC";
		}
	}

	private void findControlButtons() {
		// 歌曲控制按钮
		mPlayButton = (ImageButton) findViewById(R.id.btn_play_local);
		mNextButton = (ImageButton) findViewById(R.id.btn_next_local);
		mPreviousButton = (ImageButton) findViewById(R.id.btn_pre_local);
		mPraiseButton = (ImageButton) findViewById(R.id.btn_praised);
		// 底部控制栏歌曲封面
		mArtImageView = (ImageView) findViewById(R.id.iv_art_bottom);
		// 歌曲名
		titleTextView = (TextView) findViewById(R.id.title);
		artistTextView = (TextView) findViewById(R.id.artist);

		mCurrentTime = (TextView) findViewById(R.id.currentTime);
		mTotalTime = (TextView) findViewById(R.id.totalTime);
		mSeekBar = (SeekBar) findViewById(R.id.playSeekBar);
		mSeekBar.setMax(SEEKBARMAXVALUE);
		mSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
		mPlayButton.setOnClickListener(this);
		mPreviousButton.setOnClickListener(this);
		mNextButton.setOnClickListener(this);

		// 收藏按钮
		mPraiseButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (mPlayService != null) {
					if (mPlayService.onPraisedBtnPressed()) {
						mPraiseButton.setImageResource(R.drawable.btn_loved_prs);
					} else {
						mPraiseButton.setImageResource(R.drawable.btn_love_prs);
					}
				}
				// 更新收藏页面的收藏按钮
				mFragments.get(2).onPraisedPressed();

			}
		});
	}

	private OnSeekBarChangeListener mSeekBarChangeListener = new OnSeekBarChangeListener() {

		// 拖拽停止时
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			int progress = seekBar.getProgress();
			mPlayService.seek(progress);
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

		}
	};

	@Override
	public void onClick(View v) {
		int btnId = v.getId();
		switch (btnId) {

		case R.id.btn_play_local:
			if (mPlayService.getIsPlaying()) {
				mPlayService.pausePlayer();
			} else {
				mPlayService.resumePlayer();
			}
			break;

		case R.id.btn_next_local:
			mPlayService.playNextTrack();
			break;

		case R.id.btn_pre_local:
			mPlayService.playPreviousTrack();
			break;

		default:
			break;
		}
	}

	public void onBlurReady(Drawable drawable) {
		if (drawable != null) {
			mRootLayout.setBackground(drawable);
			mRootLayout.startAnimation(mAnimationFade);
			drawable = null;
		}
	}

	// 当播放状态改变时，回调该函数
	@Override
	public void onPlayStateChanged() {
		// 播放/暂停按钮
		updateControlButtonBackground();
		// 专辑封面
		updateArtImage(mArtImageView);
		updateTitle(mPlayService.getCurrentTitle());
		updateArtist(mPlayService.getCurrentArtist());
		updateTotalTime(mPlayService.getCurrentDuration());
		updatePrisedImg();
		mFragments.get(0).onPraisedPressed();

	}

	// 功能和图像变化分离
	public void updateControlButtonBackground() {
		if (mPlayService.getIsPlaying()) {
			mPlayButton.setImageResource(R.drawable.notification_pause);
		} else {
			mPlayButton.setImageResource(R.drawable.notification_play);
		}
	}

	public void updateArtImage(ImageView imageView) {

		if (mPlayService.getPlayList() != null) {
			ImageLoader.getInstance().displayImage(mPlayService.getCurrentAlbumUri().toString(), imageView);
		}
	}

	public void updateTitle(String title) {
		titleTextView.setText(title);
	}

	public void updateArtist(String artist) {
		artistTextView.setText(artist);
	}

	public void updatePrisedImg() {

		if (mPlayService != null && mPlayService.checkIfPraised()) {
			mPraiseButton.setImageResource(R.drawable.btn_loved_prs);
		} else {
			mPraiseButton.setImageResource(R.drawable.btn_love_prs);
		}
	}

	public void updateTotalTime(long duration) {
		mTotalTime.setText(TrackUtils.makeTimeString(this, duration));
	}

	public static void initImageLoader(Context context) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs().build();
		// 初始化图片加载器配置
		ImageLoader.getInstance().init(config);
	}

	// 初始化动画
	private void initAnim() {
		mAnimation = AnimationUtils.loadAnimation(this, R.anim.view_push_down_in);
		mAnimationFade = AnimationUtils.loadAnimation(this, R.anim.fade_in);
	}

	@Override
	public void onPublish(int progress) {
		mCurrentTime.setText("11:11");
		progress = progress * SEEKBARMAXVALUE;
		mSeekBar.setProgress(progress);
	}
}
