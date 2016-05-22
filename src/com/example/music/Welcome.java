package com.example.music;

import com.dk.animation.SwitchAnimationUtil;
import com.dk.animation.SwitchAnimationUtil.AnimationType;
import com.example.music.fonts.FontsFactory;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

public class Welcome extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		init();
		// 加载动画
		new SwitchAnimationUtil().startAnimation(getWindow().getDecorView(), AnimationType.ROTATE);
		FontsFactory.createRoboto(this);

		// 1秒的缓冲
		new CountDownTimer(1000, 1000) {

			@Override
			public void onTick(long arg0) {

			}

			@Override
			public void onFinish() {
				// 跳转到主页面
				Intent intent = new Intent(Welcome.this, MainActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
				Welcome.this.finish();
			}
		}.start();

	}

	// 初始化欢迎页面字体
	private void init() {
		Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/hero.ttf");
		TextView splash = (TextView) findViewById(R.id.splash_text);
		splash.setTypeface(typeFace);
		splash.setText("欢      迎");
	}
}
