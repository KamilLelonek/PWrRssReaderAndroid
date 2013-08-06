package pwr.rss.reader.json

import com.google.gson.Gson

object PWrJSONParser {
	def getFeeds(inputString: String) = {
		val gson = new Gson
		val feedsCollection = gson.fromJson[FeedsCollection](inputString, classOf[FeedsCollection])

		if (feedsCollection != null)
			feedsCollection.feeds
		else null
	}
}