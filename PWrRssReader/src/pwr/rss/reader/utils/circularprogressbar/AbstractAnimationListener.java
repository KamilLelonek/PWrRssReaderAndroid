package pwr.rss.reader.utils.circularprogressbar;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

/**
 * Dummy class to override unused methods in subclasses and provide only
 * relevant functions.
 */
public abstract class AbstractAnimationListener implements AnimatorListener, AnimatorUpdateListener {
	
	@Override
	public abstract void onAnimationUpdate(ValueAnimator arg0);
	
	@Override
	public abstract void onAnimationCancel(Animator arg0);
	
	@Override
	public void onAnimationEnd(Animator arg0) {}
	
	@Override
	public void onAnimationRepeat(Animator arg0) {}
	
	@Override
	public void onAnimationStart(Animator arg0) {}
}