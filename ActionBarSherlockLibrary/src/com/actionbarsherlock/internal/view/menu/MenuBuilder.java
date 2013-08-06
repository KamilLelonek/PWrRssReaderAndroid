/* Copyright (C) 2006 The Android Open Source Project Licensed under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License. */

package com.actionbarsherlock.internal.view.menu;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.SparseArray;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;

import com.actionbarsherlock.R;
import com.actionbarsherlock.view.ActionProvider;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;

/**
 * Implementation of the {@link android.view.Menu} interface for creating a
 * standard menu UI.
 */
public class MenuBuilder implements Menu {
	//UNUSED private static final String TAG = "MenuBuilder";
	
	private static final String PRESENTER_KEY = "android:menu:presenters";
	private static final String ACTION_VIEW_STATES_KEY = "android:menu:actionviewstates";
	private static final String EXPANDED_ACTION_VIEW_ID = "android:menu:expandedactionview";
	
	private static final int[] sCategoryToOrder = new int[] { 1, /* No category */
	4, /* CONTAINER */
	5, /* SYSTEM */
	3, /* SECONDARY */
	2, /* ALTERNATIVE */
	0, /* SELECTED_ALTERNATIVE */
	};
	
	private final Context mContext;
	private final Resources mResources;
	
	/**
	 * Whether the shortcuts should be qwerty-accessible. Use isQwertyMode()
	 * instead of accessing this directly.
	 */
	private boolean mQwertyMode;
	
	/**
	 * Whether the shortcuts should be visible on menus. Use
	 * isShortcutsVisible() instead of accessing this directly.
	 */
	private boolean mShortcutsVisible;
	
	/**
	 * Callback that will receive the various menu-related events generated by
	 * this class. Use getCallback to get a reference to the callback.
	 */
	private Callback mCallback;
	
	/** Contains all of the items for this menu */
	private ArrayList<MenuItemImpl> mItems;
	
	/**
	 * Contains only the items that are currently visible. This will be
	 * created/refreshed from {@link #getVisibleItems()}
	 */
	private ArrayList<MenuItemImpl> mVisibleItems;
	/**
	 * Whether or not the items (or any one item's shown state) has changed
	 * since it was last fetched from {@link #getVisibleItems()}
	 */
	private boolean mIsVisibleItemsStale;
	
	/**
	 * Contains only the items that should appear in the Action Bar, if present.
	 */
	private ArrayList<MenuItemImpl> mActionItems;
	/**
	 * Contains items that should NOT appear in the Action Bar, if present.
	 */
	private ArrayList<MenuItemImpl> mNonActionItems;
	
	/**
	 * Whether or not the items (or any one item's action state) has changed
	 * since it was last fetched.
	 */
	private boolean mIsActionItemsStale;
	
	/**
	 * Default value for how added items should show in the action list.
	 */
	private int mDefaultShowAsAction = MenuItem.SHOW_AS_ACTION_NEVER;
	
	/**
	 * Current use case is Context Menus: As Views populate the context menu,
	 * each one has extra information that should be passed along. This is the
	 * current menu info that should be set on all items added to this menu.
	 */
	private ContextMenuInfo mCurrentMenuInfo;
	
	/** Header title for menu types that have a header (context and submenus) */
	CharSequence mHeaderTitle;
	/**
	 * Header icon for menu types that have a header and support icons (context)
	 */
	Drawable mHeaderIcon;
	/**
	 * Header custom view for menu types that have a header and support custom
	 * views (context)
	 */
	View mHeaderView;
	
	/**
	 * Contains the state of the View hierarchy for all menu views when the menu
	 * was frozen.
	 */
	//UNUSED private SparseArray<Parcelable> mFrozenViewStates;
	
	/**
	 * Prevents onItemsChanged from doing its junk, useful for batching commands
	 * that may individually call onItemsChanged.
	 */
	private boolean mPreventDispatchingItemsChanged = false;
	private boolean mItemsChangedWhileDispatchPrevented = false;
	
	private boolean mOptionalIconsVisible = false;
	
	private boolean mIsClosing = false;
	
	private ArrayList<MenuItemImpl> mTempShortcutItemList = new ArrayList<MenuItemImpl>();
	
	private CopyOnWriteArrayList<WeakReference<MenuPresenter>> mPresenters = new CopyOnWriteArrayList<WeakReference<MenuPresenter>>();
	
	/**
	 * Currently expanded menu item; must be collapsed when we clear.
	 */
	private MenuItemImpl mExpandedItem;
	
	/**
	 * Called by menu to notify of close and selection changes.
	 */
	public interface Callback {
		/**
		 * Called when a menu item is selected.
		 * 
		 * @param menu The menu that is the parent of the item
		 * @param item The menu item that is selected
		 * @return whether the menu item selection was handled
		 */
		public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item);
		
		/**
		 * Called when the mode of the menu changes (for example, from icon to
		 * expanded).
		 * 
		 * @param menu the menu that has changed modes
		 */
		public void onMenuModeChange(MenuBuilder menu);
	}
	
	/**
	 * Called by menu items to execute their associated action
	 */
	public interface ItemInvoker {
		public boolean invokeItem(MenuItemImpl item);
	}
	
	public MenuBuilder(Context context) {
		mContext = context;
		mResources = context.getResources();
		
		mItems = new ArrayList<MenuItemImpl>();
		
		mVisibleItems = new ArrayList<MenuItemImpl>();
		mIsVisibleItemsStale = true;
		
		mActionItems = new ArrayList<MenuItemImpl>();
		mNonActionItems = new ArrayList<MenuItemImpl>();
		mIsActionItemsStale = true;
		
		setShortcutsVisibleInner(true);
	}
	
	public MenuBuilder setDefaultShowAsAction(int defaultShowAsAction) {
		mDefaultShowAsAction = defaultShowAsAction;
		return this;
	}
	
	/**
	 * Add a presenter to this menu. This will only hold a WeakReference; you do
	 * not need to explicitly remove a presenter, but you can using
	 * {@link #removeMenuPresenter(MenuPresenter)}.
	 * 
	 * @param presenter The presenter to add
	 */
	public void addMenuPresenter(MenuPresenter presenter) {
		mPresenters.add(new WeakReference<MenuPresenter>(presenter));
		presenter.initForMenu(mContext, this);
		mIsActionItemsStale = true;
	}
	
	/**
	 * Remove a presenter from this menu. That presenter will no longer receive
	 * notifications of updates to this menu's data.
	 * 
	 * @param presenter The presenter to remove
	 */
	public void removeMenuPresenter(MenuPresenter presenter) {
		for (WeakReference<MenuPresenter> ref : mPresenters) {
			final MenuPresenter item = ref.get();
			if (item == null || item == presenter) {
				mPresenters.remove(ref);
			}
		}
	}
	
	private void dispatchPresenterUpdate(boolean cleared) {
		if (mPresenters.isEmpty()) return;
		
		stopDispatchingItemsChanged();
		for (WeakReference<MenuPresenter> ref : mPresenters) {
			final MenuPresenter presenter = ref.get();
			if (presenter == null) {
				mPresenters.remove(ref);
			}
			else {
				presenter.updateMenuView(cleared);
			}
		}
		startDispatchingItemsChanged();
	}
	
	private boolean dispatchSubMenuSelected(SubMenuBuilder subMenu) {
		if (mPresenters.isEmpty()) return false;
		
		boolean result = false;
		
		for (WeakReference<MenuPresenter> ref : mPresenters) {
			final MenuPresenter presenter = ref.get();
			if (presenter == null) {
				mPresenters.remove(ref);
			}
			else if (!result) {
				result = presenter.onSubMenuSelected(subMenu);
			}
		}
		return result;
	}
	
	private void dispatchSaveInstanceState(Bundle outState) {
		if (mPresenters.isEmpty()) return;
		
		SparseArray<Parcelable> presenterStates = new SparseArray<Parcelable>();
		
		for (WeakReference<MenuPresenter> ref : mPresenters) {
			final MenuPresenter presenter = ref.get();
			if (presenter == null) {
				mPresenters.remove(ref);
			}
			else {
				final int id = presenter.getId();
				if (id > 0) {
					final Parcelable state = presenter.onSaveInstanceState();
					if (state != null) {
						presenterStates.put(id, state);
					}
				}
			}
		}
		
		outState.putSparseParcelableArray(PRESENTER_KEY, presenterStates);
	}
	
	private void dispatchRestoreInstanceState(Bundle state) {
		SparseArray<Parcelable> presenterStates = state.getSparseParcelableArray(PRESENTER_KEY);
		
		if (presenterStates == null || mPresenters.isEmpty()) return;
		
		for (WeakReference<MenuPresenter> ref : mPresenters) {
			final MenuPresenter presenter = ref.get();
			if (presenter == null) {
				mPresenters.remove(ref);
			}
			else {
				final int id = presenter.getId();
				if (id > 0) {
					Parcelable parcel = presenterStates.get(id);
					if (parcel != null) {
						presenter.onRestoreInstanceState(parcel);
					}
				}
			}
		}
	}
	
	public void savePresenterStates(Bundle outState) {
		dispatchSaveInstanceState(outState);
	}
	
	public void restorePresenterStates(Bundle state) {
		dispatchRestoreInstanceState(state);
	}
	
	public void saveActionViewStates(Bundle outStates) {
		SparseArray<Parcelable> viewStates = null;
		
		final int itemCount = size();
		for (int i = 0; i < itemCount; i++) {
			final MenuItem item = getItem(i);
			final View v = item.getActionView();
			if (v != null && v.getId() != View.NO_ID) {
				if (viewStates == null) {
					viewStates = new SparseArray<Parcelable>();
				}
				v.saveHierarchyState(viewStates);
				if (item.isActionViewExpanded()) {
					outStates.putInt(EXPANDED_ACTION_VIEW_ID, item.getItemId());
				}
			}
			if (item.hasSubMenu()) {
				final SubMenuBuilder subMenu = (SubMenuBuilder) item.getSubMenu();
				subMenu.saveActionViewStates(outStates);
			}
		}
		
		if (viewStates != null) {
			outStates.putSparseParcelableArray(getActionViewStatesKey(), viewStates);
		}
	}
	
	public void restoreActionViewStates(Bundle states) {
		if (states == null) return;
		
		SparseArray<Parcelable> viewStates = states.getSparseParcelableArray(getActionViewStatesKey());
		
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB && viewStates == null) //Fixes Issue #652 with sdk <= 2.3.6
		return;
		
		final int itemCount = size();
		for (int i = 0; i < itemCount; i++) {
			final MenuItem item = getItem(i);
			final View v = item.getActionView();
			if (v != null && v.getId() != View.NO_ID) {
				v.restoreHierarchyState(viewStates);
			}
			if (item.hasSubMenu()) {
				final SubMenuBuilder subMenu = (SubMenuBuilder) item.getSubMenu();
				subMenu.restoreActionViewStates(states);
			}
		}
		
		final int expandedId = states.getInt(EXPANDED_ACTION_VIEW_ID);
		if (expandedId > 0) {
			MenuItem itemToExpand = findItem(expandedId);
			if (itemToExpand != null) {
				itemToExpand.expandActionView();
			}
		}
	}
	
	protected String getActionViewStatesKey() {
		return ACTION_VIEW_STATES_KEY;
	}
	
	public void setCallback(Callback cb) {
		mCallback = cb;
	}
	
	/**
	 * Adds an item to the menu. The other add methods funnel to this.
	 */
	private MenuItem addInternal(int group, int id, int categoryOrder, CharSequence title) {
		final int ordering = getOrdering(categoryOrder);
		
		final MenuItemImpl item = new MenuItemImpl(this, group, id, categoryOrder, ordering, title,
			mDefaultShowAsAction);
		
		if (mCurrentMenuInfo != null) {
			// Pass along the current menu info
			item.setMenuInfo(mCurrentMenuInfo);
		}
		
		mItems.add(findInsertIndex(mItems, ordering), item);
		onItemsChanged(true);
		
		return item;
	}
	
	@Override
	public MenuItem add(CharSequence title) {
		return addInternal(0, 0, 0, title);
	}
	
	@Override
	public MenuItem add(int titleRes) {
		return addInternal(0, 0, 0, mResources.getString(titleRes));
	}
	
	@Override
	public MenuItem add(int group, int id, int categoryOrder, CharSequence title) {
		return addInternal(group, id, categoryOrder, title);
	}
	
	@Override
	public MenuItem add(int group, int id, int categoryOrder, int title) {
		return addInternal(group, id, categoryOrder, mResources.getString(title));
	}
	
	@Override
	public SubMenu addSubMenu(CharSequence title) {
		return addSubMenu(0, 0, 0, title);
	}
	
	@Override
	public SubMenu addSubMenu(int titleRes) {
		return addSubMenu(0, 0, 0, mResources.getString(titleRes));
	}
	
	@Override
	public SubMenu addSubMenu(int group, int id, int categoryOrder, CharSequence title) {
		final MenuItemImpl item = (MenuItemImpl) addInternal(group, id, categoryOrder, title);
		final SubMenuBuilder subMenu = new SubMenuBuilder(mContext, this, item);
		item.setSubMenu(subMenu);
		
		return subMenu;
	}
	
	@Override
	public SubMenu addSubMenu(int group, int id, int categoryOrder, int title) {
		return addSubMenu(group, id, categoryOrder, mResources.getString(title));
	}
	
	@Override
	public int addIntentOptions(int group, int id, int categoryOrder, ComponentName caller, Intent[] specifics,
		Intent intent, int flags, MenuItem[] outSpecificItems) {
		PackageManager pm = mContext.getPackageManager();
		final List<ResolveInfo> lri = pm.queryIntentActivityOptions(caller, specifics, intent, 0);
		final int N = lri != null ? lri.size() : 0;
		
		if ((flags & FLAG_APPEND_TO_GROUP) == 0) {
			removeGroup(group);
		}
		
		for (int i = 0; i < N; i++) {
			final ResolveInfo ri = lri.get(i);
			Intent rintent = new Intent(ri.specificIndex < 0 ? intent : specifics[ri.specificIndex]);
			rintent.setComponent(new ComponentName(ri.activityInfo.applicationInfo.packageName, ri.activityInfo.name));
			final MenuItem item = add(group, id, categoryOrder, ri.loadLabel(pm)).setIcon(ri.loadIcon(pm)).setIntent(
				rintent);
			if (outSpecificItems != null && ri.specificIndex >= 0) {
				outSpecificItems[ri.specificIndex] = item;
			}
		}
		
		return N;
	}
	
	@Override
	public void removeItem(int id) {
		removeItemAtInt(findItemIndex(id), true);
	}
	
	@Override
	public void removeGroup(int group) {
		final int i = findGroupIndex(group);
		
		if (i >= 0) {
			final int maxRemovable = mItems.size() - i;
			int numRemoved = 0;
			while ((numRemoved++ < maxRemovable) && (mItems.get(i).getGroupId() == group)) {
				// Don't force update for each one, this method will do it at the end
				removeItemAtInt(i, false);
			}
			
			// Notify menu views
			onItemsChanged(true);
		}
	}
	
	/**
	 * Remove the item at the given index and optionally forces menu views to
	 * update.
	 * 
	 * @param index The index of the item to be removed. If this index is
	 *        invalid an exception is thrown.
	 * @param updateChildrenOnMenuViews Whether to force update on menu views.
	 *        Please make sure you eventually call this after your batch of
	 *        removals.
	 */
	private void removeItemAtInt(int index, boolean updateChildrenOnMenuViews) {
		if ((index < 0) || (index >= mItems.size())) return;
		
		mItems.remove(index);
		
		if (updateChildrenOnMenuViews) {
			onItemsChanged(true);
		}
	}
	
	public void removeItemAt(int index) {
		removeItemAtInt(index, true);
	}
	
	public void clearAll() {
		mPreventDispatchingItemsChanged = true;
		clear();
		clearHeader();
		mPreventDispatchingItemsChanged = false;
		mItemsChangedWhileDispatchPrevented = false;
		onItemsChanged(true);
	}
	
	@Override
	public void clear() {
		if (mExpandedItem != null) {
			collapseItemActionView(mExpandedItem);
		}
		mItems.clear();
		
		onItemsChanged(true);
	}
	
	void setExclusiveItemChecked(MenuItem item) {
		final int group = item.getGroupId();
		
		final int N = mItems.size();
		for (int i = 0; i < N; i++) {
			MenuItemImpl curItem = mItems.get(i);
			if (curItem.getGroupId() == group) {
				if (!curItem.isExclusiveCheckable()) {
					continue;
				}
				if (!curItem.isCheckable()) {
					continue;
				}
				
				// Check the item meant to be checked, uncheck the others (that are in the group)
				curItem.setCheckedInt(curItem == item);
			}
		}
	}
	
	@Override
	public void setGroupCheckable(int group, boolean checkable, boolean exclusive) {
		final int N = mItems.size();
		
		for (int i = 0; i < N; i++) {
			MenuItemImpl item = mItems.get(i);
			if (item.getGroupId() == group) {
				item.setExclusiveCheckable(exclusive);
				item.setCheckable(checkable);
			}
		}
	}
	
	@Override
	public void setGroupVisible(int group, boolean visible) {
		final int N = mItems.size();
		
		// We handle the notification of items being changed ourselves, so we use setVisibleInt rather
		// than setVisible and at the end notify of items being changed
		
		boolean changedAtLeastOneItem = false;
		for (int i = 0; i < N; i++) {
			MenuItemImpl item = mItems.get(i);
			if (item.getGroupId() == group) {
				if (item.setVisibleInt(visible)) {
					changedAtLeastOneItem = true;
				}
			}
		}
		
		if (changedAtLeastOneItem) {
			onItemsChanged(true);
		}
	}
	
	@Override
	public void setGroupEnabled(int group, boolean enabled) {
		final int N = mItems.size();
		
		for (int i = 0; i < N; i++) {
			MenuItemImpl item = mItems.get(i);
			if (item.getGroupId() == group) {
				item.setEnabled(enabled);
			}
		}
	}
	
	@Override
	public boolean hasVisibleItems() {
		final int size = size();
		
		for (int i = 0; i < size; i++) {
			MenuItemImpl item = mItems.get(i);
			if (item.isVisible()) return true;
		}
		
		return false;
	}
	
	@Override
	public MenuItem findItem(int id) {
		final int size = size();
		for (int i = 0; i < size; i++) {
			MenuItemImpl item = mItems.get(i);
			if (item.getItemId() == id) return item;
			else if (item.hasSubMenu()) {
				MenuItem possibleItem = item.getSubMenu().findItem(id);
				
				if (possibleItem != null) return possibleItem;
			}
		}
		
		return null;
	}
	
	public int findItemIndex(int id) {
		final int size = size();
		
		for (int i = 0; i < size; i++) {
			MenuItemImpl item = mItems.get(i);
			if (item.getItemId() == id) return i;
		}
		
		return -1;
	}
	
	public int findGroupIndex(int group) {
		return findGroupIndex(group, 0);
	}
	
	public int findGroupIndex(int group, int start) {
		final int size = size();
		
		if (start < 0) {
			start = 0;
		}
		
		for (int i = start; i < size; i++) {
			final MenuItemImpl item = mItems.get(i);
			
			if (item.getGroupId() == group) return i;
		}
		
		return -1;
	}
	
	@Override
	public int size() {
		return mItems.size();
	}
	
	/** {@inheritDoc} */
	@Override
	public MenuItem getItem(int index) {
		return mItems.get(index);
	}
	
	@Override
	public boolean isShortcutKey(int keyCode, KeyEvent event) {
		return findItemWithShortcutForKey(keyCode, event) != null;
	}
	
	@Override
	public void setQwertyMode(boolean isQwerty) {
		mQwertyMode = isQwerty;
		
		onItemsChanged(false);
	}
	
	/**
	 * Returns the ordering across all items. This will grab the category from
	 * the upper bits, find out how to order the category with respect to other
	 * categories, and combine it with the lower bits.
	 * 
	 * @param categoryOrder The category order for a particular item (if it has
	 *        not been or/add with a category, the default category is assumed).
	 * @return An ordering integer that can be used to order this item across
	 *         all the items (even from other categories).
	 */
	private static int getOrdering(int categoryOrder) {
		final int index = (categoryOrder & CATEGORY_MASK) >> CATEGORY_SHIFT;
		
		if (index < 0 || index >= sCategoryToOrder.length) throw new IllegalArgumentException(
			"order does not contain a valid category.");
		
		return (sCategoryToOrder[index] << CATEGORY_SHIFT) | (categoryOrder & USER_MASK);
	}
	
	/**
	 * @return whether the menu shortcuts are in qwerty mode or not
	 */
	boolean isQwertyMode() {
		return mQwertyMode;
	}
	
	/**
	 * Sets whether the shortcuts should be visible on menus. Devices without
	 * hardware key input will never make shortcuts visible even if this method
	 * is passed 'true'.
	 * 
	 * @param shortcutsVisible Whether shortcuts should be visible (if true and
	 *        a menu item does not have a shortcut defined, that item will still
	 *        NOT show a shortcut)
	 */
	public void setShortcutsVisible(boolean shortcutsVisible) {
		if (mShortcutsVisible == shortcutsVisible) return;
		
		setShortcutsVisibleInner(shortcutsVisible);
		onItemsChanged(false);
	}
	
	private void setShortcutsVisibleInner(boolean shortcutsVisible) {
		mShortcutsVisible = shortcutsVisible && mResources.getConfiguration().keyboard != Configuration.KEYBOARD_NOKEYS
			&& mResources.getBoolean(R.bool.abs__config_showMenuShortcutsWhenKeyboardPresent);
	}
	
	/**
	 * @return Whether shortcuts should be visible on menus.
	 */
	public boolean isShortcutsVisible() {
		return mShortcutsVisible;
	}
	
	Resources getResources() {
		return mResources;
	}
	
	public Context getContext() {
		return mContext;
	}
	
	boolean dispatchMenuItemSelected(MenuBuilder menu, MenuItem item) {
		return mCallback != null && mCallback.onMenuItemSelected(menu, item);
	}
	
	/**
	 * Dispatch a mode change event to this menu's callback.
	 */
	public void changeMenuMode() {
		if (mCallback != null) {
			mCallback.onMenuModeChange(this);
		}
	}
	
	private static int findInsertIndex(ArrayList<MenuItemImpl> items, int ordering) {
		for (int i = items.size() - 1; i >= 0; i--) {
			MenuItemImpl item = items.get(i);
			if (item.getOrdering() <= ordering) return i + 1;
		}
		
		return 0;
	}
	
	@Override
	public boolean performShortcut(int keyCode, KeyEvent event, int flags) {
		final MenuItemImpl item = findItemWithShortcutForKey(keyCode, event);
		
		boolean handled = false;
		
		if (item != null) {
			handled = performItemAction(item, flags);
		}
		
		if ((flags & FLAG_ALWAYS_PERFORM_CLOSE) != 0) {
			close(true);
		}
		
		return handled;
	}
	
	/*
	 * This function will return all the menu and sub-menu items that can
	 * be directly (the shortcut directly corresponds) and indirectly
	 * (the ALT-enabled char corresponds to the shortcut) associated
	 * with the keyCode.
	 */
	@SuppressWarnings("deprecation")
	void findItemsWithShortcutForKey(List<MenuItemImpl> items, int keyCode, KeyEvent event) {
		final boolean qwerty = isQwertyMode();
		final int metaState = event.getMetaState();
		final KeyCharacterMap.KeyData possibleChars = new KeyCharacterMap.KeyData();
		// Get the chars associated with the keyCode (i.e using any chording combo)
		final boolean isKeyCodeMapped = event.getKeyData(possibleChars);
		// The delete key is not mapped to '\b' so we treat it specially
		if (!isKeyCodeMapped && (keyCode != KeyEvent.KEYCODE_DEL)) return;
		
		// Look for an item whose shortcut is this key.
		final int N = mItems.size();
		for (int i = 0; i < N; i++) {
			MenuItemImpl item = mItems.get(i);
			if (item.hasSubMenu()) {
				((MenuBuilder) item.getSubMenu()).findItemsWithShortcutForKey(items, keyCode, event);
			}
			final char shortcutChar = qwerty ? item.getAlphabeticShortcut() : item.getNumericShortcut();
			if (((metaState & (KeyEvent.META_SHIFT_ON | KeyEvent.META_SYM_ON)) == 0)
				&& (shortcutChar != 0)
				&& (shortcutChar == possibleChars.meta[0] || shortcutChar == possibleChars.meta[2] || (qwerty
					&& shortcutChar == '\b' && keyCode == KeyEvent.KEYCODE_DEL)) && item.isEnabled()) {
				items.add(item);
			}
		}
	}
	
	/*
	 * We want to return the menu item associated with the key, but if there is no
	 * ambiguity (i.e. there is only one menu item corresponding to the key) we want
	 * to return it even if it's not an exact match; this allow the user to
	 * _not_ use the ALT key for example, making the use of shortcuts slightly more
	 * user-friendly. An example is on the G1, '!' and '1' are on the same key, and
	 * in Gmail, Menu+1 will trigger Menu+! (the actual shortcut).
	 *
	 * On the other hand, if two (or more) shortcuts corresponds to the same key,
	 * we have to only return the exact match.
	 */
	@SuppressWarnings("deprecation")
	MenuItemImpl findItemWithShortcutForKey(int keyCode, KeyEvent event) {
		// Get all items that can be associated directly or indirectly with the keyCode
		ArrayList<MenuItemImpl> items = mTempShortcutItemList;
		items.clear();
		findItemsWithShortcutForKey(items, keyCode, event);
		
		if (items.isEmpty()) return null;
		
		final int metaState = event.getMetaState();
		final KeyCharacterMap.KeyData possibleChars = new KeyCharacterMap.KeyData();
		// Get the chars associated with the keyCode (i.e using any chording combo)
		event.getKeyData(possibleChars);
		
		// If we have only one element, we can safely returns it
		final int size = items.size();
		if (size == 1) return items.get(0);
		
		final boolean qwerty = isQwertyMode();
		// If we found more than one item associated with the key,
		// we have to return the exact match
		for (int i = 0; i < size; i++) {
			final MenuItemImpl item = items.get(i);
			final char shortcutChar = qwerty ? item.getAlphabeticShortcut() : item.getNumericShortcut();
			if ((shortcutChar == possibleChars.meta[0] && (metaState & KeyEvent.META_ALT_ON) == 0)
				|| (shortcutChar == possibleChars.meta[2] && (metaState & KeyEvent.META_ALT_ON) != 0)
				|| (qwerty && shortcutChar == '\b' && keyCode == KeyEvent.KEYCODE_DEL)) return item;
		}
		return null;
	}
	
	@Override
	public boolean performIdentifierAction(int id, int flags) {
		// Look for an item whose identifier is the id.
		return performItemAction(findItem(id), flags);
	}
	
	public boolean performItemAction(MenuItem item, int flags) {
		MenuItemImpl itemImpl = (MenuItemImpl) item;
		
		if (itemImpl == null || !itemImpl.isEnabled()) return false;
		
		boolean invoked = itemImpl.invoke();
		
		if (itemImpl.hasCollapsibleActionView()) {
			invoked |= itemImpl.expandActionView();
			if (invoked) {
				close(true);
			}
		}
		else if (item.hasSubMenu()) {
			close(false);
			
			final SubMenuBuilder subMenu = (SubMenuBuilder) item.getSubMenu();
			final ActionProvider provider = item.getActionProvider();
			if (provider != null && provider.hasSubMenu()) {
				provider.onPrepareSubMenu(subMenu);
			}
			invoked |= dispatchSubMenuSelected(subMenu);
			if (!invoked) {
				close(true);
			}
		}
		else {
			if ((flags & FLAG_PERFORM_NO_CLOSE) == 0) {
				close(true);
			}
		}
		
		return invoked;
	}
	
	/**
	 * Closes the visible menu.
	 * 
	 * @param allMenusAreClosing Whether the menus are completely closing
	 *        (true), or whether there is another menu coming in this menu's
	 *        place (false). For example, if the menu is closing because a sub
	 *        menu is about to be shown, <var>allMenusAreClosing</var> is false.
	 */
	final void close(boolean allMenusAreClosing) {
		if (mIsClosing) return;
		
		mIsClosing = true;
		for (WeakReference<MenuPresenter> ref : mPresenters) {
			final MenuPresenter presenter = ref.get();
			if (presenter == null) {
				mPresenters.remove(ref);
			}
			else {
				presenter.onCloseMenu(this, allMenusAreClosing);
			}
		}
		mIsClosing = false;
	}
	
	/** {@inheritDoc} */
	@Override
	public void close() {
		close(true);
	}
	
	/**
	 * Called when an item is added or removed.
	 * 
	 * @param structureChanged true if the menu structure changed, false if only
	 *        item properties changed. (Visibility is a structural property
	 *        since it affects layout.)
	 */
	void onItemsChanged(boolean structureChanged) {
		if (!mPreventDispatchingItemsChanged) {
			if (structureChanged) {
				mIsVisibleItemsStale = true;
				mIsActionItemsStale = true;
			}
			
			dispatchPresenterUpdate(structureChanged);
		}
		else {
			mItemsChangedWhileDispatchPrevented = true;
		}
	}
	
	/**
	 * Stop dispatching item changed events to presenters until
	 * {@link #startDispatchingItemsChanged()} is called. Useful when many menu
	 * operations are going to be performed as a batch.
	 */
	public void stopDispatchingItemsChanged() {
		if (!mPreventDispatchingItemsChanged) {
			mPreventDispatchingItemsChanged = true;
			mItemsChangedWhileDispatchPrevented = false;
		}
	}
	
	public void startDispatchingItemsChanged() {
		mPreventDispatchingItemsChanged = false;
		
		if (mItemsChangedWhileDispatchPrevented) {
			mItemsChangedWhileDispatchPrevented = false;
			onItemsChanged(true);
		}
	}
	
	/**
	 * Called by {@link MenuItemImpl} when its visible flag is changed.
	 * 
	 * @param item The item that has gone through a visibility change.
	 */
	void onItemVisibleChanged(MenuItemImpl item) {
		// Notify of items being changed
		mIsVisibleItemsStale = true;
		onItemsChanged(true);
	}
	
	/**
	 * Called by {@link MenuItemImpl} when its action request status is changed.
	 * 
	 * @param item The item that has gone through a change in action request
	 *        status.
	 */
	void onItemActionRequestChanged(MenuItemImpl item) {
		// Notify of items being changed
		mIsActionItemsStale = true;
		onItemsChanged(true);
	}
	
	ArrayList<MenuItemImpl> getVisibleItems() {
		if (!mIsVisibleItemsStale) return mVisibleItems;
		
		// Refresh the visible items
		mVisibleItems.clear();
		
		final int itemsSize = mItems.size();
		MenuItemImpl item;
		for (int i = 0; i < itemsSize; i++) {
			item = mItems.get(i);
			if (item.isVisible()) {
				mVisibleItems.add(item);
			}
		}
		
		mIsVisibleItemsStale = false;
		mIsActionItemsStale = true;
		
		return mVisibleItems;
	}
	
	/**
	 * This method determines which menu items get to be 'action items' that
	 * will appear in an action bar and which items should be 'overflow items'
	 * in a secondary menu. The rules are as follows:
	 * 
	 * <p>
	 * Items are considered for inclusion in the order specified within the
	 * menu. There is a limit of mMaxActionItems as a total count, optionally
	 * including the overflow menu button itself. This is a soft limit; if an
	 * item shares a group ID with an item previously included as an action
	 * item, the new item will stay with its group and become an action item
	 * itself even if it breaks the max item count limit. This is done to limit
	 * the conceptual complexity of the items presented within an action bar.
	 * Only a few unrelated concepts should be presented to the user in this
	 * space, and groups are treated as a single concept.
	 * 
	 * <p>
	 * There is also a hard limit of consumed measurable space:
	 * mActionWidthLimit. This limit may be broken by a single item that exceeds
	 * the remaining space, but no further items may be added. If an item that
	 * is part of a group cannot fit within the remaining measured width, the
	 * entire group will be demoted to overflow. This is done to ensure room for
	 * navigation and other affordances in the action bar as well as reduce
	 * general UI clutter.
	 * 
	 * <p>
	 * The space freed by demoting a full group cannot be consumed by future
	 * menu items. Once items begin to overflow, all future items become
	 * overflow items as well. This is to avoid inadvertent reordering that may
	 * break the app's intended design.
	 */
	public void flagActionItems() {
		if (!mIsActionItemsStale) return;
		
		// Presenters flag action items as needed.
		boolean flagged = false;
		for (WeakReference<MenuPresenter> ref : mPresenters) {
			final MenuPresenter presenter = ref.get();
			if (presenter == null) {
				mPresenters.remove(ref);
			}
			else {
				flagged |= presenter.flagActionItems();
			}
		}
		
		if (flagged) {
			mActionItems.clear();
			mNonActionItems.clear();
			ArrayList<MenuItemImpl> visibleItems = getVisibleItems();
			final int itemsSize = visibleItems.size();
			for (int i = 0; i < itemsSize; i++) {
				MenuItemImpl item = visibleItems.get(i);
				if (item.isActionButton()) {
					mActionItems.add(item);
				}
				else {
					mNonActionItems.add(item);
				}
			}
		}
		else {
			// Nobody flagged anything, everything is a non-action item.
			// (This happens during a first pass with no action-item presenters.)
			mActionItems.clear();
			mNonActionItems.clear();
			mNonActionItems.addAll(getVisibleItems());
		}
		mIsActionItemsStale = false;
	}
	
	ArrayList<MenuItemImpl> getActionItems() {
		flagActionItems();
		return mActionItems;
	}
	
	ArrayList<MenuItemImpl> getNonActionItems() {
		flagActionItems();
		return mNonActionItems;
	}
	
	public void clearHeader() {
		mHeaderIcon = null;
		mHeaderTitle = null;
		mHeaderView = null;
		
		onItemsChanged(false);
	}
	
	private void setHeaderInternal(final int titleRes, final CharSequence title, final int iconRes,
		final Drawable icon, final View view) {
		final Resources r = getResources();
		
		if (view != null) {
			mHeaderView = view;
			
			// If using a custom view, then the title and icon aren't used
			mHeaderTitle = null;
			mHeaderIcon = null;
		}
		else {
			if (titleRes > 0) {
				mHeaderTitle = r.getText(titleRes);
			}
			else if (title != null) {
				mHeaderTitle = title;
			}
			
			if (iconRes > 0) {
				mHeaderIcon = r.getDrawable(iconRes);
			}
			else if (icon != null) {
				mHeaderIcon = icon;
			}
			
			// If using the title or icon, then a custom view isn't used
			mHeaderView = null;
		}
		
		// Notify of change
		onItemsChanged(false);
	}
	
	/**
	 * Sets the header's title. This replaces the header view. Called by the
	 * builder-style methods of subclasses.
	 * 
	 * @param title The new title.
	 * @return This MenuBuilder so additional setters can be called.
	 */
	protected MenuBuilder setHeaderTitleInt(CharSequence title) {
		setHeaderInternal(0, title, 0, null, null);
		return this;
	}
	
	/**
	 * Sets the header's title. This replaces the header view. Called by the
	 * builder-style methods of subclasses.
	 * 
	 * @param titleRes The new title (as a resource ID).
	 * @return This MenuBuilder so additional setters can be called.
	 */
	protected MenuBuilder setHeaderTitleInt(int titleRes) {
		setHeaderInternal(titleRes, null, 0, null, null);
		return this;
	}
	
	/**
	 * Sets the header's icon. This replaces the header view. Called by the
	 * builder-style methods of subclasses.
	 * 
	 * @param icon The new icon.
	 * @return This MenuBuilder so additional setters can be called.
	 */
	protected MenuBuilder setHeaderIconInt(Drawable icon) {
		setHeaderInternal(0, null, 0, icon, null);
		return this;
	}
	
	/**
	 * Sets the header's icon. This replaces the header view. Called by the
	 * builder-style methods of subclasses.
	 * 
	 * @param iconRes The new icon (as a resource ID).
	 * @return This MenuBuilder so additional setters can be called.
	 */
	protected MenuBuilder setHeaderIconInt(int iconRes) {
		setHeaderInternal(0, null, iconRes, null, null);
		return this;
	}
	
	/**
	 * Sets the header's view. This replaces the title and icon. Called by the
	 * builder-style methods of subclasses.
	 * 
	 * @param view The new view.
	 * @return This MenuBuilder so additional setters can be called.
	 */
	protected MenuBuilder setHeaderViewInt(View view) {
		setHeaderInternal(0, null, 0, null, view);
		return this;
	}
	
	public CharSequence getHeaderTitle() {
		return mHeaderTitle;
	}
	
	public Drawable getHeaderIcon() {
		return mHeaderIcon;
	}
	
	public View getHeaderView() {
		return mHeaderView;
	}
	
	/**
	 * Gets the root menu (if this is a submenu, find its root menu).
	 * 
	 * @return The root menu.
	 */
	public MenuBuilder getRootMenu() {
		return this;
	}
	
	/**
	 * Sets the current menu info that is set on all items added to this menu
	 * (until this is called again with different menu info, in which case that
	 * one will be added to all subsequent item additions).
	 * 
	 * @param menuInfo The extra menu information to add.
	 */
	public void setCurrentMenuInfo(ContextMenuInfo menuInfo) {
		mCurrentMenuInfo = menuInfo;
	}
	
	void setOptionalIconsVisible(boolean visible) {
		mOptionalIconsVisible = visible;
	}
	
	boolean getOptionalIconsVisible() {
		return mOptionalIconsVisible;
	}
	
	public boolean expandItemActionView(MenuItemImpl item) {
		if (mPresenters.isEmpty()) return false;
		
		boolean expanded = false;
		
		stopDispatchingItemsChanged();
		for (WeakReference<MenuPresenter> ref : mPresenters) {
			final MenuPresenter presenter = ref.get();
			if (presenter == null) {
				mPresenters.remove(ref);
			}
			else if ((expanded = presenter.expandItemActionView(this, item))) {
				break;
			}
		}
		startDispatchingItemsChanged();
		
		if (expanded) {
			mExpandedItem = item;
		}
		return expanded;
	}
	
	public boolean collapseItemActionView(MenuItemImpl item) {
		if (mPresenters.isEmpty() || mExpandedItem != item) return false;
		
		boolean collapsed = false;
		
		stopDispatchingItemsChanged();
		for (WeakReference<MenuPresenter> ref : mPresenters) {
			final MenuPresenter presenter = ref.get();
			if (presenter == null) {
				mPresenters.remove(ref);
			}
			else if ((collapsed = presenter.collapseItemActionView(this, item))) {
				break;
			}
		}
		startDispatchingItemsChanged();
		
		if (collapsed) {
			mExpandedItem = null;
		}
		return collapsed;
	}
	
	public MenuItemImpl getExpandedItem() {
		return mExpandedItem;
	}
	
	public boolean bindNativeOverflow(android.view.Menu menu, android.view.MenuItem.OnMenuItemClickListener listener,
		HashMap<android.view.MenuItem, MenuItemImpl> map) {
		final List<MenuItemImpl> nonActionItems = getNonActionItems();
		if (nonActionItems == null || nonActionItems.size() == 0) return false;
		
		boolean visible = false;
		menu.clear();
		for (MenuItemImpl nonActionItem : nonActionItems) {
			if (!nonActionItem.isVisible()) {
				continue;
			}
			visible = true;
			
			android.view.MenuItem nativeItem;
			if (nonActionItem.hasSubMenu()) {
				android.view.SubMenu nativeSub = menu.addSubMenu(nonActionItem.getGroupId(), nonActionItem.getItemId(),
					nonActionItem.getOrder(), nonActionItem.getTitle());
				
				SubMenuBuilder subMenu = (SubMenuBuilder) nonActionItem.getSubMenu();
				for (MenuItemImpl subItem : subMenu.getVisibleItems()) {
					android.view.MenuItem nativeSubItem = nativeSub.add(subItem.getGroupId(), subItem.getItemId(),
						subItem.getOrder(), subItem.getTitle());
					
					nativeSubItem.setIcon(subItem.getIcon());
					nativeSubItem.setOnMenuItemClickListener(listener);
					nativeSubItem.setEnabled(subItem.isEnabled());
					nativeSubItem.setIntent(subItem.getIntent());
					nativeSubItem.setNumericShortcut(subItem.getNumericShortcut());
					nativeSubItem.setAlphabeticShortcut(subItem.getAlphabeticShortcut());
					nativeSubItem.setTitleCondensed(subItem.getTitleCondensed());
					nativeSubItem.setCheckable(subItem.isCheckable());
					nativeSubItem.setChecked(subItem.isChecked());
					
					if (subItem.isExclusiveCheckable()) {
						nativeSub.setGroupCheckable(subItem.getGroupId(), true, true);
					}
					
					map.put(nativeSubItem, subItem);
				}
				
				nativeItem = nativeSub.getItem();
			}
			else {
				nativeItem = menu.add(nonActionItem.getGroupId(), nonActionItem.getItemId(), nonActionItem.getOrder(),
					nonActionItem.getTitle());
			}
			nativeItem.setIcon(nonActionItem.getIcon());
			nativeItem.setOnMenuItemClickListener(listener);
			nativeItem.setEnabled(nonActionItem.isEnabled());
			nativeItem.setIntent(nonActionItem.getIntent());
			nativeItem.setNumericShortcut(nonActionItem.getNumericShortcut());
			nativeItem.setAlphabeticShortcut(nonActionItem.getAlphabeticShortcut());
			nativeItem.setTitleCondensed(nonActionItem.getTitleCondensed());
			nativeItem.setCheckable(nonActionItem.isCheckable());
			nativeItem.setChecked(nonActionItem.isChecked());
			
			if (nonActionItem.isExclusiveCheckable()) {
				menu.setGroupCheckable(nonActionItem.getGroupId(), true, true);
			}
			
			map.put(nativeItem, nonActionItem);
		}
		return visible;
	}
}
