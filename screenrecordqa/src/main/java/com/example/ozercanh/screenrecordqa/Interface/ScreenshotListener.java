package com.example.ozercanh.screenrecordqa.Interface;

/**
 * Created by ozercanh on 18/08/2015.
 */
public interface ScreenshotListener {
    public void onSuccess(int count, String address);
    public void onFail(int count, Exception e);
}
