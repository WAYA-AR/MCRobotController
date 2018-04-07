package com.mc.mcrobotcontroller.delegate;

import com.mc.mcrobotcontroller.ui.JoystickActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Meriam on 07/04/2018.
 */

public class MCBluetoothMotorDelegate {

    private JoystickActivity.JoystickInterfaceView mJoystickView;
    private OnMessageCallback onMessageCallback;

    private boolean isWarningOn = false;

    public MCBluetoothMotorDelegate(JoystickActivity.JoystickInterfaceView mJoystickView, OnMessageCallback onMessageCallback) {
        this.mJoystickView = mJoystickView;
        this.onMessageCallback = onMessageCallback;
    }

    public String onMove(int angle, int strength) {

        int motor1Sens = 0;
        int motor1Strength = 0;
        int motor2Sens = 0;
        int motor2Strength = 0;

        int maxStrenth = 90;
        int minStrenth = 20;

        if (strength == 0){
            motor1Sens = 0;
            motor1Strength = 0;
            motor2Sens = 0;
            motor2Strength = 0;
        }else if (angle == 0){
            motor1Sens = 1;
            motor1Strength = maxStrenth;
            motor2Sens = 0;
            motor2Strength = 0;
        }else if (angle == 90){
            motor1Sens = 1;
            motor1Strength = maxStrenth;
            motor2Sens = 1;
            motor2Strength = maxStrenth;
        }else if (angle == 180){
            motor1Sens = 0;
            motor1Strength = 0;
            motor2Sens = 1;
            motor2Strength = maxStrenth;
        }else if (angle == 270){
            motor1Sens = 2;
            motor1Strength = maxStrenth;
            motor2Sens = 2;
            motor2Strength = maxStrenth;
        }else if (angle> 0 && angle<90){
            motor1Sens = 1;
            motor1Strength = maxStrenth;
            motor2Sens = 1;
            motor2Strength = map(angle, 0,90,minStrenth,maxStrenth);
        }else if (angle> 90 && angle<180){
            motor1Sens = 1;
            motor1Strength = map(angle, 90,180,maxStrenth,minStrenth);
            motor2Sens = 1;
            motor2Strength = maxStrenth;
        }else if (angle> 180 && angle<270){
            motor1Sens = 2;
            motor1Strength = map(angle, 180,270,minStrenth,maxStrenth);
            motor2Sens = 2;
            motor2Strength = maxStrenth;
        }else if (angle> 270 && angle<360){
            motor1Sens = 2;
            motor1Strength = maxStrenth;
            motor2Sens = 2;
            motor2Strength = map(angle, 270,360,maxStrenth,minStrenth);
        }

//                if (motor1Strength >20 && motor1Strength<50){
//                    motor1Strength = 50;
//                }
//
//                if (motor2Strength >20 && motor2Strength<50){
//                    motor2Strength = 50;
//                }

        String is1OnString = ""+motor1Sens;
        String percentage1String = String.format("%03d", motor1Strength);
        String is2OnString = ""+motor2Sens;
        String percentage2String = String.format("%03d", motor2Strength);

        String msg = "[" + is2OnString + "," + percentage2String + "," + is1OnString + "," + percentage1String + "]";
//                String msg = "[" + motor1Sens + "," + motor1Strength + "," + motor2Sens + "," + motor2Strength + "]";
        return msg;
    }


    private static int map(int x, int in_min, int in_max, int out_min, int out_max)
    {
        int res =  (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
        String msg = "[ map("+ x+","+in_min+","+in_max+","+ out_min+","+out_max+")] ====> "+res;

//        Display("map: " + msg);
        return res;
    }

    public static String getClearMessage(){
        return "#";
    }

    public static String getActivateMessage(){
        return "[0,0,0,0]";
    }

    public void onMessageReceived(String message){
        Pattern p = Pattern.compile("^DIS-?\\d+\\$$");
        Matcher m = p.matcher(message);
        if (m.matches()){
            String distanceString = message.substring(3,message.length()-1);

            final int distance = Integer.parseInt(distanceString);
            mJoystickView.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mJoystickView.setDistance(distance);
                }
            });
            boolean newWarningState = distance <= 10;
            if (newWarningState != isWarningOn){
                isWarningOn = newWarningState;
                onMessageCallback.onSend(isWarningOn ? "WARN1$" : "WARN0$");
            }
        }
    }


    public interface OnMessageCallback{
        void onSend(String message);
    }
}
