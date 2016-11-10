package com.inrix.analytics.dal.monitorthreshold.domain;

import com.inrix.analytics.dal.monitorthreshold.model.ThresholdInfo;

/**
 * Created by Yishuai.Li on 10/26/2016.
 */
public class SqlBuilder {
    private ThresholdInfo thresholdInfo;

    public SqlBuilder(ThresholdInfo thresholdInfo) { this.thresholdInfo = thresholdInfo; };

    public String buildInsertStmt() {
        return null;

    }
}
