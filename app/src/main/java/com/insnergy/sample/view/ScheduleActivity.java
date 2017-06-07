/*
 * Copyright (C) 2017 The InApi Project
 */
package com.insnergy.sample.view;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.insnergy.sample.R;
import com.insnergy.sample.domainobj.ApiResult;
import com.insnergy.sample.domainobj.ScheduleFreq;
import com.insnergy.sample.domainobj.ScheduleFreqDaily;
import com.insnergy.sample.domainobj.ScheduleFreqOnce;
import com.insnergy.sample.domainobj.ScheduleFreqWeekly;
import com.insnergy.sample.domainobj.ScheduleInfo.WEEK;
import com.insnergy.sample.domainobj.ScheduleInfo.ACTION;
import com.insnergy.sample.model.ApiCallback;
import com.insnergy.sample.presenter.SchedulePresenter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class ScheduleActivity extends AppCompatActivity {
    private static final String TAG = "ScheduleActivity";
    public static final String DEV_ID = "dev_id";

    private static int sSelectedYear = 1970;
    private static int sSelectedMonth = 1;
    private static int sSelectedDay = 1;
    private static int sSelectedHour = 10;
    private static int sSelectedMinute = 10;
    private static TextView sTextViewSelectedDate;
    private static TextView sTextViewSelectedTime;
    private static Button sButton;

    private SchedulePresenter mScheduler = SchedulePresenter.getInstance();
    private ScheduleItemAdapter mScheduleItemAdapter;
    private String mDeviceId;

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            sSelectedHour = hourOfDay;
            sSelectedMinute = minute;
            sTextViewSelectedTime.setText(sSelectedHour + ":" + sSelectedMinute);
            checkIsButtonEnabled();
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            sSelectedYear = year;
            sSelectedMonth = month + 1; //magic number!
            sSelectedDay = day;
            sTextViewSelectedDate.setText(sSelectedYear + "-" + sSelectedMonth + "-" + sSelectedDay);
            checkIsButtonEnabled();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        sTextViewSelectedDate = (TextView)findViewById(R.id.textSelectedDate);
        sTextViewSelectedTime = (TextView)findViewById(R.id.textSelectedTime);
        sButton = (Button)findViewById(R.id.btnAdd);

        ((CheckBox)findViewById(R.id.checkBox)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setText((isChecked)? "開啟": "關閉");
            }
        });

        ListView listViewSchedule = (ListView) findViewById(R.id.listView);
        listViewSchedule.setOnItemClickListener(getItemClickListener());
        listViewSchedule.setOnItemLongClickListener(getItemLongClickListener());
        mScheduleItemAdapter = new ScheduleItemAdapter(this, new ArrayList<ScheduleFreq>());
        listViewSchedule.setAdapter(mScheduleItemAdapter);

        getDeviceIdFromIntent();
        getScheduleList();
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void addSchedule(final View view) {
        if (mDeviceId == null || !checkIsButtonEnabled())
            return;

        ACTION isEnable = (((CheckBox)findViewById(R.id.checkBox)).isChecked())? ACTION.ON: ACTION.OFF;

        // 單次排程設定
        addOnceSchedule(mDeviceId, isEnable, sSelectedYear, sSelectedMonth, sSelectedDay, sSelectedHour, sSelectedMinute);

        // 每日排程設定
//        addDailySchedule(mDeviceId, isEnable, sSelectedHour, sSelectedMinute);

        // 每週排程設定
//        Set<WEEK> weeks = new HashSet<>();
//        weeks.add(WEEK.MON);
//        weeks.add(WEEK.TUE);
//        addWeeklySchedule(mDeviceId, isEnable, weeks, sSelectedHour, sSelectedMinute);
    }

    private static boolean checkIsButtonEnabled() {
        if (sTextViewSelectedDate.getText().toString().contains("-") &&
                sTextViewSelectedTime.getText().toString().contains(":")) {
            sButton.setAlpha(1f);
            return true;
        } else {
            sButton.setAlpha(0.3f);
            return false;
        }
    }

    private void getScheduleList() {
        if (mDeviceId == null)
            return;

        mScheduler.getSchedule(mDeviceId, new ApiCallback() {
            @Override
            public void onSuccess(ApiResult apiResult) {
                mScheduleItemAdapter.clear();
                mScheduleItemAdapter.addAll(SchedulePresenter.scheduleToScheduleFreq(apiResult.getSchedules()));
                mScheduleItemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(ApiResult apiResult) { }
        });
    }

    private void editSchedule(final ScheduleFreq scheduleFreq) {
        mScheduler.editSchedule(scheduleFreq, new ApiCallback() {
            @Override
            public void onSuccess(ApiResult apiResult) {
                mScheduleItemAdapter.remove(scheduleFreq);
                mScheduleItemAdapter.add(SchedulePresenter.scheduleToScheduleFreq(apiResult.getSchedule()));
                mScheduleItemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(ApiResult apiResult) {

            }
        });
    }

    private void deleteSchedule(final ScheduleFreq scheduleFreq) {
        mScheduler.deleteSchedule(scheduleFreq, new ApiCallback() {
            @Override
            public void onSuccess(ApiResult apiResult) {
                mScheduleItemAdapter.remove(scheduleFreq);
                mScheduleItemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(ApiResult apiResult) { }
        });
    }

    private AdapterView.OnItemClickListener getItemClickListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ScheduleFreq item  = (ScheduleFreq)parent.getAdapter().getItem(position);
//                item.setHour(sSelectedHour);
//                item.setMinute(sSelectedMinute);
//                editSchedule(item);
            }
        };
    }

    private AdapterView.OnItemLongClickListener getItemLongClickListener() {
        return new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final ScheduleFreq item  = (ScheduleFreq)parent.getAdapter().getItem(position);
                showAlertDialogToDeleteSchedule(item);
                return true;
            }
        };
    }

    private void getDeviceIdFromIntent() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mDeviceId = extras.getString(DEV_ID);
        }
    }

    private void showAlertDialogToDeleteSchedule(final ScheduleFreq item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ScheduleActivity.this);
        builder.setCancelable(false);
        builder.setMessage(R.string.delete_message)
                .setPositiveButton(R.string.delete_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i(TAG, "yes delete");
                        deleteSchedule(item);
                    }
                })
                .setNegativeButton(R.string.delete_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i(TAG,  "not delete");
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void addOnceSchedule(String deviceId, ACTION isEnable,
                                 int selectedYear, int selectedMonth,
                                 int selectedDay, int selectedHour, int selectedMinute) {
        ScheduleFreqOnce freqOnce = new ScheduleFreqOnce(
                deviceId, isEnable, selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute);
        mScheduler.addSchedule(freqOnce, new ApiCallback() {
            @Override
            public void onSuccess(ApiResult apiResult) {
                getScheduleList();
            }

            @Override
            public void onFailure(ApiResult apiResult) { }
        });
    }

    private void addDailySchedule(String deviceId, ACTION isEnable,
                                  int selectedHour, int selectedMinute) {
        ScheduleFreqDaily freqDaily = new ScheduleFreqDaily(
                deviceId, isEnable, selectedHour, selectedMinute);
        mScheduler.addSchedule(freqDaily, new ApiCallback() {
            @Override
            public void onSuccess(ApiResult apiResult) {
                getScheduleList();
            }

            @Override
            public void onFailure(ApiResult apiResult) { }
        });
    }

    private void addWeeklySchedule(String deviceId, ACTION isEnable,
                                   Set<WEEK> weeks, int selectedHour, int selectedMinute) {
        ScheduleFreqWeekly freqWeekly = new ScheduleFreqWeekly(
                deviceId, isEnable, weeks, selectedHour, selectedMinute);
        mScheduler.addSchedule(freqWeekly, new ApiCallback() {
            @Override
            public void onSuccess(ApiResult apiResult) {
                getScheduleList();
            }

            @Override
            public void onFailure(ApiResult apiResult) { }
        });
    }
}
