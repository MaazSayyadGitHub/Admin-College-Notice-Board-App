package com.maaz.admincollegeapp.Faculty;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.maaz.admincollegeapp.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class UpdateTeacherActivity extends AppCompatActivity {

    private ImageView updateTeacherImg;
    private EditText updateTeachername, updateTeacheremail, updateTeacherpost;
    private Button updateBtn, deleteBtn;

    private String name, email, image, post;
    private final static int REQ = 1;
    private Bitmap bitmap = null; // null becos we are checking null condition for validate
    private String downloadUrl = "";

    private StorageReference storageReference;
    private DatabaseReference reference;

    private String category, uniqueKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_teacher);

        updateTeacherImg = findViewById(R.id.updateTeacherimg);
        updateTeachername = findViewById(R.id.updateTeacherName);
        updateTeacheremail = findViewById(R.id.updateTeacherEmail);
        updateTeacherpost = findViewById(R.id.updateTeacherPost);
        updateBtn = findViewById(R.id.updateTeacherBtn);
        deleteBtn = findViewById(R.id.deleteTeacherBtn);

        reference = FirebaseDatabase.getInstance().getReference().child("Teacher");
        storageReference = FirebaseStorage.getInstance().getReference();

        // get data from adapter for update
        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        post = getIntent().getStringExtra("post");
        image = getIntent().getStringExtra("image");
        uniqueKey = getIntent().getStringExtra("key");
        category = getIntent().getStringExtra("category");

        // set data to fields.
        try {
            Picasso.get().load(image).into(updateTeacherImg);
        } catch (Exception e) {
            e.printStackTrace();
        }

        updateTeachername.setText(name);
        updateTeacheremail.setText(email);
        updateTeacherpost.setText(post);

        updateTeacherImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();  // Open Image Picker
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = updateTeachername.getText().toString();
                email = updateTeacheremail.getText().toString();
                post = updateTeacherpost.getText().toString();

                // check if any field is empty
                checkValidation();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteData();
            }
        });
    }

    private void checkValidation(){
        if (name.isEmpty()){
            updateTeachername.setError("Field is Empty");
            updateTeachername.requestFocus();
        } else if (email.isEmpty()){
            updateTeacheremail.setError("Field is Empty");
            updateTeacheremail.requestFocus();
        } else if (post.isEmpty()){
            updateTeacherpost.setError("Field is Empty");
            updateTeacherpost.requestFocus();
        } else if (bitmap == null) {
            // if admin not select new image from (Image Picker) means bitmap is null
            // then it will upload previous image
            updateData(image); // this is pre image
        } else {
            // if select then new means bitmap will be upload
            uploadImage();
        }
    }

    private void updateData(String Image){ // here both images will be come, previous image also and new bitmap also.

        // put all data into hashmap and pass to upload in database
        HashMap hp = new HashMap();
        hp.put("name", name);
        hp.put("email", email);
        hp.put("post", post);
        hp.put("image", Image);


        reference.child(category).child(uniqueKey).updateChildren(hp).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(UpdateTeacherActivity.this, "Teacher Updated Successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UpdateTeacherActivity.this, UpdateFaculty.class);
                // if i will press back after coming back from Update Activity then it will not go to Update Activity Again.
                // proven by testing - that's why we use flags to clear all Activities and put current activity on top.
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateTeacherActivity.this, "Something went Wrong.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteData(){
        // we will simply remove value of that particular uniqueKey. so it is our deletion
        reference.child(category).child(uniqueKey).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(UpdateTeacherActivity.this, "Teacher Deleted Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UpdateTeacherActivity.this, UpdateFaculty.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);  // for come back from Activity
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateTeacherActivity.this, "Something went Wrong.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // upload new image to storage and get url from here to upload in database
    private void uploadImage(){

        // compress image
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos); // compress image..
        byte[] finalImage = baos.toByteArray();

        final StorageReference filePath;
        filePath = storageReference.child("Teacher").child(finalImage+"jpg"); // store img in firebase file path.
        final UploadTask uploadTask = filePath.putBytes(finalImage);   // for upload img .

        uploadTask.addOnCompleteListener(UpdateTeacherActivity.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {    // after complete.
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if (task.isSuccessful()){     // upload after task complete..
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {  // after success , uri will be download.

                                    downloadUrl = String.valueOf(uri);
                                    updateData(downloadUrl);

                                }
                            });
                        }
                    });

                }
                else {
//                    progressDialog.dismiss();
                    Toast.makeText(UpdateTeacherActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void openGallery(){  // image picker from gallery
        Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickImage, REQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ && resultCode == RESULT_OK){
            Uri uri = data.getData();  // it will get image from gallery
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri); // and convert in bitmap
            } catch (IOException e) {
                e.printStackTrace();
            }
            updateTeacherImg.setImageBitmap(bitmap);  // and set in ImageView.. icon

        }
    }
}