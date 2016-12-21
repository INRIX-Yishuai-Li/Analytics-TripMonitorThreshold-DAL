package com.inrix.analytics.dal.monitorthreshold.request;

import com.inrix.analytics.dal.interfaces.IDALResponse;
import com.inrix.analytics.dal.monitorthreshold.model.ThresholdInfo;
import org.junit.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.assertTrue;

/**
 * Created by Yishuai.Li on 10/26/2016.
 */
public class GetThresholdRequestTest {

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

    @Before
    public void createReports() throws SQLException {
        String sql = "INSERT INTO threshold (providerId, look_back_days, up_threshold_percent, down_threshold_percent) " +
                "VALUES (?, ?, ?, ?) " +
                "RETURNING *";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, 1);
            stmt.setInt(2, 3);
            stmt.setDouble(3, 0.2);
            stmt.setDouble(4, 0.2);

            stmt.execute();
            ResultSet rset = stmt.getResultSet();
            rset.next();
            oneThreshold = ThresholdInfo.from(rset);
        }
    }

    @Test
    public void testExecute() throws Exception {

        GetThresholdRequest req = new GetThresholdRequest(null, oneThreshold.getProviderId());
        IDALResponse<ThresholdInfo> res = req.execute();
        if (res.getException() != null) {
            res.getException().printStackTrace();
            assertTrue(false);
        }
        ThresholdInfo threshold = res.getSingleObject();
        assert threshold != null;
        assertTrue(threshold.equals(oneThreshold));

    }
}
