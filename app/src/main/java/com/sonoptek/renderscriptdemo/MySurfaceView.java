package com.sonoptek.renderscriptdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

/**
 * Created by zhangchongshan on 2020/11/24.
 */
class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Canvas mCanvas;
    private Paint mPaint;
    private Path mPath;
    private MyAsyncTask mAsyncTask;

    int theWidth,theHeight;
    public MySurfaceView(Context context) {
        super(context);
        mHolder=getHolder();
        mHolder.addCallback(this);

        mPaint=new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(5);
        mPaint.setStyle(Paint.Style.STROKE);




        /*new Thread(){
            @Override
            public void run() {
                super.run();

                boolean quit=false;
                Random random=new Random();
                while (!quit) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Log.e("TTTT", "draw: ");
                    int x=random.nextInt(500);
                    mPath.lineTo(x,x);
                    draw();
                }
            }
        }.start();*/
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);



    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        theWidth = getWidth();
        theHeight=getHeight();
        mPath=new Path();
        mPath.moveTo(theWidth/2,theHeight/2);

        mAsyncTask=new MyAsyncTask();
        mAsyncTask.execute(1);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        mAsyncTask.cancel(true);
    }

    void draw(){

        mCanvas=mHolder.lockCanvas();
        mCanvas.drawColor(Color.WHITE);
        mCanvas.drawPath(mPath,mPaint);

        mHolder.unlockCanvasAndPost(mCanvas);
    }

    private class MyAsyncTask extends AsyncTask<Integer,Integer,Integer>{

        @Override
        protected Integer doInBackground(Integer... integers) {
            Random random=new Random();
            while (!isCancelled()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int x = random.nextInt(theWidth);
                int y = random.nextInt(theHeight);
                mPath.lineTo(x,y);
                draw();
            }
            return null;
        }
    }
}
