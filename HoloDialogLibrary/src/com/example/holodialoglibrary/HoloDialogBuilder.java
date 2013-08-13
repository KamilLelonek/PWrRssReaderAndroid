package com.example.holodialoglibrary;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HoloDialogBuilder extends Dialog implements android.view.View.OnClickListener {
	private Context context;
	private View mDialogView;
	private TextView mTitle;
	private ImageView mIcon;
	private TextView mMessage;
	private View mDivider;
	private LinearLayout customViewContainer;
	Resources resources;
	
	public HoloDialogBuilder(Context context) {
		super(context, R.style.Dialog_No_Border);
		
		this.context = context;
		resources = context.getResources();
		setCanceledOnTouchOutside(true);
		
		mDialogView = View.inflate(context, R.layout.custom_dialog_layout, null);
		customViewContainer = (LinearLayout) mDialogView.findViewById(R.id.customPanel);
		setContentView(mDialogView);
		
		mTitle = (TextView) mDialogView.findViewById(R.id.alertTitle);
		mMessage = (TextView) mDialogView.findViewById(R.id.message);
		mIcon = (ImageView) mDialogView.findViewById(R.id.icon);
		mDivider = mDialogView.findViewById(R.id.titleDivider);
	}
	
	public HoloDialogBuilder setDividerColor(int color) {
		mDivider.setBackgroundColor(resources.getColor(color));
		return this;
	}
	
	public HoloDialogBuilder setTitleText(int text) {
		mTitle.setText(text);
		return this;
	}
	
	public HoloDialogBuilder setTitleColor(int color) {
		mTitle.setTextColor(resources.getColor(color));
		return this;
	}
	
	public HoloDialogBuilder setMessage(int textResId) {
		mMessage.setText(textResId);
		return this;
	}
	
	public HoloDialogBuilder setIcon(int drawableResId) {
		mIcon.setImageResource(drawableResId);
		return this;
	}
	
	public HoloDialogBuilder setIcon(Drawable icon) {
		mIcon.setImageDrawable(icon);
		return this;
	}
	
	public HoloDialogBuilder setCustomView(int resId) {
		View customView = View.inflate(context, resId, null);
		customViewContainer.addView(customView);
		return this;
	}
	
	public void setButtonsColorPressed(int color) {
		setButtonsColorPressed(R.id.buttonPositive, color);
		setButtonsColorPressed(R.id.buttonNegative, color);
		setButtonsColorPressed(R.id.buttonNeutral, color);
	}
	
	@SuppressWarnings("deprecation")
	private void setButtonsColorPressed(int buttonId, int color) {
		Button button = (Button) customViewContainer.findViewById(buttonId);
		button.setBackgroundDrawable(new HoloStateListDrawable(resources.getColor(color)));
	}
	
	public HoloDialogBuilder setOnPositiveClickListener(View.OnClickListener listener) {
		return setOnClickListener(R.id.buttonPositive, listener);
	}
	
	public HoloDialogBuilder setOnNegativeClickListener(View.OnClickListener listener) {
		return setOnClickListener(R.id.buttonNegative, listener);
	}
	
	public HoloDialogBuilder setOnNeutralClickListener(View.OnClickListener listener) {
		return setOnClickListener(R.id.buttonNeutral, listener);
	}
	
	public HoloDialogBuilder setOnPositiveClickDismiss() {
		return setOnClickListener(R.id.buttonPositive, this);
	}
	
	public HoloDialogBuilder setOnNegativeClickDismiss() {
		return setOnClickListener(R.id.buttonNegative, this);
	}
	
	public HoloDialogBuilder setOnNeutralClickDismiss() {
		return setOnClickListener(R.id.buttonNeutral, this);
	}
	
	private HoloDialogBuilder setOnClickListener(int resId, final View.OnClickListener listener) {
		customViewContainer.findViewById(resId).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
				listener.onClick(v);
			}
		});
		return this;
	}
	
	public HoloDialogBuilder setPositiveButtonText(int textId) {
		return setButtonText(textId, R.id.buttonPositive);
	}
	
	public HoloDialogBuilder setNegativeButtonText(int textId) {
		return setButtonText(textId, R.id.buttonNegative);
	}
	
	public HoloDialogBuilder setNeutralButtonText(int textId) {
		return setButtonText(textId, R.id.buttonNeutral);
	}
	
	private HoloDialogBuilder setButtonText(int textId, int buttonId) {
		Button button = (Button) customViewContainer.findViewById(buttonId);
		button.setText(textId);
		button.setVisibility(View.VISIBLE);
		return this;
	}
	
	@Override
	public void show() {
		if (mTitle.getText().equals("")) {
			mDialogView.findViewById(R.id.topPanel).setVisibility(View.GONE);
		}
		super.show();
	}
	
	@Override
	public void onClick(View v) {
		dismiss();
	}
}