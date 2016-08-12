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
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
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
 * Created by ahsanmanzoor on 25/04/16.
 */
public class recipe extends AppCompatActivity {

    public static String API = "/api/recipes/";
    public String IP , GROUPID, MEMBERID;
    private LinearLayout CONTAINERVIEW;
    public TextView[] tVARRAY = new TextView[10];
    public ImageView[] iBTN = new ImageView[10];
    public int I =0;
    public String[] recipeValue, recipeLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipepage);
        CONTAINERVIEW = (LinearLayout) findViewById(R.id.recipelayout);
        IP = getIntent().getStringExtra("IP");
        GROUPID = getIntent().getStringExtra("groupid");
        MEMBERID = getIntent().getStringExtra("memberid");
        new GetRecipe().execute();
    }

    public void newRecipe(View view){
        Intent intent = new Intent(getBaseContext(), newrecipe.class);
        intent.putExtra("IP", IP);
        intent.putExtra("groupid", GROUPID);
        intent.putExtra("memberid", MEMBERID);
        startActivity(intent);
    }

    public class GetRecipe extends AsyncTask<Void,Void,Void>
    {
        protected void onPreExecute() {
            //display progress dialog.

        }
        protected Void doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            try {
                String serviceUrl = IP+API+GROUPID;
                System.out.println(serviceUrl);
                URL urlToRequest = new URL(serviceUrl);
                urlConnection = (HttpURLConnection)
                        urlToRequest.openConnection();
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(5000);


                int statusCode = urlConnection.getResponseCode();
                if (statusCode != HttpURLConnection.HTTP_OK) {
                    System.out.println("ERROR");
                }

                InputStream in = new BufferedInputStream(
                        urlConnection.getInputStream());
                JSONObject jsonObject = new JSONObject(getResponseText(in));
                try{
                    JSONArray recipes = jsonObject.getJSONArray("recipes");
                    recipeValue = new String[recipes.length()];
                    recipeLink = new String[recipes.length()];
                    for (int i=0; i < recipes.length(); i++)
                    {
                        recipeLink[i]=recipes.getJSONObject(i).getString("href");
                        recipeValue[i]=recipes.getJSONObject(i).getJSONArray("data").getJSONObject(0).getString("value");
                    }

                }catch (JSONException e) {
                    System.out.println("ERROR" +e);
                }

            } catch (MalformedURLException e) {
                // URL is invalid
            } catch (SocketTimeoutException e) {
                // data retrieval or connection timed out
            } catch (IOException e) {
                // could not read response body
                // (could not create input stream)
            } catch (JSONException e) {
                // response body is no valid JSON string
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }
        protected void onPostExecute(Void result) {
            for (int i=0; i<recipeValue.length;i++) {
                inflateEditRow(recipeValue[i], recipeLink[i].substring("/api/recipe/".length()));
            }

        }
    }
    private static String getResponseText(InputStream inStream) {
        return new Scanner(inStream).useDelimiter("\\A").next();
    }

    private void inflateEditRow(String name,final String recipeid) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.row_recipe, null);
        final TextView recipename = (TextView) rowView.findViewById(R.id.recipe_name);
        final ImageView imgBtn = (ImageView) rowView.findViewById(R.id.recipe_arrowButton);

        tVARRAY[I] = recipename;
        iBTN[I] = imgBtn;
        I++;

        if (name != null && !name.isEmpty()) {
            recipename.setText(name);
        }
        imgBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), recipedetail.class);
                intent.putExtra("IP", IP);
                intent.putExtra("recipeid", recipeid);
                intent.putExtra("groupid", GROUPID);
                intent.putExtra("memberid", MEMBERID);
                startActivity(intent);

            }
        });

        CONTAINERVIEW.addView(rowView, CONTAINERVIEW.getChildCount());
    }
}
