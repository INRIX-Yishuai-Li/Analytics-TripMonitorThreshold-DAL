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
 * Created by Yishuai.Li on 10/28/2016.
 */
public class GetMaxLookBackDaysRequest {
    private final IRequestContext requestContext;

    public GetMaxLookBackDaysRequest(IRequestContext requestContext) {
        this.requestContext = requestContext;
    }


    public IDALResponse<Integer> execute() {
        ILogger logger = LoggerFactory.getLogger();
        IDALConfiguration config = Utils.loadConfig();
        String sql = "SELECT MAX(look_back_days) FROM threshold;";
        int maxDays = -1;
        try{
            JDBCProperties props = JDBCProperties.get(config);
            try (Connection conn = props.getConnection()){
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    ResultSet rset = stmt.executeQuery();
                    if (rset.next()) {
                        maxDays = rset.getInt(1);
                        return new IDALResponse<>(new Integer[] {maxDays});
                    } else {
                        return new IDALResponse<>(new Integer[0]);
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
