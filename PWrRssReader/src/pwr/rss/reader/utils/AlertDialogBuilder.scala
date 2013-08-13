package pwr.rss.reader.utils

import com.example.holodialoglibrary.HoloDialogBuilder
import android.content.Context
import pwr.rss.reader.R
import android.view.View
import android.content.Intent
import android.provider.Settings

object AlertDialogBuilder {
	def getAlertDialog(context: Context) =
		(new HoloDialogBuilder(context)).setPositiveButtonText(R.string.menu_settings)
			.setOnPositiveClickListener(new View.OnClickListener {
				override def onClick(v: View) = {
					context.startActivity(new Intent(Settings.ACTION_SETTINGS))
				}
			}).setNegativeButtonText(android.R.string.cancel).setOnNegativeClickDismiss()
			.setTitleText(R.string.alert_internet_connection_title)
			.setMessage(R.string.alert_internet_connection_body)
			.setDividerColor(R.color.blue_light)
			.setTitleColor(R.color.blue_dark)
			.setIcon(R.drawable.ic_alert)
}