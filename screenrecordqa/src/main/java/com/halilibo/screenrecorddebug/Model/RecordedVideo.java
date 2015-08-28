package com.halilibo.screenrecorddebug.Model;

import java.util.Date;

/**
 * Created by absolute on 27.08.2015.
 */
public class RecordedVideo {
    //private variables
    private int _id;
    private String _title;
    private String _desc;
    private String _path;
    private String _package_name;
    private Date _time;

    // Empty constructor
    public RecordedVideo(){

    }

    public RecordedVideo(String title, String desc, String path, String packageName, Date time){
        this._title = title;
        this._desc = desc;
        this._path = path;
        this._package_name = packageName;
        this._time = time;
    }

    public String getTitle() {
        return _title;
    }

    public void setTitle(String _title) {
        this._title = _title;
    }

    public String getDescription() {
        return _desc;
    }

    public void setDescription(String _desc) {
        this._desc = _desc;
    }

    public String getPath() {
        return _path;
    }

    public void setPath(String _path) {
        this._path = _path;
    }

    public String getPackageName() {
        return _package_name;
    }

    public void setPackageName(String _package_name) {
        this._package_name = _package_name;
    }

    public Date getTime() {
        return _time;
    }

    public void setTime(Date _time) {
        this._time = _time;
    }

    public int getId() {
        return _id;
    }

    public void setId(int _id) {
        this._id = _id;
    }
}
