package com.example.android.bluetoothlegatt_touchMe.com.main_menu;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bluetoothlegatt_touchMe.R;
import com.example.android.bluetoothlegatt_touchMe.com.BluetoothLeService;
import com.example.android.bluetoothlegatt_touchMe.com.DeviceControlActivity;

/**
 * Created by GTO on 2020-01-22.
 */

public class NodeScanningActivity extends Activity {

    //TODO: BLE variable
    private boolean mConnected = false;
    private TextView mConnectionState;
    private BluetoothLeService mBluetoothLeService;
    private String mDeviceAddress;

    public static TextView node1_txt, node2_txt, node3_txt, node4_txt, node5_txt, node6_txt;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.node_scanning);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        node1_txt = (TextView)findViewById(R.id.node1);
        node2_txt = (TextView)findViewById(R.id.node2);
        node3_txt = (TextView)findViewById(R.id.node3);
        node4_txt = (TextView)findViewById(R.id.node4);
        node5_txt = (TextView)findViewById(R.id.node5);
        node6_txt = (TextView)findViewById(R.id.node6);
        getStandardSize();

        TextView setup_txt = (TextView)findViewById(R.id.setup_txt);

        setup_txt.setTextSize((float) (standardSize_X / 8)); setup_txt.setTextSize((float) (standardSize_Y / 18));


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

        System.out.println("By. NodeScanning activity : "+ hex_value);
        StringBuilder sb = new StringBuilder(packet.length * 2);

        System.out.println("By. NodeScanning HEX To ASCII : "+ hexToASCII(hex_value));

        Toast tMsg = Toast.makeText(NodeScanningActivity.this, hexToASCII(hex_value), Toast.LENGTH_SHORT);
        tMsg.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout view = (LinearLayout) tMsg.getView();
        tMsg.show();

        String sub_node1 = hexToASCII(hex_value).substring(7,8);
        String sub_node2 = hexToASCII(hex_value).substring(8,9);
        String sub_node3 = hexToASCII(hex_value).substring(9,10);
        String sub_node4 = hexToASCII(hex_value).substring(10,11);
        String sub_node5 = hexToASCII(hex_value).substring(11,12);
        String sub_node6 = hexToASCII(hex_value).substring(12,13);

        node1_txt.setText(sub_node1);
        node2_txt.setText(sub_node2);
        node3_txt.setText(sub_node3);
        node4_txt.setText(sub_node4);
        node5_txt.setText(sub_node5);
        node6_txt.setText(sub_node6);

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
