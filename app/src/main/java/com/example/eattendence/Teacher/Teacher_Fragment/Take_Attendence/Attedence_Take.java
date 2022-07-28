package com.example.eattendence.Teacher.Teacher_Fragment.Take_Attendence;

import androidx.annotation.NonNull;
//import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eattendence.R;
import com.example.eattendence.Register.Register_Stud;
import com.example.eattendence.Teacher.Teacher_Activity;
import com.example.eattendence.Teacher.Teacher_Fragment.Student.Student_List;
import com.example.eattendence.model.CustomAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

public class Attedence_Take extends AppCompatActivity {

    FirebaseUser user;
    DatabaseReference ref;
    FirebaseDatabase fd;
    FirebaseAuth auth;
    ListView listView;
    Button button;
    Context context;
    ArrayList<String> attendence;

    String sem;
    String branch;
    String sub;
    ProgressDialog progressDialog;
    String t;

    ArrayList<String> studnm =new ArrayList<>();
    ArrayList<String> prn =new ArrayList<>();
    ArrayList<String> uid =new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attedence_take);

        if (savedInstanceState == null) {

            Toolbar toolbar = findViewById(R.id.tool);

            //context=getApplicationContext();

            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Take Attendence");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Attedence_Take.this, Teacher_Activity.class);
                    startActivity(i);
                }
            });

            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();
            ref = FirebaseDatabase.getInstance().getReference();
            fd = FirebaseDatabase.getInstance();

            listView = findViewById(R.id.list);
            button = findViewById(R.id.button);

            String id = user.getUid();

            Intent intent = getIntent();
            sem = intent.getStringExtra("semester");
            sub = intent.getStringExtra("subject");
            branch = intent.getStringExtra("branch");

            Date time = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            t = df.format(time);

            Log.d(sem, "semester");
            Log.d(branch, "branch");
            Log.d(sub, "subject");


            ref.child("Students").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Iterable<DataSnapshot> snapshotIterator = snapshot.getChildren();
                    Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                    HashMap<String, String> map;

                    while (iterator.hasNext()) {
                        DataSnapshot next = (DataSnapshot) iterator.next();

                        if (next.child("branch").getValue().toString().equals(branch) && next.child("semester").getValue().toString().equals(sem)) {

                            studnm.add("Name : " + next.child("name").getValue().toString());
                            prn.add("PRN NO : " + next.child("prn").getValue().toString());
                            uid.add(next.child("uid").getValue().toString());

                        }
                    }
                    CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), studnm, prn);
                    listView.setAdapter(customAdapter);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    attendence = CustomAdapter.array;

                    if (attendence.contains("Not Attempted"))
                    {
                        Toast.makeText(Attedence_Take.this, "Attempt All", Toast.LENGTH_SHORT).show();
                    } else {

                        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                        final View popupview = inflater.inflate(R.layout.popup_window, null);

                        TextView t1 = popupview.findViewById(R.id.text1);
                        TextView t2 = popupview.findViewById(R.id.text2);

                        int present = 0;
                        int absent = 0;

                        for (int i = 0; i < attendence.size(); i++) {
                            if (attendence.get(i) == "Present") {
                                present++;
                            }
                            if (attendence.get(i) == "Absent") {
                                absent++;
                            }

                        }

                        t1.setText(String.valueOf(present));
                        t2.setText(String.valueOf(absent));

                        final AlertDialog.Builder builder = new AlertDialog.Builder(Attedence_Take.this);
                        builder.setView(popupview);
                        builder.setCancelable(false);
                        builder.setPositiveButton("Submit", (dialog, which) -> {

                            DatabaseReference mref = FirebaseDatabase.getInstance().getReference();

                            for (int i = 0; i < uid.size(); i++) {

                                mref.child("Students/"+uid.get(i)+"/Attendence/"+sub+"/"+t).setValue(attendence.get(i));


                            }

                            progressDialog=new ProgressDialog(Attedence_Take.this);
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
                                    //progressDialog.dismiss();
                                    if (progressDialog != null) {
                                        progressDialog.dismiss();
                                        progressDialog = null;
                                    }
                                }
                            }).start();

                            dialog.dismiss();
                            Toast.makeText(Attedence_Take.this, "Attendence Submitted", Toast.LENGTH_SHORT).show();

                            Intent intent1 = new Intent(Attedence_Take.this, Teacher_Activity.class);
                            startActivity(intent1);




                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.cancel();
                            }
                        });

                        AlertDialog alertDialog = builder.create();

                        alertDialog.show();
                    }

                }
            });

        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}