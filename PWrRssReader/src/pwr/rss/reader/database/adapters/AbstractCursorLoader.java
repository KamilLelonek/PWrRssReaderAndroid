package pwr.rss.reader.database.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;

abstract public class AbstractCursorLoader extends AsyncTaskLoader<Cursor> {
	private Cursor lastCursor = null;
	
	public AbstractCursorLoader(Context context) {
		super(context);
	}
	
	abstract protected Cursor buildCursor();
	
	/**
	 * Runs on a worker thread, loading in our data. Delegates the real work to
	 * concrete subclass' buildCursor() method.
	 */
	@Override
	public Cursor loadInBackground() {
		Cursor cursor = buildCursor();
		fillCursor(cursor);
		return cursor;
	}
	
	private void fillCursor(Cursor cursor) {
		if (isNotNull(cursor)) {
			// Ensure the cursor window is filled
			cursor.getCount();
		}
	}
	
	/**
	 * Runs on the UI thread, routing the results from the background thread to
	 * whatever is using the Cursor (e.g., a CursorAdapter).
	 */
	@Override
	public void deliverResult(Cursor cursor) {
		if (isReset()) {
			// An async query came in while the loader is stopped
			closeCursor(cursor);
			return;
		}
		
		Cursor oldCursor = lastCursor;
		lastCursor = cursor;
		
		if (isStarted()) {
			super.deliverResult(cursor);
		}
		
		if (!equals(cursor, oldCursor)) {
			closeCursor(oldCursor);
		}
	}
	
	private boolean equals(Cursor cursor, Cursor oldCursor) {
		return oldCursor == cursor;
	}
	
	/**
	 * Starts an asynchronous load of the list data. When the result is ready
	 * the callbacks will be called on the UI thread. If a previous load has
	 * been completed and is still valid the result may be passed to the
	 * callbacks immediately.
	 * 
	 * Must be called from the UI thread.
	 */
	@Override
	protected void onStartLoading() {
		deliverResultIfIsNotNull();
		forceLoadIfIsPossible();
	}
	
	private void deliverResultIfIsNotNull() {
		if (isNotNull(lastCursor)) {
			deliverResult(lastCursor);
		}
	}
	
	private void forceLoadIfIsPossible() {
		if (shouldBeForceLoaded()) {
			forceLoad();
		}
	}
	
	private boolean shouldBeForceLoaded() {
		return takeContentChanged() || lastCursor == null;
	}
	
	/**
	 * Must be called from the UI thread, triggered by a call to stopLoading().
	 */
	@Override
	protected void onStopLoading() {
		// Attempt to cancel the current load task if possible.
		cancelLoad();
	}
	
	/**
	 * Must be called from the UI thread, triggered by a call to cancel(). Here,
	 * we make sure our Cursor is closed, if it still exists and is not already
	 * closed.
	 */
	@Override
	public void onCanceled(Cursor cursor) {
		closeCursor(cursor);
	}
	
	private void closeCursor(Cursor cursor) {
		if (canBeClosed(cursor)) {
			cursor.close();
		}
	}
	
	private boolean canBeClosed(Cursor cursor) {
		return isNotNull(cursor) && isOpen(cursor);
	}
	
	private boolean isNotNull(Cursor cursor) {
		return cursor != null;
	}
	
	private boolean isOpen(Cursor cursor) {
		return !cursor.isClosed();
	}
	
	/**
	 * Must be called from the UI thread, triggered by a call to reset(). Here,
	 * we make sure our Cursor is closed, if it still exists and is not already
	 * closed.
	 */
	@Override
	protected void onReset() {
		super.onReset();
		
		// Ensure the loader is stopped
		onStopLoading();
		
		// Close and GC last cursor
		closeCursor(lastCursor);
		lastCursor = null;
	}
}