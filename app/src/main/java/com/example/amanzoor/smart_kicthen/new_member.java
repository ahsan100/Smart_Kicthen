package com.example.amanzoor.smart_kicthen;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.GsonBuilder;

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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by ahsanmanzoor on 23/04/16.
 */
public class new_member extends AppCompatActivity {

    public static String IP ;
    public static String API = "/api/new_users/";
    protected EditText NAME,NUMBER, DATE, MONTH, YEAR,EMAIL, PASSWORD;
    protected Spinner GENDER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_member);
        EMAIL = (EditText)findViewById(R.id.edit_email);
        PASSWORD = (EditText)findViewById(R.id.edit_password);
        NAME = (EditText)findViewById(R.id.edit_membername);
        NUMBER = (EditText)findViewById(R.id.editphone);
        DATE = (EditText)findViewById(R.id.edit_day);
        MONTH = (EditText)findViewById(R.id.edit_month);
        YEAR = (EditText)findViewById(R.id.edit_year);
        GENDER = (Spinner)findViewById(R.id.spinner_gender);
        IP = getIntent().getStringExtra("IP");
    }

    public void saveButton(View view){
        String DOB = DATE.getText().toString() + MONTH.getText().toString() + YEAR.getText().toString();
        String[] values = {NAME.getText().toString(), NUMBER.getText().toString(), GENDER.getSelectedItem().toString(), DOB,
                EMAIL.getText().toString(), PASSWORD.getText().toString()};
        new MyDownloadTask().execute(values);
    }

    public class MyDownloadTask extends AsyncTask<String,Void,Void>
    {
        protected void onPreExecute() {
            //display progress dialog.

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
                comment.put("name", params[0]);
                comment.put("phone", params[1]);
                comment.put("gender", params[2]);
                comment.put("dob", params[3]);
                comment.put("email", params[4]);
                comment.put("password", params[5]);
                String json = new GsonBuilder().create().toJson(comment, Map.class);

                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(json);
                writer.close();
                os.close();

                int statusCode = urlConnection.getResponseCode();

                InputStream in = new BufferedInputStream(
                        urlConnection.getInputStream());

            } catch (MalformedURLException e) {
                // URL is invalid
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
            Toast.makeText(getBaseContext(), "ACCOUNT CREATED", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getBaseContext(), login.class);
            startActivity(intent);
        }
    }
    private static String getResponseText(InputStream inStream) {
        return new Scanner(inStream).useDelimiter("\\A").next();
    }
}

