package com.infi.testview;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.view.SurfaceHolder;


public class MainActivity extends Activity {

    private static final String ACTIVITY_TAG="MainActivity";

    private static Bitmap mBitmap;

    private SurfaceViewL sfv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo
                .SCREEN_ORIENTATION_LANDSCAPE);  //horizontal screen

        setContentView(R.layout.activity_main);
        sfv = (SurfaceViewL) findViewById(R.id.sfv01);
        //setContentView(sfv);

        Thread thread = new Thread(sfv);
        thread.run();

        String imgname = "c01.jpg";
        int pic_id = getResourceId(imgname);  //failed
        int pic_id2 = R.drawable.c01;
        mBitmap = BitmapFactory.decodeResource(getResources(), pic_id2);
        if (mBitmap == null){
            Log.e(ACTIVITY_TAG, "Picture load failed");
        }

    }

    private int getResourceId(String imageName) {
        Context ctx = getBaseContext();
        int resId = getResources().getIdentifier(imageName, "drawable", ctx.getPackageName());
        return resId;
    }


    public static class SurfaceViewL extends SurfaceView implements Runnable{
        private Rect mSrcRect,mDestRect;
        private Paint mBitPaint;

        public SurfaceViewL(Context context) {
            super(context);
        }

        public SurfaceViewL(Context context, AttributeSet attrs) {
            super(context,attrs);
        }

        public void initPaint() {
            mBitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mBitPaint.setFilterBitmap(true);
            mBitPaint.setDither(true);
        }

        @Override
        public void run() {
            initPaint();

            SurfaceHolder holder = getHolder();

            holder.addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {

                    Canvas canvas = holder.lockCanvas();

                    int mBitWidth = mBitmap.getWidth();
                    int mBitHeight = mBitmap.getHeight();

                    mSrcRect = new Rect(0, 0, mBitWidth, mBitHeight);
                    mDestRect = new Rect(0, 0, mBitWidth, mBitHeight);

                    canvas.drawBitmap(mBitmap, mSrcRect, mDestRect, mBitPaint);
                    holder.unlockCanvasAndPost(canvas);

                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {

                }
            });

        }

    }


}
