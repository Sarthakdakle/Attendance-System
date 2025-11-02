package com.example.attendancesystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {

    private Context context;
    private List<StudentAttendanceModel> studentList;

    public StudentAdapter(Context context, List<StudentAttendanceModel> studentList) {
        this.context = context;
        this.studentList = studentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.student_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StudentAttendanceModel student = studentList.get(position);
        holder.tvName.setText(student.getName());
        holder.switchAttendance.setChecked(student.isPresent());

        holder.switchAttendance.setOnCheckedChangeListener((buttonView, isChecked) ->
                student.setPresent(isChecked)
        );
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public List<StudentAttendanceModel> getStudentList() {
        return studentList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        Switch switchAttendance;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_student_name);
            switchAttendance = itemView.findViewById(R.id.attendance_switch);
        }
    }
}
