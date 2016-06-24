package com.example.music.adapter;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.example.music.R;
import com.example.music.model.Track;
import com.example.music.utils.TrackUtils;
import com.example.music.views.ViewHolderList;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * 歌曲列表适配器
 */
public class TrackListAdapter extends BaseAdapter {

	// 歌曲列表
	private List<Track> mTracks;
	private ViewHolderList mViewHolderList;
	private Context mContext;

	private ImageLoader mImageLoader;

	static String mArtworkUri = "content://media/external/audio/albumart";
	private DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	private boolean isListScrolling = false;

	public TrackListAdapter(List<Track> mTracks, Context mContext, ImageLoader imageLoader) {
		super();
		Log.i("TrackListAdapter", mTracks.size()+"mContext="+mContext );
		this.mTracks = mTracks;
		this.mContext = mContext;
		this.mImageLoader = imageLoader;
		initLoader();
	}

	// 判断ListView是否在滑动,这是
	public void isListScrolling(boolean scrolling) {
		this.isListScrolling = scrolling;
		if (!scrolling)
			notifyDataSetChanged();
	}

	// 更新歌曲列表
	public void updateList(List<Track> tracks) {
		this.mTracks = tracks;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mTracks.size();
	}

	@Override
	public Object getItem(int position) {
		return mTracks.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.i("position mTracks.size()", mTracks.size() + "");
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_list_item, null);
			mViewHolderList = new ViewHolderList(convertView);
			convertView.setTag(mViewHolderList);
		} else {
			mViewHolderList = (ViewHolderList) convertView.getTag();
		}

		// 更新ListView列表条目中的内容
		Track track = mTracks.get(position);
		
		Log.i("track","mViewHolderList="+mViewHolderList+"mViewHolderList.mTitleView"+mViewHolderList.mTitleView);
		mViewHolderList.mTitleView.setText(track.getTitle());//null
		mViewHolderList.mArtistView.setText(track.getArtist());
		mViewHolderList.mDuration.setText(TrackUtils.makeTimeString(mContext, track.getDuration()));

		// 专辑封面加载
		String uri = mArtworkUri + File.separator + track.getAlbumId();
		// 在ListView停下来的时候加载
		if (!isListScrolling)
			mImageLoader.displayImage(uri, mViewHolderList.mArtView, options, animateFirstListener);
		return convertView;
	}

	void initLoader() {
		// 初始化加载设置，当没有图片时选择加载默认图片
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_artist)
				.showImageForEmptyUri(R.drawable.default_artist).showImageOnFail(R.drawable.default_artist)
				.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).build();
	}

	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				// 优先获取缓存的图片
				boolean firstDisplay = !displayedImages.contains(imageUri);
				FadeInBitmapDisplayer.animate(imageView, 1000);
				if (firstDisplay) {
					displayedImages.add(imageUri);
				}
			}
		}
	}

}
