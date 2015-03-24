package com.wp.demo.psbcdemo1.demo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangpeng on 15-3-21.
 */
public class FragAdapter extends FragmentPagerAdapter {

	List<Fragment> data = new ArrayList<>();

	public FragAdapter(FragmentManager fm) {
		super(fm);
	}

	public FragAdapter(FragmentManager fm, List<Fragment> data) {
		super(fm);
		this.data = data;
	}

	@Override
	public Fragment getItem(int position) {
		return data.get(position);
	}

	@Override
	public int getCount() {
		return data.size();
	}

	/**
	 * add the fragment to the special position
	 * 
	 * @param location
	 *            the position be added to.
	 * @param fragment
	 */
	public void addFragment(int location, Fragment fragment) {
		this.data.add(location, fragment);
		this.notifyDataSetChanged();
	}

	/**
	 * add the fragment to the default position.the end of the list.
	 * 
	 * @param fragment
	 */
	public void addFragment(Fragment fragment) {
		this.data.add(fragment);
		this.notifyDataSetChanged();
	}
}
