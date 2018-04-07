package com.mc.mcrobotcontroller.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mc.mcrobotcontroller.R;
import com.mc.mcrobotcontroller.delegate.BluetoothCommunicationDelegate;
import com.mc.mcrobotcontroller.delegate.MCBluetoothMotorDelegate;
import com.mc.mcrobotcontroller.utils.ToolsUtils;

import io.github.controlwear.virtual.joystick.android.JoystickView;

/**
 * Created by Meriam on 07/04/2018.
 */

public class JoystickActivity  extends AppCompatActivity {

    private BluetoothCommunicationDelegate mDelegate;

    private TextView distanceTextView;
    private Button clearButton;
    private CheckBox onButton;
    private JoystickView joystickView;
    private View mLoadingView;
    private TextView mLoadingTextView;

    private boolean forceDisconnect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joystick);

        if(getIntent().getExtras()!=null) {
            BluetoothDevice device = getIntent().getExtras().getParcelable(ToolsUtils.EXTRA_DEVICE_KEY);
            mDelegate = new BluetoothCommunicationDelegate(device, mJoystickView);
        }else{
            forceDisconnect = true;
            onBackPressed();
            return;
        }

        distanceTextView = findViewById(R.id.distance_text);
        onButton = findViewById(R.id.activate_button);
        clearButton = findViewById(R.id.reset_button);
        joystickView = findViewById(R.id.joystick);
        mLoadingView = findViewById(R.id.loading_view);
        mLoadingTextView = findViewById(R.id.loading_text);


        onButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked){
                    clear();
                }
                activate();
            }
        });
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            clear();
            }
        });


        joystickView.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                String msgM = "onMove=[" + angle + "," + strength + "]";


                if( !onButton.isChecked()){
                    return;
                }
                mDelegate.onMove(angle, strength);

            }
        }, 1000);
    }



    private void clear(){
        mDelegate.sendMessage(MCBluetoothMotorDelegate.getClearMessage());
    }

    private void activate(){
        mDelegate.sendMessage(MCBluetoothMotorDelegate.getActivateMessage());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDelegate.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mDelegate.onStop();
    }

    private void quit(){
        super.onBackPressed();
    }
    public void onBackPressed() {
        if (forceDisconnect){
            quit();
            return;
        }
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.disconnect_device_title)
                .setMessage(R.string.disconnect_device_message)
                .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        quit();
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

    JoystickInterfaceView mJoystickView = new JoystickInterfaceView() {

        @Override
        public void showLoading(String message) {
            mLoadingView.setVisibility(View.VISIBLE);
            mLoadingTextView.setText(message);
        }

        @Override
        public void showLoading(int messageID) {
            mLoadingView.setVisibility(View.VISIBLE);
            mLoadingTextView.setText(messageID);
        }

        @Override
        public void displayMessage(String message) {
            Toast.makeText(JoystickActivity.this, message, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void displayMessage(int messageID) {
            Toast.makeText(JoystickActivity.this, messageID, Toast.LENGTH_SHORT).show();
        }

        @Override
        public Activity getActivity() {
            return JoystickActivity.this;
        }

        @Override
        public void hideLoading() {
            mLoadingView.setVisibility(View.GONE);
        }

        @Override
        public void setDistance(int distance) {
            distanceTextView.setText("Distance: "+ distance + "cm");
        }
    };

    public interface JoystickInterfaceView{
        void showLoading(String message);
        void showLoading(int messageID);
        void displayMessage(String message);
        void displayMessage(int messageID);
        void setDistance(int distance);
        void hideLoading();
        Activity getActivity();
    }

}
