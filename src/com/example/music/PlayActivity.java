package com.example.music;

import com.example.music.service.PlayService;

import android.app.Activity;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class PlayActivity extends Activity implements OnClickListener {

	private PlayService mPlayService;
	private ServiceConnection mServiceConnection;

	private TextView mTitle;
	private TextView mArtist;
	private ImageView mBack;
	private ImageView mArt;

	private TextView mCurrentTime;
	private TextView mTotalTime;
	private SeekBar playSeekBar;

	private ImageView mMode;
	private ImageView mPrevious;
	private ImageView mPlay;
	private ImageView mNext;
	private ImageView mPraise;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play);
		findViews();
	}

	private void findViews() {
		mTitle = (TextView) findViewById(R.id.title);
		mArtist = (TextView) findViewById(R.id.artist);
		mArt = (ImageView) findViewById(R.id.art);
		mCurrentTime = (TextView) findViewById(R.id.currentTime);
		mTotalTime = (TextView) findViewById(R.id.totalTime);
		playSeekBar = (SeekBar) findViewById(R.id.playSeekBar);
		mMode = (ImageView) findViewById(R.id.modeBtn);
		mPraise = (ImageView) findViewById(R.id.favour);
		mPrevious = (ImageView) findViewById(R.id.preBtn);
		mPlay = (ImageView) findViewById(R.id.playBtn);
		mNext = (ImageView) findViewById(R.id.nextBtn);
		mBack = (ImageView) findViewById(R.id.back);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.preBtn:

			break;
		case R.id.playBtn:

			break;
		case R.id.nextBtn:

			break;
		case R.id.back:
			finish();
		default:
			break;
		}
	}

}
