package com.wp.demo.psbcdemo1.tools;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.wp.demo.psbcdemo1.psbccase.R;
import com.wp.demo.psbcdemo1.tools.gifview.GifView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * SimpleSingleImageViewer.java
 * Use to open a single image.
 * Created by wangpeng on 14-8-21.
 */
public class SimpleSingleImageViewer extends Activity {

    public final static String IMAGE_OS = "image_os";
    private final static String TAG = "SimpleSingleImageViewer";

    private final static String MINI_TYPE = "mineType";
    private final static String MINI_TYPE_GIF = "image/gif";
    private final static String URI = "uri";
    private final static int THREAD_COUNT = 2;


    private int mWindow_width, mWindow_height;
    private int state_height;
    private Bitmap mBitmap;
    private DragImageView mDragImageView;
    private GifView mGifView;
    private Handler mHandler;
    private Intent mIntent;
    private static ExecutorService mExecutorService = Executors
            .newFixedThreadPool(THREAD_COUNT);
    private static boolean isGif;
    private static boolean isRun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.simple_single_image_viewer_activity);
        mDragImageView = (DragImageView) findViewById(R.id.div_main);
        mGifView = (GifView) findViewById(R.id.gif1);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mWindow_width = dm.widthPixels;
        mWindow_height = dm.heightPixels;

        mDragImageView.setmActivity(SimpleSingleImageViewer.this);
        ViewTreeObserver viewTreeObserver = mDragImageView.getViewTreeObserver();
        if (viewTreeObserver != null) {
            viewTreeObserver.addOnGlobalLayoutListener
                    (new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            if (state_height == 0) {
                                Rect frame = new Rect();
                                getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
                                state_height = frame.top;
                                mDragImageView.setScreen_H(mWindow_height - state_height);
                                mDragImageView.setScreen_W(mWindow_width);
                            }
                        }
                    });
        } else {
            showToast(SimpleSingleImageViewer.this, "Sorry, System Error!", true);
        }

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0) {
                    openPngJpg(zoomImage(mBitmap));
                }
            }
        };
    }

    private void openPngJpg(Bitmap bmp) {
        if (bmp == null) {
            showToast(SimpleSingleImageViewer.this, R.string.setting_error_open_image, true);
            dismissProgress();
            SimpleSingleImageViewer.this.finish();
            return;
        }
        //open image/jpg or image/png
        dismissProgress();
        mDragImageView.isInit(true);
        mDragImageView.setVisibility(View.VISIBLE);
        mDragImageView.setImageBitmap(bmp);
    }

    private void openGif(Uri uri) throws FileNotFoundException {
        //open image/gif
        isGif = true;
        InputStream input;
        input = getContentResolver().openInputStream(uri);
        mGifView.setVisibility(View.VISIBLE);
        mGifView.setGifImage(input);
    }

    private Bitmap getCursorBitmap(Intent intent) throws Exception{
        byte[] in = intent.getByteArrayExtra(IMAGE_OS);
        return BitmapFactory.decodeByteArray(in,0,in.length);
    }

    private Bitmap getBitmap(Intent intent) throws IOException {
        return getBitmap(getContentResolver(), Uri.parse(intent.getStringExtra(URI)));
    }

    private Bitmap getBitmap(ContentResolver cr, Uri uri) throws IOException {
        Bitmap bitmap;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inDither = false;
        opts.inPurgeable = true;
        //todo :  this is a bug, all of image be zoom here!
        opts.inSampleSize = 2;
        InputStream input = cr.openInputStream(uri);
        bitmap = BitmapFactory.decodeStream(input, null, opts);
        if (input != null) {
            input.close();
        }
        return bitmap;
    }

    private Bitmap zoomImage(Bitmap bmp) {
        Log.d(TAG, "zoomImage!");

        if (bmp == null) {
            return null;
        }

        int x, y, scale;
        scale = mWindow_width / mWindow_height;
        if ((bmp.getWidth() / bmp.getHeight()) == scale) {
            x = mWindow_width;
            y = mWindow_height;
        } else if (bmp.getWidth() > bmp.getHeight()) {
            x = mWindow_width;
            y = (x * bmp.getHeight()) / bmp.getWidth();
        } else {
            y = mWindow_height;
            x = (y * bmp.getWidth()) / bmp.getHeight();
        }
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) x) / width;
        float scaleHeight = ((float) y) / height;
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bmp, 0, 0, width, height,
                matrix, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
        }
        if (getIntent() == null
                || TextUtils.isEmpty(getIntent().getStringExtra(URI))) {
            Log.e(TAG, "ERROR : The INTENT is empty or URI is empty!");
            Toast.makeText(this, "SYSTEM ERROR : couldn't find file!", Toast.LENGTH_SHORT).show();
            this.finish();
        }
        mIntent = getIntent();
        if (TextUtils.equals(MINI_TYPE_GIF, mIntent.getStringExtra(MINI_TYPE))) {
            try {
                openGif(Uri.parse(getIntent().getStringExtra(URI)));
            } catch (FileNotFoundException e) {
                Log.e(TAG, "ERROR : Open gif was OOM !");
                showToast(this,
                        R.string.setting_error_open_image, true);
                e.printStackTrace();
            }
        } else if (!isRun) {
            showProgressDialog(this,
                    this.getString(R.string.setting_checkupdate_wait_please), false);
            mExecutorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        isRun = true;
                        mBitmap = getCursorBitmap(mIntent);
                        mHandler.sendEmptyMessage(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    protected void clearMemory() {
        if (mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
        }
        isRun = false;
        isGif = false;
        mExecutorService.isShutdown();
        if (null != mGifView) {
            mGifView.free();
            mGifView = null;
        }
        if (null != mDragImageView) {
            mDragImageView = null;
        }
        this.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isGif) {
            clearMemory();
        }
    }

    //We will close this activity when you touch the back key or home key!
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            clearMemory();
        }
        return super.dispatchKeyEvent(event);
    }

     public static void showToast(Context context, int resId, boolean showShort) {
        showToast(context, context.getResources().getString(resId), showShort);
    }

    public static void showToast(Context context, String msg, boolean showShort) {
        Toast toast = Toast.makeText(context, msg, showShort ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG);
        toast.show();
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
}
