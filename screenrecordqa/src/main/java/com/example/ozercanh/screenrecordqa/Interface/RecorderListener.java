package com.example.ozercanh.screenrecordqa.Interface;

/**
 * Created by ozercanh on 19/08/2015.
 */
public interface RecorderListener {
    void onStarted();
    void onRecordFailed();
    void onRecordCancel();
    void onSaving();
    void onSaveFailed();
    void onFinish(String path);
}
