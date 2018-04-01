package com.mc.mcrobotcontroller;

import android.app.Application;

import com.mc.mcrobotcontroller.utils.PreferenceUtils;

/**
 * Created by Meriam on 01/04/2018.
 */

public class MCRobotControllerApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        getPrefUtils().initialise(this);
    }

    public PreferenceUtils getPrefUtils() {
        return PreferenceUtils.getInstance();
    }
}
