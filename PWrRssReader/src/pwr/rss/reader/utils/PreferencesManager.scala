package pwr.rss.reader.utils

import android.preference.PreferenceManager
import android.content.Context
import pwr.rss.reader.PreferencesActivity._
import pwr.rss.reader.R
import java.util.Date

class PreferencesManager(context: Context) {
	private lazy val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

	def isAutoRefreshEnabled = getBoolean(KEY_AUTO_REFRESH)
	def getRefreshPeriod = sharedPreferences.getString(KEY_REFRESH_PERIOD, "10")
	def areNotificationsEnabled = getBoolean(KEY_NOTIFICATIONS)
	def isNotificationLedEnabled = getBoolean(KEY_NOTIFICATIONS_LED)
	def isNotificationVibrateEnabled = getBoolean(KEY_NOTIFICATIONS_SOUND)
	def isNotificationSoundEnabled = getBoolean(KEY_NOTIFICATIONS_VIBRATIONS)
	def autoMarkAsRead = getBoolean(KEY_AUTO_MARK_AS_READ)
	def keepFeedsAsRead = getBoolean(KEY_KEEP_READ_FEEDS)

	def getSelectedRadioButtonId = sharedPreferences.getInt(KEY_CHECKED_RADIO_BUTTON, R.id.radioButtonAll)
	def setSelectedRadioButtonId(radioButtonId: Int) =
		putValueToPreferences(KEY_CHECKED_RADIO_BUTTON, radioButtonId)

	def isSelectedOnlyChecked = getBoolean(KEY_SHOW_SELECTED_ONLY)
	def setSelectedOnlyChecked(isChecked: Boolean) =
		putValueToPreferences(KEY_SHOW_SELECTED_ONLY, isChecked)

	def getLastUpdateDate = getString(KEY_LAST_UPDATED)
	def setLastUpdateDate =
		putValueToPreferences(KEY_LAST_UPDATED, System.currentTimeMillis)

	private def putValueToPreferences[T](key: String, value: T) = {
		val sharedPrefEditor = sharedPreferences.edit
		value match {
			case v: Int => sharedPrefEditor.putInt(key, v)
			case v: Boolean => sharedPrefEditor.putBoolean(key, v)
			case v: Long => sharedPrefEditor.putLong(key, v)
			case v: Float => sharedPrefEditor.putFloat(key, v)
			case v: String => sharedPrefEditor.putString(key, v)
			case _ =>
		}
		sharedPrefEditor.commit
	}

	private def getBoolean(key: String) = sharedPreferences.getBoolean(key, false)
	private def getString(key: String) = sharedPreferences.getLong(key, 0L)
}