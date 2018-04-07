package com.mc.mcrobotcontroller.delegate;

import android.bluetooth.BluetoothDevice;
import android.os.Handler;

import com.mc.mcrobotcontroller.R;
import com.mc.mcrobotcontroller.ui.JoystickActivity;

import me.aflak.bluetooth.Bluetooth;
import me.aflak.bluetooth.BluetoothCallback;
import me.aflak.bluetooth.DeviceCallback;

/**
 * Created by Meriam on 07/04/2018.
 */

public class BluetoothCommunicationDelegate {

    BluetoothDevice mDevice;
    private Bluetooth mBluetooth;
    private JoystickActivity.JoystickInterfaceView mJoystickView;
    private MCBluetoothMotorDelegate mBluetoothMotorDelegate;

    public BluetoothCommunicationDelegate(BluetoothDevice device, JoystickActivity.JoystickInterfaceView joystickView) {
        mJoystickView = joystickView;
        this.mDevice = device;
        this.mBluetooth = new Bluetooth(mJoystickView.getActivity());
        mBluetoothMotorDelegate = new MCBluetoothMotorDelegate(joystickView, new MCBluetoothMotorDelegate.OnMessageCallback() {
            @Override
            public void onSend(String message) {
                if (mBluetooth.isEnabled()){
                    mBluetooth.send(message);
                }
            }
        });
    }

    public void onStart() {
        startBluetooth();
        if(mBluetooth.isEnabled()){
            connectToDevice();
        }
        else{
            mBluetooth.enable();
        }
    }

    public void disconnect(){
        mBluetooth.disconnect();
    }

    public void sendMessage(String message){
        mBluetooth.send(message);
    }


    private void startBluetooth() {
        mBluetooth.setCallbackOnUI(mJoystickView.getActivity());
        mBluetooth.setBluetoothCallback(mBluetoothCallback);
        mBluetooth.onStart();
    }


    private void connectToDevice() {
        mBluetooth.setDeviceCallback(mDevicenCallback);
        mBluetooth.connectToDevice(mDevice);
    }

    public void onStop() {
        if (mBluetooth != null || mBluetooth.isEnabled())
            mBluetooth.onStop();
    }


    public void onMove(int angle, int strength){
        sendMessage(mBluetoothMotorDelegate.onMove(angle, strength));
    }

    private DeviceCallback mDevicenCallback = new DeviceCallback() {
        @Override
        public void onDeviceConnected(BluetoothDevice device) {
            mJoystickView.hideLoading();
        }

        @Override
        public void onDeviceDisconnected(BluetoothDevice device, String message) {
            mJoystickView.showLoading(R.string.bluetooth_connecting);
            connectToDevice();
        }

        @Override
        public void onMessage(String message) {
            mBluetoothMotorDelegate.onMessageReceived(message);
        }

        @Override
        public void onError(String message) {
        }

        @Override
        public void onConnectError(final BluetoothDevice device, String message) {
            mJoystickView.displayMessage(R.string.bluetooth_connect_in_3sec);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    connectToDevice();
                }
            }, 3000);
        }
    };

    private BluetoothCallback mBluetoothCallback = new BluetoothCallback() {
        @Override
        public void onBluetoothTurningOn() {

        }

        @Override
        public void onBluetoothOn() {
            connectToDevice();
            mJoystickView.showLoading(R.string.bluetooth_connecting);
        }

        @Override
        public void onBluetoothTurningOff() {

        }

        @Override
        public void onBluetoothOff() {

        }

        @Override
        public void onUserDeniedActivation() {

        }
    };

}
