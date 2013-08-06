package undobar.controller.library;

import java.io.Serializable;

public interface UndoBarListener {
	void onUndo(Serializable token);
}