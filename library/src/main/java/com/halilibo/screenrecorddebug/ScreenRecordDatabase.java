package com.halilibo.screenrecorddebug;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.halilibo.screenrecorddebug.Model.RecordedVideo;

import java.util.ArrayList;
import java.util.Date;

public class ScreenRecordDatabase extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "screenrecord";

    // RecordedVideo table name
    private static final String TABLE_RECORDED_VIDEO = "recorded_video";

    // RecordedVideo Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DESC = "desc";
    private static final String KEY_PATH = "path";
    private static final String KEY_PACKAGE_NAME = "package_name";
    private static final String KEY_TIME = "time";

    public ScreenRecordDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_RecordedVideoS_TABLE = "CREATE TABLE " + TABLE_RECORDED_VIDEO + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TITLE + " TEXT,"
                + KEY_DESC + " TEXT," + KEY_PATH + " TEXT,"
                + KEY_PACKAGE_NAME + " TEXT, " + KEY_TIME + " INTEGER" + ")";
        db.execSQL(CREATE_RecordedVideoS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORDED_VIDEO);

        // Create tables again
        onCreate(db);
    }

    // Adding new RecordedVideo
    public void addRecordedVideo(RecordedVideo recordedVideo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, recordedVideo.getTitle());
        values.put(KEY_DESC, recordedVideo.getDescription());
        values.put(KEY_PATH, recordedVideo.getPath());
        values.put(KEY_PACKAGE_NAME, recordedVideo.getPackageName());
        values.put(KEY_TIME, recordedVideo.getTime().getTime());

        // Inserting Row
        db.insert(TABLE_RECORDED_VIDEO, null, values);
        db.close(); // Closing database connection
    }

    public RecordedVideo getRecordedVideo(int id) {
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_RECORDED_VIDEO + " WHERE " + KEY_ID + "=" +id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {

                RecordedVideo recordedVideo = new RecordedVideo();
                recordedVideo.setId(Integer.parseInt(cursor.getString(0)));
                recordedVideo.setTitle(cursor.getString(1));
                recordedVideo.setDescription(cursor.getString(2));
                recordedVideo.setPath(cursor.getString(3));
                recordedVideo.setPackageName(cursor.getString(4));
                recordedVideo.setTime(new Date(Long.parseLong(cursor.getString(5))));
                // Adding RecordedVideo to list
                return recordedVideo;
        }

        // return RecordedVideo list
        return null;
    }

    public ArrayList<RecordedVideo> getAllRecordedVideos() {
        ArrayList<RecordedVideo> recordedVideoList = new ArrayList<RecordedVideo>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_RECORDED_VIDEO;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                RecordedVideo recordedVideo = new RecordedVideo();
                recordedVideo.setId(Integer.parseInt(cursor.getString(0)));
                recordedVideo.setTitle(cursor.getString(1));
                recordedVideo.setDescription(cursor.getString(2));
                recordedVideo.setPath(cursor.getString(3));
                recordedVideo.setPackageName(cursor.getString(4));
                recordedVideo.setTime(new Date(Long.parseLong(cursor.getString(5))));
                // Adding RecordedVideo to list
                recordedVideoList.add(recordedVideo);
            } while (cursor.moveToNext());
        }

        // return RecordedVideo list
        return recordedVideoList;
    }

    public int getRecordedVideosCount() {
        String countQuery = "SELECT  * FROM " + TABLE_RECORDED_VIDEO;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    public int updateRecordedVideo(RecordedVideo recordedVideo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, recordedVideo.getTitle());
        values.put(KEY_DESC, recordedVideo.getDescription());
        values.put(KEY_PATH, recordedVideo.getPath());
        values.put(KEY_PACKAGE_NAME, recordedVideo.getPackageName());
        values.put(KEY_TIME, recordedVideo.getTime().getTime());

        // updating row
        return db.update(TABLE_RECORDED_VIDEO, values, KEY_ID + " = ?",
                new String[]{String.valueOf(recordedVideo.getId())});
    }

    public void deleteRecordedVideo(RecordedVideo recordedVideo) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RECORDED_VIDEO, KEY_ID + " = ?",
                new String[]{String.valueOf(recordedVideo.getId())});
        db.close();
    }
}