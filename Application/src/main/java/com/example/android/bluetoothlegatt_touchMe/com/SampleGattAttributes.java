package com.example.android.bluetoothlegatt_touchMe.com;
import java.util.HashMap;
/**
 * Created by GTO on 2020-01-22.
 */

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class SampleGattAttributes {
    private static HashMap<String, String> attributes = new HashMap();
    public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    public static String FFF4_RATE_MEASUREMENT = "D973F2E1-B19E-11E2-9E96-0800200C9A66";
    public static String FFF3_RATE_MEASUREMENT = "D973F2E2-B19E-11E2-9E96-0800200C9A66";

    public static String JDY_TX_MEASUREMENT = "0000FFE1-0000-1000-8000-00805F9B34FB"; //JDY_RATE_MEASUREMENT
    public static String JDY_RX_MEASUREMENT = "0000FFE1-0000-1000-8000-00805F9B34FB"; //JDY_RATE_MEASUREMENT


    static {
        // Sample Services.
        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        attributes.put("D973F2E0-B19E-11E2-9E96-0800200C9A66", "FFF0");

        attributes.put("0000ffe0-0000-1000-8000-00805f9b34fb", "JDY unknown service");

        // Sample Characteristics.
        attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
        attributes.put("FFF4_RATE_MEASUREMENT", "FFF4");


    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}