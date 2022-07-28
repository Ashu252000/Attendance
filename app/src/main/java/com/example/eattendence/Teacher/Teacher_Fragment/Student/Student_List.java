package com.example.eattendence.Teacher.Teacher_Fragment.Student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.eattendence.Login;
import com.example.eattendence.R;
import com.example.eattendence.Register.Register_Stud;
import com.example.eattendence.Teacher.Teacher_Activity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Student_List extends AppCompatActivity {

    FirebaseUser user;
    DatabaseReference ref;
    FirebaseDatabase fd;
    FirebaseAuth auth;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        Toolbar toolbar = findViewById(R.id.tool);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Student list");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Student_List.this, Teacher_Activity.class);
                startActivity(i);
            }
        });

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference();
        fd = FirebaseDatabase.getInstance();

        listView=findViewById(R.id.list);

        String id = user.getUid();

        ArrayList<String> array=new ArrayList<>();
        ArrayList<HashMap<String,String>> list=new ArrayList<HashMap<String, String>>(array.size());


        Intent intent=getIntent();
        String sem=intent.getStringExtra("semester");
        String sub=intent.getStringExtra("subject");
        String branch=intent.getStringExtra("branch");

        Log.d(sem,"semester");
        Log.d(branch,"branch");
        Log.d(sub,"subject");

        ref.child("Students").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterable<DataSnapshot> snapshotIterator = snapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                HashMap<String,String> map;
                while (iterator.hasNext()) {
                    DataSnapshot next = (DataSnapshot) iterator.next();

                 //   Log.i("sample", "Value = " + next.child("sub1").getValue());
                    Log.i("next",next.getKey());
                    Log.i("branch",next.child("branch").getValue().toString());
                    Log.i("sem",next.child("semester").getValue().toString());


                    if (next.child("branch").getValue().toString().equals(branch) && next.child("semester").getValue().toString().equals(sem)){
                        //array.add(next.getKey());
                        map = new HashMap<>();
                        map.put("line1",next.child("name").getValue().toString() );
                        Log.d("name",next.child("name").getValue().toString());
                        Log.d("phone",next.child("contact").getValue().toString());
                        map.put("line2", "Phone : " + next.child("contact").getValue().toString());
                        list.add(map);
                    }


                }

                String[] from={"line1","line2"};

                int[] to={android.R.id.text1,android.R.id.text2};

                SimpleAdapter adapter=new SimpleAdapter(Student_List.this,list,android.R.layout.simple_list_item_2,from,to);
                listView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
