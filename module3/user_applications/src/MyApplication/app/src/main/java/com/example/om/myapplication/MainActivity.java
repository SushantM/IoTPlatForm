package com.example.om.myapplication;

import android.content.Intent;
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

public class MainActivity extends ActionBarActivity {

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
        setContentView(R.layout.activity_main);
        int ambulanceName= 1234;
        int start_longitude = 57;
        int start_latitude = 45;
        int end_latitude = 348;
        int end_longitude = 1060;



        EditText editText = (EditText) findViewById(R.id.editText);
        EditText editText1 = (EditText) findViewById(R.id.editText1);
        EditText editText2 = (EditText) findViewById(R.id.editText2);
        EditText editText3 = (EditText) findViewById(R.id.editText3);
        EditText editText4 = (EditText) findViewById(R.id.editText4);

        editText.setText("" + ambulanceName);
        editText1.setText(""+start_longitude);
       editText2.setText(""+start_latitude);
        editText3.setText(""+end_longitude);
       editText4.setText(""+end_latitude);


    /*    setContentView(R.layout.activity_main);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int w = size.x;
        int h = size.y;

        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap bmp = Bitmap.createBitmap(w, h, conf); // this creates a MUTABLE bitmap
        Canvas canvas = new Canvas(bmp);

        Paint p=new Paint();
        p.setColor(Color.GREEN);
        Bitmap tempBMP = BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher);

        canvas.drawBitmap(tempBMP, 0, 0, null);


        canvas.drawRect(0,0,w,h,p);

        LinearLayout ll = (LinearLayout) findViewById(R.id.rect);

        ll.setBackgroundDrawable(new BitmapDrawable(getResources(), bmp));
*/


        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Runnable runObj = new Runnable() {
                    @Override
                    public void run() {

                        EditText editText = (EditText) findViewById(R.id.editText);
                        EditText editText1 = (EditText) findViewById(R.id.editText1);
                        EditText editText2 = (EditText) findViewById(R.id.editText2);
                        EditText editText3 = (EditText) findViewById(R.id.editText3);
                        EditText editText4 = (EditText) findViewById(R.id.editText4);


                        if(editText.getText().toString().trim().equalsIgnoreCase(""))
                            return;

                        try {
                            URL url =  new URL("http://10.42.0.24:3000/register/");
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setReadTimeout(10000);
                            conn.setConnectTimeout(15000);
                            conn.setRequestMethod("POST");
                            conn.setDoInput(true);
                            conn.setDoOutput(true);

                            List<Pair> params = new ArrayList<Pair>();
                            params.add(new Pair("name", editText.getText()));
                            params.add(new Pair("startX", editText1.getText()));
                            params.add(new Pair("startY", editText2.getText()));
                            params.add(new Pair("endX", editText3.getText()));
                            params.add(new Pair("endY", editText4.getText()));

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


                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }


                };

                new Thread(runObj).start();
                doit();

            }

        });
    }

    public void doit(){
        EditText editText = (EditText) findViewById(R.id.editText);
        Intent intent = new Intent(this , NavigationActivity.class);
        intent.putExtra("ambulance", editText.getText().toString());
        startActivity(intent);
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


}
