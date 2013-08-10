package pwr.rss.reader.web

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.Context
import pwr.rss.reader.fragments.FeedsListFragment
import pwr.rss.reader.ApplicationObject
import pwr.rss.reader.R

class WebEventReceiver(fragment: FeedsListFragment) extends BroadcastReceiver {

	override def onReceive(context: Context, intent: Intent) = {
		val applicationObject = context.getApplicationContext.asInstanceOf[ApplicationObject]
		val receivedIntent = intent.getAction

		if (DownloadService.ACTION_DOWNLOAD_COMPLETED.equals(receivedIntent)) {
			if (!applicationObject.isAppRunningInBackground)
				applicationObject.showBottomToast(R.string.message_feeds_list_updated)
		}
		else if (DownloadService.ACTION_DEVICE_OFFLINE.equals(receivedIntent))
			fragment.showAlertDeviceOffline

		fragment.getSherlockActivity.setSupportProgressBarIndeterminateVisibility(false)
		fragment.restartLoader
	}
}