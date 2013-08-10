package pwr.rss.reader.web;

import java.util.ArrayList;

import pwr.rss.reader.ApplicationObject;
import pwr.rss.reader.database.dao.Feed;
import pwr.rss.reader.json.PWrJSONParser;
import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.WakefulBroadcastReceiver;

public class DownloadService extends IntentService {
	public static final String ACTION_DOWNLOAD_COMPLETED = "pwr.rss.reader.action.download_completed";
	public static final String ACTION_START_DOWNLOAD = "pwr.rss.reader.action.start_download";
	public static final String ACTION_DEVICE_OFFLINE = "pwr.rss.reader.action.device_offline";
	
	private ApplicationObject applicationObject;
	private LocalBroadcastManager localBroadcastManager;
	private Intent downloadCompletedBroadcast;
	private Intent downloadAbortedBroadcast;
	
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
		this.downloadAbortedBroadcast = new Intent(ACTION_DEVICE_OFFLINE);
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		downloadDataIfOnline();
		WakefulBroadcastReceiver.completeWakefulIntent(intent);
	}
	
	private void downloadDataIfOnline() {
		if (canUpdateFeeds()) {
			this.isDownloading = true;
			downloadData();
			this.isDownloading = false;
		}
		else {
			notifyDeviceOffline();
		}
	}
	
	private void notifyDeviceOffline() {
		localBroadcastManager.sendBroadcast(downloadAbortedBroadcast);
	}
	
	private boolean canUpdateFeeds() {
		return isDeviceOnline() && !isDownloading;
	}
	
	private boolean isDeviceOnline() {
		return applicationObject.isConnectedToInternet();
	}
	
	private void downloadData() {
		long lastUpdateTime = applicationObject.getLastUpdateDate();
		String inputString = HttpConnection.getInputString(lastUpdateTime);
		ArrayList<Feed> feeds = PWrJSONParser.getFeeds(inputString);
		updateFeedsData(feeds);
		notifyDownloadCompleted();
	}
	
	private void updateFeedsData(ArrayList<Feed> feeds) {
		if (feeds != null && !feeds.isEmpty()) {
			applicationObject.addFeeds(feeds);
			applicationObject.showNotification(feeds.size());
			applicationObject.setLastUpdateDate();
		}
	}
	
	private void notifyDownloadCompleted() {
		localBroadcastManager.sendBroadcast(downloadCompletedBroadcast);
	}
}