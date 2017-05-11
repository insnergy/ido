/*
 * Copyright (C) 2017 The InApi Project
 */
package com.insnergy.sample.view;

import com.insnergy.sample.domainobj.Action;
import com.insnergy.sample.domainobj.DeviceInfo;
import com.insnergy.sample.domainobj.Rule;

import java.util.ArrayList;
import java.util.List;

public class RuleActionGenerator {

    private Rule mRule;

    private RuleActionGenerator() { }

    public static RuleActionGenerator init() {
        return new RuleActionGenerator();
    }

    public RuleActionGenerator generatePirRule(String deviceId) {
        mRule = new Rule();
        mRule.setCategory(DeviceInfo.Category.SENSOR);
        mRule.setDev_id(deviceId);
        mRule.setDev_ext_type(DeviceInfo.Ext_Type.SENSOR_IR_TRANSCEIVER);
        mRule.setAttribute(DeviceInfo.WidgetAttr.MOTION_STATUS);
        mRule.setAttribute_type(Rule.RULE_ATTY_TYPE.Value);
        mRule.setOperator(Rule.RULE_LIST.EQ);
        mRule.setCondition("1");
        mRule.setAction_list(new ArrayList<Action>());
        return this;
    }

    public RuleActionGenerator generateOutletRule(String deviceId, Rule.RULE_LIST operator, String condition) {
        mRule = new Rule();
        mRule.setCategory(DeviceInfo.Category.OUTLET);
        mRule.setDev_id(deviceId);
        mRule.setDev_ext_type(DeviceInfo.Ext_Type.OUTLET_I18N_0910);
        mRule.setAttribute(DeviceInfo.WidgetAttr.ACTIVE_POWER); //CONNECTION_STATUS
        mRule.setAttribute_type(Rule.RULE_ATTY_TYPE.Value);
        mRule.setOperator(operator);
        mRule.setCondition(condition);
        mRule.setAction_list(new ArrayList<Action>());
        return this;
    }

    public RuleActionGenerator addNoticeAction(String mailTo, String mailContent) {
        Action action = new Action();
        action.setCategory(Action.Category.MOBILE);
        action.setCommand(Action.Command.SEND_NOTICE);
        action.setBody(mailContent);
        List<String> targetList = action.getTarget_list();
        targetList.add(mailTo);
        action.setTarget_list(targetList);
        addAction(action);
        return this;
    }

    public RuleActionGenerator addOutletAction(String deviceId, Action.Outlet_Type onOff) {
        Action action = new Action();
        action.setCategory(Action.Category.OUTLET);
        action.setDev_id(deviceId);
        action.setCommand(Action.Command.CMD_SWITCH);
        action.setValue(onOff.getCode());
        addAction(action);
        return this;
    }

    public RuleActionGenerator addSirenAction(String deviceId, Action.Siren_Voice_Type voiceType) {
        Action action = new Action();
        action.setCategory(Action.Category.CONTROLLER);
        action.setDev_id(deviceId);
        action.setCommand(Action.Command.CMD_SIREN_WARNING);
        action.setValue(voiceType.getCode());
        addAction(action);
        return this;
    }

    public Rule generate() {
        return mRule;
    }

    private void addAction(Action action) {
        if (mRule != null && mRule.getAction_list() != null)
            mRule.getAction_list().add(action);
    }
}
