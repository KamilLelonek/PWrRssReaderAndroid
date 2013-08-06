package pwr.rss.reader.database.dao;

import java.io.Serializable;

import android.text.format.DateFormat;

import com.google.gson.annotations.SerializedName;

public class Feed implements Serializable {
	private static final long serialVersionUID = 5756794234663999445L;
	
	private static final String DATE_FORMAT = "MMMM dd, yyyy H:mm";
	
	@SerializedName("title")
	public String title;
	
	@SerializedName("link")
	public String link;
	
	@SerializedName("description")
	public String description;
	
	@SerializedName("channel")
	public long channel;
	
	@SerializedName("date")
	public long date;
	
	@SerializedName("image")
	public String image;
	
	public long ID = 0L;
	public long read = 0L;
	
	public Feed(long ID, String title, String image, String link, String description, long channel, long read, long date) {
		this.ID = ID;
		this.title = title;
		this.image = image;
		this.link = link;
		this.description = description;
		this.channel = channel;
		this.read = read;
		this.date = date;
	}
	
	public boolean isRead() {
		return read == 1;
	}
	
	public void markAsRead(boolean asRead) {
		this.read = asRead ? 1L : 0L;
	}
	
	public String dateString() {
		return DateFormat.format(DATE_FORMAT, date).toString();
	}
}