package com.example.attendancesystem;

public class StudentAttendanceModel {
    private String name;
    private boolean isPresent;

    public StudentAttendanceModel() {
    }

    public StudentAttendanceModel(String name, boolean isPresent) {
        this.name = name;
        this.isPresent = isPresent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPresent() {
        return isPresent;
    }

    public void setPresent(boolean present) {
        isPresent = present;
    }
}
