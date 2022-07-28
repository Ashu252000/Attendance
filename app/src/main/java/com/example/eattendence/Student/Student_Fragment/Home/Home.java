package com.example.eattendence.Student.Student_Fragment.Home;



//import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

//import com.example.attendence.DatabaseHelper;
import com.example.eattendence.R;
import com.example.eattendence.model.Students;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Home extends Fragment {

    //private HomeViewModel mViewModel;
    Activity context;
    //DatabaseHelper db;
    TextView name,contact,prn,sem,branch;
    Button edit;
    FirebaseUser user;
    DatabaseReference ref;
    FirebaseDatabase fd;
    FirebaseAuth auth;

    public static Home newInstance() {
        return new Home();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.student_home_fragment, container, false);
        context=getActivity();

        //db=new DatabaseHelper(context);
        name=view.findViewById(R.id.name);
        contact=view.findViewById(R.id.number);
        prn=view.findViewById(R.id.prn);
        sem=view.findViewById(R.id.sem);
        branch=view.findViewById(R.id.branch);
        edit=view.findViewById(R.id.edit);

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        ref=FirebaseDatabase.getInstance().getReference();
        fd=FirebaseDatabase.getInstance();

        String id=user.getUid();

        ref.child("Students").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Students std=snapshot.getValue(Students.class);
                name.setText(std.getName());
                contact.setText(std.getContact());
                prn.setText(std.getPrn());
                sem.setText(std.getSemester());
                branch.setText(std.getBranch());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(context, Edit_Student.class);
                i.putExtra("id",id);
                startActivity(i);
            }
        });
        return view;
    }

}