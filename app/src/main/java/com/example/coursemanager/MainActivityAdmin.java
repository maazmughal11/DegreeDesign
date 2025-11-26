package com.example.coursemanager;

import android.os.Bundle;

import com.example.coursemanager.ui.login.Course;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.coursemanager.databinding.ActivityMainAdminBinding;

public class MainActivityAdmin extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainAdminBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(false);
        }


        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main_activity_admin);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main_activity_admin);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public String getUsername() {
        String name = "";
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            name = bundle.getString("username");
        }
        return name;
    }

    public String getTableName(){
        String name = "";
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            name = bundle.getString("Table Name");
        }
        return name;
    }

    public String getCourseCode() {
        String name = "";
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            name = bundle.getString("courseCode");
        }
        return name;
    }

    public Course getOldCourse(){
        Course old = new Course();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            old = (Course) bundle.getSerializable("oldCourse");
        }
        return old;
    }

}