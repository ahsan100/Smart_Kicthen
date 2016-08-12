package com.example.amanzoor.smart_kicthen;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
 * Created by ahsanmanzoor on 23/04/16.
 */
public class new_group extends AppCompatActivity {

    public String IP;
    public static String API = "/api/manage_group/";
    public EditText GROUPNAME;
    String LINK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_group);
        GROUPNAME = (EditText) findViewById(R.id.edit_group);
        IP = getIntent().getStringExtra("IP");
    }

    public void saveBtn (View view){
        new MyDownloadTask().execute(GROUPNAME.getText().toString());

    }

    public class MyDownloadTask extends AsyncTask<String,Void,Void>
    {
        protected void onPreExecute() {
        }
        protected Void doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            try {
                String Url = IP+API+params[0];
                URL urlToRequest = new URL(Url);
                urlConnection = (HttpURLConnection)
                        urlToRequest.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
//                urlConnection.setRequestProperty("Content-Type",
//                        "application/vnd.collection+json");
                urlConnection.connect();

//                Map<String, String> comment = new HashMap<String, String>();
//                comment.put("groupname", params[0]);
//                String json = new GsonBuilder().create().toJson(comment, Map.class);
//
//                OutputStream os = urlConnection.getOutputStream();
//                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
//                writer.write(json);
//                writer.close();
//                os.close();

                int statusCode = urlConnection.getResponseCode();
                LINK = urlConnection.getHeaderField("location");

            } catch (MalformedURLException e) {
                // URL is invalid
            } catch (SocketTimeoutException e) {
                System.out.println("System is Busy, Try Again");
            } catch (IOException e) {
                System.out.println("ERROR3");
            }finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }
        protected void onPostExecute(Void result) {
            Toast.makeText(getBaseContext(), "ACCOUNT CREATED", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            intent.putExtra("groupid", LINK.substring(LINK.length() - 2, LINK.length()));
            startActivity(intent);
        }
    }
    private static String getResponseText(InputStream inStream) {
        return new Scanner(inStream).useDelimiter("\\A").next();
    }
}
