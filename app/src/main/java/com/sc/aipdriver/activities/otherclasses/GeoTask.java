package com.sc.aipdriver.activities.otherclasses;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SRIVASTAVA on 1/9/2016.
 */
/*The instance of this class is called by "MainActivty",to get the time taken reach the destination from Google Distance Matrix API in background.
  This class contains interface "Geo" to call the function setDouble(String) defined in "MainActivity.class" to display the result.*/
public class GeoTask extends AsyncTask<String, Void, List<String>> {
    ProgressDialog pd;
    Context mContext;
    Double duration;
    List<String> distanceList = new ArrayList<>();
    Geo geo1;
    String listAction = "";
    String action = "";
    //constructor is used to get the context.
    public GeoTask(Context mContext, String action) {
        this.mContext = mContext;
        geo1= (Geo) mContext;
        this.action = action;
    }

    //This function is executed before before "doInBackground(String...params)" is executed to dispaly the progress dialog
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd = new ProgressDialog(mContext);
        if(action.equalsIgnoreCase("set")){
            pd.setMessage("Loading");
            pd.setCancelable(false);
            pd.show();
        }
    }

    //This function is executed after the execution of "doInBackground(String...params)" to dismiss the dispalyed progress dialog and call "setDouble(Double)" defined in "MainActivity.java"
    @Override
    protected void onPostExecute(List<String> stringList) {
        try {
            super.onPostExecute(stringList);
            if (stringList.size() != 0) {
                if (listAction.equalsIgnoreCase("set"))
                    geo1.setDistanceTime(stringList);
                else if (listAction.equalsIgnoreCase("update"))
                    geo1.updateDistanceTime(stringList);
                pd.dismiss();
            } else
                Toast.makeText(mContext, "Error4!Please Try Again wiht proper values", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected List<String> doInBackground(String... params) {
        try {
            URL url = new URL(params[0]);
            listAction = params[1].trim();
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.connect();
            int statuscode = con.getResponseCode();
            if (statuscode == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();
                while (line != null) {
                    sb.append(line);
                    line = br.readLine();
                }
                String json = sb.toString();
                Log.d("JSON", json);
                JSONObject root = new JSONObject(json);
                JSONArray array_rows = root.getJSONArray("rows");
                Log.d("JSON", "array_rows:" + array_rows);
//                JSONObject object_rows = array_rows.getJSONObject(0);

                int previous_duration = 0;
                int previous_distance = 0;
                //double miles = 0.0;

                for(int n = 0; n < array_rows.length(); n++){
                    JSONObject object_rows = array_rows.getJSONObject(n);

                    Log.d("JSON", "object_rows:" + object_rows);
                    JSONArray array_elements = object_rows.getJSONArray("elements");

                    for(int i = 0; i < array_elements.length(); i++){

                        if(n == i){
                            Log.d("JSON", "array_elements:" + array_elements);
                            JSONObject object_elements = array_elements.getJSONObject(i);
                            Log.d("JSON", "object_elements:" + object_elements);
                            JSONObject object_duration = object_elements.getJSONObject("duration");
                            JSONObject object_distance = object_elements.getJSONObject("distance");

                            if(n == 0){
                                distanceList.add(object_duration.getString("text") + "," + object_distance.getString("text"));
                                previous_duration = Integer.parseInt(object_duration.getString("value"));
                                previous_distance = Integer.parseInt(object_distance.getString("value"));

                                //miles = Double.parseDouble(object_distance.getString("text").split(" ")[0]);
                            }
                            else{
                                previous_duration = previous_duration + Integer.parseInt(object_duration.getString("value"));
                                previous_distance = previous_distance + Integer.parseInt(object_distance.getString("value"));
                               // miles = miles + Double.parseDouble(object_distance.getString("text").split(" ")[0]);
                                int seconds = previous_duration;
                                int p1 = seconds % 60;
                                int p2 = seconds / 60;
                                int p3 = p2 % 60;
                                p2 = p2 / 60;

                                int recv_seconds = p1;
                                String timeFormat = "";

                                if(p2 > 0)
                                    timeFormat += p2+" hours";
                                if(p3 > 0){
                                    if(recv_seconds >= 30)
                                        p3 = p3+1;

                                    timeFormat += " "+p3+" mins";
                                }
                                else{
                                    if(recv_seconds >= 30)
                                        timeFormat += " 1 mins";
                                }

                                int meter = previous_distance;
                                double miles = meter * 0.00062137119;
                                miles = Double.parseDouble(new DecimalFormat("##.#").format(miles));

                                distanceList.add(timeFormat + "," + miles+" mi");

                            }
                        }
                    }
                }





//                String json = sb.toString();
//                Log.d("JSON", json);
//                JSONObject root = new JSONObject(json);
//                JSONArray array_rows = root.getJSONArray("rows");
//                Log.d("JSON", "array_rows:" + array_rows);
//                JSONObject object_rows = array_rows.getJSONObject(0);
//                Log.d("JSON", "object_rows:" + object_rows);
//                JSONArray array_elements = object_rows.getJSONArray("elements");
//
//                for(int i = 0; i < array_elements.length(); i++){
//                    Log.d("JSON", "array_elements:" + array_elements);
//                    JSONObject object_elements = array_elements.getJSONObject(i);
//                    Log.d("JSON", "object_elements:" + object_elements);
//                    JSONObject object_duration = object_elements.getJSONObject("duration");
//                    JSONObject object_distance = object_elements.getJSONObject("distance");
//
//
//                    distanceList.add(object_duration.getString("text") + "," + object_distance.getString("text"));
//                    System.out.println("eueeuweuwueiwueiieueu " + object_distance + " -- " + object_duration);
//
//                }



//                Log.d("JSON", "array_elements:" + array_elements);
//                JSONObject object_elements = array_elements.getJSONObject(0);
//                Log.d("JSON", "object_elements:" + object_elements);
//                JSONObject object_duration = object_elements.getJSONObject("duration");
//                JSONObject object_distance = object_elements.getJSONObject("distance");
//
//
//                Log.d("JSON", "object_duration:" + object_duration);
                return distanceList;
//                return object_duration.getString("text") + "," + object_distance.getString("text");

            }
        } catch (MalformedURLException e) {
            Log.d("error", "error1");
        } catch (IOException e) {
            Log.d("error", "error2");
        } catch (JSONException e) {
            Log.d("error", "error3");
        }
        return null;
    }
    public interface Geo{
        public void setDistanceTime(List<String> min);
        public void updateDistanceTime(List<String> min);

    }

}

