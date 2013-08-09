package pwr.rss.reader.fragments

import com.actionbarsherlock.app.SherlockDialogFragment
import android.app.AlertDialog
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import pwr.rss.reader.R
import pwr.rss.reader.views.ViewHelper._
import android.widget.Button
import android.view.View.OnClickListener
import android.content.Intent
import pwr.rss.reader.PreferencesActivity
import pwr.rss.reader.OnMenuListActionListener
import android.view.LayoutInflater
import android.view.ViewGroup
import android.app.Dialog

abstract class ListMenuFragment extends SherlockDialogFragment with OnClickListener {
	protected lazy val activity = getActivity
	protected lazy val view: View = null
	protected lazy val dialog: Dialog = null
	protected var onMenuActionListener: OnMenuListActionListener = null

	protected lazy val settingsButton = findView[Button](view, R.id.buttonMenuSettings)
	protected lazy val refreshButton = findView[Button](view, R.id.buttonMenuRefresh)
	protected lazy val markAllAsReadButton = findView[Button](view, R.id.buttonMenuMarkAllAsRead)

	override def onCreateDialog(savedInstanceState: Bundle) = {
		configureAlertDialogWindow
		configureDialog
		configureButtons

		dialog
	}

	protected def configureAlertDialogWindow = {
		val alertDialogWindow = dialog.getWindow
		alertDialogWindow.setBackgroundDrawableResource(android.R.color.transparent)
		alertDialogWindow.setGravity(Gravity.RIGHT | Gravity.TOP)
		alertDialogWindow.getAttributes.y = 100
		alertDialogWindow.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
	}

	protected def configureDialog = {
		dialog.setCanceledOnTouchOutside(true)
		dialog.setContentView(view)
	}

	protected def configureButtons = {
		settingsButton.setOnClickListener(this)
		refreshButton.setOnClickListener(this)
		markAllAsReadButton.setOnClickListener(this)
	}

	def setOnMenuActionListener(onMenuActionListener: OnMenuListActionListener) = {
		this.onMenuActionListener = onMenuActionListener
		this
	}

	override def onClick(view: View) = {
		this.dismiss
		view match {
			case `settingsButton` =>
				activity.startActivity(new Intent(activity, classOf[PreferencesActivity]))
				activity.overridePendingTransition(0, 0)
			case `refreshButton` =>
				if (onMenuActionListener != null) onMenuActionListener.notifyMenuRefresh
			case `markAllAsReadButton` =>
				if (onMenuActionListener != null) onMenuActionListener.notifyMenuMarkAllAsRead
			case _ =>
		}
	}
}