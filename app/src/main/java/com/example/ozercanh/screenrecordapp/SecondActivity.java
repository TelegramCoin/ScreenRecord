package com.example.ozercanh.screenrecordapp;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.VideoView;

public class SecondActivity extends AppCompatActivity {

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        videoView = (VideoView) findViewById(R.id.videoView);

        videoView.setVideoURI(Uri.parse("http://sdp.sezonlukdizi.com/v.asp?v=95ZiG5nVuWUt9onKIfUSIf4qxhCqG7CSR3ZqR3YKTfUauis0ZBLDGfMPYBa0YbMfRBZZCNZ8CqUCnj15b3129BtHxiTW9bMdxc4XYXsanXe="));
        videoView.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_second, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume(){
        super.onResume();
        MainActivity.myRecorder.onResume(this);
    }

    @Override
    public void onPause(){
        super.onPause();
        MainActivity.myRecorder.onPause();
    }
}
