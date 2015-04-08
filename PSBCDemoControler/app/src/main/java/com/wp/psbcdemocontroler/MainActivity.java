package com.wp.psbcdemocontroler;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.wp.demo.psbc.count.PSBCCount;

public class MainActivity extends Activity implements View.OnClickListener{

    EditTextWithDelete mUserName, mPassword;
    Button mCreateAccountBtn, mDeleteAccountBtn, mFreezeAccountBtn, mUnFreezeAccountBtn,
    mLockClientBtn, mUnlockClientBtn;
    PsbcDemoContorler mControler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUserName = (EditTextWithDelete) findViewById(R.id.user_name);
        mPassword = (EditTextWithDelete) findViewById(R.id.pass_word);
        mCreateAccountBtn = (Button) findViewById(R.id.create_btn);
        mDeleteAccountBtn = (Button) findViewById(R.id.delete_btn);
        mFreezeAccountBtn = (Button) findViewById(R.id.freeze_btn);
        mUnFreezeAccountBtn = (Button) findViewById(R.id.unfreeze_btn);
        mLockClientBtn = (Button) findViewById(R.id.lock_btn);
        mUnlockClientBtn = (Button) findViewById(R.id.unlock_btn);
        mControler = PsbcDemoContorler.getInstance(this);

        mCreateAccountBtn.setOnClickListener(this);
        mDeleteAccountBtn.setOnClickListener(this);
        mFreezeAccountBtn.setOnClickListener(this);
        mUnFreezeAccountBtn.setOnClickListener(this);
        mLockClientBtn.setOnClickListener(this);
        mUnlockClientBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        showProgressDialog(MainActivity.this, getString(R.string.loading_text), false);
        String username, password;
        if (id == R.id.lock_btn) {
            PsbcDemoContorler.getInstance(MainActivity.this).lockDemoClient();
            dismissProgress();
            return;
        } else if (id == R.id.unlock_btn) {
            PsbcDemoContorler.getInstance(MainActivity.this).unlockDemoClient();
            dismissProgress();
            return;
        }

        if (null != mUserName.getText()) {
            username = mUserName.getText();
        } else {
            showInfo("Please enter user name !");
            return;
        }

        if (null != mPassword.getText()) {
            password = mPassword.getText();
        } else {
            showInfo("Please enter password !");
            return;
        }
        switch (id) {
            case R.id.create_btn:
                createAccount(username, password);
                break;
            case R.id.delete_btn:
                deleteAccount(username, password);
                break;
            case R.id.freeze_btn:
                freezeAccount(username, password);
                break;
            case R.id.unfreeze_btn:
                unfreezeAccount(username, password);
                break;
        }
    }

    protected void createAccount(String username, String password) {
        mControler.createAccount(username, password, new PsbcDemoContorler.ContorlerCreateCallback() {
            @Override
            public void onCreateAccount(boolean successful, int errorCode) {
                Message msg = new Message();
                if (successful) {
                    msg.obj = "Create account was successful! " + errorCode;
                } else msg.obj = "Create account was failed! " + errorCode;
                mHandler.sendMessage(msg);
            }
        });
    }

    protected void deleteAccount(String username, String password) {
        mControler.deleteAccount(username, password, new PsbcDemoContorler.ContorlerDeleteCallback() {
            @Override
            public void onDeleteAccount(boolean successful, int errorCode) {
                Message msg = new Message();
                if (successful) {
                    msg.obj = "Delete account was successful! " + errorCode;
                } else msg.obj = "Delete account was failed!" + errorCode;
                mHandler.sendMessage(msg);
            }
        });
    }

    protected void freezeAccount(String username, String password) {
        mControler.freezeAccount(username, password, new PsbcDemoContorler.ContorlerFreezeCallback() {
            @Override
            public void onFreezeAccount(boolean successful, int errorCode) {
                Message msg = new Message();
                if (successful) {
                    msg.obj = "Freeze account was successful! " + errorCode;
                } else msg.obj = "Unfreeze account was failed!" + errorCode;
                mHandler.sendMessage(msg);
            }
        });
    }

    protected void unfreezeAccount(String username, String password) {
        mControler.unfreezeAccount(username, password, new PsbcDemoContorler.ContorlerUnfreezeCallback() {
            @Override
            public void onUnFreezeAccount(boolean successful, int errorCode) {
                Message msg = new Message();
                if (successful) {
                    msg.obj = "Unfreeze account was successful! " + errorCode;
                } else msg.obj = "Unfreeze account was failed! " + errorCode;
                mHandler.sendMessage(msg);
            }
        });
    }

    final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dismissProgress();
            String msgObj = (String)msg.obj;
            showInfo(msgObj);
        }
    };

    private Toast mToast;

    protected void showInfo(String text) {
        if (mToast == null) {
            mToast = Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    private static ProgressDialog mProgress;

    public static void showProgressDialog(Context ctx, String msg, boolean cancelable) {
        if (mProgress != null) {
            if (mProgress.isShowing()) {
                mProgress.setMessage(msg);
                return;
            }
            mProgress = null;
        }
        mProgress = new ProgressDialog(ctx, R.style.LoadingDialogStyle);
        mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgress.setMessage(msg);
        mProgress.setCancelable(cancelable);
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();
    }

    public static void dismissProgress() {
        if (mProgress != null && mProgress.isShowing()) {
            try {
                mProgress.dismiss();
            } catch (Exception e) {
            } finally {
                mProgress = null;
            }
        }
    }
}
