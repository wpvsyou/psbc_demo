package com.wp.demo.psbcdemo2.tools;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wp.demo.psbcdemo2.R;

/**
 *
 */
public abstract class BaseActivity extends Activity implements
		View.OnClickListener {
	private static final String TAG = "BaseActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
			Log.d(TAG, "In BaseActivity, ActionBar back button onClick!");
			onBackHomeOnClick();
		}
	}

	public void onBackHomeOnClick() {
		Log.d(TAG, "In BaseActivity onBackHomeOnClick");
	}
}
