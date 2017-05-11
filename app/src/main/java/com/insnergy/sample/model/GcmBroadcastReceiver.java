/*
 * Copyright (C) 2017 The InApi Project
 */
package com.insnergy.sample.model;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ComponentName comp = new ComponentName(context.getPackageName(), NotificationIntentService.class.getName());
        startWakefulService(context, (intent.setComponent(comp)));
    }
}
