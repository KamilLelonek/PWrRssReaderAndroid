package pwr.rss.reader.database.adapters

import android.database.Cursor
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import pwr.rss.reader.fragments.FeedDetailsFragment

class FeedDetailsPageAdapter(fragmentManager: FragmentManager, cursor: Cursor) extends FragmentStatePagerAdapter(fragmentManager) {
	override def getItem(position: Int): Fragment = FeedDetailsFragment.newInstance(position)
	override def getCount: Int = cursor.getCount
}