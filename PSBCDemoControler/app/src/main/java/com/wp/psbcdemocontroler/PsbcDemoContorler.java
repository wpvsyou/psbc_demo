package com.wp.psbcdemocontroler;

import android.content.Context;
import android.text.TextUtils;

import com.wp.androidftpclient.FTPAndroidClientManager;
import com.wp.demo.psbc.count.PSBCCount;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bean.PSBCDataBean;
import bean.PersonalBean;
import bean.Personals;

/**
 * Created by wangpeng on 15-4-8.
 */
public class PsbcDemoContorler {

    public final static int ERROR_CODE_BASE = 1;
    public final static int CODE_CONNECT_TIME_OUT = ERROR_CODE_BASE << 1;
    public final static int CODE_USERNAME_EXIST = ERROR_CODE_BASE << 2;
    public final static int CODE_WRONG_USERNAME_EMPTY = ERROR_CODE_BASE << 3;
    public final static int CODE_WRONG_PASSWORD_EMPTY = ERROR_CODE_BASE << 4;
    public final static int CODE_SUCCESSFUL = ERROR_CODE_BASE << 5;

    public final static int CODE_USER_UN_EXIST = ERROR_CODE_BASE << 6;
    public final static int CODE_WRONG_USER_OR_PWD = ERROR_CODE_BASE << 7;

    public final static int NOTIFY_TYPE_BASE = 2;
    public final static int NOTIFY_TYPE_BY_CREATE = NOTIFY_TYPE_BASE << 1;
    public final static int NOTIFY_TYPE_BY_DELETE = NOTIFY_TYPE_BASE << 2;
    public final static int NOTIFY_TYPE_BY_FREEZE = NOTIFY_TYPE_BASE << 3;
    public final static int NOTIFY_TYPE_BY_UNFREEZE = NOTIFY_TYPE_BASE << 4;

    public interface ContorlerCreateCallback {
        void onCreateAccount(boolean successful, int errorCode);
    }

    public interface ContorlerDeleteCallback{
        void onDeleteAccount(boolean successful, int errorCode);
    }

    public interface ContorlerFreezeCallback{
        void onFreezeAccount(boolean successful, int errorCode);
    }

    public interface ContorlerUnfreezeCallback {
        void onUnFreezeAccount(boolean successful, int errorCode);
    }

    private static PsbcDemoContorler INSTANCE;

    Context mContext;
    FTPAndroidClientManager mFTPManager;

    private PsbcDemoContorler(Context context) {
        mContext = context;
        mFTPManager = FTPAndroidClientManager.getInstance(context);
    }

    public static PsbcDemoContorler getInstance(Context context) {
        if (null == INSTANCE) {
            INSTANCE = new PsbcDemoContorler(context);
        }
        return INSTANCE;
    }

    private int checkAccountLegitimacy(String username, String password) {
        if (TextUtils.isEmpty(username)) {
            return CODE_WRONG_USERNAME_EMPTY;
        } else if (filterTransferCharacter(username)) {
            return CODE_WRONG_USERNAME_EMPTY;
        }

        if (TextUtils.isEmpty(password)) {
            return CODE_WRONG_PASSWORD_EMPTY;
        } else if (filterTransferCharacter(password)) {
            return CODE_WRONG_PASSWORD_EMPTY;
        }
        return CODE_SUCCESSFUL;
    }

    /**
     * 解冻账户。
     *
     * @param username 账户名。
     * @param password 密码。
     * @param callback 回调接口。 当服务器相应后回调并返回结果。
     */
    public synchronized void unfreezeAccount(final String username, final String password, final ContorlerUnfreezeCallback callback) {
        switch (checkAccountLegitimacy(username, password)) {
            case CODE_WRONG_USERNAME_EMPTY:
                callback.onUnFreezeAccount(false, CODE_WRONG_USERNAME_EMPTY);
                return;
            case CODE_WRONG_PASSWORD_EMPTY:
                callback.onUnFreezeAccount(false, CODE_WRONG_PASSWORD_EMPTY);
                return;
        }

        mFTPManager.downloadFile(1, FTPAndroidClientManager.PERSONAL_PATH, new FTPAndroidClientManager.FTPThreadCallback() {

            @Override
            public void ftpDownloadCallback(int threadId, PSBCDataBean psbcDataBean) {
                if (null == psbcDataBean) {
                    callback.onUnFreezeAccount(false, CODE_USER_UN_EXIST);
                    return;
                }
                if (null == psbcDataBean.getPersonalsBean()) {
                    callback.onUnFreezeAccount(false, CODE_USER_UN_EXIST);
                    return;
                }
                if (null == psbcDataBean.getPersonalsBean().getList()) {
                    callback.onUnFreezeAccount(false, CODE_USER_UN_EXIST);
                    return;
                }
                List<PersonalBean> ps = psbcDataBean.getPersonalsBean().getList();
                if (ps.size() > 0) {
                    for (PersonalBean p : ps) {
                        if (TextUtils.equals(username, p.getUserName()) && TextUtils.equals(password, p.getPassword())) {
                            PersonalBean personalBean = new PersonalBean();
                            personalBean.setId(p.getId());
                            personalBean.setUserName(p.getUserName());
                            personalBean.setPassword(p.getPassword());
                            personalBean.setLevel(PSBCCount.Login.LEVEL_UNFREEZE);
                            ps.remove(p);
                            ps.add(personalBean);
                            Personals pos = new Personals();
                            pos.setList(ps);
                            PSBCDataBean pb = new PSBCDataBean();
                            pb.setPersonalBean(pos);
                            ControlCallback cb = new ControlCallback();
                            cb.type = NOTIFY_TYPE_BY_UNFREEZE;
                            cb.unfreezeCallback = callback;
                            updateAccount(pb, cb);
                            return;
                        }
                    }
                    callback.onUnFreezeAccount(false, CODE_WRONG_USER_OR_PWD);
                } else {
                    callback.onUnFreezeAccount(false, CODE_USER_UN_EXIST);
                }
            }

            @Override
            public void connectSuccessful() {

            }

            @Override
            public void ftpUploadCallback() {

            }

            @Override
            public void connectFailed(int errorCode) {
                callback.onUnFreezeAccount(false, CODE_CONNECT_TIME_OUT);
            }
        });
    }

    public synchronized void freezeAccount(final String username, final String password, final ContorlerFreezeCallback callback) {
        switch (checkAccountLegitimacy(username, password)) {
            case CODE_WRONG_USERNAME_EMPTY:
                callback.onFreezeAccount(false, CODE_WRONG_USERNAME_EMPTY);
                return;
            case CODE_WRONG_PASSWORD_EMPTY:
                callback.onFreezeAccount(false, CODE_WRONG_PASSWORD_EMPTY);
                return;
        }

        mFTPManager.downloadFile(1, FTPAndroidClientManager.PERSONAL_PATH, new FTPAndroidClientManager.FTPThreadCallback() {

            @Override
            public void ftpDownloadCallback(int threadId, PSBCDataBean psbcDataBean) {
                if (null == psbcDataBean) {
                    callback.onFreezeAccount(false, CODE_USER_UN_EXIST);
                    return;
                }
                if (null == psbcDataBean.getPersonalsBean()) {
                    callback.onFreezeAccount(false, CODE_USER_UN_EXIST);
                    return;
                }
                if (null == psbcDataBean.getPersonalsBean().getList()) {
                    callback.onFreezeAccount(false, CODE_USER_UN_EXIST);
                    return;
                }
                List<PersonalBean> ps = psbcDataBean.getPersonalsBean().getList();
                if (ps.size() > 0) {
                    for (PersonalBean p : ps) {
                        if (TextUtils.equals(username, p.getUserName()) && TextUtils.equals(password, p.getPassword())) {
                            PersonalBean personalBean = new PersonalBean();
                            personalBean.setId(p.getId());
                            personalBean.setUserName(p.getUserName());
                            personalBean.setPassword(p.getPassword());
                            personalBean.setLevel(PSBCCount.Login.LEVEL_FREEZE);
                            ps.remove(p);
                            ps.add(personalBean);
                            Personals pos = new Personals();
                            pos.setList(ps);
                            PSBCDataBean pb = new PSBCDataBean();
                            pb.setPersonalBean(pos);
                            ControlCallback cb = new ControlCallback();
                            cb.type = NOTIFY_TYPE_BY_FREEZE;
                            cb.freezeCallback = callback;
                            updateAccount(pb, cb);
                            return;
                        }
                    }
                    callback.onFreezeAccount(false, CODE_WRONG_USER_OR_PWD);
                } else {
                    callback.onFreezeAccount(false, CODE_USER_UN_EXIST);
                }
            }

            @Override
            public void connectSuccessful() {

            }

            @Override
            public void ftpUploadCallback() {

            }

            @Override
            public void connectFailed(int errorCode) {
                callback.onFreezeAccount(false, CODE_CONNECT_TIME_OUT);
            }
        });
    }

    public synchronized void deleteAccount(final String username, final String password, final ContorlerDeleteCallback callback) {
        switch (checkAccountLegitimacy(username, password)) {
            case CODE_WRONG_USERNAME_EMPTY:
                callback.onDeleteAccount(false, CODE_WRONG_USERNAME_EMPTY);
                return;
            case CODE_WRONG_PASSWORD_EMPTY:
                callback.onDeleteAccount(false, CODE_WRONG_PASSWORD_EMPTY);
                return;
        }

        mFTPManager.downloadFile(1, FTPAndroidClientManager.PERSONAL_PATH, new FTPAndroidClientManager.FTPThreadCallback() {
            @Override
            public void ftpDownloadCallback(int threadId, PSBCDataBean psbcDataBean) {
                if (null == psbcDataBean) {
                    callback.onDeleteAccount(false, CODE_USER_UN_EXIST);
                    return;
                }
                if (null == psbcDataBean.getPersonalsBean()) {
                    callback.onDeleteAccount(false, CODE_USER_UN_EXIST);
                    return;
                }
                if (null == psbcDataBean.getPersonalsBean().getList()) {
                    callback.onDeleteAccount(false, CODE_USER_UN_EXIST);
                    return;
                }
                List<PersonalBean> ps = psbcDataBean.getPersonalsBean().getList();
                if (ps.size() > 0) {
                    for (PersonalBean p : ps) {
                        if (TextUtils.equals(username, p.getUserName()) && TextUtils.equals(password, p.getPassword())) {
                            ps.remove(p);
                            Personals pos = new Personals();
                            pos.setList(ps);
                            PSBCDataBean pb = new PSBCDataBean();
                            pb.setPersonalBean(pos);
                            ControlCallback cb = new ControlCallback();
                            cb.type = NOTIFY_TYPE_BY_DELETE;
                            cb.deleteCallback = callback;
                            updateAccount(pb, cb);
                            return;
                        }
                    }
                    callback.onDeleteAccount(false, CODE_WRONG_USER_OR_PWD);
                } else {
                    callback.onDeleteAccount(false, CODE_USER_UN_EXIST);
                }
            }

            @Override
            public void connectSuccessful() {

            }

            @Override
            public void ftpUploadCallback() {

            }

            @Override
            public void connectFailed(int errorCode) {
                callback.onDeleteAccount(false, CODE_CONNECT_TIME_OUT);
            }
        });
    }

    public synchronized void createAccount(final String username, final String password, final ContorlerCreateCallback callback) {
        if (TextUtils.isEmpty(username)) {
            callback.onCreateAccount(false, CODE_WRONG_USERNAME_EMPTY);
            return;
        } else if (filterTransferCharacter(username)) {
            callback.onCreateAccount(false, CODE_WRONG_USERNAME_EMPTY);
            return;
        }

        if (TextUtils.isEmpty(password)) {
            callback.onCreateAccount(false, CODE_WRONG_PASSWORD_EMPTY);
            return;
        } else if (filterTransferCharacter(password)) {
            callback.onCreateAccount(false, CODE_WRONG_PASSWORD_EMPTY);
            return;
        }

        mFTPManager.downloadFile(1, FTPAndroidClientManager.PERSONAL_PATH, new FTPAndroidClientManager.FTPThreadCallback() {
            @Override
            public void ftpDownloadCallback(int threadId, PSBCDataBean psbcDataBean) {
                List<PersonalBean> ps = null;
                if (psbcDataBean != null) {
                    Personals personals = psbcDataBean.getPersonalsBean();
                    if (personals != null) {
                        ps = personals.getList();
                        if (ps != null && ps.size() > 0) {
                            for (PersonalBean p : ps) {
                                if (TextUtils.equals(p.getUserName(), username) && !TextUtils.equals("personal", username)) {
                                    callback.onCreateAccount(false, CODE_USERNAME_EXIST);
                                    return;
                                }
                            }
                        }
                    }
                }
                PersonalBean pb = new PersonalBean();
                pb.setUserName(username);
                pb.setPassword(password);
                pb.setLevel(PSBCCount.Login.LEVEL_UNFREEZE);
                pb.setId(username);
                if (null == ps) {
                    ps = new ArrayList<PersonalBean>();
                }
                ps.add(pb);
                Personals pos = new Personals();
                pos.setList(ps);
                PSBCDataBean pd = new PSBCDataBean();
                pd.setPersonalBean(pos);
                ControlCallback cb = new ControlCallback();
                cb.type = NOTIFY_TYPE_BY_CREATE;
                cb.createCallback = callback;
                updateAccount(pd, cb);
            }

            @Override
            public void connectSuccessful() {

            }

            @Override
            public void ftpUploadCallback() {

            }

            @Override
            public void connectFailed(int errorCode) {
                callback.onCreateAccount(false, CODE_CONNECT_TIME_OUT);
            }
        });
    }

    private static class ControlCallback {
        int type;
        int errorCode;
        boolean successful;
        ContorlerCreateCallback createCallback;
        ContorlerDeleteCallback deleteCallback;
        ContorlerFreezeCallback freezeCallback;
        ContorlerUnfreezeCallback unfreezeCallback;
    }

    private void updateCallback(ControlCallback cb) {
        switch (cb.type) {
            case NOTIFY_TYPE_BY_CREATE:
                cb.createCallback.onCreateAccount(cb.successful, cb.errorCode);
                break;
            case NOTIFY_TYPE_BY_DELETE:
                cb.deleteCallback.onDeleteAccount(cb.successful, cb.errorCode);
                break;
            case NOTIFY_TYPE_BY_FREEZE:
                cb.freezeCallback.onFreezeAccount(cb.successful, cb.errorCode);
                break;
            case NOTIFY_TYPE_BY_UNFREEZE:
                cb.unfreezeCallback.onUnFreezeAccount(cb.successful, cb.errorCode);
                break;
        }
        if (cb.successful) {
            notifyPsbcDemoClientByAction(cb.type);
        }
    }

    private synchronized void updateAccount(PSBCDataBean p, final ControlCallback callback) {
        mFTPManager.updateFile(1, FTPAndroidClientManager.PERSONAL_PATH, new FTPAndroidClientManager.FTPThreadCallback() {
            @Override
            public void ftpDownloadCallback(int threadId, PSBCDataBean psbcDataBean) {
            }

            @Override
            public void connectSuccessful() {
                callback.successful = true;
                callback.errorCode = CODE_SUCCESSFUL;
                updateCallback(callback);
            }

            @Override
            public void ftpUploadCallback() {
            }

            @Override
            public void connectFailed(int errorCode) {
                callback.successful = false;
                callback.errorCode = CODE_CONNECT_TIME_OUT;
                updateCallback(callback);
            }
        }, p);
    }

    private boolean filterTransferCharacter(String text) {
        String regEx = "[/\\:*?<>|\"'0\t\b\f\r\n' ']";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(text);
        return TextUtils.isEmpty(m.replaceAll(""));
    }

    private void notifyPsbcDemoClientByAction(int notifyCode) {
        switch (notifyCode) {
            case NOTIFY_TYPE_BY_CREATE:
                break;
            case NOTIFY_TYPE_BY_DELETE:
                break;
            case NOTIFY_TYPE_BY_FREEZE:
                break;
            case NOTIFY_TYPE_BY_UNFREEZE:
                break;
        }
    }

    public void lockDemoClient() {
        mContext.getContentResolver().notifyChange(PSBCCount.Uri.COMMAND_LOCK, null);
    }

    public void unlockDemoClient() {
        mContext.getContentResolver().notifyChange(PSBCCount.Uri.COMMAND_UN_LOCK, null);
    }
}
