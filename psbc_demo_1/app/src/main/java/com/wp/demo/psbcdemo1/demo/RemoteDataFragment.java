package com.wp.demo.psbcdemo1.demo;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.wp.androidftpclient.FTPAndroidClientManager;
import com.wp.demo.psbcdemo1.psbccase.R;
import com.wp.demo.psbcdemo1.tools.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import bean.CompanyDataBean;
import bean.PSBCDataBean;

/**
 * Created by wangpeng on 15-3-21.
 */
public class RemoteDataFragment extends BaseFragment{

    private final static String TAG = "PSBC_case_demo_debug_RemoteDataFragment";
    TextView mCenterTextView;
    LinearLayout mCenterLayout;
    ListView mListView;
    RemoteDataAdapter mAdapter;
    List<CompanyDataBean> mList = new ArrayList<>();
//    ImageView mRefreshBtn;

    public final static String ACTION_REFRESH_DATA = "com.psbc.demo.ACTION_REFRESH_DATA";

    final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateCompanyData();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        BaseFragment.mCallback.showBtn();
        mAdapter.upgradeData(mList);
        mAdapter.notifyDataSetChanged();
        if (mList.size() <= 0) {
            mListView.setVisibility(View.GONE);
            mCenterLayout.setVisibility(View.VISIBLE);
        }
        Log.d(TAG, "RemoteDataFragment on resume!");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_REFRESH_DATA);
        getActivity().registerReceiver(mReceiver, filter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        return inflater.inflate(R.layout.show_data_fragment_layout, container,
                false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);
        mCenterTextView = (TextView) view.findViewById(R.id.center_text);
        mCenterLayout = (LinearLayout) view.findViewById(R.id.center_layout);
        mListView = (ListView) view.findViewById(R.id.listView);
//        if (null != getActivity().getActionBar()) {
//            mRefreshBtn =
//                    (ImageView) getActivity().getActionBar().getCustomView().findViewById(R.id.ic_action_refresh);
//            mRefreshBtn.setVisibility(View.VISIBLE);
//            mRefreshBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    updateCompanyData();
//                }
//            });
//        }

        mAdapter = new RemoteDataAdapter(getActivity());
        if (null != FTPAndroidClientManager.getInstance(getActivity()).getOldRemoteData()
                && FTPAndroidClientManager.getInstance(getActivity()).getOldRemoteData().size() > 0) {
            mCenterLayout.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
            mAdapter.upgradeData(FTPAndroidClientManager.getInstance(getActivity()).getOldRemoteData());
            mListView.setAdapter(mAdapter);
        }
    }

    protected void updateCompanyData() {
        showProgressDialog(getActivity(), "Please wait, uploading remote data...", false);
        FTPAndroidClientManager.getInstance(getActivity()).syncRemoteData
                (1, FTPAndroidClientManager.COMPANY_DATA_PATH, new FTPAndroidClientManager.FTPThreadCallback() {
                    @Override
                    public void ftpDownloadCallback(int threadId, PSBCDataBean psbcDataBean) {
                        Log.d(TAG, "ftpDownloadCallback");
                    }

                    @Override
                    public void connectSuccessful() {
                        Log.d(TAG, "connectSuccessful");
                    }

                    @Override
                    public void ftpUploadCallback() {
                        Log.d(TAG, "ftpUploadCallback");
                    }

                    @Override
                    public void connectFailed(int errorCode) {
                        dismissProgress();
                    }

                }, TokenHelper.getInstance().getToken(), new FTPAndroidClientManager.SyncRemoteDataCallback() {
                    @Override
                    public void syncRemoteData(int threadId, List<CompanyDataBean> companyDataBeans) {
                        if (null != companyDataBeans && companyDataBeans.size() > 0) {
                            Log.d(TAG, "sync remote data was successful!!!");
                            mList = companyDataBeans;
                            dismissProgress();
                            mHandler.sendEmptyMessage(MSG_BASE);
                        } else {
                            Log.d(TAG, "remote data was empty!");
                        }
                        BaseFragment.mCallback.hideBth();
                        dismissProgress();
                    }
                }, new FTPAndroidClientManager.SettingFtpClient() {
                    @Override
                    public String getUrl() {
                        return DemoActivity.getConfiguration().getFtpClientUrl();
                    }

                    @Override
                    public String getUsername() {
                        return DemoActivity.getConfiguration().getSuUsername();
                    }

                    @Override
                    public String getPassword() {
                        return DemoActivity.getConfiguration().getSuPassword();
                    }
                });
    }

    private final static int MSG_BASE = 1;

    final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mListView.setVisibility(View.VISIBLE);
            mCenterLayout.setVisibility(View.GONE);
            mAdapter.upgradeData(mList);
            mListView.setAdapter(mAdapter);
        }
    };

    class RemoteDataAdapter extends BaseAdapter {

        Context context;
        List<CompanyDataBean> list = new ArrayList<>();
        LayoutInflater mInflater;

        public void upgradeData(List<CompanyDataBean> list) {
            this.list = list;
        }

        public RemoteDataAdapter(Context context) {
            this.context = context;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return (CompanyDataBean) list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CompanyDataBean data = list.get(position);
            Log.d(TAG, "Check the data " + data.toString());
            ViewHolder holder;
            if (null == convertView) {
                convertView = mInflater.inflate(R.layout.location_data_list_item_layout, parent, false);
                holder = new ViewHolder();
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.imageView = (ImageView) convertView.findViewById(R.id.image_view);
            holder.imageView.setImageDrawable(byteToDrawable(data.getDataThumbnail()));
            holder.title = (TextView) convertView.findViewById(R.id.item_title);
            holder.title.setText(data.getDataTitle());
            holder.text = (TextView) convertView.findViewById(R.id.item_content_string);
            holder.text.setText(data.getDataInformation());
            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
            TextView title;
            TextView text;
        }

    }

    protected void showText(String txt) {
        mCenterLayout.setVisibility(View.VISIBLE);
        mCenterTextView.setVisibility(View.VISIBLE);
        mCenterTextView.setText(txt);
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

    public static synchronized Drawable byteToDrawable(String icon) {
        byte[] img = Base64.decode(icon.getBytes(), Base64.DEFAULT);
        Bitmap bitmap;
        if (img != null) {
            bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
            @SuppressWarnings("deprecation")
            Drawable drawable = new BitmapDrawable(bitmap);
            return drawable;
        }
        return null;
    }
}