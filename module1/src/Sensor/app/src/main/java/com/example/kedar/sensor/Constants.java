package com.example.kedar.sensor;

/**
 * Created by kedar on 4/13/15.
 */
public interface Constants {
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    public static final String TrafficSensor = "Traffic";
    public static final String HealthSensor = "Health";

    public static final String  HealthSensorArray [] = {"Temperature","ECG","BP","Oxygen","Pulse"};
    public static final int HealthSensorCount = 5;

    /*
    *
    *       Sushant
            startLati  = "57.0";
            startLongi = "45.0";

            endLati  = "645.0";
            endLongi = "40.0";


            absLongi = "100.0";
            absLati  = "50.0";


            //kedar
            startLati  = "645.0";
            startLongi = "40.0";

            endLati  = "645.0";
            endLongi = "745.0";


            absLongi = "645.0";
            absLati  = "200.0";

            //Kapil

            startLati  = "57.0";
            startLongi = "45.0";

            endLati  = "645.0";
            endLongi = "745.0";


            absLongi = "100.0";
            absLati  = "300.0";


            //Dhruvil

            startLati  = "57.0";
            startLongi = "45.0";

            endLati  = "60.0";
            endLongi = "760.0";


            absLongi = "58.0";
            absLati  = "200.0";


        //doctor
                    startLati  = "645.0";
            startLongi = "40.0";

            endLati  = "60.0";
            endLongi = "760.0";


            absLongi = "100.0";
            absLati  = "500.0";

    * */

}
