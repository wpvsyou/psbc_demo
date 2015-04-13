package com.wp.androidftpclient;

import android.content.Context;

import bean.PSBCDataBean;


/**
 * Created by wangpeng on 15-4-3.
 */
public class FTPAndroidClientManager {

    public interface FTPThreadCallback {
        void ftpDownloadCallback(int threadId, PSBCDataBean psbcDataBean);

        void connectSuccessful();

        void ftpUploadCallback();

        void connectFailed(int errorCode);
    }

    public interface SettingFtpClient {
        String getUrl();
        String getUsername();
        String getPassword();
    }

    public final static String PERSONAL_PATH = "/storage/psbc_demo/personal";
    public final static String COMPANY_DATA_PATH = "/storage/psbc_demo";

    private static FTPAndroidClientManager INSTANCE;
    Context mContext;
    FTPService mService;

    private FTPAndroidClientManager(Context context) {
        mContext = context;
        mService = FTPService.getInstance();
    }

    public static FTPAndroidClientManager getInstance(Context context) {
        if (null == INSTANCE) {
            INSTANCE = new FTPAndroidClientManager(context);
        }
        return INSTANCE;
    }

    public void downloadFile(int ftpThreadId, String filePath, FTPAndroidClientManager.FTPThreadCallback callback, SettingFtpClient settingFtpClient) {
        mService.startDownloadFile(ftpThreadId, filePath, callback, settingFtpClient);
    }

    public void updateFile(int ftpThreadId, String filePath, FTPAndroidClientManager.FTPThreadCallback callback, PSBCDataBean psbcDataBean, SettingFtpClient settingFtpClient) {
        mService.startUploadFile(ftpThreadId, filePath, callback, psbcDataBean, settingFtpClient);
    }

}
