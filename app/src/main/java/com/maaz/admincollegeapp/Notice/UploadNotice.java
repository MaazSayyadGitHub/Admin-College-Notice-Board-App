package com.maaz.admincollegeapp.Notice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.maaz.admincollegeapp.DataClass.NoticeData;
import com.maaz.admincollegeapp.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UploadNotice extends AppCompatActivity {

    private CardView addImage;  // for onclickListener
    private static final int REQ = 1;   // request code
    private Bitmap bitmap = null;     // bitmap for image get
    private ImageView noticeImageView;   // for set Image in image PreView

    String downloadUrl = "";

    private EditText noticeTitle;
    private Button uploadNoticeBtn;

    private ProgressDialog progressDialog;

    private DatabaseReference reference, DbRef; // for real time database
    private StorageReference storageReference; // for storage

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_notice);

        addImage = findViewById(R.id.addImage);
        noticeImageView = findViewById(R.id.noticeImageView);
        noticeTitle = findViewById(R.id.noticeTitle);
        uploadNoticeBtn = findViewById(R.id.uploadNoticeButton);

        progressDialog = new ProgressDialog(this);

        reference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        uploadNoticeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (noticeTitle.getText().toString().isEmpty()){
                    noticeTitle.setError("Fill This Title");  // if title is empty
                    noticeTitle.requestFocus();   // focus on title

                } else if (bitmap == null){
                    Toast.makeText(UploadNotice.this, "You Not Selected Image", Toast.LENGTH_SHORT).show();
                    uploadData(); // data will be added to database
                } else {
                    // both data and image will be store in database
                    uploadImage();
                }

            }
        });
    }

    private void uploadImage(){

        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos); // compress image..
        byte[] finalImage = baos.toByteArray();

        final StorageReference filePath;
        filePath = storageReference.child("Notice").child(finalImage+"jpg"); // store img in firebase file path.

        final UploadTask uploadTask = filePath.putBytes(finalImage);   // for upload img.

        uploadTask.addOnCompleteListener(UploadNotice.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {    // after complete.
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if (task.isSuccessful()){     // upload after task complete..
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // get the image path and download image through Uri and get it in downloadUrl.
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {  // after success , uri will be download.
                                    // get in downloadUrl
                                    downloadUrl = String.valueOf(uri);
                                    uploadData();

                                }
                            });
                        }
                    });

                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(UploadNotice.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadData(){
        DbRef = reference.child("Notice");
        final String uniqueKey = DbRef.push().getKey();

        String title = noticeTitle.getText().toString();

        // this is for Date.
        Calendar calenderDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yy");
        String date = currentDate.format(calenderDate.getTime());

        // this is for Time.
        Calendar calendarTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a"); // a = for AM/PM
        String time = currentTime.format(calendarTime.getTime());

        // pass data to model class
        NoticeData noticeData = new NoticeData(title, downloadUrl, date, time, uniqueKey);

        DbRef.child(uniqueKey).setValue(noticeData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Toast.makeText(UploadNotice.this, "Notice Uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(UploadNotice.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void openGallery(){
        Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickImage, REQ);
    }

    // startActivityForResult,
    // we use these two methods for going to another activity or another apps to get data from there and use in this activity.
    // here we are getting image from gallery.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ && resultCode == RESULT_OK) {
            // each data comes in data.
            Uri uri = data.getData();  // it will get image uri from gallery
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri); // and convert in bitmap
            } catch (IOException e) {
                e.printStackTrace();
            }
            noticeImageView.setImageBitmap(bitmap);  // and set in noticeImageView.. Preview

        }
    }



}