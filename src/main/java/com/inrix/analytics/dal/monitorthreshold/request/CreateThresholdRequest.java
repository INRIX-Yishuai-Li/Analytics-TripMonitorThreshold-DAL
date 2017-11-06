package com.inrix.analytics.dal.monitorthreshold.request;

import com.inrix.analytics.dal.datasource.jdbc.JDBCProperties;
import com.inrix.analytics.dal.interfaces.IDALConfiguration;
import com.inrix.analytics.dal.interfaces.IDALResponse;
import com.inrix.analytics.dal.monitorthreshold.domain.Utils;
import com.inrix.analytics.dal.monitorthreshold.model.ThresholdInfo;
import com.inrix.analytics.logging.ILogger;
import com.inrix.analytics.logging.IRequestContext;
import com.inrix.analytics.logging.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by Yishuai.Li on 10/26/2016.
 */
public class CreateThresholdRequest {

    private final IRequestContext requestContext;
    private final ThresholdInfo thresholdInfo;

    public CreateThresholdRequest (IRequestContext requestContext, ThresholdInfo thresholdInfo) {
        this.requestContext = requestContext;
        this.thresholdInfo = thresholdInfo;
    }

    public IDALResponse<ThresholdInfo> execute() {
        ILogger logger = LoggerFactory.getLogger();
        IDALConfiguration configuration = Utils.loadConfig();

        String sql = "INSERT INTO threshold (providerid, look_back_days, up_threshold_percent, down_threshold_percent) VALUES (?, ?, ?, ?) RETURNING *";
        try{
            JDBCProperties props = JDBCProperties.get(configuration);
            try (Connection conn = props.getConnection()){
                try (PreparedStatement stmt = conn.prepareStatement(sql)){
                    stmt.setInt(1, thresholdInfo.getProviderId());
                    stmt.setInt(2, thresholdInfo.getLookBackDays());
                    stmt.setDouble(3, thresholdInfo.getUpThresholdPercent());
                    stmt.setDouble(4, thresholdInfo.getDownThresholdPercent());
                    if (stmt.execute()) {
                        ResultSet rset = stmt.getResultSet();
                        rset.next();
                        ThresholdInfo returned = ThresholdInfo.from(rset);
                        return new IDALResponse<ThresholdInfo>(new ThresholdInfo[] {returned}, null);
                    } else {
                        String msg = String.format("Unable to insert!");
                        throw new RuntimeException(msg);
                    }
                }
            }
        } catch (Exception ex) {
            String tb = Utils.stackTrace(ex);
            String msg = "Unable to insert: providerId: " + thresholdInfo.getProviderId()
                    + " look_back_days:" + thresholdInfo.getLookBackDays()
                    + "up percent:" + thresholdInfo.getUpThresholdPercent()
                    + "down percent:" + thresholdInfo.getDownThresholdPercent()
                    + "\nStacktrace:\n" + tb;
            logger.error(requestContext, msg);
            return new IDALResponse<>(ex);
        }
    }
}
