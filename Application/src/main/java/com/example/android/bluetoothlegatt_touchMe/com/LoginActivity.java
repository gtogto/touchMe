package com.example.android.bluetoothlegatt_touchMe.com;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.android.bluetoothlegatt_touchMe.R;

/**
 * Created by GTO on 2020-01-22.
 */

public class LoginActivity extends Activity {

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
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getStandardSize();

        final EditText idText = (EditText) findViewById(R.id.editText);
        final EditText passwordText= (EditText) findViewById(R.id.editText2);

        TextView main_txt = (TextView)findViewById(R.id.main_txt);

        TextView ubio_text = (TextView)findViewById(R.id.ubio_text);
        TextView inno_text = (TextView)findViewById(R.id.inno_text);

        main_txt.setTextSize((float) (standardSize_X / 3)); main_txt.setTextSize((float) (standardSize_Y / 10));

        ubio_text.setTextSize((float) (standardSize_X / 6)); ubio_text.setTextSize((float) (standardSize_Y / 16));
        inno_text.setTextSize((float) (standardSize_X / 12)); inno_text.setTextSize((float) (standardSize_Y / 24));


        Button button2 = (Button) findViewById(R.id.login_btn);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = idText.getText().toString();
                String pass = passwordText.getText().toString();

                Intent intent = new Intent();
                intent.putExtra("id", id);
                intent.putExtra("pass", pass);

                setResult(RESULT_OK, intent);

                Intent intent_1 = new Intent(getApplicationContext(),DeviceScanActivity.class);
                startActivityForResult(intent_1, 201);

                //finish();

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
