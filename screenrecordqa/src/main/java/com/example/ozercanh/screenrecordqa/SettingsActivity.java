package com.example.ozercanh.screenrecordqa;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onStop(){
        super.onStop();
        Intent intent = new Intent(this, RecordService.class);
        intent.setAction("update_settings");
        startService(intent);
    }
}