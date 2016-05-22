package com.example.music.receiver;

import com.example.music.service.PlayService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TrackPreviousReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent newIntent = new Intent();
		newIntent.setAction(PlayService.ACTION_PREVIOUS_TRACK);
		context.sendBroadcast(newIntent);
	}

}
