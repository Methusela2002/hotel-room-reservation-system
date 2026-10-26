package com.hotel.util;

public final class DataConfig {

    private static volatile String dataDir =
            System.getProperty("user.home") + "/hotel-data";

    private DataConfig() {
    }

    public static String getDataDir() {
        return dataDir;
    }

    public static void setDataDir(String dir) {
        dataDir = dir;
    }
}