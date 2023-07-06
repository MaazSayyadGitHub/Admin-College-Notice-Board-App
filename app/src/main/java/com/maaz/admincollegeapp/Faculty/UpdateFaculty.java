package com.maaz.admincollegeapp.Faculty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maaz.admincollegeapp.R;

import java.util.ArrayList;
import java.util.List;

public class UpdateFaculty extends AppCompatActivity {

    FloatingActionButton fab;
    private RecyclerView csDepartment, mechanicalDepartment, physicsDepartment, chemistryDepartment;
    private LinearLayout csNoData, mechanicalNoData, physicsNoData, chemistryNoData;

    private List<TeacherData> list1, list2, list3, list4;
    private DatabaseReference reference, DbRef;

    private TeacherAdapters adapters;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_faculty);

        fab = findViewById(R.id.fab);

        csDepartment = findViewById(R.id.CsDepartment);
        mechanicalDepartment = findViewById(R.id.mechanicalDepartment);
        physicsDepartment = findViewById(R.id.physicsDepartment);
        chemistryDepartment = findViewById(R.id.chemistryDepartment);

        csNoData = findViewById(R.id.csNoData);
        mechanicalNoData = findViewById(R.id.mechanicalNoData);
        physicsNoData = findViewById(R.id.physicsNoData);
        chemistryNoData = findViewById(R.id.chemistryNoData);

        reference = FirebaseDatabase.getInstance().getReference().child("Teacher");

        // call individually
        csDepartment();
        mechanicalDepartment();
        physicsDepartment();
        chemistryDepartment();

        // to go Add Teacher Activity
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UpdateFaculty.this, AddTeature.class));
            }
        });
    }

    private void csDepartment() {
        DbRef = reference.child("Computer Science");
        // to get data from realtime database
        DbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list1 = new ArrayList<>();

                if (!snapshot.exists()){    // if data is not exists here then visibility csNoData will display
                    csNoData.setVisibility(View.VISIBLE);
                    csDepartment.setVisibility(View.GONE);   // and csDepartment will not display
                } else {
                    csNoData.setVisibility(View.GONE);         // if data exists then csDepartment will display
                    csDepartment.setVisibility(View.VISIBLE);   // and csNoData Visibility Gone.
                    for (DataSnapshot snapshot1: snapshot.getChildren()){
                        TeacherData data = snapshot1.getValue(TeacherData.class); // convert all data to TeacherData object
                        list1.add(data); // add all data to list1.
                    }
                    csDepartment.setHasFixedSize(true);     // for recyclerView size fixed.
                    csDepartment.setLayoutManager(new LinearLayoutManager(UpdateFaculty.this));
                    // category to pass update Activity
                    adapters = new TeacherAdapters(list1, UpdateFaculty.this, "Computer Science");
                    csDepartment.setAdapter(adapters);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateFaculty.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mechanicalDepartment() {
        DbRef = reference.child("Mechanical");
        // to get data from realtime database
        DbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list2 = new ArrayList<>();

                if (!snapshot.exists()){    // if data is not exists here then visibility csNoData will display
                    mechanicalNoData.setVisibility(View.VISIBLE);
                    mechanicalDepartment.setVisibility(View.GONE);   // and csDepartment will not display
                } else {
                    mechanicalNoData.setVisibility(View.GONE);         // if data exists then csDepartment will display
                    mechanicalDepartment.setVisibility(View.VISIBLE);   // and csNoData Visibility Gone.
                    for (DataSnapshot snapshot1: snapshot.getChildren()){
                        TeacherData data = snapshot1.getValue(TeacherData.class);
                        list2.add(data);
                    }
                    mechanicalDepartment.setHasFixedSize(true);     // for fixed recyclerview Size
                    mechanicalDepartment.setLayoutManager(new LinearLayoutManager(UpdateFaculty.this));
                    adapters = new TeacherAdapters(list2, UpdateFaculty.this, "Mechanical");
                    mechanicalDepartment.setAdapter(adapters);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateFaculty.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void physicsDepartment() {
        DbRef = reference.child("Physics");
        // to get data from realtime database
        DbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list3 = new ArrayList<>();
                if (!snapshot.exists()){    // if data is not exists here then visibility csNoData will display
                    physicsNoData.setVisibility(View.VISIBLE);
                    physicsDepartment.setVisibility(View.GONE);   // and csDepartment will not display
                } else {
                    physicsNoData.setVisibility(View.GONE);         // if data exists then csDepartment will display
                    physicsDepartment.setVisibility(View.VISIBLE);   // and csNoData Visibility Gone.
                    for (DataSnapshot snapshot1: snapshot.getChildren()){
                        TeacherData data = snapshot1.getValue(TeacherData.class);
                        list3.add(data);
                    }
                    physicsDepartment.setHasFixedSize(true);     // fixed size of Recyclerview
                    physicsDepartment.setLayoutManager(new LinearLayoutManager(UpdateFaculty.this));
                    adapters = new TeacherAdapters(list3, UpdateFaculty.this, "Physics");
                    physicsDepartment.setAdapter(adapters);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateFaculty.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void chemistryDepartment() {
        DbRef = reference.child("Chemistry");

        // to get data from realtime database
        DbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list4 = new ArrayList<>();
                if (!snapshot.exists()){    // if data is not exists here then visibility csNoData will display
                    chemistryNoData.setVisibility(View.VISIBLE);
                    chemistryDepartment.setVisibility(View.GONE);   // and csDepartment will not display
                } else {
                    chemistryNoData.setVisibility(View.GONE);         // if data exists then csDepartment will display
                    chemistryDepartment.setVisibility(View.VISIBLE);   // and csNoData Visibility Gone.
                    for (DataSnapshot snapshot1: snapshot.getChildren()){
                        TeacherData data = snapshot1.getValue(TeacherData.class);
                        list4.add(data);
                    }
                    chemistryDepartment.setHasFixedSize(true);     // for fixing size of recyclerview
                    chemistryDepartment.setLayoutManager(new LinearLayoutManager(UpdateFaculty.this));
                    adapters = new TeacherAdapters(list4, UpdateFaculty.this, "Chemistry");
                    chemistryDepartment.setAdapter(adapters);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateFaculty.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}