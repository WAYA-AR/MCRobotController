package com.mc.mcrobotcontroller.utils;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.PatternSyntaxException;

/**
 * Created by Meriam on 01/04/2018.
 */

public class PreferenceUtils {

    public static final String SHARED_PREF_NAME = "mcrobotcontroller_Prefs";
    public static final String SHARED_PREF_DEVICE_LIST_KEY = "device_list_key";

    private static PreferenceUtils instance = new PreferenceUtils();

    private SharedPreferences sharedpreferences;
    private Context context;

    public void initialise(Context context) {
        this.context = context;
        sharedpreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

    }

    private PreferenceUtils() {
    }

    public static PreferenceUtils getInstance() {
        return instance;
    }


    //Device List
    private Set<String> getSavedDevicesSet() {
        return sharedpreferences.getStringSet(SHARED_PREF_DEVICE_LIST_KEY, new HashSet<String>());
    }

    private void saveDevicesSet(Set<String> set) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putStringSet(SHARED_PREF_DEVICE_LIST_KEY, set);
        editor.apply();
    }


    private void addDevice(String device) {
        Set<String> set = getSavedDevicesSet();
        if (set.contains(device)) {
            return;
        }
        set.add(device);
        saveDevicesSet(set);
    }

    private boolean isDeviceSaved(String device) {
        return getSavedDevicesSet().contains(device);
    }

    private void removeDevice(String device) {
        Set<String> set = getSavedDevicesSet();
        if (set.remove(device)) {
            saveDevicesSet(set);
        }
    }


    public List<Pair<String, String>> getSavedDevices() {
        List<String> list = new ArrayList<>(getSavedDevicesSet());
        List<Pair<String, String>> result = new ArrayList<>(list.size());
        for (String stringDevice : list) {
            Pair<String, String> device = parseDevice(stringDevice);
            if (device != null) {
                result.add(device);
            }
        }
        return result;
    }

    public void addDevice(BluetoothDevice device) {
        String stringDevice = formatDevice(device);
        addDevice(stringDevice);
    }

    public boolean isDeviceSaved(BluetoothDevice device) {
        return isDeviceSaved(formatDevice(device));
    }

    public void removeDevice(BluetoothDevice device) {
        removeDevice(formatDevice(device));
    }

    private String formatDevice(BluetoothDevice device) {
        return device.getName() + "~~~" + device.getAddress();
    }

    private Pair<String, String> parseDevice(String stringDevice) {
        try {
            String[] splits = stringDevice.split("~~~");
            if (splits != null && splits.length == 2) {
                return new Pair<>(splits[0], splits[1]);

            }
        } catch (PatternSyntaxException e) {
        }
        return null;
    }
}
