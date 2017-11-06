package com.inrix.analytics.dal.monitorthreshold.request;

import com.inrix.analytics.dal.interfaces.IDALResponse;
import com.inrix.analytics.dal.monitorthreshold.model.ThresholdInfo;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.Assert.assertTrue;

/**
 * Created by Yishuai.Li on 11/3/2016.
 */
public class CreateThresholdRequestTest {

    // fixtures
    private ThresholdInfo oneThreshold;

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
        String deleteSql = "DELETE FROM threshold WHERE providerid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
            stmt.setInt(1, 1);

            int rowDeleted = stmt.executeUpdate();
            assertTrue(rowDeleted == 1);
        }

        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException se) {}
        }
    }

    @Test
    public void testInsert() {
        oneThreshold = new ThresholdInfo();
        oneThreshold.setProviderId(1);
        oneThreshold.setLookBackDays(3);
        oneThreshold.setUpThresholdPercent(0.2);
        oneThreshold.setDownThresholdPercent(0.2);
        CreateThresholdRequest request = new CreateThresholdRequest(null, oneThreshold);
        IDALResponse<ThresholdInfo> response = request.execute();

        if (response.getException() != null) {
            response.getException().printStackTrace();
            assertTrue(false);
        }
        ThresholdInfo thresholdCreate = response.getSingleObject();

        GetThresholdRequest request1 = new GetThresholdRequest(null, 1);
        IDALResponse<ThresholdInfo> response1 = request1.execute();
        ThresholdInfo thresholdGet = response1.getSingleObject();

        assertTrue(thresholdCreate.equals(oneThreshold));
        assertTrue(thresholdGet.equals(oneThreshold));
    }
}
