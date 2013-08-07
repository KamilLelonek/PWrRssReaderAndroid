package pwr.rss.reader.notifications

import android.content.Context
import android.app.NotificationManager

class NotificationsManager(context: Context) {
	private lazy val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE).asInstanceOf[NotificationManager]
}