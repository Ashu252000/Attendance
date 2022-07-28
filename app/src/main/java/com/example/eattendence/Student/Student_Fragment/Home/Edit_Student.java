package com.example.eattendence.Student.Student_Fragment.Home;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.eattendence.Login;
import com.example.eattendence.R;
import com.example.eattendence.Register.Register_Stud;
import com.example.eattendence.Student.StudentActivity;
import com.example.eattendence.model.Students;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class Edit_Student extends AppCompatActivity {

    EditText name,prn , phone , email;
    RadioGroup brch;
    RadioGroup year;
    Button submit;

    String br,em;
    FirebaseUser user;
    DatabaseReference ref,databaseReference;
    FirebaseDatabase fd;
    FirebaseAuth auth;

    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__student);

        toolbar = findViewById(R.id.tool);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Update Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Edit_Student.this,StudentActivity.class);
                startActivity(i);
            }
        });

        name=findViewById(R.id.studnm);
        prn=findViewById(R.id.prn);
        phone=findViewById(R.id.studcontact);
        email=findViewById(R.id.studemail);
        brch=findViewById(R.id.radio);
        year=findViewById(R.id.radioSem);
        submit=findViewById(R.id.submit);


        Intent i=getIntent();
        String uid = i.getStringExtra("id");

        auth= FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        ref= FirebaseDatabase.getInstance().getReference();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        fd=FirebaseDatabase.getInstance();

        ArrayList<String> branch=new ArrayList<>();




        ref.child("Students").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Students std=snapshot.getValue(Students.class);
                name.setText(std.getName());
                phone.setText(std.getContact());
                prn.setText(std.getPrn());
                email.setText(std.getEmail());
                em = (std.getEmail());
                String s = (std.getSemester());
                br =(std.getBranch());


                int z = year.getChildCount();
                for (int i=0;i<z;i++){
                    RadioButton rb = (RadioButton)year.getChildAt(i);
                    String y = rb.getText().toString();
                    if (y.equals(s)){
                        rb.setChecked(true);
                    }
                }
                /*int x = brch.getChildCount();
                for (int i=0;i<x;i++){
                    RadioButton rb = (RadioButton)brch.getChildAt(i);
                    String y = rb.getText().toString();
                    if (y.equals(br)){
                        rb.setChecked(true);
                    }
                }*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ref.child("Branch").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterable<DataSnapshot> snapshotIterator = snapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();

                while (iterator.hasNext()) {
                    DataSnapshot next = (DataSnapshot) iterator.next();
                    branch.add(next.getKey());
                    Log.d("Branch",next.getKey());
                }

                RadioButton[] rb =new RadioButton[branch.size()];

                for (int i=0;i<branch.size();i++){
                    rb[i]=new RadioButton(Edit_Student.this);
                    rb[i].setText(branch.get(i));
                    if(branch.get(i).equals(br)){
                        rb[i].setChecked(true);
                    }
                    rb[i].setId(View.generateViewId());
                    brch.addView(rb[i]);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nm=name.getText().toString().trim();

                String prnn=prn.getText().toString().trim();
                String cont=phone.getText().toString().trim();
                String e=email.getText().toString().trim();

                int id=year.getCheckedRadioButtonId();
                RadioButton rb=findViewById(id);
                String sem=rb.getText().toString();

                int id1=brch.getCheckedRadioButtonId();
                RadioButton rb1=findViewById(id1);
                String branch=rb1.getText().toString();

                if (!e.equals(em)){
                    user.updateEmail(e);
                }
                if(! branch.equals(br)){
                    databaseReference.child("Students/"+uid+"/Attendence").removeValue();
                }

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        databaseReference.child("Students").child(uid).child("name").setValue(nm);
                        databaseReference.child("Students").child(uid).child("prn").setValue(prnn);
                        databaseReference.child("Students").child(uid).child("contact").setValue(cont);
                        databaseReference.child("Students").child(uid).child("email").setValue(e);

                        databaseReference.child("Students").child(uid).child("semester").setValue(sem);
                        databaseReference.child("Students").child(uid).child("branch").setValue(branch);



                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                ProgressDialog progressDialog=new ProgressDialog(Edit_Student.this);
                progressDialog.setMessage("Loading.....");
                progressDialog.setTitle("Please Wait");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
                progressDialog.setCancelable(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(20000);
                        }catch (Exception e){

                        }
                        progressDialog.dismiss();
                    }
                }).start();
                Toast.makeText(Edit_Student.this, "Update Successfull", Toast.LENGTH_SHORT).show();
                Intent i=new Intent(Edit_Student.this, StudentActivity.class);
                startActivity(i);
            }
        });

    }
}