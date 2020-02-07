package com.example.android.bluetoothlegatt_touchMe.com.main_menu;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.media.Image;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bluetoothlegatt_touchMe.R;
import com.example.android.bluetoothlegatt_touchMe.com.BluetoothLeService;
import com.example.android.bluetoothlegatt_touchMe.com.DeviceControlActivity;

/**
 * Created by GTO on 2020-01-22.
 */

public class RunActivity extends Activity {

    //TODO: BLE variable
    private boolean mConnected = false;
    private TextView mConnectionState;
    private BluetoothLeService mBluetoothLeService;
    private String mDeviceAddress;

    public static ImageView node_imageView01, node_imageView02, node_imageView03, node_imageView04, node_imageView05, node_imageView06;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.run_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        node_imageView01 = (ImageView) findViewById(R.id.node1);
        node_imageView02 = (ImageView) findViewById(R.id.node2);
        node_imageView03 = (ImageView) findViewById(R.id.node3);
        node_imageView04 = (ImageView) findViewById(R.id.node4);
        node_imageView05 = (ImageView) findViewById(R.id.node5);
        node_imageView06 = (ImageView) findViewById(R.id.node6);

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

        System.out.println("By. Run activity : "+ hex_value);
        StringBuilder sb = new StringBuilder(packet.length * 2);

        System.out.println("By. Run HEX To ASCII : "+ hexToASCII(hex_value));

        Toast tMsg = Toast.makeText(RunActivity.this, hexToASCII(hex_value), Toast.LENGTH_SHORT);
        tMsg.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout view = (LinearLayout) tMsg.getView();
        tMsg.show();

        String choice_node = hexToASCII(hex_value).substring(13,14);

        asciiToHex(choice_node);       // is 1

        String node1 = "1";
        String node2 = "2";
        String node3 = "3";
        String node4 = "4";
        String node5 = "5";
        String node6 = "6";

        //System.out.println(" choice num = " + choice_node);

        if (choice_node.equals(node1)) {
            node_imageView01.setImageResource(R.drawable.clicked_pink_circle_button);
            System.out.println("node 1 = " + node1 + " " + " choice num = " + choice_node);
        }
        else node_imageView01.setImageResource(R.drawable.green_circle_button1);

        if (choice_node.equals(node2)) {
            node_imageView02.setImageResource(R.drawable.clicked_pink_circle_button);
            System.out.println("node 2 = " + node2 + " " + " choice num = " + choice_node);
        }
        else node_imageView02.setImageResource(R.drawable.green_circle_button1);

        if (choice_node.equals(node3)) {
            node_imageView03.setImageResource(R.drawable.clicked_pink_circle_button);
            System.out.println("node 3 = " + node3 + " " + " choice num = " + choice_node);
        }
        else node_imageView03.setImageResource(R.drawable.green_circle_button1);

        if (choice_node.equals(node4)) {
            node_imageView04.setImageResource(R.drawable.clicked_pink_circle_button);
            System.out.println("node 4 = " + node4 + " " + " choice num = " + choice_node);
        }
        else node_imageView04.setImageResource(R.drawable.green_circle_button1);

        if (choice_node.equals(node5)) {
            node_imageView05.setImageResource(R.drawable.clicked_pink_circle_button);
            System.out.println("node 5 = " + node5 + " " + " choice num = " + choice_node);
        }
        else node_imageView05.setImageResource(R.drawable.green_circle_button1);

        if (choice_node.equals(node6)) {
            node_imageView06.setImageResource(R.drawable.clicked_pink_circle_button);
            System.out.println("node 6 = " + node6 + " " + " choice num = " + choice_node);
        }
        else node_imageView06.setImageResource(R.drawable.green_circle_button1);



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
