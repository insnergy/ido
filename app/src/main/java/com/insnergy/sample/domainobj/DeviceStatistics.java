/*
 * Copyright (C) 2017 The InApi Project
 */
package com.insnergy.sample.domainobj;

public class DeviceStatistics {
    private String dev_id;
    private DeviceInfo.WidgetAttr attributes = DeviceInfo.WidgetAttr.ACTIVE_POWER;
    private StatType stats_type;
    private String start_time;
    private String end_time;

    public enum StatType {
        Day("dm60", "日", "日"), //不可查詢超過24小時的範圍
        Week("dm1di", "週", "周"), //不可查詢超過31天的範圍
        Month("dm1di", "月", "月"); //不可查詢超過31天的範圍

        private String code;
        private String tw;
        private String cn;

        StatType(String code, String tw, String cn) {
            this.code = code;
            this.tw = tw;
            this.cn = cn;
        }

        public String getCode() {
            return this.code;
        }

        public String getTW() {
            return this.tw;
        }

        public String getCN() {
            return this.cn;
        }

        public static StatType getEnum(String type) {
            for(StatType stat : values()) {
                if (stat.name().equalsIgnoreCase(type) ||
                        stat.getTW().equals(type) || stat.getCN().equals(type)) {
                    return stat;
                }
            }
            return Day;
        }
    }

    public DeviceStatistics() { }

    public String getDev_id() {
        return dev_id;
    }

    public void setDev_id(String dev_id) {
        this.dev_id = dev_id;
    }

    public DeviceInfo.WidgetAttr getAttributes() {
        return attributes;
    }

    public void setAttributes(DeviceInfo.WidgetAttr attributes) {
        this.attributes = attributes;
    }

    public StatType getStats_type() {
        return stats_type;
    }

    public void setStats_type(StatType stats_type) {
        this.stats_type = stats_type;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }
}
