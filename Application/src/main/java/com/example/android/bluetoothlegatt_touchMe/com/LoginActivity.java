package com.example.android.bluetoothlegatt_touchMe.com;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.android.bluetoothlegatt_touchMe.R;

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText idText = (EditText) findViewById(R.id.editText);
        final EditText passwordText= (EditText) findViewById(R.id.editText2);

        Drawable logo_blue = getResources().getDrawable(R.drawable.ubio_logo_blue);
        Drawable logo_red = getResources().getDrawable(R.drawable.ubio_logo);
        Drawable logo_black = getResources().getDrawable(R.drawable.ubio_logo_black);


        final ImageView ubio_logo = (ImageView) findViewById(R.id.login_ubio_logo);

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
                startActivity(intent_1);

                //finish();

            }
        });

        ImageView s = (ImageView) findViewById(R.id.login_ubio_logo);

        s.setX(100);
        s.setY(100);
        s.setImageDrawable(logo_red);



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
