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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnScrollChangeListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

public class AllTracksFragment extends Base implements Defs {
	private ListView mListView;
	private TrackListAdapter mAdapter;

	// fragment依赖的Activity
	private MainActivity mActivity;

	private PlayService mServiceCallback;

	private PopupWindow popupwindow;

	private Track clickedTrack;
	private ImageView deleteImageView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mListView = (ListView) inflater.inflate(R.layout.fragment_all, null);
		mListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				dismissPopupWindow();

			}
		});
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
				dismissPopupWindow();
				if (mServiceCallback.getPlayList() != tracks)
					mServiceCallback.setupPLayList(tracks);
				mServiceCallback.setCurrentPosition(position);
				mServiceCallback.playTrack(position);
			}
		});
		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				// 获取到当前点击的item对象
				Object obj = mListView.getItemAtPosition(position);
				clickedTrack = (Track) obj;
				View contentView = View.inflate(getContext(), R.layout.popup_item, null);
				deleteImageView = (ImageView) contentView.findViewById(R.id.delete);
				deleteImageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						
						
						Toast.makeText(getContext(), "删除", 0).show();
						dismissPopupWindow();
					}
				});
				dismissPopupWindow();
				popupwindow = new PopupWindow(contentView, -2, -2);
				// 动画播放有一个前提条件： 窗体必须要有背景资源。 如果窗体没有背景，动画就播放不出来。
				popupwindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
				int[] location = new int[2];
				view.getLocationInWindow(location);
				popupwindow.showAtLocation(parent, Gravity.LEFT + Gravity.TOP, 100, location[1]);
				popupwindow.setOutsideTouchable(true);
				ScaleAnimation sa = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f, Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0.5f);
				sa.setDuration(200);
				AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
				aa.setDuration(200);
				AnimationSet set = new AnimationSet(false);
				set.addAnimation(aa);
				set.addAnimation(sa);
				contentView.startAnimation(set);

				return true;
			}
		});

		mListView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

			@Override
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {

			}
		});
	}

	// List<Track> getTracks() {
	// return TrackUtils.getTrackList(getActivity());
	// }

	List<Track> getTracks() {
		return new QueryTools(getActivity()).getListFrmDataBase(DB_TRACK_NAME, TB_ALLTRACKS_NAME, 1, "TITLE DESC",
				false);
	}

	public void dismissPopupWindow() {
		if (popupwindow != null && popupwindow.isShowing()) {
			popupwindow.dismiss();
			popupwindow = null;
		}
	}

	@Override
	public void onDestroyView() {
		dismissPopupWindow();
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		dismissPopupWindow();
		super.onDetach();
	}

	@Override
	public void onStop() {
		System.out.println("all----------onStop");
		dismissPopupWindow();
		super.onStop();
	}

	@Override
	public void onPause() {
		System.out.println("all----------onPause");
		dismissPopupWindow();
		super.onPause();
	}
}
