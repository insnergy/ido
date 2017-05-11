/*
 * Copyright (C) 2017 The InApi Project
 */
package com.insnergy.sample.presenter;

import android.support.annotation.NonNull;

import com.insnergy.sample.domainobj.Device;
import com.insnergy.sample.domainobj.Schedule;
import com.insnergy.sample.domainobj.ScheduleFreq;
import com.insnergy.sample.domainobj.ScheduleFreqDaily;
import com.insnergy.sample.domainobj.ScheduleFreqOnce;
import com.insnergy.sample.domainobj.ScheduleFreqWeekly;
import com.insnergy.sample.domainobj.ScheduleInfo;
import com.insnergy.sample.model.ApiCallback;
import com.insnergy.sample.model.ApiManager;
import com.insnergy.sample.model.DataManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SchedulePresenter {
    public static final String WEEKLY = "weekly";
    public static final String DAILY = "daily";
    public static final String ONCE = "once";
    public static final String ACTION_ON = "on";
    public static final String ACTION_OFF = "off";
    public static final String ATTR_MONTH = "dm1mi";
    public static final String ATTR_DAY = "dm1di";
    public static final String ATTR_HOUR = "dm60";
    private static final long MILLISECONDS = 1000;
    private static final long DAY_MILLISECONDS = 24 * 60 * 60 * MILLISECONDS;
    private static final long MONTH_MILLISECONDS = 31 * DAY_MILLISECONDS;

    private List<Device> mDevices;

    private static SchedulePresenter mInstance = new SchedulePresenter();

    public static SchedulePresenter getInstance() {
        return mInstance;
    }

    public SchedulePresenter() { }

    public static List<ScheduleFreq> scheduleToScheduleFreq(List<Schedule> schedules) {
        List<ScheduleFreq> scheduleFreqs = new ArrayList<>();
        for (Schedule schedule : schedules) {
            scheduleFreqs.add(scheduleToScheduleFreq(schedule));
        }
        return scheduleFreqs;
    }

    public static ScheduleFreq scheduleToScheduleFreq(Schedule schedule) {
        if (ScheduleInfo.FREQ.once.getCode().equals(schedule.getSched_freq())) {
            return new ScheduleFreqOnce(schedule);
        } else if (ScheduleInfo.FREQ.weekly.getCode().equals(schedule.getSched_freq())) {
            return new ScheduleFreqWeekly(schedule);
        } else {
            return new ScheduleFreqDaily(schedule);
        }
    }

    public void addSchedule(ScheduleFreq scheduleFreq, ApiCallback apiCallback) {
        DecimalFormat decimalFormat = new DecimalFormat("00");
        StringBuilder timeBuilder = new StringBuilder();
        timeBuilder.append(decimalFormat.format(scheduleFreq.getHour())).append(":")
                .append(decimalFormat.format(scheduleFreq.getMinute()));

        if (scheduleFreq instanceof ScheduleFreqOnce) {
            StringBuilder dateBuilder = new StringBuilder();
            dateBuilder.append(((ScheduleFreqOnce)scheduleFreq).getYear()).append("-")
                    .append(((ScheduleFreqOnce)scheduleFreq).getMonth()).append("-")
                    .append(((ScheduleFreqOnce)scheduleFreq).getDay());

            addSchedule(scheduleFreq.getDev_id(), scheduleFreq.getAction(), ScheduleInfo.FREQ.once,
                    dateBuilder.toString(), null,
                    timeBuilder.toString(), apiCallback);
        } else if (scheduleFreq instanceof ScheduleFreqWeekly) {
            StringBuilder weekBuilder = new StringBuilder();
            for (ScheduleInfo.WEEK week : ((ScheduleFreqWeekly)scheduleFreq).getWeeks()) {
                weekBuilder.append(week.getCode()).append(",");
            }

            addSchedule(scheduleFreq.getDev_id(), scheduleFreq.getAction(), ScheduleInfo.FREQ.weekly,
                    null, weekBuilder.toString(),
                    timeBuilder.toString(), apiCallback);
        } else {
            addSchedule(scheduleFreq.getDev_id(), scheduleFreq.getAction(), ScheduleInfo.FREQ.daily,
                    null, null,
                    timeBuilder.toString(), apiCallback);
        }
    }

    public void getSchedule(String deviceId, ApiCallback apiCallback) {
        HashMap<String, String> apiParams = new HashMap<String, String>();
        apiParams.put("user_id", DataManager.getInstance().getEmail());
        apiParams.put("dev_id", deviceId);
        ApiManager.getInstance().callApi("getDeviceAllSchedule", apiCallback, apiParams);
    }

    public void editSchedule(ScheduleFreq scheduleFreq, ApiCallback apiCallback) {
        DecimalFormat decimalFormat = new DecimalFormat("00");
        StringBuilder timeBuilder = new StringBuilder();
        timeBuilder.append(decimalFormat.format(scheduleFreq.getHour())).append(":")
                .append(decimalFormat.format(scheduleFreq.getMinute()));

        if (scheduleFreq instanceof ScheduleFreqOnce) {
            StringBuilder dateBuilder = new StringBuilder();
            dateBuilder.append(((ScheduleFreqOnce)scheduleFreq).getYear()).append("-")
                    .append(((ScheduleFreqOnce)scheduleFreq).getMonth()).append("-")
                    .append(((ScheduleFreqOnce)scheduleFreq).getDay());

            editSchedule(scheduleFreq.getTrigger_name(), scheduleFreq.getDev_id(),
                    scheduleFreq.getAction(), ScheduleInfo.FREQ.once,
                    dateBuilder.toString(), null,
                    timeBuilder.toString(), scheduleFreq.getEnable(), apiCallback);
        } else if (scheduleFreq instanceof ScheduleFreqWeekly) {
            StringBuilder weekBuilder = new StringBuilder();
            for (ScheduleInfo.WEEK week : ((ScheduleFreqWeekly)scheduleFreq).getWeeks()) {
                weekBuilder.append(week.getCode()).append(",");
            }

            editSchedule(scheduleFreq.getTrigger_name(), scheduleFreq.getDev_id(),
                    scheduleFreq.getAction(), ScheduleInfo.FREQ.weekly,
                    null, weekBuilder.toString(),
                    timeBuilder.toString(), scheduleFreq.getEnable(), apiCallback);
        } else {
            editSchedule(scheduleFreq.getTrigger_name(), scheduleFreq.getDev_id(),
                    scheduleFreq.getAction(), ScheduleInfo.FREQ.daily,
                    null, null,
                    timeBuilder.toString(), scheduleFreq.getEnable(), apiCallback);
        }
    }

    public void deleteSchedule(ScheduleFreq scheduleFreq, ApiCallback apiCallback) {
        HashMap<String, String> apiParams = new HashMap<String, String>();
        apiParams.put("user_id", DataManager.getInstance().getEmail());
        apiParams.put("trigger_name", scheduleFreq.getTrigger_name());
        ApiManager.getInstance().callApi("deleteSchedule", apiCallback, apiParams);
    }

    private void addSchedule(String deviceId,
                             ScheduleInfo.ACTION action,
                             ScheduleInfo.FREQ sched_freq,
                             String sched_date, //yyyy-MM-dd
                             String sched_weeks,
                             String sched_time, //HH:mm
                             ApiCallback apiCallback) {
        HashMap<String, String> apiParams = getScheduleHashMap(deviceId, action, sched_freq, sched_date, sched_weeks, sched_time);
        ApiManager.getInstance().callApi("addSchedule", apiCallback, apiParams);
    }

    private void editSchedule(String triggerName,
                              String deviceId,
                              ScheduleInfo.ACTION action,
                              ScheduleInfo.FREQ sched_freq,
                              String sched_date, //yyyy-MM-dd
                              String sched_weeks,
                              String sched_time, //HH:mm
                              boolean enable,
                              ApiCallback apiCallback) {
        HashMap<String, String> apiParams = getScheduleHashMap(deviceId, action, sched_freq, sched_date, sched_weeks, sched_time);
        apiParams.put("trigger_name", triggerName);
        apiParams.put("enable", (enable)? "yes": "no");
        ApiManager.getInstance().callApi("editSchedule", apiCallback, apiParams);
    }

    @NonNull
    private HashMap<String, String> getScheduleHashMap(String deviceId,
                                                       ScheduleInfo.ACTION action,
                                                       ScheduleInfo.FREQ sched_freq,
                                                       String sched_date,
                                                       String sched_weeks,
                                                       String sched_time) {
        HashMap<String, String> apiParams = new HashMap<>();
        apiParams.put("user_id", DataManager.getInstance().getEmail());
        apiParams.put("dev_id", deviceId);
        apiParams.put("sched_freq", sched_freq.getCode());
        switch (sched_freq) {
            case once:
                apiParams.put("sched_date", sched_date);
                break;
            case weekly:
                apiParams.put("sched_week", sched_weeks);
                break;
        }
        apiParams.put("sched_time", sched_time);
        apiParams.put("action", action.getCode());
        return apiParams;
    }
//    public void getHistoryStatistics(final ApiCallback apiCallback, Date startTime, Date endTime) {
//        long timeDifference = endTime.getTime() - startTime.getTime();
//        String attr;
//        if (timeDifference < DAY_MILLISECONDS) {
//            attr = ATTR_HOUR;
//        } else if (timeDifference < MONTH_MILLISECONDS) {
//            attr = ATTR_DAY;
//        } else {
//            attr = ATTR_MONTH;
//        }
//        getHistoryStatistics(apiCallback, attr, DatetimeUtils.dateToTimestamp(startTime), DatetimeUtils.dateToTimestamp(endTime));
//    }
//
//    public void getHistoryStatistics(final ApiCallback apiCallback, String attr, String startTime, String endTime) {
//        ApiCallback callback = ApiCallback.copyOf(apiCallback, new ApiCallback.OnCallback() {
//            @Override
//            public void onSuccess(ApiResult result) {
//                mDevices = result.getDevices();
//                if (apiCallback != null) {
//                    apiCallback.onSuccess(result);
//                }
//            }
//
//            @Override
//            public void onFail(ApiResult result) {
//                if (apiCallback != null) {
//                    apiCallback.onFail(result);
//                }
//            }
//        });
//        ApiManager.getInstance().callApi("getDeviceStatisticsExt", callback,
//                /*"email", "attr", "start_time", "end_time"*/
//                DataManager.getInstance().getEmail(), attr, startTime, endTime);
//    }
//
//
//    public List<Device> getDeviceList() {
//        if (mDevices != null) {
//            return mDevices;
//        } else {
//            return new ArrayList<Device>();
//        }
//    }
}
