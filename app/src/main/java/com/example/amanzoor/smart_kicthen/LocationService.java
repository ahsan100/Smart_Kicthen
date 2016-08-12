package com.example.amanzoor.smart_kicthen;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LocationService extends Service {


    public String IP,GROUPID, MEMBERID;
    public String API = "/api/location_service/";
    public LocationManager locationManager;
    public MyLocationListener listener;
    private ScheduledExecutorService scheduleTaskExecutor;
    public Double lati, longi;
    final static String MY_ACTION = "MY_ACTION";
    //String[] LOCATIONING = new String[2];

    public LocationService() {

    }

    @Override
    public void onCreate()
    {
        super.onCreate();
//        scheduleTaskExecutor = Executors.newScheduledThreadPool(5);
//        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("AHSANNN" + lati + longi );
//                String[] LOCATIONING = new String[2];
//                LOCATIONING[0] = lati.toString();
//                LOCATIONING[1] = longi.toString();
////                if(LOCATION[0] != null) {
////                    //new updateLocationTask().execute(LOCATION);
////                }
//            }
//        }, 0, 10, TimeUnit.SECONDS); // or .MINUTES, .HOURS etc.

        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask()
        {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try
                        {
                            String[] LOCATIONING = new String[2];
                            LOCATIONING[0] = lati.toString();
                            LOCATIONING[1] = longi.toString();
                            if (LOCATIONING[0] != null) {
                                new updateLocationTask().execute(LOCATIONING);
                            }
                        }
                        catch (Exception e)
                        {

                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 60000);

        final Handler handler1 = new Handler();
        Timer timer1 = new Timer();
        TimerTask doAsynchronousTask1 = new TimerTask()
        {
            @Override
            public void run() {
                handler1.post(new Runnable() {
                    public void run() {
                        try
                        {
                            Intent intent= new Intent();
                            intent.setAction(MY_ACTION);
                            if (lati != null && longi != null) {
                                intent.putExtra("lati", lati);
                                intent.putExtra("longi", longi);
                                sendBroadcast(intent);
                            }
                        }
                        catch (Exception e)
                        {

                        }
                    }
                });
            }
        };
        timer1.schedule(doAsynchronousTask1, 0, 10000);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IP = intent.getStringExtra("IP");
        MEMBERID = intent.getStringExtra("memberid");
        GROUPID = intent.getStringExtra("groupid");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            listener = new MyLocationListener();
            //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 0, listener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 0, listener);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // handler.removeCallbacks(sendUpdatesToUI);
        super.onDestroy();
        Log.v("STOP_SERVICE", "DONE");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(listener);
        }
    }

    public class MyLocationListener implements LocationListener
    {

        public void onLocationChanged(final Location loc)
        {
            Log.i("******************", "Location changed");
            lati = loc.getLatitude();
            longi = loc.getLongitude();
        }

        public void onProviderDisabled(String provider)
        {
            Toast.makeText(getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT).show();
        }


        public void onProviderEnabled(String provider)
        {
            Toast.makeText( getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }


        public void onStatusChanged(String provider, int status, Bundle extras)
        {

        }

    }


    public class updateLocationTask extends AsyncTask<String,Void,Void>
    {
        protected void onPreExecute() {
        }
        protected Void doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            try {
                String Url = IP+API+MEMBERID;
                URL urlToRequest = new URL(Url);
                urlConnection = (HttpURLConnection)
                        urlToRequest.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("PUT");
                urlConnection.setRequestProperty("Content-Type",
                        "application/vnd.collection+json");
                urlConnection.connect();

                Map<String, String> comment = new HashMap<String, String>();
                comment.put("latitude", params[0]);
                comment.put("longitude", params[1]);
                String json = new GsonBuilder().create().toJson(comment, Map.class);

                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(json);
                writer.close();
                os.close();
                //System.out.println("HERE" + Url + params[0] + params[1]);
                int statusCode = urlConnection.getResponseCode();

            } catch (MalformedURLException e) {
            } catch (SocketTimeoutException e) {
                Toast.makeText(getBaseContext(),"System is Busy, Try Again", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                System.out.println("ERROR3");
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }
        protected void onPostExecute(Void result) {
            System.out.println("HERING");

        }
    }

}
