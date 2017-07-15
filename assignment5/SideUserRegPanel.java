package com.example.shwetashahane.assignment5;

/**
 * Created by shwetashahane on 4/14/17.
 */


import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;



public class SideUserRegPanel extends Fragment {

    JSONArray jsonarray;
    ArrayList<String> countryList = new ArrayList<String>();
    ArrayList<String> stateList = new ArrayList<String>();
    static String stateUrl, nickName, password, stateSelected, city, year,email;
    EditText nickNameText, passswordText, cityText, yearText,emailText;
    TextView latText;
    Person person;
    int yearEntered;
    Fragment frag;
    FragmentTransaction fragTransaction;
    Spinner stateSpinner, countrySpinner;
    private String country;
    private String state;
    double latNew;
    private FirebaseAuth auth;

    public SideUserRegPanel() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_userreg_side_panel, container, false);
        countryList.add("Select Country");
        System.out.println("country list in oncreate viewcountryList" + countryList);
        nickNameText = (EditText) rootView.findViewById(R.id.nickNameListText);
        nickNameText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    nickName = nickNameText.getText().toString();
                    System.out.println("Nick Name:" + nickNameText.getText());
                    if (nickName.length() == 0) {
                        nickNameText.setError("Nick name is required!");
                    } else
                        new checkNickName().execute("http://bismarck.sdsu.edu/hometown/nicknameexists?name=" + nickName);
                }
            }
        });
        passswordText = (EditText) rootView.findViewById(R.id.passwordText);
        passswordText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    password = passswordText.getText().toString();
                    if (passswordText.getText().toString().length() < 3) {
                        passswordText.setError("Password must be atleast 3 characters long!");
                    }
                }
            }
        });
        cityText = (EditText) rootView.findViewById(R.id.cityListText);
        cityText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    city = cityText.getText().toString();
                    if (cityText.getText().toString().length() == 0)
                        cityText.setError("City is required!");
                }
            }
        });
        yearText = (EditText) rootView.findViewById(R.id.yearText);
        yearText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    year = yearText.getText().toString();
                    if ((yearText.getText().toString()).equals("")) {
                        yearText.setError("Year is required!");
                    } else {
                        yearEntered = Integer.parseInt(yearText.getText().toString());
                        if (yearEntered <= 1970 || yearEntered >= 2017)
                            yearText.setError("Year must be in between 1970 and 2017");
                    }

                }
            }
        });

        latText = (TextView) rootView.findViewById(R.id.latText);

        System.out.println("Marker Lat"+latNew);

        Button submitButton = (Button) rootView.findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nickNameText.clearFocus();
                passswordText.clearFocus();
                cityText.clearFocus();
                yearText.clearFocus();
                emailText.clearFocus();
                validation();
            }
        });

        emailText=(EditText)rootView.findViewById(R.id.emailText);

        emailText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    email=emailText.getText().toString();
                    System.out.println("Emmail Id entered by user:"+email);
                    if (email.isEmpty() || !isEmailValid(email)) {
                        emailText.setError("enter valid email-id");
                    }
                }
            }
        });

        auth = FirebaseAuth.getInstance();
        System.out.println("Email ID enetered for reg in firebase:"+email);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new DownloadJSON().execute();
        if (savedInstanceState != null) {
            country = savedInstanceState.get("Country").toString();
            state = savedInstanceState.get("State").toString();
            countrySpinner.setSelection(getIndex(country));
            stateSpinner.setSelection(getIndex(state));
            System.out.println(getIndex(country));
            System.out.println(getIndex(state));
        } else
            System.out.println("not saved");

    }

    public int getIndex(String itemName) {
        for (int i = 0; i < countryList.size(); i++) {
            String country = countryList.get(i);
            if (itemName.equals(country)) {
                return i;
            }
        }

        return -1;
    }

    private static boolean isEmailValid(String email) {
        boolean checkEmail=android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        System.out.println("chekc email"+checkEmail);
        return checkEmail;
    }

    public void validation() {
        if (countrySpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(getActivity(), "Please Select Country!", Toast.LENGTH_LONG).show();
        } else if (stateSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(getActivity(), "Please Select State!", Toast.LENGTH_LONG).show();
        } else {
            if (nickNameText.getError() == null && passswordText.getError() == null && cityText.getError() == null
                    && yearText.getError() == null && emailText.getError()==null) {
                new HttpAsyncTask().execute("http://bismarck.sdsu.edu/hometown/adduser");
            }}
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        System.out.println("on save instance ");
        outState.putString("Country", stateUrl);
        outState.putString("State", stateSelected);
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

            try {
                jsonarray = JSONfunctions.getJSONfromURL("http://bismarck.sdsu.edu/hometown/states?country=" + stateUrl);
                System.out.println("Json state array :" + jsonarray.length());
                for (int i = 0; i < jsonarray.length(); i++) {
                    stateList.add(jsonarray.getString(i));
                    System.out.println("statelist in download state" + stateList.get(i));
                }
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void args) {

            System.out.println("state");
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

    private class checkNickName extends AsyncTask<String, Void, String> {

        String result;

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                System.out.println("URRRRL:" + url);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.connect();
                BufferedReader bf = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String value = bf.readLine();
                System.out.println("Response  Nick Name is " + value);
                result = value;
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return result;
        }

        protected void onPostExecute(String result) {
            if (result.startsWith("true")) {
                nickNameText.setError("Nick Name already exists");
            }
        }
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String result = "";
            person = new Person();
            person.setNickname(nickName);
            person.setPassword(password);
            person.setCountry(stateUrl);
            person.setState(stateSelected);
            person.setCity(city);
            person.setYear(yearEntered);
            person.setLat(MainShowMapPanel.markerLat);
            person.setLongitude(MainShowMapPanel.markerLong);

            try {
                URL urls = new URL(url[0]);
                System.out.println("URRRRL:" + urls);
                HttpURLConnection con = (HttpURLConnection) urls.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                String json = "";
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("nickname", person.getNickname());
                jsonObject.put("password", person.getPassword());
                jsonObject.put("country", person.getCountry());
                jsonObject.put("state", person.getState());
                jsonObject.put("city", person.getCity());
                jsonObject.put("year", person.getYear());
                jsonObject.put("latitude",person.getLat());
                jsonObject.put("longitude",person.getLongitude());
                json = jsonObject.toString();
                System.out.println("JSON data:" + json);
                BufferedWriter out =
                        new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
                out.write(json);
                out.close();

                int HttpResult = con.getResponseCode();
                System.out.println(HttpResult);
                BufferedReader bf = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String val = bf.readLine();
                result = val;

            } catch (Exception e) {
                Log.d("InputStream", e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            System.out.println("Result:" + result);
            if (result.contains("ok")) {
                Toast.makeText(getActivity(), "Data posted successfully", Toast.LENGTH_LONG).show();
                System.out.println("EMail id after data posted successfully "+email);
                System.out.println("password after data posted successfully "+password);
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(Task<AuthResult> task) {
                                Toast.makeText(getActivity(), "Registration Successful" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                if (!task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Intent myIntent = new Intent(getActivity(), NewUserActivity.class);
                                    getActivity().startActivity(myIntent);
                                    getActivity().finish();
                                }
                            }
                        });
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference();
                reference.child("users").child(nickName).child("email").setValue(email);
                reference.child("users").child(nickName).child("password").setValue(password);
                clearItems(nickNameText);
                clearItems(passswordText);
                clearItems(cityText);
                clearItems(yearText);
                clearItems(emailText);
                latText.setText("Latitude Longitude");
                countrySpinner.setSelection(0);
                stateSpinner.setSelection(0);
                MainShowMapPanel.markerLat=0;
                MainShowMapPanel.markerLong=0;

            }

        }
    }

    void clearItems(View view) {
        EditText editText = (EditText) view;
        editText.setText("");
    }


}
