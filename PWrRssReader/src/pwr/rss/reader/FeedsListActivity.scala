package pwr.rss.reader

import com.slidingmenu.lib.app.SlidingFragmentActivity
import android.os.Bundle
import pwr.rss.reader.fragments.SlideMenuFragment
import com.actionbarsherlock.view.MenuItem
import com.actionbarsherlock.view.Menu
import com.actionbarsherlock.view.MenuItem.OnActionExpandListener
import com.actionbarsherlock.widget.SearchView
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener
import android.support.v4.app.FragmentTransaction
import pwr.rss.reader.fragments.ListMenuFragment
import android.view.KeyEvent
import pwr.rss.reader.fragments.FeedsListFragment
import pwr.rss.reader.fragments.ListMenuFragmentHC
import pwr.rss.reader.fragments.ListMenuFragmentGB
import FeedsListActivity._
import com.actionbarsherlock.view.Window

object FeedsListActivity {
  final lazy val NEW_API = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB
}

class FeedsListActivity extends SlidingFragmentActivity with OnMenuListActionListener {
  private lazy val application = getApplication.asInstanceOf[ApplicationObject]
  private lazy val fragmentManager = getSupportFragmentManager
  private lazy val feedsListFragment = fragmentManager.findFragmentById(R.id.feed_list_fragment).asInstanceOf[FeedsListFragment]
  private lazy val slidingMenuFragment = new SlideMenuFragment
  private lazy val menuFragment =
    if (NEW_API)
      (new ListMenuFragmentHC().setOnMenuActionListener(this))
    else
      (new ListMenuFragmentGB().setOnMenuActionListener(this))

  override def onCreate(savedInstanceState: Bundle) = {
    requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS)
    super.onCreate(savedInstanceState)

    setContentView(R.layout.activity_feeds_list)
    setBehindContentView(R.layout.menu_frame)
    configureSlidingMenu
    configureActionBar

    application.setFirstRun
  }

  override def onResume = {
    super.onResume
    reloadList
  }

  def reloadList = feedsListFragment restartLoader
  def updateUnreadCount = slidingMenuFragment.updateUnreadCount

  private def configureSlidingMenu = {
    val fragmentTransaction = fragmentManager.beginTransaction
    fragmentTransaction.replace(R.id.menu_frame, slidingMenuFragment)
    fragmentTransaction commit
  }

  private def configureActionBar = {
    val actionBar = getSupportActionBar
    actionBar setHomeButtonEnabled (true)
    actionBar setIcon (R.drawable.ic_menu)
    setSlidingActionBarEnabled(true)

    if (application.isFirstRun && application.isConnectedToInternet)
      setSupportProgressBarIndeterminateVisibility(true)
  }

  override def onOptionsItemSelected(item: MenuItem) = {
    item.getItemId match {
      case android.R.id.home => showMenu
      case R.id.menu_list_moreover => menuFragment.show(fragmentManager, "menu")
      case _ =>
    }

    true
  }

  override def notifyMenuRefresh = feedsListFragment.notifyMenuRefresh
  override def notifyMenuMarkAllAsRead = feedsListFragment.notifyMenuMarkAllAsRead

  override def onKeyDown(keyCode: Int, event: KeyEvent) = {
    if (keyCode == KeyEvent.KEYCODE_MENU) toggle
    super.onKeyDown(keyCode, event);
  }

  override def onDestroy = {
    super.onDestroy
    application.performCleanUp
  }
}