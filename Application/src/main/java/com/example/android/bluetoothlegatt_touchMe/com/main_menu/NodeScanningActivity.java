package com.example.android.bluetoothlegatt_touchMe.com.main_menu;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bluetoothlegatt_touchMe.R;
import com.example.android.bluetoothlegatt_touchMe.com.BluetoothLeService;
import com.example.android.bluetoothlegatt_touchMe.com.DeviceControlActivity;
import com.example.android.bluetoothlegatt_touchMe.com.SampleGattAttributes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.android.bluetoothlegatt_touchMe.com.BluetoothLeService.EXTRA_DATA;
import static com.example.android.bluetoothlegatt_touchMe.com.DeviceControlActivity.action;
import static com.example.android.bluetoothlegatt_touchMe.com.DeviceControlActivity.mGattCharacteristics;
import static com.example.android.bluetoothlegatt_touchMe.com.DeviceControlActivity.packet;

/**
 * Created by GTO on 2020-01-22.
 */

public class NodeScanningActivity extends Activity {
    //TODO: BLE variable
    private final static String TAG = "DCA";

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    //TODO: BLE variable
    private TextView mConnectionState;
    private SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm:ss");
    public static final int DUMP = -1;

    public String mDeviceName;
    public String mDeviceAddress;
    public static BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";
    //private TextView node1, node2, node3, node4, node5, node6;
    private Button master, nodeBtn1, nodeBtn2, nodeBtn3, nodeBtn4, nodeBtn5, nodeBtn6, nodeBtn7, nodeBtn8, nodeBtn9;

    int standardSize_X, standardSize_Y;
    float density;

    public String node_number;
    public static int node_battery;
    public static int node_count;
    public static String node_B;

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

    private final ServiceConnection mServiceConnection = new ServiceConnection() {


        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {

            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {

                displayGattServices(mBluetoothLeService.getSupportedGattServices());
                System.out.println("displayGattServices(mBluetoothLeService.getSupportedGattServices()); OK ");

                final BluetoothGattCharacteristic notifyCharacteristic = getNottifyCharacteristic();
                if (notifyCharacteristic == null) {
                    Toast.makeText(getApplication(), "gatt_services can not supported", Toast.LENGTH_SHORT).show();
                    mConnected = false;
                    return;
                }
                final int charaProp = notifyCharacteristic.getProperties();
                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                    mBluetoothLeService.setCharacteristicNotification(
                            notifyCharacteristic, true);
                }

            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                packet = intent.getByteArrayExtra(EXTRA_DATA);
                displayData(packet);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.node_scanning);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getStandardSize();

        //node1 = (TextView)findViewById(R.id.node1);        node2 = (TextView)findViewById(R.id.node2);
        //node3 = (TextView)findViewById(R.id.node3);        node4 = (TextView)findViewById(R.id.node4);
        //node5 = (TextView)findViewById(R.id.node5);        node6 = (TextView)findViewById(R.id.node6);

        nodeBtn1 = (Button)findViewById(R.id.nodeBtn1);        nodeBtn2 = (Button)findViewById(R.id.nodeBtn2);
        nodeBtn3 = (Button)findViewById(R.id.nodeBtn3);        nodeBtn4 = (Button)findViewById(R.id.nodeBtn4);
        nodeBtn5 = (Button)findViewById(R.id.nodeBtn5);        nodeBtn6 = (Button)findViewById(R.id.nodeBtn6);
        nodeBtn7 = (Button)findViewById(R.id.nodeBtn7);        nodeBtn8 = (Button)findViewById(R.id.nodeBtn8);
        nodeBtn9 = (Button)findViewById(R.id.nodeBtn9);        master = (Button)findViewById(R.id.master);

        getStandardSize();

        TextView setup_txt = (TextView)findViewById(R.id.setup_txt);        // dynamic layout font
        setup_txt.setTextSize((float) (standardSize_X / 8)); setup_txt.setTextSize((float) (standardSize_Y / 18));      // dynamic layout font

        master.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mBluetoothLeService.writeGattCharacteristic(getWriteGattCharacteristic(), CMD_NODE_SCAN);
                byte[] cmd_bytes = new byte[8];
                cmd_bytes[0] = 0x3C;
                cmd_bytes[1] = 0x53;
                cmd_bytes[2] = 0x30;
                cmd_bytes[3] = 0x00;
                cmd_bytes[4] = 0x00;
                cmd_bytes[5] = 0x00;
                cmd_bytes[6] = 0x00;
                cmd_bytes[7] = 0x3E;

                mBluetoothLeService.writeCharacteristic(getWriteGattCharacteristic(), cmd_bytes);
                master.setBackgroundResource(R.drawable.orange_circle_button);
                nodeBtn1.setVisibility(View.VISIBLE);

                //master.setText("MASTER Battery " + node_battery + "%");

            }
        });

        nodeBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mBluetoothLeService.writeGattCharacteristic(getWriteGattCharacteristic(), CMD_NODE_SCAN);
                byte[] cmd_bytes = new byte[8];
                cmd_bytes[0] = 0x3C;
                cmd_bytes[1] = 0x53;
                cmd_bytes[2] = 0x31;
                cmd_bytes[3] = 0x00;
                cmd_bytes[4] = 0x00;
                cmd_bytes[5] = 0x00;
                cmd_bytes[6] = 0x00;
                cmd_bytes[7] = 0x3E;
                mBluetoothLeService.writeCharacteristic(getWriteGattCharacteristic(), cmd_bytes);

                nodeBtn1.setBackgroundResource(R.drawable.green_circle_button);

                //nodeBtn1.setText("Node 1 Battery " + node_battery + "%");
                nodeBtn2.setVisibility(View.VISIBLE);

                //management_node();
            }
        });

        nodeBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mBluetoothLeService.writeGattCharacteristic(getWriteGattCharacteristic(), CMD_NODE_SCAN);
                byte[] cmd_bytes = new byte[8];
                cmd_bytes[0] = 0x3C;
                cmd_bytes[1] = 0x53;
                cmd_bytes[2] = 0x32;
                cmd_bytes[3] = 0x00;
                cmd_bytes[4] = 0x00;
                cmd_bytes[5] = 0x00;
                cmd_bytes[6] = 0x00;
                cmd_bytes[7] = 0x3E;
                mBluetoothLeService.writeCharacteristic(getWriteGattCharacteristic(), cmd_bytes);

                nodeBtn2.setBackgroundResource(R.drawable.green_circle_button);

                //nodeBtn2.setText("Node 2 Battery " + node_battery + "%");

                //management_node();
                nodeBtn3.setVisibility(View.VISIBLE);

            }
        });

        nodeBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mBluetoothLeService.writeGattCharacteristic(getWriteGattCharacteristic(), CMD_NODE_SCAN);
                byte[] cmd_bytes = new byte[8];
                cmd_bytes[0] = 0x3C;
                cmd_bytes[1] = 0x53;
                cmd_bytes[2] = 0x33;
                cmd_bytes[3] = 0x00;
                cmd_bytes[4] = 0x00;
                cmd_bytes[5] = 0x00;
                cmd_bytes[6] = 0x00;
                cmd_bytes[7] = 0x3E;
                mBluetoothLeService.writeCharacteristic(getWriteGattCharacteristic(), cmd_bytes);

                nodeBtn3.setBackgroundResource(R.drawable.green_circle_button);

                //nodeBtn3.setText("Node 3 Battery " + node_battery + "%");
                nodeBtn4.setVisibility(View.VISIBLE);

                //management_node();
            }
        });

        nodeBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mBluetoothLeService.writeGattCharacteristic(getWriteGattCharacteristic(), CMD_NODE_SCAN);
                byte[] cmd_bytes = new byte[8];
                cmd_bytes[0] = 0x3C;
                cmd_bytes[1] = 0x53;
                cmd_bytes[2] = 0x34;
                cmd_bytes[3] = 0x00;
                cmd_bytes[4] = 0x00;
                cmd_bytes[5] = 0x00;
                cmd_bytes[6] = 0x00;
                cmd_bytes[7] = 0x3E;
                mBluetoothLeService.writeCharacteristic(getWriteGattCharacteristic(), cmd_bytes);

                nodeBtn4.setBackgroundResource(R.drawable.green_circle_button);

                //nodeBtn4.setText("Node 4 Battery " + node_battery + "%");
                nodeBtn5.setVisibility(View.VISIBLE);

                //management_node();
            }
        });

        nodeBtn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mBluetoothLeService.writeGattCharacteristic(getWriteGattCharacteristic(), CMD_NODE_SCAN);
                byte[] cmd_bytes = new byte[8];
                cmd_bytes[0] = 0x3C;
                cmd_bytes[1] = 0x53;
                cmd_bytes[2] = 0x35;
                cmd_bytes[3] = 0x00;
                cmd_bytes[4] = 0x00;
                cmd_bytes[5] = 0x00;
                cmd_bytes[6] = 0x00;
                cmd_bytes[7] = 0x3E;
                mBluetoothLeService.writeCharacteristic(getWriteGattCharacteristic(), cmd_bytes);

                nodeBtn5.setBackgroundResource(R.drawable.green_circle_button);
                nodeBtn6.setVisibility(View.VISIBLE);

                //nodeBtn5.setText("Node 5 Battery " + node_battery + "%");

                //management_node();
            }
        });

        nodeBtn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mBluetoothLeService.writeGattCharacteristic(getWriteGattCharacteristic(), CMD_NODE_SCAN);
                byte[] cmd_bytes = new byte[8];
                cmd_bytes[0] = 0x3C;
                cmd_bytes[1] = 0x53;
                cmd_bytes[2] = 0x36;
                cmd_bytes[3] = 0x00;
                cmd_bytes[4] = 0x00;
                cmd_bytes[5] = 0x00;
                cmd_bytes[6] = 0x00;
                cmd_bytes[7] = 0x3E;
                mBluetoothLeService.writeCharacteristic(getWriteGattCharacteristic(), cmd_bytes);

                nodeBtn6.setBackgroundResource(R.drawable.green_circle_button);
                nodeBtn7.setVisibility(View.VISIBLE);

                //nodeBtn6.setText("Node 6 Battery " + node_battery + "%");

                //management_node();
            }
        });

        nodeBtn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] cmd_bytes = new byte[8];
                cmd_bytes[0] = 0x3C;
                cmd_bytes[1] = 0x53;
                cmd_bytes[2] = 0x37;
                cmd_bytes[3] = 0x00;
                cmd_bytes[4] = 0x00;
                cmd_bytes[5] = 0x00;
                cmd_bytes[6] = 0x00;
                cmd_bytes[7] = 0x3E;
                mBluetoothLeService.writeCharacteristic(getWriteGattCharacteristic(), cmd_bytes);
                //nodeBtn6.setText("Node 6 Battery " + node_battery + "%");
                //management_node();
                nodeBtn7.setBackgroundResource(R.drawable.green_circle_button);
                nodeBtn8.setVisibility(View.VISIBLE);

            }
        });
        nodeBtn8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] cmd_bytes = new byte[8];
                cmd_bytes[0] = 0x3C;
                cmd_bytes[1] = 0x53;
                cmd_bytes[2] = 0x38;
                cmd_bytes[3] = 0x00;
                cmd_bytes[4] = 0x00;
                cmd_bytes[5] = 0x00;
                cmd_bytes[6] = 0x00;
                cmd_bytes[7] = 0x3E;
                mBluetoothLeService.writeCharacteristic(getWriteGattCharacteristic(), cmd_bytes);
                //nodeBtn6.setText("Node 6 Battery " + node_battery + "%");
                //management_node();
                nodeBtn8.setBackgroundResource(R.drawable.green_circle_button);
                nodeBtn9.setVisibility(View.VISIBLE);

            }
        });
        nodeBtn9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] cmd_bytes = new byte[8];
                cmd_bytes[0] = 0x3C;
                cmd_bytes[1] = 0x53;
                cmd_bytes[2] = 0x39;
                cmd_bytes[3] = 0x00;
                cmd_bytes[4] = 0x00;
                cmd_bytes[5] = 0x00;
                cmd_bytes[6] = 0x00;
                cmd_bytes[7] = 0x3E;
                mBluetoothLeService.writeCharacteristic(getWriteGattCharacteristic(), cmd_bytes);
                nodeBtn9.setBackgroundResource(R.drawable.green_circle_button);
                //nodeBtn6.setText("Node 6 Battery " + node_battery + "%");
                //management_node();
            }
        });


        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

    }

    public void management_node () {
        switch (node_count) {
            case 1:
                nodeBtn2.setVisibility(View.VISIBLE);
                break;

            case 2:
                nodeBtn3.setVisibility(View.VISIBLE);
                break;

            case 3:
                nodeBtn4.setVisibility(View.VISIBLE);
                break;

            case 4:
                nodeBtn5.setVisibility(View.VISIBLE);
                break;

            case 5:
                nodeBtn6.setVisibility(View.VISIBLE);
                break;

            case 6:
                nodeBtn7.setVisibility(View.VISIBLE);
                break;

            case 7:
                nodeBtn8.setVisibility(View.VISIBLE);
                break;

            case 8:
                nodeBtn9.setVisibility(View.VISIBLE);
                break;

            case 9:
                Toast.makeText(NodeScanningActivity.this, "No more can be added, The maximum number of nodes is 9", Toast.LENGTH_SHORT).show();
                //nodeBtn9.setVisibility(View.VISIBLE);
                //No more can be added
                break;

        }
        /*
        if (node_count == 3)
        {
            LinearLayout.LayoutParams mLayoutParams = (LinearLayout.LayoutParams) nodeBtn2.getLayoutParams();
            mLayoutParams.leftMargin = 50;
            nodeBtn2.setLayoutParams(mLayoutParams);
        }*/
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
        //node_count = 0;
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
        /*
        System.out.println("sub String : "+   "[0] " + hexToASCII(hex_value).substring(0, 1));
        System.out.println("sub String : "+   "[1] " + hexToASCII(hex_value).substring(1, 2));
        System.out.println("sub String : "+   "[2] " + hexToASCII(hex_value).substring(2, 3));
        System.out.println("sub String : "+   "[3] " + hexToASCII(hex_value).substring(3, 4));
        System.out.println("sub String : "+   "[4] " + hexToASCII(hex_value).substring(4, 5));
        System.out.println("sub String : "+   "[5] " + hexToASCII(hex_value).substring(5, 6));
        System.out.println("sub String : "+   "[6] " + hexToASCII(hex_value).substring(6, 7));
        System.out.println("sub String : "+   "[7] " + hexToASCII(hex_value).substring(7, 8));*/

        //String node_number;
        node_number = hexToASCII(hex_value).substring(2, 3);
        node_B = (hexToASCII(hex_value).substring(6, 7));

        node_count = Integer.parseInt(node_number);
        //int battery_Num = Integer.parseInt(node_B);

        int test_1 = Integer.parseInt(node_B, 16);
        //management_node();
        //node_battery = 0;

        int battery_Num = test_1;

        System.out.println("node num : "+ node_number);
        //System.out.println("Battery num : "+ battery_Num);
        System.out.println("hexString to int : "+ test_1);

        if (test_1 == 13) {
            battery_Num = 10;
        }

        /*
        master.setText("Master Battery " + node_battery + "%");
        nodeBtn1.setText("Node 1 Battery " + node_battery + "%");
        nodeBtn2.setText("Node 2 Battery " + node_battery + "%");
        nodeBtn3.setText("Node 3 Battery " + node_battery + "%");
        nodeBtn4.setText("Node 4 Battery " + node_battery + "%");
        nodeBtn5.setText("Node 5 Battery " + node_battery + "%");
        nodeBtn6.setText("Node 6 Battery " + node_battery + "%");*/

        if (node_number.equals("0")){
            master.setText("Master Battery " + battery_Num*10 + "%");
        }
        else if (node_number.equals("1"))
        {
            nodeBtn1.setText("Node 1 Battery " + battery_Num*10 + "%");
        }
        else  if (node_number.equals("2"))
        {
            //node2.setText((hexToASCII(hex_value).substring(6, 7)));
            nodeBtn2.setText("Node 2 Battery " + battery_Num*10 + "%");
        }
        else  if (node_number.equals("3"))
        {
            nodeBtn3.setText("Node 3 Battery " + battery_Num*10 + "%");
        }
        else  if (node_number.equals("4"))
        {
            nodeBtn4.setText("Node 4 Battery " + battery_Num*10 + "%");
        }
        else  if (node_number.equals("5"))
        {
            nodeBtn5.setText("Node 5 Battery " + battery_Num*10 + "%");
        }
        else  if (node_number.equals("6"))
        {
            nodeBtn6.setText("Node 6 Battery " + battery_Num*10 + "%");
        }
        else  if (node_number.equals("7"))
        {
            nodeBtn7.setText("Node 7 Battery " + battery_Num*10 + "%");
        }
        else  if (node_number.equals("8"))
        {
            nodeBtn8.setText("Node 8 Battery " + battery_Num*10 + "%");
        }
        else  if (node_number.equals("9"))
        {
            nodeBtn9.setText("Node 9 Battery " + battery_Num*10 + "%");
        }


        Toast tMsg = Toast.makeText(NodeScanningActivity.this, hexToASCII(hex_value), Toast.LENGTH_SHORT);
        tMsg.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout view = (LinearLayout) tMsg.getView();
        tMsg.show();

        return sb.toString();
    }

    private BluetoothGattCharacteristic getNottifyCharacteristic(){

        BluetoothGattCharacteristic notifyCharacteristic = null;
        if(mGattCharacteristics == null || mGattCharacteristics.size() == 0){
            return null;
        }
        for (int i = 0; i < mGattCharacteristics.size() ; i++) {
            for (int j = 0; j < mGattCharacteristics.get(i).size() ; j++) {
                notifyCharacteristic =  mGattCharacteristics.get(i).get(j);
                if(notifyCharacteristic.getUuid().equals(BluetoothLeService.FFF4_RATE_MEASUREMENT)){
                    return notifyCharacteristic;
                }
                else if(notifyCharacteristic.getUuid().equals(BluetoothLeService.JDY_TX_MEASUREMENT)){
                    return notifyCharacteristic;
                }
            }
        }
        return null;
    }

    public BluetoothGattCharacteristic getWriteGattCharacteristic(){

        BluetoothGattCharacteristic writeGattCharacteristic = null;
        if(mGattCharacteristics == null || mGattCharacteristics.size() == 0){
            return null;
        }

        for (int i = 0; i < mGattCharacteristics.size() ; i++) {
            for (int j = 0; j < mGattCharacteristics.get(i).size() ; j++) {
                writeGattCharacteristic =  mGattCharacteristics.get(i).get(j);
                if(writeGattCharacteristic. getUuid().equals(BluetoothLeService.FFF3_RATE_MEASUREMENT)){
                    return writeGattCharacteristic;
                }

                else if(writeGattCharacteristic. getUuid().equals(BluetoothLeService.JDY_RX_MEASUREMENT)){
                    return writeGattCharacteristic;
                }
            }
        }
        return null;
    }

    private void displayGattServices(List<BluetoothGattService> gattServices) {

        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(
                        LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }
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
