package com.example.om.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Pair;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class NavigationActivity extends ActionBarActivity {

    Rect dest;
    double requested_sX,requested_sY,retrieved_X,retrieved_Y;
    double requested_dX,requested_dY;
    boolean status=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new DrawView(this));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_second, menu);
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


    public class Vertex
    {
        double x,y;
        public Vertex()
        {}
        public Vertex(double x,double y)
        {
            this.x=x;
            this.y=y;
        }
    }
    public class DrawView extends View {

        Paint paint;
        double sX,sY,vX,vY,dX,dY,r,vR;
        int w,h;
        Rect dest;
        ArrayList<Vertex> graph;
        boolean flag=false;
        public DrawView(Context context) {
            super(context);
            r=10;
            vR=15;

            graph=new ArrayList();
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            w = size.x;
            h = size.y;
            dX=348;
            dY=1060;
            paint=new Paint();
            paint.setColor(Color.RED);
            graph.add(new Vertex(57,45));
            graph.add(new Vertex(645, 40));
            graph.add(new Vertex(60,760));
            graph.add(new Vertex(645,745));
            dest = new Rect(0, 0, getWidth(), getHeight());
            graph.add(new Vertex(dX,dY));
            sX=requested_sX=graph.get(0).x;
            sY=requested_sY=graph.get(0).y;
            // dX=retrieved_X=graph.get(1).x;
            // dY=retrieved_Y=graph.get(1).y;

            requested_dX=graph.get(4).x;
            requested_dY=graph.get(4).y;
            while(status==false) {

                RestControl server = new RestControl();
                server.getNextDestination();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            vX=sX;
            vY=sY;
            dX=retrieved_X;
            dY=retrieved_Y;
            status=false;

        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            sX = requested_sX;
            sY = requested_sY;
            dX = retrieved_X;
            dY = retrieved_Y;

            paint.setColor(Color.RED);
            Bitmap tempBMP = BitmapFactory.decodeResource(getResources(), R.drawable.map);
            int width = tempBMP.getWidth();

            int height = tempBMP.getHeight();

            float scaleWidth = ((float) w) / width;

            float scaleHeight = ((float) h) / height;

// create a matrix for the manipulation

            Matrix matrix = new Matrix();

// resize the bit map

            matrix.postScale(scaleWidth, scaleHeight);

// recreate the new Bitmap

            Bitmap resizedBitmap = Bitmap.createBitmap(tempBMP, 0, 0, width, height, matrix, false);

            canvas.drawBitmap(resizedBitmap, 0, 0, paint);


            System.out.println("Current destination" + dX + " " + dY);


            float d = 10;

            double n = Math.sqrt((dX - vX) * (dX - vX) + (dY - vY) * (dY - vY));

            vX = vX + d * (dX - vX) / n;
            vY = vY + d * (dY - vY) / n;
            System.out.println("Distance =" + n);
            paint.setColor(Color.RED);
            tempBMP = BitmapFactory.decodeResource(getResources(), R.drawable.amb);
            width = tempBMP.getWidth();

            height = tempBMP.getHeight();

            scaleWidth = ((float) 65) / width;

            scaleHeight = ((float) 65) / height;

// create a matrix for the manipulation

            matrix = new Matrix();

// resize the bit map

            matrix.postScale(scaleWidth, scaleHeight);

// recreate the new Bitmap

            resizedBitmap = Bitmap.createBitmap(tempBMP, 0, 0, width, height, matrix, false);


            canvas.drawBitmap(resizedBitmap, (float)vX-30, (float)vY-30, paint);

            //canvas.drawCircle((float) vX, (float) vY, (float) vR, paint);

            if (n < 5) {

                requested_sX = dX;
                requested_sY = dY;

                while (status == false) {
                    System.out.println("Sending request for new Destination" + requested_sX + " " + requested_sY);
                    if (requested_sX == requested_dX && requested_sY == requested_dY)
                    {    flag = true; break;}
                    else {
                        RestControl server = new RestControl();
                        server.getNextDestination();

                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                status=false;

            }
            if (flag)
            {
                Runnable runObj  = new Runnable() {
                    @Override
                    public void run() {
                        URL url = null;
                        try {
                            System.out.println("---------------------------------------------------------" + getIntent().getStringExtra("ambulance"));
                            url = new URL("http://10.42.0.24:3000/register/"+ getIntent().getStringExtra("ambulance"));
                            System.out.println(url.toString());
                            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
                            //httpCon.setDoOutput(true);
                            httpCon.setRequestProperty(
                                    "Content-Type", "application/x-www-form-urlencoded" );
                            httpCon.setRequestMethod("DELETE");
                            httpCon.connect();
                            int responseCode = httpCon.getResponseCode();
                            System.out.println(responseCode);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (ProtocolException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };

                new Thread(runObj).start();
                NavigationActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(getContext() , "Destination Reached" , Toast.LENGTH_LONG).show();
                    Intent intent = new Intent( getContext() ,DestReachedActivity.class);
                    startActivity(intent);

                }
            });
            }
            else {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        invalidate();
                    }
                }, 1000 / 10);
            }
        }

    }
    Handler mHandler = new Handler();
    class RestControl {
        public void getNextDestination()
        {

            Runnable RunnableObj = new Runnable() {
                @Override
                public void run() {
                    try {

                        URL url = new URL("http://10.42.0.24:3004/postUserData/");
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setReadTimeout(10000);
                        conn.setConnectTimeout(15000);
                        conn.setRequestMethod("POST");
                        conn.setDoInput(true);
                        conn.setDoOutput(true);

                        List<Pair> params = new ArrayList<Pair>();
                        params.add(new Pair("name","1234"));
                        params.add(new Pair("sX", ""+requested_sX));
                        params.add(new Pair("sY", ""+requested_sY));
                        System.out.println("Requested Source " + requested_sX+" "+requested_sY);
                        System.out.println("Requested Destination " + requested_dX+" "+requested_dY);
                        params.add(new Pair("dX", ""+requested_dX));
                        params.add(new Pair("dY", ""+requested_dY));

                        OutputStream os = conn.getOutputStream();
                        BufferedWriter writer = new BufferedWriter(
                                new OutputStreamWriter(os, "UTF-8"));
                        writer.write(getQuery(params));
                        writer.flush();
                        writer.close();
                        os.close();
                        conn.connect();
                        System.out.println("Request sent");
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

                        System.out.println(" url data **************** ------>  " + stringBuffer);

                        JSONObject object=new JSONObject(stringBuffer.toString());
                        final int x = Integer.parseInt(object.getString("x"));
                        final int y = Integer.parseInt(object.getString("y"));
                        final String next=object.getString("z");

                        retrieved_X=x;
                        retrieved_Y=y;

                        status=true;


                        NavigationActivity.this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                Toast toast = Toast.makeText(NavigationActivity.this, "Next Destination :"+next, Toast.LENGTH_LONG);
                                toast.show();

                            }
                        });


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            Thread thread = new Thread(RunnableObj);
            thread.start();

        }
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

    }
}
