package com.wp.demo.psbcdemo2.scrollerdelete_master;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wp.demo.psbc.count.PSBCCount;
import com.wp.demo.psbcdemo2.R;
import com.wp.demo.psbcdemo2.tools.GroupingListAdapter;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DeleteAdapter extends GroupingListAdapter {

    private final static String TAG = "PSBC_case_demo_debug_DeleteAdapter";
    public static ListItemDelete itemDelete = null;
    private LayoutInflater mInflater;
    private Context context;

    public DeleteAdapter(Context context) {
        super(context);
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @SuppressLint("LongLogTag")
    public void bindView(View view, Cursor cursor, int count) {
        Log.d(TAG, "bindView!");
        // TODO Auto-generated method stub
        final ViewHolder holder = (ViewHolder) view.getTag();
        ByteArrayInputStream thumbnailStream = new ByteArrayInputStream
                (cursor.getBlob(cursor.getColumnIndex(PSBCCount.Company_data.DATA_THUMBNAIL)));
        Drawable drawable = Drawable.createFromStream(thumbnailStream, "img");
        if (null != drawable) {
            Log.d(TAG, "The drawable un empty!");
            holder.imageView.setImageDrawable(drawable);
        } else {
            Log.d(TAG, "the source img is empty!");
            holder.imageView.setImageDrawable(context.getResources()
                    .getDrawable(R.drawable.ic_launcher));
        }
        String title = cursor.getString(cursor.getColumnIndex(PSBCCount.Company_data.DATA_INFORMATION));
        if (TextUtils.isEmpty(title)) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日   HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String str = formatter.format(curDate);
            holder.titleString.setText("PSBC test demo app , create a new picture when " + str);
        } else {
            holder.titleString.setText(title);
        }
        String information = cursor.getString(cursor.getColumnIndex(PSBCCount.Company_data.DATA_TITLE));
        if (TextUtils.isEmpty(information)) {
            holder.contentText.setText("Empty!");
        } else {
            holder.contentText.setText(information);
        }
        Log.d(TAG, "The bindView end!");
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
        convertView.setTag(holder);
        holder.btnDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfo("点击删除了");
            }
        });
        holder.btnNao.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfo("点击了闹铃");
            }
        });
        convertView.setTag(holder);
        return convertView;
    }

    @Override
    protected void bindStandAloneView(View view, Context context, Cursor cursor) {
        bindView(view, cursor, -1);
    }

    @Override
    protected View newGroupView(Context context, ViewGroup parent) {
        return null;
    }

    @Override
    protected void bindGroupView(View view, Context context, Cursor cursor, int groupSize, boolean expanded) {
        bindView(view, cursor, -1);
    }

    @Override
    protected View newChildView(Context context, ViewGroup parent) {
        return null;
    }

    @Override
    protected void bindChildView(View view, Context context, Cursor cursor) {
        bindView(view, cursor, -1);
    }

    class ViewHolder {
        ImageView imageView;
        TextView titleString;
        TextView contentText;
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
}
