package com.example.coursemanager;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.coursemanager.databinding.FragmentSecondStudentBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SecondFragmentStudent extends Fragment {

    private FragmentSecondStudentBinding binding;
    static int i;
    static int k;
    static String courseName;
    static List<String> names;
    static List<String> names2;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondStudentBinding.inflate(inflater, container, false);
        MainActivityStudent activity = (MainActivityStudent) getActivity();
        names = new ArrayList<String>();
        names2 = new ArrayList<String>();
        setTable(binding.addTable, activity.getTableName());
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SecondFragmentStudent.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    protected void setTable(TableLayout table, String child_name) {
        DatabaseReference courseRef = FirebaseDatabase
                .getInstance("https://course-manager-b07-default-rtdb.firebaseio.com/")
                .getReference().child("Courses");
        courseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                i = 0;
                for (DataSnapshot datasnapshot : snapshot.getChildren()) {
                    courseName = "";
                    for (DataSnapshot newSnap : datasnapshot.getChildren()) {
                        if (newSnap.getKey().compareTo("courseCode") == 0) {
                            courseName = (String) newSnap.getValue();
                        }
                    }
                    names.add(courseName);
                }
                for (String name : names){
                    DatabaseReference ref = FirebaseDatabase
                            .getInstance("https://course-manager-b07-default-rtdb.firebaseio.com/")
                            .getReference().child("students")
                            .child(((MainActivityStudent) getActivity()).getUsername())
                            .child(child_name);
                    final String finalName = name;

                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            k = 1;
                            for (DataSnapshot datasnapshot : snapshot.getChildren()) {
                                String course_name = datasnapshot.getKey();
                                if (course_name.compareTo(finalName) == 0){
                                    k = 0;
                                }
                            }
                            if (k == 1) {

                                //This is to prevent students from wishlisting
                                //courses they've already taken
                                if (child_name.compareTo("coursesWanted") == 0) {
                                    DatabaseReference ref2 = FirebaseDatabase
                                            .getInstance("https://course-manager-b07-default-rtdb.firebaseio.com/")
                                            .getReference().child("students")
                                            .child(((MainActivityStudent) getActivity()).getUsername())
                                            .child("coursesTaken");
                                    names2.add(finalName);

                                    ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            k = 1;
                                            for (DataSnapshot datasnapshot : snapshot.getChildren()) {
                                                String course_name2 = datasnapshot.getKey();
                                                if (course_name2.compareTo(names2.get(0)) == 0) {
                                                    k = 0;
                                                }
                                            }
                                            if (k == 1) {
                                                TableRow row = new TableRow(getActivity());
                                                TableRow.LayoutParams params =
                                                        new TableRow.LayoutParams(TableRow
                                                                .LayoutParams.WRAP_CONTENT);
                                                row.setLayoutParams(params);
                                                row.setId(1000 + i);
                                                TextView course = new TextView(getActivity());
                                                course.setId(2000 + i);
                                                row.addView(course);
                                                TextView space = new TextView(getActivity());
                                                space.setId(3000+i);
                                                row.addView(space);
                                                Button edit = new Button(getActivity());
                                                edit.setId(4000 + i);
                                                row.addView(edit);
                                                course.setText(names2.get(0));
                                                edit.setText("Add");
                                                edit.setTextColor(Color.rgb(0,0,0));
                                                edit.setBackgroundColor(Color.rgb(243,204,85));
                                                space.setText("        ");
                                                table.addView(row, i);
                                                i++;
                                                edit.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        ref.child(course.getText().toString()).setValue(true);
                                                        NavHostFragment.findNavController(SecondFragmentStudent.this)
                                                                .navigate(R.id.action_SecondFragment_to_FirstFragment);
                                                    }
                                                });
                                            }
                                            names2.remove(0);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                //This is normal behaviour
                                else {
                                    TableRow row = new TableRow(getActivity());
                                    TableRow.LayoutParams params =
                                            new TableRow.LayoutParams(TableRow
                                                    .LayoutParams.WRAP_CONTENT);
                                    row.setLayoutParams(params);
                                    row.setId(1000 + i);
                                    TextView course = new TextView(getActivity());
                                    course.setId(2000 + i);
                                    row.addView(course);
                                    TextView space = new TextView(getActivity());
                                    space.setId(3000+i);
                                    row.addView(space);
                                    Button edit = new Button(getActivity());
                                    edit.setId(4000 + i);
                                    row.addView(edit);
                                    course.setText(finalName);
                                    edit.setText("Add");
                                    edit.setTextColor(Color.rgb(0,0,0));
                                    edit.setBackgroundColor(Color.rgb(243,204,85));
                                    space.setText("        ");
                                    table.addView(row, i);
                                    i++;
                                    edit.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            ref.child(course.getText().toString()).setValue(true);

                                            //To prevent wishlisting a course already taken
                                            if (child_name.compareTo("coursesTaken") == 0){
                                                DatabaseReference ref3 = FirebaseDatabase
                                                        .getInstance("https://course-manager-b07-default-rtdb.firebaseio.com/")
                                                        .getReference().child("students")
                                                        .child(((MainActivityStudent) getActivity()).getUsername())
                                                        .child("coursesWanted");
                                                ref3.child(course.getText().toString()).removeValue();
                                            }

                                            NavHostFragment.findNavController(SecondFragmentStudent.this)
                                                    .navigate(R.id.action_SecondFragment_to_FirstFragment);
                                        }
                                    });
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }
}