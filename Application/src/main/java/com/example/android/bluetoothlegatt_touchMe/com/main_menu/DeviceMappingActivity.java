package com.example.android.bluetoothlegatt_touchMe.com.main_menu;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

import com.example.android.bluetoothlegatt_touchMe.R;

public class DeviceMappingActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_mapping);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //final ImageView back_btn = (ImageView) findViewById(R.id.back_btn);

    }

    public void onClickBack (View v) {
        onBackPressed();

    }
}
