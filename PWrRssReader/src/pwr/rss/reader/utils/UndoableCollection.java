package pwr.rss.reader.utils;

import java.io.Serializable;
import java.util.ArrayList;

public class UndoableCollection implements Serializable {
	private static final long serialVersionUID = 6927862081561376263L;
	
	public static enum Action {
		DISMISSED, READ
	}
	
	public final ArrayList<? extends Serializable> list;
	public final Action action;
	
	public UndoableCollection(ArrayList<? extends Serializable> list, Action action) {
		this.list = list;
		this.action = action;
	}
}