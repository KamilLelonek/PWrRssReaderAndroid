package pwr.rss.reader.utils

import android.os.Handler
import scala.concurrent._
import ExecutionContext.Implicits.global

object BackgroundTasker {
	def performDelayed(function: () => Any) =
		(new Handler).postDelayed(new Runnable { override def run = { function() } }, 300L)
}