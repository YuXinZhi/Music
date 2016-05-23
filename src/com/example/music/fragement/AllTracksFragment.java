package com.example.music.fragement;

import java.util.List;

import com.example.music.MainActivity;
import com.example.music.R;
import com.example.music.adapter.TrackListAdapter;
import com.example.music.model.Track;
import com.example.music.service.PlayService;
import com.example.music.utils.TrackUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class AllTracksFragment extends Base {
	private ListView mListView;
	private TrackListAdapter mAdapter;

	// fragment依赖的Activity
	private MainActivity mActivity;

	private PlayService mServiceCallback;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mListView = (ListView) inflater.inflate(R.layout.fragment_all, null);
		Log.i("onCreateView", "AllTracksFragmentonCreateView");
		display();
		return mListView;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onAttach(Activity activity) {
		mActivity = (MainActivity) activity;
		mServiceCallback = mActivity.getServiceCallback();
		Log.i("AllTracks-ser", mServiceCallback + "");
		super.onAttach(activity);
	}

	@Override
	public void onPraisedPressed() {

	}

	void display() {
		new TrackLoaderTask().execute();
	}

	// 异步加载歌曲条目
	final class TrackLoaderTask extends AsyncTask<Void, Void, List<Track>> {

		@Override
		protected List<Track> doInBackground(Void... arg0) {
			return getTracks();
		}

		@Override
		protected void onPostExecute(List<Track> result) {
			inflateListView(result);
			super.onPostExecute(result);
		}

	}

	private void inflateListView(final List<Track> tracks) {
		// Log.i("tracks", tracks.size() + "============="); 14
		mAdapter = new TrackListAdapter(tracks, getActivity(), ImageLoader.getInstance());
		mListView.setAdapter(mAdapter);
		// 设置播放列表
		mServiceCallback.setupPLayList(tracks);

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (mServiceCallback.getPlayList() != tracks)
					mServiceCallback.setupPLayList(tracks);
				mServiceCallback.setCurrentPosition(position);
				mServiceCallback.playTrack(position);
			}
		});
	}

	List<Track> getTracks() {
		return TrackUtils.getTrackList(getActivity());
	}

}
