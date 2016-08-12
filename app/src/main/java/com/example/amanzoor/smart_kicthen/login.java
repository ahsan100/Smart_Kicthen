package com.example.amanzoor.smart_kicthen;


import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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

/**
 * Created by ahsanmanzoor on 20/04/16.
 */
public class login extends AppCompatActivity {

    public String IP = "http://ahsanmanzoor.pythonanywhere.com";
    public String API = "/api/user/";
    public String API_LOCATION = "/api/shop_coordinate/";
    public String MEMBERID;
    public String API_SEARCHMEMBER= "/api/search_members/";
    public static String RETURN_API ="/api/group/";
    protected EditText EMAIL,PASSWORD;
    String[] values = new String[2];
    public int statusCode, statuscode_member;
    public  String LINK;
    public String SHOPNAME, LATI1,LONGI1,LATI2,LONGI2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        EMAIL = (EditText)findViewById(R.id.email);
        PASSWORD = (EditText)findViewById(R.id.password);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }
    }

    public void loginClick(View view){
        String email,password;
        email = EMAIL.getText().toString();
        password = PASSWORD.getText().toString();
        values[0] =  email;
        values[1] = password;
        new findMemberTask().execute(values);
    }

    public void newMember(View view){
        Intent intent = new Intent(getBaseContext(), com.example.amanzoor.smart_kicthen.new_member.class);
        intent.putExtra("IP", IP);
        startActivity(intent);
    }

    public class MyDownloadTask extends AsyncTask<String,Void,Void>
    {
        protected void onPreExecute() {
        }
        protected Void doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            try {
                String Url = IP+API;
                URL urlToRequest = new URL(Url);
                urlConnection = (HttpURLConnection)
                        urlToRequest.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type",
                        "application/vnd.collection+json");
                urlConnection.connect();

                Map<String, String> comment = new HashMap<String, String>();
                comment.put("email", params[0]);
                comment.put("password", params[1]);
                String json = new GsonBuilder().create().toJson(comment, Map.class);

                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(json);
                writer.close();
                os.close();

                LINK = urlConnection.getHeaderField("location");;
                statusCode = urlConnection.getResponseCode();


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
            if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                Toast.makeText(getBaseContext(),"LOGIN UNSUCCESSFUL", Toast.LENGTH_LONG).show();
            }else if (statusCode == HttpURLConnection.HTTP_OK){
                Toast.makeText(getBaseContext(),"LOGIN SUCCESSFUL", Toast.LENGTH_LONG).show();
                if(LINK != null) {
                    Intent intent = new Intent(getBaseContext(), com.example.amanzoor.smart_kicthen.MainActivity.class);
                    intent.putExtra("IP", IP);
                    intent.putExtra("groupid", LINK.substring(IP.length()+ RETURN_API.length(), LINK.length()));
                    intent.putExtra("memberid", MEMBERID);
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(getBaseContext(), com.example.amanzoor.smart_kicthen.group.class);
                    intent.putExtra("IP", IP);
                    intent.putExtra("memberid", MEMBERID);
                    startActivity(intent);
                }
            }
        }
    }

    public class findMemberTask extends AsyncTask<String,Void,Void>
    {
        protected void onPreExecute() {

        }
        protected Void doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            try {
                JSONObject RESPONSE;
                String Url = IP+API_SEARCHMEMBER+params[0];
                URL urlToRequest = new URL(Url);
                urlConnection = (HttpURLConnection)
                        urlToRequest.openConnection();
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(5000);

                statuscode_member = urlConnection.getResponseCode();
                if(statuscode_member == HttpURLConnection.HTTP_OK) {
                    InputStream in = new BufferedInputStream(
                            urlConnection.getInputStream());
                    RESPONSE = new JSONObject(getResponseText(in));
                    MEMBERID = RESPONSE.getJSONArray("user").getJSONObject(0).getJSONArray("data").getJSONObject(0).getString("value");
                }
                System.out.println("HELLO" + statuscode_member);


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
            if (statuscode_member == 500) {
                Toast.makeText(getBaseContext(),"LOGIN UNSUCCESSFUL", Toast.LENGTH_LONG).show();
            }else {
                new getShopLocation().execute();
            }
        }

    }

    private static String getResponseText(InputStream inStream) {
        return new Scanner(inStream).useDelimiter("\\A").next();
    }


    public class getShopLocation extends AsyncTask<Void,Void,Void>
    {
        protected void onPreExecute() {

        }
        protected Void doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            try {
                JSONObject RESPONSE;
                String Url = IP+API_LOCATION;
                URL urlToRequest = new URL(Url);
                urlConnection = (HttpURLConnection)
                        urlToRequest.openConnection();
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(5000);

                statuscode_member = urlConnection.getResponseCode();
                InputStream in = new BufferedInputStream(
                            urlConnection.getInputStream());
                RESPONSE = new JSONObject(getResponseText(in));
                for(int i=0; i<RESPONSE.getJSONArray("shop").length();i++) {
                     SHOPNAME = RESPONSE.getJSONArray("shop").getJSONObject(i).getJSONArray("data").getJSONObject(0).getString("name");
                    LATI1 = RESPONSE.getJSONArray("shop").getJSONObject(i).getJSONArray("data").getJSONObject(0).getString("latitude1");
                    LONGI1 = RESPONSE.getJSONArray("shop").getJSONObject(i).getJSONArray("data").getJSONObject(0).getString("longitude1");
                    LATI2 = RESPONSE.getJSONArray("shop").getJSONObject(i).getJSONArray("data").getJSONObject(0).getString("latitude2");
                    LONGI2 = RESPONSE.getJSONArray("shop").getJSONObject(i).getJSONArray("data").getJSONObject(0).getString("longitude2");
                    ContentValues new_data = new ContentValues();
                    new_data.put(com.example.amanzoor.smart_kicthen.provider_location.BasicData.NAMING, SHOPNAME);
                    new_data.put(com.example.amanzoor.smart_kicthen.provider_location.BasicData.LATITUDE1, LATI1);
                    new_data.put(com.example.amanzoor.smart_kicthen.provider_location.BasicData.LONGITUDE1, LONGI1);
                    new_data.put(com.example.amanzoor.smart_kicthen.provider_location.BasicData.LATITUDE2, LATI2);
                    new_data.put(com.example.amanzoor.smart_kicthen.provider_location.BasicData.LONGITUDE2, LONGI2);
                    getContentResolver().insert(com.example.amanzoor.smart_kicthen.provider_location.BasicData.CONTENT_URI, new_data);
                }


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
            if (statuscode_member == 500) {
                Toast.makeText(getBaseContext(),"LOGIN UNSUCCESSFUL", Toast.LENGTH_LONG).show();
            } else {
                MyDownloadTask myDownloadTask = new MyDownloadTask();
                myDownloadTask.execute(values);
            }
        }

    }
}
