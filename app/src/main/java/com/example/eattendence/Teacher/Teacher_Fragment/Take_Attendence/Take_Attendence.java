package com.example.eattendence.Teacher.Teacher_Fragment.Take_Attendence;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eattendence.R;
import com.example.eattendence.Teacher.Teacher_Fragment.Student.Student_List;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class Take_Attendence extends Fragment {

    Context context;
    Spinner sp;
    Button button;
    FirebaseUser user;
    DatabaseReference ref;
    FirebaseDatabase fd;
    FirebaseAuth auth;

    public static com.example.eattendence.Teacher.Teacher_Fragment.Take_Attendence.Take_Attendence newInstance() {
        return new com.example.eattendence.Teacher.Teacher_Fragment.Take_Attendence.Take_Attendence();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.teacher_take_attend_fragment, container, false);
        context = getActivity();

        sp=view.findViewById(R.id.spinner);
        button=view.findViewById(R.id.button);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference();
        fd = FirebaseDatabase.getInstance();

        String id = user.getUid();

        ArrayList<String> subj=new ArrayList<>();
        subj.add("Select Subject");

        ref.child("Teachers").child(id).child("subjects").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterable<DataSnapshot> snapshotIterator = snapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();

                while (iterator.hasNext()) {
                    DataSnapshot next = (DataSnapshot) iterator.next();
                    subj.add(next.getValue().toString());
                    Log.d("subject",next.getValue().toString());
                }


                Log.d("Subject Array", String.valueOf(subj));
                ArrayAdapter<String> adapter=new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,subj){
                    @Override
                    public boolean isEnabled(int position)
                    {
                        if (position == 0){
                            return false;
                        }else {
                            return true;
                        }
                    }

                    @Override
                    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View downView=super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) downView;
                        if (position == 0){
                            tv.setTextColor(Color.GRAY);
                        }else {
                            tv.setTextColor(Color.BLACK);
                        }
                        return downView;
                    }
                };
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selectSub=sp.getSelectedItem().toString();

                if (!selectSub.isEmpty()) {
                    Log.d("selected Subject", selectSub);

                    //Intent i =new Intent(Take_Attendence.this,Attedence_Take.class);
                    DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference();

                    ref1.child("Branch").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Iterable<DataSnapshot> snapshotIterator = snapshot.getChildren();
                            Iterator<DataSnapshot> iterator = snapshotIterator.iterator();

                            OuterLoop:
                            while (iterator.hasNext()) {
                                DataSnapshot next = (DataSnapshot) iterator.next();

                                Log.i("sample", "Value = " + next.child("sub1").getValue());
                                Log.i("next",next.getKey());


                                for (int i=1;i<=next.getChildrenCount();i++){
                                    String y=i+" Sem";

                                    Log.d("Semester",y);

                                    for (int n=1;n<next.child(y).getChildrenCount();n++){
                                        String x="sub"+n;
                                        Log.d("Subject",x);
                                        if ((next.child(y).child(x).getValue().toString().equals(selectSub))) {
                                            Log.d( y+" Done", next.child(y).child(x).getValue().toString());
                                            Intent intent=new Intent(context, Attedence_Take.class);
                                            intent.putExtra("subject",next.child(y).child(x).getValue().toString());
                                            intent.putExtra("semester",y);
                                            intent.putExtra("branch",next.getKey());
                                            startActivity(intent);
                                            break OuterLoop;

                                        }
                                    }


                                }

                            }

                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }else {
                    Toast.makeText(context, "Please Select Subject", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}
