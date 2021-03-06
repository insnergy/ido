/*
 * Copyright (C) 2017 The InApi Project
 */
package com.insnergy.sample.model;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;

import com.insnergy.sample.R;
import com.insnergy.sample.domainobj.NotificationInfo;
import com.insnergy.sample.view.IDoActivity;

public class NotificationIntentService extends IntentService {
    private static final String TAG = "[NotificationIntentService]";

    public NotificationIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String title = intent.getExtras().getString("title");
        String body = intent.getExtras().getString("body");
        int android_support_content_wakelockid = intent.getExtras().getInt("android.support.content.wakelockid");
        NotificationInfo notificationInfo = new NotificationInfo(title, body, true, android_support_content_wakelockid);
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (!powerManager.isScreenOn()) {
            final PowerManager.WakeLock mWakelock = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "SimpleTimer");
            mWakelock.acquire(3000);//3秒後自動關閉螢幕
        }

        showNotification(this, notificationInfo);
    }

    private void showNotification(Context context, NotificationInfo notificationInfo) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, notificationInfo.getAndroid_support_content_wakelockid(), new Intent(context, IDoActivity.class), 0);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); // 通知音效的URI，在這裡使用系統內建的通知音效
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE); // 取得系統的通知服務

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(notificationInfo.getTitle())
                .setContentText(notificationInfo.getBody())
                .setContentIntent(pendingIntent)//點擊後的行為
                .setSound(soundUri)//聲音
                .setAutoCancel(true);//點擊後是否關閉
        notificationManager.notify(notificationInfo.getAndroid_support_content_wakelockid(), mBuilder.build());
    }
}
