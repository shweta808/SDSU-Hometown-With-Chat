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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

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

import static com.example.shwetashahane.assignment5.R.id.noUsersText;

/**
 * Created by shwetashahane on 3/19/17.
 */

public class SidePanelFragment extends Fragment {

    JSONArray jsonarray;
    ArrayList<String> countryList = new ArrayList<String>();
    ArrayList<String> stateList = new ArrayList<String>();
    public static ArrayList<Person> userList= new ArrayList<Person>();
    ArrayList<Person> dbList;
    ArrayList<String> users;
    public static String stateUrl, stateSelected,nickname;
    private ListView list;
    Spinner stateSpinner, countrySpinner;
    public static int yearEntered;
    EditText yearText;
    static String email_current,sender;
    public static int listSize;
    public static String filterFlag;

    public static long updateCount=0;
    public SidePanelFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_side_panel, container, false);
        countryList.add("Select Country");
        yearText = (EditText) rootView.findViewById(R.id.yearText);
        yearText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    try {
                        yearEntered = Integer.parseInt(yearText.getText().toString());
                        if (yearEntered <= 1970 || yearEntered >= 2017)
                            yearText.setError("Year must be in between 1970 and 2017");
                    } catch (Exception e) {
                    }
                }
            }
        });


        Button filterButton = (Button) rootView.findViewById(R.id.filterButton);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yearText.clearFocus();
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
        System.out.print("get user name "+UserDetails.getUsername());

        return rootView;
    }

    public void validation() {

        if (yearText.getText().toString().length() != 0) {
            filterFlag="year";
            String query="select * from home where year=\"" + yearText.getText() + "\"";
            String url="http://bismarck.sdsu.edu/hometown/users?year=" + yearText.getText() +"&page=0&reverse=true";
            downloadFromDBNServerList(query,url);

        }
        if (countrySpinner.getSelectedItemPosition() != 0){
            filterFlag="country";
            String query="select * from home where country=\"" + stateUrl + "\"";
            String url="http://bismarck.sdsu.edu/hometown/users?country=" + stateUrl +"&page=0&reverse=true";
            downloadFromDBNServerList(query,url);
        }
        if (stateSpinner.getSelectedItemPosition() != 0){
            filterFlag="country_state";
            String query="select * from home where country=\"" + stateUrl + "\""+ " and state=\"" + stateSelected + "\"";
            String url="http://bismarck.sdsu.edu/hometown/users?country=" + stateUrl + "&state=" + URLEncoder.encode(stateSelected)+"&page=0&reverse=true";
            downloadFromDBNServerList(query,url);
        }

        if (yearText.getText().toString().length() != 0 && countrySpinner.getSelectedItemPosition() != 0 && stateSpinner.getSelectedItemPosition() != 0)
        {
            filterFlag="country_state_year";
            String query="select * from home where country=\"" + stateUrl + "\""+ "and state=\"" + stateSelected + "\""+" and year=\"" + yearText.getText() + "\"";
            String url="http://bismarck.sdsu.edu/hometown/users?country=" + stateUrl + "&state=" + URLEncoder.encode(stateSelected) + "&year=" + yearText.getText()+"&page=0&reverse=true";
            downloadFromDBNServerList(query,url);
        }
        else if (yearText.getText().toString().length() == 0 && stateSpinner.getSelectedItemPosition() == 0 && countrySpinner.getSelectedItemPosition() == 0)
        {
            filterFlag="all";
            String query="select * from home";
            String url="http://bismarck.sdsu.edu/hometown/users?page=0&reverse=true";
            downloadFromDBNServerList(query,url);

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
            countrySpinner = (Spinner) getActivity().findViewById(R.id.countrySpinner);

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
                                    stateSpinner = (Spinner) getActivity().findViewById(R.id.stateSpinner);
                                    stateSpinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item,
                                            stateList));
                                } else {
                                    stateList.clear();
                                    stateList.add("Select State");
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
            stateSpinner = (Spinner) getActivity().findViewById(R.id.stateSpinner);
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


    private class AsyncServerCall extends AsyncTask<String, Void, ArrayList<Person>> {

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

                }
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return userList;
        }

        protected void onPostExecute(ArrayList<Person> result) {

            list = (ListView) getActivity().findViewById(R.id.listView);
            final CustomListAdapter adapter = new CustomListAdapter(getContext(), R.layout.custom_row, result);
            if (result.size() != 0){
                list.setAdapter(adapter);
                listSize=result.size();
                System.out.println("list size "+listSize);
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
            else if (result.size() == 0) {
                Toast.makeText(getActivity(), "No Data", Toast.LENGTH_LONG).show();
                list.setAdapter(null);
            }
        }

    }



    public void downloadFromDBNServerList(String query,String url) {

        dbList = new ArrayList<Person>();
        DBUtility dbUtility = new DBUtility(getContext());
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

        else
        {
            System.out.println("data not present in db then make call to server");
            new AsyncServerCall().execute(url);
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
}





