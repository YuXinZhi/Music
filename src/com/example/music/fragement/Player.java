package com.example.music.fragement;

import com.example.music.App;
import com.example.music.MainActivity;
import com.example.music.R;
import com.example.music.service.PlayService;
import com.example.music.utils.ImageTools;
import com.example.music.utils.UIUtils;
import com.example.music.utils.TrackUtils.Defs;
import com.example.music.views.CDView;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Player extends Base implements Defs {
	private LinearLayout mRootLayout;
	private TextView mTitle;
	private TextView mArtist;
	private CDView mArt;
	private ImageView mPlayMode;
	private MainActivity mActivity;
	private PlayService mServiceCallback;

	@Override
	public void onPraisedPressed() {
		updateTackInfo();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onAttach(Activity activity) {

		mActivity = (MainActivity) activity;
		mServiceCallback = mActivity.getServiceCallback();
		Log.i("Player-Ser", mServiceCallback + "");
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootLayout = (LinearLayout) inflater.inflate(R.layout.fragment_player, null);
		mTitle = (TextView) mRootLayout.findViewById(R.id.title);
		mArtist = (TextView) mRootLayout.findViewById(R.id.artist);
		mArt = (CDView) mRootLayout.findViewById(R.id.cd);
		mPlayMode = (ImageView) mRootLayout.findViewById(R.id.play_mode);
		mPlayMode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mServiceCallback.playMode == MODE_ALL) {
					mServiceCallback.playMode = MODE_SINGGLE;
					mPlayMode.setImageResource(R.drawable.play_icn_one);
					UIUtils.showToast(mActivity, "单曲循环");
				} else if (mServiceCallback.playMode == MODE_SINGGLE) {
					mServiceCallback.playMode = MODE_SHUFFLE;
					mPlayMode.setImageResource(R.drawable.play_icn_shuffle);
					UIUtils.showToast(mActivity, "随机播放");
				} else if (mServiceCallback.playMode == MODE_SHUFFLE) {
					mServiceCallback.playMode = MODE_ALL;
					mPlayMode.setImageResource(R.drawable.play_icn_loop);
					UIUtils.showToast(mActivity, "列表循环");
				}
			}
		});
		updateTackInfo();
		return mRootLayout;
	}

	@Override
	public void onResume() {
		super.onResume();
		updateTackInfo();
	}

	private void updatePlayMode() {
		switch (mServiceCallback.playMode) {
		case MODE_ALL:
			mPlayMode.setImageResource(R.drawable.play_icn_loop);
			Toast.makeText(mActivity, "列表循环", Toast.LENGTH_SHORT).show();
			break;
		case MODE_SINGGLE:
			mPlayMode.setImageResource(R.drawable.play_icn_loop);
			Toast.makeText(mActivity, "单曲循环", Toast.LENGTH_SHORT).show();
			break;
		case MODE_SHUFFLE:
			mPlayMode.setImageResource(R.drawable.play_icn_loop);
			Toast.makeText(mActivity, "随机播放", Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
	}

	private void updateTackInfo() {

		String title = null;
		String artist = null;
		if (mServiceCallback != null) {
			title = mServiceCallback.getCurrentTitle();
			artist = mServiceCallback.getCurrentArtist();
		}
		if (title == null || title == "") {
			title = "unknown title";
		}
		if (artist == "" || artist == null) {
			artist = "unknown artist";
		}
		Log.i("title", title);
		mTitle.setText(title);
		mArtist.setText(artist);

		// 获取专辑封面更新
		Bitmap trackArt = mServiceCallback.getCurrentTrackArt();
		if (trackArt == null) {
			trackArt = BitmapFactory.decodeResource(getResources(), R.drawable.default_artist);
		}
		mArt.setImage(ImageTools.scaleBitmap(trackArt, (int) (App.sScreenWidth * 0.7)));

		if (mServiceCallback.getIsPlaying()) {
			mArt.start();
		} else {
			mArt.pause();
		}

	}

}
