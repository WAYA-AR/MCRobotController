package com.mc.mcrobotcontroller.data;

/**
 * Created by Meriam on 01/04/2018.
 */

public class AdapterDevice {

    private String title;
    private boolean isHeader;
    private boolean isAvailable;

    public AdapterDevice(String title, boolean isHeader, boolean isAvailable) {
        this.title = title;
        this.isHeader = isHeader;
        this.isAvailable = isAvailable;
    }

    public String getTitle() {
        return title;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public boolean isAvailable() {
        return isAvailable;
    }
}
