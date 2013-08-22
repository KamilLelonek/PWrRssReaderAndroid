package squixy.sln.swipetodismiss;

import static com.nineoldandroids.view.ViewHelper.setAlpha;
import static com.nineoldandroids.view.ViewHelper.setTranslationX;
import static com.nineoldandroids.view.ViewPropertyAnimator.animate;

import java.io.Serializable;

import undobar.controller.library.UndoBarController;
import undobar.controller.library.UndoBarListener;
import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ValueAnimator;

/**
 * A {@link android.view.View.OnTouchListener} that makes the list items in a
 * {@link ListView} dismissable. {@link ListView} is given special treatment
 * because by default it handles touches for its list items... i.e. it's in
 * charge of drawing the pressed state (the list selector), handling list item
 * clicks, etc.
 * 
 * Read the README file for a detailed explanation on how to use this class.
 */
public final class SwipeDismissList implements View.OnTouchListener, OnScrollListener, UndoBarListener {
	private static final int HIDE_DELAY = 2 * 1000;
	
	// Cached ViewConfiguration and system-wide constant values
	private int mSlop;
	private int mMinFlingVelocity;
	private int mMaxFlingVelocity;
	private long mAnimationTime;
	private int mViewWidth;
	
	// Fixed properties
	private ListView listView;
	private OnDismissCallback callback;
	
	// Transient properties
	private PendingDismissData pendingDismissData;
	private int mDismissAnimationRefCount;
	private VelocityTracker mVelocityTracker;
	private float mDownX;
	private View swipedListItem;
	private int mDownPosition;
	private boolean isSwiping;
	private boolean isPaused;
	private boolean isBlocked;
	
	private Undoable undoAction;
	private int messageResourceId;
	private Handler hideUndoPopupHandler;
	private UndoBarController undoBarController;
	private Resources resources;
	
	/**
	 * Constructs a new swipe-to-dismiss touch listener for the given list view.
	 * 
	 * @param listView The list view whose items should be dismissable.
	 * @param callback The callback to trigger when the user has indicated that
	 *        she would like to dismiss one or more list items.
	 */
	
	public SwipeDismissList(ListView listView, OnDismissCallback callback, int messageResourceId) {
		this.resources = listView.getResources();
		this.messageResourceId = messageResourceId;
		this.listView = listView;
		this.callback = callback;
		this.undoBarController = new UndoBarController(listView);
		this.undoBarController.registerUndoBarListener(this);
		this.hideUndoPopupHandler = new HideUndoPopupHandler();
		
		configureListView();
		configureAnimationProperties();
	}
	
	private void configureAnimationProperties() {
		ViewConfiguration vc = ViewConfiguration.get(listView.getContext());
		mSlop = vc.getScaledTouchSlop();
		mMinFlingVelocity = vc.getScaledMinimumFlingVelocity();
		mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
		mAnimationTime = resources.getInteger(android.R.integer.config_shortAnimTime);
	}
	
	private void configureListView() {
		listView.setOnTouchListener(this);
		listView.setOnScrollListener(this);
	}
	
	/**
	 * If a scroll listener is already assigned, the caller should still pass
	 * scroll changes through to this listener. This will ensure that this
	 * {@link SwipeDismissListViewTouchListener} is paused during list view
	 * scrolling.</p>
	 * 
	 * @see {@link SwipeDismissListViewTouchListener}
	 */
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		isPaused = listIsScrolling(scrollState);
	}
	
	private boolean listIsScrolling(int scrollState) {
		return scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;
	}
	
	public void pause() {
		isBlocked = true;
	}
	
	public void resume() {
		isBlocked = false;
	}
	
	private View getClickedListItem(MotionEvent motionEvent) {
		int childCount = countListItems();
		int[] listViewCoords = getListCoordinatesOnScreen();
		int x = calculateXposition(motionEvent, listViewCoords);
		int y = calculateYposition(motionEvent, listViewCoords);
		
		Rect listItemRect = new Rect();
		View listItem;
		for (int i = 0; i < childCount; i++) {
			listItem = getListItem(i);
			fillRectWithItem(listItemRect, listItem);
			if (listItemMatchesCoordinates(x, y, listItemRect)) return listItem;
		}
		return null;
	}
	
	private int countListItems() {
		return listView.getChildCount();
	}
	
	private int[] getListCoordinatesOnScreen() {
		int[] listViewCoords = new int[2];
		listView.getLocationOnScreen(listViewCoords);
		return listViewCoords;
	}
	
	private int calculateXposition(MotionEvent motionEvent, int[] listViewCoords) {
		return (int) motionEvent.getRawX() - listViewCoords[0];
	}
	
	private int calculateYposition(MotionEvent motionEvent, int[] listViewCoords) {
		return (int) motionEvent.getRawY() - listViewCoords[1];
	}
	
	private View getListItem(int i) {
		return listView.getChildAt(i);
	}
	
	private void fillRectWithItem(Rect rect, View child) {
		child.getHitRect(rect);
	}
	
	private boolean listItemMatchesCoordinates(int x, int y, Rect rect) {
		return rect.contains(x, y);
	}
	
	@Override
	public boolean onTouch(View touchedView, MotionEvent motionEvent) {
		hideUndoPopupHandler.sendEmptyMessageAtTime(0, HIDE_DELAY);
		if (isBlocked) return false;
		
		mViewWidth = listView.getWidth();
		
		switch (motionEvent.getActionMasked()) {
			case MotionEvent.ACTION_DOWN: {
				if (isPaused) return false;
				
				swipedListItem = getClickedListItem(motionEvent);
				moveListItem(motionEvent);
				
				touchedView.onTouchEvent(motionEvent);
				return true;
			}
			case MotionEvent.ACTION_MOVE: {
				if (mVelocityTracker == null || isPaused) return false;
				
				mVelocityTracker.addMovement(motionEvent);
				float deltaX = motionEvent.getRawX() - mDownX;
				// Only start swipe in correct direction
				if (deltaX < 0) {
					if (Math.abs(deltaX) > mSlop) {
						isSwiping = true;
						cancelListItemClick(motionEvent);
					}
				}
				else {
					// If we swiped into wrong direction, act like this was the new touch down point
					mDownX = motionEvent.getRawX();
					deltaX = 0;
				}
				
				if (isSwiping) {
					setTranslationX(swipedListItem, deltaX);
					setAlpha(swipedListItem, Math.max(0f, Math.min(1f, 1f - 2f * Math.abs(deltaX) / mViewWidth)));
					return true;
				}
				break;
			}
			case MotionEvent.ACTION_UP: {
				if (mVelocityTracker == null) return false;
				
				float deltaX = motionEvent.getRawX() - mDownX;
				
				mVelocityTracker.addMovement(motionEvent);
				mVelocityTracker.computeCurrentVelocity(1000);
				
				float velocityX = Math.abs(mVelocityTracker.getXVelocity());
				float velocityY = Math.abs(mVelocityTracker.getYVelocity());
				
				boolean dismiss = false;
				boolean dismissRight = false;
				
				if (Math.abs(deltaX) > mViewWidth / 2 && isSwiping) {
					dismiss = true;
					dismissRight = deltaX > 0;
				}
				else if (mMinFlingVelocity <= velocityX && velocityX <= mMaxFlingVelocity && velocityY < velocityX
					&& isSwiping && (mVelocityTracker.getXVelocity() < 0) && deltaX >= mViewWidth * 0.2f) {
					dismiss = true;
					dismissRight = mVelocityTracker.getXVelocity() > 0;
				}
				if (dismiss) {
					final View cachedListItemView = swipedListItem;
					final int cachedListItemPosition = mDownPosition;
					
					++mDismissAnimationRefCount;
					animate(swipedListItem).translationX(dismissRight ? mViewWidth : -mViewWidth).alpha(0)
						.setDuration(mAnimationTime).setListener(new AnimatorListenerAdapter() {
							@Override
							public void onAnimationEnd(Animator animation) {
								performDismiss(cachedListItemView, cachedListItemPosition);
							}
						});
				}
				else {
					animate(swipedListItem).translationX(0).alpha(1).setDuration(mAnimationTime).setListener(null);
				}
				clearData();
				break;
			}
		}
		return false;
	}
	
	private void cancelListItemClick(MotionEvent motionEvent) {
		listView.requestDisallowInterceptTouchEvent(true);
		MotionEvent cancelEvent = getCancelEvent(motionEvent);
		listView.onTouchEvent(cancelEvent);
		cancelEvent.recycle();
	}
	
	private MotionEvent getCancelEvent(MotionEvent motionEvent) {
		MotionEvent cancelEvent = MotionEvent.obtain(motionEvent);
		cancelEvent.setAction(MotionEvent.ACTION_CANCEL
			| (motionEvent.getActionIndex() << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
		return cancelEvent;
	}
	
	private void clearData() {
		mVelocityTracker = null;
		mDownX = 0;
		swipedListItem = null;
		mDownPosition = ListView.INVALID_POSITION;
		isSwiping = false;
	}
	
	private void moveListItem(MotionEvent motionEvent) {
		if (swipedListItem != null) {
			mDownX = motionEvent.getRawX();
			mDownPosition = listView.getPositionForView(swipedListItem);
			
			mVelocityTracker = VelocityTracker.obtain();
			mVelocityTracker.addMovement(motionEvent);
			mVelocityTracker.recycle();
		}
	}
	
	private void performDismiss(final View dismissView, final int dismissPosition) {
		final int originalHeight = dismissView.getHeight();
		
		ValueAnimator animator = ValueAnimator.ofInt(originalHeight, 1).setDuration(mAnimationTime);
		animator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				--mDismissAnimationRefCount;
				if (mDismissAnimationRefCount == 0) { // If no active animations, process pending dismiss
					if (undoAction != null) {
						undoAction.discard();
					}
					undoAction = null;
					undoAction = callback.onDismiss(listView, pendingDismissData.position);
					
					if (undoAction != null) {
						undoBarController.showUndoBar(null, messageResourceId);
					}
					setAlpha(pendingDismissData.view, 1f);
					setTranslationX(pendingDismissData.view, 0);
					ViewGroup.LayoutParams lp = pendingDismissData.view.getLayoutParams();
					lp.height = originalHeight;
					pendingDismissData.view.setLayoutParams(lp);
					pendingDismissData = null;
				}
			}
		});
		
		final ViewGroup.LayoutParams lp = dismissView.getLayoutParams();
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				lp.height = (Integer) valueAnimator.getAnimatedValue();
				dismissView.setLayoutParams(lp);
			}
		});
		
		pendingDismissData = new PendingDismissData(dismissPosition, dismissView);
		
		animator.start();
	}
	
	/**
	 * Handler used to hide the undo popup after a special delay.
	 */
	@SuppressLint("HandlerLeak")
	private class HideUndoPopupHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			discardUndo();
		}
	}
	
	@Override
	public void onUndo(Serializable token) {
		if (undoAction != null) {
			undoAction.onDismissUndo();
			undoAction = null;
			hideUndoBar();
		}
	}
	
	public void discardUndo() {
		if (undoAction != null) {
			undoAction.discard();
			undoAction = null;
			hideUndoBar();
		}
	}
	
	private void hideUndoBar() {
		undoBarController.hideUndoBar();
	}
}