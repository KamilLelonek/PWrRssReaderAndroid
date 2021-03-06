package pwr.rss.reader.utils

import android.preference.PreferenceManager
import PreferencesManager._
import android.content.Context
import pwr.rss.reader.PreferencesActivity._
import pwr.rss.reader.R
import java.util.Date

object PreferencesManager {
	private lazy val KEY_IS_FIRST_RUN = "is_first_run"
	private lazy val KEY_IS_FIRST_DETAILS_RUN = "is_first_details_run"
}

class PreferencesManager(context: Context) {
	private lazy val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

	def isAutoRefreshEnabled = getBooleanOrTrue(KEY_AUTO_REFRESH)
	def getRefreshPeriod = sharedPreferences.getString(KEY_REFRESH_PERIOD, "10")
	def areNotificationsEnabled = getBooleanOrTrue(KEY_NOTIFICATIONS)
	def isNotificationLedEnabled = getBooleanOrTrue(KEY_NOTIFICATIONS_LED)
	def isNotificationVibrateEnabled = getBooleanOrTrue(KEY_NOTIFICATIONS_VIBRATIONS)
	def isNotificationSoundEnabled = getBooleanOrTrue(KEY_NOTIFICATIONS_SOUND)
	def keepFeedsAsRead = getBooleanOrFalse(KEY_KEEP_READ_FEEDS)
	def autoMarkAsRead = getBooleanOrTrue(KEY_AUTO_MARK_AS_READ)
	def imagesOnlyOnWifi = getBooleanOrTrue(KEY_IMAGES_ONLY_WIFI)

	def getSelectedRadioButtonId = sharedPreferences.getInt(KEY_CHECKED_RADIO_BUTTON, R.id.radioButtonAll)
	def setSelectedRadioButtonId(radioButtonId: Int) =
		putValueToPreferences(KEY_CHECKED_RADIO_BUTTON, radioButtonId)

	def getLastUpdateDate(channelID: Int) = getLong(KEY_LAST_UPDATED + channelID)
	def setLastUpdateDate(channelID: Int) =
		putValueToPreferences(KEY_LAST_UPDATED + channelID, System.currentTimeMillis)

	def isFirstRun = getBooleanOrTrue(KEY_IS_FIRST_RUN)
	def setFirstRun = putValueToPreferences(KEY_IS_FIRST_RUN, false)

	def isFirstDetailsRun = getBooleanOrTrue(KEY_IS_FIRST_DETAILS_RUN)
	def setFirstDetailsRun = putValueToPreferences(KEY_IS_FIRST_DETAILS_RUN, false)

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

	private def getBooleanOrFalse(key: String) = sharedPreferences.getBoolean(key, false)
	private def getBooleanOrTrue(key: String) = sharedPreferences.getBoolean(key, true)
	private def getLong(key: String) = sharedPreferences.getLong(key, 0L)
}