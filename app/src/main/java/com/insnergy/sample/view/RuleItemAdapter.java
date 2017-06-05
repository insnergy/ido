/*
 * Copyright (C) 2017 The InApi Project
 */
package com.insnergy.sample.view;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.insnergy.sample.R;
import com.insnergy.sample.domainobj.Action;
import com.insnergy.sample.domainobj.Rule;

import java.util.List;

import static com.insnergy.sample.domainobj.DeviceInfo.WidgetAttr.MOTION_STATUS;

public class RuleItemAdapter extends BaseExpandableListAdapter {

    private LayoutInflater mLayoutInflater;
    private List<Rule> mRuleList;

    public RuleItemAdapter(LayoutInflater layoutInflater, List<Rule> ruleList) {
        this.mLayoutInflater = layoutInflater;
        this.mRuleList = ruleList;
    }

    public void updateRuleItems(List<Rule> ruleList) {
        mRuleList = ruleList;
        notifyDataSetChanged();
    }

    public void addRuleItem(Rule rule) {
        mRuleList.add(rule);
        notifyDataSetChanged();
    }

    public void deleteRuleItem(Rule rule) {
        mRuleList.remove(rule);
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return mRuleList.size();
    }

    @Override
    public int getChildrenCount(int i) {
        if (mRuleList.get(i)==null || mRuleList.get(i).getAction_list()==null) {
            return 0;
        }
        return mRuleList.get(i).getAction_list().size();
    }

    @Override
    public Object getGroup(int i) {
        return mRuleList.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        if (mRuleList.get(i)==null || mRuleList.get(i).getAction_list()==null) {
            return null;
        }
        return mRuleList.get(i).getAction_list().get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.item_rule, viewGroup, false);
        }

        Rule rule = (Rule)getGroup(i);
//        if (...) {
//            ImageView imgIcon = (ImageView) view.findViewById(R.id.imgIcon);
//            imgIcon.setImageResource(R.mipmap.clock);
//        }
        TextView txtDevId = (TextView) view.findViewById(R.id.txtDevId);
        txtDevId.setText(rule.getDev_id());
        txtDevId.setVisibility(View.VISIBLE);

        TextView txtAttr = (TextView) view.findViewById(R.id.txtAttr);
        TextView txtOperator = (TextView) view.findViewById(R.id.txtOperator);
        TextView txtCondition = (TextView) view.findViewById(R.id.txtCondition);

        setupTextAttrOfRule(rule, txtAttr);
        setupTextOperatorOfRule(rule, txtOperator);
        setupTextConditionOfRule(rule, txtCondition);

        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.item_rule, viewGroup, false);
        }
        view.setBackgroundColor(Color.DKGRAY);
        ImageView imgIcon = (ImageView) view.findViewById(R.id.imgIcon);
        imgIcon.setVisibility(View.INVISIBLE);

        TextView txtCommand = (TextView) view.findViewById(R.id.txtOperator);
        TextView txtValue = (TextView) view.findViewById(R.id.txtCondition);

        Action action = (Action)getChild(i, i1);
        switch (action.getCommand()) {
            case CMD_SWITCH:
                if (Action.Outlet_Type.ON.equals(action.getValue())) {
                    txtCommand.setText("開啟 ");
                } else {
                    txtCommand.setText("關閉 ");
                }
                txtValue.setText(action.getDev_id());
                break;
            case CMD_SIREN_WARNING:
                txtCommand.setText(Action.Siren_Voice_Type.getEnum(action.getValue()).name());
                txtValue.setText(action.getDev_id());
                break;
            case SEND_ALL:
            case SEND_MAIL:
            case SEND_NOTICE:
                txtCommand.setText("發送訊息：" + action.getBody());
                txtValue.setText("");
                break;
        }

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    private void setupTextAttrOfRule(Rule rule, TextView txtAttr) {
        switch (rule.getAttribute()) {
            case MOTION_STATUS:
                txtAttr.setText("偵測到物體移動時觸發");
                break;
            case ACTIVE_POWER:
                txtAttr.setText("即時功率");
                break;
            case TEMP:
                txtAttr.setText("溫度");
                break;
            case HUMID:
                txtAttr.setText("濕度");
                break;
        }
    }

    private void setupTextOperatorOfRule(Rule rule, TextView txtOperator) {
        switch (rule.getOperator()) {
            case GE:
                txtOperator.setText(Rule.GE);
                break;
            case EQ:
                txtOperator.setText(Rule.EQ);
                break;
            case LT:
                txtOperator.setText(Rule.LT);
                break;
        }

        if (MOTION_STATUS.equals(rule.getAttribute())) {
            txtOperator.setText("");
        }
    }

    private void setupTextConditionOfRule(Rule rule, TextView txtCondition) {
        txtCondition.setText(rule.getCondition() + " 時觸發");

        if (MOTION_STATUS.equals(rule.getAttribute())) {
            txtCondition.setText("");
        }
    }
}
