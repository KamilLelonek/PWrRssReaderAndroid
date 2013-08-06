package pwr.rss.reader.utils.circularprogressbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

/**
 * Custom View class representing circular progress bar
 * 
 */
public class CircularProgressBar extends View {
	private static final String INSTNACE_STATE_NAME = "saved_state";
	private static final String INSTNACE_STATE_PROGRESS = "progressValue";
	private static final String INSTNACE_STATE_MARKER_PROGRESS = "marker_progress";
	
	private static final int MARKER_WIDTH = 20;
	private static final int CIRCLE_STROKE_WIDTH = 10;
	private static final int CIRCLE_STROKE_COLOR = 0xff0099cc;
	
	private RectF progressHeadRect = new RectF();
	private Paint progressHeadPaint;
	private float progressHeadPosX;
	private float progressHeadPosY;
	
	private Paint progressPaint;
	private Paint progressBackgroundPaint;
	private float progressValue;
	
	private RectF circleBounds = new RectF();
	
	private Paint markerPaint;
	private float markerProgress;
	
	private boolean isOverrdraw;
	
	/**
	 * The Horizontal inset calcualted in {@link #computeInsets(int, int)}
	 * depends on {@link #mGravity}.
	 */
	private int mHorizontalInset;
	
	/**
	 * The Vertical inset calcualted in {@link #computeInsets(int, int)} depends
	 * on {@link #mGravity}..
	 */
	private int mVerticalInset;
	
	/**
	 * The Translation offset x which gives us the ability to use our own
	 * coordinates system.
	 */
	private float mTranslationOffsetX;
	
	/**
	 * The Translation offset y which gives us the ability to use our own
	 * coordinates system.
	 */
	private float mTranslationOffsetY;
	
	public CircularProgressBar(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		
		progressBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		initializePaint(progressBackgroundPaint, Color.WHITE, Paint.Style.STROKE, CIRCLE_STROKE_WIDTH);
		
		markerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		initializePaint(markerPaint, Color.WHITE, Paint.Style.STROKE, CIRCLE_STROKE_WIDTH / 2);
		
		progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		initializePaint(progressPaint, CIRCLE_STROKE_COLOR, Paint.Style.STROKE, CIRCLE_STROKE_WIDTH);
		
		progressHeadPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		initializePaint(progressHeadPaint, CIRCLE_STROKE_COLOR, Paint.Style.FILL_AND_STROKE, CIRCLE_STROKE_WIDTH);
	}
	
	private void initializePaint(Paint paint, int color, Style style, int CIRCLE_STROKE_WIDTH) {
		paint.setColor(color);
		paint.setStyle(style);
		paint.setStrokeWidth(CIRCLE_STROKE_WIDTH);
	}
	
	@Override
	protected void onDraw(final Canvas canvas) {
		// All of our positions are using our internal coordinate system.
		// Instead of translating
		// them we let Canvas do the work for us.
		canvas.translate(mTranslationOffsetX, mTranslationOffsetY);
		
		final float progressRotation = getCurrentRotation();
		
		// draw the background
		if (!isOverrdraw) {
			canvas.drawArc(circleBounds, 270, -(360 - progressRotation), false, progressBackgroundPaint);
		}
		
		// draw the progressValue or a full circle if overdraw is true
		canvas.drawArc(circleBounds, 270, isOverrdraw ? 360 : progressRotation, false, progressPaint);
		
		// draw the marker at the correct rotated position
		final float markerRotation = getMarkerRotation();
		
		canvas.save();
		canvas.rotate(markerRotation - 90);
		canvas.drawLine((float) (progressHeadPosX + MARKER_WIDTH / 2 * 1.4), progressHeadPosY,
			(float) (progressHeadPosX - MARKER_WIDTH / 2 * 1.4), progressHeadPosY, markerPaint);
		canvas.restore();
		
		// draw the progressHead square at the correct rotated position
		canvas.save();
		canvas.rotate(progressRotation - 90);
		// rotate the square by 45 degrees
		canvas.rotate(45, progressHeadPosX, progressHeadPosY);
		progressHeadRect.left = progressHeadPosX - MARKER_WIDTH / 3;
		progressHeadRect.right = progressHeadPosX + MARKER_WIDTH / 3;
		progressHeadRect.top = progressHeadPosY - MARKER_WIDTH / 3;
		progressHeadRect.bottom = progressHeadPosY + MARKER_WIDTH / 3;
		canvas.drawRect(progressHeadRect, progressHeadPaint);
		canvas.restore();
	}
	
	@Override
	protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
		final int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
		final int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
		final int min = Math.min(width, height);
		setMeasuredDimension(min, height);
		
		final float halfWidth = min * 0.5f;
		final float circleRadius = halfWidth - MARKER_WIDTH;
		
		circleBounds.set(-circleRadius, -circleRadius, circleRadius, circleRadius);
		
		progressHeadPosX = (float) (circleRadius * Math.cos(0));
		progressHeadPosY = (float) (circleRadius * Math.sin(0));
		computeInsets(width - min, height - min);
		
		mTranslationOffsetX = halfWidth + mHorizontalInset;
		mTranslationOffsetY = halfWidth + mVerticalInset;
	}
	
	@Override
	protected void onRestoreInstanceState(final Parcelable state) {
		if (state instanceof Bundle) {
			final Bundle bundle = (Bundle) state;
			setProgress(bundle.getFloat(INSTNACE_STATE_PROGRESS));
			setMarkerProgress(bundle.getFloat(INSTNACE_STATE_MARKER_PROGRESS));
			super.onRestoreInstanceState(bundle.getParcelable(INSTNACE_STATE_NAME));
		}
		else {
			super.onRestoreInstanceState(state);
		}
	}
	
	@Override
	protected Parcelable onSaveInstanceState() {
		final Bundle bundle = new Bundle();
		bundle.putParcelable(INSTNACE_STATE_NAME, super.onSaveInstanceState());
		bundle.putFloat(INSTNACE_STATE_PROGRESS, progressValue);
		bundle.putFloat(INSTNACE_STATE_MARKER_PROGRESS, markerProgress);
		return bundle;
	}
	
	@SuppressLint("NewApi")
	private void computeInsets(final int dx, final int dy) {
		final int layoutDirection;
		int absoluteGravity = Gravity.CENTER;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			layoutDirection = getLayoutDirection();
			absoluteGravity = Gravity.getAbsoluteGravity(Gravity.CENTER, layoutDirection);
		}
		
		switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
			case Gravity.LEFT:
				mHorizontalInset = 0;
				break;
			case Gravity.RIGHT:
				mHorizontalInset = dx;
				break;
			case Gravity.CENTER_HORIZONTAL:
			default:
				mHorizontalInset = dx / 2;
				break;
		}
		switch (absoluteGravity & Gravity.VERTICAL_GRAVITY_MASK) {
			case Gravity.TOP:
				mVerticalInset = 0;
				break;
			case Gravity.BOTTOM:
				mVerticalInset = dy;
				break;
			case Gravity.CENTER_VERTICAL:
			default:
				mVerticalInset = dy / 2;
				break;
		}
	}
	
	private float getCurrentRotation() {
		return 360 * progressValue;
	}
	
	private float getMarkerRotation() {
		return 360 * markerProgress;
	}
	
	public float getMarkerProgress() {
		return markerProgress;
	}
	
	public void setMarkerProgress(final float progress) {
		markerProgress = progress;
	}
	
	public float getProgress() {
		return progressValue;
	}
	
	public void setProgress(final float progress) {
		if (progressValue != progress) {
			
			progressValue = progress % 1.0f;
			isOverrdraw = progress >= 1;
			
			invalidate();
		}
	}
}