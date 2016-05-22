package com.example.music.adapter;

import java.util.List;

import com.example.music.model.Track;
import com.example.music.utils.TrackUtils;
import com.example.music.views.ViewHolderList;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 歌曲列表适配器
 */
public class TrackListAdapter extends BaseAdapter {

	// 歌曲列表
	private List<Track> mTracks;
	private ViewHolderList mViewHolderList;
	private Context mContext;

	private ImageLoader mImageLoader;

	@Override
	public int getCount() {
		return mTracks.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(0, null);
			mViewHolderList = new ViewHolderList(convertView);
			convertView.setTag(mViewHolderList);
		} else {
			mViewHolderList = (ViewHolderList) convertView.getTag();
		}
		
		//更新ListView列表条目中的内容
		Track track = mTracks.get(position);
		mViewHolderList.mTitleView.setText(track.getTitle());
		mViewHolderList.mArtistView.setText(track.getArtist());
		mViewHolderList.mDuration.setText(TrackUtils.makeTimeString(mContext, track.getDuration()));
		
		return null;
	}

}
