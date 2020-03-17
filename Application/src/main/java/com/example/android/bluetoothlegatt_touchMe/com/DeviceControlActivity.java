package com.example.android.bluetoothlegatt_touchMe.com;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothGatt;
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
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bluetoothlegatt_touchMe.Common.CommonData;
import com.example.android.bluetoothlegatt_touchMe.R;
import com.example.android.bluetoothlegatt_touchMe.com.main_menu.DeviceMappingActivity;
import com.example.android.bluetoothlegatt_touchMe.com.main_menu.NodeScanningActivity;
import com.example.android.bluetoothlegatt_touchMe.com.main_menu.ReportAnalysisActivity;
import com.example.android.bluetoothlegatt_touchMe.com.main_menu.RunActivity;
import com.example.android.bluetoothlegatt_touchMe.com.main_menu.SetupActivity;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;

import static android.bluetooth.BluetoothProfile.STATE_CONNECTED;
import static com.example.android.bluetoothlegatt_touchMe.com.BluetoothLeService.EXTRA_DATA;
import static com.example.android.bluetoothlegatt_touchMe.com.BluetoothLeService.JDY_RX_MEASUREMENT;
import static com.example.android.bluetoothlegatt_touchMe.com.BluetoothLeService.JDY_TX_MEASUREMENT;
import static com.example.android.bluetoothlegatt_touchMe.com.BluetoothLeService.RX_CHAR_UUID;
import static com.example.android.bluetoothlegatt_touchMe.com.BluetoothLeService.RX_SERVICE_UUID;

/**
 * Created by GTO on 2020-01-22.
 */

public class DeviceControlActivity extends Activity implements View.OnClickListener {
    private final static String TAG = "DCA";

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private TextView mConnectionState;
    private SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm:ss");
    public static final int DUMP = -1;

    private TextView mDataTextView;
    private ScrollView mDataScrollView;

    public String mDeviceName;
    public String mDeviceAddress;
    public static BluetoothLeService mBluetoothLeService;
    private BluetoothGattCharacteristic mWriteCharacteristic;
    public static ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    public static byte[] packet;
    public static byte[] send_packet;

    int standardSize_X, standardSize_Y;
    float density;

    public static Button run_btn;
    public static String action;

    //AccSlidingCollection asc = new AccSlidingCollection();

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
            System.out.println("main service disconnect");
        }
    };

    public void onClick(View v) {
    }

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
                action = intent.getAction();
                System.out.println("main Function Test action = " + action );
                    if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                        mConnected = true;
                        updateConnectionState(R.string.connected);
                        System.out.println("main Function Test 1");
                        System.out.println("main Function Test 1 action = " + action );
                        invalidateOptionsMenu();
                    } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                        mConnected = false;
                        updateConnectionState(R.string.disconnected);
                        System.out.println("main Function Test 2");
                        System.out.println("main Function Test 2 action = " + action );

                        invalidateOptionsMenu();
                    } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {

                        displayGattServices(mBluetoothLeService.getSupportedGattServices());
                        System.out.println("main Function Test 3");
                        System.out.println("main Function Test 3 action = " + action );

                        final BluetoothGattCharacteristic notifyCharacteristic = getNottifyCharacteristic();
                        if (notifyCharacteristic == null) {
                            Toast.makeText(getApplication(), "gatt_services can not supported", Toast.LENGTH_SHORT).show();
                            System.out.println("main Function Test 4");
                            System.out.println("main Function Test 4 action = " + action );

                            mConnected = false;
                            return;
                        }
                        final int charaProp = notifyCharacteristic.getProperties();
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                            mBluetoothLeService.setCharacteristicNotification(
                                    notifyCharacteristic, true);
                            System.out.println("main Function Test 5");
                            System.out.println("main Function Test 5 action = " + action );

                        }

                    } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                        packet = intent.getByteArrayExtra(EXTRA_DATA);
                        displayData(packet);
                        System.out.println("main Function Test 6");
                        System.out.println("main Function Test 6 action = " + action );

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gatt_services_characteristics);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getStandardSize();

        System.out.println("mBluetoothLeService value main = " + mBluetoothLeService) ;

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        // Sets up UI references.
        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
        mConnectionState = (TextView) findViewById(R.id.connection_state);

        mDataTextView = (TextView) findViewById(R.id.send_data_tv);
        mDataScrollView = (ScrollView) findViewById(R.id.sd_scroll);

        //run_btn = (Button) findViewById(R.id.run_btn);

        mDataTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == true) {
                    mDataScrollView.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            mDataScrollView.smoothScrollBy(0, 800);
                        }
                    }, 100);
                }
            }
        });

        TextView ubio_text = (TextView)findViewById(R.id.ubio_text);
        TextView inno_text = (TextView)findViewById(R.id.inno_text);

        ubio_text.setTextSize((float) (standardSize_X / 6)); ubio_text.setTextSize((float) (standardSize_Y / 16));
        inno_text.setTextSize((float) (standardSize_X / 12)); inno_text.setTextSize((float) (standardSize_Y / 24));

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    private void showMessage(String msg) {
        Log.e(TAG, msg);
    }
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "main Connect request result=" + result + "*********************");

        }
        else {
            Log.d(TAG, "run Connect request result= " + mBluetoothLeService);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregisterReceiver(mGattUpdateReceiver);
        Log.d(TAG, "main onPause request");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
        Log.d(TAG, "main onDestroy request");
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:
                mBluetoothLeService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    private String getCurrentTime() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        return mFormat.format(date);
    }

    private void displayData(byte[] packet) {

        if (packet != null) {

            autoScrollView(getStringPacket(packet));
            getStringPacket(packet);
        }
    }

    private void autoScrollView(String text) {
        if (!text.isEmpty())
            mDataTextView.append(text);
        mDataScrollView.post(new Runnable() {
            @Override
            public void run() {
                mDataScrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    private String getStringPacket(byte[] packet) {

        String hex_value = "";

        for (byte b : packet) {  						//readBuf -> Hex
            hex_value += Integer.toString((b & 0xF0) >> 4, 16);
            hex_value += Integer.toString(b & 0x0F, 16);
        }

        System.out.println("By. Device control activity : "+ hex_value);
        StringBuilder sb = new StringBuilder(packet.length * 2);

        System.out.println("By. Device HEX To ASCII : "+ hexToASCII(hex_value));

        Toast tMsg = Toast.makeText(DeviceControlActivity.this, hexToASCII(hex_value), Toast.LENGTH_SHORT);
        tMsg.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout view = (LinearLayout) tMsg.getView();
        tMsg.show();

        return sb.toString();
    }


    public BluetoothGattCharacteristic getNottifyCharacteristic(){

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
            System.out.println("main getWriteGattCharacteristic null");
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
        System.out.println("main getWriteGattCharacteristic for");
        return null;
    }

    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.

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
        System.out.println("main device UUID = " + uuid);

    }

    public void onClick_setup(View v) {        //Map info Activity     //Map Button

        final Intent i = new Intent(this, SetupActivity.class);
        startActivityForResult(i, 201);
        //test_send();
        //mBluetoothLeService.writeGattCharacteristic(getWriteGattCharacteristic(), CommonData.TOUCH_GTO_TEST1);
        //mBluetoothLeService.writeGattCharacteristic(getWriteGattCharacteristic(), CommonData.TOUCH_GTO_TEST2);
        //System.out.println("send data "+ mBluetoothLeService.writeGattCharacteristic(getWriteGattCharacteristic(), CommonData.TOUCH_GTO_TEST1));

    }

    public void onClick_scan(View v) {        //Map info Activity     //Map Button

        final Intent i = new Intent(this, NodeScanningActivity.class);
        startActivityForResult(i, 201);
        mBluetoothLeService.writeGattCharacteristic(getWriteGattCharacteristic(), CommonData.TOUCH_GTO_TEST2);

    }

    public void onClick_mapping(View v) {        //Map info Activity     //Map Button

        final Intent i = new Intent(this, DeviceMappingActivity.class);
        startActivityForResult(i, 201);
    }

    public void onClick_report(View v) {        //Map info Activity     //Map Button

        //final Intent i = new Intent(this, ReportAnalysisActivity.class);
        //startActivityForResult(i, 201);
        //System.out.println("main device address = " + mDeviceAddress);
        //System.out.println("main device NAME = " + LIST_NAME);
        //System.out.println("main device UUID = " + LIST_UUID);
        //System.out.println("main Function Test action = " + action );

        //mBluetoothLeService.writeGattCharacteristic(getWriteGattCharacteristic(), CommonData.touch_test1);

        //System.out.println("clicked Mapping btn");

    }

    public void onClick_run(View v) {        //Map info Activity     //Map Button

        final Intent intent5 = new Intent(this, RunActivity.class);
        intent5.putExtra(RunActivity.EXTRAS_DEVICE_NAME, mDeviceName);
        intent5.putExtra(RunActivity.EXTRAS_DEVICE_ADDRESS, mDeviceAddress);

        startActivity(intent5);


        //final Intent i = new Intent(this, RunActivity.class);
        //startActivityForResult(i, 201);

        //mBluetoothLeService.writeGattCharacteristic(getWriteGattCharacteristic(), CommonData.TOUCH_GTO_TEST1);

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

}
