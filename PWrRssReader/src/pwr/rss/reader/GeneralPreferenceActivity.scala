package pwr.rss.reader

import com.actionbarsherlock.app.SherlockPreferenceActivity
import android.os.Bundle
import com.actionbarsherlock.view.MenuItem

class GeneralPreferenceActivity extends SherlockPreferenceActivity {
	override def onCreate(savedInstanceState: Bundle) = {
		super.onCreate(savedInstanceState)
		getSupportActionBar.setDisplayHomeAsUpEnabled(true)
		getSupportActionBar.setIcon(R.drawable.ic_preferences)
	}

	override def onMenuItemSelected(featureId: Int, item: MenuItem) = {
		item.getItemId match {
			case android.R.id.home => onBackPressed
			case _ =>
		}
		true
	}

	override def onBackPressed = {
		finish
		overridePendingTransition(0, 0)
	}
}