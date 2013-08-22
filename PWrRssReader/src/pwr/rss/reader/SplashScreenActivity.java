package pwr.rss.reader;

import pwr.rss.reader.utils.ProgressWheel;
import pwr.rss.reader.web.ServiceManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

public class SplashScreenActivity extends Activity {
	public static final String TAG_SPLASH_SCREEN = "SplashScreen";
	public static final int SPLASH_SCREEN_DISPLAY_DURATION = 2 * 1000; // 2 seconds
	/*
	 * Action to be executed as some point in the future
	 */
	private final Runnable openMainActivityRequest = new Runnable() {
		@Override
		public void run() {
			finishAndStartMainActivity();
		}
	};
	private final Handler handler = new Handler(); // Scheduler for executing actions
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		
		initProgressBar();
		openDelayedMainActiviy();
	}
	
	/**
	 * Starts spinning progress bar in available in layout
	 */
	private void initProgressBar() {
		ProgressWheel progressWheel = (ProgressWheel) findViewById(R.id.progressBar);
		if (progressWheel != null) {
			progressWheel.spin();
		}
	}
	
	/**
	 * Dispatch action to start main activity after some time
	 */
	private void openDelayedMainActiviy() {
		handler.postDelayed(openMainActivityRequest,
			SPLASH_SCREEN_DISPLAY_DURATION);
	}
	
	/**
	 * When device is being rotated pending request should be disposed, because
	 * it will be created an another one in {@link #onCreate()} method
	 */
	@Override
	protected void onPause() {
		cancelPendingIntent();
		super.onPause();
	}
	
	/**
	 * This method is invoked when user tap on screen, because he don't want to
	 * wait specified time but want to immediately open {@link
	 * MainActivity.class}
	 */
	public void skipSplashScreen(View w) {
		Log.d(TAG_SPLASH_SCREEN, "Skipping splash screen...");
		cancelPendingIntent();
		finishAndStartMainActivity();
	}
	
	private void cancelPendingIntent() {
		handler.removeCallbacks(openMainActivityRequest);
	}
	
	private void finishAndStartMainActivity() {
		startServiceManager();
		startActivity(getMainActivityIntent());
		showInstructionOnFirstRun();
		finish();
	}
	
	private void showInstructionOnFirstRun() {
		ApplicationObject application = (ApplicationObject) getApplication();
		if (application.isFirstRun()) {
			application.setFirstRun();
			startActivity(getInstructionActivityIntent());
		}
	}
	
	/**
	 * Starts manager which handle download service and wakelock receiver
	 */
	private void startServiceManager() {
		startService(new Intent(SplashScreenActivity.this, ServiceManager.class));
	}
	
	private Intent getInstructionActivityIntent() {
		return getActivityIntent(FeedsListInstructionActivity.class);
	}
	
	private Intent getMainActivityIntent() {
		return getActivityIntent(FeedsListActivity.class);
	}
	
	private Intent getActivityIntent(Class<?> cls) {
		return new Intent(SplashScreenActivity.this, cls);
	}
	
	/**
	 * Overrides default action (finishing activity) and immediately redirects
	 * to {@link #openMainActivityRequest}
	 */
	@Override
	public void onBackPressed() {
		skipSplashScreen(null);
	}
}