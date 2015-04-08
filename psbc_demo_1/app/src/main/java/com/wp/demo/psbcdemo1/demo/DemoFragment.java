package com.wp.demo.psbcdemo1.demo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.wp.androidftpclient.FTPAndroidClientManager;
import com.wp.demo.psbc.count.PSBCCount;
import com.wp.demo.psbcdemo1.psbccase.R;
import com.wp.demo.psbcdemo1.tools.BaseFragment;
import com.wp.demo.psbcdemo1.tools.EditTextWithDelete;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bean.PSBCDataBean;
import bean.PersonalBean;
import bean.Personals;

public class DemoFragment extends BaseFragment implements OnClickListener {

    public interface UserSelectLogin {
        public void onSelectUser(String obj);
    }

    private UserSelectLogin mCallbackLogin;
    private EditTextWithDelete mUsername, mPassword;

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        try {
            mCallbackLogin = (UserSelectLogin) activity;
        } catch (ActivityNotFoundException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        return inflater
                .inflate(R.layout.demo_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);
        mUsername = (EditTextWithDelete) view.findViewById(R.id.username);
        mPassword = (EditTextWithDelete) view.findViewById(R.id.password);
        Button enterBtn = (Button) view.findViewById(R.id.enter);
        Button exitBtn = (Button) view.findViewById(R.id.exit);

        mUsername.setHint(R.string.enter_username);
        mPassword.setHint(R.string.enter_password);
        mPassword.setPassword(true);
        enterBtn.setOnClickListener(this);
        exitBtn.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mUsername.setText(null);
        mPassword.setText(null);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.enter) {
            showProgressDialog(getActivity(), getString(R.string.loading_text), false);
            checkLogin();
        } else if (v.getId() == R.id.exit) {
            getActivity().finish();
        }
    }

    protected void checkLogin() {
        if (null == mUsername.getText()
                || filterTransferCharacter(mUsername.getText())) {
            Toast.makeText(getActivity(), "Please enter user name!",
                    Toast.LENGTH_SHORT).show();
        } else if (null == mPassword.getText()
                || filterTransferCharacter(mPassword.getText())) {
            Toast.makeText(getActivity(), "Please enter password!",
                    Toast.LENGTH_SHORT).show();
        } else {
            FTPAndroidClientManager.getInstance(getActivity()).downloadFile
                    (1, FTPAndroidClientManager.PERSONAL_PATH, new FTPAndroidClientManager.FTPThreadCallback() {
                        @Override
                        public void ftpDownloadCallback(int threadId, PSBCDataBean psbcDataBean) {
                            Log.d(TAG, " DemoFragment, ftpDownloadCallback");
                            if (null != psbcDataBean) {
                                if (null != psbcDataBean.getPersonalsBean()) {
                                    Personals ps = psbcDataBean.getPersonalsBean();
                                    if (null != ps.getList() && ps.getList().size() > 0) {
                                        List<PersonalBean> pl = ps.getList();
                                        for (PersonalBean p : pl) {
                                            Log.d(TAG, "***************************************");
                                            Log.d(TAG, "echo all --> " + p.getUserName() + " | " + p.getPassword());
                                            Log.d(TAG, "echo all --> " + mUsername.getText() + " | " + mPassword.getText());
                                            if (TextUtils.equals(mUsername.getText(), p.getUserName())
                                                    && TextUtils.equals(mPassword.getText(), p.getPassword())) {
                                                dismissProgress();
                                                if (p.getLevel() > PSBCCount.Login.LEVEL_UNFREEZE) {
                                                    mHandler.sendEmptyMessage(MSG_BASE_FREEZE);
                                                } else {
                                                    Log.d(TAG, "Get username and pass1word right! enter!");
                                                    mHandler.sendEmptyMessage(MSG_LOGIN_SUCCESSFUL);
                                                    TokenHelper.getInstance().setToken(p.getId());
                                                    mCallbackLogin.onSelectUser(p.getId());
                                                }
                                                break;
                                            }
                                            dismissProgress();
                                            Log.d(TAG, "username or password error!");
                                            mHandler.sendEmptyMessage(MSG_BASE_ERROR_PASSWORD);
                                        }
                                    } else {
                                        dismissProgress();
                                        Log.d(TAG, "PersonalBean list is empty!");
                                        mHandler.sendEmptyMessage(MSG_BASE_UN_KNOX_USER);
                                    }
                                } else {
                                    dismissProgress();
                                    Log.d(TAG, "personals is empty!");
                                    mHandler.sendEmptyMessage(MSG_BASE_UN_KNOX_USER);
                                }
                            } else {
                                dismissProgress();
                                Log.d(TAG, "psbcDataBean is empty!");
                                mHandler.sendEmptyMessage(MSG_BASE_UN_KNOX_USER);
                            }
                        }

                        @Override
                        public void connectSuccessful() {
                            Log.d(TAG, " DemoFragment, connectSuccessful");
                        }

                        @Override
                        public void ftpUploadCallback() {
                            Log.d(TAG, " DemoFragment, ftpUploadCallback");
                        }

                        @Override
                        public void connectFailed(int errorCode) {
                            Log.d(TAG, " DemoFragment, connectFailed");
                            mHandler.sendEmptyMessage(MSG_TIME_OUE);
                        }
                    });
        }
    }

    private final static int MSG_BASE = 1;
    private final static int MSG_TIME_OUE = MSG_BASE << 1;
    private final static int MSG_BASE_UN_KNOX_USER = MSG_BASE << 2;
    private final static int MSG_BASE_ERROR_PASSWORD = MSG_BASE << 3;
    private final static int MSG_BASE_FREEZE = MSG_BASE << 4;
    private final static int MSG_LOGIN_SUCCESSFUL = MSG_BASE << 5;

    final Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_TIME_OUE:
                    showInfo("Error : Time out, please check your network!");
                    break;
                case MSG_BASE_UN_KNOX_USER:
                    showInfo("Un_know user or user un exits!");
                    break;
                case MSG_BASE_ERROR_PASSWORD:
                    showInfo("Username or password error!");
                    break;
                case MSG_BASE_FREEZE:
                    showInfo("Account be freeze!");
                    break;
                case MSG_LOGIN_SUCCESSFUL:
                    showInfo("Login successful!");
                    break;
            }
        }
    };

    private Toast mToast;
    void showInfo(String text) {
        if (mToast == null) {
            mToast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

//    protected void checkLogin() {
//        if (null == mUsername.getText()
//                || filterTransferCharacter(mUsername.getText())) {
//            Toast.makeText(getActivity(), "Please enter user name!",
//                    Toast.LENGTH_SHORT).show();
//        } else if (null == mPassword.getText()
//                || filterTransferCharacter(mPassword.getText())) {
//            Toast.makeText(getActivity(), "Please enter password!",
//                    Toast.LENGTH_SHORT).show();
//        } else {
//            String selection = Personnel.USER_NAME + "=?";
//            String[] selectionArgs = new String[]{mUsername.getText()};
//            Cursor cursor = getActivity().getContentResolver().query(
//                    Uri.PERSONNEL_URI, null, selection,
//                    selectionArgs, null);
//            if (null != cursor && cursor.moveToFirst()) {
//                try {
//                    String locPassword = cursor
//                            .getString(cursor
//                                    .getColumnIndex(Personnel.PASSWORD));
//                    if (TextUtils.equals(locPassword, mPassword.getText())) {
//                        String token = cursor
//                                .getString(cursor
//                                        .getColumnIndex(Personnel.ID));
//                        if (TextUtils.isEmpty(token)) {
//                            Toast.makeText(
//                                    getActivity(),
//                                    "Error, the token was empty, use un exsit in location!",
//                                    Toast.LENGTH_SHORT).show();
//                        } else {
//                            mCallbackLogin.onSelectUser(token);
//                        }
//                    } else {
//                        Toast.makeText(getActivity(),
//                                "Password error, try again please!",
//                                Toast.LENGTH_SHORT).show();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    cursor.close();
//                }
//            } else {
//                Toast.makeText(getActivity(), "User un exist!",
//                        Toast.LENGTH_SHORT).show();
//            }
//        }
//
//    }

    /**
     * Use to filter transfer character in the string.
     *
     * @param text ;
     */
    public static boolean filterTransferCharacter(String text) {
        String regEx = "[/\\:*?<>|\"'0\t\b\f\r\n' ']";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(text);
        return TextUtils.isEmpty(m.replaceAll(""));
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
