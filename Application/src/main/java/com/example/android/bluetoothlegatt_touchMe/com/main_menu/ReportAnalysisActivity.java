package com.example.android.bluetoothlegatt_touchMe.com.main_menu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bluetoothlegatt_touchMe.R;
import com.example.android.bluetoothlegatt_touchMe.com.BluetoothLeService;
import com.example.android.bluetoothlegatt_touchMe.com.DeviceControlActivity;
import com.example.android.bluetoothlegatt_touchMe.com.ViewHolder;

import java.lang.reflect.Array;
import java.util.Arrays;

import static com.example.android.bluetoothlegatt_touchMe.com.main_menu.NodeScanningActivity.scan_node_count;
import static com.example.android.bluetoothlegatt_touchMe.com.main_menu.RunActivity.NODE_COUNT_REQ_1;
import static com.example.android.bluetoothlegatt_touchMe.com.main_menu.RunActivity.NODE_COUNT_REQ_2;
import static com.example.android.bluetoothlegatt_touchMe.com.main_menu.RunActivity.NODE_COUNT_REQ_3;
import static com.example.android.bluetoothlegatt_touchMe.com.main_menu.RunActivity.NODE_COUNT_REQ_4;
import static com.example.android.bluetoothlegatt_touchMe.com.main_menu.RunActivity.NODE_COUNT_REQ_5;
import static com.example.android.bluetoothlegatt_touchMe.com.main_menu.RunActivity.NODE_COUNT_REQ_6;
import static com.example.android.bluetoothlegatt_touchMe.com.main_menu.RunActivity.report_hour;
import static com.example.android.bluetoothlegatt_touchMe.com.main_menu.RunActivity.report_mSec;
import static com.example.android.bluetoothlegatt_touchMe.com.main_menu.RunActivity.report_min;
import static com.example.android.bluetoothlegatt_touchMe.com.main_menu.RunActivity.report_sec;

/**
 * Created by GTO on 2020-01-22.
 */

public class ReportAnalysisActivity extends Activity {

    //TODO: BLE variable
    private boolean mConnected = false;
    private TextView mConnectionState;
    private BluetoothLeService mBluetoothLeService;
    private String mDeviceAddress;

    int standardSize_X, standardSize_Y;
    float density;

    LinearLayout mainLayout;
    Resources res;
    Animation growAnim;

    private TextView mTimeTextView;
    private TextView mNumberOfRun_NODE1, mNumberOfRun_NODE2, mNumberOfRun_NODE3, mNumberOfRun_NODE4, mNumberOfRun_NODE5, mNumberOfRun_NODE6;

    private TableRow mTable1, mTable2, mTable3, mTable4, mTable5, mTable6;

    private TextView mNode1_Rank, mNode2_Rank, mNode3_Rank, mNode4_Rank, mNode5_Rank, mNode6_Rank;

    private static int mNode1_ranking,mNode2_ranking, mNode3_ranking, mNode4_ranking, mNode5_ranking, mNode6_ranking;

    private static int[] score, rank;

    //public static int report_mSec, report_sec, report_min, report_hour;


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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 세로모드
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // 가로모드
        getStandardSize();

        TextView setup_txt = (TextView)findViewById(R.id.setup_txt);        // dynamic layout font
        setup_txt.setTextSize((float) (standardSize_X / 8)); setup_txt.setTextSize((float) (standardSize_Y / 18));      // dynamic layout font

        res = getResources();
        growAnim = AnimationUtils.loadAnimation(this, R.anim.grow);
        mainLayout = (LinearLayout)findViewById(R.id.mainLayout);

        mTimeTextView = (TextView) findViewById(R.id.timeView_report);

        mNode1_Rank = (TextView) findViewById(R.id.node1_Rank);        mNode2_Rank = (TextView) findViewById(R.id.node2_Rank);
        mNode3_Rank = (TextView) findViewById(R.id.node3_Rank);        mNode4_Rank = (TextView) findViewById(R.id.node4_Rank);
        mNode5_Rank = (TextView) findViewById(R.id.node5_Rank);        mNode6_Rank = (TextView) findViewById(R.id.node6_Rank);

        mNumberOfRun_NODE1 = (TextView) findViewById(R.id.node1_NumberOfRun);        mNumberOfRun_NODE2 = (TextView) findViewById(R.id.node2_NumberOfRun);
        mNumberOfRun_NODE3 = (TextView) findViewById(R.id.node3_NumberOfRun);        mNumberOfRun_NODE4 = (TextView) findViewById(R.id.node4_NumberOfRun);
        mNumberOfRun_NODE5 = (TextView) findViewById(R.id.node5_NumberOfRun);        mNumberOfRun_NODE6 = (TextView) findViewById(R.id.node5_NumberOfRun);

        mTable1 = (TableRow) findViewById(R.id.table_1);        mTable2 = (TableRow) findViewById(R.id.table_2);
        mTable3 = (TableRow) findViewById(R.id.table_3);        mTable4 = (TableRow) findViewById(R.id.table_4);
        mTable5 = (TableRow) findViewById(R.id.table_5);        mTable6 = (TableRow) findViewById(R.id.table_6);

        @SuppressLint("DefaultLocale") String result = String.format("%02d:%02d:%02d:%02d", report_hour, report_min, report_sec, report_mSec);

        mTimeTextView.setText(result);

        switch (scan_node_count) {
            case 1:
                score = new int[]{NODE_COUNT_REQ_1};
                rank = new int[]{1};
                break;
            case 2:
                score = new int[]{NODE_COUNT_REQ_1, NODE_COUNT_REQ_2};
                rank = new int[]{1, 1};
                break;
            case 3:
                score = new int[]{NODE_COUNT_REQ_1, NODE_COUNT_REQ_2, NODE_COUNT_REQ_3};
                rank = new int[]{1, 1, 1};
                break;
            case 4:
                score = new int[]{NODE_COUNT_REQ_1, NODE_COUNT_REQ_2, NODE_COUNT_REQ_3, NODE_COUNT_REQ_4};
                rank = new int[]{1, 1, 1, 1};
                break;
            case 5:
                score = new int[]{NODE_COUNT_REQ_1, NODE_COUNT_REQ_2, NODE_COUNT_REQ_3, NODE_COUNT_REQ_4, NODE_COUNT_REQ_5};
                rank = new int[]{1, 1, 1, 1, 1};
                break;
            case 6:
                score = new int[]{NODE_COUNT_REQ_1, NODE_COUNT_REQ_2, NODE_COUNT_REQ_3, NODE_COUNT_REQ_4, NODE_COUNT_REQ_5, NODE_COUNT_REQ_6};
                rank = new int[]{1, 1, 1, 1, 1, 1};
                break;
        }

        for(int i=0; i<score.length; i++){
            rank[i] = 1; //1등으로 초기화

            for (int j = 0; j < score.length; j++) { //기준데이터와 나머지데이터비교
                if(score[i]<score[j]){   //기준데이터가 나머지데이터라 비교했을때 적으면 rank[i] 카운트
                    rank[i]++; //COUNT
                }
            }
        }

        for (int i = 0; i < score.length; i++) {
            System.out.println(score[i]+"점 : "+rank[i]+"등");
        }

        switch (scan_node_count) {
            case 1:
                mNode1_ranking = rank[0];
                break;
            case 2:
                mNode1_ranking = rank[0];
                mNode2_ranking = rank[1];
                break;
            case 3:
                mNode1_ranking = rank[0];
                mNode2_ranking = rank[1];
                mNode3_ranking = rank[2];
                break;
            case 4:
                mNode1_ranking = rank[0];
                mNode2_ranking = rank[1];
                mNode3_ranking = rank[2];
                mNode4_ranking = rank[3];
                break;
            case 5:
                mNode1_ranking = rank[0];
                mNode2_ranking = rank[1];
                mNode3_ranking = rank[2];
                mNode4_ranking = rank[3];
                mNode5_ranking = rank[4];
                break;
            case 6:
                mNode1_ranking = rank[0];
                mNode2_ranking = rank[1];
                mNode3_ranking = rank[2];
                mNode4_ranking = rank[3];
                mNode5_ranking = rank[4];
                mNode6_ranking = rank[5];
                break;
        }

        switch (scan_node_count) {
            case 0:
                Toast.makeText(ReportAnalysisActivity.this, "No have scan node !", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                addItem("NODE 1", NODE_COUNT_REQ_1*10, Color.RED);
                mTable1.setVisibility(View.VISIBLE);

                mNumberOfRun_NODE1.setText(Integer.toString(NODE_COUNT_REQ_1));
                mNode1_Rank.setText(" "+Integer.toString(mNode1_ranking));
                break;
            case 2:
                addItem("NODE 1", NODE_COUNT_REQ_1*10, Color.RED);                addItem("NODE 2", NODE_COUNT_REQ_2*10, Color.GREEN);
                mTable1.setVisibility(View.VISIBLE);                mTable2.setVisibility(View.VISIBLE);

                mNumberOfRun_NODE1.setText(Integer.toString(NODE_COUNT_REQ_1));                mNumberOfRun_NODE2.setText(Integer.toString(NODE_COUNT_REQ_2));
                mNode1_Rank.setText(" "+Integer.toString(mNode1_ranking));                mNode2_Rank.setText(" "+Integer.toString(mNode2_ranking));
                break;
            case 3:
                addItem("NODE 1", NODE_COUNT_REQ_1*10, Color.RED);
                addItem("NODE 2", NODE_COUNT_REQ_2*10, Color.GREEN);
                addItem("NODE 3", NODE_COUNT_REQ_3*10, Color.BLUE);
                mTable1.setVisibility(View.VISIBLE);
                mTable2.setVisibility(View.VISIBLE);
                mTable3.setVisibility(View.VISIBLE);

                mNumberOfRun_NODE1.setText(Integer.toString(NODE_COUNT_REQ_1));                mNumberOfRun_NODE2.setText(Integer.toString(NODE_COUNT_REQ_2));                mNumberOfRun_NODE3.setText(Integer.toString(NODE_COUNT_REQ_3));
                mNode1_Rank.setText(" "+Integer.toString(mNode1_ranking));                mNode2_Rank.setText(" "+Integer.toString(mNode2_ranking));                mNode3_Rank.setText(" "+Integer.toString(mNode3_ranking));
                break;
            case 4:
                addItem("NODE 1", NODE_COUNT_REQ_1*10, Color.RED);                addItem("NODE 2", NODE_COUNT_REQ_2*10, Color.GREEN);
                addItem("NODE 3", NODE_COUNT_REQ_3*10, Color.BLUE);                addItem("NODE 4", NODE_COUNT_REQ_4*10, Color.YELLOW);
                mTable1.setVisibility(View.VISIBLE);                mTable2.setVisibility(View.VISIBLE);
                mTable3.setVisibility(View.VISIBLE);                mTable4.setVisibility(View.VISIBLE);

                mNumberOfRun_NODE1.setText(Integer.toString(NODE_COUNT_REQ_1));                mNumberOfRun_NODE2.setText(Integer.toString(NODE_COUNT_REQ_2));
                mNumberOfRun_NODE3.setText(Integer.toString(NODE_COUNT_REQ_3));                mNumberOfRun_NODE4.setText(Integer.toString(NODE_COUNT_REQ_4));
                mNode1_Rank.setText(" "+Integer.toString(mNode1_ranking));                mNode2_Rank.setText(" "+Integer.toString(mNode2_ranking));
                mNode3_Rank.setText(" "+Integer.toString(mNode3_ranking));                mNode4_Rank.setText(" "+Integer.toString(mNode4_ranking));
                break;
            case 5:
                addItem("NODE 1", NODE_COUNT_REQ_1*10, Color.RED);                addItem("NODE 2", NODE_COUNT_REQ_2*10, Color.GREEN);
                addItem("NODE 3", NODE_COUNT_REQ_3*10, Color.BLUE);                addItem("NODE 4", NODE_COUNT_REQ_4*10, Color.YELLOW);
                addItem("NODE 5", NODE_COUNT_REQ_5*10, Color.DKGRAY);  // pink

                mTable1.setVisibility(View.VISIBLE);                mTable2.setVisibility(View.VISIBLE);
                mTable3.setVisibility(View.VISIBLE);                mTable4.setVisibility(View.VISIBLE);
                mTable5.setVisibility(View.VISIBLE);

                mNumberOfRun_NODE1.setText(Integer.toString(NODE_COUNT_REQ_1));                mNumberOfRun_NODE2.setText(Integer.toString(NODE_COUNT_REQ_2));
                mNumberOfRun_NODE3.setText(Integer.toString(NODE_COUNT_REQ_3));                mNumberOfRun_NODE4.setText(Integer.toString(NODE_COUNT_REQ_4));
                mNumberOfRun_NODE5.setText(Integer.toString(NODE_COUNT_REQ_5));
                mNode1_Rank.setText(" "+Integer.toString(mNode1_ranking));                mNode2_Rank.setText(" "+Integer.toString(mNode2_ranking));
                mNode3_Rank.setText(" "+Integer.toString(mNode3_ranking));                mNode4_Rank.setText(" "+Integer.toString(mNode4_ranking));
                mNode5_Rank.setText(" "+Integer.toString(mNode5_ranking));
                break;
            case 6:
                addItem("NODE 1", NODE_COUNT_REQ_1*10, Color.RED);                addItem("NODE 2", NODE_COUNT_REQ_2*10, Color.GREEN);
                addItem("NODE 3", NODE_COUNT_REQ_3*10, Color.BLUE);                addItem("NODE 4", NODE_COUNT_REQ_4*10, Color.YELLOW);
                addItem("NODE 5", NODE_COUNT_REQ_5*10, Color.DKGRAY);                addItem("NODE 6", NODE_COUNT_REQ_6*10, Color.CYAN);     // sky
                mTable1.setVisibility(View.VISIBLE);                mTable2.setVisibility(View.VISIBLE);
                mTable3.setVisibility(View.VISIBLE);                mTable4.setVisibility(View.VISIBLE);
                mTable5.setVisibility(View.VISIBLE);                mTable6.setVisibility(View.VISIBLE);

                mNumberOfRun_NODE1.setText(Integer.toString(NODE_COUNT_REQ_1));                mNumberOfRun_NODE2.setText(Integer.toString(NODE_COUNT_REQ_2));
                mNumberOfRun_NODE3.setText(Integer.toString(NODE_COUNT_REQ_3));                mNumberOfRun_NODE4.setText(Integer.toString(NODE_COUNT_REQ_4));
                mNumberOfRun_NODE5.setText(Integer.toString(NODE_COUNT_REQ_5));                mNumberOfRun_NODE6.setText(Integer.toString(NODE_COUNT_REQ_6));
                mNode1_Rank.setText(" "+Integer.toString(mNode1_ranking));                mNode2_Rank.setText(" "+Integer.toString(mNode2_ranking));
                mNode3_Rank.setText(" "+Integer.toString(mNode3_ranking));                mNode4_Rank.setText(" "+Integer.toString(mNode4_ranking));
                mNode5_Rank.setText(" "+Integer.toString(mNode5_ranking));                mNode6_Rank.setText(" "+Integer.toString(mNode6_ranking));
                break;
        }
        /*
        addItem("Red", 180, Color.RED);
        addItem("Green", 290, Color.GREEN);
        addItem("Blue", 140, Color.BLUE);
        addItem("Yellow", 170, Color.YELLOW);
        addItem("Pink", 260, Color.DKGRAY);  // pink
        addItem("Sky", 20, Color.CYAN);     // sky
        */



    }

    private void addItem(String name, int value, int color) {
        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);


        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);


        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);


        // 텍스트뷰 추가
        TextView textView = new TextView(this);
        textView.setText(name);
        textView.setTextColor(Color.DKGRAY);
        params.width = 180;
        params.setMargins(2, 4, 0, 4);  // textView layout control
        itemLayout.addView(textView, params);


        // 프로그레스바 추가
        ProgressBar proBar = new ProgressBar(this, null,

                android.R.attr.progressBarStyleHorizontal);
        proBar.setIndeterminate(false);
        proBar.setMax(300);
        proBar.setProgress(300);
        if(color == Color.RED)
            proBar.setProgressDrawable(
                    getResources().getDrawable(R.drawable.progressbar_color_red));
        else if(color == Color.GREEN)
            proBar.setProgressDrawable(
                    getResources().getDrawable(R.drawable.progressbar_color_green));
        else if(color == Color.BLUE)
            proBar.setProgressDrawable(
                    getResources().getDrawable(R.drawable.progressbar_color_blue));

        else if(color == Color.YELLOW)
            proBar.setProgressDrawable(
                    getResources().getDrawable(R.drawable.progressbar_color_yellow));

        else if(color == Color.DKGRAY)  // PINK
            proBar.setProgressDrawable(
                    getResources().getDrawable(R.drawable.progressbar_color_pink));

        else if(color == Color.CYAN)    // SKY
            proBar.setProgressDrawable(
                    getResources().getDrawable(R.drawable.progressbar_color_sky));

        proBar.setAnimation(growAnim);


        params2.height = 3;
        params2.width = value * 3;
        params2.gravity = Gravity.CENTER_VERTICAL;
        itemLayout.addView(proBar, params2);

        mainLayout.addView(itemLayout, params3);
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);


        Toast.makeText(getApplicationContext(),
                "onWindowFocusChanged : " + hasFocus, Toast.LENGTH_SHORT).show();


        if(hasFocus) {
            growAnim.start();
        } else {
            growAnim.reset();
        }
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
