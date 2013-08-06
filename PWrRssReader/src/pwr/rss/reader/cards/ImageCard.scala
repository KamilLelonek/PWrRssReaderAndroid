package pwr.rss.reader.cards

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import pwr.rss.reader.R
import pwr.rss.reader.utils.BitmapUtils
import pwr.rss.reader.views.ViewHelper.findView

class ImageCard(cursor: Cursor, context: Context) extends MyCard(cursor, context) {

	override def getCardContent(context: Context) = {
		super.getCardContent
		val view = LayoutInflater.from(context).inflate(R.layout.card_image, null)
		configureView(view)
		view
	}

	protected def configureView(view: View) = {
		val imageView = findView[ImageView](view, R.id.card_image)
		val imageLink = cursorFetcher.getImageLink
		BitmapUtils.downloadAndSetBitmapFromLink(imageLink, imageView)
	}

	override def onClick(v: View) = {}
}