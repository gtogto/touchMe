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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bluetoothlegatt_touchMe.Common.CommonData;
import com.example.android.bluetoothlegatt_touchMe.R;
import com.example.android.bluetoothlegatt_touchMe.com.BluetoothLeService;
import com.example.android.bluetoothlegatt_touchMe.com.DeviceControlActivity;
import com.example.android.bluetoothlegatt_touchMe.com.SampleGattAttributes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.android.bluetoothlegatt_touchMe.Common.CommonData.SETUP_MODE_SINGLE;
import static com.example.android.bluetoothlegatt_touchMe.com.BluetoothLeService.EXTRA_DATA;
import static com.example.android.bluetoothlegatt_touchMe.com.DeviceControlActivity.action;
import static com.example.android.bluetoothlegatt_touchMe.com.DeviceControlActivity.mGattCharacteristics;
import static com.example.android.bluetoothlegatt_touchMe.com.DeviceControlActivity.packet;
import static com.example.android.bluetoothlegatt_touchMe.com.main_menu.NodeScanningActivity.scan_node_count;


/**
 * Created by GTO on 2020-01-22.
 */
public class DeviceMappingActivity extends Activity {
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

    int standardSize_X, standardSize_Y;
    float density;

    private TextView node1, node2, node3, node4, node5, node6, node7, node8, node9;
    private TextView Bnode1, Bnode2, Bnode3, Bnode4, Bnode5, Bnode6, Bnode7, Bnode8, Bnode9;

    private Button master, nodeBtn1, nodeBtn2, nodeBtn3, nodeBtn4, nodeBtn5, nodeBtn6, nodeBtn7, nodeBtn8, nodeBtn9;


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

    // 헥사 접두사 "0x" 붙이는 버전
    public static String stringToHex0x(String s) {
        String result = "";

        for (int i = 0; i < s.length(); i++) {
            result += String.format("0x%02X ", (int) s.charAt(i));
        }
        return result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_mapping);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getStandardSize();

        System.out.println("mBluetoothLeService value map = " + mBluetoothLeService) ;

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        TextView setup_txt = (TextView)findViewById(R.id.setup_txt);

        node1 = (TextView)findViewById(R.id.nodetxt1);        node2 = (TextView)findViewById(R.id.nodetxt2);        node3 = (TextView)findViewById(R.id.nodetxt3);
        node4 = (TextView)findViewById(R.id.nodetxt4);        node5 = (TextView)findViewById(R.id.nodetxt5);        node6 = (TextView)findViewById(R.id.nodetxt6);
        node7 = (TextView)findViewById(R.id.nodetxt7);        node8 = (TextView)findViewById(R.id.nodetxt8);        node9 = (TextView)findViewById(R.id.nodetxt9);

        nodeBtn1 = (Button)findViewById(R.id.nodeBtn1);        nodeBtn2 = (Button)findViewById(R.id.nodeBtn2);        nodeBtn3 = (Button)findViewById(R.id.nodeBtn3);
        nodeBtn4 = (Button)findViewById(R.id.nodeBtn4);        nodeBtn5 = (Button)findViewById(R.id.nodeBtn5);        nodeBtn6 = (Button)findViewById(R.id.nodeBtn6);
        nodeBtn7 = (Button)findViewById(R.id.nodeBtn7);        nodeBtn8 = (Button)findViewById(R.id.nodeBtn8);        nodeBtn9 = (Button)findViewById(R.id.nodeBtn9);
        master = (Button)findViewById(R.id.master);

        setup_txt.setTextSize((float) (standardSize_X / 8)); setup_txt.setTextSize((float) (standardSize_Y / 18));

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        switch (scan_node_count) {
            case 0:
                Toast.makeText(DeviceMappingActivity.this, "No have scan node !", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                nodeBtn1.setVisibility(View.VISIBLE);                node1.setVisibility(View.VISIBLE);
                nodeBtn1.setText("node 1 100%");
                break;
            case 2:
                nodeBtn1.setVisibility(View.VISIBLE);                nodeBtn2.setVisibility(View.VISIBLE);
                node1.setVisibility(View.VISIBLE);                node2.setVisibility(View.VISIBLE);
                nodeBtn1.setText("node 1 100%");                nodeBtn2.setText("node 2 95%");
                break;
            case 3:
                nodeBtn1.setVisibility(View.VISIBLE);                nodeBtn2.setVisibility(View.VISIBLE);                nodeBtn3.setVisibility(View.VISIBLE);
                node1.setVisibility(View.VISIBLE);                node2.setVisibility(View.VISIBLE);                node3.setVisibility(View.VISIBLE);

                nodeBtn1.setText("100%");                nodeBtn2.setText("95%");                nodeBtn3.setText("95%");
                break;
            case 4:
                nodeBtn1.setVisibility(View.VISIBLE);                nodeBtn2.setVisibility(View.VISIBLE);
                nodeBtn3.setVisibility(View.VISIBLE);                nodeBtn4.setVisibility(View.VISIBLE);

                node1.setVisibility(View.VISIBLE);                node2.setVisibility(View.VISIBLE);
                node3.setVisibility(View.VISIBLE);                node4.setVisibility(View.VISIBLE);

                nodeBtn1.setText("100%");                nodeBtn2.setText("95%");                nodeBtn3.setText("95%");                nodeBtn4.setText("80%");

                break;
            case 5:
                nodeBtn1.setVisibility(View.VISIBLE);                nodeBtn2.setVisibility(View.VISIBLE);                nodeBtn3.setVisibility(View.VISIBLE);
                nodeBtn4.setVisibility(View.VISIBLE);                nodeBtn5.setVisibility(View.VISIBLE);

                node1.setVisibility(View.VISIBLE);                node2.setVisibility(View.VISIBLE);                node3.setVisibility(View.VISIBLE);
                node4.setVisibility(View.VISIBLE);                node5.setVisibility(View.VISIBLE);

                nodeBtn1.setText("100%");                nodeBtn2.setText("95%");                nodeBtn3.setText("95%");
                nodeBtn4.setText("80%");                nodeBtn5.setText("90%");

                break;
            case 6:
                nodeBtn1.setVisibility(View.VISIBLE);                nodeBtn2.setVisibility(View.VISIBLE);                nodeBtn3.setVisibility(View.VISIBLE);
                nodeBtn4.setVisibility(View.VISIBLE);                nodeBtn5.setVisibility(View.VISIBLE);                nodeBtn6.setVisibility(View.VISIBLE);

                node1.setVisibility(View.VISIBLE);                node2.setVisibility(View.VISIBLE);                node3.setVisibility(View.VISIBLE);
                node4.setVisibility(View.VISIBLE);                node5.setVisibility(View.VISIBLE);                node6.setVisibility(View.VISIBLE);

                nodeBtn1.setText("100%");                nodeBtn2.setText("95%");                nodeBtn3.setText("95%");
                nodeBtn4.setText("80%");                nodeBtn5.setText("90%");                nodeBtn6.setText("85%");

                break;
            case 7:
                nodeBtn1.setVisibility(View.VISIBLE);                nodeBtn2.setVisibility(View.VISIBLE);                nodeBtn3.setVisibility(View.VISIBLE);
                nodeBtn4.setVisibility(View.VISIBLE);                nodeBtn5.setVisibility(View.VISIBLE);                nodeBtn6.setVisibility(View.VISIBLE);
                nodeBtn7.setVisibility(View.VISIBLE);

                node1.setVisibility(View.VISIBLE);                node2.setVisibility(View.VISIBLE);                node3.setVisibility(View.VISIBLE);
                node4.setVisibility(View.VISIBLE);                node5.setVisibility(View.VISIBLE);                node6.setVisibility(View.VISIBLE);
                node7.setVisibility(View.VISIBLE);

                nodeBtn1.setText("100%");                nodeBtn2.setText("95%");                nodeBtn3.setText("95%");
                nodeBtn4.setText("80%");                nodeBtn5.setText("90%");                nodeBtn6.setText("85%");                nodeBtn7.setText("100%");

                break;
            case 8:
                nodeBtn1.setVisibility(View.VISIBLE);                nodeBtn2.setVisibility(View.VISIBLE);                nodeBtn3.setVisibility(View.VISIBLE);
                nodeBtn4.setVisibility(View.VISIBLE);                nodeBtn5.setVisibility(View.VISIBLE);                nodeBtn6.setVisibility(View.VISIBLE);
                nodeBtn7.setVisibility(View.VISIBLE);                nodeBtn8.setVisibility(View.VISIBLE);

                nodeBtn1.setText("100%");                nodeBtn2.setText("95%");                nodeBtn3.setText("95%");                nodeBtn4.setText("80%");
                nodeBtn5.setText("90%");                nodeBtn6.setText("85%");                nodeBtn7.setText("100%");                nodeBtn8.setText("75%");

                break;
            case 9:
                nodeBtn1.setVisibility(View.VISIBLE);                nodeBtn2.setVisibility(View.VISIBLE);                nodeBtn3.setVisibility(View.VISIBLE);
                nodeBtn4.setVisibility(View.VISIBLE);                nodeBtn5.setVisibility(View.VISIBLE);                nodeBtn6.setVisibility(View.VISIBLE);
                nodeBtn7.setVisibility(View.VISIBLE);                nodeBtn8.setVisibility(View.VISIBLE);                nodeBtn9.setVisibility(View.VISIBLE);

                node1.setVisibility(View.VISIBLE);                node2.setVisibility(View.VISIBLE);                node3.setVisibility(View.VISIBLE);
                node4.setVisibility(View.VISIBLE);                node5.setVisibility(View.VISIBLE);                node6.setVisibility(View.VISIBLE);
                node7.setVisibility(View.VISIBLE);                node8.setVisibility(View.VISIBLE);                node9.setVisibility(View.VISIBLE);

                nodeBtn1.setText("100%");                nodeBtn2.setText("95%");                nodeBtn3.setText("95%");
                nodeBtn4.setText("80%");                nodeBtn5.setText("90%");                nodeBtn6.setText("85%");
                nodeBtn7.setText("100%");                nodeBtn8.setText("75%");                nodeBtn9.setText("55%");

                break;
        }


    }

    //TODO BLE Packet receive
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
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

        System.out.println("By. DeviceMapping activity : "+ hex_value);
        StringBuilder sb = new StringBuilder(packet.length * 2);

        System.out.println("By. DeviceMapping HEX To ASCII : "+ hexToASCII(hex_value));

        Toast tMsg = Toast.makeText(DeviceMappingActivity.this, hexToASCII(hex_value), Toast.LENGTH_SHORT);
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

    public void onClick_Map_btn(View v) {        //Map info Activity     //Map Button
        mBluetoothLeService.writeGattCharacteristic(getWriteGattCharacteristic(), SETUP_MODE_SINGLE);
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
