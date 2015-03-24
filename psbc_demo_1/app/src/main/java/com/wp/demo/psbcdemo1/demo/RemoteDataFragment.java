package com.wp.demo.psbcdemo1.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wp.demo.psbcdemo1.psbccase.R;
import com.wp.demo.psbcdemo1.tools.BaseFragment;

/**
 * Created by wangpeng on 15-3-21.
 */
public class RemoteDataFragment extends BaseFragment {

	TextView mCenterTextView;
	LinearLayout mCenterLayout;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.show_data_fragment_layout, container,
				false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		mCenterTextView = (TextView) view.findViewById(R.id.center_text);
		mCenterLayout = (LinearLayout) view.findViewById(R.id.center_layout);
		init();
	}

	protected void init() {
		showEmptyText();
	}

	protected void showEmptyText() {
		mCenterLayout.setVisibility(View.VISIBLE);
		mCenterTextView.setVisibility(View.VISIBLE);
		mCenterTextView.setText(getActivity().getString(
				R.string.no_remote_data_text));
	}
}
