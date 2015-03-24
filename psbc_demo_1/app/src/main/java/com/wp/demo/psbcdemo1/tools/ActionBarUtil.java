package com.wp.demo.psbcdemo1.tools;

import android.app.ActionBar;
import android.app.Activity;

import com.wp.demo.psbcdemo1.psbccase.R;

/**
 *
 */
public class ActionBarUtil {
	public static void setupActionBar(Activity activity) {
		ActionBar actionBar = activity.getActionBar();
		if (actionBar != null) {
			actionBar.setDisplayShowCustomEnabled(true);
			actionBar.setCustomView(R.layout.setting_action_bar_layout);
		}
	}
}
