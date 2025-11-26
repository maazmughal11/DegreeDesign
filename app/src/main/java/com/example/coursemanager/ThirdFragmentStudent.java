package com.example.coursemanager;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.coursemanager.databinding.FragmentThirdStudentBinding;
import com.example.coursemanager.ui.login.Course;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ThirdFragmentStudent extends Fragment {
    private FragmentThirdStudentBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentThirdStudentBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(ThirdFragmentStudent.this)
                        .navigate(R.id.action_thirdFragmentStudent_to_FirstFragment);
            }
        });

        ArrayList<Course> CoursesWanted = new ArrayList<Course>();
        ArrayList<String> CoursesTaken = new ArrayList<String>();
        ArrayList<Course> CoursesScheduled = new ArrayList<Course>();

        DatabaseReference dReference = FirebaseDatabase
                .getInstance("https://course-manager-b07-default-rtdb.firebaseio.com/")
                .getReference();
        dReference.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                DataSnapshot snap = dataSnapshot.child("students").
                        child(((MainActivityStudent) getActivity()).getUsername())
                        .child("coursesTaken");
                for (DataSnapshot snapshot: snap.getChildren()){
                    CoursesTaken.add(snapshot.getKey());
                    // Don't need to add more details for Courses Taken I believe, other than course codes
                }


                DataSnapshot data = dataSnapshot.child("students")
                        .child(((MainActivityStudent) getActivity()).getUsername())
                        .child("coursesWanted");

                for (DataSnapshot snapshot: data.getChildren()){
                    Course WantedCourse = new Course();
                    WantedCourse.setCourseCode(snapshot.getKey());

                    DataSnapshot dataSnapshot1 = dataSnapshot.child("Courses")
                            .child(snapshot.getKey());
                    WantedCourse.setCourseName(dataSnapshot1.child("courseName").getValue(String.class));
                    WantedCourse.setSummer(dataSnapshot1.child("summer").getValue(boolean.class));
                    WantedCourse.setWinter(dataSnapshot1.child("winter").getValue(boolean.class));
                    WantedCourse.setFall(dataSnapshot1.child("fall").getValue(boolean.class));

                    DataSnapshot dataSnapshot2 = dataSnapshot.child("Courses")
                            .child(snapshot.getKey()).child("prereqs");
                    for (DataSnapshot snapshot2: dataSnapshot2.getChildren()){
                        WantedCourse.addPrereqs(snapshot2.getValue(String.class));
                    }

                    CoursesWanted.add(WantedCourse);
                }

                // creating list of things to schedule
                while (!CoursesWanted.isEmpty()) {
                    Course a = CoursesWanted.get(0);
                    CoursesScheduled.add(a);
                    for (String b: a.getPrereqs()){
                        Course c = dataSnapshot.child("Courses").child(b).getValue(Course.class);
                        boolean found = false;
                        for (String d: CoursesTaken){
                            if (d.compareTo(b) == 0){
                                found = true;
                            }
                        }
                        for (Course d: CoursesWanted){
                            if (d.getCourseCode().compareTo(b) == 0){
                                found = true;
                            }
                        }
                        for (Course d: CoursesScheduled){
                            if (d.getCourseCode().compareTo(b) == 0){
                                found = true;
                            }
                        }
                        if (!found){
                            CoursesWanted.add(c);
                        }
                    }
                    CoursesWanted.remove(0);
                }

                // creating schedule
                int i = 0;
                int j = 0;
                boolean morethanfive = false;
                ArrayList<Course> presentSemester = new ArrayList<Course>();
                while(!CoursesScheduled.isEmpty()) {
                    for (Course a: CoursesScheduled){
                        boolean takeable = true;
                        if (j == 0 && !a.isFall()){
                            takeable = false;
                        }
                        if (j == 1 && !a.isWinter()){
                            takeable = false;
                        }
                        if (j == 2 && !a.isSummer()){
                            takeable = false;
                        }
                        for (String b: a.getPrereqs()){
                            boolean pretaken = false;
                            for (String c: CoursesTaken){
                                if (b.compareTo(c) == 0){
                                    pretaken = true;
                                }
                            }
                            if (!pretaken) {
                                takeable = false;
                            }
                        }
                        if (takeable) {
                            presentSemester.add(a);
                        }
                    }

                    //adding row to table
                    int k = 0;
                    int l = 0;
                    TableRow row = new TableRow(getActivity());
                    TableRow.LayoutParams params =
                            new TableRow.LayoutParams(TableRow
                                    .LayoutParams.WRAP_CONTENT);
                    row.setLayoutParams(params);
                    row.setId(1000 + (i * 9 + 3 * j + l));
                    TextView semester = new TextView(getActivity());
                    semester.setId(2000 + (i * 9 + 3 * j + l));
                    semester.setText("");
                    int year = i + 2022;
                    if (j == 0){
                        semester.setText(year + " Fall: ");
                    }
                    if (j == 1){
                        year ++;
                        semester.setText(year + " Winter: ");
                    }
                    if (j == 2){
                        year ++;
                        semester.setText(year + " Summer: ");
                    }
                    semester.setTypeface(semester.getTypeface(), Typeface.BOLD);
                    row.addView(semester);
                    binding.table.addView(row,i * 9 + 3 * j + l);
                    l++;

                    TableRow row2 = new TableRow(getActivity());
                    TableRow.LayoutParams params2 =
                            new TableRow.LayoutParams(TableRow
                                    .LayoutParams.WRAP_CONTENT);
                    row2.setLayoutParams(params2);
                    row2.setId(4000 + (i * 9 + 3 * j + l));
                    TextView courseList = new TextView(getActivity());
                    String longName = "";
                    while (!presentSemester.isEmpty()){
                        Course a = presentSemester.get(0);
                        if (k != 0){
                            longName = longName + ", ";
                        }
                        longName = longName + a.getCourseCode();
                        for (Course b: CoursesScheduled){
                            if (b.getCourseCode().compareTo(a.getCourseCode()) == 0){
                                CoursesScheduled.remove(b);
                                break;
                            }
                        }
                        CoursesTaken.add(a.getCourseCode());
                        presentSemester.remove(0);
                        k ++;
                    }
                    if (k > 5){
                        morethanfive = true;
                    }
                    courseList.setText(longName);
                    courseList.setId(5000 + (i * 9 + 3 * j + l));
                    row2.addView(courseList);
                    binding.table.addView(row2, i * 9 + 3 * j + l);
                    l++;

                    TableRow spaceRow2 = new TableRow(getActivity());
                    TableRow.LayoutParams spaceParams2 =
                            new TableRow.LayoutParams(TableRow
                                    .LayoutParams.WRAP_CONTENT);
                    spaceRow2.setLayoutParams(spaceParams2);
                    spaceRow2.setId(3000 + (i * 9 + 3 * j + l));
                    TextView space2 = new TextView(getActivity());
                    space2.setText("   ");
                    spaceRow2.addView(space2);
                    binding.table.addView(spaceRow2, i * 9 + 3 * j + l);

                    j ++;
                    if (j == 3){
                        i++;
                        j = 0;
                    }
                }

                if (morethanfive){
                    binding.textView.setText("Please consult with your program coordinator");
                    binding.textView2.setText("before taking more than 5 courses in a semester");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }
}