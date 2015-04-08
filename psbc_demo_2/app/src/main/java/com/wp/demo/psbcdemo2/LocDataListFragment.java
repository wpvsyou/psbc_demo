package com.wp.demo.psbcdemo2;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.wp.demo.psbc.count.PSBCCount;
import com.wp.demo.psbcdemo2.scrollerdelete_master.DeleteAdapter;
import com.wp.demo.psbcdemo2.scrollerdelete_master.ScrollListviewDelete;
import com.wp.demo.psbcdemo2.tools.BaseFragment;

public class LocDataListFragment extends BaseFragment implements ScrollListviewDelete.ItemClickListener{

    private final static int MSG_DATA_CHANGED = 1;

    @Override
    public void onItemClick(int position) {

    }

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

    final Handler mDataChangedHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_DATA_CHANGED) {
                Log.d(TAG, "MSG_DATA_CHANGED");
                if (null != mAdapter) {
                    String selection = PSBCCount.Company_data.ID + "=? or " + PSBCCount.Company_data.ID + "=?";
                    String[] selectionArgs = new String[]{DemoActivity.getKeyToken(), DemoActivity.PUBLIC_KEY};
                    Log.d(TAG, "check the token [" + DemoActivity.getKeyToken() + "]");
                    Log.d(TAG, "observer : check the uri --> " + PSBCCount.Uri.COMPANY_DATA_URI);
                    Cursor cursor = mContentResolver.
                            query(PSBCCount.Uri_local.LOCAL_DATA_URI, null, selection, selectionArgs, null);
                    if (null != cursor) {
                        mCenterLayout.setVisibility(View.GONE);
                        Log.d(TAG, "observer : the cursor un empty! cursor --> " + cursor.toString());
                        mAdapter.changeCursor(cursor);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        Log.d(TAG, "observer : the cursor was empty!");
                        mCenterLayout.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    };

    LinearLayout mCenterLayout;
    static String TOKEN;
    Cursor mSourceCursor;
    ContentResolver mContentResolver;
    ScrollListviewDelete mListView;
    DeleteAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContentResolver = getActivity().getContentResolver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        if (null != savedInstanceState) {
            TOKEN = savedInstanceState.getString(DemoActivity.KEY_TOKEN);
        } else {
            Log.d(TAG, "The 【LocDataListFragment】savedInstanceState is empty!");
        }
        return inflater.inflate(R.layout.location_data_list_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);
        mCenterLayout = (LinearLayout) view.findViewById(R.id.center_layout);
        mListView = (ScrollListviewDelete) view.findViewById(android.R.id.list);
        Bundle bundle = getArguments();
        if (null != bundle) {
            TOKEN = bundle.getString(DemoActivity.KEY_TOKEN);
        } else Log.d(TAG, "Get arguments was empty!");
        DataChangedObserver observer = new DataChangedObserver(mDataChangedHandler);
        getActivity().getContentResolver().registerContentObserver(PSBCCount.Uri.PERSONNEL_URI, true, observer);
        getActivity().getContentResolver().registerContentObserver(PSBCCount.Uri_local.LOCAL_DATA_URI, true, observer);

        String selection = PSBCCount.Company_data.ID + "=? or " + PSBCCount.Company_data.ID + "=?";
        Log.d(TAG, "check the token [" + DemoActivity.getKeyToken() + "]");
        String[] selectionArgs = new String[]{DemoActivity.getKeyToken(), DemoActivity.PUBLIC_KEY};
        mSourceCursor = getActivity().getContentResolver().query(
                PSBCCount.Uri_local.LOCAL_DATA_URI, null, selection,
                selectionArgs, null);
        mAdapter = new DeleteAdapter(getActivity(), new DeleteAdapter.NotifyUpdateOrDelete() {
            @Override
            public void onDataChanged() {
                mDataChangedHandler.sendEmptyMessage(MSG_DATA_CHANGED);
            }
        });
        mListView.setAdapter(mAdapter);
        mCenterLayout.setVisibility(View.VISIBLE);
        init(TOKEN);
    }

    protected void init(String token) {
        Log.d(TAG, "The TOKEN is [" + token + "]");
        if (mSourceCursor != null && mSourceCursor.moveToFirst()) {
            mCenterLayout.setVisibility(View.GONE);
            try {
                mAdapter.changeCursor(mSourceCursor);
                mAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "mCursor was empty!");
        }
    }

    public void updateToken(String token) {
        Log.d(TAG, "change token to [" + token + "]");
        TOKEN = token;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != mSourceCursor) {
            mSourceCursor.close();
        }
    }

    public void updateView() {
        mDataChangedHandler.sendEmptyMessage(MSG_DATA_CHANGED);
    }
}
