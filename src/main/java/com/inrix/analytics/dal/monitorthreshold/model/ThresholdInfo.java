package com.inrix.analytics.dal.monitorthreshold.model;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Yishuai.Li on 10/26/2016.
 */
public class ThresholdInfo {

    private int providerId;
    private int lookBackDays;
    private double thresholdPercent;

    public ThresholdInfo () {}

    public ThresholdInfo (ThresholdInfo thresholdInfo) {
        this.providerId = thresholdInfo.providerId;
        this.lookBackDays = thresholdInfo.lookBackDays;
        this.thresholdPercent = thresholdInfo.thresholdPercent;
    }

    public ThresholdInfo (int providerId, int lookBackDays, double thresholdPercent){
        this.providerId = providerId;
        this.lookBackDays = lookBackDays;
        this.thresholdPercent = thresholdPercent;
    }

    public static ThresholdInfo from(ResultSet rset) throws SQLException {
        ThresholdInfo thresholdInfo = new ThresholdInfo();

        thresholdInfo.providerId = rset.getInt("providerId");
        thresholdInfo.lookBackDays = rset.getInt("look_back_days");
        thresholdInfo.thresholdPercent = rset.getDouble("threshold_percent");

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
                thresholdPercent == thresholdPercent){
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("<ThresholdInfo(providerId=%d, lookBackDays=%d, thresholdPercent=%d)>",
                providerId, lookBackDays, thresholdPercent);
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

    public double getThresholdPercent() {
        return thresholdPercent;
    }

    public void setThresholdPercent(double thresholdPercent) {
        this.thresholdPercent = thresholdPercent;
    }
}
