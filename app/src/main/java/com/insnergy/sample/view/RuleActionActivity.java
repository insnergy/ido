/*
 * Copyright (C) 2017 The InApi Project
 */
package com.insnergy.sample.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.Spinner;

import com.insnergy.sample.R;
import com.insnergy.sample.domainobj.Action;
import com.insnergy.sample.domainobj.ApiResult;
import com.insnergy.sample.domainobj.DeviceInfo.Ext_Type;
import com.insnergy.sample.domainobj.Rule;
import com.insnergy.sample.domainobj.Widget;
import com.insnergy.sample.model.ApiCallback;
import com.insnergy.sample.model.DataManager;
import com.insnergy.sample.presenter.DevicePresenter;
import com.insnergy.sample.presenter.RulePresenter;

import java.util.ArrayList;
import java.util.List;

public class RuleActionActivity extends AppCompatActivity {
    public static final String PLAN_ID = "planId";
    private static final String SPLITTER = "：\n";
    private static final int DEVICEID_OFFSET_OF_SPLITTER = 2;

    private static final String RULE_OUTLET_KWH_GE10 = "建立規則 即時功率≧10觸發" + SPLITTER;
    private static final String RULE_OUTLET_KWH_GE100 = "建立規則 即時功率≧100觸發" + SPLITTER;
    private static final String RULE_PIR = "建立移動偵測規則" + SPLITTER;
    private static final String RULE_SENSOR_TEMPERATURE = "建立規則 溫度≧25觸發" + SPLITTER;
    private static final String RULE_SENSOR_HUMIDITY = "建立規則 濕度≧80觸發" + SPLITTER;

    private static final String SIREN_DIDI = "Siren didi聲" + SPLITTER;
    private static final String SIREN_DOOR_BELL = "Siren 門鈴聲" + SPLITTER;
    private static final String OUTLET_CLOSE = "關閉電力計" + SPLITTER;
    private static final String OUTLET_OPEN = "開啟電力計" + SPLITTER;

    private RulePresenter mRulePresenter = RulePresenter.getInstance();
    private String mPlanId = "";
    private RuleItemAdapter mRuleItemAdapter;
    private String mSelectedRuleDevId = "";
    private String mSelectedActionDevId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rule_action);

        ExpandableListView expListView = (ExpandableListView)findViewById(R.id.expListView);
        mRuleItemAdapter = new RuleItemAdapter(getLayoutInflater(), new ArrayList<Rule>());
        expListView.setAdapter(mRuleItemAdapter);
        expListView.setOnItemLongClickListener(getItemLongClickListener());

        getRuleActionFromPlanId();

        initSpinnerItems();
    }

    public void addRule(View view) {
        try {
            String ruleStr = ((Spinner)findViewById(R.id.spinnerRule)).getSelectedItem().toString();
            RuleActionGenerator ruleActionGenerator = RuleActionGenerator.init();
            if (ruleStr!=null && ruleStr.startsWith(RULE_PIR)) {
                ruleActionGenerator.generatePirRule(mSelectedRuleDevId)
                        .addNoticeAction(DataManager.getInstance().getEmail(), "PIR 移動偵測通知");
            } else if (ruleStr!=null && ruleStr.startsWith(RULE_OUTLET_KWH_GE10)) {
                ruleActionGenerator.generateOutletRule(mSelectedRuleDevId, Rule.RULE_LIST.GE, "10")
                        .addNoticeAction(DataManager.getInstance().getEmail(), "電力計功率≧10通知");
            } else if (ruleStr!=null && ruleStr.startsWith(RULE_OUTLET_KWH_GE100)) {
                ruleActionGenerator.generateOutletRule(mSelectedRuleDevId, Rule.RULE_LIST.GE, "100")
                        .addNoticeAction(DataManager.getInstance().getEmail(), "電力計功率≧100通知");
            } else if (ruleStr!=null && ruleStr.startsWith(RULE_SENSOR_TEMPERATURE)) {
                ruleActionGenerator.generateTemperatureRule(mSelectedRuleDevId, Rule.RULE_LIST.GE, "25")
                        .addNoticeAction(DataManager.getInstance().getEmail(), "溫度≧25通知");
            } else if (ruleStr!=null && ruleStr.startsWith(RULE_SENSOR_HUMIDITY)) {
                ruleActionGenerator.generateHumidityRule(mSelectedRuleDevId, Rule.RULE_LIST.GE, "80")
                        .addNoticeAction(DataManager.getInstance().getEmail(), "濕度≧80通知");
            }

            String actionStr = ((Spinner)findViewById(R.id.spinnerAction)).getSelectedItem().toString();
            if (actionStr!=null && actionStr.startsWith(OUTLET_CLOSE)) {
                ruleActionGenerator.addOutletAction(mSelectedActionDevId, Action.Outlet_Type.OFF);
            } else if (actionStr!=null && actionStr.startsWith(OUTLET_OPEN)) {
                ruleActionGenerator.addOutletAction(mSelectedActionDevId, Action.Outlet_Type.ON);
            } else if (actionStr!=null && actionStr.startsWith(SIREN_DIDI)) {
                ruleActionGenerator.addSirenAction(mSelectedActionDevId, Action.Siren_Voice_Type.DI_DI);
            } else if (actionStr!=null && actionStr.startsWith(SIREN_DOOR_BELL)) {
                ruleActionGenerator.addSirenAction(mSelectedActionDevId, Action.Siren_Voice_Type.DOOR_BELL);
            }

            addRuleAction(ruleActionGenerator.generate());
        } catch (Exception ex) { }
    }

    private void initSpinnerItems() {
        DevicePresenter.getInstance().getDeviceWidgets(new ApiCallback() {
            @Override
            public void onSuccess(ApiResult apiResult) {
                ArrayList<String> spinnerRuleArray = new ArrayList<>();
                ArrayList<String> spinnerActionArray = new ArrayList<>();

                getSpinnerItemsFromWidgets(apiResult.getWidgets(), spinnerRuleArray, spinnerActionArray);

                setupRuleSpinner(spinnerRuleArray);
                setupActionSpinner(spinnerActionArray);
            }

            @Override
            public void onFailure(ApiResult apiResult) { }
        });
    }

    private void getRuleActionFromPlanId() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String planId = extras.getString(PLAN_ID);
            mPlanId = planId;

            mRulePresenter.getRuleList(planId, new ApiCallback() {
                @Override
                public void onSuccess(ApiResult apiResult) {
                    mRuleItemAdapter.updateRuleItems(apiResult.getPlan().getRule_list());
                }

                @Override
                public void onFailure(ApiResult apiResult) { }
            });
        }
    }

    private AdapterView.OnItemLongClickListener getItemLongClickListener() {
        return new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Rule item  = (Rule)parent.getAdapter().getItem(position);
                showAlertDialogToDeletePlan(item);
                return true;
            }
        };
    }

    private void showAlertDialogToDeletePlan(final Rule item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RuleActionActivity.this);
        builder.setCancelable(false);
        builder.setMessage(R.string.delete_message)
                .setPositiveButton(R.string.delete_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteRuleAction(item);
                    }
                })
                .setNegativeButton(R.string.delete_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void addRuleAction(Rule rule) {
        mRulePresenter.addRule(mPlanId, rule, new ApiCallback() {
            @Override
            public void onSuccess(ApiResult apiResult) {
                mRuleItemAdapter.addRuleItem(apiResult.getPlan().getRule_list().get(0));
            }

            @Override
            public void onFailure(ApiResult apiResult) { }
        });
    }

    private void deleteRuleAction(final Rule rule) {
        mRulePresenter.deleteRule(rule.getRule_id(), new ApiCallback() {
            @Override
            public void onSuccess(ApiResult apiResult) {
                mRuleItemAdapter.deleteRuleItem(rule);
            }

            @Override
            public void onFailure(ApiResult apiResult) { }
        });
    }

    private void getSpinnerItemsFromWidgets(List<Widget> widgetList,
                                            ArrayList<String> spinnerRuleArray,
                                            ArrayList<String> spinnerActionArray) {
        for (Widget widget : widgetList) {
            if (Ext_Type.OUTLET_I18N_0910.equals(widget.getDev_ext_type())) {
                // 電力計裝置
                // 可設定的 rule
                spinnerRuleArray.add(RULE_OUTLET_KWH_GE10 + widget.getDev_id());
                spinnerRuleArray.add(RULE_OUTLET_KWH_GE100 + widget.getDev_id());
                // 可設定的 action
                spinnerActionArray.add(OUTLET_OPEN + widget.getDev_id());
                spinnerActionArray.add(OUTLET_CLOSE + widget.getDev_id());
            }
            if (Ext_Type.SENSOR_IR_TRANSCEIVER.equals(widget.getDev_ext_type())) {
                // PIR 裝置
                // 可設定的 rule
                spinnerRuleArray.add(RULE_PIR + widget.getDev_id());
            }
            if (Ext_Type.SENSOR_HYGRO_212.equals(widget.getDev_ext_type())) {
                // 溫濕度裝置
                // 可設定的 rule
                spinnerRuleArray.add(RULE_SENSOR_TEMPERATURE + widget.getDev_id());
                spinnerRuleArray.add(RULE_SENSOR_HUMIDITY + widget.getDev_id());
            }
            if (Ext_Type.CONTROLLER_SIREN.equals(widget.getDev_ext_type())) {
                // Siren 裝置
                // 可設定的 action
                spinnerActionArray.add(SIREN_DIDI + widget.getDev_id());
                spinnerActionArray.add(SIREN_DOOR_BELL + widget.getDev_id());
            }
        }
    }

    private void setupRuleSpinner(ArrayList<String> spinnerRuleArray) {
        final Spinner spinnerRule = (Spinner)findViewById(R.id.spinnerRule);

        ArrayAdapter<String> spinnerRuleAdapter = new ArrayAdapter<>(
                RuleActionActivity.this, android.R.layout.simple_list_item_1, spinnerRuleArray);
        spinnerRuleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerRule.setAdapter(spinnerRuleAdapter);
        spinnerRule.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String itemStr = spinnerRule.getSelectedItem().toString();
                mSelectedRuleDevId = itemStr.substring(itemStr.indexOf(SPLITTER)+DEVICEID_OFFSET_OF_SPLITTER, itemStr.length());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void setupActionSpinner(ArrayList<String> spinnerActionArray) {
        final Spinner spinnerAction = (Spinner)findViewById(R.id.spinnerAction);

        ArrayAdapter<String> spinnerActionAdapter = new ArrayAdapter<>(
                RuleActionActivity.this, android.R.layout.simple_list_item_1, spinnerActionArray);
        spinnerActionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerAction.setAdapter(spinnerActionAdapter);
        spinnerAction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String itemStr = spinnerAction.getSelectedItem().toString();
                mSelectedActionDevId = itemStr.substring(itemStr.indexOf(SPLITTER)+DEVICEID_OFFSET_OF_SPLITTER, itemStr.length());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }
}
