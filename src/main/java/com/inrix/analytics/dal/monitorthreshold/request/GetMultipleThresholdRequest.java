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
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Yishuai.Li on 10/26/2016.
 */
public class GetMultipleThresholdRequest {

    private final IRequestContext requestContext;
    private final List<Integer> providerIds;

    public GetMultipleThresholdRequest(IRequestContext requestContext, List<Integer> providerIds) {
        this.requestContext = requestContext;
        this.providerIds = providerIds;
    }

    public IDALResponse<ThresholdInfo> execute() {
        ILogger logger = LoggerFactory.getLogger();
        IDALConfiguration config = Utils.loadConfig();

        Set<Integer> allThresholdId = providerIds
                .stream()
                .collect(Collectors.toSet());

        Set<Integer> thresholdFromDB = new HashSet<>();

        String sqlTmpl = "SELECT * FROM threshold WHERE providerId IN (%s)";
        //String sqlGetDefaultTmpl = "SELECT * FROM threshold WHERE providerId = %s";
        StringBuilder pidsBuilder = new StringBuilder();
        for (int providerId : providerIds) {
            pidsBuilder.append(providerId + ", ");
        }
        pidsBuilder.append(Utils.getDefaultId() + ", ");
        String pids = pidsBuilder.toString().replaceFirst(",\\s+$", "");
        String sql = String.format(sqlTmpl, pids);
        //String sqlGetDefault = String.format(sqlGetDefaultTmpl, "-999");
        try {
            JDBCProperties props = JDBCProperties.get(config);
            try (Connection conn = props.getConnection()) {
                try (Statement stmt = conn.createStatement()) {
                    try (ResultSet rset = stmt.executeQuery(sql)) {
                        ThresholdInfo thresholdDefault = null;
                        List<ThresholdInfo> thresholdInfos = new ArrayList<>();
                        while (rset.next()) {
                            ThresholdInfo fromDBSingle = ThresholdInfo.from(rset);
                            if (fromDBSingle.getProviderId() == Utils.getDefaultId()){
                                thresholdDefault = new ThresholdInfo(fromDBSingle);
                                continue;
                            }
                            thresholdInfos.add(fromDBSingle);
                            thresholdFromDB.add(fromDBSingle.getProviderId());
                        }

                        if (thresholdDefault == null){
                            throw new Exception("Failed to get default threshold!");
                        }

                        for (int id : providerIds) {
                            if (!thresholdFromDB.contains(id)) {
                                thresholdInfos.add(new ThresholdInfo(
                                        id,
                                        thresholdDefault.getLookBackDays(),
                                        thresholdDefault.getUpThresholdPercent(),
                                        thresholdDefault.getDownThresholdPercent()));
                            }
                        }
                        return new IDALResponse<>(thresholdInfos.toArray(new ThresholdInfo[0]));


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
