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
import android.media.Image;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bluetoothlegatt_touchMe.Common.CommonData;
import com.example.android.bluetoothlegatt_touchMe.R;
//import com.example.android.bluetoothlegatt_touchMe.com.BluetoothLeService;
import com.example.android.bluetoothlegatt_touchMe.com.BluetoothLeService;
import com.example.android.bluetoothlegatt_touchMe.com.DeviceControlActivity;
import com.example.android.bluetoothlegatt_touchMe.com.SampleGattAttributes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.android.bluetoothlegatt_touchMe.Common.CommonData.TOUCH_GTO_TEST1;
import static com.example.android.bluetoothlegatt_touchMe.Common.CommonData.TOUCH_GTO_TEST2;
import static com.example.android.bluetoothlegatt_touchMe.com.DeviceControlActivity.action;
import static com.example.android.bluetoothlegatt_touchMe.com.DeviceControlActivity.mGattCharacteristics;
import static com.example.android.bluetoothlegatt_touchMe.com.DeviceControlActivity.packet;

import static android.bluetooth.BluetoothProfile.STATE_CONNECTED;
import static com.example.android.bluetoothlegatt_touchMe.com.BluetoothLeService.EXTRA_DATA;
import static com.example.android.bluetoothlegatt_touchMe.com.BluetoothLeService.JDY_RX_MEASUREMENT;
import static com.example.android.bluetoothlegatt_touchMe.com.BluetoothLeService.JDY_TX_MEASUREMENT;
import static com.example.android.bluetoothlegatt_touchMe.com.BluetoothLeService.RX_CHAR_UUID;
import static com.example.android.bluetoothlegatt_touchMe.com.BluetoothLeService.RX_SERVICE_UUID;
import static com.example.android.bluetoothlegatt_touchMe.com.DeviceControlActivity.run_btn;
import static com.example.android.bluetoothlegatt_touchMe.com.main_menu.SetupActivity.act_flag;

/**
 * Created by GTO on 2020-01-22.
 */

public class RunActivity extends Activity {
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

    public static ImageView node_imageView01, node_imageView02, node_imageView03, node_imageView04, node_imageView05, node_imageView06;

    /*public ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();*/
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

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

    // Code to manage Service lifecycle.
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
            System.out.println("run Function Test action = " + action );
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                Log.w(TAG, String.format("run connected "));
                System.out.println("run Function Test 1");
                System.out.println("run Function Test 1 action = " + action );

                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                Log.w(TAG, String.format("run disconnected "));
                System.out.println("run Function Test 2");
                System.out.println("run Function Test 2 action = " + action );
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {

                displayGattServices(mBluetoothLeService.getSupportedGattServices());
                System.out.println("run Function Test 3");
                System.out.println("run Function Test 3 action = " + action );

                final BluetoothGattCharacteristic notifyCharacteristic = getNottifyCharacteristic();
                if (notifyCharacteristic == null) {
                    Toast.makeText(getApplication(), "gatt_services can not supported", Toast.LENGTH_SHORT).show();
                    System.out.println("run Function Test 4");
                    System.out.println("run Function Test 4 action = " + action );
                    mConnected = false;
                    return;
                }
                final int charaProp = notifyCharacteristic.getProperties();
                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                    mBluetoothLeService.setCharacteristicNotification(
                            notifyCharacteristic, true);
                    System.out.println("run Function Test 5");
                    System.out.println("run Function Test 5 action = " + action );
                }

            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                packet = intent.getByteArrayExtra(EXTRA_DATA);
                displayData(packet);
                System.out.println("run Function Test 6");
                System.out.println("run Function Test 6 action = " + action );
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.run_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getStandardSize();

        System.out.println("mBluetoothLeService value run = " + mBluetoothLeService) ;

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        TextView setup_txt = (TextView)findViewById(R.id.setup_txt);

        TextView run_mode_txt = (TextView)findViewById(R.id.run_mode_txt);
        TextView run_mode_change = (TextView)findViewById(R.id.run_mode_change);

        setup_txt.setTextSize((float) (standardSize_X / 8)); setup_txt.setTextSize((float) (standardSize_Y / 18));

        run_mode_txt.setTextSize((float) (standardSize_X / 15)); run_mode_txt.setTextSize((float) (standardSize_Y / 25));
        run_mode_change.setTextSize((float) (standardSize_X / 15)); run_mode_change.setTextSize((float) (standardSize_Y / 25));


        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        DeviceControlActivity.run_btn = (Button) findViewById(R.id.run_btn);

        //run_btn.setOnClickListener();

        if (act_flag == 0) {
            run_mode_change.setText("자동");
        }
        else if (act_flag == 1) {
            run_mode_change.setText("수동");
        }

    }


    //TODO BLE Packet receive
    protected void onResume() {
        super.onResume();
        System.out.println("run on Resume function");
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "run Connect request result=" + result + "*********************");

        }
        else {
            Log.d(TAG, "run Connect request result= " + mBluetoothLeService);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
        Log.d(TAG, "run onPause request");
    }

    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
        Log.d(TAG, "run onDestroy request");
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

        /*
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
        */


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

    private BluetoothGattCharacteristic getWriteGattCharacteristic(){

        BluetoothGattCharacteristic writeGattCharacteristic = null;
        if(mGattCharacteristics == null || mGattCharacteristics.size() == 0){
            System.out.println("run getWriteGattCharacteristic null");
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
        System.out.println("run getWriteGattCharacteristic for");
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

        //System.out.println("run device NAME = " + LIST_NAME);
        System.out.println("run device UUID = " + uuid);
    }

    public void onClick_test_btn(View v) {        //Map info Activity     //Map Button

        //mBluetoothLeService.writeGattCharacteristic(getWriteGattCharacteristic(), TOUCH_GTO_TEST1);
        //mBluetoothLeService.writeGattCharacteristic(getWriteGattCharacteristic(), TOUCH_GTO_TEST1);
        //mBluetoothLeService.writeGattCharacteristic(getWriteGattCharacteristic(), TOUCH_GTO_TEST1);
        //DeviceControlActivity.test_send();
        System.out.println("run device address = " + mDeviceAddress);
        //System.out.println("run device NAME = " + LIST_NAME);
        //System.out.println("run device UUID = " + LIST_UUID);
        System.out.println("run Function Test action = " + action );
        mBluetoothLeService.writeGattCharacteristic(getWriteGattCharacteristic(), TOUCH_GTO_TEST2);
        /*
        byte[] cmd_bytes = new byte[4];
        cmd_bytes[0] = 0x30;
        cmd_bytes[1] = 0x31;
        cmd_bytes[2] = 0x32;
        cmd_bytes[3] = 0x33;
        mBluetoothLeService.writeCharacteristic(getWriteGattCharacteristic(), cmd_bytes);*/

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
