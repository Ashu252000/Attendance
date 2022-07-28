package com.example.eattendence.model;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.eattendence.R;
import com.example.eattendence.Teacher.Teacher_Activity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;
import java.util.Iterator;

public class AttendenceAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> date;
    ArrayList<String> student,uid;
    LayoutInflater inflater;
    FirebaseUser user;
    DatabaseReference ref,ref1;
    FirebaseDatabase fd;
    int rowCount;
    FirebaseAuth auth;

    CellStyle cellStyle;;
    Workbook wb;
    Cell cell = null;
    Row r1;
    Sheet sheet;
    String r;

    public AttendenceAdapter(Context applicationContext, ArrayList<String> arrayList, ArrayList<String> student , ArrayList<String> uid, Workbook wb, Sheet sheet,String r) {
        context=applicationContext;
        inflater = (LayoutInflater.from(applicationContext));
        this.date =arrayList;
        this.uid=uid;
        this.wb=wb;
        this.r=r;
        this.sheet=sheet;
        this.student=student;

        cellStyle=wb.createCellStyle();
        cellStyle.setFillForegroundColor(HSSFColor.LIGHT_BLUE.index);
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        Row row=sheet.createRow(0);
        cell =row.createCell(0);
        cell.setCellValue(r);
        cell.setCellStyle(cellStyle);

        rowCount = 2;
    }

    @Override
    public int getCount() {
        return student.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int z, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.list1,null);

        TextView t1=view.findViewById(R.id.text1);
        t1.setText(student.get(z));

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference();
        ref1 = FirebaseDatabase.getInstance().getReference();
        fd = FirebaseDatabase.getInstance();

        TableRow.LayoutParams l = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        l.setMargins(0, 20, 0, 0);

        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(30, TableRow.LayoutParams.WRAP_CONTENT);
        TableLayout tableLayout = view.findViewById(R.id.table);
        ArrayList<String> teacherSub=new ArrayList<>();
        ArrayList<String> studentAttendeSub=new ArrayList<>();

        ref.child("Teachers").child(user.getUid()).child("subjects").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterable<DataSnapshot> snapshotIterator = snapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();

                while (iterator.hasNext()) {
                    DataSnapshot next = (DataSnapshot) iterator.next();
                    teacherSub.add(next.getValue().toString());
                    Log.d("subject",next.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ArrayList<String> semSub=new ArrayList<>();
        ref.child("Students").child(uid.get(z)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   String sem=dataSnapshot.child("semester").getValue().toString();
                   String branch=dataSnapshot.child("branch").getValue().toString();
                   String contact=dataSnapshot.child("contact").getValue().toString();


                    ref1.child("Branch").child(branch).child(sem).addValueEventListener(new ValueEventListener() {
                        @SuppressLint("ResourceAsColor")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Iterable<DataSnapshot> snapshotIterable = snapshot.getChildren();
                            Iterator<DataSnapshot> iterator = snapshotIterable.iterator();

                            Row row1 = sheet.createRow(rowCount);
                            cell = row1.createCell(0);
                            cell.setCellValue(student.get(z));
                            cell.setCellStyle(cellStyle);

                            rowCount++;
                            rowCount++;
                            //Log.d("RowCount", String.valueOf(rowCount));

                            Row rw = sheet.createRow(rowCount);
                            cell = rw.createCell(0);
                            cell.setCellValue("Subject Name");
                            cell.setCellStyle(cellStyle);

                            cell = rw.createCell(1);
                            cell.setCellValue("Present");
                            cell.setCellStyle(cellStyle);

                            cell = rw.createCell(2);
                            cell.setCellValue("Absent");
                            cell.setCellStyle(cellStyle);

                            rowCount++;
                            rowCount++;

                            while (iterator.hasNext()) {
                                DataSnapshot next=iterator.next();
                                Log.d(branch + " " + sem, next.getValue().toString());
                                semSub.add(next.getValue().toString());

                                if (teacherSub.contains(next.getValue().toString())) {
                                    TableRow row = new TableRow(context);

                                    Row r1 = sheet.createRow(rowCount);
                                    cell = r1.createCell(0);
                                    cell.setCellValue(next.getValue().toString());
                                    Log.d("nm", next.getValue().toString());
                                    cell.setCellStyle(cellStyle);

                                    TextView text1 = new TextView(context);
                                    text1.setText(next.getValue().toString());
                                    text1.setTextColor(Color.BLACK);

                                    //dataSnapshot.child("Attendence").child(next.getValue().toString())
                                    float r=0;
                                    float g=0;
                                    float t=0;

                                    Iterable<DataSnapshot> d = dataSnapshot.child("Attendence").child(next.getValue().toString()).getChildren();
                                    Iterator<DataSnapshot> it = d.iterator();
                                    //Log.d("iterable",it.next().getKey());

                                    for (int p=0;it.hasNext();p++){
                                        DataSnapshot next1 = (DataSnapshot) it.next();
                                        Log.d("iterable",next1.getKey());
                                        if (date.contains(next1.getKey())) {
                                            if (next1.getValue().equals("Present")) {
                                                r++;
                                            }
                                            if (next1.getValue().equals("Absent")) {
                                                g++;
                                            }
                                            t++;

                                        }
                                    }

                                    Log.d("total", String.valueOf(t));
                                    float perP;
                                    float perA;
                                    if (t!=0) {
                                        perP = (r / t) * 100;
                                        perA = (g / t) * 100;
                                        Log.d("perP", String.format("%.02f",perP));
                                        Log.d("perA", String.format("%.02f",perA));

                                    }
                                    else {
                                        perA=0;
                                        perP=0;
                                    }
                                    TextView text3 = new TextView(context);

                                    int b=(int)r;
                                    cell = r1.createCell(1);
                                    cell.setCellValue(String.format("%.02f",perP)+"%");
                                    //Log.d("nm", next.child("name").getValue().toString());
                                    cell.setCellStyle(cellStyle);
                                    //text3.setText(String.valueOf(r)+"("+String.valueOf(perP)+"%)");
                                    text3.setText(String.format("%.0f",perP)+"%");
                                    text3.setTextColor(Color.BLACK);

                                    Log.d("present count " , String.valueOf(r));


                                    Log.d("total count " , String.valueOf(t));

                                    int n=(int)g;
                                    cell = r1.createCell(2);
                                    cell.setCellValue(String.format("%.02f",perA)+"%");
                                    //Log.d("nm", next.child("name").getValue().toString());
                                    cell.setCellStyle(cellStyle);

                                    TextView text4 = new TextView(context);
                                    text4.setText(String.format("%.02f",perA)+"%");
                                    text4.setTextColor(Color.BLACK);
                                    Log.d("absent count " , String.valueOf(g));

                                    text1.setLayoutParams(layoutParams);
                                    text1.setGravity(Gravity.CENTER);
                                    //row.addView(text1);

                                    Button button=new Button(context);
                                    button.setText("Alert");
                                    button.setWidth(20);

                                    //button.setHeight(30);
                                    //button.setTextSize(12);
                                    //button.setBackgroundColor(Color.RED);

                                    //button.setLayoutParams(new ViewGroup.LayoutParams(20,20));
                                    //button.setBackgroundColor(Color.argb(100,34,179,255));
                                    button.setBackgroundColor(Color.parseColor("#22b3e8"));
                                    button.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            String msg="Dear Student "+t1.getText().toString()+", Your Attendance for the Subject - "+text1.getText().toString() +" is "+text3.getText().toString()+".";
                                            //Uri smsUri = Uri.parse("tel:" + contact);
                                            Uri smsUri = Uri.parse("smsto:" + contact);
                                            Intent intent = new Intent(Intent.ACTION_SENDTO, smsUri);
                                            //intent.putExtra("address", contact);
                                            intent.putExtra("sms_body", msg);
                                            //intent.setType("vnd.android-dir/mms-sms");
                                            context.startActivity(intent);



                                        }
                                    });

                                    text3.setLayoutParams(layoutParams);
                                    text3.setGravity(Gravity.CENTER);

                                    text4.setLayoutParams(layoutParams);
                                    text4.setGravity(Gravity.CENTER);

                                    row.addView(text1,0);
                                    row.addView(text3,1);
                                    //row.addView(text4,2);
                                    row.addView(button,2);

                                    row.setLayoutParams(l);

                                    tableLayout.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
                                }
                                rowCount++;
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


/*        ref.child("Students").child(uid.get(z)).child("Attendence").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Iterable<DataSnapshot> snapshotIterable = snapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterable.iterator();
                while (iterator.hasNext()){
                    DataSnapshot next = (DataSnapshot) iterator.next();
                    Log.d("next",next.getKey());
                    studentAttendeSub.add(next.getKey());

                    TableRow row = new TableRow(context);

                    TextView text1 = new TextView(context);
                    text1.setText(next.getKey());
                    text1.setTextColor(Color.BLACK);

                    text1.setLayoutParams(layoutParams);
                    text1.setGravity(Gravity.CENTER);

                    row.addView(text1);

                    tableLayout.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/

        return view;
    }
    public Workbook getWb() {
        return wb;
    }
}
