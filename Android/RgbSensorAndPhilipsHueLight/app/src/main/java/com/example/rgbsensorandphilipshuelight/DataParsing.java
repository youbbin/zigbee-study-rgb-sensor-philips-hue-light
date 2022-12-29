package com.example.rgbsensorandphilipshuelight;

import org.json.JSONException;
import org.json.JSONObject;

public class DataParsing {
    public String[] getParsedData(String result){
        String[] data = new String[5];
        try {
            JSONObject jsonObject=new JSONObject(result);
            String r = jsonObject.getString("r");
            String g = jsonObject.getString("g");
            String b = jsonObject.getString("b");
            String cct = jsonObject.getString("cct");
            String illum = jsonObject.getString("illum");

            data[0] = r;
            data[1] = g;
            data[2] = b;
            data[3] = cct;
            data[4] = illum;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }
}
