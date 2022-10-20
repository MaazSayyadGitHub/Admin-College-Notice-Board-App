package com.maaz.admincollegeapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.maaz.admincollegeapp.Faculty.UpdateFaculty;
import com.maaz.admincollegeapp.Notice.DeleteNoticeActivity;
import com.maaz.admincollegeapp.Notice.UploadNotice;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    CardView uploadNotice, addGalleryImage, addEBook, addFaculty, deleteNotice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        switch (view.getId()){
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