package pwr.rss.reader.animations

import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener

abstract class AnimationEndListener extends AnimationListener {
	override def onAnimationStart(animation: Animation): Unit = {}
	override def onAnimationRepeat(animation: Animation): Unit = {}
}