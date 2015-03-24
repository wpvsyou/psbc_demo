package com.wp.demo.psbcdemo1.tools;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wp.demo.psbcdemo1.psbccase.R;

/**
 *
 */
public abstract class BasePreferenceActivity extends PreferenceActivity
		implements View.OnClickListener {
	private static final String TAG = "BasePreferenceActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Window window = getWindow();
		WindowManager.LayoutParams winParams = window.getAttributes();
		winParams.flags = winParams.flags
				| WindowManager.LayoutParams.FLAG_SECURE;
		super.onCreate(savedInstanceState);
		ActionBarUtil.setupActionBar(this);
		if (null != getActionBar()) {
			Log.d(TAG, "The actionBar un empty!");
			View view = getActionBar().getCustomView();
			TextView tv = (TextView) view
					.findViewById(R.id.setting_action_bar_title_tv);
			if (null != getTitle() && null != tv) {
				Log.d(TAG, "The title view un empty, set a new title ["
						+ getTitle() + "]");
				tv.setText(getTitle());
			}
			LinearLayout backButton = (LinearLayout) view
					.findViewById(R.id.actionbarLayoutId);
			if (null != backButton) {
				backButton.setOnClickListener(this);
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.actionbarLayoutId) {
			Log.d(TAG,
					"In BasePreferenceActivity, ActionBar back button onClick!");
			onBackHomeOnClick();
		}
	}

	public void onBackHomeOnClick() {
		Log.d(TAG, "In BasePreferenceActivity onBackHomeOnClick");
	}
}
