package com.example.eattendence.Student.Student_Fragment.View_Attendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eattendence.R;
import com.example.eattendence.Student.StudentActivity;
import com.example.eattendence.Teacher.Teacher_Fragment.View_Attendence.AttendenceStudent;
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

public class Student_Attendences extends AppCompatActivity {
    TextView present,absent, subjects,teacher;
    TableLayout tableLayout;
    FirebaseUser user;
    DatabaseReference ref,ref1;
    FirebaseDatabase fd;
    FirebaseAuth auth;
    Workbook wb=new HSSFWorkbook();
    Cell cell = null;
    Workbook wb1;
    Sheet sheet=wb.createSheet("Database");
    CellStyle cellStyle;;

    Row r1;
    int rowCount;
    String r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_attendences);

        Toolbar toolbar = findViewById(R.id.tool);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("View Attendence");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Student_Attendences.this, StudentActivity.class);
                startActivity(i);
            }
        });

        auth= FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        ref= FirebaseDatabase.getInstance().getReference();
        ref1=FirebaseDatabase.getInstance().getReference();
        fd=FirebaseDatabase.getInstance();

        String id=user.getUid();

        present=findViewById(R.id.present);
        absent=findViewById(R.id.absent);
        subjects =findViewById(R.id.subject);
        teacher=findViewById(R.id.teacher);

        tableLayout=findViewById(R.id.table);

        Intent i = getIntent();
        ArrayList<String> date=i.getStringArrayListExtra("date");

        Log.d("date",date.toString());

        String subject = i.getStringExtra("subject");
        subjects.setText(subject);

        cellStyle=wb.createCellStyle();
        cellStyle.setFillForegroundColor(HSSFColor.LIGHT_BLUE.index);
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        Row row=sheet.createRow(0);
        cell =row.createCell(0);
        cell.setCellValue("Subject - "+subject);
        cell.setCellStyle(cellStyle);

        rowCount = 2;

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterable<DataSnapshot> snapshotIterable = snapshot.child("Teachers").getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterable.iterator();

                while (iterator.hasNext()){
                    DataSnapshot next = iterator.next();

                    Iterable<DataSnapshot> snapshotIterable1 = next.child("subjects").getChildren();
                    Iterator<DataSnapshot> iterator1 = snapshotIterable1.iterator();

                    while (iterator1.hasNext()) {
                        DataSnapshot next1 = iterator1.next();

                        if (next1.getValue().toString().equals(subject)) {
                            teacher.setText(next.child("name").getValue().toString());
                            Row row1 = sheet.createRow(rowCount);
                            cell = row1.createCell(0);
                            cell.setCellValue("Teacher - "+next.child("name").getValue().toString());
                            cell.setCellStyle(cellStyle);

                            rowCount++;

                            Row row2 = sheet.createRow(rowCount);
                            cell = row2.createCell(0);
                            cell.setCellValue("Date");
                            cell.setCellStyle(cellStyle);

                            cell = row2.createCell(1);
                            cell.setCellValue("Status");
                            cell.setCellStyle(cellStyle);

                            rowCount++;
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterable<DataSnapshot> snapshotIterable = snapshot.child("Students").child(id).child("Attendence").child(subject).getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterable.iterator();
                int Present=0,Absent=0;

                while (iterator.hasNext()){
                    DataSnapshot next = iterator.next();


                    if (date.contains(next.getKey())){

                        TableRow tableRow=new TableRow(Student_Attendences.this);
                        TableRow.LayoutParams l=new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                        l.setMargins(0,20,0,0);
                        tableRow.setGravity(Gravity.CENTER_HORIZONTAL);
                        tableRow.setLayoutParams(l);
                        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);

                        Row row3 = sheet.createRow(rowCount);



                        TextView t1=new TextView(Student_Attendences.this);
                        t1.setText(next.getKey());
                        t1.setGravity(Gravity.CENTER);
                        t1.setLayoutParams(layoutParams);

                        cell = row3.createCell(0);
                        cell.setCellValue(next.getKey());
                        cell.setCellStyle(cellStyle);

                        TextView t2=new TextView(Student_Attendences.this);
                        t2.setText(next.getValue().toString());
                        t2.setGravity(Gravity.CENTER);
                        t2.setLayoutParams(layoutParams);

                        cell = row3.createCell(1);
                        cell.setCellValue(next.getValue().toString());
                        cell.setCellStyle(cellStyle);

                        rowCount++;

                        if (next.getValue().toString().equals("Present")){
                            Present++;
                        }
                        if (next.getValue().toString().equals("Absent")){
                            Absent++;
                        }

                        tableRow.addView(t1);
                        tableRow.addView(t2);

                        tableLayout.addView(tableRow,new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT));
                    }
                }

                present.setText(String.valueOf(Present));
                absent.setText(String.valueOf(Absent));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        String startdt=i.getStringExtra("Start Date");
        String enddt=i.getStringExtra("End Date");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id=item.getItemId();
        if (id==R.id.download){
            File file = new File(getExternalFilesDir(null), "Student_Attendence.xls");
            FileOutputStream outputStream = null;
            Log.d("File", "done");

            try {
                outputStream = new FileOutputStream(file);
                wb.write(outputStream);
                Log.d("try", "block");

            } catch (java.io.IOException e) {
                e.printStackTrace();
            }


            try {
                outputStream.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Toast.makeText(Student_Attendences.this, "Attendence File Downloaded in AttendenceSystem Folder", Toast.LENGTH_SHORT).show();

        }
        return true;
    }
}