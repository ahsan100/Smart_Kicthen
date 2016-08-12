package com.example.amanzoor.smart_kicthen;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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
 * Created by ahsanmanzoor on 27/04/16.
 */
public class memberdetails extends AppCompatActivity {

    public String IP, MEMBERID,GROUPID;
    public String API = "/api/member/";
    protected TextView NAME,NUMBER, DATE,EMAIL, GENDER;
    JSONObject RESPONSE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memberdetails);
        EMAIL = (TextView)findViewById(R.id.memberdetail_email);
        NAME = (TextView)findViewById(R.id.memberdetail_name);
        NUMBER = (TextView)findViewById(R.id.memberdetail_phone);
        DATE = (TextView)findViewById(R.id.memberdetail_dob);
        GENDER = (TextView)findViewById(R.id.memberdetail_gender);
        IP = getIntent().getStringExtra("IP");
        MEMBERID = getIntent().getStringExtra("memberid");
        GROUPID = getIntent().getStringExtra("groupid");
        new MyDownloadTask().execute();
    }

    public void deleteButtonMember(View view){
            new AlertDialog.Builder(memberdetails.this)
                    .setTitle("DELETE MEMBER")
                    .setMessage("Are you sure you delete this "+  NAME.getText().toString() +"?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getBaseContext(),"MEMBER DELETED", Toast.LENGTH_LONG).show();
                            new deleteMember().execute();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getBaseContext(),"MEMBER NOT DELETED", Toast.LENGTH_LONG).show();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

    }

    public void backButtonMember(View view){
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        intent.putExtra("IP", IP);
        intent.putExtra("groupid", GROUPID);
        intent.putExtra("memberid", MEMBERID);
        startActivity(intent);
    }

    public class MyDownloadTask extends AsyncTask<Void,Void,Void>
    {
        protected void onPreExecute() {

        }
        protected Void doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            try {
                String Url = IP+API+MEMBERID;
                URL urlToRequest = new URL(Url);
                urlConnection = (HttpURLConnection)
                        urlToRequest.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(1000);
                urlConnection.setReadTimeout(1000);
                urlConnection.connect();

                int statusCode = urlConnection.getResponseCode();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                RESPONSE = new JSONObject(getResponseText(in));


            } catch (MalformedURLException e) {
                // URL is invalid
            } catch (SocketTimeoutException e) {
                System.out.println("System is Busy, Try Again");
            } catch (IOException e) {
                System.out.println("ERROR3");
            } catch (JSONException e) {
            }finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }
        protected void onPostExecute(Void result) {
            try {
                NAME.setText(RESPONSE.getJSONObject("member").getString("name"));
                NUMBER.setText(RESPONSE.getJSONObject("member").getString("phone"));
                EMAIL.setText(RESPONSE.getJSONObject("member").getString("email"));
                DATE.setText(RESPONSE.getJSONObject("member").getString("dob"));
                GENDER.setText(RESPONSE.getJSONObject("member").getString("gender"));
            }catch (JSONException e) {
            }
        }
    }
    private static String getResponseText(InputStream inStream) {
        return new Scanner(inStream).useDelimiter("\\A").next();
    }
    public class deleteMember extends AsyncTask<Void,Void,Void>
    {
        protected void onPreExecute() {

        }
        protected Void doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            try {
                String Url = IP+API+MEMBERID;
                URL urlToRequest = new URL(Url);
                urlConnection = (HttpURLConnection)
                        urlToRequest.openConnection();
                urlConnection.setRequestMethod("DELETE");
                urlConnection.setConnectTimeout(1000);
                urlConnection.setReadTimeout(1000);
                urlConnection.connect();

                int statusCode = urlConnection.getResponseCode();

            } catch (MalformedURLException e) {
                // URL is invalid
            } catch (SocketTimeoutException e) {
                System.out.println("System is Busy, Try Again");
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
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            intent.putExtra("IP", IP);
            intent.putExtra("groupid", GROUPID);
            startActivity(intent);
        }
    }

}
