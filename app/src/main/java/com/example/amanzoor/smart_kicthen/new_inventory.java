package com.example.amanzoor.smart_kicthen;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by ahsanmanzoor on 27/04/16.
 */
public class new_inventory extends AppCompatActivity {

    public  String IP , GROUPID, MEMBERID;
    EditText NAME, DETAIL,QUANTITY, THRESHOLD,UNIT;
    public  String API_INVENTORY = "/api/inventorys/";
    public  String API_LISTPRODUCT = "/api/list_product/";
    public int statusCode;
    public String LINK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_inventory);
        NAME = (EditText) findViewById(R.id.inventoryname_editText);
        DETAIL = (EditText) findViewById(R.id.inventorydetail_editText);
        QUANTITY = (EditText) findViewById(R.id.inventoryquantity_editText);
        THRESHOLD = (EditText) findViewById(R.id.inventorythreshold_editText);
        UNIT = (EditText) findViewById(R.id.inventoryunit_editText);
        IP = getIntent().getStringExtra("IP");
        GROUPID = getIntent().getStringExtra("groupid");
        MEMBERID = getIntent().getStringExtra("memberid");
    }

    public void saveinventory(View view) {
        Map<String, String> comment1 = new HashMap<String, String>();
        comment1.put("name", NAME.getText().toString());
        comment1.put("description", DETAIL.getText().toString());
        comment1.put("quantity", QUANTITY.getText().toString());
        comment1.put("threshold", THRESHOLD.getText().toString());
        comment1.put("unit", UNIT.getText().toString());
        String json = new GsonBuilder().create().toJson(comment1, Map.class);
        new PostInventory().execute(json);

    }

    public void backinventory(View view) {
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        intent.putExtra("IP", IP);
        intent.putExtra("groupid", GROUPID);
        intent.putExtra("memberid", MEMBERID);
        startActivity(intent);

    }
    public class PostInventory extends AsyncTask<String,Void,Void>
    {
        protected void onPreExecute() {

        }
        protected Void doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            try {
                // create connection
                String Url = IP+API_INVENTORY+GROUPID;
                URL urlToRequest = new URL(Url);
                urlConnection = (HttpURLConnection)
                        urlToRequest.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type",
                        "application/vnd.collection+json");
                urlConnection.connect();

                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(params[0]);
                writer.close();
                os.close();

                statusCode = urlConnection.getResponseCode();
                LINK = urlConnection.getHeaderField("location");

                InputStream in = new BufferedInputStream(
                        urlConnection.getInputStream());

            } catch (MalformedURLException e) {

            } catch (SocketTimeoutException e) {
                //Toast.makeText(this,"System is Busy, Try Again", Toast.LENGTH_LONG).show();
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
            if (statusCode == 201){
                Toast.makeText(getBaseContext(), "Inventory Saved", Toast.LENGTH_LONG).show();
                if(Integer.parseInt(QUANTITY.getText().toString()) <= Integer.parseInt(THRESHOLD.getText().toString())) {
                    new PostList_product().execute(LINK.substring(IP.length()+"/api/inventory/".length()));
                }
                else{
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    intent.putExtra("IP", IP);
                    intent.putExtra("groupid", GROUPID);
                    intent.putExtra("memberid", MEMBERID);
                    startActivity(intent);
                }
            }
        }
    }


    private static String getResponseText(InputStream inStream) {
        return new Scanner(inStream).useDelimiter("\\A").next();
    }


    public class PostList_product extends AsyncTask<String,Void,Void> {
        protected void onPreExecute() {

        }

        protected Void doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            try {
                // create connection
                String Url = IP + API_LISTPRODUCT + GROUPID;
                URL urlToRequest = new URL(Url);
                urlConnection = (HttpURLConnection)
                        urlToRequest.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type",
                        "application/vnd.collection+json");
                urlConnection.connect();

                Map<String, String> comment1 = new HashMap<String, String>();
                comment1.put("inventoryid", params[0]);
                String json = new GsonBuilder().create().toJson(comment1, Map.class);

                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(json);
                writer.close();
                os.close();

                statusCode = urlConnection.getResponseCode();

                InputStream in = new BufferedInputStream(
                        urlConnection.getInputStream());

            } catch (MalformedURLException e) {

            } catch (SocketTimeoutException e) {
                //Toast.makeText(this,"System is Busy, Try Again", Toast.LENGTH_LONG).show();
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
            intent.putExtra("memberid", MEMBERID);
            startActivity(intent);
        }
    }
}
