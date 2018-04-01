package com.mc.mcrobotcontroller.data;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Meriam on 01/04/2018.
 */

public class AdapterDevice {

    private BluetoothDevice device;
    private String headerTitle;
    private boolean isHeader;
    private boolean isAvailable;

    public AdapterDevice(BluetoothDevice device, boolean isAvailable) {
        this.device = device;
        this.isHeader = false;
        this.isAvailable = isAvailable;
    }

    public AdapterDevice(String headerTitle) {
        this.headerTitle = headerTitle;
        this.isHeader = true;
        this.isAvailable = false;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public String getHeaderTitle() {
        return headerTitle;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public boolean isAvailable() {
        return isAvailable;
    }
}
