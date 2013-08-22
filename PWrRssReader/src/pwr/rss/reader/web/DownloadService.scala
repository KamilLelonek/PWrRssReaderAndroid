package pwr.rss.reader.web

import android.app.IntentService
import android.content.Intent
import DownloadService._
import pwr.rss.reader.ApplicationObject
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.content.WakefulBroadcastReceiver
import scala.collection.mutable.ListBuffer
import pwr.rss.reader.json.PWrJSONParser
import java.util.ArrayList
import pwr.rss.reader.database.dao.Feed

object DownloadService {
	val ACTION_DOWNLOAD_COMPLETED = "pwr.rss.reader.action.download_completed"
	val ACTION_START_DOWNLOAD = "pwr.rss.reader.action.start_download"
	val ACTION_DEVICE_OFFLINE = "pwr.rss.reader.action.device_offline"
}

class DownloadService extends IntentService("DownloadService") {
	private lazy val applicationObject = getApplication.asInstanceOf[ApplicationObject]
	private lazy val localBroadcastManager = LocalBroadcastManager.getInstance(this)
	private lazy val downloadCompletedBroadcast = new Intent(ACTION_DOWNLOAD_COMPLETED)
	private lazy val downloadAbortedBroadcast = new Intent(ACTION_DEVICE_OFFLINE)
	private var isDownloading = false

	def onHandleIntent(intent: Intent) = {
		downloadDataIfOnline
		WakefulBroadcastReceiver.completeWakefulIntent(intent)
	}

	private def downloadDataIfOnline {
		if (canUpdateFeeds) {
			this.isDownloading = true
			downloadData
			this.isDownloading = false
		}
		else {
			notifyDeviceOffline
		}
	}

	private def canUpdateFeeds = isDeviceOnline && !isDownloading

	private def isDeviceOnline =
		applicationObject.isConnectedToInternet

	private def downloadData = {
		val selectedChannelsIds = applicationObject.getSelectedChannelsIds
		val lastUpdateTimes = getLastUpdateTimes(selectedChannelsIds)
		val inputString = HttpConnection.getInputString(lastUpdateTimes, selectedChannelsIds)
		val feeds = PWrJSONParser.getFeeds(inputString)
		updateFeedsData(feeds, selectedChannelsIds)
		notifyDownloadCompleted
	}

	private def getLastUpdateTimes(selectedChannelsIds: List[Int]) = {
		val lastUpdateTimes = new ListBuffer[Long]

		for (channelID <- selectedChannelsIds) {
			val lastUpdateTime = applicationObject.getLastUpdateDate(channelID)
			lastUpdateTimes += lastUpdateTime
		}

		lastUpdateTimes toList
	}

	private def updateFeedsData(feeds: ArrayList[Feed], selectedChannelsIds: List[Int]) {
		if (feeds != null && !feeds.isEmpty) {
			applicationObject.addFeeds(feeds)
			applicationObject.showNotification(feeds.size)
			selectedChannelsIds.foreach(id => applicationObject.setLastUpdateDate(id))
		}
	}

	private def notifyDownloadCompleted =
		localBroadcastManager.sendBroadcast(downloadCompletedBroadcast)

	private def notifyDeviceOffline =
		localBroadcastManager.sendBroadcast(downloadAbortedBroadcast)
}