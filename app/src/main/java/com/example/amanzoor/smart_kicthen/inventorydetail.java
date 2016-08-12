package com.example.amanzoor.smart_kicthen;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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
import java.net.URI;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by ahsanmanzoor on 27/04/16.
 */
public class inventorydetail extends AppCompatActivity {

    public String IP, INVENTORYID,GROUPID, MEMBERID;
    public String API = "/api/inventory/";
    protected TextView NAME,DESCRIPTION, QUANTITY,THRESHOLD, UNIT;
    JSONObject RESPONSE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventorydetail);
        DESCRIPTION = (TextView)findViewById(R.id.inventorydetail_discription);
        NAME = (TextView)findViewById(R.id.inventorydetail_name);
        QUANTITY = (TextView)findViewById(R.id.inventorydetail_quantity);
        THRESHOLD = (TextView)findViewById(R.id.inventorydetail_threshold);
        UNIT = (TextView)findViewById(R.id.inventorydetail_unit);
        IP = getIntent().getStringExtra("IP");
        MEMBERID = getIntent().getStringExtra("memberid");
        INVENTORYID = getIntent().getStringExtra("inventoryid");
        GROUPID = getIntent().getStringExtra("groupid");
        new MyDownloadTask().execute();
    }

    public void deleteButtoninventory(View view){
        new AlertDialog.Builder(inventorydetail.this)
                .setTitle("DELETE INVENTORY")
                .setMessage("Are you sure you delete this "+  NAME.getText().toString() +"?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getBaseContext(), "PRODUCT DELETED", Toast.LENGTH_LONG).show();
                        new deleteInventory().execute();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getBaseContext(),"PRODUCT NOT DELETED", Toast.LENGTH_LONG).show();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

    public void backButtonInventory(View view){
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
                String Url = IP+API+INVENTORYID;
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
                Toast.makeText(getBaseContext(), "System is Busy, Try Again", Toast.LENGTH_LONG).show();
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
                NAME.setText(RESPONSE.getJSONObject("inventory").getString("name"));
                QUANTITY.setText(RESPONSE.getJSONObject("inventory").getString("quantity"));
                THRESHOLD.setText(RESPONSE.getJSONObject("inventory").getString("threshold"));
                UNIT.setText(RESPONSE.getJSONObject("inventory").getString("unit"));
                DESCRIPTION.setText(RESPONSE.getJSONObject("inventory").getString("description"));
            }catch (JSONException e) {
            }
        }
    }
    private static String getResponseText(InputStream inStream) {
        return new Scanner(inStream).useDelimiter("\\A").next();
    }
    public class deleteInventory extends AsyncTask<Void,Void,Void>
    {
        protected void onPreExecute() {

        }
        protected Void doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            try {
                String Url = IP+API+INVENTORYID;
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
            intent.putExtra("memberid", MEMBERID);
            startActivity(intent);
        }
    }

}

