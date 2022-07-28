package com.example.eattendence.Register;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.eattendence.Login;
import com.example.eattendence.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class Register_Teach extends AppCompatActivity implements View.OnClickListener {

    EditText fnm,con,email,pass,cnf;
    Button button;
    Toolbar toolbar;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    FirebaseUser fuser;
    Context context;
    ArrayList<String> s=new ArrayList<>();

    ArrayList<String> check=new ArrayList<>();
    ArrayList<String> alreadySub=new ArrayList<>();

    CheckBox[] checkBoxes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_teach);

        context=this;
        toolbar = findViewById(R.id.tool);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register Teacher");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(view -> {
            Intent i=new Intent(Register_Teach.this,Login.class);
            startActivity(i);
        });

        firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference=FirebaseDatabase.getInstance().getReference();
        fuser= FirebaseAuth.getInstance().getCurrentUser();
        auth=FirebaseAuth.getInstance();

        fnm=findViewById(R.id.teacherfirstnm);
        con=findViewById(R.id.teachercontact);
        email=findViewById(R.id.teacheremail);
        pass=findViewById(R.id.teacherpass);
        cnf=findViewById(R.id.teachercnfpass);
        button=findViewById(R.id.addteacher);

        databaseReference=FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Teachers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterable<DataSnapshot> snapshotIterator = snapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                while (iterator.hasNext()){
                    DataSnapshot next=(DataSnapshot) iterator.next();

                    Iterable<DataSnapshot> snapshotIterator1 = next.child("subjects").getChildren();
                    Iterator<DataSnapshot> iterator1 = snapshotIterator1.iterator();

                    while (iterator1.hasNext()){
                        DataSnapshot next1=(DataSnapshot) iterator1.next();
                        alreadySub.add(next1.getValue().toString());
                        Log.d("",next1.getValue().toString());
                        //Log.d("",next1.getKey());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Log.d("already subject array", String.valueOf(alreadySub));

        databaseReference.child("Branch").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterable<DataSnapshot> snapshotIterator = snapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();

                while (iterator.hasNext()) {
                    DataSnapshot next = (DataSnapshot) iterator.next();

                    for (int i=1;i<=next.getChildrenCount();i++){
                        String y=i+" Sem";

                        for (int n=1;n<next.child(y).getChildrenCount();n++){
                            String x="sub"+n;
                            if (!(next.child(y).child(x).getValue() ==null)) {
                                if (!(alreadySub.contains((String) next.child(y).child(x).getValue()))) {
                                    if (!(s.contains((String) next.child(y).child(x).getValue()))) {
                                        s.add(next.child(y).child(x).getValue().toString());
                                    }
                                }

                            }
                        }


                    }

                }


                //Log.d("subject array", String.valueOf(s));

               /* for (int i=0;i<s.size() ;i++)
                {
                    if (alreadySub.contains(s.get(i))){
                        Log.d("removed",s.get(i));
                        s.remove(i);
                    }
                }*/
                Collections.sort(s);
                LinearLayout linearLayout=findViewById(R.id.layout);
                //CheckBox[] checkBoxes;
                checkBoxes=new CheckBox[s.size()];

                Log.d("Array size", String.valueOf(s.size()));
                Log.d("Checkbox array size", String.valueOf(checkBoxes.length));

                //int x=0;
                for (int i=0;i<checkBoxes.length ;i++)
                {
                        checkBoxes[i] = new CheckBox(context);
                        checkBoxes[i].setText((String) s.get(i));
                        checkBoxes[i].setOnClickListener((View.OnClickListener) context);
                        linearLayout.addView(checkBoxes[i]);
                        //Log.d("Checkboxes", checkBoxes[i].getText().toString());

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String f=fnm.getText().toString().trim();
                String c=con.getText().toString().trim();
                String e=email.getText().toString().trim();
                String p=pass.getText().toString();
                String cn=cnf.getText().toString();


                if (f.equals("") ||  c.equals("") || e.equals("") || p.equals("") || cn.equals("") || check.isEmpty())
                {
                    Toast.makeText(Register_Teach.this, "Please Fill All The Details", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (p.length() >= 6) {

                        if (p.equals(cn)) {

                            auth.createUserWithEmailAndPassword(e, p)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()){
                                                FirebaseUser firebaseUser = auth.getCurrentUser();

                                                //databaseReference.child("Students").child(fuser.getUid());
                                                databaseReference.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        databaseReference.child("Teachers").child(firebaseUser.getUid()).child("name").setValue(f);

                                                        databaseReference.child("Teachers").child(firebaseUser.getUid()).child("contact").setValue(c);
                                                        databaseReference.child("Teachers").child(firebaseUser.getUid()).child("email").setValue(e);
                                                        databaseReference.child("Teachers").child(firebaseUser.getUid()).child("uid").setValue(firebaseUser.getUid());

                                                        for (int i=0;i<check.size();i++){
                                                            databaseReference.child("Teachers").child(firebaseUser.getUid()).child("subjects").child(String.valueOf(i+1)).setValue(check.get(i));
                                                            Log.d("data is saving","done");
                                                        }

                                                        databaseReference.child("Users").child(firebaseUser.getUid()).child("uid").setValue(firebaseUser.getUid());
                                                        databaseReference.child("Users").child(firebaseUser.getUid()).child("type").setValue("Teachers");

                                                        Log.d("data save","done");


                                                    }


                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });


                                            }
                                        }
                                    });

                            ProgressDialog progressDialog=new ProgressDialog(Register_Teach.this);
                            progressDialog.setMessage("Loading.....");
                            progressDialog.setTitle("Please Wait");
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            progressDialog.show();
                            progressDialog.setCancelable(false);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(20000);
                                    }catch (Exception e){

                                    }
                                    progressDialog.dismiss();
                                }
                            }).start();

                            Toast.makeText(Register_Teach.this, "Successfull", Toast.LENGTH_SHORT).show();
                            Intent i=new Intent(Register_Teach.this, Login.class);
                            startActivity(i);

                        }else {

                            Toast.makeText(Register_Teach.this, "Password not Match", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(Register_Teach.this, "Password is too short", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(this, Login.class);
        startActivity(intent);
    }



    @Override
    public void onClick(View view) {
        for (CheckBox checkBox : checkBoxes) {

            if (checkBox.isChecked()) {

                if (!check.contains(checkBox.getText().toString())) {
                    check.add(checkBox.getText().toString());
                }
                Log.d("Check box",checkBox.getText().toString());
                //Log.d("Checked box", String.valueOf(check));
            }
            Log.d("c", String.valueOf(check));
            Log.d(String.valueOf(checkBox.isSelected()), checkBox.getText().toString());
        }

    }
}