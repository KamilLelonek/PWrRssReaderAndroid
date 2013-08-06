package pwr.rss.reader.web

import android.support.v4.content.WakefulBroadcastReceiver
import android.support.v4.content.WakefulBroadcastReceiver._
import android.content.Context
import android.content.Intent

class WakeReceiver extends WakefulBroadcastReceiver {
	override def onReceive(context: Context, intent: Intent) = {
		val startService = new Intent(context, classOf[DownloadService])
		startWakefulService(context, startService)
	}
}