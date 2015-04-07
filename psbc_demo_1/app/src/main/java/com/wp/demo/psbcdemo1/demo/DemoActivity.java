package com.wp.demo.psbcdemo1.demo;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.wp.androidftpclient.FTPAndroidClientManager;
import com.wp.demo.psbc.count.PSBCCount.Personnel;
import com.wp.demo.psbc.count.PSBCCount.Uri;
import com.wp.demo.psbcdemo1.psbccase.R;
import com.wp.demo.psbcdemo1.tools.BaseFragment;
import com.wp.demo.psbcdemo1.tools.BaseFragmentActivity;

import java.util.ArrayList;
import java.util.List;

import bean.PSBCDataBean;
import bean.PersonalBean;
import bean.Personals;

public class DemoActivity extends BaseFragmentActivity implements
        DemoFragment.UserSelectLogin {

    public interface HideRefreshBtn {
        void hideBth();
        void showBtn();
    }

    HideRefreshBtn mHideRefreshBtnCallback = new HideRefreshBtn() {
        @Override
        public void hideBth() {
            mHandler.sendEmptyMessage(MSG_HIDE_BTN);
        }

        @Override
        public void showBtn() {
            fragmentType = 2;
            mHandler.sendEmptyMessage(MSG_SHOW_BTN);
        }
    };

    private final static String TAG = "DemoActivity + FTPService";
    public final static String KEY_TOKEN = "key_token";
    ShowUserDataFragment mShowFragment;
    static boolean HAS_LOGIN;
    static String OBJ;
    Toast mToast;

    protected MenuItem refreshItem;

    @SuppressLint("NewApi")
    private void showRefreshAnimation(MenuItem item) {
        hideRefreshAnimation();
        refreshItem = item;
        //这里使用一个ImageView设置成MenuItem的ActionView，这样我们就可以使用这个ImageView显示旋转动画了
        ImageView refreshActionView = (ImageView) getLayoutInflater().inflate(R.layout.action_view, null);
        refreshActionView.setImageResource(R.drawable.ic_action_refresh);
        refreshItem.setActionView(refreshActionView);
        //显示刷新动画
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.refresh);
        animation.setRepeatMode(Animation.RESTART);
        animation.setRepeatCount(Animation.INFINITE);
        refreshActionView.startAnimation(animation);
    }

    @SuppressLint("NewApi")
    public void hideRefreshAnimation() {
        if (refreshItem != null) {
            View view = refreshItem.getActionView();
            if (view != null) {
                view.clearAnimation();
                refreshItem.setActionView(null);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_activity_layout);
        BaseFragment.mCallback = mHideRefreshBtnCallback;
        if (null == savedInstanceState) {
            DemoFragment fragment = new DemoFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment).commit();
        }
        ContentValues values = new ContentValues();
        values.put(Personnel.USER_NAME, "pekall");
        values.put(Personnel.PASSWORD, "pekall");
        values.put(Personnel.ID, "pekall");
        getContentResolver().insert(Uri.PERSONNEL_URI, values);

        ContentValues values1 = new ContentValues();
        values1.put(Personnel.USER_NAME, "wangpeng");
        values1.put(Personnel.PASSWORD, "wangpeng");
        values1.put(Personnel.ID, "wangpeng");
        getContentResolver().insert(Uri.PERSONNEL_URI, values1);

        PSBCDataBean psbcDataBean = new PSBCDataBean();
        PersonalBean personalBean = new PersonalBean();
        personalBean.setUserName("pekall");
        personalBean.setPassword("pekall");
        personalBean.setId("pekall");
        List<PersonalBean> personalBeanList = new ArrayList<>();
        personalBeanList.add(personalBean);
        Personals personals = new Personals();
        personals.setList(personalBeanList);
        psbcDataBean.setPersonalBean(personals);

        startService(new Intent("com.wp.demo.psbcdemo1.ACTION_BIND_FTP_SERVICES"));

        FTPAndroidClientManager.getInstance(this).updateFile(1, FTPAndroidClientManager.PERSONAL_PATH, new FTPAndroidClientManager.FTPThreadCallback() {
            @Override
            public void ftpDownloadCallback(int threadId, PSBCDataBean psbcDataBean) {
                Log.d(TAG, "ftpDownloadCallback");
                mHandler.sendEmptyMessage(MSG_DOWNLOAD_CALLBACK);
            }

            @Override
            public void connectSuccessful() {
                Log.d(TAG, "connectSuccessful");
                mHandler.sendEmptyMessage(MSG_CONNECT_SUCCESSFUL);
            }

            @Override
            public void ftpUploadCallback() {
                Log.d(TAG, "ftpUploadCallback");
                mHandler.sendEmptyMessage(MSG_UPLOAD_CALLBACK);
            }

            @Override
            public void connectFailed(int errorCode) {
                Log.d(TAG, "connectFailed");
                mHandler.sendEmptyMessage(MSG_CONNECT_FAILED);
            }
        }, psbcDataBean);
    }

    private final static int MSG_BASE = 1;
    private final static int MSG_DOWNLOAD_CALLBACK = MSG_BASE << 1;
    private final static int MSG_CONNECT_SUCCESSFUL = MSG_BASE << 2;
    private final static int MSG_UPLOAD_CALLBACK = MSG_BASE << 3;
    private final static int MSG_CONNECT_FAILED = MSG_BASE << 4;
    private final static int MSG_HIDE_BTN = MSG_BASE << 5;
    private final static int MSG_SHOW_BTN = MSG_BASE << 6;

    final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_DOWNLOAD_CALLBACK:
                    showInfo("ftpDownloadCallback");
                    break;
                case MSG_CONNECT_SUCCESSFUL:
                    showInfo("ftp service connect successful!");
                    break;
                case MSG_UPLOAD_CALLBACK:
                    showInfo("create successful!");
                    break;
                case MSG_CONNECT_FAILED:
                    showInfo("ftp service connect failed!");
                    break;
                case MSG_HIDE_BTN:
                    hideRefreshAnimation();
                    break;
                case MSG_SHOW_BTN:
                    invalidateOptionsMenu();
                    break;
            }
        }
    };

    void showInfo(String text) {
        if (mToast == null) {
            mToast = Toast.makeText(DemoActivity.this, text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
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
            getSupportFragmentManager().popBackStack();
            getSupportFragmentManager().beginTransaction().setCustomAnimations(
                    android.R.anim.fade_in, android.R.anim.fade_out);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, mShowFragment).commit();
        }
    }

    static int BASE_FRAGMENT_TYPE = 1;
    static int TYPE_SHOW_FRAGMENT = BASE_FRAGMENT_TYPE << 1;
    int fragmentType = BASE_FRAGMENT_TYPE;

    @Override
    public void onSelectUser(String obj) {
        // TODO Auto-generated method stub
        TokenHelper.getInstance().setToken(obj);
        HAS_LOGIN = true;
        OBJ = obj;
        mShowFragment = new ShowUserDataFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TOKEN, OBJ);
        mShowFragment.setArguments(bundle);
        mShowFragment.setToken(OBJ);
        getSupportFragmentManager().popBackStack();
        getSupportFragmentManager().beginTransaction().setCustomAnimations(
                android.R.anim.fade_in, android.R.anim.fade_out);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mShowFragment).commit();
        mShowFragment.onUserChanged(OBJ, getContentResolver());
        fragmentType = TYPE_SHOW_FRAGMENT;
        invalidateOptionsMenu();
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
                fragmentType = BASE_FRAGMENT_TYPE;
            } else {
                finish();
            }
        } else if (id == R.id.refresh) {
            showRefreshAnimation(item);
            sendBroadcast(new Intent(RemoteDataFragment.ACTION_REFRESH_DATA));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d(TAG, "onPrepareOptionsMenu " + fragmentType);
        if (fragmentType != BASE_FRAGMENT_TYPE) {
            menu.findItem(R.id.refresh).setVisible(true);
        } else menu.findItem(R.id.refresh).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackHomeOnClick() {
        super.onBackHomeOnClick();
        this.finish();
    }
}
