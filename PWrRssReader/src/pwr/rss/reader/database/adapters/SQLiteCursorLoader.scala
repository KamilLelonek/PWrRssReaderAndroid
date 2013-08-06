package pwr.rss.reader.database.adapters

import android.content.Context
import pwr.rss.reader.ApplicationObject

class SQLiteCursorLoader(context: Context) extends AbstractCursorLoader(context) {
	private lazy val applicationObject = context.getApplicationContext.asInstanceOf[ApplicationObject]

	override def buildCursor = applicationObject.getCurrentCursor
}