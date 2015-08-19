package com.example.ozercanh.screenrecordqa;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.example.ozercanh.screenrecordqa.Interface.VideoCreateListener;
import com.example.ozercanh.screenrecordqa.Model.VideoParams;

import org.bytedeco.javacv.AndroidFrameConverter;
import org.bytedeco.javacv.FFmpegFrameRecorder;

public class VideoCreate extends AsyncTask<VideoParams, String, Void> {

    private VideoCreateListener mListener;

    public void setListener(VideoCreateListener listener){
        this.mListener = listener;
    }

    @Override
    protected Void doInBackground(VideoParams... parameters) {

        VideoParams params = parameters[0];

        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(params.videoPath, params.screenWidth, params.screenHeight);

        recorder.setVideoCodec(13);
        recorder.setFormat("mp4");
        recorder.setFrameRate((double)params.fps);
        recorder.setVideoQuality(1.0D);
        recorder.setVideoBitrate(40000);


        long l = System.currentTimeMillis();
        try {
            recorder.start();
            publishProgress("onPreparing");
        } catch (org.bytedeco.javacv.FrameRecorder.Exception e1) {
            publishProgress("onFail", e1.getMessage());

            e1.printStackTrace();
            return null;
        }

        for (int i = 0; i < params.images.size(); i++) {

            AndroidFrameConverter converter = new AndroidFrameConverter();
            Bitmap bitmap = BitmapFactory.decodeFile(params.images.get(i), new BitmapFactory.Options());
            
            try {
                recorder.record(converter.convert(bitmap));
            } catch (org.bytedeco.javacv.FrameRecorder.Exception e) {
                publishProgress("onFail", e.getMessage());
                e.printStackTrace();
                return null;
            }
        }

        try {
            recorder.stop();
        } catch (org.bytedeco.javacv.FrameRecorder.Exception e) {
            publishProgress("onFail", e.getMessage());
            e.printStackTrace();
            return null;
        }

        publishProgress("onSaved", params.videoPath);

        return null;
    }

    @Override
    protected void onProgressUpdate(String... a){
        if(a[0].equals("onFail")){
            mListener.onFail(a[1]);
        }
        else if(a[0].equals("onSaved")){
            mListener.onSaved(a[1]);
        }
        else if(a[0].equals("onPreparing")){
            mListener.onPreparing();
        }
    }
}