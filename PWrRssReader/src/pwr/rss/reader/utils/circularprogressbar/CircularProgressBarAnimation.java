package pwr.rss.reader.utils.circularprogressbar;

import pwr.rss.reader.R;
import android.app.Activity;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;

/**
 * Adapter class to hide CircularProgressBar implementation and provide simple
 * API for user.
 */
public class CircularProgressBarAnimation extends AbstractAnimationListener {
	private static final int PROGRESS_MAX_VALUE = 1;
	private static final int PROGRESS_DURATION = 2000;
	private static final String PROGRESS_NAME = "progress";
	
	private final float PROGRESS_UPDATES;
	private final float PROGRESS_STEP;
	private float progressLimit;
	
	private final CircularProgressBar progressBar;
	private ObjectAnimator progressBarAnimator;
	
	public CircularProgressBarAnimation(Activity activity, int progressUpdates) {
		PROGRESS_UPDATES = progressUpdates;
		PROGRESS_STEP = PROGRESS_MAX_VALUE / PROGRESS_UPDATES;
		progressLimit = PROGRESS_STEP;
		progressBar = (CircularProgressBar) activity.findViewById(R.id.circularProgressBar);
	}
	
	@Override
	public void onAnimationUpdate(ValueAnimator animation) {
		stopIfLimitReached();
	}
	
	private void stopIfLimitReached() {
		if (hasProgressReachedLimit()) {
			stopAnimation();
		}
	}
	
	private boolean hasProgressReachedLimit() {
		float progress = getProgress();
		return progress >= progressLimit;
	}
	
	private void stopAnimation() {
		progressBarAnimator.cancel();
	}
	
	private float getProgress() {
		return (Float) progressBarAnimator.getAnimatedValue();
	}
	
	@Override
	public void onAnimationCancel(Animator animation) {
		increaseProgressLimit();
	}
	
	private void increaseProgressLimit() {
		progressLimit += PROGRESS_STEP;
		resetLimitWhenIsOverMax();
	}
	
	private void resetLimitWhenIsOverMax() {
		if (hasLimitExceedMax()) {
			resetProgressLimit();
		}
	}
	
	private boolean hasLimitExceedMax() {
		return progressLimit > PROGRESS_MAX_VALUE;
	}
	
	private void resetProgressLimit() {
		progressLimit = PROGRESS_STEP;
	}
	
	public void animate() {
		initializeAnimator();
		startAnimation();
	}
	
	private void startAnimation() {
		progressBarAnimator.start();
	}
	
	private void initializeAnimator() {
		progressBarAnimator = getProgressBarAnimator();
		progressBarAnimator.setDuration(PROGRESS_DURATION);
		progressBarAnimator.addListener(this);
		progressBarAnimator.addUpdateListener(this);
	}
	
	private ObjectAnimator getProgressBarAnimator() {
		return ObjectAnimator.ofFloat(progressBar, PROGRESS_NAME, PROGRESS_MAX_VALUE);
	}
}