package com.example.attendancesystem;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class StudentAttendanceFragment extends Fragment {

    private ImageView btn_profile;

    TextView tv_date, tv_status, tv_confirmation_text;

    Button btn_mark_attendance;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_student_attendance, container, false);

        btn_profile = view.findViewById(R.id.btn_profile);
        tv_date = view.findViewById(R.id.tv_date);
        tv_status = view.findViewById(R.id.tv_status);
        tv_confirmation_text = view.findViewById(R.id.tv_confirmation_text);
        btn_mark_attendance = view.findViewById(R.id.btn_mark_attendance);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Student_Profile.class);
                startActivity(intent);
            }
        });

        String date = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());
        tv_date.setText("Date: " + date);

        btn_mark_attendance.setOnClickListener(v -> {
            markAttendance(date);
        });
        return view;
    }

    private void markAttendance(String date) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = user.getUid();

        db.collection("attendance")
                .whereEqualTo("uid", uid)
                .whereEqualTo("date", date)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        tv_status.setText("Status: Present ");
                        btn_mark_attendance.setEnabled(false);
                        Toast.makeText(getContext(), "You already marked attendance for today.", Toast.LENGTH_SHORT).show();
                    } else {
                        db.collection("users").document(uid).get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        String name = documentSnapshot.getString("name");
                                        Map<String, Object> attendanceData = new HashMap<>();
                                        attendanceData.put("uid", uid);
                                        attendanceData.put("name", name);
                                        attendanceData.put("email", user.getEmail());
                                        attendanceData.put("date", date);
                                        attendanceData.put("status", "Present");

                                        db.collection("attendance")
                                                .add(attendanceData)
                                                .addOnSuccessListener(documentReference -> {
                                                    tv_status.setText("Status: Present ");
                                                    btn_mark_attendance.setEnabled(false);
                                                    Toast.makeText(getContext(), "Attendance Marked Successfully!", Toast.LENGTH_SHORT).show();
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(getContext(), "Failed to mark attendance: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                });
                                    }
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(getContext(), "Error checking attendance: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                });
    }
}