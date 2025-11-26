package com.example.coursemanager.ui.login;

import java.io.Serializable;
import java.util.ArrayList;

public class Course implements Serializable {
    String courseName;
    String courseCode;
    ArrayList<String> prereqs;
    boolean fall;
    boolean winter;
    boolean summer;

    public Course(){
        courseName = "";
        courseCode = "";
        prereqs = new ArrayList<String>();
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public ArrayList<String> getPrereqs() {
        return prereqs;
    }

    public void addPrereqs(String prereqs) {
        this.prereqs.add(prereqs);
    }

    public void setPrereqs(ArrayList<String> s) {
        prereqs = s;
    }

    public boolean isFall() {
        return fall;
    }

    public void setFall(boolean fall) {
        this.fall = fall;
    }

    public boolean isWinter() {
        return winter;
    }

    public void setWinter(boolean winter) {
        this.winter = winter;
    }

    public boolean isSummer() {
        return summer;
    }

    public void setSummer(boolean summer) {
        this.summer = summer;
    }

    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof Course)){
            return false;
        }
        Course course = (Course) obj;

        return (courseCode.compareTo(course.courseCode) == 0);
    }

}
