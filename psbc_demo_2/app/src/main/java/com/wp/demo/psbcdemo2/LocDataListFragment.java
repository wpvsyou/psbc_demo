package com.wp.demo.psbcdemo2;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wp.demo.psbc.count.PSBCCount;
import com.wp.demo.psbcdemo2.scrollerdelete_master.DeleteAdapter;
import com.wp.demo.psbcdemo2.scrollerdelete_master.ScrollListviewDelete;
import com.wp.demo.psbcdemo2.tools.BaseFragment;
import com.wp.demo.psbcdemo2.tools.GroupingListAdapter;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LocDataListFragment extends BaseFragment implements ScrollListviewDelete.ItemClickListener{

    private final static int MSG_DATA_CHANGED = 1;

    @Override
    public void onItemClick(int position) {

    }

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
                Log.d(TAG, "MSG_DATA_CHANGED");
                if (null != mAdapter) {
                    String selection = PSBCCount.Personnel.ID + "=?";
                    String[] selectionArgs = new String[]{TOKEN};
                    Log.d(TAG, "observer : check the uri --> " + PSBCCount.Uri.COMPANY_DATA_URI);
                    Cursor cursor = mContentResolver.
                            query(PSBCCount.Uri.COMPANY_DATA_URI, null, selection, selectionArgs, null);
                    if (null != cursor) {
//                        mListView.setVisibility(View.VISIBLE);
                        mCenterLayout.setVisibility(View.GONE);
                        Log.d(TAG, "observer : the cursor un empty! cursor --> " + cursor.toString());
                        mAdapter.changeCursor(cursor);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        Log.d(TAG, "observer : the cursor was empty!");
                        mCenterLayout.setVisibility(View.VISIBLE);
//                        mListView.setVisibility(View.GONE);
                    }
                }
            }
        }
    };

    LinearLayout mCenterLayout;
//    ListView mListView;
//    CreateViewAdapter mAdapter;
    static String TOKEN;
    Cursor mSourceCursor;
    ContentResolver mContentResolver;

    ScrollListviewDelete mListView;
    DeleteAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContentResolver = getActivity().getContentResolver();
    }

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
//        mListView = (ListView) view.findViewById(R.id.list_view);
        mListView = (ScrollListviewDelete) view.findViewById(android.R.id.list);
        Bundle bundle = getArguments();
        if (null != bundle) {
            TOKEN = bundle.getString(DemoActivity.KEY_TOKEN);
        } else Log.d(TAG, "Get arguments was empty!");
        DataChangedObserver observer = new DataChangedObserver(mDataChangedHandler);
        getActivity().getContentResolver().registerContentObserver(PSBCCount.Uri.PERSONNEL_URI, true, observer);
        getActivity().getContentResolver().registerContentObserver(PSBCCount.Uri.COMPANY_DATA_URI, true, observer);

        String selection = PSBCCount.Company_data.ID + "=?";
        String[] selectionArgs = new String[]{TOKEN};
        mSourceCursor = getActivity().getContentResolver().query(
                PSBCCount.Uri.COMPANY_DATA_URI, null, selection,
                selectionArgs, null);
//        mAdapter = new CreateViewAdapter(getActivity());
        mAdapter = new DeleteAdapter(getActivity());
//        mListView.setAdapter(mAdapter);
        mListView.setAdapter(mAdapter);
        mCenterLayout.setVisibility(View.VISIBLE);
        init(TOKEN);
    }

    protected void init(String token) {
        Log.d(TAG, "The TOKEN is [" + token + "]");
        if (mSourceCursor != null && mSourceCursor.moveToFirst()) {
            mCenterLayout.setVisibility(View.GONE);
//            mListView.setVisibility(View.VISIBLE);
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

    class CreateViewAdapter extends GroupingListAdapter {
        Context mContext;

        @SuppressWarnings("deprecation")
        public CreateViewAdapter(Context context) {
            // TODO Auto-generated constructor stub
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
//            byte[] os = cursor.getBlob(cursor.getColumnIndex(PSBCCount.Company_data.DATA_IMAGE));
//            final Intent intent = new Intent(getActivity(), SimpleSingleImageViewer.class);
//            intent.putExtra(SimpleSingleImageViewer.IMAGE_OS, os);
//            holder.imageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (null != intent) {
//                        getActivity().startActivity(intent);
//                    } else {
//                        Toast.makeText(getActivity(), "Read file error!", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
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

    public void updateView() {
        mDataChangedHandler.sendEmptyMessage(MSG_DATA_CHANGED);
    }
}
