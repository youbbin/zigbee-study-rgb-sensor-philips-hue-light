package com.example.rgbsensorandphilipshuelight;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpTask extends AsyncTask {
    URL url;
    String userip = "192.168.100.61";
    String username = "U4DRfDFVCUJ9OBkqEcXInghKJRFNACfWfoLEy6RT";

    JSONObject json;
    @Override
    protected Object doInBackground(Object[] objects) {
        OutputStream outputStream = null;

        int CONN_TIMEOUT = 5;
        int READ_TIMEOUT = 5;
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(CONN_TIMEOUT * 1000);
            conn.setReadTimeout(READ_TIMEOUT * 1000);
            conn.setRequestProperty("Cache-Control", "no-cache");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestMethod("PUT");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            outputStream = conn.getOutputStream();
            outputStream.write(json.toString().getBytes());
            outputStream.flush();
            outputStream.close();

            int responseCode = conn.getResponseCode();
            System.out.println("url-response Code : "+responseCode);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setLight(String str){
        String string ="";
        if(str.equals("circle"))
            string = "/lights/1/state";
        else if(str.equals("strip"))
            string = "/lights/2/state";
        else if(str.equals("bulb"))
            string = "/lights/3/state";
        else if(str.equals("all"))
            string = "/groups/0/action";
        try {
            url = new URL("http://"+userip+"/api/"+username+string);
            Log.d("url",url.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void setOff(){
        json = new JSONObject();
        try {
            json.put("on",false);
            Log.d("url-json",json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void setOn(){
        json = new JSONObject();
        try {
            json.put("on",true);
            Log.d("url-json",json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setColor(int brt, double x, double y){
        json = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(x);
            jsonArray.put(y);
            json.put("bri", brt);
            json.put("xy",jsonArray);
            Log.d("url-json",json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void setCct(int brt, int cct){
        double m = -0.0771 * cct + 654.22;
        json = new JSONObject();
        try {
            json.put("bri", brt);
            json.put("ct",Math.round(m));
            Log.d("url-json",json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
