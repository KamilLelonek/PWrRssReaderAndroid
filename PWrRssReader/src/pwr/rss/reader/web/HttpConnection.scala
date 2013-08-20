package pwr.rss.reader.web

import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.List
import scala.collection.JavaConversions._

object HttpConnection {
	//	final lazy val GET_FEEDS_FROM_CHANNELS = "http://192.168.1.110:9000/getAllFeeds/"
	final lazy val GET_FEEDS_FROM_CHANNELS = "http://pwrrssreader.herokuapp.com/getAllFeeds/"
	final lazy val EMPTY_JSON = "{}"
	private final lazy val TIMEOUT = 30 * 1000 // 10s

	def getInputString(lastUpdateTime: Long, selectedChannelsIds: List[Int]) =
		inputStreamToString(openInputStream(lastUpdateTime, selectedChannelsIds))

	private def openInputStream(lastUpdateTime: Long, selectedChannelsIds: List[Int]) = {
		try {
			val url = new URL(GET_FEEDS_FROM_CHANNELS + lastUpdateTime)
			val urlConnection = openUrlConnection(url)
			new BufferedInputStream(urlConnection.getInputStream)
		}
		catch {
			case _: IOException => {
				val emptyJSONarray = EMPTY_JSON.getBytes
				val bais = new ByteArrayInputStream(emptyJSONarray)
				new BufferedInputStream(new BufferedInputStream(bais))
			}
		}
	}

	/* For future use */
	/**
	  * USAGE:
	  *
	  * 	final lazy val GET_FEEDS_FROM_CHANNELS = "http://192.168.1.110:9000/feedsFromChannels/"
	  *  	val url = buildUrl(lastUpdateTime, selectedChannelsIds)
	  *
	  */
	private def buildUrl(lastUpdateTime: Long, selectedChannelsIds: List[Int]) =
		new URL(
			GET_FEEDS_FROM_CHANNELS +
				lastUpdateTime +
				"/" + selectedChannelsIds.mkString("&"))

	private def openUrlConnection(url: URL) = {
		val urlConnection = url.openConnection.asInstanceOf[HttpURLConnection]
		urlConnection.setReadTimeout(TIMEOUT)
		urlConnection
	}

	private def inputStreamToString(is: InputStream) = {
		val inputStreamReader = new InputStreamReader(is)
		val bufferedReader = new BufferedReader(inputStreamReader)
		Iterator continually bufferedReader.readLine takeWhile (_ != null) mkString
	}

	private def inputStreamToString2(is: InputStream): String =
		scala.io.Source.fromInputStream(is).mkString
}