package pwr.rss.reader.fragments;

import java.util.ArrayList;
import java.util.List;

import pwr.rss.reader.ApplicationObject;
import pwr.rss.reader.R;
import pwr.rss.reader.cards.DescriptionCard;
import pwr.rss.reader.cards.ImageCard;
import pwr.rss.reader.cards.MyCard;
import pwr.rss.reader.cards.RowCard;
import pwr.rss.reader.database.dao.Feed;
import pwr.rss.reader.database.tables.TableFeeds;
import pwr.rss.reader.utils.CursorFetcher;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ext.SatelliteMenu;
import android.view.ext.SatelliteMenu.SateliteClickedListener;
import android.view.ext.SatelliteMenuItem;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.ShareActionProvider;
import com.fima.cardsui.objects.CardStack;
import com.fima.cardsui.views.CardUI;

public class FeedDetailsFragment extends SherlockFragment implements SateliteClickedListener {
	public static final String FLAG_DISMISSED = "FLAG_DISMISSED";
	public static final String FLAG_READ = "FLAG_READ";
	public static final String FLAG_UNREAD = "FLAG_UNREAD";
	public static final String FLAG_ACTION = "FLAG_ACTION";
	public static final String FEED = "FEED";
	public static final String SAVE_STATE = "SAVE_STATE";
	
	private static final String POSITION = "POSITION";
	private static final int MENU_COPY_URL = 0x34;
	private static final int MENU_OPEN_BROWSER = 0x45;
	private static final int MENU_KEEP_AS_UNREAD = 0x55;
	private static final int MENU_MARK_AS_READ = 0x65;
	private static final int MENU_DISMISS = 0x74;
	
	private Cursor cursor;
	private CardUI cardView;
	private RowCard cardRow;
	private MyCard cardDescription;
	private int position;
	private SherlockFragmentActivity activity;
	private CursorFetcher cursorFetcher;
	private SatelliteMenu satelliteMenu;
	private ApplicationObject applicationObject;
	private MenuItem menuShareItem;
	
	/***************************************
	 ********** LIFECYCLE METHODS **********
	 ***************************************/
	
	public static FeedDetailsFragment newInstance(int position) {
		FeedDetailsFragment instance = new FeedDetailsFragment();
		
		Bundle args = new Bundle();
		args.putInt(POSITION, position);
		instance.setArguments(args);
		
		return instance;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (SherlockFragmentActivity) activity;
		this.applicationObject = (ApplicationObject) activity.getApplication();
		this.cursor = applicationObject.getCurrentCursor();
		this.activity.getSupportActionBar().setIcon(R.drawable.chat_rss_icon);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setHasOptionsMenu(true);
		this.position = getArguments() != null ? getArguments().getInt(POSITION) : 1;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_feed_details, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (cursor.moveToPosition(position)) {
			this.cursorFetcher = new CursorFetcher(cursor, activity);
			this.cardView = (CardUI) view.findViewById(R.id.cardsview);
			
			fillCardsView();
			configureSateliteMenu(view);
		}
	}
	
	/***************************************
	 *********** FILLING LAYOUTS ***********
	 ***************************************/
	
	private void fillCardsView() {
		this.cardDescription = new DescriptionCard(cursor, activity);
		this.cardRow = new RowCard(cursor, activity);
		
		cardView.addCard(cardRow);
		cardView.addCardToLastStack(cardDescription);
		
		createImageCardIfImageExists();
		
		cardView.refresh();
	}
	
	private void createImageCardIfImageExists() {
		String imageLink = cursorFetcher.getImageLink();
		if (canDownloadImage(imageLink)) {
			CardStack cs = new CardStack();
			cs.add(new ImageCard(cursor, activity));
			cardView.addStack(cs);
		}
	}
	
	private boolean canDownloadImage(String imageLink) {
		return !TextUtils.isEmpty(imageLink) && applicationObject.isConnectedToInternet();
	}
	
	private void configureSateliteMenu(View view) {
		satelliteMenu = (SatelliteMenu) view.findViewById(R.id.satelliteMenu);
		configureView(satelliteMenu);
		configureItems(satelliteMenu);
	}
	
	private void configureView(SatelliteMenu menu) {
		float distance = TypedValue
			.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
		menu.setSatelliteDistance((int) distance);
		menu.setExpandDuration(300);
		menu.setMainImage(R.drawable.rss_icon);
	}
	
	private void configureItems(SatelliteMenu menu) {
		List<SatelliteMenuItem> items = new ArrayList<SatelliteMenuItem>();
		
		SatelliteMenuItem readUnreadMenuItem = null;
		if (cursorFetcher.isRead()) {
			readUnreadMenuItem = new SatelliteMenuItem(MENU_KEEP_AS_UNREAD, R.drawable.ic_keep_as_unread);
		}
		else {
			readUnreadMenuItem = new SatelliteMenuItem(MENU_MARK_AS_READ, R.drawable.ic_mark_as_read);
		}
		items.add(readUnreadMenuItem);
		items.add(new SatelliteMenuItem(MENU_DISMISS, R.drawable.ic_dismiss));
		items.add(new SatelliteMenuItem(MENU_COPY_URL, R.drawable.ic_copy_url));
		items.add(new SatelliteMenuItem(MENU_OPEN_BROWSER, R.drawable.ic_open_in_browser));
		
		menu.addItems(items);
		menu.setOnItemClickedListener(this);
	}
	
	/************************************
	 ****** SATELLITE MENU ACTIONS ******
	 ************************************/
	@Override
	public void eventOccured(int id) {
		switch (id) {
			case MENU_COPY_URL:
				saveUrlToClipboard();
				showCopiedUrlToast();
				break;
			case MENU_OPEN_BROWSER:
				cardDescription.onClick(getView());
				break;
			case MENU_KEEP_AS_UNREAD:
				markFeedAsUnread();
				recreateActivityAfterUnread();
				break;
			case MENU_MARK_AS_READ:
				markFeedAsRead();
				recreateActivityAfterRead();
				break;
			case MENU_DISMISS:
				dismissFeed();
				recreateActivityAfterDismiss();
				break;
		}
	}
	
	@SuppressWarnings("deprecation")
	private void saveUrlToClipboard() {
		String feedUrl = cursorFetcher.getFeedLink();
		
		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager) activity
				.getSystemService(Context.CLIPBOARD_SERVICE);
			clipboard.setText(feedUrl);
		}
		else {
			android.content.ClipboardManager clipboard = (android.content.ClipboardManager) activity
				.getSystemService(Context.CLIPBOARD_SERVICE);
			android.content.ClipData clip = android.content.ClipData.newPlainText("text label", feedUrl);
			clipboard.setPrimaryClip(clip);
		}
	}
	
	private void showCopiedUrlToast() {
		applicationObject.toastFactory().showBottomToast(R.string.toast_url_copied_to_clipboard);
	}
	
	private void dismissFeed() {
		Feed currentFeed = getCurrentFeed();
		long feedId = currentFeed.ID;
		applicationObject.deleteFeed(feedId);
	}
	
	private void markFeedAsRead() {
		applicationObject.markFeedAsRead(getCurrentFeed());
	}
	
	private void markFeedAsUnread() {
		applicationObject.markFeedAsUnread(getCurrentFeed());
	}
	
	private Feed getCurrentFeed() {
		return applicationObject.getFeed(cursor);
	}
	
	/***************************************
	 ******** REFRESHING VIEW PAGER ********
	 ***************************************/
	
	private void recreateActivityAfterDismiss() {
		recreateActivity(getDismissIntent());
	}
	
	private void recreateActivityAfterRead() {
		recreateActivity(getReadIntent());
	}
	
	private void recreateActivityAfterUnread() {
		recreateActivity(getUnReadIntent());
	}
	
	private void recreateActivity(Intent intent) {
		intent.putExtra(FEED, getCurrentFeed());
		
		activity.finish();
		activity.overridePendingTransition(0, 0);
		startActivity(intent);
		activity.overridePendingTransition(0, 0);
	}
	
	/************************************
	 ********** INTENT BUILDERS *********
	 ************************************/
	
	private Intent getDismissIntent() {
		return getConfiguredIntent(FLAG_ACTION, FLAG_DISMISSED);
	}
	
	private Intent getReadIntent() {
		return getConfiguredIntent(FLAG_ACTION, FLAG_READ);
	}
	
	private Intent getUnReadIntent() {
		return getConfiguredIntent(FLAG_ACTION, FLAG_UNREAD);
	}
	
	private Intent getConfiguredIntent(String flagAction, String action) {
		Intent intent = getRecreateIntent();
		intent.putExtra(flagAction, action);
		return intent;
	}
	
	private Intent getRecreateIntent() {
		Intent intent = activity.getIntent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		intent.putExtra(FeedsListFragment.FLAG_POSITION, position);
		return intent;
	}
	
	/************************************
	 ****** CONFIGURE SHARE ACTION ******
	 ************************************/
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		activity.getSupportMenuInflater().inflate(R.menu.details_menu, menu);
		
		menuShareItem = menu.findItem(R.id.menu_item_share_action_provider_action_bar);
		ShareActionProvider actionProvider = (ShareActionProvider) menuShareItem.getActionProvider();
		actionProvider.setShareIntent(createShareIntent());
	}
	
	private Intent createShareIntent() {
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_TEXT, getLink());
		return shareIntent;
	}
	
	private String getLink() {
		String link = "";
		int linkColumnId = cursor.getColumnIndex(TableFeeds.C_LINK());
		link = cursor.getString(linkColumnId);
		return link;
	}
}