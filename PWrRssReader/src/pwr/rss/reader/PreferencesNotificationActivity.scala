package pwr.rss.reader

import com.actionbarsherlock.app.SherlockPreferenceActivity
import android.os.Bundle
import com.actionbarsherlock.view.MenuItem

class PreferencesNotificationActivity extends GeneralPreferenceActivity {
	override def onCreate(savedInstanceState: Bundle) = {
		super.onCreate(savedInstanceState)
		addPreferencesFromResource(R.xml.preferences_notifications)
	}
}