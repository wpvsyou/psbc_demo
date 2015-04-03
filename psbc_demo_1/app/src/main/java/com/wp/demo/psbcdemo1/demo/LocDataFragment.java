package com.wp.demo.psbcdemo1.demo;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wp.demo.psbc.count.PSBCCount;
import com.wp.demo.psbcdemo1.psbccase.R;
import com.wp.demo.psbcdemo1.tools.BaseFragment;
import com.wp.demo.psbcdemo1.tools.GroupingListAdapter;
import com.wp.demo.psbcdemo1.tools.SimpleSingleImageViewer;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by wangpeng on 15-3-21.
 */
public class LocDataFragment extends BaseFragment {

    TextView mCenterTextView;
    LinearLayout mCenterLayout;
    LocDataAdapter mAdapter;
    ContentResolver mContentResolver;
    ListView mListView;
    static String TOKEN;
    Cursor mSourceCursor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContentResolver = getActivity().getContentResolver();
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
        mAdapter = new LocDataAdapter(getActivity());

        Bundle bundle = getArguments();
        if (null != bundle) {
            TOKEN = bundle.getString(DemoActivity.KEY_TOKEN);
        } else Log.d(TAG, "Get arguments was empty!");
        Log.d(TAG, "The token is [" + TOKEN + "]");
        if (TextUtils.isEmpty(TOKEN)) {
            TOKEN = TokenHelper.getInstance().getToken();
        }
        String selection = PSBCCount.Company_data.ID + "=?";
        String[] selectionArgs = new String[]{TOKEN};
        mSourceCursor = getActivity().getContentResolver().query(
                PSBCCount.Uri.COMPANY_DATA_URI, null, selection,
                selectionArgs, null);

        mListView.setAdapter(mAdapter);
        showEmptyText();
        Log.d(TAG, "The TOKEN is [" + TOKEN + "]");
        if (mSourceCursor != null && mSourceCursor.moveToFirst()) {
            mCenterLayout.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
            try {
                mAdapter.changeCursor(mSourceCursor);
                mAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "mCursor was empty!");
        }
    }

    public void showEmptyText() {
        mCenterLayout.setVisibility(View.VISIBLE);
        mCenterTextView.setVisibility(View.VISIBLE);
        mCenterTextView.setText(getActivity().getString(
                R.string.no_loc_data_text));
    }

    class LocDataAdapter extends GroupingListAdapter {
        Context mContext;

        public LocDataAdapter(Context context) {
            super(context);
            mContext = context;
        }

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
                holder.imageView.setImageDrawable(getActivity().getResources()
                        .getDrawable(R.drawable.ic_launcher));
            }
            final byte[] os = cursor.getBlob(cursor.getColumnIndex(PSBCCount.Company_data.DATA_IMAGE));
            final Intent intent = new Intent(getActivity(), SimpleSingleImageViewer.class);
            intent.putExtra(SimpleSingleImageViewer.IMAGE_OS, os);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != os) {
                        getActivity().startActivity(intent);
                    } else {
                        Toast.makeText(getActivity(), "Read file error!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
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
            // TODO Auto-generated method stub
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(
                    R.layout.location_data_list_item_layout, parent, false);
            ViewHolder holder = new ViewHolder();
            holder.imageView = (ImageView) view.findViewById(R.id.image_view);
            holder.titleString = (TextView) view.findViewById(R.id.item_title);
            holder.contentText = (TextView) view
                    .findViewById(R.id.item_content_string);
            view.setTag(holder);
            return view;
        }

        @Override
        protected void bindStandAloneView(View view, Context context, Cursor cursor) {
            bindView(view, cursor, 1);
        }

        @Override
        protected View newGroupView(Context context, ViewGroup parent) {
            // TODO Auto-generated method stub
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(
                    R.layout.location_data_list_item_layout, parent, false);
            ViewHolder holder = new ViewHolder();
            holder.imageView = (ImageView) view.findViewById(R.id.image_view);
            holder.titleString = (TextView) view.findViewById(R.id.item_title);
            holder.contentText = (TextView) view
                    .findViewById(R.id.item_content_string);
            view.setTag(holder);
            return view;
        }

        @Override
        protected void bindGroupView(View view, Context context, Cursor cursor, int groupSize, boolean expanded) {
            bindView(view, cursor, groupSize);
        }

        @Override
        protected View newChildView(Context context, ViewGroup parent) {
            // TODO Auto-generated method stub
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(
                    R.layout.location_data_list_item_layout, parent, false);
            ViewHolder holder = new ViewHolder();
            holder.imageView = (ImageView) view.findViewById(R.id.image_view);
            holder.titleString = (TextView) view.findViewById(R.id.item_title);
            holder.contentText = (TextView) view
                    .findViewById(R.id.item_content_string);
            view.setTag(holder);
            return view;
        }

        @Override
        protected void bindChildView(View view, Context context, Cursor cursor) {
            bindView(view, cursor, 1);
        }

        class ViewHolder {
            ImageView imageView;
            TextView titleString;
            TextView contentText;
            Button actionBtnButton;
        }
    }

    @Override
    public void onViewChanged(Cursor cursor) {
        super.onViewChanged(cursor);
        if (null != mAdapter) {
            mAdapter.changeCursor(cursor);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setToken(String obj) {
        Log.d(TAG + " LocDataFragment", "setToken to [" + obj + "]");
        super.setToken(obj);
        TOKEN = obj;
    }
}
