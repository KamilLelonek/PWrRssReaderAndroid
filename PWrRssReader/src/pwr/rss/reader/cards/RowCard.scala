package pwr.rss.reader.cards

import com.fima.cardsui.objects.Card
import android.content.Context
import android.view.View
import android.database.Cursor
import android.widget.TextView
import android.view.LayoutInflater
import pwr.rss.reader.R
import pwr.rss.reader.views.ViewHelper._
import android.widget.ImageView
import pwr.rss.reader.database.tables.TableChannels.C_SITE
import pwr.rss.reader.database.tables.TableFeeds.C_ADDED_DATE
import pwr.rss.reader.database.tables.TableFeeds.C_IMAGE
import pwr.rss.reader.database.tables.TableFeeds.C_READ
import pwr.rss.reader.database.tables.TableFeeds.C_TITLE
import pwr.rss.reader.database.tables.TableFeeds.C_LINK
import java.util.Date
import android.view.View.OnClickListener
import pwr.rss.reader.utils.CursorFetcher
import android.content.Intent
import android.net.Uri

class RowCard(cursor: Cursor, context: Context) extends MyCard(cursor, context) {

	override def getCardContent(context: Context) = {
		super.getCardContent
		val view = LayoutInflater.from(context).inflate(R.layout.list_item, null)
		configureView(view)
		view
	}

	protected override def configureView(view: View) = {
		val textColor = cursorFetcher.getTextColor

		val feedImage = cursorFetcher.getFeedImageId
		findView[ImageView](view, R.id.imageViewFeedImage).setImageResource(feedImage)

		val feedTitle = cursorFetcher.getFeedTitle
		val textViewFeedTitle = findView[TextView](view, R.id.textViewFeedTitle)
		textViewFeedTitle.setText(feedTitle)
		textViewFeedTitle.setTextColor(textColor)

		val channelSite = cursorFetcher.getChannelSite
		val textViewChannelSite = findView[TextView](view, R.id.textViewChannelSite)
		textViewChannelSite.setText(channelSite)
		textViewChannelSite.setTextColor(textColor)

		val dateLong = cursorFetcher.getAddedDate
		findView[TextView](view, R.id.textViewFeedDate).setText(dateLong)
	}

	override def onClick(v: View) = {
		val intent = new Intent(Intent.ACTION_VIEW)
		intent.setData(Uri.parse(cursorFetcher.getChannelSite))
		context.startActivity(intent)
	}
}