package com.wp.demo.psbcdemo2;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.CursorAdapter;
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

import com.google.gson.Gson;
import com.wp.demo.psbcdemo2.tools.BaseFragment;
import com.wp.demo.psbcdemo2.tools.GroupingListAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LocDataListFragment extends BaseFragment {

    private final static int MSG_DATA_CHANGED = 1;

    class DataChangedObserver extends ContentObserver {

        Handler handler;

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public DataChangedObserver(Handler handler) {
            super(handler);
            this.handler = handler;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            handler.sendEmptyMessage(MSG_DATA_CHANGED);
        }
    }

    final Handler mDataChangedHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_DATA_CHANGED) {
                if (null != mAdapter) {
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    LinearLayout mCenterLayout;
    ListView mListView;
    CreateViewAdapter mAdapter;
    static String TOKEN;
    Cursor mSourceCursor;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        if (null != savedInstanceState) {
            TOKEN = savedInstanceState.getString(DemoActivity.KEY_TOKEN);
        } else {
            Log.d(TAG, "The 【LocDataListFragment】savedInstanceState is empty!");
        }
        return inflater.inflate(R.layout.location_data_list_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);
        mCenterLayout = (LinearLayout) view.findViewById(R.id.center_layout);
        mListView = (ListView) view.findViewById(R.id.list_view);
        Bundle bundle = getArguments();
        if (null != bundle) {
            TOKEN = bundle.getString(DemoActivity.KEY_TOKEN);
        } else Log.d(TAG, "Get arguments was empty!");
        DataChangedObserver observer = new DataChangedObserver(mDataChangedHandler);
        getActivity().getContentResolver().registerContentObserver(DemoActivity.PERSONNEL_URI, true, observer);
        getActivity().getContentResolver().registerContentObserver(DemoActivity.COMPANY_DATA_URI, true, observer);
        mCenterLayout.setVisibility(View.VISIBLE);
        init(TOKEN);
    }

    protected void init(String token) {
        Log.d(TAG, "The TOKEN is [" + token + "]");
        if (token == null) {
            mCenterLayout.setVisibility(View.VISIBLE);
        } else {
            mCenterLayout.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
            String selection = DemoActivity.ID + "=?";
            String[] selectionArgs = new String[]{token};
            mSourceCursor = getActivity().getContentResolver().query(
                    DemoActivity.COMPANY_DATA_URI, null, selection,
                    selectionArgs, null);
            if (mSourceCursor != null && mSourceCursor.moveToFirst()) {
                try {
                    mAdapter = new CreateViewAdapter(getActivity(), mSourceCursor);
                    mListView.setAdapter(mAdapter);
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            } else {
                Log.d(TAG, "The cursor is empty or the data is empty!");
                mCenterLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    class CreateViewAdapter extends CursorAdapter {
        Context mContext;

        @SuppressWarnings("deprecation")
        public CreateViewAdapter(Context context, Cursor c) {
            // TODO Auto-generated constructor stub
            super(context, c);
            mContext = context;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
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
        public void bindView(View view, Context context, Cursor cursor) {
            // TODO Auto-generated method stub
            String json = cursor.getString(cursor
                    .getColumnIndex(DemoActivity.DATA_1));
            DemoActivity.BeanCompanyData data = new Gson().fromJson(json,
                    DemoActivity.BeanCompanyData.class);
            Log.d(TAG, "Check the source [ the title is {" + data.title + "} ]");
            final ViewHolder holder = (ViewHolder) view.getTag();
            if (null != data.bitmap) {
                Log.d(TAG, "the bitmap un empty!");
                holder.imageView.setImageBitmap(data.bitmap);
            } else {
                Log.d(TAG, "the bitmap is empty!");
                holder.imageView.setImageDrawable(getActivity().getResources()
                        .getDrawable(R.drawable.ic_launcher));
            }
            if (TextUtils.isEmpty(data.title)) {
                SimpleDateFormat formatter = new SimpleDateFormat(
                        "yyyy年MM月dd日   HH:mm:ss");
                Date curDate = new Date(System.currentTimeMillis());
                String str = formatter.format(curDate);
                holder.titleString.setText("PSBC test demo app , create a new picture when " + str);
            } else {
                holder.titleString.setText(data.title);
            }
            if (TextUtils.isEmpty(data.data_1)) {
                holder.contentText.setText("Empty!");
            } else {
                holder.contentText.setText(data.data_1);
            }
            Log.d(TAG, "The bindView end!");
        }

//        @Override
//        protected void addGroups(Cursor cursor) {
//
//        }
//
//        @Override
//        protected View newStandAloneView(Context context, ViewGroup parent) {
//            // TODO Auto-generated method stub
//            LayoutInflater inflater = (LayoutInflater) mContext
//                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            View view = inflater.inflate(
//                    R.layout.location_data_list_item_layout, parent, false);
//            ViewHolder holder = new ViewHolder();
//            holder.imageView = (ImageView) view.findViewById(R.id.image_view);
//            holder.titleString = (TextView) view.findViewById(R.id.item_title);
//            holder.contentText = (TextView) view
//                    .findViewById(R.id.item_content_string);
//            view.setTag(holder);
//            return view;
//        }
//
//        @Override
//        protected void bindStandAloneView(View view, Context context, Cursor cursor) {
//            bindView(view, cursor, 1);
//        }
//
//        @Override
//        protected View newGroupView(Context context, ViewGroup parent) {
//            // TODO Auto-generated method stub
//            LayoutInflater inflater = (LayoutInflater) mContext
//                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            View view = inflater.inflate(
//                    R.layout.location_data_list_item_layout, parent, false);
//            ViewHolder holder = new ViewHolder();
//            holder.imageView = (ImageView) view.findViewById(R.id.image_view);
//            holder.titleString = (TextView) view.findViewById(R.id.item_title);
//            holder.contentText = (TextView) view
//                    .findViewById(R.id.item_content_string);
//            view.setTag(holder);
//            return view;
//        }
//
//        @Override
//        protected void bindGroupView(View view, Context context, Cursor cursor, int groupSize, boolean expanded) {
//            bindView(view, cursor, groupSize);
//        }
//
//        @Override
//        protected View newChildView(Context context, ViewGroup parent) {
//            // TODO Auto-generated method stub
//            LayoutInflater inflater = (LayoutInflater) mContext
//                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            View view = inflater.inflate(
//                    R.layout.location_data_list_item_layout, parent, false);
//            ViewHolder holder = new ViewHolder();
//            holder.imageView = (ImageView) view.findViewById(R.id.image_view);
//            holder.titleString = (TextView) view.findViewById(R.id.item_title);
//            holder.contentText = (TextView) view
//                    .findViewById(R.id.item_content_string);
//            view.setTag(holder);
//            return view;
//        }
//
//        @Override
//        protected void bindChildView(View view, Context context, Cursor cursor) {
//            bindView(view, cursor, 1);
//        }

        class ViewHolder {
            ImageView imageView;
            TextView titleString;
            TextView contentText;
            Button actionBtnButton;
        }
    }

    public void updateToken(String token) {
        Log.d(TAG, "change token to [" + token + "]");
        TOKEN = token;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != mSourceCursor) {
            mSourceCursor.close();
        }
    }
}
