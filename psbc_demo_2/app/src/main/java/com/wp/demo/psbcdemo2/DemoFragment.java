package com.wp.demo.psbcdemo2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.wp.demo.psbcdemo2.tools.BaseFragment;
import com.wp.demo.psbcdemo2.tools.EditTextWithDelete;
import com.wp.demo.psbc.count.PSBCCount.*;

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
            String selection = Personnel.USER_NAME + "=?";
            String[] selectionArgs = new String[]{mUsername.getText()};
            Cursor cursor = getActivity().getContentResolver().query(
                    Uri.PERSONNEL_URI, null, selection, selectionArgs,
                    null);
            if (null != cursor && cursor.moveToFirst()) {
                try {
                    String locPassword = cursor.getString(cursor
                            .getColumnIndex(Personnel.PASSWORD));
                    if (TextUtils.equals(locPassword, mPassword.getText())) {
                        String token = cursor.getString(cursor
                                .getColumnIndex(Personnel.ID));
                        if (TextUtils.isEmpty(token)) {
                            Toast.makeText(
                                    getActivity(),
                                    "Error, the token was empty, use un exist in location!",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, "The token is " + token);
                            mCallbackLogin.onSelectUser(token);
                        }
                    } else {
                        Toast.makeText(getActivity(),
                                "Password error, try again please!",
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    cursor.close();
                }
            } else {
                Toast.makeText(getActivity(), "User un exist!",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

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
}
