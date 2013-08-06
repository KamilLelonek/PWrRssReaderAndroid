package pwr.rss.reader.animations

import android.view.animation.Animation
import android.view.View
import android.view.ViewGroup.LayoutParams

class ResizingAnimation(animatingView: View) extends Animation {
	override def willChangeBounds = true
	private def screenDensity = animatingView.getContext.getResources.getDisplayMetrics.density
	protected lazy val targetHeight = animatingView.getMeasuredHeight

	animatingView.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
	setDuration((targetHeight / screenDensity).toLong)
}