package com.example.attendancesystem;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button login_btn, register_btn, teacher_lg, student_lg;
    EditText etEmail, etPassword, etUsername;
    TextView forgot_password;
    boolean isTeacher = true;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // UI References
        login_btn = findViewById(R.id.btnLogin);
        register_btn = findViewById(R.id.btnRegister);
        teacher_lg = findViewById(R.id.teacher_lg);
        student_lg = findViewById(R.id.student_lg);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        forgot_password = findViewById(R.id.forgot_password);
        etUsername = findViewById(R.id.et_username);

        // Switch Between Teacher / Student
        teacher_lg.setOnClickListener(v -> switchRole(true));
        student_lg.setOnClickListener(v -> switchRole(false));

        // Login
        login_btn.setOnClickListener(v -> loginUser());

        // Register
        register_btn.setOnClickListener(v -> registerUser());

        // Forgot Password
//        forgot_password.setOnClickListener(v -> {
//            String email = etEmail.getText().toString().trim();
//            if (TextUtils.isEmpty(email)) {
//                Toast.makeText(this, "Enter your email to reset password", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            mAuth.sendPasswordResetEmail(email)
//                    .addOnSuccessListener(unused -> Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show())
//                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to send email", Toast.LENGTH_SHORT).show());
//        });
    }

    private void switchRole(boolean teacher) {
        isTeacher = teacher;

        if (teacher) {
            teacher_lg.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.red));
            teacher_lg.setTextColor(getResources().getColor(R.color.white));
            student_lg.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.white));
            student_lg.setTextColor(getResources().getColor(R.color.red));
        } else {
            student_lg.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.red));
            student_lg.setTextColor(getResources().getColor(R.color.white));
            teacher_lg.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.white));
            teacher_lg.setTextColor(getResources().getColor(R.color.red));
        }
    }

    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String username = etUsername.getText().toString().trim();

        if (!isValidInput(email, password, username)) return;

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                String role = isTeacher ? "teacher" : "student";

                Map<String, Object> userData = new HashMap<>();
                userData.put("uid", user.getUid());
                userData.put("name", username);
                userData.put("email", email);
                userData.put("role", role);

                db.collection("users").document(user.getUid()).set(userData)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                            redirectUser(role);
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String username = etUsername.getText().toString().trim();

        if (!isValidInput(email, password, null)) return;

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                db.collection("users").document(user.getUid()).get()
                        .addOnSuccessListener(document -> {
                            if (document.exists()) {
                                String role = document.getString("role");
                                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                                redirectUser(role);
                            } else {
                                Toast.makeText(this, "User role not found", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Login Failed"+ e.getMessage(), Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValidInput(String email, String password, String username) {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Enter valid email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (username != null && TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void redirectUser(String role) {
        Intent intent;
        if ("teacher".equals(role)) {
            intent = new Intent(this, TeacherDashboardActivity.class);
        } else {
            intent = new Intent(this, StudentDashboardActivity.class);
        }
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            db.collection("users").document(user.getUid()).get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            String role = document.getString("role");
                            redirectUser(role);
                        }
                    });
        }
    }
}
