package squixy.sln.swipetodismiss;

/**
 * An implementation of this abstract class must be returned by the
 * {@link OnDismissCallback#onDismiss(android.widget.ListView, int)} method, if
 * the user should be able to undo that dismiss. If the action will be undone by
 * the user {@link #undo()} will be called. That method should undo the previous
 * deletion of the item and add it back to the adapter. Read the README file for
 * more details. If you implement the {@link #getTitle()} method, the undo popup
 * will show an individual title for that item. Otherwise the default title (set
 * via {@link #setUndoString(java.lang.String)}) will be shown.
 */
public abstract class Undoable {
	/**
	 * Undoes the dismission.
	 */
	public abstract void onDismissUndo();
	
	/**
	 * Will be called when this Undoable won't be able to undo anymore, meaning
	 * the undo popup has disappeared from the screen.
	 */
	public void discard() {};
}