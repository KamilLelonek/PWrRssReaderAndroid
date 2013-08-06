package pwr.rss.reader.utils

import pwr.rss.reader.database.tables.TableChannels._
import pwr.rss.reader.database.tables.TableFeeds._
import android.database.Cursor
import java.util.Date
import android.graphics.Color
import android.content.Context
import android.graphics.BitmapFactory
import pwr.rss.reader.database.tables.TableChannels
import pwr.rss.reader.database.tables.SQLQueries

class CursorFetcher(cursor: Cursor, context: Context) {
	implicit def int2bool(int: Int) = if (int == 1) true else false
	implicit def longDate2String(longDate: Long) = new Date(longDate).toLocaleString

	private lazy val bitmapUtils = new BitmapUtils(context)

	private def getColumnIndex(columnName: String) = cursor.getColumnIndex(columnName)
	private lazy val indexID = getColumnIndex(SQLQueries.ID)
	private lazy val indexTitle = getColumnIndex(C_TITLE)
	private lazy val indexLink = getColumnIndex(C_LINK)
	private lazy val indexSite = getColumnIndex(C_SITE)
	private lazy val indexDate = getColumnIndex(C_ADDED_DATE)
	private lazy val indexDescription = getColumnIndex(C_DESCRIPTION)
	private lazy val indexRead = getColumnIndex(C_READ)
	private lazy val indexChannel = getColumnIndex(C_CHANNEL)
	private lazy val indexImage = getColumnIndex(C_IMAGE)

	def getID = cursor.getLong(indexID)
	def getFeedTitle = getText(indexTitle)
	def getFeedLink = getText(indexLink)
	def getChannelSite = getText(indexSite)
	def getDescription = getText(indexDescription)
	def getImageLink = getText(indexImage)
	private def getText(index: Int) = cursor.getString(index)

	def getAddedDate: String = cursor.getLong(indexDate)
	def isRead: Boolean = cursor.getInt(indexRead)

	def getFeedImageId = {
		val channel = cursor.getInt(indexChannel)
		TableChannels.getDrawableIdForChannel(channel)
	}

	def getTextColor = if (isRead) Color.GRAY else Color.BLACK
}