package pwr.rss.reader.views

import android.view.View
import android.app.Activity

object ViewHelper {
	def findView[T](view: View, id: Int) = view.findViewById(id).asInstanceOf[T]
	def findView[T](implicit activity: Activity, id: Int) = activity.findViewById(id).asInstanceOf[T]
}