package pwr.rss.reader.database.manager

import java.lang.Long
import java.util.ArrayList

import scala.collection.JavaConversions.asScalaBuffer

import android.content.Context
import android.database.Cursor
import pwr.rss.reader.ApplicationObject
import pwr.rss.reader.R
import pwr.rss.reader.data.dao.ChannelDao
import pwr.rss.reader.data.dao.FeedDao
import pwr.rss.reader.data.dao.FeedDao.ALL
import pwr.rss.reader.data.dao.FeedDao.READ
import pwr.rss.reader.data.dao.FeedDao.UNREAD
import pwr.rss.reader.database.dao.Channel
import pwr.rss.reader.database.dao.Feed

object DataBaseManager {
	def apply(context: Context) = new DataBaseManager(context)
}

class DataBaseManager(context: Context) {
	private lazy val applicationObject = context.getApplicationContext.asInstanceOf[ApplicationObject]
	private lazy val dataBaseHelper = new DataBaseHelper(context)
	private lazy val database = dataBaseHelper.getWritableDatabase
	private lazy val channelDao = new ChannelDao(database)
	private lazy val feedDao = new FeedDao(database)

	/**
	  * Channel DAO methods
	  */
	def getAllChannelsList = channelDao getAllChannelsList
	def selectChannel(channel: Channel) = channelDao.selectChannel(channel)
	def getSelectedChannelsIds = channelDao.getSelectedChannelsIds

	/**
	  * Feed DAO methods
	  */
	def removeReadFeeds = feedDao.removeReadFeeds
	def addFeed(feed: Feed) = if (feed != null) feedDao.addFeed(feed) else 0L
	def addFeeds(feeds: ArrayList[Feed]) = if (feeds != null) feeds foreach { feed => addFeed(feed) } else 0L
	def deleteFeed(feedID: Long) = feedDao.deleteFeed(feedID)
	def getFeed(cursor: Cursor) = feedDao.buildFeedFromCursor(cursor)
	def markAllAsRead(unreadCursor: Cursor) = feedDao.markAllAsRead(unreadCursor)
	def markChannelAsRead(channelID: Long) = feedDao.markChannelAsRead(channelID)
	def markAsUnread(readFeedsIDs: ArrayList[Long]) = feedDao.markAsUnread(readFeedsIDs)
	def markFeedAsRead(feed: Feed) = feedDao.markFeedAsRead(feed, true)
	def markFeedAsUnread(feed: Feed) = feedDao.markFeedAsRead(feed, false)
	def countUnreadInChannel(channelID: Long) = feedDao.countUnreadInChannel(channelID)

	def getCurrentCursor(filterQuery: String) = {
		val selectedRadioButtonId = applicationObject.getSelectedRadioButtonId

		selectedRadioButtonId match {
			case R.id.radioButtonAll =>
				feedDao.getFeedsCursor(ALL, filterQuery)

			case R.id.radioButtonRead =>
				feedDao.getFeedsCursor(READ, filterQuery)

			case R.id.radioButtonUnread =>
				feedDao.getFeedsCursor(UNREAD, filterQuery)
		}
	}
}