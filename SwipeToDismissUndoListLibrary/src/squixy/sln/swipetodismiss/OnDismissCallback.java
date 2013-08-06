package squixy.sln.swipetodismiss;

import android.widget.AbsListView;
import android.widget.ListView;

/**
 * The callback interface used by {@link SwipeDismissListViewTouchListener} to
 * inform its client about a successful dismissal of one or more list item
 * positions.
 */
public interface OnDismissCallback {
	
	/**
	 * Called when the user has indicated they she would like to dismiss one or
	 * more list item positions.
	 * 
	 * @param listView The originating {@link ListView}.
	 * @param position The position of the item to dismiss.
	 */
	Undoable onDismiss(AbsListView listView, int position);
}