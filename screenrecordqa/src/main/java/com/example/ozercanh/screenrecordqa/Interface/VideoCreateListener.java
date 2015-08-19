package com.example.ozercanh.screenrecordqa.Interface;

/**
 * Created by ozercanh on 18/08/2015.
 */
public interface VideoCreateListener {

    public void onPreparing();
    public void onSaved(String path);
    public void onFail(String message);
}
