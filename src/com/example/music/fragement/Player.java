package com.example.music.fragement;

import com.example.music.App;
import com.example.music.MainActivity;
import com.example.music.R;
import com.example.music.service.PlayService;
import com.example.music.service.PlayService.StateChangedListener;
import com.example.music.utils.ImageTools;
import com.example.music.views.CDView;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Player extends Base {
	private LinearLayout mRootLayout;
	private TextView mTitle;
	private TextView mArtist;
	private CDView mArt;

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
		updateTackInfo();
		return mRootLayout;
	}

	@Override
	public void onResume() {
		super.onResume();
		updateTackInfo();
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
