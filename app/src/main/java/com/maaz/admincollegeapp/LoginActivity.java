package com.maaz.admincollegeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;

public class LoginActivity extends AppCompatActivity {

    private EditText adminEmail, adminPassword;
    private TextView tvShow;
    private RelativeLayout loginBtn;

    private String email, password;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = this.getSharedPreferences("LogIn", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // sending To Login else MainActivity.
        String loginStatus = sharedPreferences.getString("IsLogIn","NO"); // NO is default it will get.
        if (loginStatus.equals("YES")){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        adminEmail = findViewById(R.id.user_email);
        adminPassword = findViewById(R.id.user_password);
        tvShow = findViewById(R.id.txt_show);
        loginBtn = findViewById(R.id.loginBtn);

        // this is for password SHOW and HIDE Functionality
        tvShow.setOnClickListener(view -> {
            // 144 means it is in HIDE Mode
            if (adminPassword.getInputType() == 144) {
                adminPassword.setInputType(129); // this 129 means it is now in SHOW Mode.
                tvShow.setText("Show");
            } else {
                adminPassword.setInputType(144); // else put it in HIDE Mode.
                tvShow.setText("Hide");
            }

            // this is becos after doing show/hide cursor will go to start.
            adminPassword.setSelection(adminPassword.getText().toString().length()); // it will show cursor to the end.
        });

        loginBtn.setOnClickListener(view -> {
            validateData();
        });


    }

    private void validateData() {
        email = adminEmail.getText().toString();
        password = adminPassword.getText().toString();

        if (email.isEmpty() && password.isEmpty()) {
            adminEmail.setError("Required");
            adminPassword.setError("Required");
            adminEmail.requestFocus();
            adminPassword.requestFocus();
        } else if (email.isEmpty()){
            adminEmail.setError("Required");
            adminEmail.requestFocus();
        } else if (password.isEmpty()){
            adminPassword.setError("Required");
            adminPassword.requestFocus();
        } else if (email.equals("admin@gmail.com") && password.equals("admin@123")){
            openDashBoard();
        } else {
            Toast.makeText(this, "Email & Password are Wrong.", Toast.LENGTH_LONG).show();
        }


    }

    // if admin is loged in then it will directly go to DashBoard not again logIn.
    private void openDashBoard() {
        editor.putString("IsLogIn", "YES");
        editor.commit();

        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }
}