package com.halilibo.screenrecorddebug.View;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import com.halilibo.screenrecorddebug.Model.RecordedVideo;
import com.halilibo.screenrecorddebug.Utility;
import com.halilibo.screenrecorddebug.R;

import java.io.File;
import java.util.Date;

public class AfterRecordActivity extends AppCompatActivity {

    private Intent intent;
    private EditText titleEditText;
    private EditText descEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_record);

        setTitle(getString(R.string.save_details));

        intent = getIntent();

        titleEditText = (EditText) findViewById(R.id.title_edittext);
        descEditText = (EditText) findViewById(R.id.desc_edittext);

    }

    public void play(View view) {
        String path = intent.getStringExtra("path");
        Intent playIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(path));
        playIntent.setDataAndType(Uri.fromFile(new File(path)), Utility.getMimeType(path));
        playIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(playIntent);
        }catch(ActivityNotFoundException e){
            Toast.makeText(this, getString(R.string.cant_play), Toast.LENGTH_SHORT).show();
        }
    }

    public void save(View view){
        String path = intent.getStringExtra("path");

        RecordedVideo recordedVideo = new RecordedVideo();
        recordedVideo.setTitle(titleEditText.getText().toString());
        recordedVideo.setDescription(descEditText.getText().toString());
        recordedVideo.setPath(path);
        recordedVideo.setPackageName(getPackageName());
        recordedVideo.setTime(new Date());

        Utility.getDB().addRecordedVideo(recordedVideo);

        Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show();
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_after_record, menu);
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
