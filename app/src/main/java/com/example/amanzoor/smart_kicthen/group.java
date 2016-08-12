package com.example.amanzoor.smart_kicthen;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

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

/**
 * Created by ahsanmanzoor on 22/04/16.
 */
public class group extends AppCompatActivity {
    public String IP, GROUPID, MEMBERID;
    public static String API = "/api/group/";
    String GROUP_NAME;
    public TextView GROUPNAME;
    public LinearLayout NAME_LAYOUT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grouppage);
        GROUPNAME = (TextView) findViewById(R.id.group_change);
        NAME_LAYOUT = (LinearLayout) findViewById(R.id.groupname_layout);
        IP = getIntent().getStringExtra("IP");
        MEMBERID = getIntent().getStringExtra("memberid");
        if(getIntent().getStringExtra("groupid") != null){
            GROUPID = getIntent().getStringExtra("groupid");
            new GetGroupMembers().execute(GROUPID);
        }
        else{
            NAME_LAYOUT.setVisibility(View.INVISIBLE);
        }
    }


    public void NewGroupButton (View view){
        Intent intent = new Intent(getBaseContext(), new_group.class);
        intent.putExtra("IP", IP);
        intent.putExtra("memberid", MEMBERID);
        startActivity(intent);
    }

    public void JoinButton (View view){
        Intent intent = new Intent(getBaseContext(), search_group.class);
        intent.putExtra("IP", IP);
        intent.putExtra("memberid", MEMBERID);
        startActivity(intent);
    }

    public void imageButton(View view){
        Intent intent = new Intent(getBaseContext(), member.class);
        intent.putExtra("IP",IP);
        intent.putExtra("groupid", GROUPID);
        startActivity(intent);
    }
    public class GetGroupMembers extends AsyncTask<String,Void,Void>
    {
        protected void onPreExecute() {

        }
        protected Void doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            try {
                JSONObject RESPONSE;
                String Url = IP+API+params[0];
                URL urlToRequest = new URL(Url);
                urlConnection = (HttpURLConnection)
                        urlToRequest.openConnection();
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(5000);

                int statusCode = urlConnection.getResponseCode();
                if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                } else if (statusCode != HttpURLConnection.HTTP_OK) {
                    System.out.println("PROBLEM IN SERVER");
                }else if (statusCode == HttpURLConnection.HTTP_OK){
                    System.out.println("GOT");
                }

                InputStream in = new BufferedInputStream(
                        urlConnection.getInputStream());
                RESPONSE = new JSONObject(getResponseText(in));
                GROUP_NAME= RESPONSE.getJSONArray("group").getJSONObject(0).getJSONArray("data").getJSONObject(0).getString("value");

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
            GROUPNAME.setText(GROUP_NAME);
        }
    }
    private static String getResponseText(InputStream inStream) {
        return new Scanner(inStream).useDelimiter("\\A").next();
    }

}
