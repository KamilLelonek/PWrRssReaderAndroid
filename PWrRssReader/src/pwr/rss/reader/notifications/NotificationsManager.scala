package pwr.rss.reader.notifications

import android.content.Context
import android.app.NotificationManager
import NotificationsManager._

object NotificationsManager {
	private lazy val NOTIFICAION_ID = 0x333
	def apply(context: Context) = new NotificationsManager(context)
}

class NotificationsManager(context: Context) {
	private lazy val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE).asInstanceOf[NotificationManager]
	private lazy val notificationsBuilder = new NotificationsBuilder(context)

	private def getNotification(count: Int) = notificationsBuilder.getNotification(count)
	def showNotification(count: Int) = {
		val notification = getNotification(count)
		notificationManager.notify(NOTIFICAION_ID, notification)
	}
}