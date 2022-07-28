package com.example.eattendence;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.eattendence.Register.Register_Man;
import com.example.eattendence.Register.Register_Stud;
import com.example.eattendence.Register.Register_Teach;
import com.example.eattendence.Student.StudentActivity;
import com.example.eattendence.Teacher.Teacher_Activity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Login extends AppCompatActivity {
    // reg_stud,reg_teach,reg_man;
    EditText email,passwd;
    Button login,register;
    Spinner spinner;
    private static int RC_SIGN_IN = 100;
    ProgressDialog progressDialog;
    GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private SignInButton signInButton;
    DatabaseReference ref;
    FirebaseDatabase fd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        FirebaseApp.initializeApp(this);
        mAuth=FirebaseAuth.getInstance();
        fd=FirebaseDatabase.getInstance();
        ref=FirebaseDatabase.getInstance().getReference();

        email = (EditText) findViewById(R.id.email);
        passwd = (EditText) findViewById(R.id.password);

        spinner = (Spinner) findViewById(R.id.spinner);

        ArrayList<String> sp=new ArrayList<>();
        sp.add("Select Account Type");
        sp.add("Teachers");
        sp.add("Students");


        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,sp){
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
                View view=super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0){
                    tv.setTextColor(Color.GRAY);
                }else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        login = (Button) findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {

                String em = email.getText().toString().trim();
                String pwd = passwd.getText().toString().trim();
                String acc_type = spinner.getSelectedItem().toString();

                Log.i("email",em);
                Log.i("pwd",pwd);
                Log.i("acc type",acc_type);


                if (em.equals("") || pwd.equals("") || acc_type.equals("Select Account Type")) {

                    Toast.makeText(Login.this, "All Fields Are Required", Toast.LENGTH_SHORT).show();
                }

                else {
                    if (pwd.length() >= 6){
                        mAuth.signInWithEmailAndPassword(em, pwd)
                                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {

                                            FirebaseUser user = mAuth.getCurrentUser();
                                            String uid=user.getUid();

                                            ref.child("Users").child(uid).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot)
                                                {
                                                    String type = snapshot.child("type").getValue().toString();
                                                    Log.d("type",type);

                                                    if (type.equals(acc_type))
                                                    {
                                                        progressDialog=new ProgressDialog(Login.this);
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


                                                        if (acc_type.equals("Students"))
                                                        {
                                                            Intent i = new Intent(Login.this, StudentActivity.class);
                                                            startActivity(i);
                                                        }
                                                        else if (acc_type.equals("Teachers"))
                                                        {
                                                            Intent i = new Intent(Login.this, Teacher_Activity.class);
                                                            startActivity(i);
                                                        }
                                                        else if (acc_type.equals("Management"))
                                                        {
                                                            /*Intent i=new Intent(Login.this, Teacher_Activity.class);
                                                            startActivity(i);*/
                                                        }
                                                    }
                                                    else {
                                                        Toast.makeText(Login.this, "Account Type Not Exist", Toast.LENGTH_SHORT).show();
                                                        mAuth.signOut();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });


                                        } else {
                                            Toast.makeText(Login.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                });
                    }
                    else{
                        Toast.makeText(Login.this, "Password is too short", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        register=findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                final View popupview = inflater.inflate(R.layout.register_popup, null);

                final AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                builder.setView(popupview);
                builder.setCancelable(false);
                //arrayList=getDateArray();

                LinearLayout layout=popupview.findViewById(R.id.linear);
                RadioGroup rg=popupview.findViewById(R.id.radio);

                //add to laayout

                builder.setPositiveButton("Sumit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //checking get info

                        int c = rg.getCheckedRadioButtonId();
                        RadioButton selected_btn = (RadioButton) popupview.findViewById(c);

                        String s=selected_btn.getText().toString();
                        switch (s) {
                            case "Students": {
                                Intent intent = new Intent(Login.this, Register_Stud.class);
                                startActivity(intent);
                                break;
                            }
                            case "Teachers": {
                                Intent intent = new Intent(Login.this, Register_Teach.class);
                                startActivity(intent);
                                break;
                            }
                            case "Management": {
                                Intent intent = new Intent(Login.this, Register_Man.class);
                                startActivity(intent);
                                break;
                            }
                            default:
                                Toast.makeText(Login.this, "Plaese select the UserType", Toast.LENGTH_SHORT).show();
                                break;
                        }

                        dialogInterface.dismiss();
                    }
                });

                AlertDialog alertDialog=builder.create();

                alertDialog.show();
                alertDialog.getButton(Dialog.BUTTON_POSITIVE).setBackgroundColor(Color.parseColor("#FE22D3E8"));
                alertDialog.getButton(Dialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);

            }
        });

    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);

        builder.setMessage("Do You Want To Exit ?");

        builder.setTitle("Alert !");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
                ActivityCompat.finishAffinity(Login.this);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }

}