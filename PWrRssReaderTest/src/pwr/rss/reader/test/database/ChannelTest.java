package pwr.rss.reader.test.database;

import pwr.rss.reader.database.dao.Channel;
import pwr.rss.reader.database.manager.DataBaseHelper;
import pwr.rss.reader.database.manager.DataBaseManager;
import android.test.AndroidTestCase;

public class ChannelTest extends AndroidTestCase {
	private Channel channel;
	private DataBaseManager database;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		channel = new Channel(Long.valueOf(1L), "Channel", 0L, "www.link.com", Long.valueOf(1L));
		database = new DataBaseManager(mContext);
	}
	
	public void testCRUDonChannel() {
		update();
	}
	
	public void update() {
		long isSelected = channel.selectedValue();
		assertEquals(1L, isSelected);
		
		database.selectChannel(channel);
		isSelected = channel.selectedValue();
		assertEquals(0L, isSelected);
		
		database.selectChannel(channel);
		isSelected = channel.selectedValue();
		assertEquals(1L, isSelected);
	}
	
	@Override
	protected void tearDown() throws Exception {
		mContext.deleteDatabase(DataBaseHelper.DATABASE_NAME());
		super.tearDown();
	}
}