package com.wp.demo.psbcdemo1.demo;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.wp.demo.psbc.count.PSBCCount.Personnel;
import com.wp.demo.psbc.count.PSBCCount.Uri;
import com.wp.demo.psbcdemo1.bean.PSBCDataBean;
import com.wp.demo.psbcdemo1.bean.PersonalBean;
import com.wp.demo.psbcdemo1.bean.Personals;
import com.wp.demo.psbcdemo1.psbccase.R;
import com.wp.demo.psbcdemo1.tools.BaseFragment;
import com.wp.demo.psbcdemo1.tools.BaseFragmentActivity;
import com.wp.demo.psbcdemo1.tools.FTPAndroidClientManager;

import java.util.ArrayList;
import java.util.List;

public class DemoActivity extends BaseFragmentActivity implements
        DemoFragment.UserSelectLogin {

    private final static String TAG = "DemoActivity + FTPService";
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
            }

            @Override
            public void connectSuccessful() {
                Log.d(TAG, "connectSuccessful");
                Toast.makeText(DemoActivity.this, "ftp service connect successful!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void ftpUploadCallback() {
                Log.d(TAG, "ftpUploadCallback");
                Toast.makeText(DemoActivity.this, "create successful!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void connectFailed(int errorCode) {
                Log.d(TAG, "connectFailed");
                Toast.makeText(DemoActivity.this, "ftp service connect failed!", Toast.LENGTH_SHORT).show();
            }
        }, psbcDataBean);
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

    @Override
    public void onBackHomeOnClick() {
        super.onBackHomeOnClick();
        this.finish();
    }
}
