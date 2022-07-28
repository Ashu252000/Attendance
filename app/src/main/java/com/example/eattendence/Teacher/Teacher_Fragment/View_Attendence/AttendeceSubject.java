package com.example.eattendence.Teacher.Teacher_Fragment.View_Attendence;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eattendence.R;
import com.example.eattendence.Teacher.Teacher_Activity;
import com.example.eattendence.model.MyAdapter;
import com.example.eattendence.model.Xsl;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class AttendeceSubject extends AppCompatActivity {

    TextView report;
    String start,end,r;
    int rowCount;
    CellStyle cellStyle;
    ArrayList<String> c, subject;
    ArrayList<String> dateArray;
    FirebaseUser user;
    DatabaseReference ref,ref1,ref2;
    FirebaseDatabase fd;
    FirebaseAuth auth;
    String s,sem,branch;
    ArrayList<String> sb;
    ArrayList<Integer> pr;
    ArrayList<Integer> ab;
    Workbook wb=new HSSFWorkbook();
    Cell cell = null;
    Workbook wb1;
    Sheet sheet=wb.createSheet("Database");

    ArrayList<ArrayList<String>> snm=new ArrayList<>();
    ArrayList<ArrayList<Integer>> p=new ArrayList<ArrayList<Integer>>();
    ArrayList<ArrayList<Integer>> a=new ArrayList<ArrayList<Integer>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendece_subject);

        //sheet=wb.createSheet("Database");

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference();
        ref1 = FirebaseDatabase.getInstance().getReference();
        ref2 = FirebaseDatabase.getInstance().getReference();
        fd = FirebaseDatabase.getInstance();
        Toolbar toolbar = findViewById(R.id.tool);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("View Attendence");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AttendeceSubject.this, Teacher_Activity.class);
                startActivity(i);
            }
        });

        report = findViewById(R.id.report);
        ListView listView = findViewById(R.id.list);

        Intent intent = getIntent();

        subject = intent.getStringArrayListExtra("subject Name");
        start = intent.getStringExtra("Start Date");
        end = intent.getStringExtra("End Date");
        dateArray = intent.getStringArrayListExtra("Date");
        Log.d("Date Array", dateArray.toString());

        r = "Attendence Report from " + start + " to " + end;
        report.setText(r);

        Log.d("Subject", subject.toString());

        MyAdapter myAdapter = new MyAdapter(getApplicationContext(), dateArray, subject,wb,sheet,r);
        listView.setAdapter(myAdapter);
         wb1 = myAdapter.getWb();




       /* Log.d("hello","hello");
        for (int z = 0; z < subject.size(); z++) {

            int finalZ = z;
            ref.child("Branch").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Iterable<DataSnapshot> snapshotIterator = snapshot.getChildren();
                    Iterator<DataSnapshot> iterator = snapshotIterator.iterator();

                    OuterLoop:
                    while (iterator.hasNext()) {

                        DataSnapshot next = (DataSnapshot) iterator.next();

                        Log.i("sample", "Value = " + next.child("sub1").getValue());
                        Log.i("next", next.getKey());


                        for (int i = 1; i <= next.getChildrenCount(); i++) {
                            String y = i + " Sem";

                            Log.d("Semester", y);

                            for (int n = 1; n < next.child(y).getChildrenCount(); n++) {
                                String x = "sub" + n;
                                Log.d("Subject", x);
                                if ((next.child(y).child(x).getValue().toString().equals(subject.get(finalZ)))) {
                                    Log.d(y + " Done", next.child(y).child(x).getValue().toString());
                                *//*Intent intent=new Intent(getActivity(), Student_List.class);
                                intent.putExtra("subject",next.child(y).child(x).getValue().toString());
                                intent.putExtra("semester",y);
                                intent.putExtra("branch",next.getKey());
                                startActivity(intent);*//*

                                    s = next.child(y).child(x).getValue().toString();
                                    sem = y;
                                    branch = next.getKey();
                                    Log.d("matched", s);
                                    Log.d("matched", sem);
                                    Log.d("matched", branch);


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

            ref1.child("Students").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot1) {
                    Iterable<DataSnapshot> snapshotIterator1 = snapshot1.getChildren();
                    Iterator<DataSnapshot> iterator1 = snapshotIterator1.iterator();

                    while (iterator1.hasNext()) {
                        sb = new ArrayList<>();
                        pr = new ArrayList<>();
                        ab = new ArrayList<>();
                        DataSnapshot next = (DataSnapshot) iterator1.next();

                        //   Log.i("sample", "Value = " + next.child("sub1").getValue());
                        Log.i("next", next.getKey());
                        Log.i("branch", next.child("branch").getValue().toString());
                        Log.i("sem", next.child("semester").getValue().toString());


                        if (next.child("branch").getValue().toString().equals(branch) && next.child("semester").getValue().toString().equals(sem)) {
                            sb.add(next.child("name").getValue().toString());

                        }

                        float r = 0;
                        float g = 0;
                        float t = 0;

                        Iterable<DataSnapshot> d = next.child("Attendence").child(subject.get(finalZ)).getChildren();
                        Iterator<DataSnapshot> it = d.iterator();
                        //Log.d("iterable",it.next().getKey());

                        for (int p = 0; it.hasNext(); p++) {
                            DataSnapshot next1 = (DataSnapshot) it.next();
                            Log.d("iterable", next1.getKey());
                            if (dateArray.contains(next1.getKey())) {
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
                        if (t != 0) {
                            perP = (r / t) * 100;
                            perA = (g / t) * 100;
                            Log.d("perP", String.format("%.02f", perP));
                            Log.d("perA", String.format("%.02f", perA));

                        } else {
                            perA = 0;
                            perP = 0;
                        }

                        pr.add((int)perP);
                        ab.add((int)perA);

                        Log.d("Subject", subject.get(finalZ));


                    }
                    snm.add(sb);
                    a.add(ab);
                    p.add(pr);
                    Log.d("Student name array", sb.toString());
                    Log.d("Student absent array", ab.toString());
                    Log.d("Student present array", pr.toString());

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



        }*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

/*
  */
/* *//*
*/
@Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id=item.getItemId();
        if (id==R.id.download) {
            File file = new File(getExternalFilesDir(null), "Subject_Attendence.xls");
            FileOutputStream outputStream = null;
            Log.d("File", "done");

            try {
                outputStream = new FileOutputStream(file);
                wb1.write(outputStream);
                Log.d("try", "block");

            } catch (java.io.IOException e) {
                e.printStackTrace();
            }


            try {
                outputStream.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Toast.makeText(AttendeceSubject.this, "Attendence File Downloaded in AttendenceSystem Folder", Toast.LENGTH_SHORT).show();

        }
        return true;
    }


    private void save() {

        Log.d("Function","Save");
        /*Workbook wb=new HSSFWorkbook();
        Cell cell = null;
        Sheet sheet=null;
        sheet=wb.createSheet("Database");*/
        cellStyle=wb.createCellStyle();
        cellStyle.setFillForegroundColor(HSSFColor.LIGHT_BLUE.index);
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        /*Sheet sheet=null;
        sheet=wb.createSheet("Database");*/


        Row row=sheet.createRow(0);
        cell =row.createCell(0);
        cell.setCellValue(r);
        cell.setCellStyle(cellStyle);

        rowCount = 2;
        Log.d("RowCount", String.valueOf(rowCount));


        for (int j=0;j<subject.size();j++)
        {
            //Xsl xsl=new Xsl(subject.get(j),dateArray);
            int finalJ = j;
            Thread t=new Thread(new Runnable() {
                @Override
                public void run() {

                    ArrayList<String> studnm = new ArrayList<>();
                    ArrayList<String> uid = new ArrayList<>();

                    Row row1 = sheet.createRow(rowCount);
                    cell = row1.createCell(0);
                    cell.setCellValue(subject.get(finalJ));
                    cell.setCellStyle(cellStyle);

                    rowCount++;
                    //Log.d("RowCount", String.valueOf(rowCount));

                    Row rw = sheet.createRow(rowCount);
                    cell = rw.createCell(0);
                    cell.setCellValue("Student");
                    cell.setCellStyle(cellStyle);

                    cell = rw.createCell(1);
                    cell.setCellValue("Present");
                    cell.setCellStyle(cellStyle);

                    cell = rw.createCell(2);
                    cell.setCellValue("Absent");
                    cell.setCellStyle(cellStyle);

                    ref.child("Branch").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Iterable<DataSnapshot> snapshotIterator = snapshot.getChildren();
                            Iterator<DataSnapshot> iterator = snapshotIterator.iterator();

                            OuterLoop:
                            while (iterator.hasNext()) {

                                DataSnapshot next = (DataSnapshot) iterator.next();

                                for (int i = 1; i <= next.getChildrenCount(); i++) {
                                    String y = i + " Sem";

                                    for (int n = 1; n < next.child(y).getChildrenCount(); n++) {
                                        String x = "sub" + n;

                                        if ((next.child(y).child(x).getValue().toString().equals(subject.get(finalJ)))) {


                                            s = next.child(y).child(x).getValue().toString();
                                            sem = y;
                                            branch = next.getKey();


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

                    ref1.child("Students").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot1) {
                            Iterable<DataSnapshot> snapshotIterator1 = snapshot1.getChildren();
                            Iterator<DataSnapshot> iterator1 = snapshotIterator1.iterator();

                            while (iterator1.hasNext()) {

                                DataSnapshot next = (DataSnapshot) iterator1.next();

                                if (next.child("branch").getValue().toString().equals(branch) && next.child("semester").getValue().toString().equals(sem)) {

                                    Row r = sheet.createRow(rowCount);
                                    cell = r.createCell(0);
                                    cell.setCellValue(next.child("name").getValue().toString());
                                    Log.d("nm", next.child("name").getValue().toString());
                                    cell.setCellStyle(cellStyle);
                                }
                                rowCount++;
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });

            t.start();
            t.setPriority(Thread.MAX_PRIORITY);

            rowCount+=1;
        }

        Thread t1=new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("Sheet ","done");
                sheet.setColumnWidth(0, (10 * 200));
                sheet.setColumnWidth(1, (10 * 200));
                sheet.setColumnWidth(2, (10 * 200));


                File file=new File(getExternalFilesDir(null),"Subject_Attendence.xls");
                FileOutputStream outputStream=null;
                Log.d("File","done");

                try {
                    outputStream=new FileOutputStream(file);
                    wb.write(outputStream);
                    Log.d("try","block");

                }catch (java.io.IOException e){
                    e.printStackTrace();
                }


                try {
                    outputStream.close();
                }catch (IOException ex){
                    ex.printStackTrace();
                }
            }
        });
        t1.start();
        t1.setPriority(Thread.MIN_PRIORITY);
        Toast.makeText(AttendeceSubject.this, "Attendence File Downloaded in AttendenceSystem Folder", Toast.LENGTH_SHORT).show();
    }


}