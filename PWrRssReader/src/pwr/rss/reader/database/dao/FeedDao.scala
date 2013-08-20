package pwr.rss.reader.data.dao

import android.content.ContentValues
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import pwr.rss.reader.database.tables.SQLQueries
import pwr.rss.reader.database.tables.SQLQueries
import pwr.rss.reader.database.dao.Feed
import scala.collection.mutable.MutableList
import pwr.rss.reader.database.dao.DAO
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteQueryBuilder
import android.os.Build
import pwr.rss.reader.database.tables.TableFeeds._
import pwr.rss.reader.data.dao.FeedDao._
import pwr.rss.reader.database.tables.TableChannels._
import scala.collection.mutable.ArrayBuilder
import scala.collection.mutable.ArrayBuffer
import android.text.TextUtils
import scala.collection.JavaConversions._
import java.util.ArrayList
import java.lang.Long
import pwr.rss.reader.FeedsListActivity._

object FeedDao {
	sealed trait READABLE extends Product with Serializable
	case object ALL extends READABLE
	case object READ extends READABLE
	case object UNREAD extends READABLE

	private lazy val read: Long = 1L
	private lazy val unread: Long = 0L

	private lazy val STATEMENT_INSERT = SQLQueries.INSERT + TABLE_NAME_FEEDS +
		"(" +
		C_TITLE + ", " +
		C_IMAGE + ", " +
		C_LINK + ", " +
		C_DESCRIPTION + ", " +
		C_READ + ", " +
		C_CHANNEL + ", " +
		C_ADDED_DATE + ")" +
		" VALUES (?, ?, ?, ?, ?, ?, ?)"
}

class FeedDao(private val database: SQLiteDatabase) extends DAO(database) {
	/**
	  * ************************
	  * ******** CREATE ********
	  * ************************
	  */
	private lazy val statementINSERT = database.compileStatement(STATEMENT_INSERT)

	def addFeed(feed: Feed) = {
		statementINSERT.clearBindings
		statementINSERT.bindString(1, feed.title)
		statementINSERT.bindString(2, feed.image)
		statementINSERT.bindString(3, feed.link)
		statementINSERT.bindString(4, feed.description)
		statementINSERT.bindLong(5, feed.read)
		statementINSERT.bindLong(6, feed.channel)
		statementINSERT.bindLong(7, feed.date)
		statementINSERT.executeInsert
	}

	/**
	  * ************************
	  * ********* READ *********
	  * ************************
	  */
	implicit def bool2string(b: Boolean) = if (b) "1" else "0"

	def getFeedsCursor(kind: READABLE, filterQuery: String) = {
		val whereClause = new StringBuilder
		val selectionArgs = ArrayBuffer[String]()

		addKindClause(kind, whereClause, selectionArgs)
		addSelectedClause(whereClause, selectionArgs)
		addFilterClause(filterQuery, whereClause, selectionArgs)

		getCursorWithSpecifiedWhereClause(whereClause.toString, selectionArgs.toArray)
	}

	private def getChannelUnreadFeedsCursor(channelID: Long) = {
		val whereClause = new StringBuilder
		val selectionArgs = ArrayBuffer[String]()

		addKindClause(UNREAD, whereClause, selectionArgs)
		addChannelClause(channelID, whereClause, selectionArgs)

		getCursorWithSpecifiedWhereClause(whereClause.toString, selectionArgs.toArray)
	}

	private def addKindClause(kind: READABLE, whereClause: StringBuilder, selectionArgs: ArrayBuffer[String]) = {
		kind match {
			case READ => {
				addToWhereClause(whereClause, C_READ + " =?")
				addToSelectionArgs(selectionArgs, "1")
			}
			case UNREAD => {
				addToWhereClause(whereClause, C_READ + " =?")
				addToSelectionArgs(selectionArgs, "0")
			}
			case ALL =>
		}
	}

	private def addSelectedClause(whereClause: StringBuilder, selectionArgs: ArrayBuffer[String]) = {
		addConnectiveToWhereClauses(whereClause, selectionArgs)
		addToWhereClause(whereClause, C_SELECTED + " =?")
		addToSelectionArgs(selectionArgs, 1.toString)
	}

	private def addFilterClause(filterQuery: String, whereClause: StringBuilder, selectionArgs: ArrayBuffer[String]) =
		if (!TextUtils.isEmpty(filterQuery)) {
			addConnectiveToWhereClauses(whereClause, selectionArgs)
			addToWhereClause(whereClause, C_TITLE + " LIKE ?")
			addToSelectionArgs(selectionArgs, "%" + filterQuery + "%")
		}

	def addChannelClause(channelID: Long, whereClause: StringBuilder, selectionArgs: ArrayBuffer[String]) = {
		addConnectiveToWhereClauses(whereClause, selectionArgs)
		addToWhereClause(whereClause, C_CHANNEL + " =?")
		addToSelectionArgs(selectionArgs, channelID.toString)
	}

	private def addConnectiveToWhereClauses(whereClause: StringBuilder, selectionArgs: ArrayBuffer[String]) =
		if (!selectionArgs.isEmpty) whereClause ++= " AND "

	private def addToWhereClause(whereClause: StringBuilder, clause: String) = whereClause ++= clause
	private def addToSelectionArgs(selectionArgs: ArrayBuffer[String], arg: String) = selectionArgs += arg

	private def getCursorWithSpecifiedWhereClause(whereClause: String, selectionArgs: Array[String]) =
		getJoinQuery.query(
			database,
			Array(
				TABLE_NAME_FEEDS + "." + SQLQueries.ID,
				C_TITLE,
				C_IMAGE,
				C_READ,
				C_SITE,
				C_NAME,
				C_CHANNEL,
				C_ADDED_DATE,
				C_LINK,
				C_DESCRIPTION,
				C_READ,
				C_CHANNEL,
				C_ADDED_DATE),
			whereClause,
			selectionArgs,
			null, null, C_ADDED_DATE + " DESC")

	private def getJoinQuery = {
		val joinQuery = new SQLiteQueryBuilder
		joinQuery.setTables(TABLE_NAME_FEEDS + ", " + TABLE_NAME_CHANNELS)
		joinQuery.appendWhere(TABLE_NAME_FEEDS + "." + C_CHANNEL + " = " + TABLE_NAME_CHANNELS + "." + SQLQueries.ID)
		joinQuery
	}

	def countUnreadInChannel(channelID: Long) = countForBuildVersion(channelID)

	private def countForBuildVersion(channelID: Long) = {
		val selectionArgs = Array(channelID.toString, "0")
		val whereClause = C_CHANNEL + " =? AND " + C_READ + " =?"

		if (!NEW_API)
			countPreHoneycomb(whereClause, selectionArgs)
		else
			countPostHoneycomb(whereClause, selectionArgs)
	}

	private def countPreHoneycomb(whereClause: String, selectionArgs: Array[String]) = {
		val countCursor =
			database.rawQuery("SELECT * FROM " + TABLE_NAME_FEEDS + " WHERE " + whereClause, selectionArgs)
		val count = countCursor.getCount
		if (!countCursor.isClosed) countCursor.close
		count
	}

	private def countPostHoneycomb(whereClause: String, selectionArgs: Array[String]) =
		DatabaseUtils.queryNumEntries(
			database,
			TABLE_NAME_FEEDS,
			whereClause,
			selectionArgs)

	def buildFeedFromCursor(cursor: Cursor) = {
		if (cursor.getCount == 0) null
		else {
			val idIndex = cursor.getColumnIndex(SQLQueries.ID)
			val id = cursor.getLong(idIndex)

			val titleIndex = cursor.getColumnIndex(C_TITLE)
			val title = cursor.getString(titleIndex)

			val imageIndex = cursor.getColumnIndex(C_IMAGE)
			val image = cursor.getString(imageIndex)

			val linkIndex = cursor.getColumnIndex(C_LINK)
			val link = cursor.getString(linkIndex)

			val descriptionIndex = cursor.getColumnIndex(C_DESCRIPTION)
			val description = cursor.getString(descriptionIndex)

			val channelIndex = cursor.getColumnIndex(C_CHANNEL)
			val channel = cursor.getLong(channelIndex)

			val isReadIndex = cursor.getColumnIndex(C_READ)
			val isRead = cursor.getLong(isReadIndex)

			val dateIndex = cursor.getColumnIndex(C_ADDED_DATE)
			val date = cursor.getLong(dateIndex)

			new Feed(id, title, image, link, description, channel, isRead, date)
		}
	}

	/**
	  * ************************
	  * ******** UPDATE ********
	  * ************************
	  */
	def markFeedAsRead(feed: Feed, read: Boolean) = {
		feed.markAsRead(read)
		update(feed)
	}

	def markChannelAsRead(channelID: Long) = markAllAsRead(getChannelUnreadFeedsCursor(channelID))

	private def update(feed: Feed) = {
		val values = new ContentValues
		values.put(C_READ, feed.read.asInstanceOf[java.lang.Long])
		updateDatabase(values, feed.ID)
	}

	def markAllAsRead(unreadCursor: Cursor): ArrayList[Long] = {
		val feedsIdMarkedAsRead = new ArrayList[Long]()
		val columnIndexID = unreadCursor.getColumnIndex(SQLQueries.ID)
		val columnIndexRead = unreadCursor.getColumnIndex(C_READ)

		if (unreadCursor.moveToFirst) {
			do {
				val feedID = unreadCursor.getLong(columnIndexID)
				val isUnread = unreadCursor.getLong(columnIndexRead) == 0L
				markAsReadIfIsUnread(feedID, isUnread, feedsIdMarkedAsRead)
			} while (unreadCursor.moveToNext)
		}

		feedsIdMarkedAsRead
	}

	def markAsReadIfIsUnread(feedID: Long, isUnread: Boolean, feedsIdMarkedAsRead: ArrayList[Long]) =
		if (isUnread) {
			val values = new ContentValues
			values.put(C_READ, read)
			updateDatabase(values, feedID)
			feedsIdMarkedAsRead.add(feedID)
		}

	def markAsUnread(readFeedsIDs: ArrayList[Long]) =
		readFeedsIDs.toList.foreach {
			feedID =>
				val values = new ContentValues
				values.put(C_READ, unread)
				updateDatabase(values, feedID)
		}

	private def updateDatabase(values: ContentValues, feedID: Long) = super.update(TABLE_NAME_FEEDS, values, feedID)

	/**
	  * ************************
	  * ******** DELETE ********
	  * ************************
	  */
	def removeReadFeeds = {
		val cursor = getFeedsCursor(READ, "")
		if (cursor.moveToFirst) {
			do {
				val idIndex = cursor.getColumnIndex(SQLQueries.ID)
				val id = cursor.getLong(idIndex)
				deleteFeed(id)
			} while (cursor.moveToNext)
		}

	}
	def deleteFeed(feedID: Long) = super.delete(TABLE_NAME_FEEDS, feedID)
}