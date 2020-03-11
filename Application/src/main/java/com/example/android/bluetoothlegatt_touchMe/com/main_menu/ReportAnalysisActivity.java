package com.example.android.bluetoothlegatt_touchMe.com.main_menu;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bluetoothlegatt_touchMe.R;
import com.example.android.bluetoothlegatt_touchMe.com.BluetoothLeService;
import com.example.android.bluetoothlegatt_touchMe.com.DeviceControlActivity;
import com.example.android.bluetoothlegatt_touchMe.com.ViewHolder;


import de.blox.treeview.BaseTreeAdapter;
import de.blox.treeview.TreeNode;
import de.blox.treeview.TreeView;

/**
 * Created by GTO on 2020-01-22.
 */

public class ReportAnalysisActivity extends Activity {

    //TODO: BLE variable
    private boolean mConnected = false;
    private TextView mConnectionState;
    private BluetoothLeService mBluetoothLeService;
    private String mDeviceAddress;

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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        TreeView treeView = findViewById(R.id.treeview);

        BaseTreeAdapter adapter = new BaseTreeAdapter<ViewHolder>(this, R.layout.node) {
            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(View view) {
                return new ViewHolder(view);
            }

            @Override
            public void onBindViewHolder(ViewHolder viewHolder, Object data, int position) {
                viewHolder.mTextView1.setText(data.toString());
            }
        };
        treeView.setAdapter(adapter);

        TreeNode rootNode = new TreeNode("Root");
        TreeNode child1 = new TreeNode("Child 1");
        TreeNode child2 = new TreeNode("Child 2");
        TreeNode child3 = new TreeNode("C_3");
        TreeNode child4 = new TreeNode("Child 4");
        TreeNode child5 = new TreeNode("C_5");
        TreeNode child6 = new TreeNode("C_6");
        TreeNode child7 = new TreeNode("C_7");
        TreeNode child8 = new TreeNode("C_8");
        TreeNode child9 = new TreeNode("Child 9");
        TreeNode child10 = new TreeNode("Child 10");
        TreeNode child11 = new TreeNode("C_11");
        TreeNode child12 = new TreeNode("C_12");
        TreeNode child13 = new TreeNode("C_13");
        TreeNode child14 = new TreeNode("C_14");

        // Childs added to root
        rootNode.addChild(child1);
        rootNode.addChild(child2);

        // Childs 3 & 4 added to Child 1
        child1.addChild(child3);
        child1.addChild(child4);

        // Childs 5 & 6 added to Child 2
        child2.addChild(child5);
        child2.addChild(child6);

        // Childs 7, 8 & 9 added to Child 4
        child4.addChild(child7);
        child4.addChild(child8);
        child4.addChild(child9);

        // Childs 10 & 11 added to Child 9
        child9.addChild(child10);
        child9.addChild(child11);

        // Childs 12, 13 & 14 added to Child 10
        child10.addChild(child12);
        child10.addChild(child13);
        child10.addChild(child14);

        adapter.setRootNode(rootNode);
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
