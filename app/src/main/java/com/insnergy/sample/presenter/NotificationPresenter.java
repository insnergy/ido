/*
 * Copyright (C) 2017 The InApi Project
 */
package com.insnergy.sample.presenter;

import com.insnergy.sample.domainobj.ApiResult;
import com.insnergy.sample.domainobj.Token;
import com.insnergy.sample.model.ApiCallback;
import com.insnergy.sample.model.ApiManager;
import com.insnergy.sample.model.DataManager;

import java.util.HashMap;

public class NotificationPresenter {
    private static final String TAG = "NotificationPresenter";
    private static NotificationPresenter mInstance = new NotificationPresenter();

    public static NotificationPresenter getInstance() {
        return mInstance;
    }

    private NotificationPresenter() { }

    /**
     * 與 NServer 註冊
     */
    public void registerNServer(String token) {
        HashMap<String, String> apiParams = new HashMap<>();
        apiParams.put("user_id", DataManager.getInstance().getEmail());
        apiParams.put("channels", "4");
        apiParams.put("mobile_token", token);
        apiParams.put("client_alias", "InFamilyGCM");

        ApiCallback callback = new ApiCallback() {
            @Override
            public void onSuccess(ApiResult apiResult) {
                Token token = apiResult.getToken();
            }

            @Override
            public void onFailure(ApiResult result) { }
        };
        ApiManager.getInstance().callApi("addMobileToken", callback, apiParams);
    }

    /**
     * 註銷 NServer 的註冊
     */
    public void unregisterNServer(String token) {
        HashMap<String, String> apiParams = new HashMap<>();
        apiParams.put("user_id", DataManager.getInstance().getEmail());
        apiParams.put("mobile_token", token);

        ApiCallback apiCallback =new ApiCallback() {
            @Override
            public void onSuccess(ApiResult apiResult) { }

            @Override
            public void onFailure(ApiResult result) { }
        };
        ApiManager.getInstance().callApi("deleteMobileToken", apiCallback, apiParams);
    }
}
