package com.wp.demo.psbcdemo1.demo;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.wp.demo.psbcdemo1.psbccase.R;
import com.wp.demo.psbcdemo1.tools.BaseFragment;
import com.wp.demo.psbcdemo1.tools.BaseFragmentActivity;
import com.wp.demo.psbcdemo1.demo.PSBCDatabaseHelper.*;

public class DemoActivity extends BaseFragmentActivity implements
		DemoFragment.UserSelectLogin {

	public final static String KEY_TOKEN = "key_token";
	ShowUserDataFragment mShowFragment;
	static boolean HAS_LOGIN;
	static String OBJ; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.demo_activity_layout);
		if (null == savedInstanceState) {
			DemoFragment fragment = new DemoFragment();
			getSupportFragmentManager().beginTransaction()
					.add(R.id.fragment_container, fragment).commit();
		}
		ContentValues values = new ContentValues();
		values.put(Personnel.USER_NAME, "pekall");
		values.put(Personnel.PASSWORD, "pekall");
		values.put(Personnel.ID, "pekall");
		getContentResolver().insert(PSBCContentProvider.PERSONNEL_URI, values);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (HAS_LOGIN) {
			if (null == mShowFragment) {
				mShowFragment = new ShowUserDataFragment();
				Bundle bundle = new Bundle();
				bundle.putString(KEY_TOKEN, OBJ);
				mShowFragment.setArguments(bundle);
			}
			getSupportFragmentManager().beginTransaction().setCustomAnimations(
					android.R.anim.fade_in, android.R.anim.fade_out);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.fragment_container, mShowFragment).commit();
		}
	}
	
	@Override
	public void onSelectUser(String obj) {
		// TODO Auto-generated method stub
		HAS_LOGIN = true;
		OBJ = obj;
		if (null == mShowFragment) {
			mShowFragment = new ShowUserDataFragment();
			Bundle bundle = new Bundle();
			bundle.putString(KEY_TOKEN, OBJ);
			mShowFragment.setArguments(bundle);
		}
		getSupportFragmentManager().beginTransaction().setCustomAnimations(
				android.R.anim.fade_in, android.R.anim.fade_out);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragment_container, mShowFragment).commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			BaseFragment demoFragment = (BaseFragment) getSupportFragmentManager()
					.findFragmentById(R.id.fragment_container);
			if (null != demoFragment && !(demoFragment instanceof DemoFragment)) {
				HAS_LOGIN = false;
				DemoFragment fragment = new DemoFragment();
				getSupportFragmentManager().beginTransaction()
						.setCustomAnimations(android.R.anim.fade_in,
								android.R.anim.fade_out);
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.fragment_container, fragment).commit();
			} else {
				finish();
			}
		}
		return super.onOptionsItemSelected(item);
	}
}
