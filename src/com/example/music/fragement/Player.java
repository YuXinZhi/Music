package com.example.music.fragement;

import java.util.List;

import com.example.music.App;
import com.example.music.MainActivity;
import com.example.music.R;
import com.example.music.model.Track;
import com.example.music.service.PlayService;
import com.example.music.utils.ImageTools;
import com.example.music.utils.TrackUtils;
import com.example.music.utils.TrackUtils.Defs;
import com.example.music.views.CDView;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Player extends Base implements Defs {
	private LinearLayout mRootLayout;
	private TextView mTitle;
	private TextView mArtist;
	private CDView mArt;

	private MainActivity mActivity;
	private PlayService mServiceCallback;

	private int lastPostion;
	private SharedPreferences sp;

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

		sp = mActivity.getSharedPreferences(LASTPOSITION, 0);

		new TrackLoaderTask().execute();
		updateTackInfo();
		return mRootLayout;
	}

	@Override
	public void onResume() {
		super.onResume();
		updateTackInfo();
	}

	private void updateTackInfo() {
		lastPostion = sp.getInt(LASTPOSITION, -1);
		mServiceCallback.setCurrentPosition((lastPostion==-1)?0:lastPostion);
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

	// 异步加载歌曲条目
	final class TrackLoaderTask extends AsyncTask<Void, Void, List<Track>> {

		@Override
		protected List<Track> doInBackground(Void... arg0) {
			return getTracks();
		}

		@Override
		protected void onPostExecute(List<Track> result) {
			mServiceCallback.setupPLayList(result);
			super.onPostExecute(result);
		}

	}

	List<Track> getTracks() {
		return TrackUtils.getTrackList(getActivity());
	}

}
