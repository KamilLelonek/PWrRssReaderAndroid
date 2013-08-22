package pwr.rss.reader;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;

public abstract class InstructionActivity extends Activity {
	private int step = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getFirstLayout());
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		manageInstructionIfUserClickOnScrenn(event);
		return super.onTouchEvent(event);
	}
	
	private void manageInstructionIfUserClickOnScrenn(MotionEvent event) {
		if (userClicksOnInstruction(event)) {
			showSecondStepOrFinish();
		}
	}
	
	private boolean userClicksOnInstruction(MotionEvent event) {
		return event.getAction() == MotionEvent.ACTION_DOWN;
	}
	
	private void showSecondStepOrFinish() {
		if (isFirstStep()) {
			showSecondStep();
		}
		else {
			closeInstruction();
		}
	}
	
	private void showSecondStep() {
		setContentView(getSecondLayout());
	}
	
	private boolean isFirstStep() {
		return step++ == 0;
	}
	
	private void closeInstruction() {
		this.finish();
	}
	
	@Override
	public void onBackPressed() {
		showSecondStepOrFinish();
	}
	
	protected abstract int getFirstLayout();
	
	protected abstract int getSecondLayout();
}