package com.example.ozercanh.screenrecordqa;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.View;

import com.example.ozercanh.screenrecordqa.Interface.RecorderListener;
import com.example.ozercanh.screenrecordqa.Model.RecorderParams;
import com.example.ozercanh.screenrecordqa.Model.Screenshot;
import com.example.ozercanh.screenrecordqa.Model.Status;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Halil Ozercan on 18/08/2015.
 */
public class Recorder {

    private int fps;
    private int count;

    private Activity activity;
    private Status statusFlag;
    private long initiateTime;
    private Point size;
    private RecorderListener mRecorderListener;

    private RecorderThread recorderThread;
    private RecorderParams recorderParams;

    public Recorder(Context context){
        Utility.init(context);
    }

    /**
     * Takes an activity and assumes it is the currently running activity.
     * This activity provides a rootview for screenshot and also size for the video.
     * This method should be called from {@link Activity} onResume.
     */
    public void onResume(Activity activity){
        this.activity = activity;
        Display display = activity.getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        Log.d("injection", "Recorder will work with " + activity.getComponentName() + " " + size.x + " " + size.y);
    }

    /**
     * Makes the current activity null.
     */
    public void onPause(){
        activity = null;
    }

    /**
     * Sets the maximum frame per seconds for the video.
     * @param fps Desired maximum fps. Actual FPS can be lower due to memory problems.
     */
    public void setFps(int fps) {
        this.fps = fps;
    }

    /**
     * Sets a recorder listener for callbacks.
     * @param _listener A {@link RecorderListener} instance to use for callbacks.
     */
    public void setRecorderListener(RecorderListener _listener){
        this.mRecorderListener = _listener;
    }

    /**
     * Initializes variables before the recording starts.
     */
    private void initializeVariables() {
        String recordName = Utility.getNextRecordName();
        Handler handler = new Handler();
        File videoFile = new File(Environment.getExternalStorageDirectory(), recordName);
        this.count = 0;

        recorderParams = new RecorderParams();
        recorderParams.videoPath = videoFile.getPath();
        recorderParams.fps = fps;
        recorderParams.screenWidth = size.x;
        recorderParams.screenHeight = size.y;
        recorderParams.queue = new ConcurrentLinkedQueue<>();

        recorderThread = new RecorderThread(recorderParams, mRecorderListener, handler);

    }

    /**
     * Starts the recording instantly.
     * This method should be called from outside.
     */
    public void startRecording(){
        initializeVariables();

        // Hold the starting time. If recording time goes over a constant limit, it'll be cancelled.
        initiateTime = System.currentTimeMillis();

        recorderThread.start();
        statusFlag = Status.RECORDING;
        saveFrame();
    }

    /**
     * Starts the stopping process.
     * Recording does not finish immediately. This will only stop taking new screenshots.
     * Queue should be cleaned first.
     */
    public void stopRecording(){
        this.statusFlag = Status.STOPPED;
        recorderThread.stopRecording(true);
    }

    /**
     * Stops the recording instantly.
     * Calling this method cancels all recording tasks and removes the video from files.
     */
    public void cancelRecording(){
        statusFlag = Status.CANCELLED;
        recorderThread.stopRecording(false);
    }

    /**
     * Initiates the screenshot taking process.
     * This method calls itself repeatedly. Delays are calculated according to maximum FPS
     */
    private void saveFrame() {
        // Stop taking new screenshots
        if(statusFlag != Status.RECORDING){
            Log.d("recording", "takescreenshot status is not recording");
            return;
        }

        // This object will be sent to video recording thread.
        Screenshot ss = new Screenshot();
        ss.time = System.currentTimeMillis();
        ss.frameNumber = count;

        // Hold the starting time to calculate how long does it take to draw a screenshot.
        long startTime = System.currentTimeMillis();

        Bitmap drawing = null;
        try {
            // Draw the view over a canvas whose bitmap reference is known to us.
            View rootView = activity.findViewById(android.R.id.content).getRootView();
            drawing = Bitmap.createBitmap(size.x, size.y, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(drawing);
            rootView.draw(canvas);
        }
        catch(Error | Exception ignored){
            //TODO : Make a log!
        }

        ss.bitmap = drawing;
        recorderParams.queue.add(ss);
        count++;

        long finishTime = System.currentTimeMillis();

        // Over time should stop recording.
        if(finishTime - initiateTime >= 100000){
            stopRecording();
        }
        else{
            int passedTime = (int) (finishTime - startTime);
            int delay = 1000/fps - passedTime;
            // If we are not able to achieve the maximum fps, at least continue with what we have!
            if(delay <= 0){
                delay = 0;
            }

            Timer timer = new Timer();

            timer.schedule(new TimerTask() {

                synchronized public void run() {

                    saveFrame();
                }

            }, delay);

        }
    }


    public boolean isRecording() {
        if(statusFlag == Status.RECORDING)
            return true;
        else
            return false;
    }
}
