package com.example.eattendence.Register;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.eattendence.Login;
import com.example.eattendence.R;
import com.example.eattendence.Student.StudentActivity;
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
import java.util.Iterator;

public class Register_Stud extends AppCompatActivity {

    EditText name,prn,contact,email,pass,cnfpass;
    RadioGroup rg,rg1;
    Button button;
    Toolbar toolbar;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference reference;
    FirebaseAuth auth;
    FirebaseUser fuser;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_stud);

        toolbar = findViewById(R.id.tool);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register Student");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Register_Stud.this,Login.class);
                startActivity(i);
            }
        });

        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=FirebaseDatabase.getInstance().getReference();
        fuser= FirebaseAuth.getInstance().getCurrentUser();
        auth=FirebaseAuth.getInstance();

        context=this;

        name=findViewById(R.id.studnm);
        prn=findViewById(R.id.prn);
        contact=findViewById(R.id.studcontact);
        email=findViewById(R.id.studemail);
        pass=findViewById(R.id.studpass);
        cnfpass=findViewById(R.id.studcnfpass);

        rg=findViewById(R.id.radio);
        rg1=findViewById(R.id.radioSem);

        ArrayList<String> branch=new ArrayList<>();

        databaseReference.child("Branch").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterable<DataSnapshot> snapshotIterator = snapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();

                while (iterator.hasNext()) {
                    DataSnapshot next = (DataSnapshot) iterator.next();
                    branch.add(next.getKey());
                    Log.d("Branch",next.getKey());
                }

                RadioButton[] rb =new RadioButton[branch.size()];

                for (int i=0;i<branch.size();i++){
                    rb[i]=new RadioButton(context);
                    rb[i].setText(branch.get(i));
                    rb[i].setId(View.generateViewId());
                    rg.addView(rb[i]);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        button=findViewById(R.id.add_student);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String nm=name.getText().toString().trim();

                String prnn=prn.getText().toString().trim();
                String cont=contact.getText().toString().trim();
                String e=email.getText().toString().trim();
                String p=pass.getText().toString();
                String c=cnfpass.getText().toString();

                int id=rg1.getCheckedRadioButtonId();
                RadioButton rb=findViewById(id);
                String sem=rb.getText().toString();

                int id1=rg.getCheckedRadioButtonId();
                RadioButton rb1=findViewById(id1);
                String branch=rb1.getText().toString();

                if (nm.equals("") || prnn.equals("") || cont.equals("") || e.equals("") || p.equals("") || c.equals("") || sem.equals("") || branch.equals(""))
                {
                    Toast.makeText(Register_Stud.this, "Please Fill All The Details", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (p.length()>=6) {

                        if (p.equals(c)){

                            auth.createUserWithEmailAndPassword(e,p)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if(task.isSuccessful()) {
                                                //FirebaseUser firebaseUser = auth.getCurrentUser();


                                                //databaseReference.child("Students").child(fuser.getUid());
                                                databaseReference.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        databaseReference.child("Students").child(fuser.getUid()).child("name").setValue(nm);
                                                        databaseReference.child("Students").child(fuser.getUid()).child("prn").setValue(prnn);
                                                        databaseReference.child("Students").child(fuser.getUid()).child("contact").setValue(cont);
                                                        databaseReference.child("Students").child(fuser.getUid()).child("email").setValue(e);
                                                        databaseReference.child("Students").child(fuser.getUid()).child("uid").setValue(fuser.getUid());
                                                        databaseReference.child("Students").child(fuser.getUid()).child("semester").setValue(sem);
                                                        databaseReference.child("Students").child(fuser.getUid()).child("branch").setValue(branch);

                                                        databaseReference.child("Users").child(fuser.getUid()).child("uid").setValue(fuser.getUid());
                                                        databaseReference.child("Users").child(fuser.getUid()).child("type").setValue("Students");


                                                    }


                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });


                                            }
                                        }
                                    });

                            ProgressDialog progressDialog=new ProgressDialog(Register_Stud.this);
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
                            Toast.makeText(Register_Stud.this, "Successfull", Toast.LENGTH_SHORT).show();
                            Intent i=new Intent(Register_Stud.this, Login.class);
                            startActivity(i);


                        } else {
                            Toast.makeText(Register_Stud.this, "Password not Match", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(Register_Stud.this, "Password is too short", Toast.LENGTH_SHORT).show();
                    }

                    //Toast.makeText(Register_Stud.this, "hello", Toast.LENGTH_SHORT).show();
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
}