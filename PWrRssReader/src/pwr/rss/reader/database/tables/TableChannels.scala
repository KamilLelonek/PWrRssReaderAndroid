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
import pwr.rss.reader.database.tables.ChannelIDs._

object TableChannels extends Table {
	val TABLE_NAME_CHANNELS = "CHANNELS"

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
		database.execSQL(CREATE_TABLE_CHANNELS)
		insertInitialChannels(database)
	}

	private def insertInitialChannels(database: SQLiteDatabase) = {
		val channelDao = new ChannelDao(database)
		channelDao.addChannel(new Channel(ID_NaPWr, "NaPWr", getDrawableIdForChannel(ID_NaPWr), "http://www.napwr.pl/", 1))
		channelDao.addChannel(new Channel(ID_Samorzad, "Samorząd PWr", getDrawableIdForChannel(ID_Samorzad), "http://samorzad.pwr.wroc.pl/", 1))
		channelDao.addChannel(new Channel(ID_eStudent, "eStudent", getDrawableIdForChannel(ID_eStudent), "http://www.estudent.pwr.wroc.pl/", 1))
		channelDao.addChannel(new Channel(ID_PWr, "PWr", getDrawableIdForChannel(ID_PWr), "http://www.portal.pwr.wroc.pl/", 1))
		channelDao.addChannel(new Channel(ID_PWr_W1, "W1: WA", getDrawableIdForChannel(ID_PWr_W1), "http://www.wa.pwr.wroc.pl/rss,21.xml"))
		channelDao.addChannel(new Channel(ID_SS_W1, "Samorząd W1", getDrawableIdForChannel(ID_SS_W1), "http://wa.samorzad.pwr.wroc.pl/feed/"))
		channelDao.addChannel(new Channel(ID_PWr_W2, "W2: WBLiW", getDrawableIdForChannel(ID_PWr_W2), "http://www.wbliw.pwr.wroc.pl/rss,31.xml"))
		channelDao.addChannel(new Channel(ID_SS_W2, "Samorząd W2", getDrawableIdForChannel(ID_SS_W2), "http://wbliw.samorzad.pwr.wroc.pl/feed/"))
		channelDao.addChannel(new Channel(ID_PWr_W3, "W3: WCH", getDrawableIdForChannel(ID_PWr_W3), "http://www.wch.pwr.wroc.pl/rss,11.xml"))
		channelDao.addChannel(new Channel(ID_SS_W3, "Samorząd W3", getDrawableIdForChannel(ID_SS_W3), "http://wch.samorzad.pwr.wroc.pl/feed/"))
		channelDao.addChannel(new Channel(ID_PWr_W4, "W4: WEKA", getDrawableIdForChannel(ID_PWr_W4), "http://www.weka.pwr.wroc.pl/rss,41.xml"))
		channelDao.addChannel(new Channel(ID_SS_W4, "Samorząd W4", getDrawableIdForChannel(ID_SS_W4), "http://weka.samorzad.pwr.wroc.pl/feed/"))
		channelDao.addChannel(new Channel(ID_PWr_W5, "W5: WENY", getDrawableIdForChannel(ID_PWr_W5), "http://www.weny.pwr.wroc.pl/rss,51.xml"))
		channelDao.addChannel(new Channel(ID_SS_W5, "Samorząd W5", getDrawableIdForChannel(ID_SS_W5), "http://weny.samorzad.pwr.wroc.pl/feed/"))
		channelDao.addChannel(new Channel(ID_PWr_W6, "W6: WGGG", getDrawableIdForChannel(ID_PWr_W6), "http://www.wggg.pwr.wroc.pl/rss,61.xml"))
		channelDao.addChannel(new Channel(ID_SS_W6, "Samorząd W6", getDrawableIdForChannel(ID_SS_W6), "http://wggg.samorzad.pwr.wroc.pl/feed/"))
		channelDao.addChannel(new Channel(ID_PWr_W7, "W7: WIŚ", getDrawableIdForChannel(ID_PWr_W7), "http://www.wis.pwr.wroc.pl/rss,71.xml"))
		channelDao.addChannel(new Channel(ID_SS_W7, "Samorząd W7", getDrawableIdForChannel(ID_SS_W7), "http://wis.samorzad.pwr.wroc.pl/feed/"))
		channelDao.addChannel(new Channel(ID_PWr_W8, "W8: WIZ", getDrawableIdForChannel(ID_PWr_W8), "http://www.wiz.pwr.wroc.pl/rss,1.xml"))
		channelDao.addChannel(new Channel(ID_SS_W8, "Samorząd W8", getDrawableIdForChannel(ID_SS_W8), "http://wiz.samorzad.pwr.wroc.pl/feed/"))
		channelDao.addChannel(new Channel(ID_PWr_W9, "W9: WME", getDrawableIdForChannel(ID_PWr_W9), "http://www.wme.pwr.wroc.pl/rss,81.xml"))
		channelDao.addChannel(new Channel(ID_SS_W9, "Samorząd W9", getDrawableIdForChannel(ID_SS_W9), "http://wme.samorzad.pwr.wroc.pl/feed/"))
		channelDao.addChannel(new Channel(ID_PWr_W10, "W10: WM", getDrawableIdForChannel(ID_PWr_W10), "http://www.wm.pwr.wroc.pl/rss,91.xml"))
		channelDao.addChannel(new Channel(ID_SS_W10, "Samorząd W10", getDrawableIdForChannel(ID_SS_W10), "http://wm.samorzad.pwr.wroc.pl/feed/"))
		channelDao.addChannel(new Channel(ID_PWr_W11, "W11: WPPT", getDrawableIdForChannel(ID_PWr_W11), "http://www.wppt.pwr.wroc.pl/rss,101.xml"))
		channelDao.addChannel(new Channel(ID_SS_W11, "Samorząd W11", getDrawableIdForChannel(ID_SS_W11), "http://wppt.samorzad.pwr.wroc.pl/feed/"))
		channelDao.addChannel(new Channel(ID_PWr_W12, "W12: WEMiF", getDrawableIdForChannel(ID_PWr_W12), "http://www.wemif.pwr.wroc.pl/rss,111.xml"))
		channelDao.addChannel(new Channel(ID_SS_W12, "Samorząd W12", getDrawableIdForChannel(ID_SS_W12), "http://wemif.samorzad.pwr.wroc.pl/feed/"))
	}

	def onUpgrade(database: SQLiteDatabase) = {
		database.execSQL(SQLQueries.DROP_TABLE + TABLE_NAME_CHANNELS)
		onCreate(database)
	}

	def getDrawableIdForChannel(channelID: Long) = channelID match {
		case ID_NaPWr => R.drawable.channel_napwr
		case ID_Samorzad => R.drawable.channel_samorzad
		case ID_eStudent => R.drawable.channel_estudent
		case ID_PWr => R.drawable.channel_pwr
		case ID_PWr_W1 => R.drawable.channel_pwr_w1
		case ID_SS_W1 => R.drawable.channel_ss_w1
		case ID_PWr_W2 => R.drawable.channel_pwr_w2
		case ID_SS_W2 => R.drawable.channel_ss_w2
		case ID_PWr_W3 => R.drawable.channel_pwr_w3
		case ID_SS_W3 => R.drawable.channel_ss_w3
		case ID_PWr_W4 => R.drawable.channel_pwr_w4
		case ID_SS_W4 => R.drawable.channel_ss_w4
		case ID_PWr_W5 => R.drawable.channel_pwr_w5
		case ID_SS_W5 => R.drawable.channel_ss_w5
		case ID_PWr_W6 => R.drawable.channel_pwr_w6
		case ID_SS_W6 => R.drawable.channel_ss_w6
		case ID_PWr_W7 => R.drawable.channel_pwr_w7
		case ID_SS_W7 => R.drawable.channel_ss_w7
		case ID_PWr_W8 => R.drawable.channel_pwr_w8
		case ID_SS_W8 => R.drawable.channel_ss_w8
		case ID_PWr_W9 => R.drawable.channel_pwr_w9
		case ID_SS_W9 => R.drawable.channel_ss_w9
		case ID_PWr_W10 => R.drawable.channel_pwr_w10
		case ID_SS_W10 => R.drawable.channel_ss_w10
		case ID_PWr_W11 => R.drawable.channel_pwr_w11
		case ID_SS_W11 => R.drawable.channel_ss_w11
		case ID_PWr_W12 => R.drawable.channel_pwr_w12
		case ID_SS_W12 => R.drawable.channel_ss_w12
	}
}