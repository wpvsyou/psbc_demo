package com.wp.demo.psbcdemo2;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.wp.demo.psbc.count.PSBCCount;
import com.wp.demo.psbc.count.PSBCCount.Company_data;
import com.wp.demo.psbcdemo2.tools.BaseFragment;
import com.wp.demo.psbcdemo2.tools.BaseFragmentActivity;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DemoActivity extends BaseFragmentActivity implements
        DemoFragment.UserSelectLogin {

    public static final String TAG = "PSBC_case_demo_debug";
    public final static String KEY_TOKEN = "key_token";
    LocDataListFragment mLocDataListFragment;
    static boolean HAS_LOGIN;
    static String OBJ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_activity_layout);
        if (null == savedInstanceState) {
            DemoFragment fragment = new DemoFragment();
            getSupportFragmentManager().popBackStack();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment).commit();
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (HAS_LOGIN) {
            if (null == mLocDataListFragment) {
                mLocDataListFragment = new LocDataListFragment();
                Bundle bundle = new Bundle();
                bundle.putString(KEY_TOKEN, OBJ);
                mLocDataListFragment.setArguments(bundle);
            } else mLocDataListFragment.updateToken(OBJ);
            getSupportFragmentManager().popBackStack();
            getSupportFragmentManager().beginTransaction().setCustomAnimations(
                    android.R.anim.fade_in, android.R.anim.fade_out);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, mLocDataListFragment)
                    .commit();
        }
    }

    @Override
    public void onSelectUser(String obj) {
        // TODO Auto-generated method stub
        HAS_LOGIN = true;
        Log.d(TAG, "Check the token in the DemoActivity callback method! [" + obj + "]");
        OBJ = obj;
        Log.d(TAG, "The mLocDataListFragment is empty!");
        mLocDataListFragment = new LocDataListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TOKEN, OBJ);
        mLocDataListFragment.setArguments(bundle);
        getSupportFragmentManager().popBackStack();
        getSupportFragmentManager().beginTransaction().setCustomAnimations(
                android.R.anim.fade_in, android.R.anim.fade_out);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mLocDataListFragment)
                .commit();
        mLocDataListFragment.updateToken(OBJ);
        mLocDataListFragment.updateView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            BaseFragment demoFragment = (BaseFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_container);
            if (null != demoFragment && !(demoFragment instanceof DemoFragment)) {
                HAS_LOGIN = false;
                DemoFragment fragment = new DemoFragment();
                getSupportFragmentManager().popBackStack();
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in,
                                android.R.anim.fade_out);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment).commit();
            } else {
                finish();
            }
        } else if (id == R.id.create_data) {
            if (HAS_LOGIN) {
                openCamera();
            } else {
                Toast.makeText(DemoActivity.this, "Please login first!",
                        Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    protected void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) bundle.get("data");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日   HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String str = formatter.format(curDate);
            String information = "PSBC test demo app , create a new picture when " + str;
            String title = "This is a test picture!";
            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            Bitmap thumbnail = getBitmapByWidth(os.toByteArray(), 0, os.toByteArray().length, 260, 10);
            final ByteArrayOutputStream thumbnailOs = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.PNG, 100, thumbnailOs);
            ContentValues values = new ContentValues();
            values.put(Company_data.ID, OBJ);
            values.put(Company_data.DATA_TITLE, title);
            values.put(Company_data.DATA_INFORMATION, information);
            values.put(Company_data.DATA_IMAGE, os.toByteArray());
            values.put(Company_data.DATA_THUMBNAIL, thumbnailOs.toByteArray());
            if (getContentResolver().insert(PSBCCount.Uri.COMPANY_DATA_URI, values) != null) {
                Log.d(TAG, "Insert image was done!");
            } else {
                Log.d(TAG, "Error insert image!!!");
            }
        }
    }

    public static Bitmap getBitmapByWidth(byte[] in, int offset, int length, int width, int addedScaling) {
        Bitmap temBitmap = null;
        try {
            BitmapFactory.Options outOptions = new BitmapFactory.Options();
            // 设置该属性为true，不加载图片到内存，只返回图片的宽高到options中。
            outOptions.inJustDecodeBounds = true;
            // 加载获取图片的宽高
            BitmapFactory.decodeByteArray(in, offset, length, outOptions);
            int height = outOptions.outHeight;
            if (outOptions.outWidth > width) {
                // 根据宽设置缩放比例
                outOptions.inSampleSize = outOptions.outWidth / width + 1 + addedScaling;
                outOptions.outWidth = width;
                // 计算缩放后的高度
                height = outOptions.outHeight / outOptions.inSampleSize;
                outOptions.outHeight = height;
            }
            // 重新设置该属性为false，加载图片返回
            outOptions.inJustDecodeBounds = false;
            outOptions.inPurgeable = true;
            outOptions.inInputShareable = true;
            temBitmap = BitmapFactory.decodeByteArray(in, offset, length, outOptions);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return temBitmap;
    }

    @Override
    public void onBackHomeOnClick() {
        super.onBackHomeOnClick();
        this.finish();
    }
}
