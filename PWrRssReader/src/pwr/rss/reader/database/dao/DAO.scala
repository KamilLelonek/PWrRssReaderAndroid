package pwr.rss.reader.database.dao

import android.database.sqlite.SQLiteDatabase
import pwr.rss.reader.database.tables.SQLQueries
import android.content.ContentValues

class DAO(private val database: SQLiteDatabase) {

	protected def update(tableName: String, values: ContentValues, entityID: Long) =
		database.update(tableName, values, SQLQueries.ID + " = ?", Array[String](entityID toString))

	protected def delete(tableName: String, entityID: Long) =
		database.delete(tableName, SQLQueries.ID + " = ?", Array[String](entityID toString))
}