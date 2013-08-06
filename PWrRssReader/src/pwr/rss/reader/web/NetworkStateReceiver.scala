package pwr.rss.reader.web

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import pwr.rss.reader.ApplicationObject

class NetworkStateReceiver extends BroadcastReceiver {
	override def onReceive(context: Context, intent: Intent) = {
		val serviceManagerIntent = new Intent(context, classOf[ServiceManager])
		val applicationObject = context.getApplicationContext.asInstanceOf[ApplicationObject]

		if (applicationObject.isConnectedToInternet) context.startService(serviceManagerIntent)
		else context.stopService(serviceManagerIntent)
	}
}