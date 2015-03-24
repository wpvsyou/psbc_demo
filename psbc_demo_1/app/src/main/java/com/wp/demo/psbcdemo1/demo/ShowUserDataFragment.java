package com.wp.demo.psbcdemo1.demo;

import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.wp.demo.psbcdemo1.psbccase.R;
import com.wp.demo.psbcdemo1.bean.BeanCompanyData;
import com.wp.demo.psbcdemo1.tools.BaseFragment;
import com.wp.demo.psbcdemo1.demo.PSBCDatabaseHelper.*;

import java.util.ArrayList;
import java.util.List;

public class ShowUserDataFragment extends BaseFragment {

	class DataChangedObserver extends ContentObserver {
		Handler handler;

		/**
		 * Creates a content observer.
		 * 
		 * @param handler
		 *            The handler to run {@link #onChange} on, or null if none.
		 */
		public DataChangedObserver(Handler handler) {
			super(handler);
			this.handler = handler;
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			handler.sendEmptyMessage(MSG_DATA_CHANGED);
		}
	}

	private final static Handler DATA_CHANGED_HANDLER = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_DATA_CHANGED:
				if (DEBUG)
					Log.d(TAG,
							"Data changed handler get change data massage to notify data changed!");
				mAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		}
	};

	private final static int MSG_DATA_CHANGED = 1;
	private final static int LOC_DATA_TITLE = 0;
	private final static int REMOTE_DATA_TITLE = 1;

	protected String mToken; // ID
	protected BeanCompanyData mBeanCompanyData;
	static FragAdapter mAdapter; // Use in observer to notify data changed!
	BaseFragment mLocDataFragment, mRemoteDataFragment;
	View mLocDataTable, mRemoteDataTitle;
	LinearLayout mLocDataLayout, mRemoteDataLayout;
	ViewPager mViewPager;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (null != savedInstanceState) {
			mToken = savedInstanceState.getString(DemoActivity.KEY_TOKEN);
		}

		mLocDataFragment = new LocDataFragment();
		mRemoteDataFragment = new RemoteDataFragment();
		return inflater.inflate(R.layout.show_user_data_fragment_layout,
				container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
		mLocDataLayout = (LinearLayout) view.findViewById(R.id.title_loc_data);
		mRemoteDataLayout = (LinearLayout) view
				.findViewById(R.id.title_remote_data);
		mLocDataTable = (View) view.findViewById(R.id.loc_data_table_slider);
		mRemoteDataTitle = (View) view
				.findViewById(R.id.remote_data_table_slider);
		DataChangedObserver dataChangedObserver = new DataChangedObserver(
				DATA_CHANGED_HANDLER);
		getActivity().getContentResolver()
				.registerContentObserver(PSBCContentProvider.COMPANY_DATA_URI,
						true, dataChangedObserver);
		getActivity().getContentResolver()
				.registerContentObserver(PSBCContentProvider.COMPANY_DATA_URI,
						true, dataChangedObserver);
		init();
	}

	protected void init() {
		List<Fragment> list = new ArrayList<>();
		list.add(mLocDataFragment);
		list.add(mRemoteDataFragment);
		mAdapter = new FragAdapter(getFragmentManager(), list);
		mViewPager.setCurrentItem(LOC_DATA_TITLE);
		selectItemToShowData(LOC_DATA_TITLE);
		mViewPager.setAdapter(mAdapter);
		mLocDataLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectItemToShowData(LOC_DATA_TITLE);
				mViewPager.setCurrentItem(LOC_DATA_TITLE);
			}
		});
		mRemoteDataLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectItemToShowData(REMOTE_DATA_TITLE);
				mViewPager.setCurrentItem(REMOTE_DATA_TITLE);
			}
		});
		mViewPager
				.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
					@Override
					public void onPageScrolled(int position,
							float positionOffset, int positionOffsetPixels) {
					}

					@Override
					public void onPageSelected(int position) {
						selectItemToShowData(position);
					}

					@Override
					public void onPageScrollStateChanged(int state) {
					}
				});
	}

	protected void selectItemToShowData(int whichTitleToShowData) {
		switch (whichTitleToShowData) {
		case REMOTE_DATA_TITLE:
			mLocDataTable.setVisibility(View.GONE);
			mRemoteDataTitle.setVisibility(View.VISIBLE);
			break;
		case LOC_DATA_TITLE:
			mLocDataTable.setVisibility(View.VISIBLE);
			mRemoteDataTitle.setVisibility(View.GONE);
			break;
		default:
			break;
		}
	}

	protected BeanCompanyData getLocData(String token) {
		BeanCompanyData locData = new BeanCompanyData();
		String select = Company_data.ID + "=?";
		String[] args = new String[] { token };
		Cursor cursor = getActivity().getContentResolver().query(
				PSBCContentProvider.COMPANY_DATA_URI, null, select, args, null);
		if (null != cursor) {
			try {
				String json = cursor.getString(cursor
						.getColumnIndex(Company_data.DATA_1));
				locData = new Gson().fromJson(json, BeanCompanyData.class);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				cursor.close();
			}
		}
		return locData;
	}

	protected BeanCompanyData getRemoteData(String token) {
		return new BeanCompanyData();
	}
}
