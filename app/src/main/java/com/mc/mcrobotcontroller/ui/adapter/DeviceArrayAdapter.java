package com.mc.mcrobotcontroller.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mc.mcrobotcontroller.R;
import com.mc.mcrobotcontroller.data.AdapterDevice;

import java.util.List;

/**
 * Created by Meriam on 01/04/2018.
 */

public class DeviceArrayAdapter extends ArrayAdapter<AdapterDevice> {
    public DeviceArrayAdapter(Context context, List<AdapterDevice> list) {
        super(context, R.layout.device_cellview, list);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder; // to reference the child views for later actions

        if (v == null) {
            LayoutInflater vi =
                    (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.device_cellview, null);
            // cache view fields into the holder
            holder = new ViewHolder();
            holder.titleView =  v.findViewById(R.id.device_cellview_text);
            v.setTag(holder);
        }
        else {
            holder = (ViewHolder) v.getTag();
        }

        AdapterDevice device = getItem(position);
        holder.titleView.setText(device.getTitle());
        holder.titleView.setTypeface(null, device.isHeader() ? Typeface.BOLD : Typeface.NORMAL);
        holder.titleView.setTextSize(device.isHeader() ? 30 : 20);
        v.setBackgroundColor(ContextCompat.getColor(getContext(), (device.isHeader() || device.isAvailable()) ? android.R.color.white : android.R.color.darker_gray));


        return v;
    }

    static class ViewHolder {
        TextView titleView;
    }
}
