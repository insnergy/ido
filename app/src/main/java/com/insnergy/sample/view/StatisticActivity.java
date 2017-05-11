/*
 * Copyright (C) 2017 The InApi Project
 */
package com.insnergy.sample.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.insnergy.sample.R;
import com.insnergy.sample.domainobj.ApiResult;
import com.insnergy.sample.domainobj.DeviceInfo;
import com.insnergy.sample.domainobj.DeviceInfo.WidgetAttr;
import com.insnergy.sample.domainobj.DeviceStatistics;
import com.insnergy.sample.domainobj.Widget;
import com.insnergy.sample.model.ApiCallback;
import com.insnergy.sample.presenter.DevicePresenter;
import com.insnergy.sample.presenter.StatisticPresenter;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class StatisticActivity extends AbstractAnimActivity {
    private static final String TAG = "StatisticActivity";

    private static GregorianCalendar sCalendar = new GregorianCalendar();
    private String mSelectedItem = "Hour";
    private String mStartTime;
    private String mEndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
        setCurrentTabEnabled(R.id.imgStat);

        final Spinner spinner = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> statTypeList = ArrayAdapter.createFromResource(StatisticActivity.this,
                R.array.statistic_type,
                android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(statTypeList);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedItem = spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        setupTextListener();

        setAvailableDeviceIdToEditText((EditText)findViewById(R.id.editTextDevId));
    }

    public void getStatistics(View view) {
        if (!isStartTimeValid() || !isEndTimeValid())
            return;

        mStartTime = String.valueOf(sCalendar.getTimeInMillis());
        mEndTime = String.valueOf(sCalendar.getTimeInMillis());
        String deviceId = ((EditText)findViewById(R.id.editTextDevId)).getText().toString();
        getStatistics(deviceId);
    }

    private void setupTextListener() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (isStartTimeValid() && isEndTimeValid()) {
                    findViewById(R.id.btnGetStatistic).setAlpha(1f);
                } else {
                    findViewById(R.id.btnGetStatistic).setAlpha(0.3f);
                }
            }
        };
        ((EditText)findViewById(R.id.editTextYearStart)).addTextChangedListener(textWatcher);
        ((EditText)findViewById(R.id.editTextMonthStart)).addTextChangedListener(textWatcher);
        ((EditText)findViewById(R.id.editTextDayStart)).addTextChangedListener(textWatcher);
        ((EditText)findViewById(R.id.editTextHourStart)).addTextChangedListener(textWatcher);
        ((EditText)findViewById(R.id.editTextMinStart)).addTextChangedListener(textWatcher);

        ((EditText)findViewById(R.id.editTextYearEnd)).addTextChangedListener(textWatcher);
        ((EditText)findViewById(R.id.editTextMonthEnd)).addTextChangedListener(textWatcher);
        ((EditText)findViewById(R.id.editTextDayEnd)).addTextChangedListener(textWatcher);
        ((EditText)findViewById(R.id.editTextHourEnd)).addTextChangedListener(textWatcher);
        ((EditText)findViewById(R.id.editTextMinEnd)).addTextChangedListener(textWatcher);
    }

    private boolean isStartTimeValid() {
        String yearStr = ((EditText)findViewById(R.id.editTextYearStart)).getText().toString();
        if (yearStr.isEmpty()) {
            return false;
        }
        String monthStr = ((EditText)findViewById(R.id.editTextMonthStart)).getText().toString();
        if (monthStr.isEmpty() || Integer.parseInt(monthStr) == 0 || Integer.parseInt(monthStr) > 12) {
            return false;
        }
        String dayStr = ((EditText)findViewById(R.id.editTextDayStart)).getText().toString();
        if (dayStr.isEmpty() || Integer.parseInt(dayStr) == 0 || Integer.parseInt(dayStr) > 31) {
            return false;
        }
        String hourStr = ((EditText)findViewById(R.id.editTextHourStart)).getText().toString();
        if (hourStr.isEmpty() || Integer.parseInt(hourStr) > 24) {
            return false;
        }
        String minStr = ((EditText)findViewById(R.id.editTextMinStart)).getText().toString();
        if (minStr.isEmpty() || Integer.parseInt(minStr) > 59) {
            return false;
        }

        sCalendar.set(Integer.parseInt(yearStr), Integer.parseInt(monthStr), Integer.parseInt(dayStr));
        sCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hourStr));
        sCalendar.set(Calendar.MINUTE, Integer.parseInt(minStr));
        return true;
    }

    private boolean isEndTimeValid() {
        String yearStr = ((EditText)findViewById(R.id.editTextYearEnd)).getText().toString();
        if (yearStr.isEmpty()) {
            return false;
        }
        String monthStr = ((EditText)findViewById(R.id.editTextMonthEnd)).getText().toString();
        if (monthStr.isEmpty() || Integer.parseInt(monthStr) == 0 || Integer.parseInt(monthStr) > 12) {
            return false;
        }
        String dayStr = ((EditText)findViewById(R.id.editTextDayEnd)).getText().toString();
        if (dayStr.isEmpty() || Integer.parseInt(dayStr) == 0 || Integer.parseInt(dayStr) > 31) {
            return false;
        }
        String hourStr = ((EditText)findViewById(R.id.editTextHourEnd)).getText().toString();
        if (hourStr.isEmpty() || Integer.parseInt(hourStr) > 24) {
            return false;
        }
        String minStr = ((EditText)findViewById(R.id.editTextMinEnd)).getText().toString();
        if (minStr.isEmpty() || Integer.parseInt(minStr) > 59) {
            return false;
        }

        sCalendar.set(Integer.parseInt(yearStr), Integer.parseInt(monthStr), Integer.parseInt(dayStr));
        sCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hourStr));
        sCalendar.set(Calendar.MINUTE, Integer.parseInt(minStr));
        return true;
    }

    private void getStatistics(String deviceId) {
        StatisticPresenter.getInstance().getDeviceStatistics(createDevStatForKWH(deviceId), new ApiCallback() {
            @Override
            public void onSuccess(ApiResult apiResult) {
                Gson gson = new Gson();
                startActivity(new Intent(getApplicationContext(), LineChartActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        .putExtra(LineChartActivity.DATA, gson.toJson(apiResult))
                );
            }

            @Override
            public void onFailure(ApiResult apiResult) {

            }
        });
    }

    private DeviceStatistics createDevStatForKWH(String deviceId) {
        DeviceStatistics stat = new DeviceStatistics();
        stat.setDev_id(deviceId);
        stat.setAttributes(WidgetAttr.KWH);
        stat.setStats_type(DeviceStatistics.Statistics_Type.getEnum(mSelectedItem));
        stat.setStart_time(mStartTime);
        stat.setEnd_time(mEndTime);
        return stat;
    }

    // 僅電力計裝置可查詢統計資料，此 method 會找出第一個電力計裝置進行查詢
    private void setAvailableDeviceIdToEditText(final EditText editText) {
        DevicePresenter.getInstance().getDeviceWidgets(new ApiCallback() {
            @Override
            public void onSuccess(ApiResult apiResult) {
                for (Widget widget : apiResult.getWidgets()) {
                    if (DeviceInfo.Ext_Type.OUTLET_02.equals(widget.getDev_ext_type()) ||
                            DeviceInfo.Ext_Type.OUTLET_I18N_0910.equals(widget.getDev_ext_type())) {
                        editText.setText(widget.getDev_id());
                        getStatistics(widget.getDev_id());
                        break;
                    }
                }
            }

            @Override
            public void onFailure(ApiResult apiResult) { }
        });
    }
}
