package com.example.shwetashahane.assignment5;

/**
 * Created by shwetashahane on 4/15/17.
 */

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Button viewUserButton = (Button) this.findViewById(R.id.viewUserButton);
        Button viewUserMapButton = (Button) this.findViewById(R.id.viewUserMapButton);
        TextView logout=(TextView)this.findViewById(R.id.logoutLink);
        viewUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invokeViewUserActivity();
            }
        });

        viewUserMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invokeViewMapActivity();
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, LoginActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

    }

    public void invokeViewUserActivity() {
        Intent intent = new Intent(this, NewUserActivity.class);
        startActivity(intent);
    }

    public void invokeViewMapActivity() {
        Intent intent = new Intent(this, ViewUserByMap.class);
        startActivity(intent);
    }
}
