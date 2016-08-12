package com.example.amanzoor.smart_kicthen;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
 * Created by ahsanmanzoor on 25/04/16.
 */
public class list_product extends AppCompatActivity {

    public String IP , GROUPID, MEMBERID;
    public String API = "/api/list_product/";
    public String API_FLAG = "/api/monitor_member/";
    public String[] LIST_PRODUCT;
    private LinearLayout CONTAINERVIEW;
    public TextView[] tVARRAY = new TextView[10];
    public ImageView[] iBTN = new ImageView[10];
    public int I =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listpage);
        CONTAINERVIEW = (LinearLayout) findViewById(R.id.listproductlayout);
        IP = getIntent().getStringExtra("IP");
        GROUPID = getIntent().getStringExtra("groupid");
        MEMBERID = getIntent().getStringExtra("memberid");
        new GetProduct().execute();
    }

    public void sendbuttonList(View view){
        new changeflag().execute();
    }
    public class GetProduct extends AsyncTask<Void,Void,Void>
    {
        protected void onPreExecute() {

        }
        protected Void doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            try {
                JSONObject RESPONSE;
                String Url = IP+API+GROUPID;
                URL urlToRequest = new URL(Url);
                urlConnection = (HttpURLConnection)
                        urlToRequest.openConnection();
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(5000);

                int statusCode = urlConnection.getResponseCode();

                InputStream in = new BufferedInputStream(
                        urlConnection.getInputStream());
                RESPONSE = new JSONObject(getResponseText(in));
                LIST_PRODUCT= new String[RESPONSE.getJSONArray("listproduct").length()];
                for(int i=0; i<RESPONSE.getJSONArray("listproduct").length();i++) {
                    LIST_PRODUCT[i] = RESPONSE.getJSONArray("listproduct").getJSONObject(i).getJSONArray("data").getJSONObject(0).getString("value");
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
            if(LIST_PRODUCT != null) {
                for (int i = 0; i < LIST_PRODUCT.length; i++) {
                    inflateEditRow(LIST_PRODUCT[i]);
                }
            }
        }
    }
    private static String getResponseText(InputStream inStream) {
        return new Scanner(inStream).useDelimiter("\\A").next();
    }


    private void inflateEditRow(String name) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.row_listpage, null);
        final TextView productname = (TextView) rowView.findViewById(R.id.product_name);
        final ImageView imgBtn = (ImageView) rowView.findViewById(R.id.product_arrowButton);

        tVARRAY[I] = productname;
        iBTN[I] = imgBtn;
        I++;

        if (name != null && !name.isEmpty()) {
            productname.setText(name);
        }
        imgBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                //System.out.println("AHSANFUCK" + productname.getText());

            }
        });

        CONTAINERVIEW.addView(rowView, CONTAINERVIEW.getChildCount());
    }


    public class changeflag extends AsyncTask<Void,Void,Void>
    {
        protected void onPreExecute() {
        }
        protected Void doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            try {
                String Url = IP+API_FLAG+GROUPID;
                URL urlToRequest = new URL(Url);
                urlConnection = (HttpURLConnection)
                        urlToRequest.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("PUT");
                urlConnection.setRequestProperty("Content-Type",
                        "application/vnd.collection+json");
                urlConnection.connect();
                //System.out.println("HERE" + Url + params[0] + params[1]);
                int statusCode = urlConnection.getResponseCode();

            } catch (MalformedURLException e) {
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
            Toast.makeText(getBaseContext(), "LIST SENT", Toast.LENGTH_LONG).show();
        }
    }

}

