package com.mc.mcrobotcontroller.delegate;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.mc.mcrobotcontroller.MCRobotControllerApplication;
import com.mc.mcrobotcontroller.data.AdapterDevice;

import java.util.ArrayList;
import java.util.List;

import me.aflak.bluetooth.Bluetooth;
import me.aflak.bluetooth.DiscoveryCallback;

/**
 * Created by mcharfi on 31/03/2018.
 */

public class DeviceSelectionDelegate implements DiscoveryCallback {
    private MCRobotControllerApplication mApplication;
    private Bluetooth mBluetooth;
    private Context mContext;
    private List<BluetoothDevice> mScannedDevices = new ArrayList<>();

    private OnScanListener mOnScanListener;

    public DeviceSelectionDelegate(MCRobotControllerApplication application, Context context, OnScanListener onScanListener){
        mApplication = application;
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
        mScannedDevices.clear();
    }

    @Override
    public void onDiscoveryFinished() {
        List<AdapterDevice> result = new ArrayList<>();

        List<BluetoothDevice> paired = new ArrayList<>(mBluetooth.getPairedDevices());
        if (paired != null && !paired.isEmpty()) {

            List<Pair<String, String>> preferedDevices = mApplication.getPrefUtils().getSavedDevices();
            if (!preferedDevices.isEmpty()){
                List<BluetoothDevice> prefered = new ArrayList<>();
                for(Pair<String, String> preferedDevice : preferedDevices){
                    for(BluetoothDevice pairedDevice: paired){
                        if (pairedDevice.getAddress().equals(preferedDevice.second)){
                            prefered.add(pairedDevice);
                        }
                    }
                }
                //Prefered
                if (!prefered.isEmpty()){
                    result.add(new AdapterDevice("prefered devices"));
                    for (BluetoothDevice device : prefered) {
                        result.add(new AdapterDevice(device, true));
                    }
                }
            }
            //Paired
            result.add(new AdapterDevice("paired devices"));
            for (BluetoothDevice device : mBluetooth.getPairedDevices()) {
                result.add(new AdapterDevice(device, true));
            }
        }
        //Scanned
        if(!mScannedDevices.isEmpty()) {
            result.add(new AdapterDevice("scanned devices"));
            for (BluetoothDevice device : mScannedDevices) {
                result.add(new AdapterDevice(device,true));
            }
        }
        if (mOnScanListener != null){
            mOnScanListener.onScannedDevices(result);
        }
    }

    @Override
    public void onDeviceFound(BluetoothDevice device) {
//        Log.d(this.getClass().getCanonicalName(),"onDeviceFound");
        if (mScannedDevices.contains(device))
            return;
        mScannedDevices.add(device);

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
