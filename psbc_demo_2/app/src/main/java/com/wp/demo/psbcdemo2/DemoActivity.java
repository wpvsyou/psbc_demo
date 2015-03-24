package com.wp.demo.psbcdemo2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.wp.demo.psbcdemo2.tools.BaseFragment;
import com.wp.demo.psbcdemo2.tools.BaseFragmentActivity;

public class DemoActivity extends BaseFragmentActivity implements
        DemoFragment.UserSelectLogin {

    public static final String TAG = "PSBC_case_demo_debug";
    public static final String DATABASE_NAME = "psbcdatabase";
    public static final String ID = "id";
    public static final String USER_NAME = "user_name";
    public static final String PASSWORD = "password";
    public static final String COMPANY_DATA = "company_data";
    public static final String PERSONNEL = "personnel";
    public static final String DATA_1 = "data_1";

    public static final Uri COMPANY_DATA_URI = Uri.parse("content://"
            + DATABASE_NAME + "/" + COMPANY_DATA);
    public static final Uri PERSONNEL_URI = Uri.parse("content://"
            + DATABASE_NAME + "/" + PERSONNEL);

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
        if (null == mLocDataListFragment) {
            Log.d(TAG, "The mLocDataListFragment is empty!");
            mLocDataListFragment = new LocDataListFragment();
            Bundle bundle = new Bundle();
            bundle.putString(KEY_TOKEN, OBJ);
            mLocDataListFragment.setArguments(bundle);
        } else {
            Log.d(TAG, "update token to [" + OBJ + "]");
            mLocDataListFragment.updateToken(OBJ);
        }
        getSupportFragmentManager().beginTransaction().setCustomAnimations(
                android.R.anim.fade_in, android.R.anim.fade_out);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mLocDataListFragment)
                .commit();
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
            BeanCompanyData beanCompanyData = new BeanCompanyData();
            SimpleDateFormat formatter = new SimpleDateFormat(
                    "yyyy年MM月dd日   HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String str = formatter.format(curDate);
            beanCompanyData.title = "PSBC test demo app , create a new picture when "
                    + str;

            Log.e(TAG, "保存图片");
            File f = new File(Environment.getDownloadCacheDirectory(), "picture");
            if (f.exists()) {
                f.delete();
            }
            try {
                FileOutputStream out = new FileOutputStream(f);
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.flush();
                out.close();
                Log.i(TAG, "已经保存");
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            Bitmap smallBmp = ThumbnailUtils.createVideoThumbnail(f.toString(), 320);
            beanCompanyData.bitmap = smallBmp;
            beanCompanyData.data_1 = "This is a test picture!";
            Log.d(TAG, "The title is [" + beanCompanyData.title + "] the data_1 is [" + beanCompanyData.data_1 + "]");
            String json = new Gson().toJson(beanCompanyData);
            ContentValues values = new ContentValues();
            values.put(DATA_1, json);
            values.put(ID, OBJ);
            getContentResolver().insert(COMPANY_DATA_URI, values);
        }
    }

    public static class BeanCompanyData {
        String title;
        String data_1;
        Bitmap bitmap;
    }

    private static Bitmap small(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(0.2f, 0.2f); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizeBmp;
    }
}
