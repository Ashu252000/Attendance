package com.example.eattendence.Teacher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eattendence.Login;
import com.example.eattendence.R;
import com.example.eattendence.Student.StudentActivity;
import com.example.eattendence.Teacher.Teacher_Fragment.Change_Pass.Change_Pass;
import com.example.eattendence.Teacher.Teacher_Fragment.Home.Home;
import com.example.eattendence.Teacher.Teacher_Fragment.Student.Student;
import com.example.eattendence.Teacher.Teacher_Fragment.Take_Attendence.Attedence_Take;
import com.example.eattendence.Teacher.Teacher_Fragment.Take_Attendence.Take_Attendence;
import com.example.eattendence.Teacher.Teacher_Fragment.View_Attendence.View_Attendence;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Teacher_Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    FragmentTransaction fragmentTransaction;
    FragmentManager fragmentManager;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Teacher_Activity.this, Login.class);
                startActivity(i);
            }
        });

        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=FirebaseDatabase.getInstance().getReference();

        drawer = findViewById(R.id.teacher_drawer);
        navigationView = findViewById(R.id.teacher_nav_view);

        View view=navigationView.getHeaderView(0);
        ImageView logo=view.findViewById(R.id.imageView);
        TextView name=view.findViewById(R.id.myusernm);
        TextView email = view.findViewById(R.id.myemail);

        mAuth = FirebaseAuth.getInstance();
        FirebaseAuth.getInstance().getCurrentUser();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        databaseReference.child("Teachers").child(firebaseUser.getUid()).child("name").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    name.setText(String.valueOf(task.getResult().getValue()));
                    Log.d("TextView",name.getText().toString());

                }
            }
        });

        String e = firebaseUser.getEmail();
        email.setText(e);

        navigationView.setNavigationItemSelectedListener(this);

        actionBarDrawerToggle=new ActionBarDrawerToggle(this,drawer,toolbar,R.string.open,R.string.close);
        drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

        navigationView.setCheckedItem(R.id.home);
        fragmentManager=getSupportFragmentManager();
        fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.teacher_fragment,new Home());
        fragmentTransaction.commit();

        /*Intent intent=getIntent();
        String i=intent.getStringExtra("Intent");

        switch (i){
            case "Student":
                getSupportActionBar().setTitle("Student");
                fragmentManager=getSupportFragmentManager();
                navigationView.setCheckedItem(R.id.student);
                fragmentTransaction=fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.teacher_fragment,new Student());
                fragmentTransaction.commit();
                break;

            case "Take_Attendence":
                getSupportActionBar().setTitle("Take Attendence");
                fragmentManager=getSupportFragmentManager();
                fragmentTransaction=fragmentManager.beginTransaction();
                navigationView.setCheckedItem(R.id.take_attendence);
                fragmentTransaction.replace(R.id.teacher_fragment,new Take_Attendence());
                fragmentTransaction.commit();
                break;

            case "View_Attendence":
                getSupportActionBar().setTitle("View Attendence");
                navigationView.setCheckedItem(R.id.view_attendence);
                fragmentManager=getSupportFragmentManager();
                fragmentTransaction=fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.teacher_fragment,new View_Attendence());
                fragmentTransaction.commit();
                break;
        }*/
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawer.closeDrawer(GravityCompat.START);
        if(item.getItemId() == R.id.home){
            getSupportActionBar().setTitle("Home");
            fragmentManager=getSupportFragmentManager();
            navigationView.setCheckedItem(R.id.home);
            fragmentTransaction=fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.teacher_fragment,new Home());
            fragmentTransaction.commit();
        }
        else if(item.getItemId() == R.id.student){
            getSupportActionBar().setTitle("Student");
            fragmentManager=getSupportFragmentManager();
            navigationView.setCheckedItem(R.id.student);
            fragmentTransaction=fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.teacher_fragment,new Student());
            fragmentTransaction.commit();
        }
        else if(item.getItemId() == R.id.take_attendence){
            getSupportActionBar().setTitle("Take Attendence");
            fragmentManager=getSupportFragmentManager();
            navigationView.setCheckedItem(R.id.take_attendence);
            fragmentTransaction=fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.teacher_fragment,new Take_Attendence());
            fragmentTransaction.commit();
        }
        else if(item.getItemId() == R.id.view_attendence){
            getSupportActionBar().setTitle("View Attendence");
            fragmentManager=getSupportFragmentManager();
            navigationView.setCheckedItem(R.id.view_attendence);
            fragmentTransaction=fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.teacher_fragment,new View_Attendence());
            fragmentTransaction.commit();
        }
        else if(item.getItemId() == R.id.change_pass){
            getSupportActionBar().setTitle("Change PassWord");
            fragmentManager=getSupportFragmentManager();
            navigationView.setCheckedItem(R.id.change_pass);
            fragmentTransaction=fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.teacher_fragment,new Change_Pass());
            fragmentTransaction.commit();
        }
        else if (item.getItemId() == R.id.logout){

            mAuth.signOut();
            Toast.makeText(Teacher_Activity.this, "Signout", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, Login.class));
        }
        return true;
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Teacher_Activity.this);

        builder.setMessage("Do You Want To Logout or Exit ?");

        builder.setTitle("Alert !");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
                ActivityCompat.finishAffinity(Teacher_Activity.this);
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