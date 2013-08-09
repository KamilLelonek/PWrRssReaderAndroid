package pwr.rss.reader.utils

import java.io.InputStream
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.content.Context
import BitmapUtils._
import java.net.URL
import android.os.AsyncTask
import android.widget.ImageView
import pwr.rss.reader.database.dao.Feed

object BitmapUtils {
	private lazy val FILE_NAME = "feed"

	private lazy val bitmapDownloader = new BitmapCachedDownloader

	def downloadAndSetBitmapFromLink(imageLink: String, imageView: ImageView) =
		bitmapDownloader.download(imageLink, imageView)

	def apply(context: Context) = new BitmapUtils(context)
}

class BitmapUtils(context: Context) {
	def getBitmapFromResource(resourceId: Int) = BitmapFactory.decodeResource(context.getResources, resourceId)
	/**
	  * Deleting bitmap from file
	  */
	def deleteBitmapForFeed(feed: Feed): Boolean = deleteBitmapForFeed(feed.title.hashCode)

	def deleteBitmapForFeed(feedHashCode: Int) = context.deleteFile(FILE_NAME + feedHashCode)

	/**
	  * Saving bitmap to file
	  */
	def writeBitmapForFeed(feed: Feed, bitmap: Bitmap): Boolean =
		writeBitmapForFeed(feed.title.hashCode, bitmap)

	def writeBitmapForFeed(feedHashCode: Int, bitmap: Bitmap) =
		bitmap.compress(Bitmap.CompressFormat.PNG, 0, getInternalOutputStream(feedHashCode))
	private def getInternalOutputStream(feedHashCode: Int) = context.openFileOutput(FILE_NAME + feedHashCode, Context.MODE_PRIVATE)

	/**
	  * Reading bitmap from file
	  */
	def readBitmapForFeed(feed: Feed): Bitmap = readBitmapForFeed(feed.title.hashCode)

	def readBitmapForFeed(feedHashCode: Int) =
		try {
			BitmapFactory.decodeStream(getInternalInputStream(feedHashCode))
		}
		catch {
			case _: Exception => null
		}
	private def getInternalInputStream(feedHashCode: Int) = context.openFileInput(FILE_NAME + feedHashCode)
}