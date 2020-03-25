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
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Switch;
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

import static android.bluetooth.BluetoothProfile.STATE_CONNECTED;
import static com.example.android.bluetoothlegatt_touchMe.Common.CommonData.CMD_NODE_REGISTER;
import static com.example.android.bluetoothlegatt_touchMe.Common.CommonData.CMD_NODE_SCAN;
import static com.example.android.bluetoothlegatt_touchMe.Common.CommonData.CMD_PLAY_NODE;
import static com.example.android.bluetoothlegatt_touchMe.Common.CommonData.CMD_SCAN_FINISH;
import static com.example.android.bluetoothlegatt_touchMe.Common.CommonData.SETUP_MODE_DUAL;
import static com.example.android.bluetoothlegatt_touchMe.Common.CommonData.SETUP_MODE_SINGLE;
import static com.example.android.bluetoothlegatt_touchMe.Common.CommonData.TOUCH_GTO_TEST1;
import static com.example.android.bluetoothlegatt_touchMe.Common.CommonData.TOUCH_GTO_TEST2;
import static com.example.android.bluetoothlegatt_touchMe.com.BluetoothLeService.EXTRA_DATA;
import static com.example.android.bluetoothlegatt_touchMe.com.BluetoothLeService.JDY_RX_MEASUREMENT;
import static com.example.android.bluetoothlegatt_touchMe.com.BluetoothLeService.JDY_TX_MEASUREMENT;
import static com.example.android.bluetoothlegatt_touchMe.com.BluetoothLeService.RX_CHAR_UUID;
import static com.example.android.bluetoothlegatt_touchMe.com.BluetoothLeService.RX_SERVICE_UUID;
import static com.example.android.bluetoothlegatt_touchMe.com.DeviceControlActivity.action;
import static com.example.android.bluetoothlegatt_touchMe.com.DeviceControlActivity.mGattCharacteristics;

/**
 * Created by GTO on 2020-01-22.
 */

public class SetupActivity extends Activity implements View.OnClickListener {
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

    public static SeekBar seekBar_age;
    public static TextView output_age;
    public static int age_number;
    public static int age;

    public static int sex_flag;

    public static SeekBar seekBar_weight;
    public static TextView output_weight;
    public static int weight_number;

    public static int act_flag;
    public static int auto_flag;
    public static int cds_flag;
    public static int voice_flag;

    public static Switch OnOff;

    public static int color_flag;

    public static int sensitive_flag;

    public static SeekBar seekBar_timer;
    public static TextView output_timer;
    public static int timer_number;

    int standardSize_X, standardSize_Y;
    float density;
    public SharedPreferences.Editor editor;
    private SharedPreferences preferences;
    private static final String PROGRESS1 = "SEEKBAR_AGE";
    private static final String PROGRESS2 = "SEEKBAR_WEIGHT";
    private static final String PROGRESS3 = "SEEKBAR_TIMER";
    private static final String VOICE1 = "VOICE_SWITCHING";
    private int save;

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

    public void onClick(View v) {}

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //action = intent.getAction();
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
                    DeviceControlActivity.packet = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                    displayData(DeviceControlActivity.packet);
                }

                else if (BluetoothLeService.JDY_TX_MEASUREMENT.equals(action)) {
                    Log.w(TAG, String.format("RECEIVE DATA BY JDY"));      // Receive data
                }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_up);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getStandardSize();

        seekBar_age = (SeekBar)findViewById(R.id.age_seekbar);
        output_age = (TextView)findViewById(R.id.age_value_out);

        RadioButton radio_male = (RadioButton) findViewById(R.id.radio_male);
        RadioButton radio_female = (RadioButton) findViewById(R.id.radio_female);

        seekBar_weight = (SeekBar)findViewById(R.id.weight_seekbar);
        output_weight = (TextView)findViewById(R.id.weight_value_out);

        RadioButton radio_auto = (RadioButton) findViewById(R.id.radio_auto);
        RadioButton radio_manual = (RadioButton) findViewById(R.id.radio_manual);

        RadioButton radio_random = (RadioButton) findViewById(R.id.radio_random);
        RadioButton radio_sequential = (RadioButton) findViewById(R.id.radio_sequential);

        RadioButton radio_center = (RadioButton) findViewById(R.id.radio_center);
        RadioButton radio_dispersion = (RadioButton) findViewById(R.id.radio_dispersion);
        RadioButton radio_scenario = (RadioButton) findViewById(R.id.radio_scenario);

        RadioButton radio_rand_color = (RadioButton) findViewById(R.id.radio_random_color);
        RadioButton radio_one_color = (RadioButton) findViewById(R.id.radio_one_color);

        RadioButton red_color = (RadioButton) findViewById(R.id.radio_red);
        RadioButton green_color = (RadioButton) findViewById(R.id.radio_green);
        RadioButton blue_color = (RadioButton) findViewById(R.id.radio_blue);
        RadioButton yellow_color = (RadioButton) findViewById(R.id.radio_yellow);
        RadioButton pink_color = (RadioButton) findViewById(R.id.radio_pink);
        RadioButton sky_color = (RadioButton) findViewById(R.id.radio_sky);

        OnOff = (Switch)findViewById(R.id.switch_voice);

        RadioButton radio_contact = (RadioButton) findViewById(R.id.radio_contact);
        RadioButton radio_nearing = (RadioButton) findViewById(R.id.radio_nearing);

        seekBar_timer = (SeekBar)findViewById(R.id.timer_seekbar);
        output_timer = (TextView)findViewById(R.id.timer_value_out);

        TextView setup_txt = (TextView)findViewById(R.id.setup_txt);
        TextView age_txt = (TextView)findViewById(R.id.age_txt);
        TextView age_txt2 = (TextView)findViewById(R.id.age_txt2);
        TextView age_value_out = (TextView)findViewById(R.id.age_value_out);

        TextView sex_txt = (TextView)findViewById(R.id.sex_txt);

        TextView weight_value_out = (TextView)findViewById(R.id.weight_value_out);
        TextView weight_txt2 = (TextView)findViewById(R.id.weight_txt2);
        TextView weight_txt = (TextView)findViewById(R.id.weight_txt);

        TextView act_mode_txt = (TextView)findViewById(R.id.act_mode_txt);

        TextView auto_mode_txt = (TextView)findViewById(R.id.auto_mode_txt);

        TextView voice_txt = (TextView)findViewById(R.id.voice_txt);

        TextView radiation_mode_txt = (TextView)findViewById(R.id.radiation_mode_txt);

        TextView Sensitivity_mode_txt = (TextView)findViewById(R.id.Sensitivity_mode_txt);

        TextView timer_txt = (TextView)findViewById(R.id.timer_txt);
        TextView timer_value_out = (TextView)findViewById(R.id.timer_value_out);
        TextView timer_txt2 = (TextView)findViewById(R.id.timer_txt2);


        setup_txt.setTextSize((float) (standardSize_X / 8)); setup_txt.setTextSize((float) (standardSize_Y / 18));

        age_txt.setTextSize((float) (standardSize_X / 15)); age_txt.setTextSize((float) (standardSize_Y / 25));
        age_txt2.setTextSize((float) (standardSize_X / 12)); age_txt2.setTextSize((float) (standardSize_Y / 22));
        age_value_out.setTextSize((float) (standardSize_X / 12)); age_value_out.setTextSize((float) (standardSize_Y / 22));

        sex_txt.setTextSize((float) (standardSize_X / 12)); sex_txt.setTextSize((float) (standardSize_Y / 22));

        weight_txt.setTextSize((float) (standardSize_X / 15)); weight_txt.setTextSize((float) (standardSize_Y / 25));
        weight_txt2.setTextSize((float) (standardSize_X / 12)); weight_txt2.setTextSize((float) (standardSize_Y / 22));
        weight_value_out.setTextSize((float) (standardSize_X / 12)); weight_value_out.setTextSize((float) (standardSize_Y / 22));

        act_mode_txt.setTextSize((float) (standardSize_X / 15)); act_mode_txt.setTextSize((float) (standardSize_Y / 25));

        auto_mode_txt.setTextSize((float) (standardSize_X / 15)); auto_mode_txt.setTextSize((float) (standardSize_Y / 25));

        voice_txt.setTextSize((float) (standardSize_X / 15)); voice_txt.setTextSize((float) (standardSize_Y / 25));

        radiation_mode_txt.setTextSize((float) (standardSize_X / 15)); radiation_mode_txt.setTextSize((float) (standardSize_Y / 25));

        Sensitivity_mode_txt.setTextSize((float) (standardSize_X / 15)); Sensitivity_mode_txt.setTextSize((float) (standardSize_Y / 25));

        timer_txt.setTextSize((float) (standardSize_X / 15)); timer_txt.setTextSize((float) (standardSize_Y / 25));
        timer_value_out.setTextSize((float) (standardSize_X / 12)); timer_value_out.setTextSize((float) (standardSize_Y / 22));
        timer_txt2.setTextSize((float) (standardSize_X / 12)); timer_txt2.setTextSize((float) (standardSize_Y / 22));

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        preferences = getSharedPreferences(" ", MODE_PRIVATE);
        editor = preferences.edit();

        //TODO Age selection SeekBar menu
        seekBar_age.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                age_number = seekBar_age.getProgress();
                update_age();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                age_number = seekBar_age.getProgress();
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //age_number = seekBar_age.getProgress();
                editor.putInt(PROGRESS1, seekBar.getProgress());
                editor.commit();
            }
        });

        //TODO Sex selection Radio menu
        radio_male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "남 자", Toast.LENGTH_SHORT).show();
                Toast tMsg = Toast.makeText(SetupActivity.this, "  남   자  ", Toast.LENGTH_SHORT);
                tMsg.setGravity(Gravity.CENTER, 0, 0);
                LinearLayout view = (LinearLayout) tMsg.getView();
                tMsg.show();
                sex_flag = 0;
            }
        });

        radio_female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "여 자", Toast.LENGTH_SHORT).show();
                Toast tMsg = Toast.makeText(SetupActivity.this, "  여   자  ", Toast.LENGTH_SHORT);
                tMsg.setGravity(Gravity.CENTER, 0, 0);
                LinearLayout view = (LinearLayout) tMsg.getView();
                tMsg.show();
                sex_flag = 1;
            }
        });

        if (sex_flag == 0) {
            radio_male.setChecked(true);
        } else {
            radio_male.setChecked(false);
        }
        if (sex_flag == 1) {
            radio_female.setChecked(true);
        } else {
            radio_female.setChecked(false);
        }

        //TODO Weight selection SeekBar menu
        seekBar_weight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                weight_number = seekBar_weight.getProgress();
                update_weight();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                weight_number = seekBar_weight.getProgress();
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //weight_number = seekBar_weight.getProgress();
                editor.putInt(PROGRESS2, seekBar.getProgress());
                editor.commit();
            }
        });

        //TODO Act mode selection Radio menu
        radio_auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "남 자", Toast.LENGTH_SHORT).show();
                Toast tMsg = Toast.makeText(SetupActivity.this, "  자   동  ", Toast.LENGTH_SHORT);
                tMsg.setGravity(Gravity.CENTER, 0, 0);
                LinearLayout view = (LinearLayout) tMsg.getView();
                tMsg.show();
                act_flag = 0;
                //mBluetoothLeService.writeGattCharacteristic(getWriteGattCharacteristic(), CMD_NODE_SCAN);
            }
        });

        radio_manual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "여 자", Toast.LENGTH_SHORT).show();
                Toast tMsg = Toast.makeText(SetupActivity.this, "  수   동  ", Toast.LENGTH_SHORT);
                tMsg.setGravity(Gravity.CENTER, 0, 0);
                LinearLayout view = (LinearLayout) tMsg.getView();
                tMsg.show();
                act_flag = 1;
                //mBluetoothLeService.writeGattCharacteristic(getWriteGattCharacteristic(), CMD_NODE_REGISTER);

            }
        });

        if (act_flag == 0) {
            radio_auto.setChecked(true);
        } else {
            radio_auto.setChecked(false);
        }
        if (act_flag == 1) {
            radio_manual.setChecked(true);
        } else {
            radio_manual.setChecked(false);
        }

        //TODO Auto mode selection Radio menu
        radio_random.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "남 자", Toast.LENGTH_SHORT).show();
                Toast tMsg = Toast.makeText(SetupActivity.this, "  랜   덤  ", Toast.LENGTH_SHORT);
                tMsg.setGravity(Gravity.CENTER, 0, 0);
                LinearLayout view = (LinearLayout) tMsg.getView();
                tMsg.show();
                auto_flag = 0;
                //mBluetoothLeService.writeGattCharacteristic(getWriteGattCharacteristic(), CMD_SCAN_FINISH);
            }
        });

        radio_sequential.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "여 자", Toast.LENGTH_SHORT).show();
                Toast tMsg = Toast.makeText(SetupActivity.this, "  순   차  ", Toast.LENGTH_SHORT);
                tMsg.setGravity(Gravity.CENTER, 0, 0);
                LinearLayout view = (LinearLayout) tMsg.getView();
                tMsg.show();
                auto_flag = 1;
                //mBluetoothLeService.writeGattCharacteristic(getWriteGattCharacteristic(), CMD_PLAY_NODE);
            }
        });

        if (auto_flag == 0) {
            radio_random.setChecked(true);
        } else {
            radio_random.setChecked(false);
        }
        if (auto_flag == 1) {
            radio_sequential.setChecked(true);
        } else {
            radio_sequential.setChecked(false);
        }

        //TODO C.D.S mode selection Radio menu
        radio_center.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "남 자", Toast.LENGTH_SHORT).show();
                Toast tMsg = Toast.makeText(SetupActivity.this, "  센   터  ", Toast.LENGTH_SHORT);
                tMsg.setGravity(Gravity.CENTER, 0, 0);
                LinearLayout view = (LinearLayout) tMsg.getView();
                tMsg.show();
                cds_flag = 0;
            }
        });

        radio_dispersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "여 자", Toast.LENGTH_SHORT).show();
                Toast tMsg = Toast.makeText(SetupActivity.this, "  분   산  ", Toast.LENGTH_SHORT);
                tMsg.setGravity(Gravity.CENTER, 0, 0);
                LinearLayout view = (LinearLayout) tMsg.getView();
                tMsg.show();
                cds_flag = 1;
            }
        });

        radio_scenario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "여 자", Toast.LENGTH_SHORT).show();
                Toast tMsg = Toast.makeText(SetupActivity.this, "  시 나 리 오  ", Toast.LENGTH_SHORT);
                tMsg.setGravity(Gravity.CENTER, 0, 0);
                LinearLayout view = (LinearLayout) tMsg.getView();
                tMsg.show();
                cds_flag = 2;
            }
        });

        if (cds_flag == 0) {
            radio_center.setChecked(true);
        } else {
            radio_center.setChecked(false);
        }
        if (cds_flag == 1) {
            radio_dispersion.setChecked(true);
        } else {
            radio_dispersion.setChecked(false);
        }
        if (cds_flag == 2) {
            radio_scenario.setChecked(true);
        } else {
            radio_scenario.setChecked(false);
        }

        //TODO Voice switch mode selection Radio menu
        OnOff.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(OnOff.isChecked())
                {
                    Toast tMsg = Toast.makeText(SetupActivity.this, "Voice ON", Toast.LENGTH_SHORT);
                    tMsg.setGravity(Gravity.CENTER, 0, 0);
                    LinearLayout view = (LinearLayout) tMsg.getView();
                    tMsg.show();
                    voice_flag = 1;
                }
                else {
                    Toast tMsg = Toast.makeText(SetupActivity.this, "Voice OFF", Toast.LENGTH_SHORT);
                    tMsg.setGravity(Gravity.CENTER, 0, 0);
                    LinearLayout view = (LinearLayout) tMsg.getView();
                    tMsg.show();
                    voice_flag = 0;
                }

                editor.putInt(VOICE1, voice_flag);
                editor.commit();

            }
        });

        //TODO color mode selection Radio menu
        radio_rand_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "남 자", Toast.LENGTH_SHORT).show();
                Toast tMsg = Toast.makeText(SetupActivity.this, "  회   전  ", Toast.LENGTH_SHORT);
                tMsg.setGravity(Gravity.CENTER, 0, 0);
                LinearLayout view = (LinearLayout) tMsg.getView();
                tMsg.show();
                color_flag = 0;
            }
        });

        radio_one_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "여 자", Toast.LENGTH_SHORT).show();
                Toast tMsg = Toast.makeText(SetupActivity.this, "  고   정  ", Toast.LENGTH_SHORT);
                tMsg.setGravity(Gravity.CENTER, 0, 0);
                LinearLayout view = (LinearLayout) tMsg.getView();
                tMsg.show();
                color_flag = 1;
            }
        });

        if (color_flag == 0) {
            radio_rand_color.setChecked(true);
        } else {
            radio_rand_color.setChecked(false);
        }
        if (color_flag == 1) {
            radio_one_color.setChecked(true);
        } else {
            radio_one_color.setChecked(false);
        }

        red_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast tMsg = Toast.makeText(SetupActivity.this, "  R  E  D  ", Toast.LENGTH_SHORT);
                tMsg.setGravity(Gravity.CENTER, 0, 0);
                LinearLayout view = (LinearLayout) tMsg.getView();
                tMsg.show();
            }
        });

        green_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast tMsg = Toast.makeText(SetupActivity.this, "  G R E E N  ", Toast.LENGTH_SHORT);
                tMsg.setGravity(Gravity.CENTER, 0, 0);
                LinearLayout view = (LinearLayout) tMsg.getView();
                tMsg.show();
            }
        });

        blue_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast tMsg = Toast.makeText(SetupActivity.this, "  B L U E  ", Toast.LENGTH_SHORT);
                tMsg.setGravity(Gravity.CENTER, 0, 0);
                LinearLayout view = (LinearLayout) tMsg.getView();
                tMsg.show();
            }
        });

        yellow_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast tMsg = Toast.makeText(SetupActivity.this, "  Y E L L O W  ", Toast.LENGTH_SHORT);
                tMsg.setGravity(Gravity.CENTER, 0, 0);
                LinearLayout view = (LinearLayout) tMsg.getView();
                tMsg.show();
            }
        });
        pink_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast tMsg = Toast.makeText(SetupActivity.this, "  P I N K  ", Toast.LENGTH_SHORT);
                tMsg.setGravity(Gravity.CENTER, 0, 0);
                LinearLayout view = (LinearLayout) tMsg.getView();
                tMsg.show();
            }
        });

        sky_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast tMsg = Toast.makeText(SetupActivity.this, "  S K Y  ", Toast.LENGTH_SHORT);
                tMsg.setGravity(Gravity.CENTER, 0, 0);
                LinearLayout view = (LinearLayout) tMsg.getView();
                tMsg.show();
            }
        });


        //TODO Sensitivity mode selection Radio menu
        radio_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "남 자", Toast.LENGTH_SHORT).show();
                Toast tMsg = Toast.makeText(SetupActivity.this, "  싱   글  ", Toast.LENGTH_SHORT);
                tMsg.setGravity(Gravity.CENTER, 0, 0);
                LinearLayout view = (LinearLayout) tMsg.getView();
                tMsg.show();
                sensitive_flag = 0;
                //mBluetoothLeService.writeGattCharacteristic(getWriteGattCharacteristic(), CommonData.OTA_DATA_REQ);
                //mBluetoothLeService.writeGattCharacteristic(getWriteGattCharacteristic(), SETUP_MODE_SINGLE);
            }
        });

        radio_nearing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "여 자", Toast.LENGTH_SHORT).show();
                Toast tMsg = Toast.makeText(SetupActivity.this, "  듀   얼  ", Toast.LENGTH_SHORT);
                tMsg.setGravity(Gravity.CENTER, 0, 0);
                LinearLayout view = (LinearLayout) tMsg.getView();
                tMsg.show();
                sensitive_flag = 1;
                //mBluetoothLeService.writeGattCharacteristic(getWriteGattCharacteristic(), SETUP_MODE_DUAL);
            }
        });

        if (sensitive_flag == 0) {
            radio_contact.setChecked(true);
        } else {
            radio_contact.setChecked(false);
        }
        if (sensitive_flag == 1) {
            radio_nearing.setChecked(true);
        } else {
            radio_nearing.setChecked(false);
        }


        //TODO timer selection SeekBar menu
        seekBar_timer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                timer_number = seekBar_timer.getProgress();
                update_timer();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                timer_number = seekBar_timer.getProgress();
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //timer_number = seekBar_timer.getProgress();
                editor.putInt(PROGRESS3, seekBar.getProgress());
                editor.commit();
            }
        });

    }

    public void update_age() {
        output_age.setText(new StringBuilder().append(age_number));
        // seekbar의 이동 값에 따라 TextView에 리턴
    }

    public void update_weight() {
        output_weight.setText(new StringBuilder().append(weight_number));
        // seekbar의 이동 값에 따라 TextView에 리턴
    }

    public void update_timer() {
        output_timer.setText(new StringBuilder().append(timer_number));
        // seekbar의 이동 값에 따라 TextView에 리턴
    }

    //TODO BLE Packet receive
    protected void onResume() {
        super.onResume();
        System.out.println("run on Resume function");
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "setup Connect request result=" + result + "*********************");

        }
        else {
            Log.d(TAG, "setup Connect request result= " + mBluetoothLeService);
        }
        seekBar_age.setProgress(preferences.getInt(PROGRESS1,0));
        seekBar_weight.setProgress(preferences.getInt(PROGRESS2,0));
        seekBar_timer.setProgress(preferences.getInt(PROGRESS3,0));
        if (voice_flag == 1) {
            OnOff.setChecked(true);
        }
        else if(voice_flag == 0) {
            OnOff.setChecked(false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    protected void onDestroy() {
        super.onDestroy();
        //unbindService(mServiceConnection);
        mBluetoothLeService = null;
        Log.d(TAG, "setup onDestroy request");
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

        System.out.println("By. SetUp activity : "+ hex_value);
        StringBuilder sb = new StringBuilder(packet.length * 2);

        System.out.println("By. SetUp HEX To ASCII : "+ hexToASCII(hex_value));

        Toast tMsg = Toast.makeText(SetupActivity.this, hexToASCII(hex_value), Toast.LENGTH_SHORT);
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
        System.out.println("setup getWriteGattCharacteristic for");
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
