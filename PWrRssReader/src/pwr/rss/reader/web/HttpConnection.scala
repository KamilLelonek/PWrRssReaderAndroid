package pwr.rss.reader.web

import java.io.InputStream
import java.net.HttpURLConnection
import java.io.ByteArrayInputStream
import java.io.BufferedInputStream
import java.net.URL
import java.io.IOException
import java.io.InputStreamReader
import java.io.BufferedReader
import android.util.Log

object HttpConnection {
	// final lazy val GET_FEEDS_FROM = "http://192.168.1.110:9000/feedsFrom/"
	final lazy val GET_FEEDS_FROM = "http://pwrrssreader.herokuapp.com/feedsFrom/";
	final lazy val EMPTY_JSON = "{}"
	private final lazy val TIMEOUT = 10 * 1000 // 10s

	def getInputString(lastUpdateTime: Long) = inputStreamToString(openInputStream(lastUpdateTime))

	private def openInputStream(lastUpdateTime: Long) = {
		try {
			val url = new URL(GET_FEEDS_FROM + lastUpdateTime)
			val urlConnection = url.openConnection.asInstanceOf[HttpURLConnection]
			urlConnection.setReadTimeout(TIMEOUT)
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

	private def inputStreamToString(is: InputStream) = {
		val inputStreamReader = new InputStreamReader(is)
		val bufferedReader = new BufferedReader(inputStreamReader)
		Iterator continually bufferedReader.readLine takeWhile (_ != null) mkString
	}

	private def inputStreamToString2(is: InputStream): String =
		scala.io.Source.fromInputStream(is).mkString
}