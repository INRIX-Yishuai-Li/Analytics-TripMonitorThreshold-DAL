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
public class GetThresholdRequest {

    private final IRequestContext requestContext;
    private final int providerId;

    public GetThresholdRequest(IRequestContext requestContext, int providerId) {
        this.requestContext = requestContext;
        this.providerId = providerId;
    }

    public IDALResponse<ThresholdInfo> execute() {
        ILogger logger = LoggerFactory.getLogger();
        IDALConfiguration config = Utils.loadConfig();
        String sql = "SELECT * FROM threshold WHERE providerId = ?";
        try{
            JDBCProperties props = JDBCProperties.get(config);
            try (Connection conn = props.getConnection()){
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setObject(1, providerId);
                    ResultSet rset = stmt.executeQuery();
                    if (rset.next()) {
                        ThresholdInfo thresholdInfo = ThresholdInfo.from(rset);
                        return new IDALResponse<>(new ThresholdInfo[] {thresholdInfo});
                    } else {
                        return new IDALResponse<>(new ThresholdInfo[0]);
                    }
                }
            }
        } catch (Exception ex) {
            String tb = Utils.stackTrace(ex);
            String msg = "Unable to execute query " + sql + "\nStacktrace:\n" + tb;
            logger.error(requestContext, msg);
            return new IDALResponse<>(ex);
        }
    }
}
