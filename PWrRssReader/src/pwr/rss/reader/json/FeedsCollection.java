package pwr.rss.reader.json;

import java.util.ArrayList;

import pwr.rss.reader.database.dao.Feed;

import com.google.gson.annotations.SerializedName;

public class FeedsCollection {
	@SerializedName("feeds")
	public ArrayList<Feed> feeds;
}