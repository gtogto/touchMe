package com.example.android.bluetoothlegatt_touchMe.com;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.android.bluetoothlegatt_touchMe.R;

/**
 * Created by GTO on 2020-01-22.
 */

public class start_Activity extends Activity {
    InputMethodManager imm;

    int standardSize_X, standardSize_Y;
    float density;

    public Point getScreenSize(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        return size;
    }

    public void getStandardSize() {
        Point ScreenSize = getScreenSize(this);
        density  = getResources().getDisplayMetrics().density;

        standardSize_X = (int) (ScreenSize.x / density);
        standardSize_Y = (int) (ScreenSize.y / density);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        getStandardSize();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivityForResult(intent, 201);
            }
        });

        TextView main_txt = (TextView)findViewById(R.id.main_txt);

        TextView ubio_text = (TextView)findViewById(R.id.ubio_text);
        TextView inno_text = (TextView)findViewById(R.id.inno_text);

        main_txt.setTextSize((float) (standardSize_X / 3)); main_txt.setTextSize((float) (standardSize_Y / 10));

        ubio_text.setTextSize((float) (standardSize_X / 6)); ubio_text.setTextSize((float) (standardSize_Y / 16));
        inno_text.setTextSize((float) (standardSize_X / 12)); inno_text.setTextSize((float) (standardSize_Y / 24));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            String Id = data.getStringExtra("id");
            String Pass = data.getStringExtra("pass");
            Toast.makeText(getApplicationContext(),"아이디 = " + Id + " 비밀번호 = " + Pass,
                    Toast.LENGTH_LONG).show();
            super.onActivityResult(requestCode, resultCode, data);
        }
        catch (NullPointerException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"value of null",
                    Toast.LENGTH_LONG).show();
        }

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
