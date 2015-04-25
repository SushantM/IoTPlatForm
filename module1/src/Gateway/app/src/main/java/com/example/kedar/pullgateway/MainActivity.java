package com.example.kedar.pullgateway;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import dalvik.system.DexClassLoader;


public class MainActivity extends ActionBarActivity {

    private Button mPullButton;
    BluetoothAdapter mBluetoothAdapter;
    private static final UUID MY_UUID = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    private static Handler mHandler;
    public Set<BluetoothDevice> sensorList;
    private ConnectThread connectThread;
    private ConnectionThread connectionThread;
    ArrayList<String> allPackets;
    ArrayList<String> alivePackets;
    ArrayList<String> activeSensors;
    static String dstAddress = Constants.filterServerIP;
    static int dstPort = Constants.filterServerPort;
    static String repodstAddress = Constants.repoServerIP;
    static int repodstPort = Constants.repoServerPort;
    int heartBeatRatio = 2;
    int heartBeatCount = 0;
    int messageType;
    boolean isGateWayStarted = false;

    private Handler uiHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uiHandler = new Handler();

        allPackets= new ArrayList<>();
        alivePackets = new ArrayList<>();
        activeSensors = new ArrayList<>();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Device does not support Bluetooth", Toast.LENGTH_LONG).show();
            //return;
        }

        // check and enable BT
        if (!mBluetoothAdapter.isEnabled()) {
            Toast.makeText(getApplicationContext(), "Bluetooth not enabled. Enable bluetooth and try again", Toast.LENGTH_LONG).show();
            //return;
        }
        bootUp();
        addHandler();
        addListenerOnPullButton();
        /*String test = new String("Biradar");
        String op = castIntoPacket(test,"Temperature");
        Toast.makeText(getApplicationContext(), op, Toast.LENGTH_LONG).show();
        op = castIntoPacket(test,"ECG");
        Toast.makeText(getApplicationContext(), op, Toast.LENGTH_LONG).show();
        op = castIntoPacket(test,"BP");
        Toast.makeText(getApplicationContext(), op, Toast.LENGTH_LONG).show();
        op = castIntoPacket(test,"Oxygen");
        Toast.makeText(getApplicationContext(), op, Toast.LENGTH_LONG).show();
        op = castIntoPacket(test,"Pulse");
        Toast.makeText(getApplicationContext(), op, Toast.LENGTH_LONG).show();
        op = castIntoPacket(test,"Traffic");
        Toast.makeText(getApplicationContext(), op, Toast.LENGTH_LONG).show();
        */

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

    public void addListenerOnPullButton()
    {
        mPullButton = (Button)findViewById(R.id.pullButton);
        mPullButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isGateWayStarted) {
                    Toast.makeText(getApplicationContext(), "Gateway already running.", Toast.LENGTH_LONG).show();
                    return;
                }
                isGateWayStarted = true;
                Toast.makeText(getApplicationContext(), "Gateway Started", Toast.LENGTH_LONG).show();
                pullSensorPacket();
            }
        });
    }

    public void pullSensorPacket()
    {
        RepeatingThread rt = new RepeatingThread();
        rt.start();
    }

    public void sendDataToFilterGateway()
    {
        Log.i("sendDataToFilterGateway","Preparing myClientTask gateway");
        MyClientTask myClientTask = new MyClientTask(dstAddress,dstPort);
        myClientTask.execute();

    }

    public void sendDataToRepo(){
        Log.i("sendDataToRepo","Preparing myRepoTask gateway");
        MyRepoTask myRepoTask = new MyRepoTask(repodstAddress,repodstPort);
        myRepoTask.execute();
    }

    public void connect(BluetoothSocket socket,BluetoothDevice device,int messageType){

        String message = new packet(messageType).toString();
        Log.i("Connect","ConnectionThread started ");
        connectionThread = new ConnectionThread(socket);
        connectionThread.start();
        connectionThread.write(message.getBytes());
    }

    private class RepeatingThread extends Thread {
        @Override
        public void run() {
            while(true) {
                sensorList = mBluetoothAdapter.getBondedDevices();
                messageType=Constants.DATA_MSG;
                allPackets.clear();
                alivePackets.clear();
                if(heartBeatCount < heartBeatRatio) {
                    heartBeatCount++;
                    messageType = Constants.DATA_MSG;
                }
                else{
                    messageType = Constants.ALIVE_MSG;
                    heartBeatCount = 0;
                }

                for (BluetoothDevice device : sensorList) {
                    Log.i("ConnectInfo", "connect To Android Device" + device.getName());
                    connectThread = new ConnectThread(device, messageType);
                    connectThread.start();
                }

                if (messageType == Constants.DATA_MSG) {
                    while (allPackets.size() < sensorList.size()) {
                        //do nothing
                    }
                } else if (messageType == Constants.ALIVE_MSG) {
                    while (alivePackets.size() < sensorList.size()) {
                        //do nothing
                    }
                }
                // TODO sendDataToFilterGateway

                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (messageType == Constants.DATA_MSG)
                            sendDataToFilterGateway();
                        else if (messageType == Constants.ALIVE_MSG) {
                            sendDataToRepo();
                        }
                    }
                });

                //Communicate to repo

                try {
                    Thread.sleep(Constants.SLEEP_TIME);
                } catch (Exception e) {
                    Log.e("SensorApp", "Could not sleep");
                }
            }
        }
    }

    private class ConnectThread extends Thread{
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        int messageType;

        public ConnectThread(BluetoothDevice device,int _messsageType){
            BluetoothSocket tmp =null;
            mmDevice = device;
            messageType = _messsageType;
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
                Log.i("connectThread"," Created Insecure Socket using UUID" + tmp.getRemoteDevice().getName());
            } catch (IOException e) {
                Log.e("ConnectThread","Error in constructor " + e);
            }
            mmSocket = tmp;
        }

        public  void run(){
            //while(true){
            mBluetoothAdapter.cancelDiscovery();
            try{
                    Log.i("connectThread","Trying to connect with device :: " + mmSocket.getRemoteDevice().getName());
                    mmSocket.connect();

            }catch (IOException e){

                Log.e("ConnectThread","Error in connect :: "+ mmSocket.getRemoteDevice().getName() +" > " + e);
                if(messageType==Constants.ALIVE_MSG) {
                    alivePackets.add("null");
                    //mHandler.obtainMessage(Constants.MESSAGE_READ, bytes, -1, mmSocket.getRemoteDevice().getName()+":"+temp).sendToTarget();
                    Log.i("ConnectedThread", "HeartBeat Message");
                }
                else if(messageType==Constants.DATA_MSG) {

                    allPackets.add("null");
                }
                try {
                    mmSocket.close();
                } catch (IOException e1) {
                    Log.e("ConnectThread","Error in close "+ e1);
                }
                return;
            }

            connect(mmSocket,mmDevice,messageType);
                //break;
            //}
        }
    }

    private class ConnectionThread extends Thread{
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectionThread(BluetoothSocket socket) {
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

        public void run() {
            byte[] temp = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
            byte []buffer;
            while (true) {
                try {
                    Log.i("connectedThread", "Reading from socket ");
                    bytes = mmInStream.read(temp);
                    if (bytes > 0) {
                        mHandler.obtainMessage(Constants.MESSAGE_READ, bytes, -1, temp).sendToTarget();
                        buffer = Arrays.copyOf(temp,bytes);
                        Log.i("ConnectedThread", "Received Data :: " + new String(buffer));
                        String message =new String(buffer);
                        if(messageType==Constants.ALIVE_MSG) {
                            alivePackets.add(new String(buffer));
                            //mHandler.obtainMessage(Constants.MESSAGE_READ, bytes, -1, mmSocket.getRemoteDevice().getName()+":"+temp).sendToTarget();
                            Log.i("ConnectedThread", "HeartBeat Message");
                        }
                        else if(messageType==Constants.DATA_MSG) {

                            allPackets.add(new String(buffer));
                        }
                        mmSocket.close();
                        break;
                    }
                }catch (IOException e) {
                    Log.e("connectedSockets","Exception in connected thread run() " + e);
                    if(messageType==Constants.ALIVE_MSG) {
                        alivePackets.add("null");
                        //mHandler.obtainMessage(Constants.MESSAGE_READ, bytes, -1, mmSocket.getRemoteDevice().getName()+":"+temp).sendToTarget();
                        Log.i("ConnectedThread", "HeartBeat Message");
                    }
                    else if(messageType==Constants.DATA_MSG) {

                        allPackets.add("null");
                    }
                    try {
                        mmSocket.close();
                    } catch (IOException e1) {
                        Log.e("ConnectThread","Error in close "+ e1);
                    }
                    break;
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                Log.i("Connected Thread" , "Writing to streamData :: " + buffer);
                mmOutStream.write(buffer);
            } catch (IOException e) {
                Log.e("ConnectedThread","write error " + e);
                if(messageType==Constants.ALIVE_MSG) {
                    alivePackets.add("null");
                    //mHandler.obtainMessage(Constants.MESSAGE_READ, bytes, -1, mmSocket.getRemoteDevice().getName()+":"+temp).sendToTarget();
                    Log.i("ConnectedThread", "HeartBeat Message");
                }
                else if(messageType==Constants.DATA_MSG) {

                    allPackets.add("null");
                }
            }
        }
    }
    class packet
    {
        int type;
        public packet(int _n){
            type = _n;
        }

        @Override
        public String toString()
        {
            return new String(""+type);
        }
    }


    public class MyClientTask extends AsyncTask<Void, Void, Void> {

        String dstAddress;
        int dstPort;
        String response = "";
        String dataPacket;

        MyClientTask(String addr, int port){
            dstAddress = addr;
            dstPort = port;
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            Socket socket = null;
            Log.i("MyClientTask","in dobackgroung befor socket create");
            try {
                socket = new Socket(dstAddress, dstPort);
                Log.i("MyClientTask","in dobackgroung after socket create");
                /*ByteArrayOutputStream byteArrayOutputStream =
                        new ByteArrayOutputStream(1024);
                byte[] buffer = new byte[1024];

                int bytesRead;
                InputStream inputStream = socket.getInputStream();
                */

                Log.i("MyClientTask","doInBackground ");
                DataOutputStream DOS = new DataOutputStream(socket.getOutputStream());
                Log.i("MyClientTask","doInBackground allPacketSize = " + allPackets.size());
                for (String packet : allPackets)
                {

                    if(!packet.equalsIgnoreCase("null")) {
                        String [] packetArray = packet.split(",");

                        if(activeSensors.contains(packetArray[0]))
                        {
                                //String output = castIntoPacket(packet,packetArray[1]);
                                //DOS.writeBytes(output);
                                DOS.writeBytes(packet);
                            Log.i("MyClientTask", "sending done :: " + packet);

                    }else{
                            Log.i("MyClientTask", "Device ID :" + packetArray[0] +"is not registered on repo server");
                        }
                    }

                }
                //DOS.close();
    /*
     * notice:
     * inputStream.read() will block if no data return
     */
                /*while ((bytesRead = inputStream.read(buffer)) != -1){
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                    response += byteArrayOutputStream.toString("UTF-8");
                }*/

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                response = "UnknownHostException: " + e.toString();
                Log.e("doInBackground","UnknownHostException: " + e.toString());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                response = "IOException: " + e.toString();
                Log.e("doInBackground","UnknownHostException: " + e.toString());
            }finally{
                /*if(socket != null){
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }*/
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //textResponse.setText(response);
            //super.onPostExecute(result);
        }

    }

    public class MyRepoTask extends AsyncTask<Void, Void, Void> {

        String dstAddress;
        int dstPort;
        String response = "";
        String dataPacket;

        MyRepoTask(String addr, int port){
            dstAddress = addr;
            dstPort = port;
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            Socket socket = null;

            Log.i("MyRepoTask","Before socket create ");
            try {
                socket = new Socket(dstAddress, dstPort);
                Log.i("MyRepoTask","After socket create ");
                /*ByteArrayOutputStream byteArrayOutputStream =
                        new ByteArrayOutputStream(1024);
                byte[] buffer = new byte[1024];

                int bytesRead;
                InputStream inputStream = socket.getInputStream();
                */

                Log.i("MyRepoTask","doInBackground ");
                DataOutputStream DOS = new DataOutputStream(socket.getOutputStream());
                Log.i("MyRepoTask","doInBackground alivePacketSize = " + alivePackets.size());
                StringBuilder wholePacket = new StringBuilder();
                for (String packet : alivePackets)
                {
                    if(!packet.equalsIgnoreCase("null")) {
                        wholePacket.append(packet+",");
                        Log.i("MyRepoTask", "sending done :: " + packet);
                    }
                }
                DOS.writeBytes(wholePacket.toString());
                //DOS.flush();
                //DOS.close();
    /*
     * notice:
     * inputStream.read() will block if no data return
     */
                /*while ((bytesRead = inputStream.read(buffer)) != -1){
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                    response += byteArrayOutputStream.toString("UTF-8");
                }*/

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                response = "UnknownHostException: " + e.toString();
                Log.e("MyRepoTask","UnknownHostException: " + e.toString());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                response = "IOException: " + e.toString();
                Log.e("MyRepoTask","UnknownHostException: " + e.toString());
            }finally{
                /*if(socket != null){
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }*/
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //textResponse.setText(response);
            //super.onPostExecute(result);
        }

    }

    public void bootUp()
    {
        Log.i("BootUp","Gateway is booting UP!!!");
        BootUpSystem bootUpSystem = new BootUpSystem(repodstAddress,repodstPort);
        bootUpSystem.execute();
        Log.i("BootUp","Gateway is bootup finished!!!");
    }

    public class BootUpSystem extends AsyncTask<Void, Void,Void>{
        String dstAddress;
        int dstPort;

        public BootUpSystem(String _ip,int _port){
            dstAddress = _ip;
            dstPort = _port;
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            Socket socket = null;

            try {
                socket = new Socket(dstAddress, dstPort);

                ByteArrayOutputStream byteArrayOutputStream =new ByteArrayOutputStream(1024);
                byte[] buffer = new byte[1024];

                int bytesRead;
                InputStream inputStream = socket.getInputStream();
                DataOutputStream DOS = new DataOutputStream(socket.getOutputStream());
                String packet = Constants.requestDeviceID;
                Log.i("bootUp","Requesting");
                DOS.writeBytes(packet);
                DOS.flush();
                //DOS.close();

                bytesRead = inputStream.read(buffer);
                    String receive = new String(Arrays.copyOf(buffer,bytesRead));
                    Log.i("bootUp","String :: "+receive);
                    String [] receivedDevicesIds = receive.split(",");
                    for(String s : receivedDevicesIds){
                        if(!activeSensors.contains(s))
                            activeSensors.add(s);
                    }
                Log.i("bootUp","Active Sensors List size = " + activeSensors.size() );
                for(String s : activeSensors){
                    Log.i("bootUp","Active Sensors :: " + s );
                }


/*

                //To receive Type Handler jar
                packet = Constants.requestTypeHandler;
                DOS.writeBytes(packet);
                DOS.flush();

                OutputStream outputJarFile = new FileOutputStream(Constants.typeHandlerPath);
                while ((bytesRead = inputStream.read(buffer)) != -1){
                    outputJarFile.write(buffer);
                }
                outputJarFile.flush();
                outputJarFile.close();
*/
            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                Log.e("doInBackground","UnknownHostException: " + e.toString());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.e("doInBackground","UnknownHostException: " + e.toString());
            }finally{
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

        }
    }


    public String castIntoPacket(String input,String packetType)
    {
        String packet = new String ();
        try {
            DexClassLoader classLoader = new DexClassLoader(Constants.typeHandlerPath, "/mnt/sdcard/tmp", null, getClass().getClassLoader());
            Class<?> className = classLoader.loadClass(Constants.jarClassName);
            Object pktObj = className.newInstance();
            Method method = className.getMethod(packetType,new Class  [] {String.class });
            packet = (String)method.invoke(pktObj,input);
        } catch (Exception e) {
          e.printStackTrace();
        }
        return packet;
    }

}

