package com.example.music.fragement;

import com.example.music.App;
import com.example.music.MainActivity;
import com.example.music.R;
import com.example.music.service.PlayService;
import com.example.music.utils.ImageTools;
import com.example.music.utils.TrackUtils.Defs;
import com.example.music.utils.UIUtils;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
		Log.i("Player", "-------------onAttach--------------");
		mActivity = (MainActivity) activity;
		mServiceCallback = mActivity.getServiceCallback();
		Log.i("Player-Ser", mServiceCallback + "");
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i("Player", "-------------onCreate--------------");
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i("Player", "-------------onCreateView--------------");
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
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.i("Player", "-------------onActivityCreated--------------");
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onStart() {
		Log.i("Player", "-------------onStart--------------");
		super.onStart();
	}

	@Override
	public void onResume() {
		Log.i("Player", "-------------onResume--------------");

		super.onResume();
		// updateTackInfo();
	}

	@Override
	public void onPause() {
		Log.i("Player", "-------------onPause--------------");
		super.onPause();
	}

	@Override
	public void onStop() {
		Log.i("Player", "-------------onStop--------------");
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		Log.i("Player", "-------------onDestroyView--------------");
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		Log.i("Player", "-------------onResume--------------");
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		Log.i("Player", "-------------onResume--------------");
		super.onDetach();
	}

	// private void updatePlayMode() {
	// switch (mServiceCallback.playMode) {
	// case MODE_ALL:
	// mPlayMode.setImageResource(R.drawable.play_icn_loop);
	// Toast.makeText(mActivity, "列表循环", Toast.LENGTH_SHORT).show();
	// break;
	// case MODE_SINGGLE:
	// mPlayMode.setImageResource(R.drawable.play_icn_loop);
	// Toast.makeText(mActivity, "单曲循环", Toast.LENGTH_SHORT).show();
	// break;
	// case MODE_SHUFFLE:
	// mPlayMode.setImageResource(R.drawable.play_icn_loop);
	// Toast.makeText(mActivity, "随机播放", Toast.LENGTH_SHORT).show();
	// break;
	// default:
	// break;
	// }
	// }

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
