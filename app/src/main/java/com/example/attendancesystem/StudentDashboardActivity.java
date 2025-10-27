package com.example.attendancesystem;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.attendancesystem.databinding.ActivityStudentDashboardBinding;

public class StudentDashboardActivity extends AppCompatActivity {

    FrameLayout frameLayout;
    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        frameLayout = findViewById(R.id.frame_layout);
        bottomNavigationView = findViewById(R.id.stu_bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                if (itemId == R.id.attendanceS){
                    loadFrame(new StudentAttendanceFragment(), false);
                } else if (itemId == R.id.history) {
                    loadFrame(new StudentHistoryFragment(), false);
                } else if (itemId == R.id.location) {
                    loadFrame(new StudentLocationFragment(),false);
                }
                return true;
            }
        });
        loadFrame(new StudentAttendanceFragment(), true);
    }

    private void loadFrame(Fragment fragment, boolean isAppInitialized){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (isAppInitialized){
            fragmentTransaction.add(R.id.frame_layout,fragment);
        } else {
            fragmentTransaction.replace(R.id.frame_layout,fragment);
        }
        fragmentTransaction.commit();
    }
}