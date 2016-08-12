package com.example.amanzoor.smart_kicthen;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
public class search_group extends AppCompatActivity {
    public String IP,MEMBERID;
    public String API = "/api/manage_group/";
    public String API_ADDMEMBER = "/api/group_member/";
    public EditText SERACHNAME;
    public int statusCode;
    public JSONObject RESPONSE;
    String LINK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_group);
        IP= getIntent().getStringExtra("IP");
        MEMBERID= getIntent().getStringExtra("memberid");
        SERACHNAME = (EditText) findViewById(R.id.edit_search_group);

    }
    public void  searchClickGroup(View view){
        if(SERACHNAME.getText().toString() != null){
            new searchGroupTask().execute(SERACHNAME.getText().toString());
        }
    }

    public void  searchBackGroup(View view){

    }

    public class searchGroupTask extends AsyncTask<String,Void,Void>
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
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(5000);

                statusCode = urlConnection.getResponseCode();
                if (statusCode == HttpURLConnection.HTTP_OK){
                    InputStream in = new BufferedInputStream(
                            urlConnection.getInputStream());
                    RESPONSE = new JSONObject(getResponseText(in));
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
            if (statusCode == HttpURLConnection.HTTP_NOT_FOUND){
                groupNotFound();
            }
            else if (statusCode == HttpURLConnection.HTTP_OK){
                try {
                    groupFound(RESPONSE.getJSONArray("group").getJSONObject(0).getString("href"));
                }catch (JSONException e) {
                }
            }
        }

    }
    private static String getResponseText(InputStream inStream) {
        return new Scanner(inStream).useDelimiter("\\A").next();
    }
    public void groupNotFound(){
        Toast.makeText(getBaseContext(),"GROUP NOT FOUND, PLEASE TRY AGAIN", Toast.LENGTH_LONG).show();
    }
    public void groupFound(final String uRl){
        LINK = uRl;
        Toast.makeText(getBaseContext(),"GROUP FOUND", Toast.LENGTH_LONG).show();
        new AlertDialog.Builder(search_group.this)
                .setTitle("JOIN GROUP")
                .setMessage("Are you sure you want to this " + SERACHNAME.getText().toString()+ " Group ? ")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getBaseContext(), "GROUP JOINED", Toast.LENGTH_LONG).show();
                                new addMemberTask().execute(LINK.substring("/api/group/".length(), LINK.length()));
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getBaseContext(), "GROUP NOT JOINED", Toast.LENGTH_LONG).show();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public class addMemberTask extends AsyncTask<String,Void,Void>
    {
        protected void onPreExecute() {

        }
        protected Void doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            try {
                String Url = IP+API_ADDMEMBER+params[0];
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

                statusCode = urlConnection.getResponseCode();

            } catch (MalformedURLException e) {
            } catch (SocketTimeoutException e) {
                //Toast.makeText(this,"System is Busy, Try Again", Toast.LENGTH_LONG).show();
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
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            intent.putExtra("IP", IP);
            intent.putExtra("groupid", LINK.substring("/api/group/".length(), LINK.length()));
            intent.putExtra("memberid", MEMBERID);
            startActivity(intent);
        }

    }
}
