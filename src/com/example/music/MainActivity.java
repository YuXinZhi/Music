package com.example.music;

import java.util.ArrayList;
import java.util.List;

import com.example.music.fragement.AllTracksFragment;
import com.example.music.fragement.Base;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 主界面
 *
 */
public class MainActivity extends FragmentActivity implements OnClickListener {

	// Actionbar中的图标
	private ImageView mLogoImageView, mLocalImageView, mFavouriteImageView, mInternetImageView;
	private List<ImageView> mNaViews;

	// ViewPager中的各个子页面
	private List<Base> mFragments;

	private Animation mAnimation, mAnimationFade;

	private ViewPager mViewPager;

	// 底部控制栏控制按钮
	private ImageButton mPlayButton, mNextButton, mPreviousButton, mPraiseButton;
	private TextView titleTextView;
	private ImageView mArtImageView;

	// 根布局
	private RelativeLayout mRootLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 初始化控制栏
		findControlButtons();
		// 初始化子页面
		initPages();
	}

	private void initPages() {
		mFragments = new ArrayList<Base>();
		mFragments.add(new AllTracksFragment());
		mFragments.add(new AllTracksFragment());
		mFragments.add(new AllTracksFragment());
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

		mPlayButton.setOnClickListener(this);
		mPreviousButton.setOnClickListener(this);
		mNextButton.setOnClickListener(this);

		titleTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		});
		// 收藏按钮
		mPraiseButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
			}
		});
	}

	@Override
	public void onClick(View v) {

	}

}
