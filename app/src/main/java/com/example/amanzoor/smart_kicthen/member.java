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
 * Created by ahsanmanzoor on 23/04/16.
 */
public class member extends AppCompatActivity {

    public String IP , GROUPID;
    public String API = "/api/group_member/";
    public String[] GROUP_MEMBERS;
    public String[] LINK;
    private LinearLayout CONTAINERVIEW;
    public TextView[] tVARRAY = new TextView[10];
    public ImageView[] iBTN = new ImageView[10];
    public int I =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memberpage);
        CONTAINERVIEW = (LinearLayout) findViewById(R.id.groupmemberlayout);
        IP = getIntent().getStringExtra("IP");
        GROUPID = getIntent().getStringExtra("groupid");
        new GetMembers().execute();
    }
    public class GetMembers extends AsyncTask<Void,Void,Void>
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
                if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                } else if (statusCode != HttpURLConnection.HTTP_OK) {
                    System.out.println("PROBLEM IN SERVER");
                }else if (statusCode == HttpURLConnection.HTTP_OK){
                    System.out.println("GOT");
                }

                InputStream in = new BufferedInputStream(
                        urlConnection.getInputStream());
                RESPONSE = new JSONObject(getResponseText(in));
                GROUP_MEMBERS= new String[RESPONSE.getJSONArray("group").length()];
                LINK = new String[RESPONSE.getJSONArray("group").length()];
                for(int i=0; i<RESPONSE.getJSONArray("group").length();i++) {
                    LINK[i] = RESPONSE.getJSONArray("group").getJSONObject(i).getString("href");
                    GROUP_MEMBERS[i] = RESPONSE.getJSONArray("group").getJSONObject(i).getJSONArray("data").getJSONObject(0).getString("value");
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
            for (int i=0; i<GROUP_MEMBERS.length;i++) {
                inflateEditRow(GROUP_MEMBERS[i], LINK[i].substring("/api/member/".length()));
            }
        }
    }
    private static String getResponseText(InputStream inStream) {
        return new Scanner(inStream).useDelimiter("\\A").next();
    }


    private void inflateEditRow(String name, final String memberid) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.row_memberpage, null);
        final TextView membername = (TextView) rowView.findViewById(R.id.member_name);
        final ImageView imgBtn = (ImageView) rowView.findViewById(R.id.arrowButton);
        tVARRAY[I] = membername;
        iBTN[I] = imgBtn;

        if (name != null && !name.isEmpty()) {
            membername.setText(name);
        }
        imgBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), memberdetails.class);
                intent.putExtra("IP", IP);
                intent.putExtra("memberid", memberid);
                intent.putExtra("groupid", GROUPID);
                startActivity(intent);
            }
        });
        I++;
        CONTAINERVIEW.addView(rowView, CONTAINERVIEW.getChildCount());
    }
}

