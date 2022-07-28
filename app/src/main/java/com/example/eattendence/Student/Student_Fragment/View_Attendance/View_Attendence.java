package com.example.eattendence.Student.Student_Fragment.View_Attendance;

import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eattendence.R;
import com.example.eattendence.Student.Student_Fragment.Home.Home;
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
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Locale;

public class View_Attendence extends Fragment {
    Activity context;
    private EditText start,end;
    Button submit;
    Calendar mycalendar;
    Date startD=new Date();
    Date endD=new Date();
    FirebaseUser user;
    DatabaseReference ref,ref1;
    FirebaseDatabase fd;
    FirebaseAuth auth;
    String sem,branch;

    public static View_Attendence newInstance() {
        return new View_Attendence();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.student_view_attend_fragment, container, false);
        context = getActivity();

        start=view.findViewById(R.id.startdate);
        end=view.findViewById(R.id.enddate);
        submit=view.findViewById(R.id.submit);

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        ref=FirebaseDatabase.getInstance().getReference();
        ref1=FirebaseDatabase.getInstance().getReference();
        fd=FirebaseDatabase.getInstance();

        String id=user.getUid();

        mycalendar= Calendar.getInstance();
        Date time=Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        String t=df.format(time);
        startD=time;
        endD=time;
        start.setText(t);
        end.setText(t);
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mycalendar.set(Calendar.YEAR,year);
                mycalendar.set(Calendar.MONTH,month);
                mycalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                updateLabel();
            }
        };
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(context,date,mycalendar.get(Calendar.YEAR),mycalendar.get(Calendar.MONTH),mycalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        mycalendar=Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener dateSetListener= new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mycalendar.set(Calendar.YEAR,year);
                mycalendar.set(Calendar.MONTH,month);
                mycalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                updateDate();
            }
        };
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(context,dateSetListener,mycalendar.get(Calendar.YEAR),mycalendar.get(Calendar.MONTH),mycalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        final Spinner sp=view.findViewById(R.id.spinner);
        ArrayList<String> arrayList=new ArrayList<>();
        arrayList.add("Select Subject");

        ref.child("Students").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sem=snapshot.child("semester").getValue().toString();
                branch=snapshot.child("branch").getValue().toString();

                ref1.child("Branch").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Iterable<DataSnapshot> snapshotIterator = snapshot.getChildren();
                        Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                        OuterLoop:
                        while (iterator.hasNext()) {
                            DataSnapshot next2 = (DataSnapshot) iterator.next();
                            //Log.d("",next2.getKey());
                            for (int i = 1; i <= next2.getChildrenCount(); i++) {
                                String y = i + " Sem";

                                for (int n = 1; n <= next2.child(y).getChildrenCount(); n++) {
                                    String x = "sub" + n;

                                    if (next2.child(y).getKey().equals(sem) && next2.getKey().equals(branch)) {

                                        Log.d("",next2.child(y).child(x).getValue().toString());
                                        arrayList.add(next2.child(y).child(x).getValue().toString());
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        /*arrayList.addAll(db.getParticularStudentCourse(user));*/

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,arrayList){
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

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String startdt=start.getText().toString();
                String enddt=end.getText().toString();
                ArrayList date = getDateArray();
                String selected_sub = sp.getSelectedItem().toString();

                Intent i=new Intent(context,Student_Attendences.class);
                i.putExtra("date",date);
                i.putExtra("subject",selected_sub);
                i.putExtra("Start Date",startdt);
                i.putExtra("End Date",enddt);
                startActivity(i);
            }
        });

        return view;
    }

    private void updateDate() {
        String myFormat = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        endD=mycalendar.getTime();
        end.setText(sdf.format(mycalendar.getTime()));
    }

    private void updateLabel() {
        String myFormat = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        startD=mycalendar.getTime();
        start.setText(sdf.format(mycalendar.getTime()));
    }

    private ArrayList<String> getDateArray() {
        ArrayList<String> arrayList=new ArrayList<>();
        Calendar calendar=new GregorianCalendar();
        calendar.setTime(startD);
        Calendar endcalendar=new GregorianCalendar();
        endcalendar.setTime(endD);
        String myFormat = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        Log.d("start date", sdf.format(calendar.getTime()));
        Log.d("end date",sdf.format(endcalendar.getTime()));

        if (calendar.equals(endcalendar)){
            arrayList.add(sdf.format(calendar.getTime()));
        }else {
            while (calendar.before(endcalendar)) {
                String date = sdf.format(calendar.getTime());
                Log.d("D", date);
                arrayList.add(date);
                calendar.add(Calendar.DATE, 1);
            }
            arrayList.add(sdf.format(endcalendar.getTime()));
        }
        Log.d("getDateArray",arrayList.toString());
        return arrayList;
    }
}
