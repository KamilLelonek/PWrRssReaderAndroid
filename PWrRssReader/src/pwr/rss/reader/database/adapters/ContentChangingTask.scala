package pwr.rss.reader.database.adapters

import android.os.AsyncTask
import android.support.v4.content.Loader

abstract class ContentChangingTask[T1](loader: Loader[Any]) extends AsyncTask[T1, Unit, Unit] {
	override def onPostExecute(result: Unit) = loader.onContentChanged
}