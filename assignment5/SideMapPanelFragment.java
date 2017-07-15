package com.example.shwetashahane.assignment5;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import android.location.Address;

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
import java.util.List;

/**
 * Created by shwetashahane on 3/19/17.
 */

public class SideMapPanelFragment extends Fragment {

    JSONArray jsonarray;
    ArrayList<String> countryList = new ArrayList<String>();
    ArrayList<String> stateList = new ArrayList<String>();
    private GoogleMap gMap;
    private com.google.android.gms.maps.MapView mapView;
    ArrayList<Person> userList, personList;
    ArrayList<Person> dbList;
    Spinner stateSpinner, countrySpinner;
    double lat, lng;
    EditText yearMapText;
    int yearEntered;
    static String filterMapFlag;
    static String email_current,sender;
    public static long updateCount=0;
    Button nextMapButton;
    public static String stateUrl, stateSelected,nickname;

    public SideMapPanelFragment() {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sidemap_panel, container, false);
        System.out.println("inside map fragment");
        countryList.add("Select Country");
        System.out.println("Select country" + countryList);
        yearMapText = (EditText) rootView.findViewById(R.id.yearMapText);
        yearMapText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    try {
                        yearEntered = Integer.parseInt(yearMapText.getText().toString());
                        if (yearEntered <= 1970 || yearEntered >= 2017)
                            yearMapText.setError("Year must be in between 1970 and 2017");
                    } catch (Exception e) {
                    }
                }
            }
        });
        mapView = (com.google.android.gms.maps.MapView) rootView.findViewById(R.id.mapViewUser);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                gMap = googleMap;
                gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        });

        Button filterMapButton = (Button) rootView.findViewById(R.id.filterMapButton);
        filterMapButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                yearMapText.clearFocus();
                validation();
            }
        });

        email_current= FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String url = "https://assignment5-98801.firebaseio.com/users.json";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                retrieveEmail(s);
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);
            }
        });
        RequestQueue rQueue = Volley.newRequestQueue(getActivity());
        rQueue.add(request);

        nextMapButton=(Button)rootView.findViewById(R.id.updateMapButton);
        nextMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMapNDB(filterMapFlag);

            }
        });
        TextView logout=(TextView)rootView.findViewById(R.id.logoutMap);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getActivity().finish();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        return rootView;
    }

    public void validation() {
        if (yearMapText.getText().toString().length() != 0)
        {
            filterMapFlag="year";
            String query="select * from home where year=\"" + yearMapText.getText() + "\"";
            String url="http://bismarck.sdsu.edu/hometown/users?year=" + yearMapText.getText() +"&page=0&reverse=true";
            downloadFromDBNServer(query,url);
        }
        if (countrySpinner.getSelectedItemPosition() != 0)
        {
            filterMapFlag="country";
            String query="select * from home where country=\"" + stateUrl + "\"";
            String url="http://bismarck.sdsu.edu/hometown/users?country=" + stateUrl +"&page=0&reverse=true";
            downloadFromDBNServer(query,url);
        }

        if (stateSpinner.getSelectedItemPosition() != 0)
        {
            filterMapFlag="country_state";
            String query="select * from home where country=\"" + stateUrl + "\""+ " and state=\"" + stateSelected + "\"";
            String url="http://bismarck.sdsu.edu/hometown/users?country=" + stateUrl + "&state=" + URLEncoder.encode(stateSelected)+"&page=0&reverse=true";
            downloadFromDBNServer(query,url);
        }
        if (yearMapText.getText().toString().length() != 0 && countrySpinner.getSelectedItemPosition() != 0 && stateSpinner.getSelectedItemPosition() != 0)
        {
            filterMapFlag="country_state_year";
            String query="select * from home where country=\"" + stateUrl + "\""+ "and state=\"" + stateSelected + "\""+" and year=\"" + yearMapText.getText() + "\"";
            String url="http://bismarck.sdsu.edu/hometown/users?country=" + stateUrl + "&state=" + URLEncoder.encode(stateSelected) + "&year=" + yearMapText.getText()+"&page=0&reverse=true";
            downloadFromDBNServer(query,url);

        }
        else if (yearMapText.getText().toString().length() == 0 && stateSpinner.getSelectedItemPosition() == 0 && countrySpinner.getSelectedItemPosition() == 0)
        {
            filterMapFlag="all";
            String query="select * from home";
            String url="http://bismarck.sdsu.edu/hometown/users?page=0&reverse=true";
            downloadFromDBNServer(query,url);
        }

    }


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new DownloadJSON().execute();

    }

    private class DownloadJSON extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {

                jsonarray = JSONfunctions.getJSONfromURL("http://bismarck.sdsu.edu/hometown/countries");
                System.out.println("Json array :" + jsonarray.length());
                for (int i = 0; i < jsonarray.length(); i++) {
                    countryList.add(jsonarray.getString(i));
                    System.out.println(countryList.get(i));
                }
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void args) {
            countrySpinner = (Spinner) getActivity().findViewById(R.id.countryMapSpinner);

            countrySpinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item,
                    countryList));
            countrySpinner
                    .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                            try {
                                stateUrl = countryList.get(position);
                                if (stateUrl == "Select Country") {
                                    stateList.add("Select State");
                                    stateSpinner = (Spinner) getActivity().findViewById(R.id.stateMapSpinner);
                                    stateSpinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item,
                                            stateList));
                                } else {
                                    stateList.clear();
                                    stateList.add("Select State");
                                    displayCountry(countryList.get(position));
                                    new DownloadState().execute();
                                }
                            } catch (Exception e) {
                                Log.e("Error", e.getMessage());
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {

                        }
                    });

        }
    }

    private class DownloadState extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            System.out.println("state");
            try {

                jsonarray = JSONfunctions.getJSONfromURL("http://bismarck.sdsu.edu/hometown/states?country=" + stateUrl);
                System.out.println("Json state array :" + jsonarray.length());
                for (int i = 0; i < jsonarray.length(); i++) {
                    stateList.add(jsonarray.getString(i));
                    System.out.println(stateList.get(i));
                }
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void args) {
            Spinner stateSpinner = (Spinner) getActivity().findViewById(R.id.stateMapSpinner);
            stateSpinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item,
                    stateList));
            stateSpinner
                    .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                            try {
                                stateSelected = stateList.get(position);
                            } catch (Exception e) {
                                Log.e("Error", e.getMessage());
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {

                        }
                    });

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private class DownloadMarker extends AsyncTask<String, Void, ArrayList<Person>> {

        String result;

        @Override
        protected ArrayList<Person> doInBackground(String... params) {
            userList = new ArrayList<Person>();
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
                    userList.add(worldpop);

                }
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return userList;
        }

        protected void onPostExecute(ArrayList<Person> result) {
            gMap.clear();
            showMarker(result);

        }
    }

    public void displayCountry(String country) {
        Geocoder locator = new Geocoder(getContext());

        try {
            List<Address> address =
                    locator.getFromLocationName(country, 1);
            for (Address addressLocation : address) {
                if (addressLocation.hasLatitude())
                    lat = addressLocation.getLatitude();
                if (addressLocation.hasLongitude())
                    lng = addressLocation.getLongitude();
            }
        } catch (Exception error) {
        }
        LatLng display = new LatLng(lat, lng);
        gMap.moveCamera(CameraUpdateFactory.newLatLng(display));
        gMap.animateCamera(CameraUpdateFactory.zoomTo(4));
    }


    public void retrieveEmail(String s){

        try {
            JSONObject obj = new JSONObject(s);
            Iterator i=obj.keys();
            while(i.hasNext()) {
                String key=i.next().toString();
                JSONObject nickname  = obj.getJSONObject(key);
                System.out.println("json object:" +nickname.getString("email"));
                if(email_current.equals(nickname.getString("email"))){
                    System.out.println("Emaild id of sender:"+nickname.getString("email"));
                    sender=key;
                    UserDetails.setUsername(sender);
                    System.out.print("sender:"+sender);
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateMapNDB(String filterFlag) {
        DBUtility dbUtility = new DBUtility(getContext());
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
                    gMap.clear();
                    showMarker(dbList);
                }
            } else {
                String url = "http://bismarck.sdsu.edu/hometown/users?page=0&reverse=true&beforeid=";
                url += lowest;
                new AsyncUpdateMapServerCall().execute(url);
            }
        } else if (filterFlag.equals("country_state_year")) {
            String url= "http://bismarck.sdsu.edu/hometown/users?country=" + SidePanelFragment.stateUrl + "&state=" + URLEncoder.encode(SidePanelFragment.stateSelected) + "&year=" + SidePanelFragment.yearEntered + "&page=0&reverse=true&beforeid=";
            String query="select * from home where country=\"" + stateUrl + "\""+ "and state=\"" + stateSelected + "\""+" and year=\"" + yearMapText.getText() + "\"";
            updateFilterMap(url,query);
        }
        else if (filterFlag.equals("country_state")) {
            String url= "http://bismarck.sdsu.edu/hometown/users?country=" + stateUrl + "&state=" + URLEncoder.encode(stateSelected) + "&page=0&reverse=true&beforeid=";
            String query="select * from home where country=\"" + stateUrl + "\""+ " and state=\"" + stateSelected + "\"";
            updateFilterMap(url,query);
        }
        else if (filterFlag.equals("country")) {
            String url= "http://bismarck.sdsu.edu/hometown/users?country=" + stateUrl + "&page=0&reverse=true&beforeid=";
            String query="select * from home where country=\"" + stateUrl + "\"";
            updateFilterMap(url,query);
        }
        else if (filterFlag.equals("year")) {
            String url= "http://bismarck.sdsu.edu/hometown/users?year=" + yearMapText.getText() + "&page=0&reverse=true&beforeid="; ;
            String query="select * from home where year=\"" + yearMapText.getText() + "\"";
            updateFilterMap(url,query);

        }
    }

    private class AsyncUpdateMapServerCall extends AsyncTask<String, Void, ArrayList<Person>> {

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
                    userList.add(worldpop);
                    System.out.println("after clicking on next button"+userList.size());

                }

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return  SidePanelFragment.userList;
        }

        protected void onPostExecute(ArrayList<Person> result) {
            gMap.clear();
            showMarker(result);
        }
    }

    public void showMarker(ArrayList<Person> result){
        if (result.size() != 0) {
            personList = result;
            for (int i = 0; i < personList.size(); i++) {
                if (personList.get(i).getLat() == 0.0 || personList.get(i).getLongitude() == 0.0) {
                    Geocoder locator = new Geocoder(getActivity());
                    String country_state = personList.get(i).getCity() + "," + personList.get(i).getState() + ", " + personList.get(i).getCountry();
                    System.out.println("CS with no lat long :" + country_state + "nick name " + personList.get(i).getNickname());
                    try {
                        List<Address> state = locator.getFromLocationName(country_state, 1);
                        for (Address stateLocation : state) {
                            if (stateLocation.hasLatitude())
                                lat = stateLocation.getLatitude();
                            if (stateLocation.hasLongitude())
                                lng = stateLocation.getLongitude();
                        }
                    } catch (Exception error) {
                        Log.e("rew", "Address lookup Error", error);
                    }
                    gMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng))
                            .title(personList.get(i).getNickname()));
                } else {
                    System.out.println("CS with lat long :" + "nick name " + personList.get(i).getNickname());
                    gMap.addMarker(new MarkerOptions().position(new LatLng(personList.get(i).getLat(), personList.get(i).getLongitude()))
                            .title(personList.get(i).getNickname()));
                }


            }
        } else if (result.size() == 0) {
            gMap.clear();
            Toast.makeText(getActivity(), "No Data", Toast.LENGTH_LONG).show();
        }

        gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                nickname = marker.getTitle();
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
                return true;
            }
        });
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

    public void updateFilterMap(String url , String query){
        DBUtility dbUtility=new DBUtility(getContext());
        String maxid=null;
        System.out.println("enter here " + SidePanelFragment.listSize);
        if (SidePanelFragment.listSize == 25) {
            Cursor cursor;
            cursor=dbUtility.fetchFilterData(query);
            if (cursor.moveToFirst()) {
                do {
                    maxid = cursor.getString(0);
                    System.out.println("max id present " + maxid);
                }while(cursor.moveToNext());
            }
            System.out.println("max id present "+maxid);
            url += maxid;
            new AsyncUpdateMapServerCall().execute(url);

        } else
            System.out.println("don't make call to server");
    }

    public void downloadFromDBNServer(String query,String url){
        dbList = new ArrayList<Person>();
        final DBUtility dbUtility = new DBUtility(getContext());
        Cursor cursor;
        JSONObject convertedJsonObj;
        System.out.println("data present in db");
        updateCount=dbUtility.getRowCount();
        cursor=dbUtility.fetchFilterData(query);
        System.out.println("cursor count "+cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(0);
                System.out.println("max id present " + id);
            }while(cursor.moveToNext());
        }
        if(cursor.getCount()!=0) {
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
            }
            gMap.clear();
            showMarker(dbList);
        }
        else
        {
            System.out.println("data not present in db then make call to server");
            new DownloadMarker().execute(url);
        }
    }
}