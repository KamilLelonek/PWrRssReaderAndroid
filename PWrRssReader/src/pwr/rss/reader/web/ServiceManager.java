package pwr.rss.reader.web;

import pwr.rss.reader.ApplicationObject;
import pwr.rss.reader.PreferencesActivity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

public class ServiceManager
	extends IntentService
	implements OnSharedPreferenceChangeListener {
	
	private static final int DELAY = 3 * 1000; // 3 seconds
	
	private SharedPreferences sharedPreferences;
	private AlarmManager alarmManager;
	private ApplicationObject applicationObject;
	private PendingIntent startDownloadServicePendingIntent;
	private Context context;
	private Intent wakeReceiverBroadcast;
	private Handler handler;
	private Runnable startServiceRunnable;
	
	public ServiceManager() {
		super("ServiceManager");
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		this.context = getApplicationContext();
		this.applicationObject = (ApplicationObject) context;
		this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		this.sharedPreferences.registerOnSharedPreferenceChangeListener(this);
		this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		this.handler = new Handler();
		this.startServiceRunnable = new Runnable() {
			@Override
			public void run() {
				sendBroadcast(wakeReceiverBroadcast);
			}
		};
		
		createWakeReceiverBroadcast();
	}
	
	private void createWakeReceiverBroadcast() {
		this.wakeReceiverBroadcast = new Intent(context, WakeReceiver.class);
		this.startDownloadServicePendingIntent =
			PendingIntent.getBroadcast
				(
					context,
					0, // request code
					wakeReceiverBroadcast,
					PendingIntent.FLAG_UPDATE_CURRENT
				);
	}
	
	/**
	 * Depends on received intent manageServices can stop or start data updates.
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		if (isConnectedToInternet()) {
			startDownloadUpdateRequest();
		}
		else {
			cancelDownloadUpdateRequest();
		}
	}
	
	private void startDownloadUpdateRequest() {
		if (isAutoRefreshEnabled()) {
			setDownloadUpdateRequest();
		}
		handler.postDelayed(startServiceRunnable, DELAY);
	}
	
	private boolean isAutoRefreshEnabled() {
		return applicationObject.isAutoRefreshEnabled();
	}
	
	private boolean isConnectedToInternet() {
		return applicationObject.isConnectedToInternet();
	}
	
	private void setDownloadUpdateRequest() {
		alarmManager.setInexactRepeating(
			AlarmManager.ELAPSED_REALTIME,
			SystemClock.elapsedRealtime(),
			getAutoRefreshTimeCycleInMillis(),
			startDownloadServicePendingIntent);
	}
	
	private int getAutoRefreshTimeCycleInMillis() {
		int refreshPeriodInHours = applicationObject.getRefreshPeriod();
		return hoursToMilliseconds(refreshPeriodInHours);
	}
	
	/**
	 * Converts provided by user (convenient to him) time to more accuracy for
	 * AlarmManager usage.
	 * */
	private int hoursToMilliseconds(int hours) {
		return hours * 60/* minutes */* 60/* seconds */* 1000/* milliseconds */;
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (hasAutorefreshChanged(key) || hasRefreshPeriodChanged(key)) {
			onHandleIntent(null); // additional network connection check
		}
	}
	
	private boolean hasAutorefreshChanged(String key) {
		return key.equals(PreferencesActivity.KEY_AUTO_REFRESH());
	}
	
	private boolean hasRefreshPeriodChanged(String key) {
		return key.equals(PreferencesActivity.KEY_REFRESH_PERIOD());
	}
	
	private void cancelDownloadUpdateRequest() {
		alarmManager.cancel(startDownloadServicePendingIntent);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}