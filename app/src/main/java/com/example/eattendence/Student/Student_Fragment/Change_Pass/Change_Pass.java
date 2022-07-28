package com.example.eattendence.Student.Student_Fragment.Change_Pass;



import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//import com.example.attendence.DatabaseHelper;
import com.example.eattendence.R;
import com.example.eattendence.Student.StudentActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Change_Pass extends Fragment {

    //private ChangePassViewModel mViewModel;

    Activity context;
    EditText current,cnf,newpass;
    Button button;
    //DatabaseHelper db;
    FirebaseUser user;
    DatabaseReference ref;
    FirebaseDatabase fd;
    FirebaseAuth auth;
    public static Change_Pass newInstance() {
        return new Change_Pass();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.change__pass_fragment, container, false);
        context=getActivity();


        current=view.findViewById(R.id.currentpass);
        cnf=view.findViewById(R.id.cnfpass);
        newpass=view.findViewById(R.id.new_pass);
        button=view.findViewById(R.id.change);

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        ref=FirebaseDatabase.getInstance().getReference();
        fd=FirebaseDatabase.getInstance();

        String id=user.getUid();


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String c = current.getText().toString();
                Log.d("Enter Current Password", c);
                String newP = newpass.getText().toString();
                Log.d("New Passowrd", newP);
                String confirn = cnf.getText().toString();
                Log.d("Confirm Password", confirn);


                //Log.d("Current Password",cp);
                /*if (c.equals(cp))
                {
                    if (newP.equals(confirn))
                    {
                  //      db.changeStudentPass(user,newP);
                        Toast.makeText(context, "PassWord Successfully Changed", Toast.LENGTH_SHORT).show();
                        current.setText("");
                        cnf.setText("");
                        newpass.setText("");
                    }else {
                        Toast.makeText(context, "Password Not Matching", Toast.LENGTH_SHORT).show();
                        current.setText("");
                        cnf.setText("");
                        newpass.setText("");
                    }
                }
                else {
                    Toast.makeText(context, "Current Password Not Matching", Toast.LENGTH_SHORT).show();
                    current.setText("");
                    cnf.setText("");
                    newpass.setText("");
                }*/
                if (newP.equals(confirn)) {

                    final String email = user.getEmail();
                    AuthCredential credential = EmailAuthProvider.getCredential(email, c);

                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                user.updatePassword(newP).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (!task.isSuccessful()) {
                                            Snackbar snackbar_su = Snackbar
                                                    .make(v, "Something went wrong. Please try again later", Snackbar.LENGTH_LONG);
                                            snackbar_su.show();
                                            //Toast.makeText(context, "Something went wrong. Please try again later", Toast.LENGTH_SHORT).show();
                                            current.setText("");
                                            cnf.setText("");
                                            newpass.setText("");
                                        } else {
                                            Snackbar snackbar_su = Snackbar
                                                    .make(v, "Password Successfully Modified", Snackbar.LENGTH_LONG);
                                            snackbar_su.show();
                                            //Toast.makeText(context, "Password Successfully Modified", Toast.LENGTH_SHORT).show();
                                            current.setText("");
                                            cnf.setText("");
                                            newpass.setText("");
                                        }
                                    }
                                });
                            } else {
                                Snackbar snackbar_su = Snackbar
                                        .make(v, "Authentication Failed", Snackbar.LENGTH_LONG);
                                snackbar_su.show();
                                // Toast.makeText(context, "Authentication Failed", Toast.LENGTH_SHORT).show();
                                current.setText("");
                                cnf.setText("");
                                newpass.setText("");
                            }
                        }
                    });
                } else {
                    Snackbar snackbar_su = Snackbar
                            .make(v, "Password not Match", Snackbar.LENGTH_LONG);
                    snackbar_su.show();
                    //Toast.makeText(context, "Password Not Matching", Toast.LENGTH_SHORT).show();
                    current.setText("");
                    cnf.setText("");
                    newpass.setText("");
                }
            }
        });


        return view;
    }



}