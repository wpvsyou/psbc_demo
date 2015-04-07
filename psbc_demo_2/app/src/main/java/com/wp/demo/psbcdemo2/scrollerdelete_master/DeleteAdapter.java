package com.wp.demo.psbcdemo2.scrollerdelete_master;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wp.androidftpclient.FTPAndroidClientManager;
import com.wp.demo.psbc.count.PSBCCount;
import com.wp.demo.psbcdemo2.R;
import com.wp.demo.psbcdemo2.tools.GroupingListAdapter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import bean.CompanyDataBean;
import bean.PSBCDataBean;

public class DeleteAdapter extends GroupingListAdapter {

    public interface NotifyUpdateOrDelete {
        void onDataChanged();
    }

    private final static String TAG = "r";
    public static ListItemDelete itemDelete = null;
    private LayoutInflater mInflater;
    private Context context;
    private NotifyUpdateOrDelete mCallback;
    private FTPAndroidClientManager mFtpManager;

    public DeleteAdapter(Context context, NotifyUpdateOrDelete callback) {
        super(context);
        mInflater = LayoutInflater.from(context);
        this.context = context;
        mCallback = callback;
        mFtpManager = FTPAndroidClientManager.getInstance(context);
    }

    @SuppressLint("LongLogTag")
    public void bindView(View view, Cursor cursor, int count) {
        Log.d(TAG, "bindView!");
        // TODO Auto-generated method stub
        final ViewHolder holder = (ViewHolder) view.getTag();
        final int baseId = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
        ByteArrayInputStream thumbnailStream = new ByteArrayInputStream
                (cursor.getBlob(cursor.getColumnIndex(PSBCCount.Company_data.DATA_THUMBNAIL)));
        final Drawable drawable = Drawable.createFromStream(thumbnailStream, "img");
        if (null != drawable) {
            Log.d(TAG, "The drawable un empty!");
            holder.imageView.setImageDrawable(drawable);
        } else {
            Log.d(TAG, "the source img is empty!");
            holder.imageView.setImageDrawable(context.getResources()
                    .getDrawable(R.drawable.ic_launcher));
        }
        final String title = cursor.getString(cursor.getColumnIndex(PSBCCount.Company_data.DATA_INFORMATION));
        if (TextUtils.isEmpty(title)) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日   HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String str = formatter.format(curDate);
            holder.titleString.setText("PSBC test demo app , create a new picture when " + str);
        } else {
            holder.titleString.setText(title);
        }
        final String information = cursor.getString(cursor.getColumnIndex(PSBCCount.Company_data.DATA_TITLE));
        if (TextUtils.isEmpty(information)) {
            holder.contentText.setText("Empty!");
        } else {
            holder.contentText.setText(information);
        }
        final String token = cursor.getString(cursor.getColumnIndex(PSBCCount.Company_data.ID));
        holder.btnDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfo("点击删除了" + baseId);
                doDeleteAction(baseId + "");
            }
        });

        holder.btnUpdate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfo("update!" + baseId);
                doUploadAction(baseId + "", token, title, information, drawable);
            }
        });
        Log.d(TAG, "The bindView end!");
    }

    void doDeleteAction(String whichColumns) {
        String where = BaseColumns._ID + "=?";
        String[] selectionArgs = new String[]{whichColumns};
        context.getContentResolver().delete(PSBCCount.Uri.COMPANY_DATA_URI, where, selectionArgs);
        mCallback.onDataChanged();
    }

    void doUploadAction(final String whichColumns, String token, String title, String content, Drawable thumbnail) {
        showProgressDialog(context, "uploading...", false);
        PSBCDataBean dataBean = new PSBCDataBean();
        CompanyDataBean companyDataBean = new CompanyDataBean();
        companyDataBean.setId(token);
        companyDataBean.setDataTitle(title);
        companyDataBean.setDataInformation(content);
        companyDataBean.setDataThumbnail(drawableToByte(thumbnail));
        Log.d(TAG, "Check the data ---XXX--- " + companyDataBean.toString());
        dataBean.setCompanyDataBean(companyDataBean);
        mFtpManager.updateFile(1, FTPAndroidClientManager.COMPANY_DATA_PATH + "/" + token + whichColumns,
                new FTPAndroidClientManager.FTPThreadCallback() {
                    @Override
                    public void ftpDownloadCallback(int threadId, PSBCDataBean psbcDataBean) {

                    }

                    @Override
                    public void connectSuccessful() {

                    }

                    @Override
                    public void ftpUploadCallback() {
                        dismissProgress();
                        Log.d(TAG, "successful!");
                        doDeleteAction(whichColumns);
                    }

                    @Override
                    public void connectFailed(int errorCode) {
                        dismissProgress();
                        Log.d(TAG, "failed! time out connect failed!!!!!!!!!!!!!");
                    }
                }, dataBean);
    }

    @Override
    protected void addGroups(Cursor cursor) {
    }

    @Override
    protected View newStandAloneView(Context context, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        View convertView = mInflater.inflate(R.layout.item_delete, null);
        holder.imageView = (ImageView) convertView.findViewById(R.id.image_view);
        holder.titleString = (TextView) convertView.findViewById(R.id.item_title);
        holder.contentText = (TextView) convertView.findViewById(R.id.item_content_string);
        holder.btnDelete = (Button) convertView.findViewById(R.id.btnDelete);
        holder.btnNao = (Button) convertView.findViewById(R.id.btnNao);
        holder.btnUpdate = (Button) convertView.findViewById(R.id.update_btn);
        convertView.setTag(holder);
        return convertView;
    }

    @Override
    protected void bindStandAloneView(View view, Context context, Cursor cursor) {
        bindView(view, cursor, -1);
    }

    @Override
    protected View newGroupView(Context context, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        View convertView = mInflater.inflate(R.layout.item_delete, null);
        holder.imageView = (ImageView) convertView.findViewById(R.id.image_view);
        holder.titleString = (TextView) convertView.findViewById(R.id.item_title);
        holder.contentText = (TextView) convertView.findViewById(R.id.item_content_string);
        holder.btnDelete = (Button) convertView.findViewById(R.id.btnDelete);
        holder.btnNao = (Button) convertView.findViewById(R.id.btnNao);
        holder.btnUpdate = (Button) convertView.findViewById(R.id.update_btn);
        convertView.setTag(holder);
        return convertView;
    }

    @Override
    protected void bindGroupView(View view, Context context, Cursor cursor, int groupSize, boolean expanded) {
        bindView(view, cursor, -1);
    }

    @Override
    protected View newChildView(Context context, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        View convertView = mInflater.inflate(R.layout.item_delete, null);
        holder.imageView = (ImageView) convertView.findViewById(R.id.image_view);
        holder.titleString = (TextView) convertView.findViewById(R.id.item_title);
        holder.contentText = (TextView) convertView.findViewById(R.id.item_content_string);
        holder.btnDelete = (Button) convertView.findViewById(R.id.btnDelete);
        holder.btnNao = (Button) convertView.findViewById(R.id.btnNao);
        holder.btnUpdate = (Button) convertView.findViewById(R.id.update_btn);
        convertView.setTag(holder);
        return convertView;
    }

    @Override
    protected void bindChildView(View view, Context context, Cursor cursor) {
        bindView(view, cursor, -1);
    }

    class ViewHolder {
        ImageView imageView;
        TextView titleString;
        TextView contentText;
        Button btnUpdate;
        Button btnDelete;
        Button btnNao;
    }

    private Toast mToast;

    public void showInfo(String text) {
        if (mToast == null) {
            mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    public static void ItemDeleteReset() {
        if (itemDelete != null) {
            itemDelete.reSet();
        }
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

    public static synchronized String drawableToByte(Drawable drawable) {
        if (drawable != null) {
            Bitmap bitmap = Bitmap
                    .createBitmap(
                            drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight(),
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                    : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            int size = bitmap.getWidth() * bitmap.getHeight() * 4;
            // 创建一个字节数组输出流,流的大小为size
            ByteArrayOutputStream baos = new ByteArrayOutputStream(size);
            // 设置位图的压缩格式，质量为100%，并放入字节数组输出流中
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            // 将字节数组输出流转化为字节数组byte[]
            byte[] imagedata = baos.toByteArray();
            String icon = Base64.encodeToString(imagedata, Base64.DEFAULT);
            return icon;
        }
        return null;
    }
}
