package undobar.controller.library;

import java.io.Serializable;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * UndoBar is simple (semi-transparent) message displayed (on the bottom of
 * screen) after performing particular action. e.g. in Gmail UndoBar is
 * displayed after deleting email. It allows us to undo performed action like
 * undelete file and so on.
 */
public class UndoBarControllerNotAutoCancelable implements OnClickListener {
	private static final int GRAVITY_BOTTOM_CENTER = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
	private static final String STATE_TOKEN = "undo_token";
	
	private View parentView;
	private View undoBarView;
	private TextView undoBarMessage;
	private Button undoBarButton;
	
	private PopupWindow undoPopup;
	
	private UndoBarListener undoListener;
	private Serializable undoToken;
	
	private static final int BOTTOM_OFFSET_X = 0;
	private static int BOTTOM_OFFSET_Y;
	
	public UndoBarControllerNotAutoCancelable(View parentView) {
		this.parentView = parentView;
		initializeComponents(parentView);
		measureUndoBar(parentView);
	}
	
	public void showUndoBar(Serializable token, int messageResId) {
		setUndoBarMessage(messageResId);
		showUndoBar(token);
	}
	
	public void showUndoBar(Serializable token, String message) {
		setUndoBarMessage(message);
		showUndoBar(token);
	}
	
	public void registerUndoBarListener(UndoBarListener undoBarListener) {
		this.undoListener = undoBarListener;
	}
	
	public void unregisterUndoBarListener(UndoBarListener undoBarListener) {
		this.undoListener = null;
	}
	
	public void setUndoBarMessage(int messageResId) {
		undoBarMessage.setText(messageResId);
	}
	
	public void setUndoBarMessage(String message) {
		undoBarMessage.setText(message);
	}
	
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(STATE_TOKEN, undoToken);
	}
	
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			undoToken = savedInstanceState.getSerializable(STATE_TOKEN);
			
			if (undoToken != null) {
				showUndoBar(undoToken);
			}
		}
	}
	
	private void initializeComponents(View parentView) {
		LayoutInflater inflater = LayoutInflater.from(parentView.getContext());
		
		this.undoBarView = inflater.inflate(R.layout.undo_bar_view, null);
		this.undoBarButton = (Button) undoBarView.findViewById(R.id.undoBarButton);
		this.undoBarMessage = (TextView) this.undoBarView.findViewById(R.id.undoBarTextView);
		this.undoBarButton.setOnClickListener(this);
		
		this.undoPopup = new PopupWindow(undoBarView);
		this.undoPopup.setAnimationStyle(R.style.fade_animation);
	}
	
	private void measureUndoBar(View parentView) {
		DisplayMetrics displayMetrics = parentView.getContext().getResources().getDisplayMetrics();
		float density = displayMetrics.density;
		BOTTOM_OFFSET_Y = (int) (density * 15);
		int width = getWidth(density, displayMetrics);
		this.undoPopup.setWidth(width);
		this.undoPopup.setHeight((int) (density * 56));
	}
	
	private int getWidth(float density, DisplayMetrics displayMetrics) {
		int xdensity = (int) (displayMetrics.widthPixels / density);
		if (xdensity < 300) return (int) (density * 280);
		else if (xdensity < 350) return (int) (density * 300);
		else if (xdensity < 500) return (int) (density * 330);
		else return (int) (density * 450);
	}
	
	protected void showUndoBar(Serializable token) {
		this.undoToken = token;
		this.undoPopup.showAtLocation(parentView, GRAVITY_BOTTOM_CENTER, BOTTOM_OFFSET_X, BOTTOM_OFFSET_Y);
	}
	
	public void hideUndoBar() {
		if (undoPopup.isShowing()) {
			undoPopup.dismiss();
		}
	}
	
	@Override
	public void onClick(View v) {
		hideUndoBar();
		if (undoListener != null) {
			undoListener.onUndo(undoToken);
		}
	}
}