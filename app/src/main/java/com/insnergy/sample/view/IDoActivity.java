/*
 * Copyright (C) 2017 The InApi Project
 */
package com.insnergy.sample.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.insnergy.sample.R;
import com.insnergy.sample.domainobj.ApiResult;
import com.insnergy.sample.domainobj.Plan;
import com.insnergy.sample.model.ApiCallback;
import com.insnergy.sample.presenter.PlanPresenter;

import java.util.ArrayList;

public class IDoActivity extends AbstractAnimActivity {
    private static final String TAG = "IDoActivity";

    private PlanPresenter mPlanPresenter = PlanPresenter.getInstance();
    private PlanItemAdapter mPlanItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ido);
        setCurrentTabEnabled(R.id.imgIdo);

        ListView listViewPlan = (ListView) findViewById(R.id.listView);
        listViewPlan.setOnItemClickListener(getItemClickListener());
        listViewPlan.setOnItemLongClickListener(getItemLongClickListener());
        mPlanItemAdapter = new PlanItemAdapter(this, new ArrayList<Plan>());
        listViewPlan.setAdapter(mPlanItemAdapter);

        getPlanList();
    }

    public void addPlan(View view) {
        String planName = ((EditText)findViewById(R.id.editText)).getText().toString();
        mPlanPresenter.addPlan(planName, new ApiCallback() {
            @Override
            public void onSuccess(ApiResult apiResult) {
                getPlanList();
            }

            @Override
            public void onFailure(ApiResult apiResult) {

            }
        });
    }

    private void getPlanList() {
        mPlanPresenter.getPlanList(false, new ApiCallback() {
            @Override
            public void onSuccess(ApiResult apiResult) {
                if (apiResult.getPlan_list() != null && apiResult.getPlan_list().size() > 0) {
                    mPlanItemAdapter.clear();
                    mPlanItemAdapter.addAll(apiResult.getPlan_list());
                    mPlanItemAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(ApiResult apiResult) {

            }
        });
    }

    private AdapterView.OnItemClickListener getItemClickListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                enterRule((Plan)parent.getAdapter().getItem(position));
            }
        };
    }

    private void enterRule(Plan plan) {
        startActivity(new Intent(getApplicationContext(), RuleActionActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                .putExtra(RuleActionActivity.PLAN_ID, plan.getPlan_id())
        );
    }

    private AdapterView.OnItemLongClickListener getItemLongClickListener() {
        return new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Plan item  = (Plan)parent.getAdapter().getItem(position);
                showAlertDialogToDeletePlan(item);
                return true;
            }
        };
    }

    private void showAlertDialogToDeletePlan(final Plan item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(IDoActivity.this);
        builder.setCancelable(false);
        builder.setMessage(R.string.delete_message)
                .setPositiveButton(R.string.delete_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deletePlan(item);
                    }
                })
                .setNegativeButton(R.string.delete_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deletePlan(final Plan plan) {
        mPlanPresenter.deletePlan(plan, new ApiCallback() {
            @Override
            public void onSuccess(ApiResult apiResult) {
                mPlanItemAdapter.remove(plan);
                mPlanItemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(ApiResult apiResult) {

            }
        });
    }
}
