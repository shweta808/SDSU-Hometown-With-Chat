package com.example.shwetashahane.assignment5;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by shwetashahane on 4/15/17.
 */

public class ContactListFragment extends Fragment {
    ListView usersList;
    TextView noUsersText;
    ArrayList<String> al = new ArrayList<>();
    ArrayList<String> ml  = new ArrayList<>();;
    HashSet<String> chatWithUsers=new HashSet<String>();
    int totalUsers = 0;
    public static String email_current,currentUserNickname;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contact_list, container, false);
        usersList = (ListView)rootView.findViewById(R.id.contactList);
        noUsersText = (TextView)rootView.findViewById(R.id.noUsersText);
        String url = "https://assignment5-98801.firebaseio.com/users.json";
        email_current= FirebaseAuth.getInstance().getCurrentUser().getEmail();
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

        usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserDetails.setChatWith(al.get(position));
                startActivity(new Intent(getActivity(), ChatViewActivity.class));
//                final ChatListFragment chatFrag=new ChatListFragment();
            }
        });

        return rootView;
    }


    public void doOnSuccess(String s){
        try {
            JSONObject obj = new JSONObject(s);

            Iterator i = obj.keys();
            String key = "";

            while(i.hasNext()){
                key = i.next().toString();
                JSONObject nickname  = obj.getJSONObject(key);
                System.out.println("json object:" +nickname.getString("email"));
                if(!(email_current.equals(nickname.getString("email")))) {
                    al.add(key);
//                    ((BaseAdapter) usersList.getAdapter()).notifyDataSetChanged();
                }
                else {
                    currentUserNickname = key;
                    System.out.println("curent user nickname " + currentUserNickname);
                }
                totalUsers++;
            }
            System.out.println(al);
//            ((BaseAdapter) usersList.getAdapter()).notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(totalUsers <=1){
            noUsersText.setVisibility(View.VISIBLE);
            usersList.setVisibility(View.GONE);
        }
        else{
            noUsersText.setVisibility(View.GONE);
            usersList.setVisibility(View.VISIBLE);
            CustomUserListAdapter adapter = new CustomUserListAdapter(getContext(), R.layout.custom_user_row, al);
            usersList.setAdapter(adapter);

        }

    }
}
