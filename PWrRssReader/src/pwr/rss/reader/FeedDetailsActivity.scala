package pwr.rss.reader

import com.actionbarsherlock.app.SherlockFragmentActivity
import com.actionbarsherlock.view.MenuItem
import com.viewpagerindicator.UnderlinePageIndicator
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import pwr.rss.reader.database.adapters.FeedDetailsPageAdapter
import pwr.rss.reader.web.DownloadService
import pwr.rss.reader.fragments.FeedsListFragment
import pwr.rss.reader.views.ViewHelper._
import pwr.rss.reader.utils.BackgroundTasker._
import pwr.rss.reader.fragments.FeedDetailsFragment
import undobar.controller.library.UndoBarController
import undobar.controller.library.UndoBarListener
import java.io.Serializable
import pwr.rss.reader.database.dao.Feed
import android.view.ViewGroup
import android.os.Handler
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import android.content.BroadcastReceiver
import android.content.Context
import android.view.KeyEvent
import java.util.ArrayList

class FeedDetailsActivity extends SherlockFragmentActivity with UndoBarListener {
	private lazy val application = getApplication.asInstanceOf[ApplicationObject]
	private lazy val cursor = application.getCurrentCursor
	private lazy val fragmentManager = getSupportFragmentManager
	private lazy val pageAdapter = new FeedDetailsPageAdapter(fragmentManager, cursor)
	private lazy val viewPager = findView[ViewPager](this, R.id.pager)
	private lazy val viewIndicator = findView[UnderlinePageIndicator](this, R.id.indicator)
	private lazy val undoBarController = new UndoBarController(findViewById(android.R.id.content))
	private lazy val flagAction = getIntent.getStringExtra(FeedDetailsFragment.FLAG_ACTION)
	private lazy val feed = getIntent.getSerializableExtra(FeedDetailsFragment.FEED).asInstanceOf[Feed]
	private lazy val localBroadcastManager = LocalBroadcastManager.getInstance(this)
	private lazy val downloadFinishedReceiver = new BroadcastReceiver {
		override def onReceive(context: Context, intent: Intent) =
			if (DownloadService.ACTION_DOWNLOAD_COMPLETED.equals(intent.getAction))
				restart
	}

	override def onCreate(savedInstanceState: Bundle) = {
		super.onCreate(savedInstanceState)
		getSupportActionBar.setDisplayHomeAsUpEnabled(true)
		setContentView(R.layout.activity_feed_details)
		viewPager.setAdapter(pageAdapter)
		viewIndicator.setViewPager(viewPager)
		undoBarController.registerUndoBarListener(this)
		this.localBroadcastManager.registerReceiver(downloadFinishedReceiver,
			new IntentFilter(DownloadService.ACTION_DOWNLOAD_COMPLETED))
	}

	override def onPostResume = {
		super.onPostResume

		setCurrentItemInPager
		showDelayedUndoBar
	}

	override def onPause = {
		super.onPause
		this.undoBarController.hideUndoBar
		this.undoBarController.unregisterUndoBarListener(this)
		this.localBroadcastManager.unregisterReceiver(downloadFinishedReceiver)
	}

	def showDelayedUndoBar = performDelayed(showUndoBar)

	def setCurrentItemInPager = {
		val currentItemPosition = getIntent.getIntExtra(FeedsListFragment.FLAG_POSITION, 1)
		viewPager.setCurrentItem(currentItemPosition)
	}

	def showUndoBar() = {
		flagAction match {
			case FeedDetailsFragment.FLAG_READ => undoBarController.showUndoBar(null, R.string.undobar_message_read_one)
			case FeedDetailsFragment.FLAG_UNREAD => undoBarController.showUndoBar(null, R.string.undobar_message_unread)
			case FeedDetailsFragment.FLAG_DISMISSED => undoBarController.showUndoBar(null, R.string.undobar_message_feed_dismissed)
			case _ =>
		}
	}

	override def onUndo(token: Serializable) = {
		flagAction match {
			case FeedDetailsFragment.FLAG_READ => application.markFeedAsUnread(feed)
			case FeedDetailsFragment.FLAG_UNREAD => application.markFeedAsRead(feed)
			case FeedDetailsFragment.FLAG_DISMISSED => application.addFeed(feed)
			case _ =>
		}

		restart
	}

	private def restart = {
		finish
		overridePendingTransition(0, 0)
		startActivity(getSelfIntent)
		overridePendingTransition(0, 0)
	}

	def getSelfIntent = {
		val currentItemPosition = getIntent.getIntExtra(FeedsListFragment.FLAG_POSITION, 1)
		val selfIntent = new Intent(FeedDetailsActivity.this, classOf[FeedDetailsActivity])
		selfIntent.putExtra(FeedsListFragment.FLAG_POSITION, currentItemPosition);
		selfIntent
	}

	override def onMenuItemSelected(featureId: Int, item: MenuItem) = {
		item.getItemId match {
			case android.R.id.home => finish
			case _ =>
		}
		true
	}

	private def getFragment = {
		val fragmentsArray = fragmentManager.getClass.getDeclaredField("mAdded")
		fragmentsArray.setAccessible(true);
		val fragments = fragmentsArray.get(fragmentManager).asInstanceOf[ArrayList[FeedDetailsFragment]]

		if (fragments.size == 0) null
		else if (fragments.size == 1) fragments.get(0)
		else fragments.get(1)
	}
}