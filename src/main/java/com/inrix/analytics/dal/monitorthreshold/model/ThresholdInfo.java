package com.inrix.analytics.dal.monitorthreshold.model;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Yishuai.Li on 10/26/2016.
 */
public class ThresholdInfo {

    private int providerId;
    private int lookBackDays;
    private double upThresholdPercent;
    private double downThresholdPercent;

    public ThresholdInfo () {}

    public ThresholdInfo (ThresholdInfo thresholdInfo) {
        this.providerId = thresholdInfo.providerId;
        this.lookBackDays = thresholdInfo.lookBackDays;
        this.upThresholdPercent = thresholdInfo.upThresholdPercent;
        this.downThresholdPercent = thresholdInfo.downThresholdPercent;
    }

    public ThresholdInfo (int providerId, int lookBackDays, double upThresholdPercent, double downThresholdPercent){
        this.providerId = providerId;
        this.lookBackDays = lookBackDays;
        this.upThresholdPercent = upThresholdPercent;
        this.downThresholdPercent = downThresholdPercent;
    }

    public static ThresholdInfo from(ResultSet rset) throws SQLException {
        ThresholdInfo thresholdInfo = new ThresholdInfo();

        thresholdInfo.providerId = rset.getInt("providerId");
        thresholdInfo.lookBackDays = rset.getInt("look_back_days");
        thresholdInfo.upThresholdPercent = rset.getDouble("up_threshold_percent");
        thresholdInfo.downThresholdPercent = rset.getDouble("down_threshold_percent");

        return thresholdInfo;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (! (obj instanceof ThresholdInfo)) {
            return false;
        }

        ThresholdInfo that = (ThresholdInfo)obj;

        if(providerId == that.providerId &&
                lookBackDays == that.lookBackDays &&
                upThresholdPercent == upThresholdPercent &&
                downThresholdPercent == downThresholdPercent){
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("<ThresholdInfo(providerId=%d, lookBackDays=%d, upThresholdPercent=%d, downThresholdPercent=%d)>",
                providerId, lookBackDays, upThresholdPercent, downThresholdPercent);
    }

    public int getProviderId() {
        return providerId;
    }

    public void setProviderId(int providerId) {
        this.providerId = providerId;
    }

    public int getLookBackDays() {
        return lookBackDays;
    }

    public void setLookBackDays(int lookBackDays) {
        this.lookBackDays = lookBackDays;
    }

    public double getUpThresholdPercent() {
        return upThresholdPercent;
    }

    public void setUpThresholdPercent(double upThresholdPercent) {
        this.upThresholdPercent = upThresholdPercent;
    }

    public double getDownThresholdPercent() {
        return downThresholdPercent;
    }

    public void setDownThresholdPercent(double downThresholdPercent) {
        this.downThresholdPercent = downThresholdPercent;
    }
}
