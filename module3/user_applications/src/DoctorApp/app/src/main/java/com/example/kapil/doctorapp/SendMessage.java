package com.example.kapil.doctorapp;

import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class SendMessage extends ActionBarActivity {

    private String getQuery(List<Pair> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Pair pair : params) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.first.toString(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.second.toString(), "UTF-8"));
        }

        return result.toString();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        final Button button = (Button) findViewById(R.id.submit);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Runnable runObj = new Runnable() {
                    @Override
                    public void run() {

                        EditText textBox = (EditText) findViewById(R.id.textBox);
                        if(textBox.getText().toString().trim().equalsIgnoreCase(""))
                            return;

                        try {
                            URL url =  new URL("http://10.42.0.24:3005/postUserData/");
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setReadTimeout(10000);
                            conn.setConnectTimeout(15000);
                            conn.setRequestMethod("POST");
                            conn.setDoInput(true);
                            conn.setDoOutput(true);

                            List<Pair> params = new ArrayList<Pair>();
                            params.add(new Pair("name", "DoctorSaid"));
                            params.add(new Pair("data", textBox.getText()));

                            OutputStream os = conn.getOutputStream();
                            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(os, "UTF-8"));
                            writer.write(getQuery(params));
                            writer.flush();
                            writer.close();
                            os.close();
                            conn.connect();

                            InputStream inputStream = conn.getInputStream();
                            final StringBuffer stringBuffer = new StringBuffer();

                            if (inputStream == null) {
                                return;
                            }
                            String line;
                            BufferedReader reader1 = new BufferedReader(new InputStreamReader(inputStream));
                            while ((line = reader1.readLine()) != null) {
                                stringBuffer.append(line + "\n");
                            }

                            SendMessage.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    EditText et = (EditText) findViewById(R.id.textBox);
                                    et.setText("");
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }


                };

                new Thread(runObj).start();
            }


        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_send_message, menu);
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
}
