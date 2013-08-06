package pwr.rss.reader.cards

import com.fima.cardsui.objects.Card
import android.content.Context
import android.view.View
import pwr.rss.reader.utils.CursorFetcher
import android.database.Cursor
import android.view.View.OnClickListener
import android.view.LayoutInflater
import android.graphics.Color

abstract class MyCard(cursor: Cursor, context: Context) extends Card with OnClickListener {
	protected lazy val cursorFetcher = new CursorFetcher(cursor, context)
	protected lazy val inflater = LayoutInflater.from(context)

	def getCardContent = {
		setOnClickListener(this)
		null
	}

	protected def configureView(view: View)
	override def onClick(v: View)
}