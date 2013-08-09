package pwr.rss.reader.notifications

import android.content.Context
import android.support.v4.app.NotificationCompat
import android.app.Notification._
import pwr.rss.reader.utils.BitmapUtils
import pwr.rss.reader.R
import pwr.rss.reader.ApplicationObject
import android.content.Intent
import pwr.rss.reader.FeedsListActivity
import android.app.PendingIntent
import android.app.PendingIntent._

class NotificationsBuilder(context: Context) {
	private lazy val application = context.getApplicationContext.asInstanceOf[ApplicationObject]
	private lazy val resources = context.getResources
	private lazy val contentTitle = resources.getString(R.string.app_name)
	private lazy val contentText = resources.getString(R.string.notification_text)
	private lazy val tickerText = resources.getString(R.string.message_feeds_list_updated)
	private lazy val colorBlue = resources.getColor(R.color.blue_light)
	private lazy val bitmapUtils = BitmapUtils(context)
	private lazy val largeIcon = bitmapUtils.getBitmapFromResource(R.drawable.ic_launcher)
	private lazy val intentFeedsList = {
		val intent = new Intent(context, classOf[FeedsListActivity])
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
		intent
	}
	private lazy val pendingIntent = PendingIntent.getActivity(context, 0, intentFeedsList, FLAG_UPDATE_CURRENT)

	private lazy val getBasicNotificationBuilder =
		new NotificationCompat.Builder(context)
			.setSmallIcon(R.drawable.ic_action_notify)
			.setLargeIcon(largeIcon)
			.setLights(colorBlue, 300, 200)
			.setContentTitle(contentTitle)
			.setContentText(contentText)
			.setAutoCancel(true)
			.setTicker(tickerText)
			.setContentIntent(pendingIntent)

	private def getCustomizedNotificationBuilder = {
		val basicNotificationBuilder = getBasicNotificationBuilder

		var flag = 0
		val led = application.isNotificationLedEnabled
		if (led) flag |= DEFAULT_LIGHTS
		val vibrate = application.isNotificationVibrateEnabled
		if (vibrate) flag |= DEFAULT_VIBRATE
		val sound = application.isNotificationSoundEnabled
		if (sound) flag |= DEFAULT_SOUND
		basicNotificationBuilder.setDefaults(flag)

		basicNotificationBuilder
	}

	def getNotification(count: Int) = {
		val customizedNotificationBuilder = getCustomizedNotificationBuilder
		customizedNotificationBuilder.setContentInfo(count.toString)
		getCustomizedNotificationBuilder.build
	}
}