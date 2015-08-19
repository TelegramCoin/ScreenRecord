package com.example.ozercanh.screenrecordqa;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Toast;

import com.example.ozercanh.screenrecordqa.Interface.RecorderListener;
import com.example.ozercanh.screenrecordqa.Interface.ScreenshotListener;
import com.example.ozercanh.screenrecordqa.Interface.VideoCreateListener;
import com.example.ozercanh.screenrecordqa.Model.SSParams;
import com.example.ozercanh.screenrecordqa.Model.Status;
import com.example.ozercanh.screenrecordqa.Model.VideoParams;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ozercanh on 18/08/2015.
 */
public class Recorder {

    private final Context context;
    private String folderName;
    private ScreenshotListener mCallback;
    private int fps;
    private int count;

    private Status stopFlag;
    private long initiateTime;
    private Activity activity;
    private Point size;
    private ArrayList<String> imageAddresses;
    private ArrayList<Integer> failedImages;
    private RecorderListener mRecorderListener;

    public void onResume(Activity activity){
        this.activity = activity;
        Display display = activity.getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
    }

    public void onPause(){
        activity = null;
    }

    public Recorder(Context context){
        this(context, 10);
    }

    public Recorder(Context context, int fps) {
        Utility.init(context);
        this.context = context;
        this.fps = fps;
    }

    public void setRecorderListener(RecorderListener _listener){
        this.mRecorderListener = _listener;
    }

    private void initializeVariables() {
        this.count = 0;
        this.imageAddresses = new ArrayList<>();
        this.failedImages = new ArrayList<>();
        this.folderName = Utility.getNextFolderName();
        this.mCallback = new ScreenshotListener() {
            @Override
            public void onSuccess(int count, String address) {
                imageAddresses.add(address);
            }

            @Override
            public void onFail(int count, Exception e) {
                failedImages.add(count);
            }
        };

        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + this.folderName);

        if (!folder.exists()) {
            if(!folder.mkdir()){

            }
        }
    }

    public void startRecording(){
        initializeVariables();

        initiateTime = System.currentTimeMillis();
        stopFlag = Status.RECORDING;
        mRecorderListener.onRecording();
        takeScreenshot();
    }

    public void stopRecording(){
        this.stopFlag = Status.STOPPED;

        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + this.folderName);
        String path = new File(folder, "record.mp4").getAbsolutePath();

        VideoCreate instance = new VideoCreate();
        instance.setListener(new VideoCreateListener() {
            @Override
            public void onPreparing() {
                mRecorderListener.onRecording();
            }

            @Override
            public void onSaved(String path) {
                Toast.makeText(context, "done", Toast.LENGTH_SHORT).show();
                clearScreenshots();
                mRecorderListener.onFinish(path);
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(context, "onFail", Toast.LENGTH_SHORT).show();
                mRecorderListener.onSaveFailed();
            }
        });

        VideoParams params = new VideoParams();
        params.images = this.imageAddresses;
        params.fps  =this.fps;
        params.screenWidth = size.x;
        params.screenHeight = size.y;
        params.videoPath = path;

        instance.execute(params);
    }

    public void cancelRecording(){
        stopFlag = Status.CANCELLED;
        clearScreenshots();

        mRecorderListener.onRecordCancel();
    }

    private void clearScreenshots(){

    }

    private void takeScreenshot() {
        if(stopFlag != Status.RECORDING){
            return;
        }

        long startTime = System.currentTimeMillis();

        SSParams _SSParams = new SSParams();
        _SSParams.count = count;
        _SSParams.folderName = folderName;
        try {
            View rootView = activity.findViewById(android.R.id.content).getRootView();
            _SSParams.drawing = Bitmap.createBitmap(size.x, size.y, Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(_SSParams.drawing);
            rootView.draw(canvas);
        }
        catch(Exception e){
            stopRecording();
        }

        new AsyncTask<SSParams, String, String>(){

            @Override
            protected String doInBackground(SSParams... params) {
                SSParams mSSParams = params[0];

                File imagePath = new File(Environment.getExternalStorageDirectory() + File.separator + mSSParams.folderName, "screenshot" + mSSParams.count + ".jpg");
                FileOutputStream fos;
                Log.d("screenshot", imagePath.getAbsolutePath());
                try {
                    fos = new FileOutputStream(imagePath);
                    mSSParams.drawing.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();

                    mCallback.onSuccess(mSSParams.count, imagePath.getAbsolutePath());
                } catch (IOException e) {
                    mCallback.onFail(mSSParams.count, e);
                }

                return null;
            }
        }.execute(_SSParams);

        this.count++;
        long finishTime = System.currentTimeMillis();
        if(finishTime - initiateTime >= 5000){
            stopRecording();
        }
        else{
            int passedTime = (int) (finishTime - startTime);
            int delay = 1000/fps - passedTime;
            if(delay <= 0){
                delay = 0;
            }

            Timer timer = new Timer();

            timer.schedule(new TimerTask() {

                synchronized public void run() {

                    takeScreenshot();
                }

            }, delay);
        }
    }


}
