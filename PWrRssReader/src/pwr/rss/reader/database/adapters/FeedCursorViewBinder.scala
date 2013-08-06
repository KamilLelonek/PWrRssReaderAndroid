package pwr.rss.reader.database.adapters

import java.util.Date
import android.content.Context
import android.database.Cursor
import android.support.v4.widget.SimpleCursorAdapter
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import pwr.rss.reader.database.tables.TableChannels.C_SITE
import pwr.rss.reader.database.tables.TableFeeds.C_ADDED_DATE
import pwr.rss.reader.database.tables.TableFeeds.C_CHANNEL
import pwr.rss.reader.database.tables.TableFeeds.C_IMAGE
import pwr.rss.reader.database.tables.TableFeeds.C_READ
import pwr.rss.reader.database.tables.TableFeeds.C_TITLE
import pwr.rss.reader.utils.BitmapUtils
import android.graphics.BitmapFactory
import android.graphics.Color
import pwr.rss.reader.utils.CursorFetcher

class FeedCursorViewBinder(context: Context) extends SimpleCursorAdapter.ViewBinder {
	override def setViewValue(view: View, cursor: Cursor, columnIndex: Int) = {
			/**
			  * Get columns indexes from cursor
			  */
			def getColumnIndex(columnName: String) = cursor.getColumnIndex(columnName)
		val indexTitle = getColumnIndex(C_TITLE)
		val indexRead = getColumnIndex(C_READ)
		val indexDate = getColumnIndex(C_ADDED_DATE)
		val indexImage = getColumnIndex(C_IMAGE)
		val indexSite = getColumnIndex(C_SITE)

		val cursorFetcher = new CursorFetcher(cursor, context)

		columnIndex match {
			case `indexTitle` | `indexSite` => {
				val textView = view.asInstanceOf[TextView]
				textView.setTextColor(cursorFetcher.getTextColor)
				val text =
					if (columnIndex == indexTitle)
						cursorFetcher.getFeedTitle
					else
						cursorFetcher.getChannelSite
				textView.setText(text)
				true
			}

			case `indexImage` => {
				val feedImage = cursorFetcher.getFeedImageId
				val imageViewFeedImage = view.asInstanceOf[ImageView]
				imageViewFeedImage.setImageResource(feedImage)
				true
			}

			case `indexDate` => {
				val dateLong = cursorFetcher.getAddedDate
				val textViewFeedDate = view.asInstanceOf[TextView]
				textViewFeedDate.setText(dateLong)
				true
			}

			case _ => false
		}
	}
}