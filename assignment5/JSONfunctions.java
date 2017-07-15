package com.example.shwetashahane.assignment5;

/**
 * Created by shwetashahane on 3/15/17.
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;

import android.util.Log;

public class JSONfunctions {

    public static JSONArray getJSONfromURL(String url) {
        String result = "";
        JSONArray jArray = null;
        System.out.println("countries in sdfad");
        try {
            URL urls = new URL(url);
            System.out.println("URL: " + urls);
            HttpURLConnection con = (HttpURLConnection) urls.openConnection();
            con.setRequestMethod("GET");
            con.connect();
            BufferedReader bf = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String value = bf.readLine();
            System.out.println("Response it is " + value);
            result = value;
            jArray = new JSONArray(result);
            System.out.println(jArray);
        } catch (Exception e) {
            Log.e("log_tag", "Error in http connection " + e.toString());
        }
        return jArray;
    }
}
