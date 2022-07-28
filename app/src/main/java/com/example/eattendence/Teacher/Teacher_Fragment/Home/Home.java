package com.example.eattendence.Teacher.Teacher_Fragment.Home;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.eattendence.R;
import com.example.eattendence.model.Students;
import com.example.eattendence.model.Teachers;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class Home extends Fragment {

    //private HomeViewModel mViewModel;
    Activity context;
    //DatabaseHelper db;
    TextView name, contact, prn, sem, branch;
    Button submit;
    FirebaseUser user;
    DatabaseReference ref;
    FirebaseDatabase fd;
    FirebaseAuth auth;

    LinearLayout list;

    public static com.example.eattendence.Student.Student_Fragment.Home.Home newInstance() {
        return new com.example.eattendence.Student.Student_Fragment.Home.Home();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.teacher_home_fragment, container, false);
        context = getActivity();

        //db=new DatabaseHelper(context);
        name = view.findViewById(R.id.name);
        contact = view.findViewById(R.id.number);
        list=view.findViewById(R.id.list);

        submit=view.findViewById(R.id.edit);

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
                contact.setText(std.getContact());



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ArrayList<String> subj = new ArrayList<>();

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

                TextView[] rb =new TextView[subj.size()];
                TextView[] rb1 =new TextView[subj.size()];

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                //params.setMargins(0,0,0,20);
                params.setMarginEnd(20);

                LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                //params.setMargins(0,0,0,20);
                param1.setMargins(0,0,0,20);
                param1.setMarginEnd(20);

                for (int i=0;i<subj.size();i++){
                    rb[i]=new TextView(context);
                    rb1[i]=new TextView(context);
                    rb[i].setLayoutParams(param);
                    rb1[i].setLayoutParams(params);
                    String x= String.valueOf(i+1);
                    rb1[i].setText(x);
                    rb[i].setId(View.generateViewId());
                    rb[i].setTextSize(20);
                    rb1[i].setTextSize(20);
                    rb[i].setText(subj.get(i));


                    LinearLayout l=new LinearLayout(context);
                    l.setLayoutParams(param1);
                    l.setOrientation(LinearLayout.HORIZONTAL);
                    l.addView(rb1[i]);
                    l.addView(rb[i]);

                    list.addView(l);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(context,Edit_Teacher.class);
                i.putExtra("id",id);
                startActivity(i);
            }
        });
        return view;
    }
}
