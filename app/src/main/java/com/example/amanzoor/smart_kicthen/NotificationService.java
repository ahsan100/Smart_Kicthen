package com.example.amanzoor.smart_kicthen;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;

import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationService extends Service {

    public String IP,GROUPID, MEMBERID;
    public String API = "/api/location_service/";
    public String API_FLAG = "/api/monitor_member/";
    public String LONGITUDE, LATIITUDE;
    public String FLAG;
    NotificationManager mNotificationManager ;
    public Timer timer = new Timer();

    public NotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        final Handler handler = new Handler();
        TimerTask doAsynchronousTask = new TimerTask()
        {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try
                        {
                                new GetFlag().execute();
                        }
                        catch (Exception e)
                        {

                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 23840);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IP = intent.getStringExtra("IP");
        MEMBERID = intent.getStringExtra("memberid");
        GROUPID = intent.getStringExtra("groupid");
        return super.onStartCommand(intent, flags, startId);
    }

//    public class GetLocation extends AsyncTask<Void,Void,Void>
//    {
//        protected void onPreExecute() {
//
//        }
//        protected Void doInBackground(Void... params) {
//            HttpURLConnection urlConnection = null;
//            try {
//                JSONObject RESPONSE;
//                String Url = IP+API+MEMBERID;
//                URL urlToRequest = new URL(Url);
//                urlConnection = (HttpURLConnection)
//                        urlToRequest.openConnection();
//                urlConnection.setConnectTimeout(1000);
//                urlConnection.setReadTimeout(1000);
//
//                int statusCode = urlConnection.getResponseCode();
//
//                InputStream in = new BufferedInputStream(
//                        urlConnection.getInputStream());
//                RESPONSE = new JSONObject(getResponseText(in));
//                LATIITUDE = RESPONSE.getJSONObject("membercoordinate").getJSONArray("data").getJSONObject(0).getString("value");
//                LONGITUDE = RESPONSE.getJSONObject("membercoordinate").getJSONArray("data").getJSONObject(1).getString("value");
//
//
//
//            } catch (MalformedURLException e) {
//            } catch (SocketTimeoutException e) {
//                //Toast.makeText(this,"System is Busy, Try Again", Toast.LENGTH_LONG).show();
//            } catch (IOException e) {
//                System.out.println("ERROR3");
//            }catch (JSONException e) {
//            }finally {
//                if (urlConnection != null) {
//                    urlConnection.disconnect();
//                }
//            }
//            return null;
//        }
//        protected void onPostExecute(Void result) {
//            System.out.println("AHSAN ITS " + LATIITUDE + " : "+ LONGITUDE);
//
//        }
//    }
    public class GetFlag extends AsyncTask<Void,Void,Void>
    {
        protected void onPreExecute() {

        }
        protected Void doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            try {
                JSONObject RESPONSE;
                String Url = IP+API_FLAG+GROUPID;
                URL urlToRequest = new URL(Url);
                urlConnection = (HttpURLConnection)
                        urlToRequest.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type",
                        "application/vnd.collection+json");
                urlConnection.connect();

                Map<String, String> comment = new HashMap<String, String>();
                comment.put("memberid", MEMBERID);
                String json = new GsonBuilder().create().toJson(comment, Map.class);

                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(json);
                writer.close();
                os.close();
                //System.out.println("HERE" + Url + params[0] + params[1]);
                int statusCode = urlConnection.getResponseCode();

                InputStream in = new BufferedInputStream(
                        urlConnection.getInputStream());
                RESPONSE = new JSONObject(getResponseText(in));
                FLAG = RESPONSE.getJSONArray("monitormember").getJSONObject(0).getJSONArray("data").getJSONObject(0).getString("value");

            } catch (MalformedURLException e) {
            } catch (SocketTimeoutException e) {
                //Toast.makeText(this,"System is Busy, Try Again", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                System.out.println("ERROR3");
            }catch (JSONException e) {
            }finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }
        protected void onPostExecute(Void result) {
            System.out.println("FLAGING " + FLAG);
            if(FLAG.equals("1")){
                System.out.println("NOTIFICATION TIME");
                timer.cancel();
                Intent intent = new Intent();
                PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
                Notification noti = new Notification.Builder(getApplicationContext())
                        .setTicker("SMART KITCHEN")
                        .setContentTitle("LOW PRODUCT ALERT")
                        .setContentText("PLEASE BUY THE GROCERY")
                        .setSmallIcon(R.drawable.logo)
                        .setContentIntent(pIntent).build();
                noti.flags=Notification.FLAG_AUTO_CANCEL;
                mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(1, noti);
            }

        }
    }
    private static String getResponseText(InputStream inStream) {
        return new Scanner(inStream).useDelimiter("\\A").next();
    }


}
