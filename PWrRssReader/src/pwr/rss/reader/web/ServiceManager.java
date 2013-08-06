package pwr.rss.reader.web;

import pwr.rss.reader.ApplicationObject;
import pwr.rss.reader.PreferencesActivity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

public class ServiceManager extends Service implements OnSharedPreferenceChangeListener {
	private SharedPreferences sharedPreferences;
	private AlarmManager alarmManager;
	private ApplicationObject applicationObject;
	private PendingIntent startDownloadServicePendingIntent;
	private Context context;
	private Intent wakeReceiverBroadcast;
	
	@Override
	public void onCreate() {
		super.onCreate();
		this.context = getApplicationContext();
		this.applicationObject = (ApplicationObject) context;
		this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		this.sharedPreferences.registerOnSharedPreferenceChangeListener(this);
		this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		createWakeReceiverBroadcast();
		
	}
	
	private void createWakeReceiverBroadcast() {
		this.wakeReceiverBroadcast = new Intent(context, WakeReceiver.class);
		this.startDownloadServicePendingIntent = PendingIntent.getBroadcast(context, 0, wakeReceiverBroadcast,
			PendingIntent.FLAG_CANCEL_CURRENT);
	}
	
	/**
	 * Depends on received intent manageServices can stop or start data updates.
	 */
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		startDownloadUpdateRequest();
		return Service.START_STICKY;
	}
	
	private void startDownloadUpdateRequest() {
		if (isAutoRefreshEnabled()) {
			startDownloadUpdateRequest(getAutoRefreshTimeCycle());
		}
		else {
			sendBroadcast(wakeReceiverBroadcast);
		}
	}
	
	private boolean isAutoRefreshEnabled() {
		return applicationObject.isAutoRefreshEnabled();
	}
	
	private int getAutoRefreshTimeCycle() {
		return applicationObject.getRefreshPeriod();
	}
	
	private void startDownloadUpdateRequest(int cycle) {
		alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),
			minutesToMiliseconds(cycle), startDownloadServicePendingIntent);
	}
	
	/**
	 * Converts provided by user (convenient to him) time to more accuracy for
	 * AlarmManager usage.
	 * */
	private int minutesToMiliseconds(int minutes) {
		return minutes * 60 * 1000;
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals(PreferencesActivity.KEY_AUTO_REFRESH()) || key.equals(PreferencesActivity.KEY_REFRESH_PERIOD())) {
			restartDownloadUpdateRequest();
		}
	}
	
	private void restartDownloadUpdateRequest() {
		cancelDownloadUpdateRequest();
		startDownloadUpdateRequest();
	}
	
	@Override
	public void onDestroy() {
		cancelDownloadUpdateRequest();
		super.onDestroy();
	}
	
	private void cancelDownloadUpdateRequest() {
		alarmManager.cancel(startDownloadServicePendingIntent);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}