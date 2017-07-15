package com.example.shwetashahane.assignment5;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by shwetashahane on 4/15/17.
 */

public class ViewUserByMap extends AppCompatActivity
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_map);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.activity_layout_map, new SideMapPanelFragment()).commit();
        }
        System.out.println("View User By Map Activity");
    }
}
