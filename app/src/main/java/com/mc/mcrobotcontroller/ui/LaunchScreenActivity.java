package com.mc.mcrobotcontroller.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.mc.mcrobotcontroller.R;
import com.mc.mcrobotcontroller.utils.PermissionUtils;

import java.util.ArrayList;

public class LaunchScreenActivity extends AppCompatActivity {

    private PermissionUtils mPermissionUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_screen);

        ArrayList<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.BLUETOOTH);
        permissions.add(Manifest.permission.BLUETOOTH_ADMIN);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        mPermissionUtils = new PermissionUtils(this, permissions, new PermissionUtils.OnPermissionGrantedHandler() {
            @Override
            public void onPermissionsGranted() {
                goToNextScreen();
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        mPermissionUtils.askPermissions();
                    }
                });
            }
        }, 1000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mPermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    void goToNextScreen(){
        startActivity(new Intent(LaunchScreenActivity.this, DeviceSelectionActivity.class));
    }

}
