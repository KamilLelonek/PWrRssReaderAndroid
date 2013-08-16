package pwr.rss.reader.cards

import java.net.URL

import android.app.Dialog
import android.content.Context
import android.database.Cursor
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.ImageView
import pwr.rss.reader.R
import pwr.rss.reader.utils.BitmapUtils
import pwr.rss.reader.views.ViewHelper.findView

class ImageCard(cursor: Cursor, context: Context) extends MyCard(cursor, context) {
	private lazy val imageLink = cursorFetcher.getImageLink
	private lazy val imageDialog = new Dialog(context, R.style.Dialog_No_Border)
	private lazy val imageView = new ImageView(context)
	private lazy val url = new URL(imageLink)
	private lazy val bmp = BitmapFactory.decodeStream(url.openConnection.getInputStream)

	override def getCardContent(context: Context) = {
		super.getCardContent
		val view = LayoutInflater.from(context).inflate(R.layout.card_image, null)
		configureView(view)
		view
	}

	protected def configureView(view: View) = {
		val imageView = findView[ImageView](view, R.id.card_image)
		BitmapUtils.downloadAndSetBitmapFromLink(imageLink, imageView)
		imageDialog.getWindow.requestFeature(Window.FEATURE_NO_TITLE)
		imageDialog.setCanceledOnTouchOutside(true)
	}

	override def onClick(v: View) = {
		getLargeLink match {
			case (link, false) => {
				BitmapUtils.downloadAndSetBitmapFromLink(link, imageView)
				imageDialog.setContentView(imageView)
				imageDialog.show
			}
			case (_, true) =>
		}
	}

	private def getLargeLink = {
		val replacedLink = imageLink.replaceFirst("small", "large")
		(replacedLink, replacedLink.equals(imageLink))
	}
}