package pwr.rss.reader.test.database;

import java.util.Date;

import pwr.rss.reader.database.dao.Feed;
import pwr.rss.reader.database.manager.DataBaseHelper;
import pwr.rss.reader.database.manager.DataBaseManager;
import android.database.sqlite.SQLiteConstraintException;
import android.test.AndroidTestCase;

public class FeedTest extends AndroidTestCase {
	private final Feed feed = new Feed(1L, "Title", "", "www.link.com", "Description", 1L, 0L, new Date().getTime());
	private DataBaseManager database;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		if (database == null) {
			database = new DataBaseManager(mContext);
		}
	}
	
	public void testCRUDonFeed() {
		create();
		read();
		update();
		delete();
	}
	
	private void create() {
		long rowsAffected = database.addFeed(feed);
		assertEquals(rowsAffected, 1);
		
		try {
			rowsAffected = database.addFeed(feed);
		}
		catch (SQLiteConstraintException e) {
			rowsAffected = 0;
		}
		assertEquals(rowsAffected, 0);
	}
	
	private void read() {
		long unreadCount = database.countUnreadInChannel(1L);
		assertEquals(1L, unreadCount);
		
		unreadCount = database.countUnreadInChannel(2L);
		assertEquals(0L, unreadCount);
	}
	
	private void update() {
		long isRead = feed.read;
		assertEquals(0L, isRead);
		
		database.markFeedAsRead(feed);
		isRead = feed.read;
		assertEquals(1L, isRead);
		
		database.markFeedAsUnread(feed);
		isRead = feed.read;
		assertEquals(0L, isRead);
	}
	
	private void delete() {
		long rowsAffected = database.deleteFeed(feed.ID);
		assertEquals(rowsAffected, 1);
		
		rowsAffected = database.deleteFeed(feed.ID);
		assertEquals(rowsAffected, 0);
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		mContext.deleteDatabase(DataBaseHelper.DATABASE_NAME());
		database = null;
	}
}