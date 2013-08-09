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

class ListMenuFragmentGB extends ListMenuFragment {
	override protected lazy val view = LayoutInflater.from(activity).inflate(R.layout.menu_list_gb, null)
	override protected lazy val dialog = new Dialog(activity, R.style.Dialog_No_Border)

	override protected def configureDialog = {
		dialog.setCanceledOnTouchOutside(true)
		dialog.setContentView(view)
	}
}