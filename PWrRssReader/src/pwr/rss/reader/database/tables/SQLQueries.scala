package pwr.rss.reader.database.tables

import android.provider.BaseColumns

object SQLQueries {
  val ID = BaseColumns._ID
  val C_ID = ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "

  val DROP_TABLE = "DROP TABLE IF EXISTS "
  val CREATE_TABLE = "CREATE TABLE "
  val INSERT = "INSERT INTO "

  val TYPE_STRING = " TEXT NOT NULL UNIQUE, "
  val TYPE_DATE = " INTEGER NOT NULL DEFAULT CURRENT_DATE, "
  val TYPE_BOOLEAN = " INTEGER NOT NULL"
  val TYPE_NUMBER = " INTEGER NOT NULL, "

  val DEFAULT_NOT_READ = " DEFAULT 0"
  val DEFAULT_SELECTED = " DEFAULT 1"
}