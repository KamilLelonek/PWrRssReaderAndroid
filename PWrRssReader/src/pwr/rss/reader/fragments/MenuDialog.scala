package pwr.rss.reader.fragments

import com.actionbarsherlock.app.SherlockDialogFragment
import android.app.AlertDialog
import android.os.Bundle
import pwr.rss.reader.R
import android.view.WindowManager
import android.view.Gravity
import android.view.View

abstract class MenuDialog extends SherlockDialogFragment {
	protected lazy val view: View = null
	protected lazy val alertDialog = new AlertDialog.Builder(getActivity).create

	override def onCreateDialog(savedInstanceState: Bundle) = {
		configureAlertDialog
		configureAlertDialogWindow
		alertDialog
	}

	private def configureAlertDialog = {
		alertDialog.setCanceledOnTouchOutside(true)
		alertDialog.setView(view)
	}

	def configureAlertDialogWindow = {
		val alertDialogWindow = alertDialog.getWindow
		alertDialogWindow.setGravity(Gravity.RIGHT | Gravity.TOP)
		alertDialogWindow.getAttributes.y = 100
		alertDialogWindow.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
	}
}