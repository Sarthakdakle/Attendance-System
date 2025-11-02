package com.example.attendancesystem;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TakeAttendance extends Fragment {

    private ImageView btn_profile;
    private TextView tv_date;
    private RecyclerView rv_student_list;
    private Button btn_save_attendance;
    private StudentAdapter adapter;
    private List<StudentAttendanceModel> studentList;
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_take_attendance, container, false);

        btn_profile = view.findViewById(R.id.btn_profile);
        btn_save_attendance = view.findViewById(R.id.btn_save_attendance);
        tv_date = view.findViewById(R.id.tv_date);
        rv_student_list = view.findViewById(R.id.rv_student_list);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        studentList = new ArrayList<>();
        adapter = new StudentAdapter(getContext(), studentList);
        rv_student_list.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_student_list.setAdapter(adapter);

        String currentDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());
        tv_date.setText("Date: " + currentDate);

        btn_profile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Teacher_Profile.class);
            startActivity(intent);
        });

        fetchStudents();

        btn_save_attendance.setOnClickListener(v -> saveAttendance());

        return view;
    }

    private void fetchStudents() {
        db.collection("users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    studentList.clear();
                    for (var doc : queryDocumentSnapshots.getDocuments()) {
                        String name = doc.getString("name");
                        StudentAttendanceModel student = new StudentAttendanceModel(name, false);
                        studentList.add(student);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error fetching students: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveAttendance() {
        List<StudentAttendanceModel> updatedList = adapter.getStudentList();
        String date = new java.text.SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new java.util.Date());

        for (StudentAttendanceModel student : updatedList) {
            Map<String, Object> attendanceData = new HashMap<>();
            attendanceData.put("studentName", student.getName());
            attendanceData.put("date", date);
            attendanceData.put("status", student.isPresent() ? "Present" : "Absent");

            db.collection("teacher_attendance")
                    .add(attendanceData)
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error saving attendance: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }

        Toast.makeText(getContext(), "Attendance saved to Firestore!", Toast.LENGTH_SHORT).show();
    }
}
