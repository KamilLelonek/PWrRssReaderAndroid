package pwr.rss.reader.database.tables

import android.database.sqlite.SQLiteDatabase
import pwr.rss.reader.database.tables.SQLQueries.DEFAULT_SELECTED
import pwr.rss.reader.database.tables.SQLQueries.TYPE_BOOLEAN
import pwr.rss.reader.database.tables.SQLQueries.TYPE_STRING
import pwr.rss.reader.database.tables.SQLQueries.TYPE_NUMBER
import pwr.rss.reader.database.tables.SQLQueries.C_ID
import pwr.rss.reader.data.dao.ChannelDao
import pwr.rss.reader.database.dao.Channel
import pwr.rss.reader.R

object TableChannels extends Table {
	val TABLE_NAME_CHANNELS = "CHANNELS";

	val C_NAME = "Name"
	val C_LOGO = "Logo"
	val C_SITE = "Site"
	val C_SELECTED = "Selected"

	private val CREATE_TABLE_CHANNELS =
		SQLQueries.CREATE_TABLE + TABLE_NAME_CHANNELS +
			"(" +
			C_ID +
			C_NAME + TYPE_STRING +
			C_LOGO + TYPE_NUMBER +
			C_SITE + TYPE_STRING +
			C_SELECTED + TYPE_BOOLEAN + DEFAULT_SELECTED +
			");"

	def onCreate(database: SQLiteDatabase) = {
		database.execSQL(CREATE_TABLE_CHANNELS);
		insertInitialChannels(database)
	}

	private def insertInitialChannels(database: SQLiteDatabase) = {
		val channelDao = new ChannelDao(database)
		channelDao.addChannel(new Channel(1, "NaPWr", R.drawable.channel_napwr, "http://www.napwr.pl/"))
		channelDao.addChannel(new Channel(2, "Samorząd PWr", R.drawable.channel_samorzad, "http://samorzad.pwr.wroc.pl/"))
		channelDao.addChannel(new Channel(3, "eStudent", R.drawable.channel_estudent, "http://www.estudent.pwr.wroc.pl/"))
		channelDao.addChannel(new Channel(4, "PWr", R.drawable.channel_pwr, "http://www.portal.pwr.wroc.pl/"))
	}

	def onUpgrade(database: SQLiteDatabase) = {
		database.execSQL(SQLQueries.DROP_TABLE + TABLE_NAME_CHANNELS);
		onCreate(database)
	}

	def getDrawableIdForChannel(channel: Long) = channel match {
		case 1L => R.drawable.channel_napwr
		case 2L => R.drawable.channel_samorzad
		case 3L => R.drawable.channel_estudent
		case 4L => R.drawable.channel_pwr
	}
}