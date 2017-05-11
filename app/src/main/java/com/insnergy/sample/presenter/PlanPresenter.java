/*
 * Copyright (C) 2017 The InApi Project
 */
package com.insnergy.sample.presenter;

import com.insnergy.sample.domainobj.Plan;
import com.insnergy.sample.model.ApiCallback;
import com.insnergy.sample.model.ApiManager;
import com.insnergy.sample.model.DataManager;

import java.util.HashMap;

public class PlanPresenter {
    private static final String TAG = "PlanPresenter";
    private static PlanPresenter mInstance = new PlanPresenter();

    private PlanPresenter() { }

    public static PlanPresenter getInstance() {
        return mInstance;
    }

    public void addPlan(String planName, ApiCallback apiCallback) {
        HashMap<String, String> apiParams = new HashMap<>();
        apiParams.put("user_id", DataManager.getInstance().getEmail());
        apiParams.put("name", planName);
        apiParams.put("enable_status", "1");
        ApiManager.getInstance().callApi("addPlan", apiCallback, apiParams);
    }

    public void getPlanList(boolean isGetAll, ApiCallback apiCallback) {
        HashMap<String, String> apiParams = new HashMap<>();
        apiParams.put("user_id", DataManager.getInstance().getEmail());
        apiParams.put("verbose", (isGetAll)? "detail": "quiet");
        ApiManager.getInstance().callApi("getPlanList", apiCallback, apiParams);
    }

    public void editPlan(Plan plan, ApiCallback apiCallback) {
        HashMap<String, String> apiParams = new HashMap<>();
        apiParams.put("user_id", DataManager.getInstance().getEmail());
        apiParams.put("plan_id", plan.getPlan_id());
        apiParams.put("name", plan.getName());
        apiParams.put("enable_status", plan.getEnable_status());
        ApiManager.getInstance().callApi("editPlan", apiCallback, apiParams);
    }

    public void deletePlan(Plan plan, ApiCallback apiCallback) {
        HashMap<String, String> apiParams = new HashMap<>();
        apiParams.put("user_id", DataManager.getInstance().getEmail());
        apiParams.put("plan_id", plan.getPlan_id());
        ApiManager.getInstance().callApi("deletePlan", apiCallback, apiParams);
    }
}
