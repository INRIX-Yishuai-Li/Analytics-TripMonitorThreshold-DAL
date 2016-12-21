package com.inrix.analytics.dal.monitorthreshold.request;

import com.inrix.analytics.dal.interfaces.IDALResponse;
import com.inrix.analytics.dal.monitorthreshold.model.ThresholdInfo;
import org.apache.hadoop.hbase.shaded.com.google.common.primitives.Ints;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by Yishuai.Li on 11/4/2016.
 */
public class GetMultipleThresholdRequestTest {
    // fixtures
    private ThresholdInfo defaultThreshold;

    private static Connection conn;

    @BeforeClass
    public static void loadConfig() {
        Setup.loadConfig();
    }

    @BeforeClass
    public static void connect() throws SQLException {
        conn = Setup.connect();
    }

    @AfterClass
    public static void disconnect() throws Exception {
        String deleteSql = "DELETE FROM threshold WHERE providerid in (1 , -999)";
        try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
            int rowDeleted = stmt.executeUpdate();
            assertTrue(rowDeleted == 2);
        }

        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException se) {}
        }
    }

    @Before
    public void createReports() throws SQLException {
        String sql = "INSERT INTO threshold (providerId, look_back_days, up_threshold_percent, down_threshold_percent) " +
                "VALUES (?, ?, ?, ?) " +
                "RETURNING *";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, -999);
            stmt.setInt(2, 5);
            stmt.setDouble(3, 0.5);
            stmt.setDouble(4, 0.5);

            stmt.execute();
            ResultSet rset = stmt.getResultSet();
            rset.next();
            defaultThreshold = ThresholdInfo.from(rset);

            stmt.setInt(1, 1);
            stmt.setInt(2, 3);
            stmt.setDouble(3, 0.3);
            stmt.setDouble(4, 0.3);

            stmt.execute();
            rset = stmt.getResultSet();
            rset.next();

        }
    }

    @Test
    public void testMultipleRequest() {
        GetMultipleThresholdRequest request = new GetMultipleThresholdRequest(null, new ArrayList<>(Arrays.asList(1,2)));
        IDALResponse<ThresholdInfo> response = request.execute();
        List<ThresholdInfo> result = Arrays.asList(response.getObjects());
        for (ThresholdInfo info : result){
            if (info.getProviderId() == 1){
                assertTrue(info.getLookBackDays() == 3);
                assertTrue(info.getUpThresholdPercent() == 0.3);
                assertTrue(info.getDownThresholdPercent() == 0.3);
            }

            if (info.getProviderId() == 2){
                assertTrue(info.getLookBackDays() == 5);
                assertTrue(info.getUpThresholdPercent() == 0.5);
                assertTrue(info.getDownThresholdPercent() == 0.5);
            }
        }
    }
}
