package pwr.rss.reader.test;

import pwr.rss.reader.FeedsListActivity;
import pwr.rss.reader.R;
import pwr.rss.reader.SplashScreenActivity;
import android.app.Activity;
import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.View;

import com.jayway.android.robotium.solo.Solo;

public class SplashScreenActivityTest extends ActivityInstrumentationTestCase2<SplashScreenActivity> {
	private static final String TAG_TEST_SplashScreenActivityTest = "SplashScreenActivityTest";
	private static final int SPLASH_SCREEN_DELCARED_DURATION = SplashScreenActivity.SPLASH_SCREEN_DISPLAY_DURATION;
	
	private Solo solo;
	private Activity FeedsListActivity;
	
	public SplashScreenActivityTest() {
		super(SplashScreenActivity.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		Instrumentation instrumentation = getInstrumentation();
		this.FeedsListActivity = getActivity();
		this.solo = new Solo(instrumentation, FeedsListActivity);
	}
	
	/******************
	 ****** TESTS *****
	 ******************/
	
	public void testSplashScreenToFeedsListActivityFlowAfterTime() {
		long start = getCurrentTimeMillis();
		waitForFeedsListActivity();
		long elapsed = getDifferenceBetweenNowAndProvidedInMillis(start);
		
		checkIfSplashScreenLastsAsDeclared(elapsed);
		checkIfCurrentActivityIsFeedsListActivity();
	}
	
	public void testSplashScreenToFeedsListActivityFlowAfterClick() {
		clickOnSplashScreen();
		checkIfCurrentActivityIsFeedsListActivity();
	}
	
	public void testOrientationChanges() {
		rotateScreen();
		waitForFeedsListActivityNoLongerThanDecalredTime();
		checkIfCurrentActivityIsFeedsListActivity();
	}
	
	/******************
	 ***** HELPERS ****
	 ******************/
	
	private long getCurrentTimeMillis() {
		return System.currentTimeMillis();
	}
	
	private void waitForFeedsListActivity() {
		solo.waitForActivity(FeedsListActivity.class);
	}
	
	private long getDifferenceBetweenNowAndProvidedInMillis(long time) {
		return getCurrentTimeMillis() - time;
	}
	
	private void checkIfSplashScreenLastsAsDeclared(long elapsedMilliseconds) {
		Log.d(TAG_TEST_SplashScreenActivityTest, String.valueOf(elapsedMilliseconds));
		assertTrue(isGreaterOrEqual(elapsedMilliseconds, SPLASH_SCREEN_DELCARED_DURATION));
	}
	
	private void checkIfCurrentActivityIsFeedsListActivity() {
		solo.assertCurrentActivity("Current activity should be FeedsListActivity", FeedsListActivity.class);
	}
	
	private boolean isGreaterOrEqual(long value, int downBoundary) {
		return value >= downBoundary;
	}
	
	private void clickOnSplashScreen() {
		View splashScreenView = getSplashScreenView();
		solo.clickOnView(splashScreenView, true);
	}
	
	private View getSplashScreenView() {
		return FeedsListActivity.findViewById(R.id.splashScreen);
	}
	
	private void rotateScreen() {
		solo.setActivityOrientation(Solo.LANDSCAPE);
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.setActivityOrientation(Solo.LANDSCAPE);
		solo.setActivityOrientation(Solo.PORTRAIT);
	}
	
	private void waitForFeedsListActivityNoLongerThanDecalredTime() {
		solo.waitForActivity(FeedsListActivity.class, SPLASH_SCREEN_DELCARED_DURATION);
	}
	
	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
	}
}