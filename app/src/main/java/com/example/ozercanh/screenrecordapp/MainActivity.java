package com.example.ozercanh.screenrecordapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.ozercanh.screenrecordqa.Interface.RecorderListener;
import com.example.ozercanh.screenrecordqa.Recorder;
import com.example.ozercanh.screenrecordqa.RecorderBuilder;

public class MainActivity extends AppCompatActivity {

    private TextView textView;

    public static Recorder myRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView)findViewById(R.id.textview);

        myRecorder = new RecorderBuilder(this)
                        .setFps(10)
                        .setListener(new RecorderListener() {
                            @Override
                            public void onStarted() {
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
                            public void onFinish(final String path) {
                                textView.setText("finished " + path);
                                textView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(path));
                                        intent.setDataAndType(Uri.parse(path), "video/mp4");
                                        startActivity(intent);
                                    }
                                });
                            }
                        })
                        .build();


        findViewById(R.id.start_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRecorder.startRecording();
            }
        });

        findViewById(R.id.stop_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRecorder.stopRecording();
            }
        });

        findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRecorder.cancelRecording();
            }
        });

        findViewById(R.id.new_window_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(MainActivity.this, SecondActivity.class);
                startActivity(intent);
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
