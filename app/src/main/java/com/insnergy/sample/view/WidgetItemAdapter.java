/*
 * Copyright (C) 2017 The InApi Project
 */
package com.insnergy.sample.view;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.insnergy.sample.R;
import com.insnergy.sample.domainobj.Action;
import com.insnergy.sample.domainobj.ApiResult;
import com.insnergy.sample.domainobj.DeviceInfo;
import com.insnergy.sample.domainobj.Widget;
import com.insnergy.sample.model.ApiCallback;
import com.insnergy.sample.presenter.DevicePresenter;

import java.util.List;

public class WidgetItemAdapter extends ArrayAdapter<Widget> {
    private LayoutInflater mLayoutInflater;

    public WidgetItemAdapter(Context context, List<Widget> items) {
        super(context, R.layout.item_schedule, items);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_device, null);
        }

        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.imgIcon);
        imgIcon.setImageResource(R.mipmap.ic_launcher);

        final Widget item = getItem(position);
        if (DeviceInfo.Ext_Type.OUTLET_02.equals(item.getDev_ext_type()) ||
                DeviceInfo.Ext_Type.OUTLET_I18N_0910.equals(item.getDev_ext_type())) {
            if (Action.Outlet_Type.ON.equals(item.getStatus())) {
                imgIcon.setImageResource(R.mipmap.power_on);
                imgIcon.setOnClickListener(getPowerClickListener(item, Action.Outlet_Type.OFF));
            } else if (Action.Outlet_Type.OFF.equals(item.getStatus())) {
                imgIcon.setImageResource(R.mipmap.power_off);
                imgIcon.setOnClickListener(getPowerClickListener(item, Action.Outlet_Type.ON));
            }
        }
        if (DeviceInfo.Ext_Type.CONTROLLER_SIREN.equals(item.getDev_ext_type())) {
            imgIcon.setImageResource(R.mipmap.siren);
            imgIcon.setOnClickListener(getSirenClickListener(item, Action.Siren_Voice_Type.DI_DI));
        }

        TextView txtDevId = (TextView) convertView.findViewById(R.id.txtDevId);
        txtDevId.setText(item.getDev_id());
        TextView txtDevExtType = (TextView) convertView.findViewById(R.id.txtDevExtType);
        txtDevExtType.setText(item.getDev_ext_type().name());
        TextView txtDevStatus = (TextView) convertView.findViewById(R.id.txtDevStatus);
        txtDevStatus.setText(item.getStatus());
        txtDevStatus.setTextColor(("offline".equals(item.getStatus()))? Color.RED : Color.BLACK);

        return convertView;
    }

    private View.OnClickListener getPowerClickListener(final Widget item, final Action.Outlet_Type action) {
        return new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                DevicePresenter.getInstance().controlDevice(item.getDev_id(), action, new ApiCallback() {
                    @Override
                    public void onSuccess(ApiResult apiResult) {
                        item.setStatus((Action.Outlet_Type.ON.equals(apiResult.getValue()))? "on": "off");
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(ApiResult apiResult) { }
                });
            }
        };
    }

    private View.OnClickListener getSirenClickListener(final Widget item, final Action.Siren_Voice_Type action) {
        return new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                DevicePresenter.getInstance().controlSiren(item.getDev_id(), action, new ApiCallback() {
                    @Override
                    public void onSuccess(ApiResult apiResult) { }

                    @Override
                    public void onFailure(ApiResult apiResult) { }
                });
            }
        };
    }
}
