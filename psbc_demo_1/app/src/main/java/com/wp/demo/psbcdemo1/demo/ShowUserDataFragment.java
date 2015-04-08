package com.wp.demo.psbcdemo1.demo;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.wp.demo.psbc.count.PSBCCount.Company_data;
import com.wp.demo.psbc.count.PSBCCount.Uri;
import com.wp.demo.psbcdemo1.psbccase.R;
import com.wp.demo.psbcdemo1.tools.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import bean.BeanCompanyData;

public class ShowUserDataFragment extends BaseFragment {

    class DataChangedObserver extends ContentObserver {
        Handler handler;

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
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
            Log.d(TAG + " ShowUserDataFragment", "savedInstanceState un empty!");
                mToken = savedInstanceState.getString(DemoActivity.KEY_TOKEN);
        } Log.d(TAG + " ShowUserDataFragment", "The saveInstanceState was empty!");
        return inflater.inflate(R.layout.show_user_data_fragment_layout,
                container, false);
    }

    @Override
    public void setToken(String obj) {
        Log.d(TAG + " ShowUserDataFragment" , "setToken [" + obj + "]");
        super.setToken(obj);
        mToken = obj;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        BaseFragment.mCallback.showBtn();
        Log.d(TAG, " show user on resume onSelectUser");
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
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
                .registerContentObserver(Uri.COMPANY_DATA_URI,
                        true, dataChangedObserver);
        getActivity().getContentResolver()
                .registerContentObserver(Uri.COMPANY_DATA_URI,
                        true, dataChangedObserver);
        Log.d(TAG + " ShowUserDataFragment", "new fragment instance the current token is [" + mToken + "]");
        mLocDataFragment = new LocDataFragment();
        Bundle bundle = new Bundle();
        bundle.putString(DemoActivity.KEY_TOKEN, mToken);
        mLocDataFragment.setArguments(bundle);
        mRemoteDataFragment = new RemoteDataFragment();
        mRemoteDataFragment.setCallback(mCallback);
        Bundle bundle1 = new Bundle();
        bundle1.putString(DemoActivity.KEY_TOKEN, mToken);
        mRemoteDataFragment.setArguments(bundle1);
        init();
    }

    protected void init() {
        final List<Fragment> list = new ArrayList<>();
        list.add(mLocDataFragment);
        list.add(mRemoteDataFragment);
        mAdapter = new FragAdapter(getFragmentManager(), list);
        mViewPager.setCurrentItem(LOC_DATA_TITLE);
        selectItemToShowData(LOC_DATA_TITLE);
        mViewPager.setAdapter(mAdapter);
        Log.d(TAG, "updateCompanyData check the token -> " + TokenHelper.getInstance().getToken());
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
                        BaseFragment fragment = (BaseFragment)list.get(position);
                        fragment.setToken(mToken);
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

    public void onUserChanged(String token, ContentResolver contentResolver) {
        Log.d(TAG + " ShowUserDataFragment", "onUserChanged the token is [" + token + "]");
        mToken = token;
        String selection = Company_data.ID + "=?";
        String[] selectionArgs = new String[]{mToken};
        Log.d(TAG + " ShowUserDataFragment", "check the selection [" + selection + "] the selectionArgs [" + mToken + "]");
        Cursor cursor = contentResolver.query(Uri.COMPANY_DATA_URI, null, selection, selectionArgs, null);
        if (null != cursor) {
            Log.d(TAG + " ShowUserDataFragment", "The cursor un empty!");
        } else Log.d(TAG + " ShowUserDataFragment", "The cursor was empty!");
        if (null == mLocDataFragment) {
            Log.d(TAG + " ShowUserDataFragment", "The location data fragment was empty!");
        }
        if (null != mLocDataFragment && null != mRemoteDataFragment) {
            mLocDataFragment.updateToken(mToken);
            mRemoteDataFragment.updateToken(mToken);
            if (null != cursor && cursor.moveToFirst()) {
                mLocDataFragment.onViewChanged(cursor);
                mRemoteDataFragment.onViewChanged(cursor);
            } else {
                mLocDataFragment.showEmptyView();
                mRemoteDataFragment.showEmptyView();
            }
            if (null != mAdapter) {
                mAdapter.notifyDataSetChanged();
            }
        } Log.d(TAG + " ShowUserDataFragment", "The location data fragment or remote data fragment was empty!");
    }
}
