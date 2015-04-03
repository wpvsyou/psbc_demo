package com.wp.demo.psbcdemo1.tools;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * Created by wangpeng on 15-3-21.
 */
public class BaseFragment extends Fragment {
    protected final static String TAG = "FTPService + PSBC_case_demo_debug";
    protected static boolean DEBUG = true;

    public void onViewChanged(Cursor cursor) {
        Log.d(TAG, "The fragment view changed!");
    }

    public void showEmptyView() {
        Log.d(TAG, "The fragment need to show empty view!");
    }

    public void updateToken(String obj) {
        Log.d(TAG, "The fragment need to update token!!!");
    }

    public void setToken(String obj) {
        Log.d(TAG, "The fragment set token!");
    }
}
