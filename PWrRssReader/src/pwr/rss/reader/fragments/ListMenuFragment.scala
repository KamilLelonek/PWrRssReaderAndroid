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

class ListMenuFragment(onMenuActionListener: OnMenuListActionListener) extends MenuDialog with OnClickListener {
	override protected lazy val view = getActivity.getLayoutInflater.inflate(R.layout.menu_list, null)
	private lazy val settingsButton = findView[Button](view, R.id.buttonMenuSettings)
	private lazy val refreshButton = findView[Button](view, R.id.buttonMenuRefresh)
	private lazy val markAllAsReadButton = findView[Button](view, R.id.buttonMenuMarkAllAsRead)

	override def onCreateDialog(savedInstanceState: Bundle) = {
		val listMenuAlertDialog = super.onCreateDialog(savedInstanceState)
		configureAlertDialogWindow
		configureButtons
		listMenuAlertDialog
	}

	private def configureButtons = {
		settingsButton.setOnClickListener(this)
		refreshButton.setOnClickListener(this)
		markAllAsReadButton.setOnClickListener(this)
	}

	override def onClick(view: View) = {
		this.dismiss
		view match {
			case `settingsButton` =>
				getActivity.startActivity(new Intent(getActivity, classOf[PreferencesActivity]))
			case `refreshButton` =>
				onMenuActionListener.notifyMenuRefresh
			case `markAllAsReadButton` =>
				onMenuActionListener.notifyMenuMarkAllAsRead
			case _ =>
		}
	}
}