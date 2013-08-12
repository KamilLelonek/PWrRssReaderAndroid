package pwr.rss.reader

import scala.collection.mutable.HashSet

import com.actionbarsherlock.app.SherlockPreferenceActivity
import com.actionbarsherlock.view.MenuItem

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.preference.PreferenceManager

import PreferencesActivity._

object PreferencesActivity {
	final lazy val KEY_AUTO_REFRESH = "preference_auto_refresh"
	final lazy val KEY_REFRESH_PERIOD = "preference_refresh_period"
	final lazy val KEY_NOTIFICATIONS = "preferencescreen_notifications"
	final lazy val KEY_NOTIFICATIONS_LED = "preference_notifications_led"
	final lazy val KEY_NOTIFICATIONS_SOUND = "preference_notifications_sound"
	final lazy val KEY_NOTIFICATIONS_VIBRATIONS = "preference_notifications_vibrations"
	final lazy val KEY_AUTO_MARK_AS_READ = "preference_auto_mark_as_read"
	final lazy val KEY_CHECKED_RADIO_BUTTON = "preference_checked_radio"
	final lazy val KEY_SHOW_SELECTED_ONLY = "preference_show_selected_only"
	final lazy val KEY_KEEP_READ_FEEDS = "preference_keep_read_feeds"
	final lazy val KEY_LAST_UPDATED = "preference_last_updated"
	final lazy val KEY_IMAGES_ONLY_WIFI = "preference_images_only_on_wifi"

	private final lazy val DEFAULT_REFRESH_PERIOD = "24"
}

class PreferencesActivity extends GeneralPreferenceActivity with OnSharedPreferenceChangeListener {
	private lazy val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
	private lazy val notificationsSet = new HashSet[String]

	private lazy val textViewRefreshPeriod = findPreference(KEY_REFRESH_PERIOD)
	private lazy val preferenceNotifications = findPreference(KEY_NOTIFICATIONS)

	override def onCreate(savedInstanceState: Bundle) = {
		super.onCreate(savedInstanceState)

		if (FeedsListActivity.NEW_API)
			addPreferencesFromResource(R.xml.preferences_hc)
		else
			addPreferencesFromResource(R.xml.preferences_gb)
	}

	override def onResume = {
		super.onResume
		sharedPreferences
			.registerOnSharedPreferenceChangeListener(this)

		configureRefreshPeriod

		if (FeedsListActivity.NEW_API) configureNotificationsType
		else preferenceNotifications.setSummary(R.string.preference_notifications_summary)
	}

	override def onPause = {
		super.onPause
		sharedPreferences
			.unregisterOnSharedPreferenceChangeListener(this)
	}

	private def configureRefreshPeriod = textViewRefreshPeriod.setSummary(getNotificationPeriod)

	private def getNotificationPeriod = {
		val refreshPeriodValue = sharedPreferences.getString(KEY_REFRESH_PERIOD, DEFAULT_REFRESH_PERIOD)
		getResources.getQuantityString(R.plurals.preference_period_summary, refreshPeriodValue.toInt, refreshPeriodValue)
	}

	def configureNotificationsType = {
		configureNotifications(KEY_NOTIFICATIONS_LED)
		configureNotifications(KEY_NOTIFICATIONS_SOUND)
		configureNotifications(KEY_NOTIFICATIONS_VIBRATIONS)

		preferenceNotifications.setSummary(getNotificationsSummary)
	}

	private def configureNotifications(key: String) = {
		val notificationPreference = findPreference(key)
		val notificationEnabled = sharedPreferences.getBoolean(key, false)
		val notificationType = notificationPreference.getTitle.toString

		if (notificationEnabled)
			notificationsSet += notificationType
		else
			notificationsSet -= notificationType

		preferenceNotifications.setSummary(getNotificationsSummary)
		onContentChanged
	}

	private def getNotificationsSummary =
		if (!notificationsSet.isEmpty)
			getString(R.string.preference_notifications_summary_checked) +
				getStringValuesFromSet(notificationsSet)
		else
			getString(R.string.preference_notifications_summary_unchecked)

	private def getStringValuesFromSet(notificationsSet: HashSet[String]) =
		notificationsSet.mkString(", ")

	override def onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) =
		key match {
			case `KEY_REFRESH_PERIOD` => configureRefreshPeriod
			case `KEY_NOTIFICATIONS_LED`
				| `KEY_NOTIFICATIONS_SOUND`
				| `KEY_NOTIFICATIONS_VIBRATIONS` => configureNotifications(key)
			case _ =>
		}
}