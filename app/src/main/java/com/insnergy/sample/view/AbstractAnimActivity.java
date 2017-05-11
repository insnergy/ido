/*
 * Copyright (C) 2017 The InApi Project
 */
package com.insnergy.sample.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.insnergy.sample.R;

abstract public class AbstractAnimActivity extends AppCompatActivity {

    public void setCurrentTabEnabled(int viewid) {
        // set alpha of bottom image
        ImageView imageView = (ImageView) findViewById(viewid);
        imageView.setAlpha(1.0f);
    }

    public void toOtherActivity(View view) {
        switch (view.getId()) {
            case R.id.imgSetting:
                startActivity(new Intent(this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
                break;
            case R.id.imgDevList:
                startActivity(new Intent(this, DeviceActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                break;
            case R.id.imgStat:
                startActivity(new Intent(this, StatisticActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.imgIdo:
                startActivity(new Intent(this, IDoActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
                break;
        }
    }
}
