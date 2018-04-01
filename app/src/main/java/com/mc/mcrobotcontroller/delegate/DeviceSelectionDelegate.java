package com.mc.mcrobotcontroller.delegate;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;

import com.mc.mcrobotcontroller.data.AdapterDevice;

import java.util.ArrayList;
import java.util.List;

import me.aflak.bluetooth.Bluetooth;
import me.aflak.bluetooth.DiscoveryCallback;

/**
 * Created by mcharfi on 31/03/2018.
 */

public class DeviceSelectionDelegate implements DiscoveryCallback {
    private Bluetooth mBluetooth;
    private Context mContext;
    private List<BluetoothDevice> mDevices = new ArrayList<>();

    private OnScanListener mOnScanListener;

    public DeviceSelectionDelegate(Context context, OnScanListener onScanListener){
        mContext = context;
        mOnScanListener = onScanListener;
        mBluetooth = new Bluetooth(mContext);
        mBluetooth.setDiscoveryCallback(this);
    }


    //
    public void scanDevices(){
        if (mOnScanListener == null){
            return;
        }

        if(!mBluetooth.isEnabled())
            mBluetooth.enable();

        mBluetooth.startScanning();
    }

    public void onStart(){
        mBluetooth.onStart();
    }

    public void onStop(){
        if (mBluetooth != null || mBluetooth.isEnabled())
        mBluetooth.onStop();
    }
    //DiscoveryCallback
    @Override
    public void onDiscoveryStarted() {
        Log.d(this.getClass().getCanonicalName(),"onDiscoveryStarted");
        mDevices.clear();
    }

    @Override
    public void onDiscoveryFinished() {
        Log.d(this.getClass().getCanonicalName(),"onDiscoveryFinished");
        List<AdapterDevice> result = new ArrayList<>();

        result.add(new AdapterDevice("devices",true, false));
        for (BluetoothDevice device: mDevices){
//            String tmp = device.getName() +  " " + device.getAddress();
//            Log.d(this.getClass().getCanonicalName(),tmp);
            result.add(new AdapterDevice(device.getName(),false, true));
        }
        if (mOnScanListener != null){
            mOnScanListener.onScannedDevices(result);
        }
    }

    @Override
    public void onDeviceFound(BluetoothDevice device) {
//        Log.d(this.getClass().getCanonicalName(),"onDeviceFound");
        mDevices.add(device);

    }

    @Override
    public void onDevicePaired(BluetoothDevice device) {

    }

    @Override
    public void onDeviceUnpaired(BluetoothDevice device) {

    }

    @Override
    public void onError(String message) {
        Log.e(this.getClass().getCanonicalName(),"onError: " + message);

    }

    public interface OnScanListener{
       void onScannedDevices(List<AdapterDevice> devices);
    }
}
