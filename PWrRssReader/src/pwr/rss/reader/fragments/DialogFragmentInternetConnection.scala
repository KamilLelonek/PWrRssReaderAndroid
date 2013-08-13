package pwr.rss.reader.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.DialogInterface.OnClickListener
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.DialogFragment
import pwr.rss.reader.R

class DialogFragmentInternetConnection extends DialogFragment {
	override def onCreateDialog(savedInstanceState: Bundle) = {
		new AlertDialog.Builder(getActivity).setPositiveButton(R.string.menu_settings, new OnClickListener {
			override def onClick(dialog: DialogInterface, which: Int) {
				startActivity(new Intent(Settings.ACTION_SETTINGS))
				dialog.dismiss
			}
		}).setNegativeButton(android.R.string.cancel, new OnClickListener {
			override def onClick(dialog: DialogInterface, which: Int) {
				dialog.dismiss
			}
		}).setTitle(R.string.alert_internet_connection_title).setMessage(R.string.alert_internet_connection_body)
			.setIcon(R.drawable.ic_alert).create
	}
}