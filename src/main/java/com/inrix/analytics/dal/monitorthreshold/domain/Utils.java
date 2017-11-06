package com.inrix.analytics.dal.monitorthreshold.domain;

import com.inrix.analytics.dal.datasource.DALConfigurations;
import com.inrix.analytics.dal.interfaces.IDALConfiguration;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by Yishuai.Li on 10/26/2016.
 */
public class Utils {
    public static String CONFIG_NAME = "TripMonitorThresholdConfig";
    public static final int defaultId = -999;

    public static IDALConfiguration loadConfig() {
        IDALConfiguration config = DALConfigurations.get(CONFIG_NAME);
        if (config == null) {
            String msg = String.format("%1$s not set in configuration", CONFIG_NAME);
            throw new RuntimeException(msg);
        }
        return config;
    }

    public static int getDefaultId() {
        return defaultId;
    }

    public static String stackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }
}
