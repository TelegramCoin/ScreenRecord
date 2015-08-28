package com.halilibo.screenrecorddebug.Interface;

/**
 * Created by ozercanh on 19/08/2015.
 */
public interface RecorderListener {
    void onStart();
    void onFail(String reason);
    void onFinish(String path);
}
