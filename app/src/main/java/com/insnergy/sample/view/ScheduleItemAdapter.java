/*
 * Copyright (C) 2017 The InApi Project
 */
package com.insnergy.sample.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.insnergy.sample.R;
import com.insnergy.sample.domainobj.ScheduleFreq;

import java.util.List;

public class ScheduleItemAdapter extends ArrayAdapter<ScheduleFreq> {
    private LayoutInflater mLayoutInflater;

    public ScheduleItemAdapter(Context context, List<ScheduleFreq> items) {
        super(context, R.layout.item_schedule, items);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_schedule, null);
        }

        ScheduleFreq item = getItem(position);
//        if (...) {
//            ImageView imgIcon = (ImageView) convertView.findViewById(R.id.imgIcon);
//            imgIcon.setImageResource(R.mipmap.clock);
//        }

        TextView textFreq = (TextView) convertView.findViewById(R.id.txtFreq);
        textFreq.setText(item.getSched_freq());
        TextView textTime = (TextView) convertView.findViewById(R.id.textTime);
        textTime.setText(item.getHour() + ":" + item.getMinute());

        return convertView;
    }
}
