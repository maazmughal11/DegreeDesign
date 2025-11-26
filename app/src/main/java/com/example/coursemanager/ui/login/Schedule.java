package com.example.coursemanager.ui.login;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Schedule {
    public Course course;
    int year;
    String semester;
    static boolean k;

    // This constructor is used for using the whole algorithm
    public Schedule() {
        this.course = null;
        this.year = 0;
        this.semester = "";
    }

    // This one is used while creating the schedule
    public Schedule(Course course, int year, String semester) {
        this.course = course;
        this.year = year;
        this.semester = semester;
    }

    // Uses recursion to create a total list for all the courses needed
    public ArrayList<Course> CreateTotalCoursesArray(ArrayList<Course> CoursesTaken, ArrayList<Course> CoursesWanted, ArrayList<Course> TotalCourses) {


        for (int i = 0; i < CoursesWanted.size(); i++) {
            if (CoursesWanted.get(i).prereqs.isEmpty() && !CoursesTaken.contains(CoursesWanted.get(i)) && !TotalCourses.contains(CoursesWanted.get(i))) {
                TotalCourses.add(CoursesWanted.get(i));
                return TotalCourses;
            } else if (!CoursesTaken.contains(CoursesWanted.get(i)) && !TotalCourses.contains(CoursesWanted.get(i))) {
                // need line here to convert string prereqs list to course prereqs list
                ArrayList<String> wanted = CoursesWanted.get(i).prereqs;
                ArrayList<Course> wantedCourses = new ArrayList<Course>();
                for (String thing: wanted){
                    k = true;
                    DatabaseReference ref = FirebaseDatabase
                            .getInstance("https://course-manager-b07-default-rtdb.firebaseio.com/")
                            .getReference().child("Courses").child(thing);
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            wantedCourses.add(snapshot.getValue(Course.class));
                            k = false;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    //this essentially forces the async task to be synchronous
                    while(k){

                    }

                }


                TotalCourses = CreateTotalCoursesArray(CoursesTaken, wantedCourses, TotalCourses);
                TotalCourses.add(CoursesWanted.get(i));
            }

        }
        return TotalCourses;

    }

    /* Functions goes through total courses thrice, checking for fall winter and summer courses and if prereqs are satisfied then it is added to a Schedule List
       -- this is repeated until all courses have been added and the degree year is also incremented */

    public ArrayList<Schedule> CreateSchedule(ArrayList<Course> CoursesTaken, ArrayList<Course> TotalCourses){
        ArrayList<Schedule> DegreeSchedule = new ArrayList<Schedule>();
        int DegreeYear = 1;
        ArrayList<Course> PresentSemCourses = new ArrayList<Course>();
        // Can create CoursesTakenString outside for loop to reduce efficiency tbf

        while (!TotalCourses.isEmpty()) {


            PresentSemCourses.clear();
            for (int i = 0; i < TotalCourses.size(); i++) {
                if (TotalCourses.get(i).fall) {
                    if (TotalCourses.get(i).prereqs.isEmpty()) {
                        DegreeSchedule.add(new Schedule(TotalCourses.get(i), DegreeYear, "fall"));
                        PresentSemCourses.add(TotalCourses.get(i));
                    }

                    else {
                        boolean PreReqsSatisfied = true;
                        ArrayList<String> CoursesTakenString = new ArrayList<String>();
                        for (int z = 0; z < CoursesTaken.size(); z++ ){
                            CoursesTakenString.add(CoursesTaken.get(z).courseCode);
                        }
                        for (int j = 0; j < TotalCourses.get(i).prereqs.size(); j++) {
                            // TotalCourses.get(i).prereqs.get(j) will be a String, should be a converted to a course in all three for loops
                            // or convert courses taken to strings - I have done the latter, need to test though
                            if (!CoursesTakenString.contains(TotalCourses.get(i).prereqs.get(j))) {
                                PreReqsSatisfied = false;
                                break;
                            }
                        }

                        if (PreReqsSatisfied) {
                            DegreeSchedule.add(new Schedule(TotalCourses.get(i), DegreeYear, "fall"));
                            PresentSemCourses.add(TotalCourses.get(i));
                        }
                    }
                }

                if (PresentSemCourses.size() == 6) {
                    break;
                }

            }
            CoursesTaken.addAll(PresentSemCourses);
            TotalCourses.removeAll(PresentSemCourses);


            //Moving this here because 2022 fall is followed by 2023 winter, and so on
            //This will be more accurate for how the semesters work in practice
            DegreeYear += 1;

            PresentSemCourses.clear();
            for (int i = 0; i < TotalCourses.size(); i++) {
                if (TotalCourses.get(i).winter) {
                    if (TotalCourses.get(i).prereqs.isEmpty()) {
                        DegreeSchedule.add(new Schedule(TotalCourses.get(i), DegreeYear, "winter"));
                        PresentSemCourses.add(TotalCourses.get(i));
                    }

                    else {
                        boolean PreReqsSatisfied = true;
                        ArrayList<String> CoursesTakenString = new ArrayList<String>();
                        for (int z = 0; z < CoursesTaken.size(); z++ ){
                            CoursesTakenString.add(CoursesTaken.get(z).courseCode);
                        }
                        for (int j = 0; j < TotalCourses.get(i).prereqs.size(); j++) {
                            if (!CoursesTakenString.contains(TotalCourses.get(i).prereqs.get(j))) {
                                PreReqsSatisfied = false;
                                break;
                            }
                        }

                        if (PreReqsSatisfied) {
                            DegreeSchedule.add(new Schedule(TotalCourses.get(i), DegreeYear, "winter"));
                            PresentSemCourses.add(TotalCourses.get(i));
                        }
                    }
                }

                if (PresentSemCourses.size() == 6) {
                    break;
                }

            }
            CoursesTaken.addAll(PresentSemCourses);
            TotalCourses.removeAll(PresentSemCourses);

            PresentSemCourses.clear();
            for (int i = 0; i < TotalCourses.size(); i++) {
                if (TotalCourses.get(i).summer) {
                    if (TotalCourses.get(i).prereqs.isEmpty()) {
                        DegreeSchedule.add(new Schedule(TotalCourses.get(i), DegreeYear, "summer"));
                        PresentSemCourses.add(TotalCourses.get(i));
                    }

                    else {
                        boolean PreReqsSatisfied = true;
                        ArrayList<String> CoursesTakenString = new ArrayList<String>();
                        for (int z = 0; z < CoursesTaken.size(); z++ ){
                            CoursesTakenString.add(CoursesTaken.get(z).courseCode);
                        }
                        for (int j = 0; j < TotalCourses.get(i).prereqs.size(); j++) {
                            if (!CoursesTakenString.contains(TotalCourses.get(i).prereqs.get(j))) {
                                PreReqsSatisfied = false;
                                break;
                            }
                        }

                        if (PreReqsSatisfied) {
                            DegreeSchedule.add(new Schedule(TotalCourses.get(i), DegreeYear, "summer"));
                            PresentSemCourses.add(TotalCourses.get(i));
                        }
                    }
                }

                if (PresentSemCourses.size() == 6) {
                    break;
                }

            }
            CoursesTaken.addAll(PresentSemCourses);
            TotalCourses.removeAll(PresentSemCourses);

        }


        return DegreeSchedule;

    }

    public void PrintSchedule (ArrayList <Schedule> DegreeSchedule) {
        for (int i = 0; i < DegreeSchedule.size(); i++) {
            System.out.println(DegreeSchedule.get(i).course.courseCode);
            System.out.println(DegreeSchedule.get(i).semester);
            System.out.println(DegreeSchedule.get(i).year);
        }
    }
}

