package pwr.rss.reader.fragments

import com.actionbarsherlock.app.SherlockFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import pwr.rss.reader.ApplicationObject
import pwr.rss.reader.FeedsListActivity
import pwr.rss.reader.R
import pwr.rss.reader.utils.PreferencesManager
import pwr.rss.reader.views.ChannelView
import pwr.rss.reader.views.ViewHelper.findView

class SlideMenuFragment extends SherlockFragment
		with android.widget.RadioGroup.OnCheckedChangeListener
		with android.widget.CompoundButton.OnCheckedChangeListener
		with OnClickListener {

	private lazy val activity = getActivity.asInstanceOf[FeedsListActivity]
	private lazy val view = getView
	private lazy val applicationObject = activity.getApplication.asInstanceOf[ApplicationObject]
	private lazy val preferencesManager = new PreferencesManager(activity)
	private lazy val channelsContainer = view.findViewById(R.id.container_channels).asInstanceOf[LinearLayout]

	override def onActivityCreated(savedInstanceState: Bundle) = {
		super.onActivityCreated(savedInstanceState)
	}

	override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle) = {
		inflater.inflate(R.layout.menu_sliding, container, false)
	}

	override def onViewCreated(view: View, savedInstanceState: Bundle) = {
		super.onViewCreated(view, savedInstanceState)
		configureChannels(view)
		configureCheckBox(view)
		configureRadioGroup(view)
		configureButton(view)
	}

	private def configureChannels(view: View) = {
		val activity = getActivity
		val channelsList = applicationObject.getAllChannelsList
		channelsList.foreach(channel => {
			val channelView = new ChannelView(activity, channel)
			channelsContainer.addView(channelView)
		})
	}

	def updateUnreadCount = {
		val channelsCount = channelsContainer.getChildCount
		for (i <- 0 until channelsCount) {
			val channel = channelsContainer.getChildAt(i).asInstanceOf[ChannelView]
			channel.updateUnreadCount
		}
	}

	/**
	  * CheckBox selected only
	  */
	private def configureCheckBox(view: View) = {
		val checkBoxSelectedOnly = findView[CheckBox](view, R.id.checkBoxSelectedOnly)
		checkBoxSelectedOnly.setOnCheckedChangeListener(this)
		checkBoxSelectedOnly.setChecked(preferencesManager.isSelectedOnlyChecked)
	}

	override def onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) =
		preferencesManager.setSelectedOnlyChecked(isChecked)

	/**
	  * RadioGroup read type
	  */
	private def configureRadioGroup(view: View) = {
		val radioGroup = findView[RadioGroup](view, R.id.radioGroupSlidingMenu)
		radioGroup.setOnCheckedChangeListener(this)
		configureRadioButtons(radioGroup)
	}

	private def configureRadioButtons(group: RadioGroup) = {
		val radioId = preferencesManager.getSelectedRadioButtonId
		val radioButton = findView[RadioButton](group, radioId)
		radioButton.setChecked(true)
	}

	override def onCheckedChanged(group: RadioGroup, checkedId: Int) =
		preferencesManager.setSelectedRadioButtonId(checkedId)

	/**
	  * Button refresh list
	  */

	private def configureButton(view: View) = {
		val button = findView[Button](view, R.id.buttonRefreshListView)
		button.setOnClickListener(this)
	}

	override def onClick(v: View) = {
		activity.reloadList
		showRefreshToast
	}

	private def showRefreshToast =
		applicationObject.toastFactory.showBottomToast(R.string.message_refreshing_feeds_list)
}