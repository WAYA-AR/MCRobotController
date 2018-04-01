package com.mc.mcrobotcontroller.ui;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mc.mcrobotcontroller.R;
import com.mc.mcrobotcontroller.delegate.DeviceSelectionDelegate;

import java.util.List;

/**
 * Created by mcharfi on 31/03/2018.
 */

public class DeviceSelectionActivity extends AppCompatActivity implements DeviceSelectionDelegate.OnScanListener{

    private DeviceSelectionDelegate mDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_selection);

        mDelegate = new DeviceSelectionDelegate(this, this);
    }

    @Override protected void onStart() {
        super.onStart();
        mDelegate.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDelegate.scanDevices();
    }

    @Override protected void onStop() {
        super.onStop();
        mDelegate.onStop();
    }

    @Override
    public void onBackPressed() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.quit_app_title)
                .setMessage(R.string.quit_app_message)
                .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            finishAffinity();
                        }else{
                            finish();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create();
        dialog.show();
    }

    @Override
    public void onScannedDevices(List<BluetoothDevice> devices) {

    }
}
