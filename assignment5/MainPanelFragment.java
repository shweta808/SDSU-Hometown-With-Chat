package com.example.shwetashahane.assignment5;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by shwetashahane on 4/10/17.
 */

public class MainPanelFragment extends Fragment {

    Button nextButton;
    JSONArray jsonarray;
    ArrayList<Person> dbList;
    private ListView list;
    public static String nickname;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_list,container,false);
        nextButton=(Button)rootView.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateListNDB(SidePanelFragment.filterFlag);

            }
        });

        return rootView;
    }

    public void updateListNDB(String filterFlag) {
        DBUtility dbUtility = new DBUtility(getContext());
        String maxid=null;
        if (filterFlag.equals("all")) {
            Cursor cursor;
            Person p = new Person();
            JSONObject convertedJsonObj;
            dbList = new ArrayList<Person>();

            long rows = dbUtility.getRowCount();
            long lowest = dbUtility.getMinId();
            long listSize = SidePanelFragment.listSize;
            System.out.println("list of userList " + listSize);
            if (rows - listSize > 0) {
                System.out.println("data present in db");
                cursor = dbUtility.fetchData();
                JSONArray convertedJson = dbUtility.convertCurToJson(cursor);
                System.out.println("convertedJson:" + convertedJson);

                for (int i = 0; i < convertedJson.length(); i++) {
                    try {

                        convertedJsonObj = convertedJson.getJSONObject(i);
                        Person worldpop = new Person();
                        worldpop.setId(convertedJsonObj.optInt("id"));
                        worldpop.setNickname(convertedJsonObj.optString("nickname"));
                        System.out.println(worldpop.getNickname());
                        worldpop.setCountry(convertedJsonObj.optString("country"));
                        worldpop.setState(convertedJsonObj.optString("state"));
                        worldpop.setCity(convertedJsonObj.optString("city"));
                        worldpop.setYear(convertedJsonObj.optInt("year"));
                        worldpop.setTimeStamp(convertedJsonObj.optString("timestamp"));
                        worldpop.setLongitude(convertedJsonObj.optDouble("longitude"));
                        worldpop.setLat(convertedJsonObj.optDouble("latitude"));
                        dbList.add(worldpop);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    list = (ListView) getActivity().findViewById(R.id.listView);
                    final CustomListAdapter adapter = new CustomListAdapter(getContext(), R.layout.custom_row, dbList);
                    list.setAdapter(adapter);
                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            nickname = adapter.getNickNameAtPosition(position);
                            System.out.println("nickname selected from list "+nickname);
                            String url = "https://assignment5-98801.firebaseio.com/users.json";
                            StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                                @Override
                                public void onResponse(String s) {
                                    doOnSuccess(s);
                                }
                            },new Response.ErrorListener(){
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                    System.out.println("" + volleyError);
                                }
                            });
                            RequestQueue rQueue = Volley.newRequestQueue(getContext());
                            rQueue.add(request);
                        }});
                }
            } else {
                String url = "http://bismarck.sdsu.edu/hometown/users?page=0&reverse=true&beforeid=";
                url += lowest;
                new AsyncUpdateServerCall().execute(url);
            }
        } else if (filterFlag.equals("country_state_year")) {
            System.out.println("enter here " + SidePanelFragment.listSize);
            if (SidePanelFragment.listSize == 25) {
                Cursor cursor;
                cursor=dbUtility.fetchFilterData("select * from home where country=\"" + SidePanelFragment.stateUrl + "\""+ "and state=\"" + SidePanelFragment.stateSelected + "\""+" and year=\"" + SidePanelFragment.yearEntered+ "\"");
                if (cursor.moveToFirst()) {
                    do {
                        maxid = cursor.getString(0);
                        System.out.println("max id present " + maxid);
                    }while(cursor.moveToNext());
                }
                System.out.println("max id present "+maxid);
                String url = "http://bismarck.sdsu.edu/hometown/users?country=" + SidePanelFragment.stateUrl + "&state=" + URLEncoder.encode(SidePanelFragment.stateSelected) + "&year=" + SidePanelFragment.yearEntered + "&page=0&reverse=true&beforeid=";
                url += maxid;
                new AsyncUpdateServerCall().execute(url);

            } else
                System.out.println("don't make call to server");
        }
        else if (filterFlag.equals("country_state")) {
            System.out.println("enter here " + SidePanelFragment.listSize);
            if (SidePanelFragment.listSize == 25) {
                Cursor cursor;
                cursor=dbUtility.fetchFilterData("select * from home where country=\"" + SidePanelFragment.stateUrl + "\""+ " and state=\"" + SidePanelFragment.stateSelected + "\"");
                if (cursor.moveToFirst()) {
                    do {
                        maxid = cursor.getString(0);
                        System.out.println("max id present " + maxid);
                    }while(cursor.moveToNext());
                }
                System.out.println("max id present "+maxid);
                String url = "http://bismarck.sdsu.edu/hometown/users?country=" + SidePanelFragment.stateUrl + "&state=" + URLEncoder.encode(SidePanelFragment.stateSelected) + "&page=0&reverse=true&beforeid=";
                url += maxid;
                new AsyncUpdateServerCall().execute(url);

            } else
                System.out.println("don't make call to server");
        }
        else if (filterFlag.equals("country")) {
            System.out.println("enter here " + SidePanelFragment.listSize);
            if (SidePanelFragment.listSize == 25) {
                Cursor cursor;
                cursor=dbUtility.fetchFilterData("select * from home where country=\"" + SidePanelFragment.stateUrl + "\"");
                if (cursor.moveToFirst()) {
                    do {
                        maxid = cursor.getString(0);
                        System.out.println("max id present " + maxid);
                    }while(cursor.moveToNext());
                }
                System.out.println("max id present "+maxid);
                String url = "http://bismarck.sdsu.edu/hometown/users?country=" + SidePanelFragment.stateUrl + "&page=0&reverse=true&beforeid=";
                url += maxid;
                new AsyncUpdateServerCall().execute(url);

            } else
                System.out.println("don't make call to server");
        }
        else if (filterFlag.equals("year")) {
            System.out.println("enter here " + SidePanelFragment.listSize);
            if (SidePanelFragment.listSize == 25) {
                Cursor cursor;
                cursor=dbUtility.fetchFilterData("select * from home where year=\"" + SidePanelFragment.yearEntered + "\"");
                if (cursor.moveToFirst()) {
                    do {
                        maxid = cursor.getString(0);
                        System.out.println("max id present " + maxid);
                    }while(cursor.moveToNext());
                }
                System.out.println("max id present "+maxid);
                String url = "http://bismarck.sdsu.edu/hometown/users?year=" + SidePanelFragment.yearEntered + "&page=0&reverse=true&beforeid=";
                url += maxid;
                new AsyncUpdateServerCall().execute(url);

            } else
                System.out.println("don't make call to server");
        }
    }
    public void doOnSuccess(String s){
        boolean checkUser=false;
        try {
            JSONObject obj = new JSONObject(s);
            System.out.println(obj);
            Iterator i = obj.keys();
            String key = "";
            while(i.hasNext()){
                key = i.next().toString();
                if(key.equals(nickname)) {
                    System.out.println("got my nickname "+key);
                    checkUser=true;
                    break;
                }
                else
                    checkUser=false;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(checkUser){
            UserDetails.setChatWith(nickname);
            Intent intent=new Intent(getActivity(),SideUserChatViewPanel.class);
            startActivity(intent);
            getActivity().finish();
        }
        else
            Toast.makeText(getActivity(), "User is not registered in Firebase", Toast.LENGTH_LONG).show();
    }
    private class AsyncUpdateServerCall extends AsyncTask<String, Void, ArrayList<Person>> {

        String result;

        @Override
        protected ArrayList<Person> doInBackground(String... params) {
            JSONObject jsonobject;
            System.out.println("person list");
            try {
                URL url = new URL(params[0]);
                System.out.println("URRRRL:" + url);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.connect();
                BufferedReader bf = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = bf.readLine()) != null) {
                    sb.append(line + "\n");
                }
                //System.out.println("Response  Nick Name is "+value);
                result = sb.toString();
                jsonarray = new JSONArray(result);
                for (int i = 0; i < jsonarray.length(); i++) {
                    jsonobject = jsonarray.getJSONObject(i);

                    Person worldpop = new Person();
                    worldpop.setId(jsonobject.optInt("id"));
                    worldpop.setNickname(jsonobject.optString("nickname"));
                    System.out.println(worldpop.getNickname());
                    worldpop.setCountry(jsonobject.optString("country"));
                    worldpop.setState(jsonobject.optString("state"));
                    worldpop.setCity(jsonobject.optString("city"));
                    worldpop.setYear(jsonobject.optInt("year"));
                    worldpop.setTimeStamp(jsonobject.optString("timestamp"));
                    worldpop.setLongitude(jsonobject.optDouble("longitude"));
                    worldpop.setLat(jsonobject.optDouble("latitude"));
                    DBUtility dbUtility=new DBUtility(getContext());
                    dbUtility.insertIntoDB(worldpop);
                    System.out.println("inserted row "+worldpop);
                    SidePanelFragment.userList.add(worldpop);
                    System.out.println("after clicking on next button"+SidePanelFragment.userList.size());

                }

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return  SidePanelFragment.userList;
        }

        protected void onPostExecute(ArrayList<Person> result) {

            list = (ListView) getActivity().findViewById(R.id.listView);
            if (result.size() != 0){
                final CustomListAdapter adapter = new CustomListAdapter(getContext(), R.layout.custom_row, result);
                list.setAdapter(adapter);
                SidePanelFragment.listSize+=result.size();
            }
            else if (result.size() == 0) {
                Toast.makeText(getActivity(), "No Data", Toast.LENGTH_LONG).show();
                list.setAdapter(null);
            }
        }
    }
}
