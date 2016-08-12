package com.example.amanzoor.smart_kicthen;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
 * Created by ahsanmanzoor on 20/04/16.
 */
public class newrecipe extends AppCompatActivity {

    public  String IP , GROUPID;
    public  String API_RECIPE = "/api/recipes/";
    public  String API_INVENTORY = "/api/inventorys/";
    public int I=-1;
    public String INVENTORY[];
    EditText eT1, eT2,eT3;
    EditText[] eTARRAY = new EditText[10];
    Spinner[] sARRAY = new Spinner[10];
    private LinearLayout mContainerView;
    private Button mAddButton;
    private View mExclusiveEmptyView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_recipe);
        eT1 = (EditText) findViewById(R.id.editText);
        eT2 = (EditText) findViewById(R.id.editText2);
        eT3 = (EditText) findViewById(R.id.editText3);
        mContainerView = (LinearLayout) findViewById(R.id.mylayout);
        mAddButton = (Button) findViewById(R.id.btnAddNewItem);
        IP = getIntent().getStringExtra("IP");
        GROUPID = getIntent().getStringExtra("groupid");
        new GetInventory().execute();
    }

    public void save(View view){
        String[] RECIPE = new String[3];
        String[][] INGREDIANTS = new String[10][2];

        RECIPE[0] = eT1.getText().toString();
        RECIPE[1] = eT2.getText().toString();
        RECIPE[2] = eT3.getText().toString();

        Map<String, String[]> comment1 = new HashMap<String, String[]>();
        comment1.put("recipe", RECIPE);

        for (int l=0,m =0; l <= I ;l++) {
            INGREDIANTS[l][1] = eTARRAY[l].getText().toString();
            INGREDIANTS[l][0] = sARRAY[l].getSelectedItem().toString();
            comment1.put("inventory" + ++m, INGREDIANTS[l]);
        }
        String json = new GsonBuilder().create().toJson(comment1, Map.class);
        new PostRecipe().execute(json);
    }

    public class GetInventory extends AsyncTask<Void,Void,Void>
    {
        protected void onPreExecute() {

        }
        protected Void doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            try {
                JSONObject RESPONSE;
                String Url = IP+API_INVENTORY+GROUPID;
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
                INVENTORY = new String[RESPONSE.getJSONArray("inventory").length()];
                for(int i=0; i<RESPONSE.getJSONArray("inventory").length();i++) {
                     INVENTORY[i] = RESPONSE.getJSONArray("inventory").getJSONObject(i).getJSONArray("data").getJSONObject(0).getString("value");
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
        }
    }

    public class PostRecipe extends AsyncTask<String,Void,Void>
    {
        protected void onPreExecute() {

        }
        protected Void doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            try {
                // create connection
                String Url = IP+API_RECIPE+GROUPID;
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

                int statusCode = urlConnection.getResponseCode();
                if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                } else if (statusCode != HttpURLConnection.HTTP_OK) {
                    System.out.println("PROBLEM IN SERVER");
                }else if (statusCode == HttpURLConnection.HTTP_OK){
                    System.out.println("SAVED");
                }

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
            Toast.makeText(getBaseContext(),"Recipe Saved", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            intent.putExtra("IP", IP);
            intent.putExtra("groupid", GROUPID);
            startActivity(intent);
        }
    }

    private static String getResponseText(InputStream inStream) {
        return new Scanner(inStream).useDelimiter("\\A").next();
    }

    public void onAddNewClicked(View v) {
        I++;
        inflateEditRow(null);
    }

    public void onDeleteClicked(View v) {
        mContainerView.removeView((View) v.getParent());
    }


    private void inflateEditRow(String name) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.row, null);
        final ImageView deleteButton = (ImageView) rowView.findViewById(R.id.buttonDelete);
        final EditText editText = (EditText) rowView.findViewById(R.id.editText);
        final Spinner spinner= (Spinner) rowView.findViewById(R.id.spinnerCategory);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, INVENTORY);
        spinner.setAdapter(adapter);

        eTARRAY[I] = editText;
        sARRAY[I] = spinner;


        if (name != null && !name.isEmpty()) {
            editText.setText(name);
        } else {
            mExclusiveEmptyView = rowView;
        }

        mContainerView.addView(rowView, mContainerView.getChildCount());
    }


}
