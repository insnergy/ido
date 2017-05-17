/*
 * Copyright (C) 2017 The InApi Project
 */
package com.insnergy.sample.presenter;

import com.insnergy.sample.domainobj.DeviceStatistics;
import com.insnergy.sample.model.ApiCallback;
import com.insnergy.sample.model.ApiManager;
import com.insnergy.sample.model.DataManager;

import java.util.HashMap;

public class StatisticPresenter {
    private static final String TAG = "StatisticPresenter";
    private static StatisticPresenter mInstance = new StatisticPresenter();

    private StatisticPresenter() { }

    public static StatisticPresenter getInstance() {
        return mInstance;
    }

    public void getDeviceHistoryData(DeviceStatistics devStatistics, ApiCallback apiCallback) {
        HashMap<String, String> apiParams = new HashMap<>();
        apiParams.put("email", DataManager.getInstance().getEmail());
        apiParams.put("dev_ids", devStatistics.getDev_id());
        apiParams.put("attr", devStatistics.getStats_type().getCode());
        apiParams.put("start_time", devStatistics.getStart_time());
        apiParams.put("end_time", devStatistics.getEnd_time());
        ApiManager.getInstance().callApi("getDeviceHistoryExt", apiCallback, apiParams);
    }

    public void getDeviceStatistics(DeviceStatistics devStatistics, ApiCallback apiCallback) {
        HashMap<String, String> apiParams = new HashMap<String, String>();
        apiParams.put("user_id", DataManager.getInstance().getEmail());
        apiParams.put("dev_id", devStatistics.getDev_id());
        apiParams.put("attributes", devStatistics.getAttributes().getCode());
        apiParams.put("stats_type", devStatistics.getStats_type().name().substring(0, 1));
        apiParams.put("start_time", devStatistics.getStart_time());
        apiParams.put("end_time", devStatistics.getEnd_time());
        ApiManager.getInstance().callApi("getStatisticsData", apiCallback, apiParams);
    }
}
