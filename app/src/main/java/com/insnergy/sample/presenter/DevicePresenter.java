/*
 * Copyright (C) 2017 The InApi Project
 */
package com.insnergy.sample.presenter;

import android.util.Log;

import com.insnergy.sample.domainobj.Action;
import com.insnergy.sample.domainobj.DeviceInfo;
import com.insnergy.sample.model.ApiCallback;
import com.insnergy.sample.model.ApiManager;
import com.insnergy.sample.model.DataManager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

public class DevicePresenter {
    private static final String TAG = "DevicePresenter";
    private static DevicePresenter mInstance = new DevicePresenter();
    private ApiManager mApiManager = ApiManager.getInstance();

    public static DevicePresenter getInstance() {
        return mInstance;
    }

    public DevicePresenter() { }

    public void addDevice(String deviceId, DeviceInfo.Ext_Type deviceType, String deviceName, ApiCallback apiCallback) {
        HashMap<String, String> apiParams = new HashMap<String, String>();
        apiParams.put("user_id", DataManager.getInstance().getEmail());
        apiParams.put("widget_mode", Boolean.TRUE.toString());
        apiParams.put("timezone", getTimeZone());
        apiParams.put("dev_id", deviceId);
        apiParams.put("dev_ext_type", deviceType.getCode());
        apiParams.put("alias", encodeByURL(deviceName));
        mApiManager.callApi("addDevice", apiCallback, apiParams);
    }

    public void addWifiDevice(String gatewayId, String deviceId, DeviceInfo.Ext_Type deviceType, String deviceName, ApiCallback apiCallback) {
        HashMap<String, String> apiParams = new HashMap<String, String>();
        apiParams.put("user_id", DataManager.getInstance().getEmail());
        apiParams.put("widget_mode", Boolean.TRUE.toString());
        apiParams.put("dev_id", gatewayId);
        apiParams.put("dev_ext_type", deviceType.getCode());
        apiParams.put("timezone", getTimeZone());
        apiParams.put("alias", encodeByURL(deviceName));
        apiParams.put("icon", "");
        apiParams.put("child_dev_id[]", deviceId);
        mApiManager.callApi("addWiFiDevice", apiCallback, apiParams);
    }

    public void getDeviceList(ApiCallback apiCallback) {
        HashMap<String, String> apiParams = new HashMap<>();
        apiParams.put("user_id", DataManager.getInstance().getEmail());
        mApiManager.callApi("getMyDevices", apiCallback, apiParams);
    }

    public void getDeviceWidgets(ApiCallback apiCallback) {
        HashMap<String, String> apiParams = new HashMap<>();
        apiParams.put("user_id", DataManager.getInstance().getEmail());
        apiParams.put("type_code", "1");
        mApiManager.callApi("getDeviceWidget", apiCallback, apiParams);
    }

    public void getNewDevice(ApiCallback apiCallback) {
        HashMap<String, String> apiParams = new HashMap<>();
        apiParams.put("user_id", DataManager.getInstance().getEmail());
        mApiManager.callApiWithoutCache("getNewDevice", apiCallback, apiParams);
    }

    public void editDevice(String widget_inst_id, String alias, String icon, ApiCallback apiCallback) {
        HashMap<String, String> apiParams = new HashMap<String, String>();
        apiParams.put("user_id", DataManager.getInstance().getEmail());
        apiParams.put("widget_inst_id", widget_inst_id);
        apiParams.put("alias", encodeByURL(alias));
        apiParams.put("icon", icon);
        mApiManager.callApi("putDevice", apiCallback, apiParams);
    }

    public void editSubDevice(String widget_inst_id, String alias, String icon, ApiCallback apiCallback) {
        HashMap<String, String> apiParams = new HashMap<String, String>();
        apiParams.put("user_id", DataManager.getInstance().getEmail());
        apiParams.put("widget_inst_id", widget_inst_id);
        apiParams.put("sub_alias", encodeByURL(alias));
        apiParams.put("sub_icon", icon);
        mApiManager.callApi("putSubDevice", apiCallback, apiParams);
    }

    public void deleteDevice(String deviceId, ApiCallback apiCallback) {
        HashMap<String, String> apiParams = new HashMap<>();
        apiParams.put("user_id", DataManager.getInstance().getEmail());
        apiParams.put("dev_id", deviceId);
//        if (DeviceInfo.Category.SENSOR.equals(widget.getDev_category()) && (DeviceInfo.Ext_Type.SENSOR_6_IN_1.equals(widget.getDev_ext_type()) || DeviceInfo.Ext_Type.SENSOR_4_IN_1.equals(widget.getDev_ext_type()))) {
//            apiParams.put("action_type", "1");
//        }
        mApiManager.callApi("deleteDevice", apiCallback, apiParams);
    }

    public void controlDevice(final String deviceId, Action.Outlet_Type action, ApiCallback apiCallback) {
        HashMap<String, String> apiParams = new HashMap<String, String>();
        apiParams.put("user_id", DataManager.getInstance().getEmail());
        apiParams.put("dev_id", deviceId);
        apiParams.put("value", action.getCode());
        mApiManager.clearAllCacheThenCallApi("controlSyncSwitch", apiCallback, apiParams);
    }

    public void controlSiren(final String deviceId, Action.Siren_Voice_Type voiceType, ApiCallback apiCallback) {
        HashMap<String, String> apiParams = new HashMap<String, String>();
        apiParams.put("user_id", DataManager.getInstance().getEmail());
        apiParams.put("dev_id", deviceId);
        apiParams.put("value", voiceType.getCode());
        ApiManager.getInstance().callApi("controlSiren", apiCallback, apiParams);
    }

//    public void addBleDevice(QRCodeInfo gatewayInfo, String deviceName, ApiCallback apiCallback) {
//        HashMap<String, String> apiParams = new HashMap<String, String>();
//        apiParams.put("user_id", DataManager.getInstance().getEmail());
//        apiParams.put("dev_id", gatewayInfo.getDeviceId());
//        apiParams.put("dev_ext_type", gatewayInfo.getDeviceExtType());
//        apiParams.put("alias", encodeByURL(deviceName));
//        apiParams.put("widget_mode", "true");
//        apiParams.put("gw_dev_id", gatewayInfo.getGatewayId());
//        apiParams.put("bt_mac", gatewayInfo.getBluetoothMac());
//        apiParams.put("bt_scan_time", gatewayInfo.getScanTime());
//        apiParams.put("bt_keep_time", gatewayInfo.getKeepTime());
//        mApiManager.callApiWithoutCache("addBLEDevice", apiCallback, apiParams);
//    }

//    public void controlLossnayStatus(String deviceId, DeviceInfo.WidgetAttr widgetAttr, String value, ApiCallback apiCallback) {
//        HashMap<String, String> apiParams = new HashMap<String, String>();
//        apiParams.put("user_id", DataManager.getInstance().getEmail());
//        apiParams.put("dev_id", deviceId);
//        apiParams.put("attribute", widgetAttr.getCode());
//        apiParams.put("value", value);
//        mApiManager.callApi("controlLossnayStatus", apiCallback, apiParams);
//    }

    private static String getTimeZone() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.getDefault());
        Date currentLocalTime = calendar.getTime();
        DateFormat date = new SimpleDateFormat("Z");
        StringBuilder localTime = new StringBuilder("GMT");
        try {
            localTime.append(URLEncoder.encode(date.format(currentLocalTime), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int stringLenght = localTime.length();
        localTime.insert((stringLenght - 2), ":");
        return localTime.toString();
    }

    private static String encodeByURL(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (Exception e) {
            Log.e(TAG, "EncodeByURL Exception " + value);
        }
        return "";
    }
}
