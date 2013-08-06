package pwr.rss.reader.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import pwr.rss.reader.R
import pwr.rss.reader.database.dao.Channel
import pwr.rss.reader.views.ViewHelper.findView
import android.view.View.OnClickListener
import android.view.View
import pwr.rss.reader.ApplicationObject
import undobar.controller.library.UndoBarController
import undobar.controller.library.UndoBarListener
import java.io.Serializable
import pwr.rss.reader.utils.UndoableCollection
import java.util.ArrayList
import java.lang.Long
import android.support.v4.content.LocalBroadcastManager
import android.content.Intent
import pwr.rss.reader.web.DownloadService
import pwr.rss.reader.fragments.FeedsListFragment

class ChannelView(context: Context, val channel: Channel) extends LinearLayout(context, null)
		with OnClickListener
		with UndoBarListener {

	private lazy val applicationObject = context.getApplicationContext.asInstanceOf[ApplicationObject]
	private lazy val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE).asInstanceOf[LayoutInflater]
	private lazy val imageViewChannelLogo = findView[ImageView](this, R.id.imageViewChannelLogo)
	private lazy val textViewChannelName = findView[TextView](this, R.id.textViewChannelName)
	private lazy val checkBoxChannelSelected = findView[CheckBox](this, R.id.checkBoxChannelSelected)
	private lazy val textViewChannelUnreadCount = findView[TextView](this, R.id.textViewChannelUnreadCount)
	private lazy val undoBarController = new UndoBarController(this)
	private lazy val localBroadcastManager = LocalBroadcastManager.getInstance(context)
	private lazy val downloadCompletedBroadcast = new Intent(FeedsListFragment.ACTION_REFRESH)

	private def setChannelLogo(resId: Long) = imageViewChannelLogo.setImageResource(resId.toInt)
	private def setName(title: String) = textViewChannelName.setText(title)
	private def setSelection(selected: Boolean) = checkBoxChannelSelected.setChecked(selected)
	private def setUnreadCount(unreadCount: Long) = textViewChannelUnreadCount.setText(unreadCount toString)

	private def clearUnreadCount = textViewChannelUnreadCount.setText("0")
	private def select = checkBoxChannelSelected.setChecked(!checkBoxChannelSelected.isChecked)
	def updateUnreadCount = setUnreadCount(applicationObject.countUnreadInChannel(channel.ID))

	def onClick(view: View) = view match {
		case `checkBoxChannelSelected` => applicationObject.selectChannel(channel)
		case `imageViewChannelLogo` | `textViewChannelName` => {
			select
			applicationObject.selectChannel(channel)
		}
		case `textViewChannelUnreadCount` => markChannelAsRead
	}

	private def markChannelAsRead = {
		showUndoBar
		clearUnreadCount
	}

	private def showUndoBar = {
		val feedsMarkedAsReadIDs = applicationObject.markChannelAsRead(channel.ID)
		if (!feedsMarkedAsReadIDs.isEmpty) {
			val undoableAction = new UndoableCollection(feedsMarkedAsReadIDs, UndoableCollection.Action.READ)
			undoBarController.showUndoBar(undoableAction, R.string.undobar_message_read_channel)
			localBroadcastManager.sendBroadcast(downloadCompletedBroadcast)
		}
	}

	override def onDetachedFromWindow = {
		undoBarController.unregisterUndoBarListener(this)
		super.onDetachedFromWindow
	}

	override def onUndo(token: Serializable) = {
		val undoableCollection = token.asInstanceOf[UndoableCollection]

		if (UndoableCollection.Action.READ == undoableCollection.action)
			revertMarkingAsRead(undoableCollection)
		localBroadcastManager.sendBroadcast(downloadCompletedBroadcast)
	}

	private def revertMarkingAsRead(undoableCollection: UndoableCollection) = {
		val readFeedsIDs = undoableCollection.list.asInstanceOf[ArrayList[Long]]
		applicationObject.markAsUnread(readFeedsIDs)
		updateUnreadCount
	}

	inflater.inflate(R.layout.channel_layout, this, true)
	setName(channel name)
	setSelection(channel.isSelected)
	setChannelLogo(channel.logo)
	undoBarController.registerUndoBarListener(this)
	updateUnreadCount

	imageViewChannelLogo.setOnClickListener(this)
	textViewChannelName.setOnClickListener(this)
	textViewChannelUnreadCount.setOnClickListener(this)
	checkBoxChannelSelected.setOnClickListener(this)
}