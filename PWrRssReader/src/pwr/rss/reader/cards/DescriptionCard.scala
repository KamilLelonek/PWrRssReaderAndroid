package pwr.rss.reader.cards

import com.fima.cardsui.objects.Card
import android.content.Context
import android.view.View
import android.widget.TextView
import android.view.LayoutInflater
import pwr.rss.reader.R
import android.database.Cursor
import android.view.View.OnClickListener
import pwr.rss.reader.views.ViewHelper._
import android.content.Intent
import android.net.Uri
import pwr.rss.reader.utils.CursorFetcher
import android.text.Html
import android.text.method.LinkMovementMethod
import android.text.SpannableString
import android.text.style.UnderlineSpan

class DescriptionCard(cursor: Cursor, context: Context) extends MyCard(cursor, context) {

	override def getCardContent(context: Context) = {
		super.getCardContent
		val view = LayoutInflater.from(context).inflate(R.layout.card_description, null)
		configureView(view)
		view
	}

	protected def configureView(view: View) = {
		val spannableHTML = Html.fromHtml(cursorFetcher.getDescription)
		val description = findView[TextView](view, R.id.card_description)
		description.setText(spannableHTML)
		description.setTextColor(cursorFetcher.getTextColor)
		description.setMovementMethod(LinkMovementMethod.getInstance)
	}

	override def onClick(v: View) = {
		val intent = new Intent(Intent.ACTION_VIEW)
		intent.setData(Uri.parse(cursorFetcher.getFeedLink))
		try {
			context.startActivity(intent)
		}
		catch {
			case _: Exception =>
		}
	}
}