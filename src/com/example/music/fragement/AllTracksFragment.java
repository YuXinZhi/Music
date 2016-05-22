package com.example.music.fragement;

import java.util.List;

import com.example.music.MainActivity;
import com.example.music.R;
import com.example.music.model.Track;
import com.example.music.utils.TrackUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhntd.nick.rocklite.loaders.TrackListAdapter;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class AllTracksFragment extends Base {
	private ListView mListView;
	//private TrackListAdapter mAdapter;

	// fragment依赖的Activity
	private MainActivity mActivity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mListView = (ListView) inflater.inflate(R.layout.fragment_all, null);
		return mListView;
	}

	@Override
	public void onAttach(Activity activity) {
		mActivity = (MainActivity) activity;

		super.onAttach(activity);
	}

	@Override
	public void onPraisedPressed() {

	}

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

	private void inflateListView(List<Track> tracks) {
		mAdapter = new TrackListAdapter(tracks, getActivity(), ImageLoader.getInstance());
		mListView.setAdapter(mAdapter);

		mServiceCallback.setupPLayList(tracks);

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


			}
		});
	}

	List<Track> getTracks() {
		return TrackUtils.getTrackList(getActivity());
	}

}
