/*
 * Copyright (C) 2017 The InApi Project
 */
package com.insnergy.sample.presenter;

import android.util.Log;

import com.google.gson.Gson;
import com.insnergy.sample.domainobj.Action;
import com.insnergy.sample.domainobj.Rule;
import com.insnergy.sample.model.ApiCallback;
import com.insnergy.sample.model.ApiManager;
import com.insnergy.sample.model.DataManager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

public class RulePresenter {
    private static final String TAG = "RulePresenter";
    private static RulePresenter mInstance = new RulePresenter();

    private RulePresenter() { }

    public static RulePresenter getInstance() {
        return mInstance;
    }

    public void addRule(String plan_id, Rule rule, final ApiCallback apiCallback) {
        HashMap<String, String> apiParams = new HashMap<>();
        apiParams.put("user_id", DataManager.getInstance().getEmail());
        apiParams.put("plan_id", plan_id);
        apiParams.put("rule", new Gson().toJson(encodeActionBody(rule)));
        ApiManager.getInstance().callApi("setRule", apiCallback, apiParams);
    }

    public void getRuleList(String planid, ApiCallback apiCallback) {
        HashMap<String, String> apiParams = new HashMap<>();
        apiParams.put("user_id", DataManager.getInstance().getEmail());
        apiParams.put("plan_id", planid);
        apiParams.put("verbose", "detail");
        ApiManager.getInstance().callApi("getRuleList", apiCallback, apiParams);
    }

    public void editRuleAction(String planid, Rule rule, ApiCallback apiCallback) {
        HashMap<String, String> apiParams = new HashMap<>();
        apiParams.put("user_id", DataManager.getInstance().getEmail());
        apiParams.put("plan_id", planid);
        apiParams.put("rule", new Gson().toJson(encodeActionBody(rule)));
        ApiManager.getInstance().callApi("setRule", apiCallback, apiParams);
    }

    public void deleteRule(String ruleid, ApiCallback apiCallback) {
        HashMap<String, String> apiParams = new HashMap<>();
        apiParams.put("user_id", DataManager.getInstance().getEmail());
        apiParams.put("rule_id", ruleid);
        ApiManager.getInstance().callApi("deleteRule", apiCallback, apiParams);
    }

    public void deleteActions(String ruleid, String actionIds, ApiCallback apiCallback) {
        HashMap<String, String> apiParams = new HashMap<>();
        apiParams.put("user_id", DataManager.getInstance().getEmail());
        apiParams.put("rule_id", ruleid);
        apiParams.put("action_ids", actionIds);
        ApiManager.getInstance().callApi("deleteAction", apiCallback, apiParams);
    }

    // 特殊符號會造成 JSON 的錯誤，需採用 urlEncode 轉碼，且雙引號(%22)轉碼需加上跳脫符號(%5C)
    private Rule encodeActionBody(Rule originRule) {
        for (Action action : originRule.getAction_list()) {
            if (Action.Category.MOBILE.equals(action.getCategory())) {
                String encodeBody = action.getBody();
                try {
                    if (action.getBody() != null) {
                        encodeBody = URLEncoder.encode(action.getBody(), "UTF-8").replaceAll("%22", "%5C%22");
                    }
                } catch (UnsupportedEncodingException ex) {
                    encodeBody = action.getBody();
                    Log.e(TAG, "UnsupportedEncodingException " + action.toString() + " ," + ex.getMessage());
                } catch (Exception exception) {
                    encodeBody = action.getBody();
                    Log.e(TAG, "Exception " + action.toString() + " ," + exception.getMessage());
                }
                action.setBody(encodeBody);
            }
        }
        return originRule;
    }
}
