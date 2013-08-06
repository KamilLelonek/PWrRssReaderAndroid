package pwr.rss.reader.web;

import java.util.ArrayList;

import pwr.rss.reader.ApplicationObject;
import pwr.rss.reader.database.dao.Feed;
import pwr.rss.reader.json.PWrJSONParser;
import pwr.rss.reader.utils.PreferencesManager;
import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

public class DownloadService extends IntentService {
	public static final String ACTION_DOWNLOAD_COMPLETED = "pwr.rss.reader.action.download_completed";
	public static final String ACTION_START_DOWNLOAD = "pwr.rss.reader.action.start_download";
	
	private ApplicationObject applicationObject;
	private LocalBroadcastManager localBroadcastManager;
	private Intent downloadCompletedBroadcast;
	private PreferencesManager preferencesManager;
	
	private volatile boolean isDownloading = false;
	
	public DownloadService() {
		super("DownloadService");
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		this.applicationObject = (ApplicationObject) getApplication();
		this.localBroadcastManager = LocalBroadcastManager.getInstance(this);
		this.downloadCompletedBroadcast = new Intent(ACTION_DOWNLOAD_COMPLETED);
		this.preferencesManager = new PreferencesManager(this);
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		downloadDataIfOnline();
		WakeReceiver.completeWakefulIntent(intent);
	}
	
	private void downloadDataIfOnline() {
		if (canUpdateFeeds()) {
			this.isDownloading = true;
			downloadData();
			this.isDownloading = false;
		}
	}
	
	private boolean canUpdateFeeds() {
		return isDeviceOnline() && !isDownloading;
	}
	
	private boolean isDeviceOnline() {
		return applicationObject.isConnectedToInternet();
	}
	
	private void downloadData() {
		long lastUpdateTime = preferencesManager.getLastUpdateDate();
		String inputString = HttpConnection.getInputString(lastUpdateTime);
		ArrayList<Feed> feeds = PWrJSONParser.getFeeds(inputString);
		applicationObject.addFeeds(feeds);
		preferencesManager.setLastUpdateDate();
		notifyDownloadCompleted();
	}
	
	private void notifyDownloadCompleted() {
		localBroadcastManager.sendBroadcast(downloadCompletedBroadcast);
	}
}