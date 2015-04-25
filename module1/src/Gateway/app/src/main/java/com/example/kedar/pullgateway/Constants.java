package com.example.kedar.pullgateway;

/**
 * Created by kedar on 4/14/15.
 */
public interface Constants {

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    public static final String filterServerIP = "10.42.0.94";
    public static final String repoServerIP = "10.42.0.37";
    public static final int filterServerPort = 6969;
    public static final int repoServerPort = 6969;

    public static final int ALIVE_MSG = 1;
    public static final int DATA_MSG = 0;

    public static final int SLEEP_TIME = 1000;   // In milli seconds

    public static final String  requestDeviceID = "request_ids";
    public static final String  requestTypeHandler = "requestTypeHandler";
    public static final String  typeHandlerPath = "/mnt/sdcard/TypeHandler.dex";
    public static final String  jarClassName = "Converter";
}
