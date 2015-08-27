package com.example.ozercanh.screenrecordqa;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AfterRecordActivity extends AppCompatActivity {

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_record);

        setTitle(getString(R.string.what_to_do));

        intent = getIntent();

    }

    public void playFromNotification(View view) {
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

    public void shareFromNotification(View view) {
        String path = intent.getStringExtra("path");
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
        shareIntent.setType("video/mp4");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());

        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.screen_record) + " " + currentDateandTime).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
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
