package com.inrix.analytics.dal.monitorthreshold.request;


import com.inrix.analytics.dal.datasource.DALConfigurations;
import com.inrix.analytics.dal.interfaces.IDALConfiguration;
import com.inrix.analytics.logging.LoggerFactory;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Yishuai.Li on 10/27/2016.
 */
public class Setup {

    private final static String ENV = "dev";

    private static Map<String, Map<String, String>> testConfig;

    static {
        // To stop the pesky apache logs from showing up
        /*Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);*/

        Map<String, String> dev = new HashMap<>();
        dev.put("confName", "analytics.roadway.reports.dev.properties");
        dev.put("jdbcUrl", "jdbc:postgresql://analytics-trips-shard3-test-2015.cl5rozhsxfee.us-west-2.rds.amazonaws.com/dev_roadway");
        //dev.put("jdbcUrl", "jdbc:postgresql://analytics-trips-shard3-test-2015.cl5rozhsxfee.us-west-2.rds.amazonaws.com/analytics_postgres");

        dev.put("user", "analyticspguser");
        dev.put("password", "W#G2KXIzzIj$fKg");

        testConfig = new HashMap<>();
        testConfig.put("dev", dev);
    }

    static class ThresholdDalConfig implements IDALConfiguration {
        public static final String tripMonitorThresholdJDBC = "TripMonitorThresholdConfig";

        @Override
        public String getName() {
            return tripMonitorThresholdJDBC;
        }

        @Override
        public String getS3Bucket() {
            return "analytics-application-configuration";
        }

        @Override
        public String getS3Key() {
            return "analytics.trip.monitor.dev.properties";
        }
    }

    public static void loadConfig() {
        ThresholdDalConfig dalConfig = new ThresholdDalConfig();
        DALConfigurations.add(dalConfig);
    }

    public static Connection connect() throws SQLException {
        String jdbcUrl = testConfig.get(ENV).get("jdbcUrl");
        Properties props = new Properties();
        props.setProperty("user", testConfig.get(ENV).get("user"));
        props.setProperty("password", testConfig.get(ENV).get("password"));
        return DriverManager.getConnection(jdbcUrl, props);
    }
}
