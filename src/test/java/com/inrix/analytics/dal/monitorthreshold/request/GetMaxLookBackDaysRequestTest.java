package com.inrix.analytics.dal.monitorthreshold.request;

import com.inrix.analytics.dal.interfaces.IDALResponse;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.assertTrue;

/**
 * Created by Yishuai.Li on 10/28/2016.
 */
public class GetMaxLookBackDaysRequestTest {
    // fixtures
    private int maxDays;

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
        String deleteSql = "DELETE FROM threshold WHERE providerid IN (?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
            stmt.setInt(1, 1);
            stmt.setInt(2, 2);

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
            stmt.setInt(1, 1);
            stmt.setInt(2, 100);
            stmt.setDouble(3, 0.2);
            stmt.setDouble(4, 0.2);

            stmt.execute();
            ResultSet rset = stmt.getResultSet();
            rset.next();
            //oneThreshold = ThresholdInfo.from(rset);

            stmt.setInt(1, 2);
            stmt.setInt(2, 101);
            stmt.setDouble(3, 0.2);
            stmt.setDouble(4, 0.2);

            stmt.execute();
            rset = stmt.getResultSet();
            rset.next();
        }
    }

    @Test
    public void testExecute() throws Exception {

        GetMaxLookBackDaysRequest req = new GetMaxLookBackDaysRequest(null);
        IDALResponse<Integer> res = req.execute();
        if (res.getException() != null) {
            res.getException().printStackTrace();
            assertTrue(false);
        }
        int days = res.getSingleObject();
        assertTrue(days == 101);
    }
}
