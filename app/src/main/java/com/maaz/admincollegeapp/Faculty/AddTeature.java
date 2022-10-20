package com.maaz.admincollegeapp.Faculty;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AddTeature extends AppCompatActivity {

    private ImageView addTeacherImg;
    private EditText addTeacherName, addTeacherEmail, addTeacherPost;
    private Spinner addTeacherCategory;
    private Button addTeacherBtn;

    private ProgressDialog progressDialog;

    private StorageReference storageReference;
    private DatabaseReference reference, DbRef;

    private final static int REQ = 1;
    private Bitmap bitmap = null;
    private String category;

    private String name, email, post, downloadUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teature);

        addTeacherImg = findViewById(R.id.addTeatureImg);
        addTeacherName = findViewById(R.id.addTeacherName);
        addTeacherEmail = findViewById(R.id.addTeacherEmail);
        addTeacherPost = findViewById(R.id.addTeacherPost);
        addTeacherCategory = findViewById(R.id.addTeacherCategory);
        addTeacherBtn = findViewById(R.id.addTeacherBtn);

        progressDialog = new ProgressDialog(this);

        reference = FirebaseDatabase.getInstance().getReference().child("Teacher");
        storageReference = FirebaseStorage.getInstance().getReference();



        addTeacherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidation();
            }
        });

        String[] items = new String[]{"Select Category", "Computer Science", "Mechanical", "Physics", "Chemistry"};  // spinner category array.
        addTeacherCategory.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items)); // set on spinner

        addTeacherCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {   // selected category
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category = addTeacherCategory.getSelectedItem().toString();    // get category
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

                Toast.makeText(AddTeature.this, "Nothing Selected", Toast.LENGTH_SHORT).show();
            }
        });


        addTeacherImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
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
            addTeacherImg.setImageBitmap(bitmap);  // and set in ImageView..

        }
    }

    private void checkValidation(){
        name = addTeacherName.getText().toString();
        email = addTeacherEmail.getText().toString();
        post = addTeacherPost.getText().toString();

        if (name.isEmpty()){
            addTeacherName.setError("Empty");
            addTeacherName.requestFocus();
        } else if (email.isEmpty()){
            addTeacherEmail.setError("Empty");
            addTeacherEmail.requestFocus();
        } else if (post.isEmpty()){
            addTeacherPost.setError("Empty");
            addTeacherPost.requestFocus();
        } else if (category.equals("Select Category")){
            Toast.makeText(this, "Please Select Category", Toast.LENGTH_SHORT).show();
        } else if (bitmap == null){
            progressDialog.setMessage("Uploading...");
            progressDialog.show();
            InsertData();
        } else{
            progressDialog.setMessage("Uploading...");
            progressDialog.show();
            uploadImage();
        }
    }

    private void InsertData(){
        DbRef = reference.child(category);
        final String uniqueKey = DbRef.push().getKey();


        TeacherData teacherData = new TeacherData(name, email, post, downloadUrl, uniqueKey);

        DbRef.child(uniqueKey).setValue(teacherData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Toast.makeText(AddTeature.this, "Teacher Added.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(AddTeature.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImage(){

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos); // compress image..
        byte[] finalImage = baos.toByteArray();

        final StorageReference filePath;
        filePath = storageReference.child("Teacher").child(finalImage+"jpg"); // store img in firebase file path.
        final UploadTask uploadTask = filePath.putBytes(finalImage);   // for upload img .

        uploadTask.addOnCompleteListener(AddTeature.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {    // after complete.
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
                                    InsertData();

                                }
                            });
                        }
                    });

                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(AddTeature.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}