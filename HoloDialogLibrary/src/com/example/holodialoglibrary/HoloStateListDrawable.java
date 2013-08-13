package com.example.holodialoglibrary;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;

public class HoloStateListDrawable extends StateListDrawable {
	public HoloStateListDrawable(int color) {
		addState(new int[] { android.R.attr.state_pressed }, new ColorDrawable(color));
	}
}