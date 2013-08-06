package squixy.sln.swipetodismiss;

import android.view.View;

public class PendingDismissData implements Comparable<PendingDismissData> {
	public final int position;
	public final View view;
	
	public PendingDismissData(int position, View view) {
		this.position = position;
		this.view = view;
	}
	
	@Override
	public int compareTo(PendingDismissData other) {
		return other.position - position;
	}
}