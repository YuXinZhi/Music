package com.example.music;

import com.example.music.utils.TrackUtils;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class Settings extends Activity {
	private TextView mSearchImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		mSearchImageView = (TextView) findViewById(R.id.search);
		mSearchImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				TrackUtils.searchAndAddTracksToDb(getApplicationContext());
			}
		});
	}
}
