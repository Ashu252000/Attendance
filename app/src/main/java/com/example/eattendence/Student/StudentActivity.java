package com.example.eattendence.Student;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

//import com.example.attendence.DatabaseHelper;
import com.example.eattendence.Login;
import com.example.eattendence.R;
import com.example.eattendence.Student.Student_Fragment.Change_Pass.Change_Pass;
import com.example.eattendence.Student.Student_Fragment.Home.Home;
import com.example.eattendence.Student.Student_Fragment.View_Attendance.View_Attendence;
import com.example.eattendence.Teacher.Teacher_Fragment.Student.Student;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StudentActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    FragmentTransaction fragmentTransaction;
    FragmentManager fragmentManager;
    FirebaseAuth mAuth;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String Inputprn;
    String Inputcourse;
    FirebaseUser fuser;
    Context context;
    String s;
    String User;

    static int set=0;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=FirebaseDatabase.getInstance().getReference();

        context=this;

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");

        NavigationView v =findViewById(R.id.student_nav_view);
        View view=v.getHeaderView(0);
        ImageView logo=view.findViewById(R.id.imageView);
        TextView Username=view.findViewById(R.id.myusernm);
        TextView email = view.findViewById(R.id.myemail);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user=mAuth.getCurrentUser();

        databaseReference.child("Students").child(user.getUid()).child("name").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Username.setText(String.valueOf(task.getResult().getValue()));
                    Log.d("TextView",Username.getText().toString());

                }
            }
        });

        //Glide.with(this).load(String.valueOf(personPhoto)).into(logo);

        email.setText(user.getEmail());



        drawer = findViewById(R.id.student_drawer);
        navigationView = findViewById(R.id.student_nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        actionBarDrawerToggle=new ActionBarDrawerToggle(this,drawer,toolbar,R.string.open,R.string.close);
        drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

        navigationView.setCheckedItem(R.id.home);
        fragmentManager=getSupportFragmentManager();
        fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.student_fragment,new Home());
        fragmentTransaction.commit();


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
            fragmentTransaction.replace(R.id.student_fragment,new Home());
            fragmentTransaction.commit();
        }
        else if(item.getItemId() == R.id.view_attendence){
            getSupportActionBar().setTitle("View Attendence");
            navigationView.setCheckedItem(R.id.view_attendence);
            fragmentManager=getSupportFragmentManager();
            fragmentTransaction=fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.student_fragment,new View_Attendence());
            fragmentTransaction.commit();
        }
        else if(item.getItemId() == R.id.change_pass){
            getSupportActionBar().setTitle("Change Password");
            navigationView.setCheckedItem(R.id.change_pass);
            fragmentManager=getSupportFragmentManager();
            fragmentTransaction=fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.student_fragment,new Change_Pass());
            fragmentTransaction.commit();
        }
        else if (item.getItemId() == R.id.logout){

            mAuth.signOut();
            Toast.makeText(StudentActivity.this, "Signout", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, Login.class));
        }
        return true;
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(StudentActivity.this);

        builder.setMessage("Do You Want To Logout or Exit ?");

        builder.setTitle("Alert !");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
                ActivityCompat.finishAffinity(StudentActivity.this);
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