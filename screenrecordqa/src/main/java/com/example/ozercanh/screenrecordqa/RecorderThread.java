package com.example.ozercanh.screenrecordqa;

import android.os.Handler;
import android.util.Log;

import com.example.ozercanh.screenrecordqa.Interface.RecorderListener;
import com.example.ozercanh.screenrecordqa.Model.RecorderParams;
import com.example.ozercanh.screenrecordqa.Model.Screenshot;

import org.bytedeco.javacv.AndroidFrameConverter;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FrameRecorder;

import java.util.concurrent.Semaphore;

/**
 * Created by ozercanh on 19/08/2015.
 */
public class RecorderThread extends Thread{

    private final AndroidFrameConverter converter;
    static Semaphore recordingMutex = new Semaphore(1);
    private final RecorderListener listener;
    private final RecorderParams params;
    private final Handler handler;
    private FFmpegFrameRecorder recorder;
    private boolean stopFlag = false;
    private long initiateTime;

    public RecorderThread(RecorderParams params, RecorderListener listener, Handler handler){
        this.converter = new AndroidFrameConverter();

        this.params = params;
        this.listener = listener;
        this.handler = handler;

        initializeRecorder();

    }

    private void initializeRecorder() {
        recorder = new FFmpegFrameRecorder(params.videoPath, params.screenWidth, params.screenHeight);

        recorder.setVideoCodec(13);
        recorder.setFormat("mp4");
        recorder.setFrameRate((double) params.fps);
        recorder.setVideoQuality(1.0D);
        recorder.setVideoBitrate(40000);
    }

    @Override
    public void run(){
        try {
            initiateTime = System.currentTimeMillis();

            recorder.start();

        } catch (FrameRecorder.Exception e) {
            e.printStackTrace();
        }

        while(!stopFlag) {
            readAndWrite();
        }

        while(!params.queue.isEmpty()) {
            readAndWrite();
        }

        try {
            recorder.stop();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onFinish(params.videoPath);
                }
            });

        } catch (FrameRecorder.Exception e) {
            e.printStackTrace();
        }
    }

    private void readAndWrite() {
        try {
            Screenshot ss = params.queue.take();

            if(ss==null || ss.bitmap==null)
                return;
            Log.d("bitmap", "read drawing with size" + ss.bitmap.getByteCount() + "");

            recordingMutex.acquire();
            //recorder.setTimestamp(ss.frameNumber);
            long currentTime = 1000L * (ss.time - initiateTime);
            recorder.setTimestamp(currentTime);
            recorder.record(converter.convert(ss.bitmap));
            recordingMutex.release();

        } catch (org.bytedeco.javacv.FrameRecorder.Exception e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stopRecording() {
        stopFlag = true;
    }
}
