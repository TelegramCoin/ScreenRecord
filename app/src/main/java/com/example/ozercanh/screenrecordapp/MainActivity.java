package com.example.ozercanh.screenrecordapp;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.ozercanh.screenrecordqa.RecorderBuilder;
import com.example.ozercanh.screenrecordqa.Interface.RecorderListener;
import com.example.ozercanh.screenrecordqa.Recorder;

public class MainActivity extends AppCompatActivity {

    private Handler handler;
    private Recorder myRecorder;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView)findViewById(R.id.textview);

        myRecorder = new RecorderBuilder(this)
                        .setFps(10)
                        .setListener(new RecorderListener() {
                            @Override
                            public void onRecording() {
                                textView.setText("recording");
                            }

                            @Override
                            public void onRecordFailed() {
                                textView.setText("record failed");
                            }

                            @Override
                            public void onRecordCancel() {
                                textView.setText("record cancelled");
                            }

                            @Override
                            public void onSaving() {
                                textView.setText("saving");
                            }

                            @Override
                            public void onSaveFailed() {
                                textView.setText("save failed");
                            }

                            @Override
                            public void onFinish(String path) {
                                textView.setText("finished " + path);
                            }
                        })
                        .build();


        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRecorder.startRecording();
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        myRecorder.onResume(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
