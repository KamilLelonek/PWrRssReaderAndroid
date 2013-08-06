package pwr.rss.reader.animations

import android.view.View
import android.view.animation.Transformation
import android.view.ViewGroup.LayoutParams

class ExpandAnimation(animatingView: View) extends ResizingAnimation(animatingView) {
	override protected def applyTransformation(interpolatedTime: Float, t: Transformation) = {
		animatingView.getLayoutParams.height =
			if (interpolatedTime == 1) LayoutParams.WRAP_CONTENT
			else (targetHeight * interpolatedTime).toInt

		animatingView.requestLayout
	}
}