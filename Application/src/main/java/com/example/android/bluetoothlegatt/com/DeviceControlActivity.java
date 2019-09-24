package com.example.android.bluetoothlegatt.com;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.example.android.bluetoothlegatt.Common.CommonData;
import com.example.android.bluetoothlegatt.R;

public class DeviceControlActivity extends Activity implements View.OnClickListener {
    private final static String TAG = "DCA";

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private TextView mConnectionState;
    private SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm:ss");
    public static final int DUMP = -1;

    private TextView mDataTextView;
    private ScrollView mDataScrollView;

    //TODO: GTO code
    public static String b_gyro1;
    public static String b_gyro2;
    public static String b_gyro3;

    public static String b_acc1;
    public static String b_acc2;
    public static String b_acc3;

    public static int b_ac_x;
    public static int b_ac_y;
    public static int b_ac_z;

    public static int b_gy_x;
    public static int b_gy_y;
    public static int b_gy_z;

    private ImageView change_img;
    private ImageView change_img2;


    private String mDeviceName;
    private String mDeviceAddress;
    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    AccSlidingCollection asc = new AccSlidingCollection();

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

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onReceive(Context context, Intent intent) {
                 String action = intent.getAction();
                 if (mDeviceName.equals("H_Band"))    {

                    final Intent mIntent = intent;
                    //*********************//
                    if (action.equals(mBluetoothLeService.ACTION_GATT_CONNECTED)) {

                        runOnUiThread(new Runnable() {
                            public void run() {
                                String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                                mBluetoothLeService.mState = mBluetoothLeService.UART_PROFILE_CONNECTED;
                            }
                        });
                    }

                    //*********************//
                    if (action.equals(mBluetoothLeService.ACTION_GATT_DISCONNECTED)) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                                mBluetoothLeService.mState = mBluetoothLeService.UART_PROFILE_DISCONNECTED;
                                mBluetoothLeService.close();

                            }
                        });
                    }


                    //*********************//
                    if (action.equals(mBluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED)) {

                        mBluetoothLeService.enableTXNotification();

                    }
                    //*********************//
                    if (action.equals(mBluetoothLeService.ACTION_DATA_AVAILABLE)) {

                        final byte[] txValue = intent.getByteArrayExtra(mBluetoothLeService.EXTRA_DATA);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                try {
                                    String text = new String(txValue, "UTF-8");
                                    String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                                    Log.d (TAG, stringToHex0x(text));


                                } catch (Exception e) {
                                    Log.e(TAG, e.toString());
                                }
                            }
                        });
                    }
                    //*********************//
                    if (action.equals(mBluetoothLeService.DEVICE_DOES_NOT_SUPPORT_UART)) {
                        Log.d(TAG, "Device doesn't support UART. Disconnecting");
                        mBluetoothLeService.disconnect();
                    }

                }


            else    {

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

                        byte[] packet = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                        displayData(packet);
                    }
             }
            }

    };

    public static String stringToHex(String s) {
        String result = "";

        for (int i = 0; i < s.length(); i++) {
            result += String.format("%02X ", (int) s.charAt(i));
        }

        return result;
    }


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

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        // Sets up UI references.
        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
        mConnectionState = (TextView) findViewById(R.id.connection_state);
        mDataTextView = (TextView) findViewById(R.id.send_data_tv);
        mDataScrollView = (ScrollView) findViewById(R.id.sd_scroll);

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
        /*
        mWebView = (WebView)findViewById(R.id.webview_login);
        mWebView.setWebViewClient(new WebViewClient());
        mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);

        mWebView.loadUrl("https://www.slideshare.net/Prasadbharatiyudu/android-ppt-14582649");
*/
        //Video play
        final VideoView videoView =
                (VideoView) findViewById(R.id.videoView2);

        videoView.setVideoPath("http://clips.vorwaerts-gmbh.de/VfE_html5.mp4");

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        mediaController.setPadding(0, 0, 0, 80); //상위 레이어의 바닥에서 얼마 만큼? 패딩을 줌
        videoView.setMediaController(mediaController);

        videoView.start();


        WebView WebView01 = (WebView) findViewById(R.id.webView01);
        WebView01.setWebViewClient(new WebViewClient());

        WebSettings webSettings = WebView01.getSettings();
        webSettings.setJavaScriptEnabled(true);

        WebView01.loadUrl("https://www.slideshare.net/Prasadbharatiyudu/android-ppt-14582649");



        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    public void onClick(View v) {

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
            Log.d(TAG, "Connect request result=" + result);
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
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
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

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
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

        int[] imgs = {R.drawable.hugh_before, R.drawable.hugh_after};

        change_img = (ImageView) findViewById(R.id.change_img);


        String hexString = "";

        for (byte b : packet) {  						//readBuf -> Hex
            hexString += Integer.toString((b & 0xF0) >> 4, 16);
            hexString += Integer.toString(b & 0x0F, 16);
        }

        int [] recv_sensors = new int [6];


        String b_head;
        // TODO : GYRO X Y Z HEX
        b_head = hexString.substring(1, 6);

        b_gyro1 = hexString.substring(6, 10);
        b_gyro2 = hexString.substring(10, 14);
        b_gyro3 = hexString.substring(14, 18);

        // TODO : ACC X Y Z HEX
        b_acc1 = hexString.substring(18, 22);
        b_acc2 = hexString.substring(22, 26);
        b_acc3 = hexString.substring(26, 30);

        // TODO : ACC X Y Z Decimal
        b_ac_x = (short) Integer.parseInt(b_acc1, 16);
        b_ac_y = (short) Integer.parseInt(b_acc2, 16);
        b_ac_z = (short) Integer.parseInt(b_acc3, 16);

        // TODO : GYRO X Y Z Decimal
        b_gy_x = (short) Integer.parseInt(b_gyro1, 16);
        b_gy_y = (short) Integer.parseInt(b_gyro2, 16);
        b_gy_z = (short) Integer.parseInt(b_gyro3, 16);

        recv_sensors [0] = b_ac_x;
        recv_sensors [1] = b_ac_y;
        recv_sensors [2] = b_ac_z;

        recv_sensors [3] = b_gy_x;
        recv_sensors [4] = b_gy_x;
        recv_sensors [5] = b_gy_x;

        //System.out.println(b_ac_x + " " + b_ac_y + " " + b_ac_z + " " + b_gy_x + " " + b_gy_y + " " + b_gy_z + " ");

        StringBuilder sb = new StringBuilder(packet.length * 2);

        int result_ = asc.SlidingCollectionInterface (recv_sensors);
        if (result_ != DUMP) {
            Log.d (TAG, "In IF branch");

            asc.gesture_count ++;
            sb.append(String.valueOf(asc.gesture_count));
            sb.append(" \t ");

            switch (result_)	{

                //TODO : FRONT Action
                case LEFT:
                    sb.append(" ←←← LEFT \n");

                    //change_img.setImageResource(R.drawable.hugh_after);

                    Runtime runtime0 = Runtime.getRuntime();
                    Process process0;
                    String res0 = "input swipe 1400 400 1000 400 40";     // display swipe action event (right -> left)
                    //String res0 = "input keyevent 66";
                    try {
                        process0 = runtime0.exec(res0); //2번 실행해야 되는 경우가 있음
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Log.e("Process Manager", "Unable to execute top command");
                    }

                    break;

                case RIGHT:
                    sb.append (" RIGHT →→→ \n");

                    //change_img.setImageResource(R.drawable.hugh_before);

                    Runtime runtime1 = Runtime.getRuntime();
                    Process process1;
                    String res1 = "input swipe 1000 400 1400 400 40";     // display swipe action event (left -> right)
                    try {
                        process1 = runtime1.exec(res1); //2번 실행해야 되는 경우가 있음
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Log.e("Process Manager", "Unable to execute top command");
                    }

                    break;

                //TODO : CLOCK Action
                case FRONT:
                    sb.append (" |||| FRONT |||| \n");

                    Runtime runtime2 = Runtime.getRuntime();
                    Process process2;
                    String res2 = "input keyevent 85";     // display swipe action event (left -> right)
                    try {
                        process2 = runtime2.exec(res2); //2번 실행해야 되는 경우가 있음
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Log.e("Process Manager", "Unable to execute top command");
                    }

                    break;

                //TODO : SIDE Action
                case UP:
                    sb.append (" ↑↑↑ UP ↑↑↑ \n");

                    Runtime runtime3 = Runtime.getRuntime();
                    Process process3;
                    String res3 = "input keyevent 24";
                    try {

                        process3 = runtime3.exec(res3); //2번 실행해야 되는 경우가 있음
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Log.e("Process Manager", "Unable to execute top command");
                    }


                    break;

                case CLOCK:
                    sb.append(" CLOCK⟳⟳⟳ \n");

                    Runtime runtime4 = Runtime.getRuntime();
                    Process process4;
                    String res4 = "input keyevent 24";
                    try {

                        process4 = runtime4.exec(res4); //2번 실행해야 되는 경우가 있음
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Log.e("Process Manager", "Unable to execute top command");
                    }

                    break;

                //TODO : UP Action
                case ANTI_CLOCK:
                    sb.append (" AntiClcok⟲⟲⟲ \n");

                    Runtime runtime5 = Runtime.getRuntime();
                    Process process5;
                    String res5 = "input keyevent 25";
                    try {

                        process5 = runtime5.exec(res5); //2번 실행해야 되는 경우가 있음
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Log.e("Process Manager", "Unable to execute top command");
                    }


                    break;

                default:
                    sb.append(" ??? Can't Detect ??? \n");

                    break;

            }
            sb.append("\n");
        }


        return sb.toString();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
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
            }
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private BluetoothGattCharacteristic getWriteGattCharacteristic(){

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
            }
        }
        return null;
    }

    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
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

    private static IntentFilter makeGattUpdateIntentFilter() {

        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_SEND_PACKET);
        return intentFilter;
    }

    public static final int FRONT = 0;
    public static final int BACK = FRONT + 1;	//1
    public static final int RIGHT = BACK + 1;   //2
    public static final int LEFT = RIGHT + 1;	//3
    public static final int UP = LEFT + 1;	//4
    public static final int DOWN = UP + 1;	//5
    public static final int	CLOCK = DOWN + 1;	//6
    public static final int ANTI_CLOCK = CLOCK + 1;	//7
    public static final int LOW_CLOCK = ANTI_CLOCK + 1;	//8
    public static final int LOW_ANTI = LOW_CLOCK + 1;	//9
    public static final int UNKNOWN_ = 99;

    public static final int GESTURE_NUM = LOW_ANTI + 1;	//10

}
