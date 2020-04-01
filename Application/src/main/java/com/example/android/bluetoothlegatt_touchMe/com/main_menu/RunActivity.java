package com.example.android.bluetoothlegatt_touchMe.com.main_menu;

import android.annotation.SuppressLint;
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
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
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
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.android.bluetoothlegatt_touchMe.Common.CommonData.CMD_PLAY_NODE_DO;
import static com.example.android.bluetoothlegatt_touchMe.Common.CommonData.CMD_PLAY_NODE_MI;
import static com.example.android.bluetoothlegatt_touchMe.Common.CommonData.CMD_PLAY_NODE_PA;
import static com.example.android.bluetoothlegatt_touchMe.Common.CommonData.CMD_PLAY_NODE_RA;
import static com.example.android.bluetoothlegatt_touchMe.Common.CommonData.CMD_PLAY_NODE_RE;
import static com.example.android.bluetoothlegatt_touchMe.Common.CommonData.CMD_PLAY_NODE_SI;
import static com.example.android.bluetoothlegatt_touchMe.Common.CommonData.CMD_PLAY_NODE_SO;
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
import static com.example.android.bluetoothlegatt_touchMe.com.main_menu.NodeScanningActivity.node_count;
import static com.example.android.bluetoothlegatt_touchMe.com.main_menu.NodeScanningActivity.scan_node_count;
import static com.example.android.bluetoothlegatt_touchMe.com.main_menu.SetupActivity.act_flag;
import static com.example.android.bluetoothlegatt_touchMe.com.main_menu.SetupActivity.timer_setting;
import static com.example.android.bluetoothlegatt_touchMe.com.main_menu.SetupActivity.voice_flag;

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

    private TextView mTimeTextView, mRecordTextView;
    private Button mStopBtn, mStartBtn;

    private Thread timeThread = null;
    private Boolean isRunning = true;

    private TextView node1, node2, node3, node4, node5, node6;
    private Button master, nodeBtn1, nodeBtn2, nodeBtn3, nodeBtn4, nodeBtn5, nodeBtn6, nodeBtn7, nodeBtn8, nodeBtn9;

    private int timer_flag;

    public static int sound_clicked_num;

    private Random rnd;
    private int timer_sec, count;
    private int node_act;

    private Handler node_handler;

    private Timer timer;
    private TimerTask timerTask;

    private byte mode_a= 17, mode_b, cmd_play_node_time;

    private int mode_count = 1;

    private TimerTask second;
    private final Handler rnd_handler = new Handler();
    public void testStart() {
        timer_sec = 0;
        count = 0;
        second = new TimerTask() {
            @Override
            public void run() {
                Log.i("Test", "Timer start");
                Update();
                timer_sec++;
            }
        };

        Timer timer = new Timer();
        timer.schedule(second, 0, timer_setting*1000);
        //timer.schedule(timerTask, 0, 1000);
        node_play();
    }
    public void testStop(){
        second.cancel();
        timerTask.cancel();
        nodeBtn1.setBackgroundResource(R.drawable.black_circle_button_off);
        nodeBtn2.setBackgroundResource(R.drawable.black_circle_button_off);
        nodeBtn3.setBackgroundResource(R.drawable.black_circle_button_off);
        nodeBtn4.setBackgroundResource(R.drawable.black_circle_button_off);
        nodeBtn5.setBackgroundResource(R.drawable.black_circle_button_off);
        nodeBtn6.setBackgroundResource(R.drawable.black_circle_button_off);
        nodeBtn7.setBackgroundResource(R.drawable.black_circle_button_off);
        nodeBtn8.setBackgroundResource(R.drawable.black_circle_button_off);
        nodeBtn9.setBackgroundResource(R.drawable.black_circle_button_off);
    }

    public void node_play() {
        timer = new Timer(true);
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Log.v(TAG,"timer run");
                Message msg = node_handler.obtainMessage();
                node_handler.sendMessage(msg);
            }

            @Override
            public boolean cancel() {
                Log.v(TAG,"timer cancel");
                return super.cancel();
            }
        };
        timer.schedule(timerTask, 0, timer_setting*1000);
    }

    protected void Update() {
        Runnable updater = new Runnable() {
            public void run() {
                node_act = rnd.nextInt((scan_node_count) + 1);
                if (node_act == 0) {
                    node_act = node_act+1;
                }
                System.out.println("Random num "+ "[" + scan_node_count + "] = " + node_act);

                mode_count++;
                if (mode_count > 7) {
                    mode_count = 1;
                }

                if (voice_flag == 1) {
                    mode_b = (byte) (mode_a*mode_count);
                }
                else if (voice_flag == 0) {
                    mode_b = (byte) (mode_count);
                }

                cmd_play_node_time = (byte) timer_setting;
                System.out.println("Random char = " + mode_b);
                //Random rnd_test = new Random();
                //String randomStr = String.valueOf((char) ((int) (rnd_test.nextInt(108)) + 11));
                //stringToHex(randomStr);
                //System.out.println("Random char = " + stringToHex0x(randomStr));
            }
        };
        rnd_handler.post(updater);
    }

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

        //System.out.println("mBluetoothLeService value run = " + mBluetoothLeService) ;
        System.out.println("get node scan count 0 = " + node_count) ;
        System.out.println("get node scan count 1 = " + scan_node_count) ;

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        TextView setup_txt = (TextView)findViewById(R.id.setup_txt);

        TextView run_mode_txt = (TextView)findViewById(R.id.run_mode_txt);
        TextView run_mode_change = (TextView)findViewById(R.id.run_mode_change);

        node1 = (TextView)findViewById(R.id.node1);        node2 = (TextView)findViewById(R.id.node2);
        node3 = (TextView)findViewById(R.id.node3);        node4 = (TextView)findViewById(R.id.node4);
        node5 = (TextView)findViewById(R.id.node5);        node6 = (TextView)findViewById(R.id.node6);

        nodeBtn1 = (Button)findViewById(R.id.nodeBtn1);        nodeBtn2 = (Button)findViewById(R.id.nodeBtn2);        nodeBtn3 = (Button)findViewById(R.id.nodeBtn3);
        nodeBtn4 = (Button)findViewById(R.id.nodeBtn4);        nodeBtn5 = (Button)findViewById(R.id.nodeBtn5);        nodeBtn6 = (Button)findViewById(R.id.nodeBtn6);
        nodeBtn7 = (Button)findViewById(R.id.nodeBtn7);        nodeBtn8 = (Button)findViewById(R.id.nodeBtn8);        nodeBtn9 = (Button)findViewById(R.id.nodeBtn9);

        master = (Button)findViewById(R.id.master);

        setup_txt.setTextSize((float) (standardSize_X / 8)); setup_txt.setTextSize((float) (standardSize_Y / 18));

        run_mode_txt.setTextSize((float) (standardSize_X / 15)); run_mode_txt.setTextSize((float) (standardSize_Y / 25));
        run_mode_change.setTextSize((float) (standardSize_X / 15)); run_mode_change.setTextSize((float) (standardSize_Y / 25));


        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        mTimeTextView = (TextView) findViewById(R.id.timeView);
        mStartBtn = (Button) findViewById(R.id.start_btn);
        mStopBtn = (Button) findViewById(R.id.stop_btn);

        rnd = new Random();

        //run_btn.setOnClickListener();

        if (act_flag == 0) {
            run_mode_change.setText("자동");
        }
        else if (act_flag == 1) {
            run_mode_change.setText("수동");
        }

        switch (scan_node_count) {
            case 0:
                Toast.makeText(RunActivity.this, "No have scan node !", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                nodeBtn1.setVisibility(View.VISIBLE);
                break;
            case 2:
                nodeBtn1.setVisibility(View.VISIBLE);
                nodeBtn2.setVisibility(View.VISIBLE);
                break;
            case 3:
                nodeBtn1.setVisibility(View.VISIBLE);
                nodeBtn2.setVisibility(View.VISIBLE);
                nodeBtn3.setVisibility(View.VISIBLE);
                break;
            case 4:
                nodeBtn1.setVisibility(View.VISIBLE);
                nodeBtn2.setVisibility(View.VISIBLE);
                nodeBtn3.setVisibility(View.VISIBLE);
                nodeBtn4.setVisibility(View.VISIBLE);
                break;
            case 5:
                nodeBtn1.setVisibility(View.VISIBLE);
                nodeBtn2.setVisibility(View.VISIBLE);
                nodeBtn3.setVisibility(View.VISIBLE);
                nodeBtn4.setVisibility(View.VISIBLE);
                nodeBtn5.setVisibility(View.VISIBLE);
                break;
            case 6:
                nodeBtn1.setVisibility(View.VISIBLE);
                nodeBtn2.setVisibility(View.VISIBLE);
                nodeBtn3.setVisibility(View.VISIBLE);
                nodeBtn4.setVisibility(View.VISIBLE);
                nodeBtn5.setVisibility(View.VISIBLE);
                nodeBtn6.setVisibility(View.VISIBLE);
                break;
            case 7:
                nodeBtn1.setVisibility(View.VISIBLE);
                nodeBtn2.setVisibility(View.VISIBLE);
                nodeBtn3.setVisibility(View.VISIBLE);
                nodeBtn4.setVisibility(View.VISIBLE);
                nodeBtn5.setVisibility(View.VISIBLE);
                nodeBtn6.setVisibility(View.VISIBLE);
                nodeBtn7.setVisibility(View.VISIBLE);
                break;
            case 8:
                nodeBtn1.setVisibility(View.VISIBLE);
                nodeBtn2.setVisibility(View.VISIBLE);
                nodeBtn3.setVisibility(View.VISIBLE);
                nodeBtn4.setVisibility(View.VISIBLE);
                nodeBtn5.setVisibility(View.VISIBLE);
                nodeBtn6.setVisibility(View.VISIBLE);
                nodeBtn7.setVisibility(View.VISIBLE);
                nodeBtn8.setVisibility(View.VISIBLE);
                break;
            case 9:
                nodeBtn1.setVisibility(View.VISIBLE);
                nodeBtn2.setVisibility(View.VISIBLE);
                nodeBtn3.setVisibility(View.VISIBLE);
                nodeBtn4.setVisibility(View.VISIBLE);
                nodeBtn5.setVisibility(View.VISIBLE);
                nodeBtn6.setVisibility(View.VISIBLE);
                nodeBtn7.setVisibility(View.VISIBLE);
                nodeBtn8.setVisibility(View.VISIBLE);
                nodeBtn9.setVisibility(View.VISIBLE);
                break;
        }


        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //v.setVisibility(View.GONE);
                //mStopBtn.setVisibility(View.VISIBLE);
                timer_flag = 1;
                timeThread = new Thread(new timeThread());
                timeThread.start();
                Toast.makeText(RunActivity.this, "Timer Start!", Toast.LENGTH_SHORT).show();
                testStart();
            }
        });

        mStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //v.setVisibility(View.GONE);
                //mStartBtn.setVisibility(View.VISIBLE);
                if (timer_flag == 1) {
                    timeThread.interrupt();
                    timer_flag = 0;
                    testStop();
                }
                else Toast.makeText(RunActivity.this, "The timer has not Started!", Toast.LENGTH_SHORT).show();
            }
        });

        node_handler = new Handler(){
            public void handleMessage(Message msg){
                if (node_act == 1) {
                    nodeBtn1.setBackgroundResource(R.drawable.green_circle_button_on);  byte[] cmd_bytes = new byte[8];
                    cmd_bytes[0] = 0x3C;
                    cmd_bytes[1] = 0x50;
                    cmd_bytes[2] = 0x31;
                    cmd_bytes[3] = mode_b;    // This byte is 'DO' and mode Push only 0x10(0001), if Dual mode is (byte) 0x90(1001)
                    cmd_bytes[4] = 0x02;
                    cmd_bytes[5] = 0x00;
                    cmd_bytes[6] = 0x00;
                    cmd_bytes[7] = 0x3E;
                    mBluetoothLeService.writeCharacteristic(getWriteGattCharacteristic(), cmd_bytes);
                } else nodeBtn1.setBackgroundResource(R.drawable.black_circle_button_off);

                if (node_act == 2) {
                    nodeBtn2.setBackgroundResource(R.drawable.green_circle_button_on);
                    byte[] cmd_bytes = new byte[8];
                    cmd_bytes[0] = 0x3C;
                    cmd_bytes[1] = 0x50;
                    cmd_bytes[2] = 0x32;
                    cmd_bytes[3] = mode_b;    // This byte is 'DO' and mode Push only 0x10(0001), if Dual mode is (byte) 0x90(1001)
                    cmd_bytes[4] = 0x02;
                    cmd_bytes[5] = 0x00;
                    cmd_bytes[6] = 0x00;
                    cmd_bytes[7] = 0x3E;
                    mBluetoothLeService.writeCharacteristic(getWriteGattCharacteristic(), cmd_bytes);
                } else nodeBtn2.setBackgroundResource(R.drawable.black_circle_button_off);

                if (node_act == 3) {
                    nodeBtn3.setBackgroundResource(R.drawable.green_circle_button_on);
                    byte[] cmd_bytes = new byte[8];
                    cmd_bytes[0] = 0x3C;
                    cmd_bytes[1] = 0x50;
                    cmd_bytes[2] = 0x33;
                    cmd_bytes[3] = mode_b;    // This byte is 'DO' and mode Push only 0x10(0001), if Dual mode is (byte) 0x90(1001)
                    cmd_bytes[4] = 0x02;
                    cmd_bytes[5] = 0x00;
                    cmd_bytes[6] = 0x00;
                    cmd_bytes[7] = 0x3E;
                    mBluetoothLeService.writeCharacteristic(getWriteGattCharacteristic(), cmd_bytes);
                } else nodeBtn3.setBackgroundResource(R.drawable.black_circle_button_off);

                if (node_act == 4) {
                    nodeBtn4.setBackgroundResource(R.drawable.green_circle_button_on);
                    byte[] cmd_bytes = new byte[8];
                    cmd_bytes[0] = 0x3C;
                    cmd_bytes[1] = 0x50;
                    cmd_bytes[2] = 0x34;
                    cmd_bytes[3] = mode_b;    // This byte is 'DO' and mode Push only 0x10(0001), if Dual mode is (byte) 0x90(1001)
                    cmd_bytes[4] = 0x02;
                    cmd_bytes[5] = 0x00;
                    cmd_bytes[6] = 0x00;
                    cmd_bytes[7] = 0x3E;
                    mBluetoothLeService.writeCharacteristic(getWriteGattCharacteristic(), cmd_bytes);
                } else nodeBtn4.setBackgroundResource(R.drawable.black_circle_button_off);

                if (node_act == 5) {
                    nodeBtn5.setBackgroundResource(R.drawable.green_circle_button_on);
                    byte[] cmd_bytes = new byte[8];
                    cmd_bytes[0] = 0x3C;
                    cmd_bytes[1] = 0x50;
                    cmd_bytes[2] = 0x35;
                    cmd_bytes[3] = mode_b;    // This byte is 'DO' and mode Push only 0x10(0001), if Dual mode is (byte) 0x90(1001)
                    cmd_bytes[4] = 0x02;
                    cmd_bytes[5] = 0x00;
                    cmd_bytes[6] = 0x00;
                    cmd_bytes[7] = 0x3E;
                    mBluetoothLeService.writeCharacteristic(getWriteGattCharacteristic(), cmd_bytes);
                } else nodeBtn5.setBackgroundResource(R.drawable.black_circle_button_off);

                if (node_act == 6) {
                    nodeBtn6.setBackgroundResource(R.drawable.green_circle_button_on);
                    byte[] cmd_bytes = new byte[8];
                    cmd_bytes[0] = 0x3C;
                    cmd_bytes[1] = 0x50;
                    cmd_bytes[2] = 0x36;
                    cmd_bytes[3] = mode_b;    // This byte is 'DO' and mode Push only 0x10(0001), if Dual mode is (byte) 0x90(1001)
                    cmd_bytes[4] = 0x02;
                    cmd_bytes[5] = 0x00;
                    cmd_bytes[6] = 0x00;
                    cmd_bytes[7] = 0x3E;
                    mBluetoothLeService.writeCharacteristic(getWriteGattCharacteristic(), cmd_bytes);
                } else nodeBtn6.setBackgroundResource(R.drawable.black_circle_button_off);

                if (node_act == 7) {
                    nodeBtn7.setBackgroundResource(R.drawable.green_circle_button_on);
                    byte[] cmd_bytes = new byte[8];
                    cmd_bytes[0] = 0x3C;
                    cmd_bytes[1] = 0x50;
                    cmd_bytes[2] = 0x37;
                    cmd_bytes[3] = mode_b;    // This byte is 'DO' and mode Push only 0x10(0001), if Dual mode is (byte) 0x90(1001)
                    cmd_bytes[4] = 0x02;
                    cmd_bytes[5] = 0x00;
                    cmd_bytes[6] = 0x00;
                    cmd_bytes[7] = 0x3E;
                    mBluetoothLeService.writeCharacteristic(getWriteGattCharacteristic(), cmd_bytes);
                } else nodeBtn7.setBackgroundResource(R.drawable.black_circle_button_off);

                if (node_act == 8) {
                    nodeBtn8.setBackgroundResource(R.drawable.green_circle_button_on);
                    byte[] cmd_bytes = new byte[8];
                    cmd_bytes[0] = 0x3C;
                    cmd_bytes[1] = 0x50;
                    cmd_bytes[2] = 0x38;
                    cmd_bytes[3] = mode_b;    // This byte is 'DO' and mode Push only 0x10(0001), if Dual mode is (byte) 0x90(1001)
                    cmd_bytes[4] = 0x02;
                    cmd_bytes[5] = 0x00;
                    cmd_bytes[6] = 0x00;
                    cmd_bytes[7] = 0x3E;
                    mBluetoothLeService.writeCharacteristic(getWriteGattCharacteristic(), cmd_bytes);
                } else nodeBtn8.setBackgroundResource(R.drawable.black_circle_button_off);

                if (node_act == 9) {
                    nodeBtn9.setBackgroundResource(R.drawable.green_circle_button_on);
                    byte[] cmd_bytes = new byte[8];
                    cmd_bytes[0] = 0x3C;
                    cmd_bytes[1] = 0x50;
                    cmd_bytes[2] = 0x39;
                    cmd_bytes[3] = mode_b;    // This byte is 'DO' and mode Push only 0x10(0001), if Dual mode is (byte) 0x90(1001)
                    cmd_bytes[4] = 0x02;
                    cmd_bytes[5] = 0x00;
                    cmd_bytes[6] = 0x00;
                    cmd_bytes[7] = 0x3E;
                    mBluetoothLeService.writeCharacteristic(getWriteGattCharacteristic(), cmd_bytes);
                } else nodeBtn9.setBackgroundResource(R.drawable.black_circle_button_off);
            }
        };

        master.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mBluetoothLeService.writeGattCharacteristic(getWriteGattCharacteristic(), CMD_NODE_SCAN);
                byte[] cmd_bytes = new byte[8];
                cmd_bytes[0] = 0x3C;
                cmd_bytes[1] = 0x50;
                cmd_bytes[2] = 0x30;
                cmd_bytes[3] = mode_b;    // This byte is 'DO' and mode Push only 0x10(0001), if Dual mode is (byte) 0x90(1001)
                cmd_bytes[4] = cmd_play_node_time;
                cmd_bytes[5] = 0x00;
                cmd_bytes[6] = 0x00;
                cmd_bytes[7] = 0x3E;
                mBluetoothLeService.writeCharacteristic(getWriteGattCharacteristic(), cmd_bytes);
            }
        });

        nodeBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mBluetoothLeService.writeGattCharacteristic(getWriteGattCharacteristic(), CMD_NODE_SCAN);
                byte[] cmd_bytes = new byte[8];
                cmd_bytes[0] = 0x3C;
                cmd_bytes[1] = 0x50;
                cmd_bytes[2] = 0x31;
                cmd_bytes[3] = 0x22;    // This byte is 'DO' and mode Push only 0x10(0001), if Dual mode is (byte) 0x90(1001)
                cmd_bytes[4] = 0x03;
                cmd_bytes[5] = 0x00;
                cmd_bytes[6] = 0x00;
                cmd_bytes[7] = 0x3E;
                mBluetoothLeService.writeCharacteristic(getWriteGattCharacteristic(), cmd_bytes);
            }
        });

        nodeBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mBluetoothLeService.writeGattCharacteristic(getWriteGattCharacteristic(), CMD_NODE_SCAN);
                byte[] cmd_bytes = new byte[8];
                cmd_bytes[0] = 0x3C;
                cmd_bytes[1] = 0x50;
                cmd_bytes[2] = 0x32;
                cmd_bytes[3] = 0x33;    // This byte is 'DO' and mode Push only 0x10(0001), if Dual mode is (byte) 0x90(1001)
                cmd_bytes[4] = 0x03;
                cmd_bytes[5] = 0x00;
                cmd_bytes[6] = 0x00;
                cmd_bytes[7] = 0x3E;
                mBluetoothLeService.writeCharacteristic(getWriteGattCharacteristic(), cmd_bytes);
            }
        });

        nodeBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mBluetoothLeService.writeGattCharacteristic(getWriteGattCharacteristic(), CMD_NODE_SCAN);
                byte[] cmd_bytes = new byte[8];
                cmd_bytes[0] = 0x3C;
                cmd_bytes[1] = 0x50;
                cmd_bytes[2] = 0x33;
                cmd_bytes[3] = 0x44;    // This byte is 'DO' and mode Push only 0x10(0001), if Dual mode is (byte) 0x90(1001)
                cmd_bytes[4] = 0x03;
                cmd_bytes[5] = 0x00;
                cmd_bytes[6] = 0x00;
                cmd_bytes[7] = 0x3E;
                mBluetoothLeService.writeCharacteristic(getWriteGattCharacteristic(), cmd_bytes);
            }
        });

        nodeBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mBluetoothLeService.writeGattCharacteristic(getWriteGattCharacteristic(), CMD_NODE_SCAN);
                byte[] cmd_bytes = new byte[8];
                cmd_bytes[0] = 0x3C;
                cmd_bytes[1] = 0x50;
                cmd_bytes[2] = 0x34;
                cmd_bytes[3] = 0x55;    // This byte is 'DO' and mode Push only 0x10(0001), if Dual mode is (byte) 0x90(1001)
                cmd_bytes[4] = 0x03;
                cmd_bytes[5] = 0x00;
                cmd_bytes[6] = 0x00;
                cmd_bytes[7] = 0x3E;
                mBluetoothLeService.writeCharacteristic(getWriteGattCharacteristic(), cmd_bytes);
            }
        });

        nodeBtn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mBluetoothLeService.writeGattCharacteristic(getWriteGattCharacteristic(), CMD_NODE_SCAN);
                byte[] cmd_bytes = new byte[8];
                cmd_bytes[0] = 0x3C;
                cmd_bytes[1] = 0x50;
                cmd_bytes[2] = 0x35;
                cmd_bytes[3] = 0x66;    // This byte is 'DO' and mode Push only 0x10(0001), if Dual mode is (byte) 0x90(1001)
                cmd_bytes[4] = 0x03;
                cmd_bytes[5] = 0x00;
                cmd_bytes[6] = 0x00;
                cmd_bytes[7] = 0x3E;
                mBluetoothLeService.writeCharacteristic(getWriteGattCharacteristic(), cmd_bytes);
            }
        });

    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int mSec = msg.arg1 % 100;
            int sec = (msg.arg1 / 100) % 60;
            int min = (msg.arg1 / 100) / 60;
            int hour = (msg.arg1 / 100) / 360;
            //1000이 1초 1000*60 은 1분 1000*60*10은 10분 1000*60*60은 한시간

            @SuppressLint("DefaultLocale") String result = String.format("%02d:%02d:%02d:%02d", hour, min, sec, mSec);
            /*
            if (result.equals("00:01:15:00")) {
                Toast.makeText(RunActivity.this, "1분 15초가 지났습니다.", Toast.LENGTH_SHORT).show();
            }*/
            mTimeTextView.setText(result);
        }
    };

    public class timeThread implements Runnable {
        @Override
        public void run() {
            int i = 0;

            while (true) {
                while (isRunning) { //일시정지를 누르면 멈춤
                    Message msg = new Message();
                    msg.arg1 = i++;
                    handler.sendMessage(msg);


                    /*
                    switch (sound_clicked_num) {
                        case 1:
                            mBluetoothLeService.writeGattCharacteristic(getWriteGattCharacteristic(), CMD_PLAY_NODE_DO);
                            break;

                        case 2:
                            mBluetoothLeService.writeGattCharacteristic(getWriteGattCharacteristic(), CMD_PLAY_NODE_RE);
                            break;

                        case 3:
                            mBluetoothLeService.writeGattCharacteristic(getWriteGattCharacteristic(), CMD_PLAY_NODE_MI);
                            break;

                        case 4:
                            mBluetoothLeService.writeGattCharacteristic(getWriteGattCharacteristic(), CMD_PLAY_NODE_PA);
                            break;

                        case 5:
                            mBluetoothLeService.writeGattCharacteristic(getWriteGattCharacteristic(), CMD_PLAY_NODE_SO);
                            break;

                        case 6:
                            mBluetoothLeService.writeGattCharacteristic(getWriteGattCharacteristic(), CMD_PLAY_NODE_RA);
                            break;

                        case 7:
                            mBluetoothLeService.writeGattCharacteristic(getWriteGattCharacteristic(), CMD_PLAY_NODE_SI);
                            break;

                    }*/

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable(){
                            @Override
                            public void run() {
                                mTimeTextView.setText("");
                                mTimeTextView.setText("00:00:00:00");
                            }
                        });
                        return; // 인터럽트 받을 경우 return
                    }
                }
            }
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
        second.cancel();
        timerTask.cancel();
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
        /*
        node1.setText(hex_value);        node2.setText(hex_value);
        node3.setText(hex_value);        node4.setText(hex_value);
        node5.setText(hex_value);        node6.setText(hex_value);*/
        /*
        Toast tMsg = Toast.makeText(RunActivity.this, hexToASCII(hex_value), Toast.LENGTH_SHORT);
        tMsg.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout view = (LinearLayout) tMsg.getView();
        tMsg.show();*/

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