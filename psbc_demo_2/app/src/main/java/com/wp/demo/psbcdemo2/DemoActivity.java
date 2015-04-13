package com.wp.demo.psbcdemo2;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wp.demo.psbc.count.PSBCCount;
import com.wp.demo.psbc.count.PSBCCount.Company_data;
import com.wp.demo.psbcdemo2.tools.BaseFragment;
import com.wp.demo.psbcdemo2.tools.BaseFragmentActivity;
import com.wp.demo.psbcdemo2.tools.EditTextWithDelete;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class DemoActivity extends BaseFragmentActivity implements
        DemoFragment.UserSelectLogin, View.OnClickListener {

    public final static String ACTION_TO_LOGOUT = "com.wp.psbc.demo2.ACTION_TO_LOGOUT";
    public final static String ACTION_TO_LUCK = "com.wp.psbc,demo2.ACTION_TO_LUCK";
    public final static String LOCK_CLIENT = "lock_client";

    public static final String TAG = "PSBC_case_demo_debug";
    public final static String KEY_TOKEN = "key_token";
    public final static String HAS_LOGIN = "has_login";
    public final static String PUBLIC_KEY = "publickey";

    private final static int MSG_BASE = 1;
    private final static int MSG_SHOW_FOCUS_BUTTON = MSG_BASE << 1;
    private final static int MSG_SHOW_UN_FOCUS = MSG_BASE << 2;
    private final static int COMMAND_LOCK = MSG_BASE << 3;

    private final static int COMMAND_UN_LOCK = MSG_BASE << 4;
    LocDataListFragment mLocDataListFragment;
    static String OBJ;
    Button mCameraButton;
    Dialog mDialog;
    CommandLockObserver mCommandLockObserver;
    CommandUnLockObserver mCommandUnlockObserver;
    static SharedPreferences mPreferences;
    static SharedPreferences.Editor mEditor;

    private final static int LOCK = 1;
    private final static int UNLOCK = 0;

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_TO_LOGOUT)) {
                mEditor.putBoolean(HAS_LOGIN, false).commit();
                DemoFragment fragment = new DemoFragment();
                getSupportFragmentManager().popBackStack();
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in,
                                android.R.anim.fade_out);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment).commit();
                setKeyToken("");
            }
        }
    };

    class CommandLockObserver extends ContentObserver {

        Handler mHandler;
        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public CommandLockObserver(Handler handler) {
            super(handler);
            mHandler = handler;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            mHandler.sendEmptyMessage(COMMAND_LOCK);
        }
    }

    class CommandUnLockObserver extends ContentObserver {

        Handler mHandler;
        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public CommandUnLockObserver(Handler handler) {
            super(handler);
            mHandler = handler;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            mHandler.sendEmptyMessage(COMMAND_UN_LOCK);
        }
    }

    final Handler mCommandHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == COMMAND_LOCK) {
                mEditor.putInt(LOCK_CLIENT, LOCK).commit();
                Toast.makeText(DemoActivity.this, "Sorry , your client was locked!!! ", Toast.LENGTH_LONG).show();
                DemoActivity.this.finish();
            } else if (msg.what == COMMAND_UN_LOCK) {
                mEditor.putInt(LOCK_CLIENT, UNLOCK).commit();
            }
        }
    };

    final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SHOW_FOCUS_BUTTON:
                    mCameraButton.setBackgroundResource(R.drawable.camera_on_focus);
                    break;
                case MSG_SHOW_UN_FOCUS:
                    mCameraButton.setBackgroundResource(R.drawable.camera_off_focus);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_activity_layout);
        mCameraButton = (Button) findViewById(R.id.camera);
        mCameraButton.setOnClickListener(this);
        if (null == savedInstanceState) {
            DemoFragment fragment = new DemoFragment();
            getSupportFragmentManager().popBackStack();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment).commit();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_TO_LOGOUT);
        filter.addAction(ACTION_TO_LUCK);
        registerReceiver(mReceiver, filter);
        mCommandLockObserver = new CommandLockObserver(mCommandHandler);
        getContentResolver().registerContentObserver(PSBCCount.Uri.COMMAND_LOCK, true, mCommandLockObserver);
        mCommandUnlockObserver = new CommandUnLockObserver(mCommandHandler);
        getContentResolver().registerContentObserver(PSBCCount.Uri.COMMAND_UN_LOCK, true, mCommandUnlockObserver);
        mPreferences = getSharedPreferences("psbc_demo", Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (mPreferences.getInt(LOCK_CLIENT, -1) > 0) {
            Toast.makeText(this, "Sorry , your client was locked!!! ", Toast.LENGTH_LONG).show();
            this.finish();
        } else {
            if (mPreferences.getBoolean(HAS_LOGIN, false)) {
                if (null == mLocDataListFragment) {
                    mLocDataListFragment = new LocDataListFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(KEY_TOKEN, OBJ);
                    mLocDataListFragment.setArguments(bundle);
                } else mLocDataListFragment.updateToken(OBJ);
                getSupportFragmentManager().popBackStack();
                getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        android.R.anim.fade_in, android.R.anim.fade_out);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, mLocDataListFragment)
                        .commit();
            }
            if (mPreferences.getBoolean(HAS_LOGIN, false)) {
                mCameraButton.setVisibility(View.VISIBLE);
            }
            showButton();
        }
    }

    void showButton() {
        mHandler.sendEmptyMessage(MSG_SHOW_FOCUS_BUTTON);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(MSG_SHOW_UN_FOCUS);
            }
        }, 2000);
    }

    @Override
    public void onSelectUser(String obj) {
        // TODO Auto-generated method stub
        mEditor.putBoolean(HAS_LOGIN, true).commit();
        Log.d(TAG, "Check the token in the DemoActivity callback method! [" + obj + "]");
        OBJ = obj;
        Log.d(TAG, "The mLocDataListFragment is empty!");
        mLocDataListFragment = new LocDataListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TOKEN, OBJ);
        mLocDataListFragment.setArguments(bundle);
        getSupportFragmentManager().popBackStack();
        getSupportFragmentManager().beginTransaction().setCustomAnimations(
                android.R.anim.fade_in, android.R.anim.fade_out);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mLocDataListFragment)
                .commit();
        mLocDataListFragment.updateToken(OBJ);
        mLocDataListFragment.updateView();
        mCameraButton.setVisibility(View.VISIBLE);
        showButton();
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
        BaseFragment demoFragment = (BaseFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
        if (id == R.id.action_settings) {
            if (null != demoFragment && !(demoFragment instanceof DemoFragment)) {
                mEditor.putBoolean(HAS_LOGIN, false).commit();
                DemoFragment fragment = new DemoFragment();
                getSupportFragmentManager().popBackStack();
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in,
                                android.R.anim.fade_out);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment).commit();
            } else {
                finish();
            }
            setKeyToken(PUBLIC_KEY);
        } else if (id == R.id.create_data) {
            openCamera();
        } else if (id == R.id.setting_action_url) {
            if (null != demoFragment && !(demoFragment instanceof DemoFragment)) {
                Toast.makeText(DemoActivity.this, "Pleas logout first!", Toast.LENGTH_SHORT).show();
            } else {
                showSettingDialog();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    protected void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) bundle.get("data");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日   HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String str = formatter.format(curDate);
            String information = "PSBC test demo app , create a new picture when " + str;
            String title = "This is a test picture!";
            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            Bitmap thumbnail = getBitmapByWidth(os.toByteArray(), 0, os.toByteArray().length, 260, 10);
            final ByteArrayOutputStream thumbnailOs = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.PNG, 100, thumbnailOs);
            ContentValues values = new ContentValues();
            values.put(Company_data.ID, DemoActivity.getKeyToken());
            values.put(Company_data.DATA_TITLE, title);
            values.put(Company_data.DATA_INFORMATION, information);
            values.put(Company_data.DATA_IMAGE, os.toByteArray());
            values.put(Company_data.DATA_THUMBNAIL, thumbnailOs.toByteArray());
            if (!TextUtils.equals(getKeyToken(), PUBLIC_KEY)) {
                try {
                    if (getContentResolver().insert(PSBCCount.Uri.COMPANY_DATA_URI, values) != null) {
                        Log.d(TAG, "Insert image was done!");
                    } else {
                        Log.d(TAG, "Error insert image!!!");
                    }
                } catch (Exception e) {
                    Log.d(TAG, "Error  :  " + e);
                }
            }
            if (getContentResolver().insert(PSBCCount.Uri_local.LOCAL_DATA_URI, values) != null) {
                Log.d(TAG, "Insert image in local data base was done!");
            } else {
                Log.d(TAG, "Error insert in local data base image!!!");
            }
        }
    }

    public static Bitmap getBitmapByWidth(byte[] in, int offset, int length, int width, int addedScaling) {
        Bitmap temBitmap = null;
        try {
            BitmapFactory.Options outOptions = new BitmapFactory.Options();
            // 设置该属性为true，不加载图片到内存，只返回图片的宽高到options中。
            outOptions.inJustDecodeBounds = true;
            // 加载获取图片的宽高
            BitmapFactory.decodeByteArray(in, offset, length, outOptions);
            int height = outOptions.outHeight;
            if (outOptions.outWidth > width) {
                // 根据宽设置缩放比例
                outOptions.inSampleSize = outOptions.outWidth / width + 1 + addedScaling;
                outOptions.outWidth = width;
                // 计算缩放后的高度
                height = outOptions.outHeight / outOptions.inSampleSize;
                outOptions.outHeight = height;
            }
            // 重新设置该属性为false，加载图片返回
            outOptions.inJustDecodeBounds = false;
            outOptions.inPurgeable = true;
            outOptions.inInputShareable = true;
            temBitmap = BitmapFactory.decodeByteArray(in, offset, length, outOptions);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return temBitmap;
    }

    @Override
    public void onBackHomeOnClick() {
        super.onBackHomeOnClick();
        this.finish();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.camera) {
            mHandler.sendEmptyMessage(MSG_SHOW_FOCUS_BUTTON);
            Log.d(TAG, "openCamera");
            openCamera();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    private final static String FTP_CLIENT_URL = "ftp_client_url";
    private final static String SU_USERNAME = "su_username";
    private final static String SU_PASSWORD = "su_password";

    public void setFtpClientUrl(String url) {
        mEditor.putString(FTP_CLIENT_URL, url).commit();
    }

    public static String getFtpClientUrl() {
        return mPreferences.getString(FTP_CLIENT_URL, "192.168.10.210");
    }

    public void setSuUsername(String username) {
        mEditor.putString(SU_USERNAME, username).commit();
    }

    public static String getSuUsername() {
        return mPreferences.getString(SU_USERNAME, "pekall");
    }

    public void setSuPassword(String password) {
        mEditor.putString(SU_PASSWORD, password).commit();
    }

    public static String getSuPassword() {
        return mPreferences.getString(SU_PASSWORD, "pekall");
    }

    public static void setKeyToken(String token) {
        mEditor.putString(KEY_TOKEN, token).commit();
    }

    public static String getKeyToken() {
        return mPreferences.getString(KEY_TOKEN, PUBLIC_KEY);
    }

    protected void showSettingDialog() {
        if (null == mDialog) {
            mDialog = new Dialog(this, R.style.exit_dialog_style);
            mDialog.setContentView(R.layout.exit_dialog_layout);
            TextView tv = (TextView) mDialog.findViewById(R.id.exit_dialog_content);
            tv.setVisibility(View.GONE);
            TextView tip = (TextView) mDialog.findViewById(R.id.username_tip);
            tip.setText("Current setting : url[" + getFtpClientUrl() + "] username[" + getSuUsername() + "]");
            LinearLayout linearLayout = (LinearLayout) mDialog.findViewById(R.id.unlock_layout);
            linearLayout.setVisibility(View.VISIBLE);
            final EditTextWithDelete url = (EditTextWithDelete) mDialog.findViewById(R.id.url);
            url.setHint("Please set new URL");
            final EditTextWithDelete username = (EditTextWithDelete) mDialog.findViewById(R.id.username_edit);
            username.setHint("Please set username");
            final EditTextWithDelete password = (EditTextWithDelete) mDialog.findViewById(R.id.password_edit);
            password.setHint("Please set password");
            password.setPassword(true);
            Button okBtn = (Button) mDialog.findViewById(R.id.exit_dialog_ok_button);
            okBtn.setText("Ok");
            okBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(username.getText()) && !TextUtils.isEmpty(password.getText())
                            && !TextUtils.isEmpty(url.getText())) {
                        Log.d(TAG, "reset password successful!");
                        setFtpClientUrl(url.getText());
                        setSuUsername(username.getText());
                        setSuPassword(password.getText());
                        Toast.makeText(DemoActivity.this, "Setting FTP client successful!", Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();
                    } else {
                        username.setText("");
                        password.setText("");
                        url.setText("");
                        Toast.makeText(DemoActivity.this, "Wrong : url/user/pwd all un empty!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            Button cancelBtn = (Button) mDialog.findViewById(R.id.exit_dialog_cancel_button);
            cancelBtn.setText("Cancel");
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                }
            });
        }
        setWindows(mDialog);
        mDialog.show();
    }

    protected void setWindows(Dialog dialog) {
        Display d = getWindowManager().getDefaultDisplay();
        Window window = dialog.getWindow();
        WindowManager.LayoutParams p = window.getAttributes();
        p.width = (int) (d.getWidth() * 0.7f);
        window.setAttributes(p);
    }
}
