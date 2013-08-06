package pwr.rss.reader.database.adapters

import android.content.Context
import android.database.Cursor
import android.support.v4.widget.SimpleCursorAdapter
import pwr.rss.reader.R
import pwr.rss.reader.database.tables.TableChannels.C_SITE
import pwr.rss.reader.database.tables.TableFeeds.C_ADDED_DATE
import pwr.rss.reader.database.tables.TableFeeds.C_IMAGE
import pwr.rss.reader.database.tables.TableFeeds.C_TITLE

object FeedCursorAdapter { private lazy val LIST_COUNT = 1000 }

class FeedCursorAdapter(context: Context, cursor: Cursor)
		extends SimpleCursorAdapter(
			context,
			R.layout.list_item,
			cursor,
			Array(C_TITLE, C_SITE, C_ADDED_DATE, C_IMAGE),
			Array(R.id.textViewFeedTitle, R.id.textViewChannelSite, R.id.textViewFeedDate, R.id.imageViewFeedImage),
			0) {

	setViewBinder(new FeedCursorViewBinder(context))

	override def getViewTypeCount = if (getCount < 1) FeedCursorAdapter.LIST_COUNT else getCount
	override def getItemViewType(position: Int) = position
}