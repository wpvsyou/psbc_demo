package com.wp.psbcdemocontroler;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener{

    EditTextWithDelete mUserName, mPassword;
    Button mCreateAccountBtn, mDeleteAccountBtn, mFreezeAccountBtn, mUnFreezeAccountBtn;

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

        mCreateAccountBtn.setOnClickListener(this);
        mDeleteAccountBtn.setOnClickListener(this);
        mFreezeAccountBtn.setOnClickListener(this);
        mUnFreezeAccountBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        String username, password;
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

    }

    protected void deleteAccount(String username, String password) {

    }

    protected void freezeAccount(String username, String password) {

    }

    protected void unfreezeAccount(String username, String password) {

    }

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
}
