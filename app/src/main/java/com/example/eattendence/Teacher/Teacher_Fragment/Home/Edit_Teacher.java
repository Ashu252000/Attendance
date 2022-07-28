package com.example.eattendence.Teacher.Teacher_Fragment.Home;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eattendence.R;
import com.example.eattendence.Student.StudentActivity;
import com.example.eattendence.Student.Student_Fragment.Home.Edit_Student;
import com.example.eattendence.Teacher.Teacher_Activity;
import com.example.eattendence.model.Teachers;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class Edit_Teacher extends AppCompatActivity {

    EditText name,prn , phone , email;
    RadioGroup brch;
    Button submit;

    String br,em;
    FirebaseUser user;
    DatabaseReference ref,databaseReference;
    FirebaseDatabase fd;
    FirebaseAuth auth;

    Toolbar toolbar;
    ArrayList<String> s=new ArrayList<>();

    ArrayList<String> check=new ArrayList<>();
    ArrayList<String> alreadySub=new ArrayList<>();

    CheckBox[] checkBoxes;
    LinearLayout list;
    ArrayList<String> subj;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__teacher);

        context=this;
        toolbar = findViewById(R.id.tool);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Update Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Edit_Teacher.this, Teacher_Activity.class);
                startActivity(i);
            }
        });

        name=findViewById(R.id.teacherfirstnm);
        phone=findViewById(R.id.teachercontact);
        email=findViewById(R.id.teacheremail);
        brch=findViewById(R.id.radio);
        list=findViewById(R.id.layout);
        submit=findViewById(R.id.submit);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference();
        fd = FirebaseDatabase.getInstance();

        String id = user.getUid();


        ref.child("Teachers").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Teachers std = snapshot.getValue(Teachers.class);
                name.setText(std.getName());
                phone.setText(std.getContact());
                email.setText(std.getEmail());
                em=std.getEmail();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        subj = new ArrayList<>();

        ref.child("Teachers").child(id).child("subjects").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterable<DataSnapshot> snapshotIterator = snapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();

                while (iterator.hasNext()) {
                    DataSnapshot next = (DataSnapshot) iterator.next();
                    subj.add(next.getValue().toString());
                    Log.d("subject",next.getValue().toString());
                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        databaseReference=FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Teachers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterable<DataSnapshot> snapshotIterator = snapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                while (iterator.hasNext()){
                    DataSnapshot next=(DataSnapshot) iterator.next();

                    Iterable<DataSnapshot> snapshotIterator1 = next.child("subjects").getChildren();
                    Iterator<DataSnapshot> iterator1 = snapshotIterator1.iterator();

                    while (iterator1.hasNext()){
                        DataSnapshot next1=(DataSnapshot) iterator1.next();
                        alreadySub.add(next1.getValue().toString());
                        Log.d("",next1.getValue().toString());
                        //Log.d("",next1.getKey());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Log.d("already subject array", String.valueOf(alreadySub));

        databaseReference.child("Branch").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterable<DataSnapshot> snapshotIterator = snapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();

                while (iterator.hasNext()) {
                    DataSnapshot next = (DataSnapshot) iterator.next();

                    for (int i=1;i<=next.getChildrenCount();i++){
                        String y=i+" Sem";

                        for (int n=1;n<next.child(y).getChildrenCount();n++){
                            String x="sub"+n;
                            if (!(next.child(y).child(x).getValue() ==null)) {
                                if (!(alreadySub.contains((String) next.child(y).child(x).getValue()))) {
                                    if (!(s.contains((String) next.child(y).child(x).getValue()))) {
                                        s.add(next.child(y).child(x).getValue().toString());
                                    }
                                }

                            }
                        }


                    }

                }

                s.addAll(subj);
                Collections.sort(s);
                LinearLayout linearLayout=findViewById(R.id.layout);
                //CheckBox[] checkBoxes;
                checkBoxes=new CheckBox[s.size()];

                Log.d("Array size", String.valueOf(s.size()));
                Log.d("Checkbox array size", String.valueOf(checkBoxes.length));

                //int x=0;
                for (int i=0;i<s.size() ;i++)
                {
                    checkBoxes[i] = new CheckBox(Edit_Teacher.this);
                    checkBoxes[i].setText((String) s.get(i));
                   if (subj.contains(s.get(i))){
                       checkBoxes[i].setChecked(true);
                   }
                    int finalI = i;
                    checkBoxes[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if (checkBoxes[finalI].isChecked()){
                                check.add(checkBoxes[finalI].getText().toString());
                            }
                        }
                    });
                    linearLayout.addView(checkBoxes[i]);
                    //Log.d("Checkboxes", checkBoxes[i].getText().toString());

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nm = name.getText().toString().trim();
                String p = phone.getText().toString().trim();
                String e = email.getText().toString().trim();

                if (!e.equals(em)) {
                    user.updateEmail(e);
                }
                FirebaseUser firebaseUser = auth.getCurrentUser();

                //databaseReference.child("Students").child(fuser.getUid());
               /* databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {*/
                        databaseReference.child("Teachers").child(firebaseUser.getUid()).child("name").setValue(nm);
                        databaseReference.child("Teachers").child(firebaseUser.getUid()).child("contact").setValue(p);
                        databaseReference.child("Teachers").child(firebaseUser.getUid()).child("email").setValue(e);

                        databaseReference.child("Teachers/"+firebaseUser.getUid()+"/subjects").removeValue();
                        for (int i = 0; i < checkBoxes.length; i++) {
                            if (checkBoxes[i].isChecked()) {
                                databaseReference.child("Teachers").child(firebaseUser.getUid()).child("subjects").child(String.valueOf(i + 1)).setValue(checkBoxes[i].getText());
                                Log.d("data is saving", checkBoxes[i].getText().toString());
                            }

                        }

                    /*}

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });*/
                ProgressDialog progressDialog=new ProgressDialog(Edit_Teacher.this);
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
                Toast.makeText(Edit_Teacher.this, "Update Successfull", Toast.LENGTH_SHORT).show();
                Intent i=new Intent(Edit_Teacher.this, Teacher_Activity.class);
                startActivity(i);
            }
        });


    }
    public void onClick(View view) {
        for (CheckBox checkBox : checkBoxes) {

            if (checkBox.isChecked()) {

                if (!check.contains(checkBox.getText().toString())) {
                    check.add(checkBox.getText().toString());
                }
                Log.d("Check box",checkBox.getText().toString());
                //Log.d("Checked box", String.valueOf(check));
            }
            Log.d("c", String.valueOf(check));
            Log.d(String.valueOf(checkBox.isSelected()), checkBox.getText().toString());
        }

    }
}