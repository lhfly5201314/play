package com.example.qq66.activity;

import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.example.qq66.Const;
import com.example.qq66.R;

/**
 * 
 * @author wzy 2015-9-8
 *
 */
public class SplashActivity extends BaseActivity {
	
	private ImageView iv_splash;
	private volatile boolean isEntered;
	private static final int DURATIONMILLIS = 3000;
	
	@Override
	public void initView() {
		setContentView(R.layout.activity_splash);
		iv_splash = (ImageView) findViewById(R.id.iv_splash);
		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		alphaAnimation.setDuration(DURATIONMILLIS);
		iv_splash.setAnimation(alphaAnimation);
		alphaAnimation.start();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				SystemClock.sleep(DURATIONMILLIS);
				enterLoginActivity();
			}
		}).start();
	}

	//为什么
	private synchronized void enterLoginActivity() {
		if (isEntered) {
			return;
		}
		isEntered = true;
		mHandler.sendEmptyMessage(Const.MSG_SPLASH);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		enterLoginActivity();
		return true;
	}

}
