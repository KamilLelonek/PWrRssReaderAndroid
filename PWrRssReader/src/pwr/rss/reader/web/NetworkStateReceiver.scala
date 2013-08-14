package pwr.rss.reader.web

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NetworkStateReceiver extends BroadcastReceiver {
	override def onReceive(context: Context, intent: Intent) =
		context.startService(new Intent(context, classOf[ServiceManager])) // to update download request (start on cancel)
}