package pwr.rss.reader.animations

import android.view.View
import android.view.animation.Transformation

class CollapseAnimation(animatingView: View) extends ResizingAnimation(animatingView) {
	override lazy val targetHeight = animatingView.getMeasuredHeight

	override protected def applyTransformation(interpolatedTime: Float, t: Transformation) = {
		animatingView.getLayoutParams.height =
			(targetHeight * (1 - interpolatedTime)).toInt

		animatingView.requestLayout
	}
}