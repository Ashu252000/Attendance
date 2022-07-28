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
import com.example.eattendence.model.AttendenceAdapter;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class AttendenceStudent extends AppCompatActivity {
    TextView report;
    Date startdate,enddate;
    String start,end,r;
    ArrayList<String> c,student,uid;
    ArrayList<String> stud;
    ArrayList<String> date;

    Workbook wb=new HSSFWorkbook();
    Cell cell = null;
    Workbook wb1;
    Sheet sheet=wb.createSheet("Database");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendence_student);

        Toolbar toolbar = findViewById(R.id.tool);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("View Attendence");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AttendenceStudent.this, Teacher_Activity.class);
                startActivity(i);
            }
        });

        report = findViewById(R.id.report);
        ListView listView=findViewById(R.id.list);

        Intent intent = getIntent();

        student = intent.getStringArrayListExtra("student Name");
        uid = intent.getStringArrayListExtra("uid");
        start = intent.getStringExtra("Start Date");
        end = intent.getStringExtra("End Date");
        date =intent.getStringArrayListExtra("Date");
        Log.d("Date Array", date.toString());

        r="Attendence Report from " + start + " to " + end;
        report.setText(r);

        Log.d("Course",student.toString());

        AttendenceAdapter attendenceAdapter=new AttendenceAdapter(getApplicationContext(), date,student,uid,wb,sheet,r);
        listView.setAdapter(attendenceAdapter);

        wb1 = attendenceAdapter.getWb();
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
            Toast.makeText(AttendenceStudent.this, "Attendence File Downloaded in AttendenceSystem Folder", Toast.LENGTH_SHORT).show();

        }
        return true;
    }
}