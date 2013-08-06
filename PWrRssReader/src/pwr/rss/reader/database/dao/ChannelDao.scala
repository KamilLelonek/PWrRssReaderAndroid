package pwr.rss.reader.data.dao

import scala.collection.JavaConversions
import scala.collection.mutable.MutableList
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import pwr.rss.reader.database.dao.Channel
import pwr.rss.reader.data.dao.ChannelDao._
import pwr.rss.reader.database.dao.DAO
import pwr.rss.reader.database.tables.SQLQueries
import pwr.rss.reader.database.tables.SQLQueries.INSERT
import pwr.rss.reader.database.tables.TableChannels.C_SITE
import pwr.rss.reader.database.tables.TableChannels.C_LOGO
import pwr.rss.reader.database.tables.TableChannels.C_NAME
import pwr.rss.reader.database.tables.TableChannels.C_SELECTED
import pwr.rss.reader.database.tables.TableChannels.TABLE_NAME_CHANNELS
import scala.collection.JavaConversions
import android.util.Log
import scala.collection.mutable.ListBuffer

object ChannelDao {
	private val STATEMENT_INSERT = SQLQueries.INSERT + TABLE_NAME_CHANNELS +
		"(" +
		C_NAME + ", " +
		C_LOGO + ", " +
		C_SITE + ", " +
		C_SELECTED + ")" +
		" VALUES (?, ?, ?, ?)";
}

class ChannelDao(private val database: SQLiteDatabase) extends DAO(database) {
	/**
	  * ************************
	  * ******** CREATE ********
	  * ************************
	  */
	private lazy val statementINSERT = database.compileStatement(STATEMENT_INSERT)

	def addChannel(entity: Channel) = {
		statementINSERT.clearBindings
		statementINSERT.bindString(1, entity.name)
		statementINSERT.bindLong(2, entity.logo)
		statementINSERT.bindString(3, entity.site)
		statementINSERT.bindLong(4, entity.selectedValue)
		statementINSERT.executeInsert
	}

	/**
	  * ************************
	  * ********* READ *********
	  * ************************
	  */

	def getAllChannelsList = {
		val listOfChannels = ListBuffer[Channel]()
		val cursor = getAllChannelsCursor

		if (cursor.moveToFirst) {
			do {
				val channel = buildChannelFromCursor(cursor)
				if (channel != null) listOfChannels += channel
			} while (cursor.moveToNext)
		}
		if (!cursor.isClosed) cursor.close

		listOfChannels
	}

	def getAllChannelsJavaList = getAllChannelsList toList

	private def getAllChannelsCursor =
		database.query(
			TABLE_NAME_CHANNELS,
			Array(SQLQueries.ID, C_NAME, C_LOGO, C_SITE, C_SELECTED),
			null, null, null, null, C_NAME)

	private def buildChannelFromCursor(cursor: Cursor) = {
		if (cursor.getCount == 0) null
		else {
			val idIndex = cursor.getColumnIndex(SQLQueries.ID)
			val id = cursor.getLong(idIndex)

			val nameIndex = cursor.getColumnIndex(C_NAME)
			val name = cursor.getString(nameIndex)

			val logoIndex = cursor.getColumnIndex(C_LOGO)
			val logo = cursor.getLong(logoIndex)

			val siteIndex = cursor.getColumnIndex(C_SITE)
			val site = cursor.getString(siteIndex)

			val selectedIndex = cursor.getColumnIndex(C_SELECTED)
			val selected = cursor.getLong(selectedIndex)

			new Channel(id, name, logo, site, selected)
		}
	}

	/**
	  * ************************
	  * ******** UPDATE ********
	  * ************************
	  */

	def selectChannel(channel: Channel) {
		if (channel != null) {
			channel.select(!channel.isSelected)
			update(channel)
		}
	}

	private def update(channel: Channel) = {
		val values = new ContentValues
		values.put(C_SELECTED, channel.selectedValue)
		updateDatabase(values, channel.ID)
	}

	private def updateDatabase(values: ContentValues, channelID: Long) = super.update(TABLE_NAME_CHANNELS, values, channelID)

	/**
	  * ************************
	  * ******** DELETE ********
	  * ************************
	  */
	def deleteChannel(channelID: Long) = super.delete(TABLE_NAME_CHANNELS, channelID)
}