package com.example.music.fragement;

import java.util.List;

import com.example.music.MainActivity;
import com.example.music.R;
import com.example.music.adapter.TrackListAdapter;
import com.example.music.model.Track;
import com.example.music.service.PlayService;
import com.example.music.utils.QueryTools;
import com.example.music.utils.TrackUtils.Defs;
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

public class Praised extends Base implements Defs {

	private ListView mListView;
	private TrackListAdapter mAdapter;

	private MainActivity mActivity;
	private PlayService mServiceCallback;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mListView = (ListView) inflater.inflate(R.layout.fragment_all, null);
		display();
		return mListView;

	}

	private void display() {
		new TrackLoaderTask().execute();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onAttach(Activity activity) {
		mActivity = (MainActivity) activity;
		mServiceCallback = mActivity.getServiceCallback();
		Log.i("Praised--Ser", mServiceCallback + "");
		// Log.i("music", mServiceCallback.toString());
		super.onAttach(activity);
	}

	@Override
	public void onPraisedPressed() {
		new TrackLoaderTask().execute();
	}

	// 异步任务
	final class TrackLoaderTask extends AsyncTask<Void, Void, List<Track>> {

		@Override
		protected List<Track> doInBackground(Void... arg0) {
			return getTracks();
		}

		@Override
		protected void onPostExecute(List<Track> result) {
			Log.i("result.size()", result.size() + "");
			inflateListView(result);
			super.onPostExecute(result);
		}
	}

	List<Track> getTracks() {
		return new QueryTools(getActivity()).getListFrmDataBase(DB_TRACK_NAME, TB_PRAISED_NAME, 1, "TITLE DESC", false);
	}

	void inflateListView(final List<Track> tracks) {

		for (int i = 0; i < tracks.size(); i++) {
			Log.i("title==", tracks.get(i).getTitle());
		}
		Log.i("mAdapter==null", mAdapter + "");

		if (mAdapter == null) {
			mAdapter = new TrackListAdapter(tracks, getActivity(), ImageLoader.getInstance());
			mListView.setAdapter(mAdapter);
			mServiceCallback.setupPLayList(tracks);
			mListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

					mServiceCallback.setupPLayList(tracks);
					mServiceCallback.setCurrentPosition(position);
					mServiceCallback.playTrack(position);

				}
			});
		} else {
			mAdapter.updateList(tracks);
			mListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

					mServiceCallback.setupPLayList(tracks);
					mServiceCallback.setCurrentPosition(position);
					mServiceCallback.playTrack(position);

				}
			});
		}

	}

}
