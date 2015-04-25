package com.example.kapil.doctorapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.SECONDS;


public class MainActivity extends ActionBarActivity {

    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ArrayList<String> weekForecast = new ArrayList<>();

        arrayAdapter = new ArrayAdapter<String>(this, R.layout.list_item_sensedata, R.id.list_item_senseData_textview, weekForecast);
        ListView listPtr = (ListView) findViewById(R.id.listview_senseData);
        listPtr.setAdapter(arrayAdapter);

        RestControl restControl = new RestControl();
        restControl.beepForAnHour();


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
        if (id == R.id.action_send){
            Intent intent = new Intent(this , SendMessage.class);
            startActivity(intent);
        }
        else if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    class RestControl {
        private final ScheduledExecutorService scheduler =
                Executors.newScheduledThreadPool(1);

        private ScheduledFuture beeperHandle = null;


        public void stop() {
            beeperHandle.cancel(true);
        }

        public void beepForAnHour() {
            final Runnable executor = new Runnable() {
                public void run() {

                    HttpURLConnection urlConnection = null;
                    BufferedReader reader = null;
                    String responseJsonStr = null;
                    String ambulance_num="1234";
                    URL url = null;
                    String name = "";
                    int timeStamp = 0;
                    String valueType = "";
                    int value = 0;
                    try {
                        url = new URL("http://10.42.0.24:3005/getHealthData/");
                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestMethod("GET");
                        urlConnection.connect();
                        InputStream inputStream = urlConnection.getInputStream();
                        StringBuffer stringBuffer = new StringBuffer();

                        if (inputStream == null) {
                            return;
                        }
                        String line;
                        reader = new BufferedReader(new InputStreamReader(inputStream));
                        while ((line = reader.readLine()) != null) {
                            stringBuffer.append(line + "\n");
                        }

                        System.out.println(" url data **************** ------>  " + stringBuffer);
                        if (stringBuffer.length() == 0) {
                            return;
                        }

                        responseJsonStr = stringBuffer.toString();

                        final String NAME = "name";
                        final String VALUETYPE = "valueType";
                        final String VALUE = "value";
                        final String TIMESTAMP = "timestamp";
                        final String ID = "_id";

                        System.out.println("-------------------Here -------------");
                        JSONArray reportObjList = new JSONArray(responseJsonStr);
                         int reportObjListLength = reportObjList.length();
                        String resp = "";
                        for(int i = 0; i < reportObjListLength; i++) {
                            JSONObject reportObj = reportObjList.getJSONObject(i);

                            name = reportObj.getString(NAME);

                            if(!name.equalsIgnoreCase(ambulance_num))
                                continue;
                            System.out.println(reportObj);
                            System.out.println("new timeStamp : " + reportObj.getInt(TIMESTAMP));
                            System.out.println("Here");
                            timeStamp = reportObj.getInt(TIMESTAMP);
                            valueType = reportObj.getString(VALUETYPE);
                            value = reportObj.getInt(VALUE);
                            System.out.println("valueType ----> " + valueType);
                            resp = resp + "" + valueType + " : " + value+"\n";
                            // }


                        }

                        if (!resp.equalsIgnoreCase("")) {
                            DateFormat df = new SimpleDateFormat("HH:mm:ss");
                            Calendar calobj = Calendar.getInstance();
                            resp = resp +"                                                                    " +df.format(calobj.getTime());
                            final String res = resp;
                            System.out.println("resp value is ---->" + resp);


                            MainActivity.this.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {

                                    int length = arrayAdapter.getCount();

                                    if (length < 15) {
                                        arrayAdapter.add(res);
                                    } else {
                                        ArrayList<String> arrayList = new ArrayList<String>();

                                        for (int i = 1; i < length; i++) {
                                            arrayList.add(arrayAdapter.getItem(i));
                                        }
                                        arrayList.add(res);

                                        arrayAdapter.clear();
                                        for (String arrayListObj : arrayList) {
                                            arrayAdapter.add(arrayListObj);
                                        }
                                    }
                                }
                            });
                        }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
            };
            beeperHandle =
                    scheduler.scheduleAtFixedRate(executor, 02, 15, SECONDS);

            scheduler.schedule(new Runnable() {
                public void run() {
                    beeperHandle.cancel(true);
                }
            }, 60 * 60, SECONDS);

        }

    }
}


