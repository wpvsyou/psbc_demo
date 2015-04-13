package com.wp.demo.psbcdemo1.tools;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by wangpeng on 15-4-13.
 */
public class Configuration {

    private static Configuration INSTANCE;
    private static SharedPreferences mPreferences;
    private static SharedPreferences.Editor mEditor;
    private Context context;

    private Configuration(Context context) {
        this.context = context;
        mPreferences = context.getSharedPreferences("psbc_demo_1", Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
    }

    public static Configuration getInstance(Context context) {
        if (null == INSTANCE) {
            INSTANCE = new Configuration(context);
        }
        return INSTANCE;
    }

    private final static String FTP_CLIENT_URL = "ftp_client_url";
    private final static String SU_USERNAME = "su_username";
    private final static String SU_PASSWORD = "su_password";

    public void setFtpClientUrl(String url) {
        mEditor.putString(FTP_CLIENT_URL, url).commit();
    }

    public String getFtpClientUrl() {
        return mPreferences.getString(FTP_CLIENT_URL, "192.168.10.210");
    }

    public void setSuUsername(String username) {
        mEditor.putString(SU_USERNAME, username).commit();
    }

    public String getSuUsername() {
        return mPreferences.getString(SU_USERNAME, "pekall");
    }

    public void setSuPassword(String password) {
        mEditor.putString(SU_PASSWORD, password).commit();
    }

    public String getSuPassword() {
        return mPreferences.getString(SU_PASSWORD, "pekall");
    }
}
