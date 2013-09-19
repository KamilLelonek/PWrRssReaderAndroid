package pwr.rss.reader.json

import com.google.gson.Gson

object PWrJSONParser {
  def getFeeds(inputString: String) =
    try {
      val feedsCollection = (new Gson).fromJson[FeedsCollection](inputString, classOf[FeedsCollection])
      feedsCollection.feeds
    }
  	catch { case e: Exception => null }
}