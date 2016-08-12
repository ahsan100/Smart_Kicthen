package com.example.amanzoor.smart_kicthen;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Scanner;
import java.util.Timer;

public class NotificationService_NoBtn extends Service {

    public String IP,GROUPID, MEMBERID;
    public String API = "/api/location_service/";
    public String API_FLAG = "/api/monitor_member/";
    public Double LONGITUDE, LATIITUDE;
    public String FLAG;
    NotificationManager mNotificationManager ;
    public Timer timer = new Timer();
    public MyReceiver myReceiver;



    public NotificationService_NoBtn() {
    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LocationService.MY_ACTION);
        registerReceiver(myReceiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IP = intent.getStringExtra("IP");
        MEMBERID = intent.getStringExtra("memberid");
        GROUPID = intent.getStringExtra("groupid");
        return super.onStartCommand(intent, flags, startId);
    }

    public class GetLocation extends AsyncTask<Void,Void,Void>
    {
        protected void onPreExecute() {

        }
        protected Void doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            try {
                JSONObject RESPONSE;
                String Url = IP+API+MEMBERID;
                URL urlToRequest = new URL(Url);
                urlConnection = (HttpURLConnection)
                        urlToRequest.openConnection();
                urlConnection.setConnectTimeout(1000);
                urlConnection.setReadTimeout(1000);

                int statusCode = urlConnection.getResponseCode();

                InputStream in = new BufferedInputStream(
                        urlConnection.getInputStream());
                RESPONSE = new JSONObject(getResponseText(in));

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
            System.out.println("AHSAN ITS " + LATIITUDE + " : "+ LONGITUDE);

        }
    }
    private static String getResponseText(InputStream inStream) {
        return new Scanner(inStream).useDelimiter("\\A").next();
    }

    public class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {

            LONGITUDE = arg1.getDoubleExtra("longi", 0);
            LATIITUDE = arg1.getDoubleExtra("lati",0);
            String[] projection = new String[]{ provider_location.BasicData.NAMING, provider_location.BasicData.LATITUDE1, provider_location.BasicData.LONGITUDE1,
                    provider_location.BasicData.LATITUDE2, provider_location.BasicData.LONGITUDE2};
            Cursor cursor = getContentResolver().query(provider_location.BasicData.CONTENT_URI, projection, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    String NAME = cursor.getString(0);
                    Double LATI1 = Double.parseDouble(cursor.getString(1));
                    Double LONGI1 = Double.parseDouble(cursor.getString(2));
                    Double LATI2 = Double.parseDouble(cursor.getString(3));
                    Double LONGI2 = Double.parseDouble(cursor.getString(4));
                    if((LATIITUDE >= LATI1 && LATIITUDE <= LATI2) && (LONGITUDE >= LONGI1 && LONGITUDE <= LONGI2)){
                        System.out.println("I AM NEAR " + NAME);
                        Toast.makeText(getBaseContext(), " I AM NEAR " + NAME, Toast.LENGTH_LONG).show();
                    }
                } while (cursor.moveToNext());
            }
//            if((LATIITUDE <= 65.060602 && LATIITUDE >= 65.059914) && (LONGITUDE >= 25.478718 && LONGITUDE <= 25.480799)){
//                System.out.println("I AM NEAR SALE");
//            }
            //System.out.println("NO" + LONGITUDE.toString() + LATIITUDE.toString());
        }
    }
}
