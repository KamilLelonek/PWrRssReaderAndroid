package pwr.rss.reader.database.manager

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import pwr.rss.reader.database.tables.TableChannels
import pwr.rss.reader.database.tables.TableFeeds
import pwr.rss.reader.database.tables.TableFeeds
import pwr.rss.reader.database.tables.TableChannels
import pwr.rss.reader.database.manager.DataBaseHelper._

object DataBaseHelper {
	val DATABASE_NAME = "RSS.db"
	val DATABASE_VERSION = 1
}

class DataBaseHelper(context: Context) extends SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
	override def onCreate(database: SQLiteDatabase) {
		TableChannels.onCreate(database)
		TableFeeds.onCreate(database)
	}

	override def onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
		TableChannels.onUpgrade(database)
		TableFeeds.onUpgrade(database)
	}
}