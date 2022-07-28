package com.example.eattendence.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
import java.util.HashMap;
import java.util.Iterator;

public class MyAdapter extends BaseAdapter {
    Context context;

    ArrayList<ArrayList<String>> snm=new ArrayList<>();
    ArrayList<String> date=new ArrayList<>();
    LayoutInflater inflater;
    ArrayList<String> subject;
    ArrayList<Integer> present=new ArrayList<>();
    ArrayList<Integer> absent=new ArrayList<>();
    String s,sem,branch;
    HashMap<String, String> map = new HashMap<>();

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

    public MyAdapter(Context applicationcontext, ArrayList<String> date, ArrayList<String> subject, Workbook wb, Sheet sheet,String r){
        context=applicationcontext;
        this.date=date;
        this.subject =subject;
        this.wb=wb;
        this.r=r;
        this.sheet=sheet;
        inflater = (LayoutInflater.from(applicationcontext));

//        sheet=wb.createSheet("Database");
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
        return subject.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int z, View view, ViewGroup viewGroup) {

        view = inflater.inflate(R.layout.list, null);
        TextView t1 = view.findViewById(R.id.text1);
        t1.setText(subject.get(z));
        Log.d("", subject.get(z));

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference();
        ref1 = FirebaseDatabase.getInstance().getReference();
        fd = FirebaseDatabase.getInstance();

        ArrayList<String> studnm = new ArrayList<>();
        ArrayList<String> uid = new ArrayList<>();

        TableRow.LayoutParams l = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, 30);
        l.setMargins(0, 50, 0, 0);

        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
        TableLayout tableLayout = view.findViewById(R.id.table);


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
                            if ((next.child(y).child(x).getValue().toString().equals(subject.get(z)))) {


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

                Row row1 = sheet.createRow(rowCount);
                cell = row1.createCell(0);
                cell.setCellValue(subject.get(z));
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

                rowCount++;

                while (iterator1.hasNext()) {
                    ArrayList<String> s = new ArrayList<>();
                    DataSnapshot next = (DataSnapshot) iterator1.next();


                    if (next.child("branch").getValue().toString().equals(branch) && next.child("semester").getValue().toString().equals(sem)) {
                        studnm.add(next.child("name").getValue().toString());

                        Row r1 = sheet.createRow(rowCount);
                        cell = r1.createCell(0);
                        cell.setCellValue(next.child("name").getValue().toString());
                        Log.d("nm", next.child("name").getValue().toString());
                        cell.setCellStyle(cellStyle);

                        uid.add(next.child("uid").getValue().toString());

                        TableRow row = new TableRow(context);

                        TextView text1 = new TextView(context);
                        text1.setText(next.child("name").getValue().toString());
                        String contact = next.child("contact").getValue().toString();
                        text1.setTextColor(Color.BLACK);


                        TextView text2 = new TextView(context);
                        text2.setText(next.child("prn").getValue().toString());
                        text2.setTextColor(Color.BLACK);


                        float r = 0;
                        float g = 0;
                        float t = 0;

                        Iterable<DataSnapshot> d = next.child("Attendence").child(subject.get(z)).getChildren();
                        Iterator<DataSnapshot> it = d.iterator();
                        //Log.d("iterable",it.next().getKey());

                        for (int p = 0; it.hasNext(); p++) {
                            DataSnapshot next1 = (DataSnapshot) it.next();

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


                        float perP;
                        float perA;
                        if (t != 0) {
                            perP = (r / t) * 100;
                            perA = (g / t) * 100;

                        } else {
                            perA = 0;
                            perP = 0;
                        }
                        TextView text3 = new TextView(context);

                        int b = (int) r;
                        present.add(b);

                        cell = r1.createCell(1);
                        cell.setCellValue(b);
                        //Log.d("nm", next.child("name").getValue().toString());
                        cell.setCellStyle(cellStyle);

                        //text3.setText(String.valueOf(r)+"("+String.valueOf(perP)+"%)");
                        text3.setText( String.format("%.0f", perP) + "%");
                        text3.setTextColor(Color.BLACK);

                        int n = (int) g;
                        absent.add(n);

                        cell = r1.createCell(2);
                        cell.setCellValue(n);
                        //Log.d("nm", next.child("name").getValue().toString());
                        cell.setCellStyle(cellStyle);

                        TextView text4 = new TextView(context);
                        text4.setText(n + "(" + String.format("%.02f", perA) + "%)");
                        text4.setTextColor(Color.BLACK);
                        Log.d("absent count ", String.valueOf(g));

                        text1.setLayoutParams(layoutParams);
                        text1.setGravity(Gravity.CENTER);

                        text2.setLayoutParams(layoutParams);
                        text2.setGravity(Gravity.CENTER);

                        text3.setLayoutParams(layoutParams);
                        text3.setGravity(Gravity.CENTER);

                        text4.setLayoutParams(layoutParams);
                        text4.setGravity(Gravity.CENTER);

                        Button button=new Button(context);
                        button.setText("Alert");
                        button.setWidth(20);
                        button.setHeight(20);

                        button.setBackgroundColor(Color.parseColor("#22b3e8"));
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String msg="Dear Student "+text1.getText().toString()+", Your Attendance for the Subject - "+t1.getText().toString() +" is "+text3.getText().toString()+".";
                                //Uri smsUri = Uri.parse("tel:" + contact);
                                Uri smsUri = Uri.parse("smsto:" + contact);
                                Intent intent = new Intent(Intent.ACTION_SENDTO, smsUri);
                                //intent.putExtra("address", contact);
                                intent.putExtra("sms_body", msg);
                                //intent.setType("vnd.android-dir/mms-sms");
                                context.startActivity(intent);

                            }
                        });

                        row.addView(text1,0);
                        row.addView(text2,1);
                        row.addView(text3,2);
                        row.addView(button,3);
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



        return view;
    }

    public Workbook getWb() {
        return wb;
    }
}
