package com.example.amanzoor.smart_kicthen;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
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
 * Created by ahsanmanzoor on 27/04/16.
 */
public class recipedetail extends AppCompatActivity {
    public String IP, RECIPEID,GROUPID, MEMBERID;
    public String API = "/api/recipe/";
    public String API_PEOPLE = "/api/recipe_add/";
    protected TextView NAME,DESCRIPTION, TIME;
    JSONObject RESPONSE;
    String PEOPLE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipedetail);
        DESCRIPTION = (TextView)findViewById(R.id.recipedetail_discription);
        NAME = (TextView)findViewById(R.id.recipedetail_name);
        TIME = (TextView)findViewById(R.id.recipedetail_time);
        IP = getIntent().getStringExtra("IP");
        MEMBERID = getIntent().getStringExtra("memberid");
        RECIPEID = getIntent().getStringExtra("recipeid");
        GROUPID = getIntent().getStringExtra("groupid");
        new MyDownloadTask().execute();
    }

    public void deleteButtonrecipe(View view){
        new AlertDialog.Builder(recipedetail.this)
                .setTitle("DELETE RECIPE")
                .setMessage("Are you sure you delete this "+  NAME.getText().toString() +"?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getBaseContext(), "RECIPE DELETED", Toast.LENGTH_LONG).show();
                        new deleteRecipe().execute();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getBaseContext(),"RECIPE NOT DELETED", Toast.LENGTH_LONG).show();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

    public void makeButtonrecipe(View view){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(recipedetail.this);
        alertDialog.setTitle("MAKE RECIPE");
        alertDialog.setMessage("Enter Number of People ");

        final EditText input = new EditText(recipedetail.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                         PEOPLE = input.getText().toString();
                        new makeRecipeTask().execute(PEOPLE);
                    }
                });

        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }


    public void backButtonrecipe(View view){
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
                String Url = IP+API+RECIPEID;
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
                NAME.setText(RESPONSE.getJSONObject("recipe").getString("name"));
                TIME.setText(RESPONSE.getJSONObject("recipe").getString("preparation_time"));
                DESCRIPTION.setText(RESPONSE.getJSONObject("recipe").getString("details"));
            }catch (JSONException e) {
            }
        }
    }
    private static String getResponseText(InputStream inStream) {
        return new Scanner(inStream).useDelimiter("\\A").next();
    }
    public class deleteRecipe extends AsyncTask<Void,Void,Void>
    {
        protected void onPreExecute() {

        }
        protected Void doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            try {
                String Url = IP+API+RECIPEID;
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
                Toast.makeText(getBaseContext(), "System is Busy, Try Again", Toast.LENGTH_LONG).show();
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


    public class makeRecipeTask extends AsyncTask<String,Void,Void>
    {
        protected void onPreExecute() {
        }
        protected Void doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            try {
                String Url = IP+API_PEOPLE+RECIPEID;
                URL urlToRequest = new URL(Url);
                urlConnection = (HttpURLConnection)
                        urlToRequest.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("PUT");
                urlConnection.setRequestProperty("Content-Type",
                        "application/vnd.collection+json");
                urlConnection.connect();

                Map<String, String> comment = new HashMap<String, String>();
                comment.put("people", params[0]);
                String json = new GsonBuilder().create().toJson(comment, Map.class);

                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(json);
                writer.close();
                os.close();

                int statusCode = urlConnection.getResponseCode();


            } catch (MalformedURLException e) {
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
            Toast.makeText(getBaseContext(), "INVENTORY UPDATED", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            intent.putExtra("IP", IP);
            intent.putExtra("groupid", GROUPID);
            intent.putExtra("memberid", MEMBERID);
            startActivity(intent);
        }
    }
}
