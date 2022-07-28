package com.example.eattendence.Teacher.Teacher_Fragment.View_Attendence;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.eattendence.R;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

public class View_Attendence extends Fragment {

    Context context;
    private EditText start,end;
    private Button subjectButton,studentButton;
    Calendar mycalendar;
    Calendar cal;
    ArrayList<String> subj = new ArrayList<>();
    ArrayList<String> studnm = new ArrayList<>();
    ArrayList<String> uid = new ArrayList<>();
    ArrayList<String> arrayList = new ArrayList<>();
    Date startD=new Date();
    Date endD=new Date();

    FirebaseUser user;
    DatabaseReference ref,ref1,ref2;
    FirebaseDatabase fd;
    FirebaseAuth auth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.teacher_view_attend_fragment, container, false);
        context = getActivity();

        start=view.findViewById(R.id.startdate);
        end=view.findViewById(R.id.enddate);
        subjectButton =(Button) view.findViewById(R.id.course_attendence);
        studentButton=view.findViewById(R.id.student_attendence);

        mycalendar=Calendar.getInstance();
        Date time=Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        String t=df.format(time);
        start.setText(t);
        end.setText(t);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference();
        ref1 = FirebaseDatabase.getInstance().getReference();
        ref2 = FirebaseDatabase.getInstance().getReference();
        fd = FirebaseDatabase.getInstance();

        String id=user.getUid();

        ref.child("Teachers").child(id).child("subjects").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterable<DataSnapshot> snapshotIterator = snapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();

                while (iterator.hasNext()) {
                    DataSnapshot next = (DataSnapshot) iterator.next();
                    subj.add(next.getValue().toString());
                    Log.d("subject",next.getValue().toString());

                    ref2.child("Branch").addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Iterable<DataSnapshot> snapshotIterator = snapshot.getChildren();
                            Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                            OuterLoop:
                            while (iterator.hasNext()) {

                                DataSnapshot next2 = (DataSnapshot) iterator.next();
                                for (int i = 1; i <= next2.getChildrenCount(); i++) {
                                    String y = i + " Sem";

                                    for (int n = 1; n < next2.child(y).getChildrenCount(); n++) {
                                        String x = "sub" + n;

                                        if ((next2.child(y).child(x).getValue().toString().equals(next.getValue().toString()))) {
                                            Log.d(y + " Done", next2.child(y).child(x).getValue().toString());

                                            HashMap<String,String> map=new HashMap<>();
                                            map.put("sem",y);
                                            map.put("branch",next2.getKey());

                                            Log.d("matched", next2.child(y).child(x).getValue().toString());
                                            Log.d("matched", y);
                                            Log.d("matched", next2.getKey());

                                            ref1.child("Students").addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    Iterable<DataSnapshot> snapshotIterator = snapshot.getChildren();
                                                    Iterator<DataSnapshot> iterator = snapshotIterator.iterator();

                                                    while (iterator.hasNext()) {
                                                        DataSnapshot next1 = (DataSnapshot) iterator.next();

                                                        if (next1.child("branch").getValue().toString().equals(map.get("branch")) && next1.child("semester").getValue().toString().equals(map.get("sem"))) {
                                                            if (!studnm.contains(next1.child("name").getValue().toString())) {
                                                                studnm.add(next1.child("name").getValue().toString());
                                                                uid.add(next1.child("uid").getValue().toString());
                                                                Log.d("Students", next1.child("name").getValue().toString());
                                                            }

                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        Log.d("Print student",studnm.toString());
        Log.d("Print Subject",subj.toString());
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

        cal=Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener dateSetListener= new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                cal.set(Calendar.YEAR,year);
                cal.set(Calendar.MONTH,month);
                cal.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                updateDate();
            }
        };
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(context,dateSetListener,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        subjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                LayoutInflater inflater = (LayoutInflater) view1.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                final View popupview = inflater.inflate(R.layout.select_subject_popup, null);

                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setView(popupview);
                builder.setCancelable(false);
                arrayList=getDateArray();

                final CheckBox[] c=new CheckBox[subj.size()];
                //final RadioGroup rg=popupview.findViewById(R.id.rgcourse);
                LinearLayout layout=popupview.findViewById(R.id.linear);

                if (subj!=null)
                {

                    for (int i=0;i<c.length;i++){
                        c[i]=new CheckBox(context);
                    }
                    for (int i=0;i<subj.size();i++){
                        c[i].setText(subj.get(i).toString());
                        layout.addView(c[i]);
                        Log.d("Check Box",c[i].getText().toString());
                    }
                }
                final ArrayList<String> subject=new ArrayList<>();

                builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int x)
                    {
                        for (int i=0;i<c.length;i++){
                            if (c[i].isChecked()){
                                subject.add(c[i].getText().toString());
                            }
                        }

                        dialog.dismiss();
                        if (subject != null){
                            Intent intent = new Intent(context, AttendeceSubject.class);
                            intent.putExtra("subject Name",subject);
                            intent.putExtra("Start Date",start.getText().toString());
                            intent.putExtra("End Date",end.getText().toString());
                            intent.putExtra("Date",arrayList);
                            startActivity(intent);
                        }else {
                            Toast.makeText(context, "Select subject", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                AlertDialog alertDialog = builder.create();

                alertDialog.show();
                alertDialog.getButton(Dialog.BUTTON_POSITIVE).setBackgroundColor(Color.parseColor("#FE22B3E8"));
                alertDialog.getButton(Dialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
            }
        });

        studentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                final View popupview = inflater.inflate(R.layout.select_student_popup, null);

                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setView(popupview);
                builder.setCancelable(false);

                arrayList=getDateArray();

                final CheckBox[] c=new CheckBox[studnm.size()];
                LinearLayout layout=popupview.findViewById(R.id.linear);

                if (studnm!=null)
                {

                    for (int i=0;i<c.length;i++){
                        c[i]=new CheckBox(context);
                    }
                    for (int i=0;i<studnm.size();i++){
                        c[i].setText(studnm.get(i).toString());
                        layout.addView(c[i]);
                        Log.d("Check Box",c[i].getText().toString());
                    }
                }
                final ArrayList<String> stud=new ArrayList<>();

                builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int j) {
                        for (int i=0;i<c.length;i++){
                            if (c[i].isChecked()){
                                stud.add(c[i].getText().toString());
                            }
                        }

                        dialogInterface.dismiss();
                        if (stud != null){
                            Intent intent = new Intent(context, AttendenceStudent.class);
                            intent.putExtra("student Name",stud);
                            intent.putExtra("uid",uid);
                            intent.putExtra("Start Date",start.getText().toString());
                            intent.putExtra("End Date",end.getText().toString());
                            intent.putExtra("Date",arrayList);
                            startActivity(intent);
                        }else {
                            Toast.makeText(context, "Select student", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                AlertDialog alertDialog = builder.create();

                alertDialog.show();
                alertDialog.getButton(Dialog.BUTTON_POSITIVE).setBackgroundColor(Color.parseColor("#FE22B3E8"));
                alertDialog.getButton(Dialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);

            }
        });

        return view;
    }

    private void updateDate() {
        String myFormat = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        end.setText(sdf.format(cal.getTime()));
        endD=cal.getTime();
        Log.d("endD",sdf.format(endD));
    }

    private void updateLabel() {
        String myFormat = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        start.setText(sdf.format(mycalendar.getTime()));
        startD=mycalendar.getTime();
        Log.d("startD",sdf.format(startD));
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
