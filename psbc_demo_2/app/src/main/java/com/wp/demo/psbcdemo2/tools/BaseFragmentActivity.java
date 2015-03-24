package com.wp.demo.psbcdemo2.tools;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wp.demo.psbcdemo2.R;

/**
 * 
 * Created by wangpeng on 15-1-14.
 */
public abstract class BaseFragmentActivity extends FragmentActivity implements
		View.OnClickListener {
	private final static String TAG = "BaseFragmentActivity";

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
					"In BaseFragmentActivity, ActionBar back button onClick!");
			onBackHomeOnClick();
		}
	}

	public void onBackHomeOnClick() {
		Log.d(TAG, "In BaseFragmentActivity onBackHomeOnClick");
	}
}
