package com.example.amanzoor.smart_kicthen;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
 * Created by ahsanmanzoor on 26/04/16.
 */
public class inventory extends AppCompatActivity {

    public String IP , GROUPID, MEMBERID;
    public String API = "/api/inventorys/";
    public String[] INVENTORYS;
    private LinearLayout CONTAINERVIEW;
    public TextView[] tVARRAY = new TextView[10];
    public ImageView[] iBTN = new ImageView[10];
    public int I =0;
    String[] LINK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventorypage);
        CONTAINERVIEW = (LinearLayout) findViewById(R.id.inventorylayout);
        IP = getIntent().getStringExtra("IP");
        GROUPID = getIntent().getStringExtra("groupid");
        MEMBERID = getIntent().getStringExtra("memberid");
        new GetInventorys().execute();
    }
    public class GetInventorys extends AsyncTask<Void,Void,Void>
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
                INVENTORYS= new String[RESPONSE.getJSONArray("inventory").length()];
                LINK = new String[RESPONSE.getJSONArray("inventory").length()];
                for(int i=0; i<RESPONSE.getJSONArray("inventory").length();i++) {
                    LINK[i] = RESPONSE.getJSONArray("inventory").getJSONObject(i).getString("href");
                    INVENTORYS[i] = RESPONSE.getJSONArray("inventory").getJSONObject(i).getJSONArray("data").getJSONObject(0).getString("value");
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
            for (int i=0; i<INVENTORYS.length;i++) {
                inflateEditRow(INVENTORYS[i] , LINK[i].substring("/api/inventory/".length()));
            }
        }
    }
    private static String getResponseText(InputStream inStream) {
        return new Scanner(inStream).useDelimiter("\\A").next();
    }


    private void inflateEditRow(String name, final String inventoryid) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.row_inventorypage, null);
        final TextView productname = (TextView) rowView.findViewById(R.id.inventory_name);
        final ImageView imgBtn = (ImageView) rowView.findViewById(R.id.inventory_arrowButton);

        tVARRAY[I] = productname;
        iBTN[I] = imgBtn;
        I++;

        if (name != null && !name.isEmpty()) {
            productname.setText(name);
        }
        imgBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), inventorydetail.class);
                intent.putExtra("IP", IP);
                intent.putExtra("inventoryid", inventoryid);
                intent.putExtra("groupid", GROUPID);
                intent.putExtra("memberid", MEMBERID);
                startActivity(intent);

            }
        });

        CONTAINERVIEW.addView(rowView, CONTAINERVIEW.getChildCount());
    }
    public void addNewProduct(View view){
        Intent intent = new Intent(getBaseContext(), new_inventory.class);
        intent.putExtra("IP", IP);
        intent.putExtra("groupid", GROUPID);
        intent.putExtra("memberid", MEMBERID);
        startActivity(intent);

    }

}
