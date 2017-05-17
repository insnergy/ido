/*
 * Copyright (C) 2017 The InApi Project
 */
package com.insnergy.sample.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.insnergy.sample.R;
import com.insnergy.sample.domainobj.Device;

import java.util.List;

public class DeviceItemAdapter extends ArrayAdapter<Device> {
    private LayoutInflater mLayoutInflater;

    public DeviceItemAdapter(Context context, List<Device> items) {
        super(context, R.layout.item_device, items);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_device, null);
        }

        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.imgIcon);
        imgIcon.setImageResource(R.mipmap.default_unknown);

        final Device item = getItem(position);

        TextView txtDevId = (TextView) convertView.findViewById(R.id.txtDevId);
        txtDevId.setText(item.getDev_id());
        TextView txtDevExtType = (TextView) convertView.findViewById(R.id.txtDevExtType);
        txtDevExtType.setText(item.getDev_ext_type().name());

        return convertView;
    }

}
