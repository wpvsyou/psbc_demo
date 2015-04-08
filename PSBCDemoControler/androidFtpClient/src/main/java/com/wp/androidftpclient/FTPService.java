package com.wp.androidftpclient;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import bean.CompanyDataBean;
import bean.PSBCDataBean;

public class FTPService extends Service {
    private final static String TAG = "FTPService";
    private static FTPService INSTANCE;
    FTPClient mFTPClient = new FTPClient();
    List<CompanyDataBean> mList = new ArrayList<>();

    public FTPService() {
    }

    public static FTPService getInstance() {
        if (null == INSTANCE) {
            INSTANCE = new FTPService();
        }
        return INSTANCE;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
    }

    public static boolean RUNNING;

    abstract class FTPThread extends Thread {
        protected int THREAD_ID = 0;
        protected FTPAndroidClientManager.FTPThreadCallback mCallback;
        protected PSBCDataBean mPSBCDataBean = new PSBCDataBean();
        private String mFilePath;

        public FTPThread(int threadId, FTPAndroidClientManager.FTPThreadCallback callback, String filePath) {
            THREAD_ID = threadId;
            mCallback = callback;
            mFilePath = filePath;
        }

        @Override
        public void run() {
            super.run();
            RUNNING = true;
            while (RUNNING) {
                mFTPClient.setConnectTimeout(30 * 1000);
                try {
//                    if (!mFTPClient.isConnected()) {
                    if (mFTPClient.isConnected()) {
                        mFTPClient.disconnect();
                    }
                    mFTPClient.connect("192.168.10.210");
                    int replyCode = mFTPClient.getReplyCode();
                    Log.d(TAG, "check the reply code [" + replyCode + "]");
                    if (FTPReply.isPositiveCompletion(replyCode)) {
                        Log.d(TAG, "now begin login! connect successful!");
                        mFTPClient.login("pekall", "pekall");
                        notifyCallback(this.mCallback, MSG_CONNECT_SUCCESSFUL, 0, null, this.THREAD_ID);
                    } else {
                        Log.d(TAG, "connect failed!");
                        notifyCallback(this.mCallback, MSG_CONNECT_FAILED, replyCode, null, this.THREAD_ID);
                    }
//                    } else Log.d(TAG, "ftp client was connect!");
                } catch (IOException e) {
                    Log.d(TAG, "connect error : " + e);
                    e.printStackTrace();
                    notifyCallback(this.mCallback, MSG_CONNECT_FAILED, -1, null, this.THREAD_ID);
                    mCallback.connectFailed(-1);
                }
                ftpThreadRun(mFilePath);
            }
        }

        public void shortDown() {
            RUNNING = false;
        }

        public abstract void ftpThreadRun(String filePath);
    }

    class DownloadThread extends FTPThread {
        public DownloadThread(int threadId, FTPAndroidClientManager.FTPThreadCallback callback, String filePath) {
            super(threadId, callback, filePath);
        }

        @Override
        public void ftpThreadRun(String filePath) {
            Log.d(TAG, "connect successful, start download file!");
            String json;
            try {
                json = inputStream2String(mFTPClient.retrieveFileStream(FTPAndroidClientManager.PERSONAL_PATH));
                if (!TextUtils.isEmpty(json)) {
                    mPSBCDataBean = new Gson().fromJson(json, PSBCDataBean.class);
                } else Log.d(TAG, "The download thread, json was empty!");
                Log.d(TAG, "get input stream from ftp was done!");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "get input stream from ftp was failed : " + e);
            }
            notifyCallback(this.mCallback, MSG_DOWNLOAD_SUCCESSFUL, 0, this.mPSBCDataBean, THREAD_ID);
            this.shortDown();
        }
    }

    class UploadThread extends FTPThread {
        PSBCDataBean psbcDataBean;

        public UploadThread(int threadId, FTPAndroidClientManager.FTPThreadCallback callback, String filePath) {
            super(threadId, callback, filePath);
        }

        public void setPSBCDataBean(PSBCDataBean psbcDataBean) {
            this.psbcDataBean = psbcDataBean;
        }

        @Override
        public void ftpThreadRun(String filePath) {
            Log.d(TAG, "connect successful, start upload file!");
            if (null != psbcDataBean) {
                String json = new Gson().toJson(psbcDataBean);
                InputStream in = new ByteArrayInputStream(json.getBytes());
                Log.d(TAG, "Check the input stream " + in.toString());
                try {
                    Log.d(TAG, "Check the up load file states ["
                            + mFTPClient.storeFile(filePath, in) + "]");
                    notifyCallback(this.mCallback, MSG_UPLOAD_SUCCESSFUL, 0, null, 0);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "Update thread was short down by a wrong! " + e);
                    this.shortDown();
                }
            } else {
                throw new NullPointerException("The upload data was empty!");
            }
            this.shortDown();
        }
    }

    SyncRemoteDataThread mSyncThread;
    SyncThreadStatus mStatus = SyncThreadStatus.FREED;

    enum SyncThreadStatus {
        RUNNING, FREED;

        @Override
        public String toString() {
            if (this == RUNNING) {
                return "The sync data threat is running!!!";
            } else return "The sync data thread was freed!";
        }
    }

    class SyncRemoteDataThread extends FTPThread {
        String token; //Use to find whose data ?
        FTPAndroidClientManager.SyncRemoteDataCallback mSyncCallback;

        public void setToken(String token) {
            this.token = token;
        }

        public void setSyncCallback(FTPAndroidClientManager.SyncRemoteDataCallback syncCallback) {
            mSyncCallback = syncCallback;
        }

        public SyncRemoteDataThread(int threadId, FTPAndroidClientManager.FTPThreadCallback callback, String filePath) {
            super(threadId, callback, filePath);
        }

        @Override
        public void ftpThreadRun(String filePath) {
            mStatus = SyncThreadStatus.RUNNING;
            List<CompanyDataBean> list = new ArrayList<>();
            Log.d(TAG, "sync remote thread was running...");
            try {
                Log.d(TAG, "check the filePath --> " + filePath);
                FTPFile[] files = mFTPClient.listFiles(filePath);
                Log.d(TAG, "check the files is empty ? " + (files == null ? "yse" : "no"));
                if (null != files) {
                    Log.d(TAG, "check the files size " + files.length);
                }
                Log.d(TAG, "get the files from ftp by token [" + token + "]");
                if (null != files && files.length > 0) {
                    int tokenLength = token.length();
                    for (FTPFile f : files) {
                        Log.d(TAG, "-->    " + f.getName());
                        if (f.getName().length() < tokenLength) {
                            continue;
                        }
                        String tokenInFTP = f.getName().substring(0, tokenLength);
                        Log.d(TAG, "check the tokenInFTP [" + tokenInFTP + "]");
                        if (!TextUtils.equals(tokenInFTP, this.token)) {
                            continue;
                        }
                        Log.d(TAG, "add data!");
                        Log.d(TAG, "get input stream!");
                        InputStream in = mFTPClient.retrieveFileStream(filePath + "/" + f.getName());
                        String json = inputStream2String(in);
                        in.close();
                        mFTPClient.completePendingCommand();
                        PSBCDataBean psbcDataBean = new Gson().fromJson(json, PSBCDataBean.class);
                        CompanyDataBean bean = psbcDataBean.getCompanyDataBean();
                        Log.d(TAG, "check the data --> title is " + bean.getDataTitle());
                        list.add(bean);
                    }
                }
                mList = list;
                mSyncCallback.syncRemoteData(mThreadId, list);
                this.shortDown();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "ERROR  : " + e);
            }
        }

        @Override
        public void shortDown() {
            Log.d(TAG, "sync thread shortDown");
            super.shortDown();
            mStatus = SyncThreadStatus.FREED;
        }
    }

    public void startDownloadFile(int ftpThreadId, String filePath, FTPAndroidClientManager
            .FTPThreadCallback callback) {
        DownloadThread thread = new DownloadThread(ftpThreadId, callback, filePath);
        thread.start();
    }

    public void startUploadFile(int ftpThreadId, String filePath, FTPAndroidClientManager.FTPThreadCallback
            callback, PSBCDataBean psbcDataBean) {
        UploadThread thread = new UploadThread(ftpThreadId, callback, filePath);
        thread.setPSBCDataBean(psbcDataBean);
        thread.start();
    }

    public void syncRemoteData(int ftpThreadId, String filePath, FTPAndroidClientManager.FTPThreadCallback
            callback, String token, FTPAndroidClientManager.SyncRemoteDataCallback syncCallback) {
        Log.d(TAG, "syncRemoteData");
        if (mStatus != SyncThreadStatus.FREED && mSyncThread != null) {
            mSyncThread.shortDown();
            mSyncThread = null;
        }
        Log.d(TAG, "sync thread was empty!");
        mSyncThread = new SyncRemoteDataThread(ftpThreadId, callback, filePath);
        mSyncThread.setToken(token);
        mSyncThread.setSyncCallback(syncCallback);
        mSyncThread.start();
    }

    private final static int MSG_BASE = 1;
    private final static int MSG_UPLOAD_SUCCESSFUL = MSG_BASE << 1;
    private final static int MSG_DOWNLOAD_SUCCESSFUL = MSG_BASE << 2;
    private final static int MSG_CONNECT_SUCCESSFUL = MSG_BASE << 3;
    private final static int MSG_CONNECT_FAILED = MSG_BASE << 4;

    FTPAndroidClientManager.FTPThreadCallback mCallback;
    int mErrorCode;
    PSBCDataBean mPSBCDataBean;
    int mThreadId;

    final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UPLOAD_SUCCESSFUL:
                    Log.d(TAG, "MSG_UPLOAD_SUCCESSFUL");
                    mCallback.ftpUploadCallback();
                    break;
                case MSG_DOWNLOAD_SUCCESSFUL:
                    Log.d(TAG, "MSG_DOWNLOAD_SUCCESSFUL");
                    mCallback.ftpDownloadCallback(mThreadId, mPSBCDataBean);
                    break;
                case MSG_CONNECT_SUCCESSFUL:
                    Log.d(TAG, "MSG_CONNECT_SUCCESSFUL");
                    mCallback.connectSuccessful();
                    break;
                case MSG_CONNECT_FAILED:
                    Log.d(TAG, "MSG_CONNECT_FAILED");
                    mCallback.connectFailed(mErrorCode);
                    break;
            }
        }
    };

    protected void notifyCallback(FTPAndroidClientManager.FTPThreadCallback callback, int whichMsg, int errorCode, PSBCDataBean psbcDataBean, int threadId) {
        Log.d(TAG, "notifyCallback please check whichMsg [" + whichMsg + "]");
        mCallback = callback;
        mErrorCode = errorCode;
        mPSBCDataBean = psbcDataBean;
        mThreadId = threadId;
        mHandler.sendEmptyMessage(whichMsg);
    }

    public static String inputStream2String(InputStream is) throws IOException {
        if (null == is) {
            return "";
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i = -1;
        while ((i = is.read()) != -1) {
            baos.write(i);
        }
        String json = baos.toString();
        baos.close();
        return json;
    }
}
