package pwr.rss.reader.web

import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object HttpConnection {
	//	final lazy val GET_FEEDS_FROM_CHANNELS = "http://192.168.1.110:9000/feedsByIdAndTime/"
	final lazy val GET_FEEDS_FROM_CHANNELS = "http://pwrrssreader.herokuapp.com/feedsByIdAndTime/"
	final lazy val EMPTY_JSON = "{}"

	def getInputString(lastUpdateTimes: List[Long], selectedChannelsIds: List[Int]) =
		inputStreamToString(openInputStream(lastUpdateTimes, selectedChannelsIds))

	private def openInputStream(lastUpdateTimes: List[Long], selectedChannelsIds: List[Int]) = {
		try {
			val url = buildUrl(lastUpdateTimes, selectedChannelsIds)
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

	private def buildUrl(lastUpdateTimes: List[Long], selectedChannelsIds: List[Int]) =
		new URL(
			GET_FEEDS_FROM_CHANNELS +
				selectedChannelsIds.mkString("&") +
				"/" +
				lastUpdateTimes.mkString("&"))

	private def openUrlConnection(url: URL) = url.openConnection.asInstanceOf[HttpURLConnection]

	private def inputStreamToString(is: InputStream) = {
		val inputStreamReader = new InputStreamReader(is)
		val bufferedReader = new BufferedReader(inputStreamReader)
		Iterator continually bufferedReader.readLine takeWhile (_ != null) mkString
	}

	private def inputStreamToString2(is: InputStream): String =
		scala.io.Source.fromInputStream(is).mkString
}