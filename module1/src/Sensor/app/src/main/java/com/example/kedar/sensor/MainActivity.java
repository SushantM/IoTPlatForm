package com.example.kedar.sensor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.UUID;


public class MainActivity extends ActionBarActivity {

    static String deviceID;
    static String deviceTYPE = "Temperature";
    static int healthSensorArrayIndex = 0;
    private static Handler mHandler;
    BluetoothAdapter mBluetoothAdapter;
    public Button startSensorButton;
    private static final UUID MY_UUID = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    private AcceptThread acceptThread;
    private ConnectionThread connectionThread;

    private boolean isServerStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        deviceTYPE = getResources().getString(R.string.sensor_type);
        deviceID = new fyl(this).getDeviceID();
        addHandler();
        addListenerOnStartButton();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    //FragmentActivity activity = getActivity();
                    case Constants.MESSAGE_STATE_CHANGE:
                        switch (msg.arg1) {
                            case Constants.MESSAGE_WRITE:
                                Log.i("Handler", "In handler, message state changed , write message");
                                byte[] writeBuf = (byte[]) msg.obj;
                                // construct a string from the buffer
                                String writeMessage = new String(writeBuf);
                                Toast.makeText(getApplicationContext(), writeMessage, Toast.LENGTH_SHORT).show();
                                break;
                            case Constants.MESSAGE_READ:
                                Log.i("Handler","In handler, message state changed , read message");
                                byte[] readBuf = (byte[]) msg.obj;
                                // construct a string from the valid bytes in the buffer
                                String readMessage = new String(readBuf, 0, msg.arg1);
                                Toast.makeText(getApplicationContext(), readMessage, Toast.LENGTH_SHORT).show();
                                break;
                        }
                    case Constants.MESSAGE_WRITE:
                        Log.i("Handler","In handler, write message");
                        byte[] writeBuf = (byte[]) msg.obj;
                        // construct a string from the buffer
                        String writeMessage = new String(writeBuf);
                        Toast.makeText(getApplicationContext(), writeMessage, Toast.LENGTH_SHORT).show();
                        break;
                    case Constants.MESSAGE_READ:
                        Log.i("Handler","In handler, read message");
                        byte[] readBuf = (byte[]) msg.obj;
                        // construct a string from the valid bytes in the buffer
                        String readMessage = new String(readBuf, 0, msg.arg1);
                        Toast.makeText(getApplicationContext(), readMessage, Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            ;
        };
    }

    public void addListenerOnStartButton()
    {
        startSensorButton = (Button)findViewById(R.id.startSensorButton);
        startSensorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isServerStarted) {
                    Toast.makeText(getApplicationContext(), "Sensor already running.", Toast.LENGTH_LONG).show();
                    return;
                }

                isServerStarted = true;
                Toast.makeText(getApplicationContext(), "Sensor Started", Toast.LENGTH_LONG).show();
                startAccepting();
            }
        });
    }

    public void startAccepting()
    {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Device does not support Bluetooth", Toast.LENGTH_LONG).show();
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Toast.makeText(getApplicationContext(), "First Start bluetooth then try again.", Toast.LENGTH_LONG).show();
            return;
        }

        acceptThread = new AcceptThread();
        acceptThread.start();
    }

    public void connected(BluetoothSocket mSocket)
    {
        connectionThread = new ConnectionThread(mSocket);
        connectionThread.start();
    }
    private class AcceptThread extends Thread{
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread()
        {
            BluetoothServerSocket tmp = null;

            try {
                // MY_UUID is the app's UUID string, also used by the client code
                tmp = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("Sensor", MY_UUID);
            } catch (IOException e) {
                Log.e("AcceptThread","listen failed "+e);
            }
            mmServerSocket = tmp;
        }

        public void run()
        {
            BluetoothSocket socket = null;
            while (true)
            {
                try {
                    Log.i("AcceptThread","Listening to incoming connection ");
                    socket = mmServerSocket.accept();
                }catch (IOException e){
                    Log.e("AcceptThread","Listening to server socket failed " + e);
                    break;
                }

                if(socket != null){
                    connected(socket);
                }
            }
        }
    }

    private class ConnectionThread extends Thread{
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectionThread(BluetoothSocket socket){
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e("ConnectedThread","Error getting IO streams "+e);
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
        public void run(){
            byte [] buffer = new byte[1024];
            int bytes;
           // while(true){
                try{
                        bytes = mmInStream.read(buffer);
                    Log.i("connectionThread", "Received data from device :: " + buffer);
                        if(bytes>0){
                            Log.i("connectionThread", "Received data from device :: " + buffer);
                            byte [] buffer1 = Arrays.copyOf(buffer,bytes);
                            String messageType = new String(buffer1);
                            mHandler.obtainMessage(Constants.MESSAGE_READ, bytes, -1, buffer).sendToTarget();

                            //TODO check type of message if it is regular then send data packet or if it is heartbeat handle accordingly

                            Log.i("Received Data",messageType+".");
                            String dataPacket = null;
                            if(messageType.equalsIgnoreCase("0")) {
                                if(deviceTYPE.equalsIgnoreCase(Constants.TrafficSensor)) {
                                    TrafficPacket pkt = new TrafficPacket();
                                    dataPacket = pkt.getPacket();
                                }
                                else if(deviceTYPE.equalsIgnoreCase(Constants.HealthSensor)){
                                    HealthPacket pkt =new HealthPacket();
                                    dataPacket = pkt.getPacket(healthSensorArrayIndex);
                                }
                            }
                            else{
                                dataPacket = new String(deviceID);
                            }
                            byte [] outputBuffer = dataPacket.getBytes();
                            Log.i("ConnectThread", "Sent data packet = " + new String(outputBuffer));
                            mmOutStream.write(outputBuffer);
                            mmSocket.close();
                           // break;
                        }
                }catch (IOException e){
                    Log.e("ConnectedThread","Error in run() "+e);
                   // break;
                }
            //}
        }
    }


    private class HealthPacket {
        public String deviceId;
        public String deviceType;
        public String timeStamp;
        public String longi,lati;
        int value, MIN, MAX;

        public HealthPacket() {
            deviceId = deviceID;
            deviceType = Constants.HealthSensorArray[healthSensorArrayIndex];;
            healthSensorArrayIndex++;
            healthSensorArrayIndex = healthSensorArrayIndex % Constants.HealthSensorCount;
            MAX = 100;
            MIN = 10;
            value = getValue();
            longi = "200";
            lati="100";
            String [] date = getCurrentTimeStamp().split(" ");
            timeStamp = date[1];
        }
        public String getCurrentTimeStamp() {
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
            Date now = new Date();
            String strDate = sdfDate.format(now);
            return strDate;
        }

        public String getPacket(int healthSensorArrayIndex)
        {
            String pkt = new String(deviceId+","+deviceType+","+value+","+timeStamp+","+longi+","+lati);
            return pkt;
        }

        public int getValue() {
            Random random = new Random();
            int n = random.nextInt();
                if(n<0)
                    n*=-1;
            return ( n % MAX) + MIN;
        }
    }
        private class fyl {
            Context mContext;

            public fyl(Context mContext) {
                this.mContext = mContext;
            }
            public String getDeviceID()
            {
                TelephonyManager tm = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
                String device_id = tm.getDeviceId();
                return device_id;
            }
        }

    class TrafficPacket{
        public String deviceId;
        public String deviceType;
        public String timeStamp;
        public String startLongi,startLati,endLongi,endLati,absLongi,absLati;
        int value, MIN, MAX;

        public TrafficPacket() {
            deviceId = deviceID;
            deviceType = deviceTYPE;
            MAX = 100;
            MIN = 10;
            value = getValue();

            startLati  = "645.0";
            startLongi = "40.0";

            endLati  = "60.0";
            endLongi = "760.0";


            absLongi = "100.0";
            absLati  = "500.0";

            String [] date = getCurrentTimeStamp().split(" ");
            timeStamp = date[1];
        }
    public String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }

    public String getPacket()
    {
        String pkt = new String(deviceId+","+deviceType+","+value+","+timeStamp+","+absLati+","+absLongi+","+startLati+","+startLongi+","+endLati+","+endLongi);
        return pkt;
    }

    public int getValue() {
        Random random = new Random();
        int n = random.nextInt();
        if(n<0)
            n*=-1;
        return ( n % MAX) + MIN;
    }
    }
}
