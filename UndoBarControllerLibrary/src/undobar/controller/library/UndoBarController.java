package undobar.controller.library;

import java.io.Serializable;

import android.os.Handler;
import android.view.View;

/**
 * UndoBar is simple (semi-transparent) message displayed (on the bottom of
 * screen) after performing particular action. e.g. in Gmail UndoBar is
 * displayed after deleting email. It allows us to undo performed action like
 * undelete file and so on.
 */
public class UndoBarController extends UndoBarControllerNotAutoCancelable {
	protected static final int HIDE_DELAY = 2 * 1000; // 2s
	
	private Handler hideHandler = new Handler();
	private Runnable hideRunnable = new Runnable() {
		@Override
		public void run() {
			hideUndoBar();
		}
	};
	
	public UndoBarController(View parentView) {
		super(parentView);
	}
	
	@Override
	protected void showUndoBar(Serializable token) {
		super.showUndoBar(token);
		restartScheduledRunnable();
	}
	
	private void restartScheduledRunnable() {
		cancelScheduledRunnable();
		postScheduledRunnable();
	}
	
	private void cancelScheduledRunnable() {
		hideHandler.removeCallbacks(hideRunnable);
	}
	
	private void postScheduledRunnable() {
		hideHandler.postDelayed(hideRunnable, HIDE_DELAY);
	}
}