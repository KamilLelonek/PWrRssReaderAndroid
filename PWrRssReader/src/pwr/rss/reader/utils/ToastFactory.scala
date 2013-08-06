package pwr.rss.reader.utils

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import pwr.rss.reader.R
import pwr.rss.reader.views.ViewHelper._
import android.view.LayoutInflater

case class ToastFactory(context: Context) {
	private final lazy val toast = new Toast(context)

	def showBottomToast(messageId: Int) = {
		buildToast(messageId, Gravity.FILL_HORIZONTAL)
		showToast
	}

	private def showToast = toast.show

	private def buildToast(messageId: Int, flag: Int) = {
		val layout = getToastLayout
		setToastMessage(layout, messageId)
		configureToast(layout, flag)
	}

	private def getToastLayout = {
		val inflater = LayoutInflater.from(context)
		inflater.inflate(R.layout.toast_layout, null)
	}

	private def setToastMessage(layout: View, messageId: Int) = {
		val textViewToastMessage = findView[TextView](layout, R.id.textViewToastMessage)
		textViewToastMessage.setText(messageId)
	}

	private def configureToast(layout: View, flag: Int) = {
		toast.setGravity(flag | Gravity.BOTTOM, 0, 0)
		toast.setDuration(Toast.LENGTH_SHORT)
		toast.setView(layout)
	}
}