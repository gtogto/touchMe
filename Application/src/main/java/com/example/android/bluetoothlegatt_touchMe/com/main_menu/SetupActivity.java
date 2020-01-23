package com.example.android.bluetoothlegatt_touchMe.com.main_menu;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.example.android.bluetoothlegatt_touchMe.R;
import com.example.android.bluetoothlegatt_touchMe.com.BluetoothLeService;
import com.example.android.bluetoothlegatt_touchMe.com.DeviceControlActivity;

/**
 * Created by GTO on 2020-01-22.
 */

public class SetupActivity extends Activity {

    //TODO: BLE variable
    private boolean mConnected = false;
    private TextView mConnectionState;
    private BluetoothLeService mBluetoothLeService;
    private String mDeviceAddress;

    public static SeekBar seekBar_age;
    public static TextView output_age;
    public static int age_number;

    public static int sex_flag;

    public static SeekBar seekBar_weight;
    public static TextView output_weight;
    public static int weight_number;

    public static int act_flag;
    public static int auto_flag;
    public static int cds_flag;

    public static Switch OnOff;

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
        setContentView(R.layout.set_up);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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

        OnOff = (Switch)findViewById(R.id.switch_voice);

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
                age_number = seekBar_age.getProgress();
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
                weight_number = seekBar_weight.getProgress();
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
            }
        });

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
            }
        });

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

                }
                else {
                    Toast tMsg = Toast.makeText(SetupActivity.this, "Voice OFF", Toast.LENGTH_SHORT);
                    tMsg.setGravity(Gravity.CENTER, 0, 0);
                    LinearLayout view = (LinearLayout) tMsg.getView();
                    tMsg.show();
                }

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

        System.out.println("By. Setup Device activity : "+ hex_value);

        StringBuilder sb = new StringBuilder(packet.length * 2);


        return sb.toString();
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
