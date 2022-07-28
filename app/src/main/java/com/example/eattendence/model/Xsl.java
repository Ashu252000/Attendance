package com.example.eattendence.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

public class Xsl {

    ArrayList<String> std =new ArrayList<>();
    ArrayList<Integer> present =new ArrayList<Integer>();
    ArrayList<Integer> absent =new ArrayList<Integer>();
    String s,sem,branch;
    FirebaseUser user;
    DatabaseReference ref,ref1,ref2;
    FirebaseDatabase fd;
    FirebaseAuth auth;

    public Xsl(String sub, ArrayList<String> dateArray) {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference();
        ref1 = FirebaseDatabase.getInstance().getReference();
        ref2 = FirebaseDatabase.getInstance().getReference();
        fd = FirebaseDatabase.getInstance();

        ref.child("Branch").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterable<DataSnapshot> snapshotIterator = snapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();

                OuterLoop:
                while (iterator.hasNext()) {

                    DataSnapshot next = (DataSnapshot) iterator.next();
/*
                        Log.i("sample", "Value = " + next.child("sub1").getValue());
                        Log.i("next",next.getKey());*/


                    for (int i=1;i<=next.getChildrenCount();i++){
                        String y=i+" Sem";

                        /* Log.d("Semester",y);*/

                        for (int n=1;n<next.child(y).getChildrenCount();n++){
                            String x="sub"+n;
                            /* Log.d("Subject",x);*/
                            if ((next.child(y).child(x).getValue().toString().equals(sub))) {
                                /*Log.d( y+" Done", next.child(y).child(x).getValue().toString());
                                 */
                                s=next.child(y).child(x).getValue().toString();
                                sem=y;
                                branch=next.getKey();
                                   /* Log.d("matched",s);
                                    Log.d("matched",sem);
                                    Log.d("matched",branch);

*/

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

                    //   Log.i("sample", "Value = " + next.child("sub1").getValue());
                       /* Log.i("next", next.getKey());
                        Log.i("branch", next.child("branch").getValue().toString());
                        Log.i("sem", next.child("semester").getValue().toString());
*/

                    if (next.child("branch").getValue().toString().equals(branch) && next.child("semester").getValue().toString().equals(sem)) {
                        std.add(next.child("name").getValue().toString());
                        //uid.add(next.child("uid").getValue().toString());
                        //Log.d("RowCount", String.valueOf(rowCount));
                        Log.d("studnm", next.child("name").getValue().toString());

                        Iterable<DataSnapshot> snapshotIterable = next.child("Attendence").child(sub).getChildren();
                        Iterator<DataSnapshot> iterator = snapshotIterable.iterator();
                        float h = 0;
                        float g = 0;
                        float t = 0;
                        float perP;
                        float perA;
                        while (iterator.hasNext()) {
                            DataSnapshot next1 = iterator.next();
                            if (dateArray.contains(next1.getKey())) {
                                if (next1.getValue().equals("Present")) {
                                    h++;
                                }
                                if (next1.getValue().equals("Absent")) {
                                    g++;
                                }
                                t++;
                            }

                            if (t != 0) {
                                perP = (h / t) * 100;
                                perA = (g / t) * 100;
                                Log.d("perP", String.format("%.02f", perP));
                                Log.d("perA", String.format("%.02f", perA));

                            } else {
                                perA = 0;
                                perP = 0;
                            }
                            present.add((int) perP);
                            absent.add((int) perA);
                        }


                            /*cell = ro[0].createCell(1);
                            cell.setCellValue(present[0]);
                            cell.setCellStyle(cellStyle);

                            cell = ro[0].createCell(2);
                            cell.setCellValue(absent[0]);
                            cell.setCellStyle(cellStyle);

                            rowCount++;
                            Log.d("New RowCount", String.valueOf(rowCount));*/

                        /*Log.d("uid",next.child("uid").getValue().toString());
                         */

                    }
                       /* Log.d("Subject",subject.get(finalJ));
                        Log.d("Student name array",studnm.toString());
                        Log.d("Student UID array",uid.toString());*/
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public ArrayList<String> getStd() {
        return std;
    }

    public void setStd(ArrayList<String> std) {
        this.std = std;
    }

    public ArrayList<Integer> getPresent() {
        return present;
    }

    public void setPresent(ArrayList<Integer> present) {
        this.present = present;
    }

    public ArrayList<Integer> getAbsent() {
        return absent;
    }

    public void setAbsent(ArrayList<Integer> absent) {
        this.absent = absent;
    }
}
