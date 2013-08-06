package pwr.rss.reader

import java.lang.Long
import java.util.ArrayList
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.database.Cursor
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import pwr.rss.reader.database.dao.Channel
import pwr.rss.reader.database.dao.Feed
import pwr.rss.reader.database.manager.DataBaseManager
import pwr.rss.reader.utils.ToastFactory
import pwr.rss.reader.utils.PreferencesManager

class ApplicationObject extends Application {
	/**
	  * ************************************************
	  * ************* Preferences Manager **************
	  * ************************************************
	  */
	private lazy val preferencesManager = new PreferencesManager(this)

	def isAutoRefreshEnabled = preferencesManager.isAutoRefreshEnabled
	def getRefreshPeriod = preferencesManager.getRefreshPeriod.toInt
	def areNotificationsEnabled = preferencesManager.areNotificationsEnabled
	def isNotificationLedEnabled = preferencesManager.isNotificationLedEnabled
	def isNotificationVibrateEnabled = preferencesManager.isNotificationVibrateEnabled
	def isNotificationSoundEnabled = preferencesManager.isNotificationSoundEnabled
	def autoMarkAsRead = preferencesManager.autoMarkAsRead
	def getSelectedRadioButtonId = preferencesManager.getSelectedRadioButtonId
	def isSelectedOnlyChecked = preferencesManager.isSelectedOnlyChecked
	def keepFeedsAsRead = preferencesManager.keepFeedsAsRead

	/**
	  * ************************************************
	  * ************* Singletons Manager ***************
	  * ************************************************
	  */
	lazy val toastFactory = ToastFactory(this)

	/**
	  * ************************************************
	  * ************** Database Manager ****************
	  * ************************************************
	  */
	private lazy val databaseManager = new DataBaseManager(this)
	private var filterQuery = ""

	def performCleanUp = if (!keepFeedsAsRead) databaseManager.removeReadFeeds
	def getCurrentCursor: Cursor = databaseManager.getCurrentCursor(filterQuery)
	def setFilterQuery(query: String) = filterQuery = query

	def getAllChannelsList = databaseManager getAllChannelsList
	def getAllChannelsJavaList = databaseManager getAllChannelsJavaList
	def selectChannel(channel: Channel) = databaseManager.selectChannel(channel)
	def addFeed(feed: Feed) = databaseManager.addFeed(feed)
	def addFeeds(feeds: ArrayList[Feed]) = databaseManager.addFeeds(feeds)
	def getFeed(cursor: Cursor): Feed = databaseManager.getFeed(cursor)
	def deleteFeed(feedID: Long) = databaseManager.deleteFeed(feedID)
	def markAllAsRead: ArrayList[Long] = databaseManager.markAllAsRead(getCurrentCursor)
	def markChannelAsRead(channelID: Long) = databaseManager.markChannelAsRead(channelID)
	def markAsUnread(readFeedsIDs: ArrayList[Long]) = databaseManager.markAsUnread(readFeedsIDs)
	def markFeedAsRead(feed: Feed) = databaseManager.markFeedAsRead(feed)
	def markFeedAsUnread(feed: Feed) = databaseManager.markFeedAsUnread(feed)
	def countUnreadInChannel(channelID: Long) = databaseManager.countUnreadInChannel(channelID)

	/**
	  * ************************************************
	  * ************* Notifications Manager ************
	  * ************************************************
	  */
	private lazy val activityManager = getSystemService(Context.ACTIVITY_SERVICE).asInstanceOf[ActivityManager]
	/**
	  * Checks if application is displayed on screen.
	  *
	  * @return true if application is not visible on screen
	  */
	def isAppRunningInBackground = {
		val topTask = activityManager.getRunningTasks(1)
		val topActivity = topTask.get(0).topActivity
		val currentActivityPackageName = topActivity.getPackageName
		!currentActivityPackageName.equalsIgnoreCase(this.getPackageName)
	}

	/**
	  * ************************************************
	  * ************** Networking Manager **************
	  * ************************************************
	  */
	private lazy val wifiManager =
		getSystemService(Context.WIFI_SERVICE).asInstanceOf[WifiManager]
	private lazy val connectivityManager =
		getSystemService(Context.CONNECTIVITY_SERVICE).asInstanceOf[ConnectivityManager]

	private def isWiFiEnabled =
		wifiManager.isWifiEnabled && getWifiNetworkInfo.isConnected

	private def getWifiNetworkInfo =
		getNetworkInfo(ConnectivityManager.TYPE_WIFI)

	private def isMobileDataEnabled =
		getMobileNetworkInfo.isConnected

	private def getMobileNetworkInfo =
		getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

	private def getNetworkInfo(networkType: Int) =
		connectivityManager.getNetworkInfo(networkType)

	def isConnectedToInternet =
		isWiFiEnabled || isMobileDataEnabled
}