package com.example.shwetashahane.assignment5;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;
    private FirebaseAuth auth;
    private EditText emailText, passwordText;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth=FirebaseAuth.getInstance();
        emailText=(EditText)this.findViewById(R.id.emailText);
        passwordText=(EditText)this.findViewById(R.id.passwordText);
        loginButton=(Button)this.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailText.clearFocus();
                passwordText.clearFocus();
                validation();
            }
        });
        passwordText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    password = passwordText.getText().toString();
                    if (passwordText.getText().toString().length() < 3) {
                        passwordText.setError("Password must be atleast 3 characters long!");
                    }
                }
            }
        });

        emailText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String email = emailText.getText().toString().trim();
                    if (email.isEmpty() || !isEmailValid(email)) {
                        emailText.setError("enter valid email-id");
                    }
                }
            }
        });
        TextView regLink =(TextView)findViewById(R.id.regLink);
        regLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, NewUserRegistration.class);
                startActivity(intent);
                finish();
            }
        });
    }
    public void submitForm(){
        String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();
        System.out.println(email);
        System.out.println(password);
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>(){
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful())
                    Toast.makeText(LoginActivity.this, "Wrong Username and Password", Toast.LENGTH_LONG).show();
                else
                {
                    Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private static boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void validation(){
        if (emailText.getError() == null && passwordText.getError() == null) {
            submitForm();
        }
    }

    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
