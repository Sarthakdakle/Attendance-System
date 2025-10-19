package com.example.attendancesystem;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;
import com.example.attendancesystem.R;

public class StudentDashboardActivity extends AppCompatActivity {
    MaterialCardView cardToday, cardHistory, cardProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        cardToday = findViewById(R.id.cardToday);
        cardHistory = findViewById(R.id.cardHistory);
        cardProfile = findViewById(R.id.cardProfile);

        cardToday.setOnClickListener(v -> {
            // Show today’s attendance (dummy for now)
        });

        cardHistory.setOnClickListener(v ->
                startActivity(new Intent(this, AttendanceHistory.class))
        );

        cardProfile.setOnClickListener(v -> {

        });
    }
}
