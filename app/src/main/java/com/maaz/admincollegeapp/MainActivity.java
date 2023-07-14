package com.maaz.admincollegeapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.maaz.admincollegeapp.Faculty.UpdateFaculty;
import com.maaz.admincollegeapp.Notice.DeleteNoticeActivity;
import com.maaz.admincollegeapp.Notice.UploadNotice;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    CardView uploadNotice, addGalleryImage, addEBook, addFaculty, deleteNotice;
    MaterialButton logOut;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Log Out Code.
        sharedPreferences = this.getSharedPreferences("LogIn", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // if directly come without login.
//        if (sharedPreferences.getString("isLogIn", "NO").equals("NO")){
//            startActivity(new Intent(MainActivity.this, LoginActivity.class));
//            finish();
//        }
        // log out code.
        logOut = findViewById(R.id.logOutBtn);
        logOut.setOnClickListener(view -> {
            // Log Out Code.
            String loginStatus = sharedPreferences.getString("IsLogIn", "NO"); // NO is default it will get.
            if (loginStatus.equals("YES")) {
                editor.putString("IsLogIn", "NO");
                editor.commit();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });


        uploadNotice = findViewById(R.id.addNotice);
        addGalleryImage = findViewById(R.id.addGalleryImage);
        addEBook = findViewById(R.id.addEBook);
        addFaculty = findViewById(R.id.addFaculty);
        deleteNotice = findViewById(R.id.deleteNotice);

        uploadNotice.setOnClickListener(this);
        addGalleryImage.setOnClickListener(this);
        addEBook.setOnClickListener(this);
        addFaculty.setOnClickListener(this);
        deleteNotice.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addNotice:
                Intent NoticeIntent = new Intent(MainActivity.this, UploadNotice.class);
                startActivity(NoticeIntent);
                break;
            case R.id.addGalleryImage:
                Intent galleryIntent = new Intent(MainActivity.this, UploadImage.class);
                startActivity(galleryIntent);
                break;
            case R.id.addEBook:
                Intent PdfIntent = new Intent(MainActivity.this, UploadPdf.class);
                startActivity(PdfIntent);
                break;
            case R.id.addFaculty:
                Intent FacultyIntent = new Intent(MainActivity.this, UpdateFaculty.class);
                startActivity(FacultyIntent);
                break;
            case R.id.deleteNotice:
                Intent DeleteIntent = new Intent(MainActivity.this, DeleteNoticeActivity.class);
                startActivity(DeleteIntent);
                break;
        }
    }


}