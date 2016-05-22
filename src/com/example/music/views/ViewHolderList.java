package com.example.music.views;

import com.example.music.R;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 提高ListView填充效率的帮助类
 */
public class ViewHolderList {

	public TextView mTitleView;
	public TextView mArtistView;
	public ImageView mArtView;
	public TextView mDuration;

	public ViewHolderList(View rootView) {
		this.mTitleView = (TextView) rootView.findViewById(R.id.line_one);
		this.mArtistView = (TextView) rootView.findViewById(R.id.line_two);
		this.mArtView = (ImageView) rootView.findViewById(R.id.art);
		this.mDuration = (TextView) rootView.findViewById(R.id.tv_duration);
	}
}
