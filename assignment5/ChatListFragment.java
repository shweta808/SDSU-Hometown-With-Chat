package com.example.shwetashahane.assignment5;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

public class ChatListFragment extends Fragment{
    ListView usersChatList;
    TextView noUsersText;
    ArrayList<String> al = new ArrayList<>();
    ArrayList<String> ml  = new ArrayList<>();;
    HashSet<String> chatWithUsers=new HashSet<String>();
    int totalUsers = 0;
    static String email_current,currentUserNickname;
    CustomUserListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat_list, container, false);
        String url1 = "https://assignment5-98801.firebaseio.com/messages.json";
        usersChatList = (ListView)rootView.findViewById(R.id.chatList);
        noUsersText = (TextView)rootView.findViewById(R.id.noUsersText);
        String url = "https://assignment5-98801.firebaseio.com/users.json";
        email_current= FirebaseAuth.getInstance().getCurrentUser().getEmail();
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                setCurrentUser(s);
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);
            }
        });
        RequestQueue rQueue = Volley.newRequestQueue(getContext());
        rQueue.add(request);



        StringRequest request1 = new StringRequest(Request.Method.GET, url1, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                downloadMessage(s);
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);
            }
        });

        RequestQueue rQueue1 = Volley.newRequestQueue(getContext());
        rQueue1.add(request1);

        usersChatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserDetails.chatWith=null;
                UserDetails.setChatWith(ml.get(position));
                startActivity(new Intent(getContext(), ChatViewActivity.class));
            }
        });

        System.out.println("value of cun "+currentUserNickname);
        return rootView;
    }

    public void setCurrentUser(String s){
        try {
            JSONObject obj = new JSONObject(s);

            Iterator i = obj.keys();
            String key = "";

            while(i.hasNext()){
                key = i.next().toString();
                JSONObject nickname  = obj.getJSONObject(key);
                System.out.println("json object:" +nickname.getString("email"));
                if((email_current.equals(nickname.getString("email")))) {
                    currentUserNickname = key;
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void downloadMessage(String s){

        try {

            JSONObject obj = new JSONObject(s);

            Iterator i = obj.keys();
            String key = "";

            while(i.hasNext()){
                key = i.next().toString();
                if(key.contains(UserDetails.getUsername())) {
                    String[] parts = key.split("_");
                    String part1 = parts[0];
                    String part2 = parts[1];
                    chatWithUsers.add(part1);
                    chatWithUsers.add(part2);

                }
            }
            for(String str:chatWithUsers){
                if(!(str.equals(UserDetails.getUsername())))
                    ml.add(str);
            }
            System.out.println("unique values "+ml);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        noUsersText.setVisibility(View.GONE);
        usersChatList.setVisibility(View.VISIBLE);
        adapter = new CustomUserListAdapter(getContext(), R.layout.custom_user_row, ml);
        usersChatList.setAdapter(adapter);

    }




}
