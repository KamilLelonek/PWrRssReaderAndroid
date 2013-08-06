package pwr.rss.reader.database.tables

import android.database.sqlite.SQLiteDatabase

trait Table {
	def onCreate(database: SQLiteDatabase)
	def onUpgrade(database: SQLiteDatabase)
}