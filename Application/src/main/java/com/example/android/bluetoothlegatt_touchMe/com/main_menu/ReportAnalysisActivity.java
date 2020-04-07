package com.example.android.bluetoothlegatt_touchMe.com.main_menu;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bluetoothlegatt_touchMe.R;
import com.example.android.bluetoothlegatt_touchMe.com.BluetoothLeService;
import com.example.android.bluetoothlegatt_touchMe.com.DeviceControlActivity;
import com.example.android.bluetoothlegatt_touchMe.com.ViewHolder;

/**
 * Created by GTO on 2020-01-22.
 */

public class ReportAnalysisActivity extends Activity {

    //TODO: BLE variable
    private boolean mConnected = false;
    private TextView mConnectionState;
    private BluetoothLeService mBluetoothLeService;
    private String mDeviceAddress;

    int standardSize_X, standardSize_Y;
    float density;

    LinearLayout mainLayout;
    Resources res;
    Animation growAnim;


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

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            }

            else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                DeviceControlActivity.packet = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                displayData(DeviceControlActivity.packet);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_analysis);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 세로모드
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // 가로모드
        getStandardSize();

        TextView setup_txt = (TextView)findViewById(R.id.setup_txt);        // dynamic layout font
        setup_txt.setTextSize((float) (standardSize_X / 8)); setup_txt.setTextSize((float) (standardSize_Y / 18));      // dynamic layout font

        res = getResources();
        growAnim = AnimationUtils.loadAnimation(this, R.anim.grow);
        mainLayout = (LinearLayout)findViewById(R.id.mainLayout);

        addItem("Red", 180, Color.RED);
        addItem("Green", 290, Color.GREEN);
        addItem("Blue", 140, Color.BLUE);
        addItem("Yellow", 170, Color.YELLOW);
        addItem("Pink", 260, Color.DKGRAY);  // pink
        addItem("Sky", 20, Color.CYAN);     // sky

    }

    private void addItem(String name, int value, int color) {
        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);


        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);


        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);


        // 텍스트뷰 추가
        TextView textView = new TextView(this);
        textView.setText(name);
        params.width = 180;
        params.setMargins(0, 4, 0, 4);  // textView layout control
        itemLayout.addView(textView, params);


        // 프로그레스바 추가
        ProgressBar proBar = new ProgressBar(this, null,

                android.R.attr.progressBarStyleHorizontal);
        proBar.setIndeterminate(false);
        proBar.setMax(300);
        proBar.setProgress(300);
        if(color == Color.RED)
            proBar.setProgressDrawable(

                    getResources().getDrawable(R.drawable.progressbar_color_red));
        else if(color == Color.GREEN)
            proBar.setProgressDrawable(

                    getResources().getDrawable(R.drawable.progressbar_color_green));
        else if(color == Color.BLUE)
            proBar.setProgressDrawable(

                    getResources().getDrawable(R.drawable.progressbar_color_blue));

        else if(color == Color.YELLOW)
            proBar.setProgressDrawable(

                    getResources().getDrawable(R.drawable.progressbar_color_yellow));

        else if(color == Color.DKGRAY)  // PINK
            proBar.setProgressDrawable(

                    getResources().getDrawable(R.drawable.progressbar_color_pink));

        else if(color == Color.CYAN)    // SKY
            proBar.setProgressDrawable(

                    getResources().getDrawable(R.drawable.progressbar_color_sky));


        proBar.setAnimation(growAnim);


        params2.height = 3;
        params2.width = value * 3;
        params2.gravity = Gravity.CENTER_VERTICAL;
        itemLayout.addView(proBar, params2);

        mainLayout.addView(itemLayout, params3);
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);


        Toast.makeText(getApplicationContext(),
                "onWindowFocusChanged : " + hasFocus, Toast.LENGTH_SHORT).show();


        if(hasFocus) {
            growAnim.start();
        } else {
            growAnim.reset();
        }
    }

    //TODO BLE Packet receive
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            //Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    public void displayData(byte[] packet) {

        getStringPacket(DeviceControlActivity.packet);

    }

    private String getStringPacket(byte[] packet) {

        String hex_value = "";

        for (byte b : packet) {  						//readBuf -> Hex
            hex_value += Integer.toString((b & 0xF0) >> 4, 16);
            hex_value += Integer.toString(b & 0x0F, 16);
        }

        System.out.println("By. Report&Analysis activity : "+ hex_value);
        StringBuilder sb = new StringBuilder(packet.length * 2);

        System.out.println("By. Report&Analysis HEX To ASCII : "+ hexToASCII(hex_value));

        Toast tMsg = Toast.makeText(ReportAnalysisActivity.this, hexToASCII(hex_value), Toast.LENGTH_SHORT);
        tMsg.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout view = (LinearLayout) tMsg.getView();
        tMsg.show();

        return sb.toString();
    }

    private static String hexToASCII(String hexValue)
    {
        StringBuilder output = new StringBuilder("");
        for (int i = 0; i < hexValue.length(); i += 2)
        {
            String str = hexValue.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        return output.toString();
    }

    private static String asciiToHex(String asciiValue)
    {
        char[] chars = asciiValue.toCharArray();
        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < chars.length; i++)
        {
            hex.append(Integer.toHexString((int) chars[i]));
        }
        return hex.toString();
    }


    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_SEND_PACKET);
        return intentFilter;
    }

    public void onClickBack (View v) {
        onBackPressed();

    }
}
