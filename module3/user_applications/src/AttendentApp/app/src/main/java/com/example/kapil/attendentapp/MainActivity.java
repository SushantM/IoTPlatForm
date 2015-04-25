package com.example.kapil.attendentapp;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
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
        if (id == R.id.action_settings) {
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
                    URL url = null;
                    String name = "";
                    String  dataString = "";
                    try {
                        url = new URL("http://10.42.0.24:3005/getUserdata/DoctorSaid/");
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
                        final String data = "data";
                        final String ID = "_id";

                        System.out.println("-------------------Here -------------");

                        JSONObject reportObj = new JSONObject(responseJsonStr);

                        if(reportObj == null)
                            return ;
                        name = reportObj.getString(NAME);
                        dataString  = reportObj.getString(data);
                        System.out.println(reportObj);


                        // }


                        if (!dataString.equalsIgnoreCase("")) {
                            final String resp =  "Doctor : " + dataString;
                            System.out.println("resp value is ---->" + resp);


                            MainActivity.this.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {

                                    int length = arrayAdapter.getCount();

                                    if (length < 15) {
                                        arrayAdapter.add(resp);
                                    } else {
                                        ArrayList<String> arrayList = new ArrayList<String>();

                                        for (int i = 1; i < length; i++) {
                                            arrayList.add(arrayAdapter.getItem(i));
                                        }
                                        arrayList.add(resp);

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
                    scheduler.scheduleAtFixedRate(executor, 0, 05, SECONDS);

            scheduler.schedule(new Runnable() {
                public void run() {
                    beeperHandle.cancel(true);
                }
            }, 60 * 60, SECONDS);

        }

    }
}
