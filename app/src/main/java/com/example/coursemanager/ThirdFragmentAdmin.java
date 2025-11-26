package com.example.coursemanager;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.coursemanager.databinding.FragmentThirdAdminBinding;
import com.example.coursemanager.ui.login.Course;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ThirdFragmentAdmin extends Fragment {
    private FragmentThirdAdminBinding binding;
    static Course course = new Course();
    static int i;
    static int exists;
    static ArrayList<String> prereqList = new ArrayList<>();
    static String req;
    static Course edit_prereq_course = new Course();
    static Course old_course = new Course();


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentThirdAdminBinding.inflate(inflater, container, false);

        MainActivityAdmin activity = (MainActivityAdmin) getActivity();
        binding.editingCourse.setText("Edit " + activity.getCourseCode());

        old_course = activity.getOldCourse();

        setPrereqTable(binding.addTable, activity.getCourseCode());

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainActivityAdmin activity = (MainActivityAdmin) getActivity();
        String editCourseCode = activity.getCourseCode();

        final EditText CourseName = binding.editcourseName;
        final EditText CourseCode = binding.editcoursecode;
        final EditText Prereq = binding.editprereq;



        DatabaseReference courseRef = FirebaseDatabase
                .getInstance("https://course-manager-b07-default-rtdb.firebaseio.com/")
                .getReference().child("Courses");

        binding.editBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                courseRef.child(editCourseCode).setValue(old_course);

                NavHostFragment.findNavController(ThirdFragmentAdmin.this)
                        .navigate(R.id.action_thirdFragmentAdmin_to_FirstFragment);
            }
        });

        courseRef.child(editCourseCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("courseName").exists()){
                    CourseName.setText(snapshot.child("courseName").getValue().toString());
                }
                else{
                    CourseName.setText("");
                }

                CourseCode.setText(snapshot.child("courseCode").getValue().toString());

                CheckBox editfallbox = getView().findViewById(R.id.editfall);
                editfallbox.setChecked(snapshot.child("fall").getValue().toString().compareTo("true") == 0);
                course.setFall(editfallbox.isChecked());

                CheckBox editwinterbox = getView().findViewById(R.id.editwinter);
                editwinterbox.setChecked(snapshot.child("winter").getValue().toString().compareTo("true") == 0);
                course.setWinter(editwinterbox.isChecked());

                CheckBox editsummerbox = getView().findViewById(R.id.editsummer);
                editsummerbox.setChecked(snapshot.child("summer").getValue().toString().compareTo("true") == 0);
                course.setSummer(editsummerbox.isChecked());


                getView().findViewById(R.id.editadd_prereq).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String s = Prereq.getText().toString();
                        courseRef.child(s).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot presnapshot) {
                                edit_prereq_course = snapshot.getValue(Course.class);
                                if(presnapshot.exists()){
                                    if(edit_prereq_course.getPrereqs().contains(s)){
                                        String warningMsg = "This course is already a prerequisite";
                                        Toast.makeText(getActivity(), warningMsg, Toast.LENGTH_LONG).show();
                                    }
                                    else if(s.compareTo("")==0){
                                        String warningMsg = "Please specify the prerequisite";
                                        Toast.makeText(getActivity(), warningMsg, Toast.LENGTH_LONG).show();
                                    }
                                    else if(s.compareTo(editCourseCode)==0){
                                        String warningMsg = "The course cannot be a prerequisite\nfor itself";
                                        Toast.makeText(getActivity(), warningMsg, Toast.LENGTH_LONG).show();
                                    }
                                    else{
                                        edit_prereq_course.addPrereqs(s);

                                        course.setPrereqs(edit_prereq_course.getPrereqs());
                                        course.setCourseCode(edit_prereq_course.getCourseCode());

                                        courseRef.child(editCourseCode).setValue(course);

                                        String warningMsg = "Prerequisite Added";
                                        Toast.makeText(getActivity(), warningMsg, Toast.LENGTH_LONG).show();

                                        NavHostFragment.findNavController(ThirdFragmentAdmin.this)
                                                .navigate(R.id.action_thirdFragmentAdmin_self);

                                    }
                                }
                                else{
                                    String warningMsg = "This course does not yet exist.";
                                    Toast.makeText(getActivity(), warningMsg, Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Prereq.setText("");

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        TextWatcher course_name_watcher = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                course.setCourseName(s.toString());
                courseRef.child(editCourseCode).child("courseName").setValue(s.toString());
            }
        };

        CourseName.addTextChangedListener(course_name_watcher);

        TextWatcher course_code_watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                course.setCourseCode(s.toString());
                courseRef.child(editCourseCode).child("courseCode").setValue(s.toString());
            }
        };

        CourseCode.addTextChangedListener(course_code_watcher);


        // Checks if the session checkbox is checked or not and assigns the corresponding boolean to our Course object
        CheckBox fallbox = getView().findViewById(R.id.editfall);
        fallbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                course.setFall(fallbox.isChecked());
                courseRef.child(editCourseCode).child("fall").setValue(fallbox.isChecked());
            }
        });

        // Checks if the session checkbox is checked or not and assigns the corresponding boolean to our Course object
        CheckBox winterbox = getView().findViewById(R.id.editwinter);
        winterbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                course.setWinter(winterbox.isChecked());
                courseRef.child(editCourseCode).child("winter").setValue(winterbox.isChecked());
            }
        });

        // Checks if the session checkbox is checked or not and assigns the corresponding boolean to our Course object
        CheckBox summerbox = getView().findViewById(R.id.editsummer);
        summerbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                course.setSummer(summerbox.isChecked());
                courseRef.child(editCourseCode).child("summer").setValue(summerbox.isChecked());
            }
        });


//      Use the "Edit Course" button to add a course to the database with the input information
        getView().findViewById(R.id.edit_course_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Use this listener on the course code that is being enetered to check if exists in the database already
                courseRef.child(editCourseCode).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        // Block course creation if no sections are selected
                        if (!course.isFall() && !course.isWinter() && !course.isSummer()) {
                            String warningMsg = "Please select offering sessions";
                            Toast.makeText(getActivity(), warningMsg, Toast.LENGTH_LONG).show();
                        }
                        // Block course creation if the user does not enter a course name
                        else if (course.getCourseName().compareTo("") == 0) {
                            String warningMsg = "Please enter a course name";
                            Toast.makeText(getActivity(), warningMsg, Toast.LENGTH_LONG).show();
                        }
                        else if (CourseCode.getText().toString().length() != 6 || !CourseCode.getText().toString().substring(0,3).matches("[A-Z]+") || !CourseCode.getText().toString().substring(3,4).matches("^[A-D]") || !CourseCode.getText().toString().substring(4).matches("^[0-9]*$")){
                            String warningMsg = "Please enter a valid course code";
                            Toast.makeText(getActivity(), warningMsg, Toast.LENGTH_LONG).show();
                        }
                        else {
                            // If you changed the course code we need to delete the old node and make a new one
                            if (old_course.getCourseCode().compareTo(course.getCourseCode()) != 0){
                                // Must check the user isn't trying to change the course code to one that already exists
                                courseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot snap: snapshot.getChildren()){
                                            // If we find a course that already exists with then block it
                                            if(snap.getKey().compareTo(course.getCourseCode()) == 0){
                                                exists = 1;
                                            }
                                        }
                                        // Block it
                                        if (exists == 1){
                                            String warningMsg = "A course with this code already";
                                            Toast.makeText(getActivity(), warningMsg, Toast.LENGTH_LONG).show();
                                            exists = 0;
                                        }
                                        // We didn't find a course with the new course code so we can go ahead and change it
                                        else{
                                            course.setPrereqs(edit_prereq_course.getPrereqs());

                                            courseRef.child(editCourseCode).removeValue();

                                            courseRef.child(course.getCourseCode()).setValue(course);

                                            // Changing the course code requires us to switch to the new course code in all prereqs of all other courses
                                            courseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    String finalName = old_course.getCourseCode();
                                                    for(DataSnapshot ds: snapshot.getChildren()){
                                                        if(ds.child("courseCode").getValue().toString().compareTo(finalName) != 0 && ds.child("prereqs").exists()){
                                                            ArrayList<String> deletion = (ArrayList) ds.child("prereqs").getValue();

                                                            int isFound = 0;
                                                            for(String item: deletion){
                                                                if(item.compareTo(finalName)==0){
                                                                    isFound = 1;
                                                                }
                                                            }
                                                            if(isFound == 1){
                                                                deletion.remove(finalName);
                                                                deletion.add(course.getCourseCode());
                                                            }

                                                            courseRef.child(ds.child("courseCode").getValue().toString()).child("prereqs").setValue(deletion);
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                            DatabaseReference ref = FirebaseDatabase.getInstance("https://course-manager-b07-default-rtdb.firebaseio.com/").getReference();

                                            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    String finalName = old_course.getCourseCode();
                                                    for (DataSnapshot child : snapshot.child("students").getChildren()) {
                                                        if (child.child("coursesTaken").hasChild(finalName)) {
                                                            ref.child("students").child(child.getKey()).child("coursesTaken").child(finalName).removeValue();
                                                            ref.child("students").child(child.getKey()).child("coursesTaken").child(course.getCourseCode()).setValue("true");
                                                        }
                                                        if (child.child("coursesWanted").hasChild(finalName)) {
                                                            ref.child("students").child(child.getKey()).child("coursesWanted").child(finalName).removeValue();
                                                            ref.child("students").child(child.getKey()).child("coursesTaken").child(course.getCourseCode()).setValue("true");
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                }
                                            });

                                            String warningMsg = "Course successfully edited";
                                            Toast.makeText(getActivity(), warningMsg, Toast.LENGTH_LONG).show();

                                            NavHostFragment.findNavController(ThirdFragmentAdmin.this)
                                                    .navigate(R.id.action_thirdFragmentAdmin_to_FirstFragment);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                            // If we didn't change the course code just update everything else
                            else{
                                courseRef.child(editCourseCode).child("courseName").setValue(course.getCourseName());
                                courseRef.child(editCourseCode).child("fall").setValue(course.isFall());
                                courseRef.child(editCourseCode).child("winter").setValue(course.isWinter());
                                courseRef.child(editCourseCode).child("summer").setValue(course.isSummer());

                                String warningMsg = "Course successfully edited";
                                Toast.makeText(getActivity(), warningMsg, Toast.LENGTH_LONG).show();

                                NavHostFragment.findNavController(ThirdFragmentAdmin.this)
                                        .navigate(R.id.action_thirdFragmentAdmin_to_FirstFragment);
                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;


    }

    protected void setPrereqTable(TableLayout table, String child_name) {
        prereqList.clear();
        DatabaseReference courseRef = FirebaseDatabase
                .getInstance("https://course-manager-b07-default-rtdb.firebaseio.com/")
                .getReference().child("Courses").child(child_name);
        courseRef.child("prereqs").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    i = 0;
                    for (DataSnapshot datasnapshot : snapshot.getChildren()) {
                        req = (String) datasnapshot.getValue();
                        prereqList.add(req);
                    }
                    for (String req : prereqList){
                        final String currReq = req;

                        courseRef.child("prereqs").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
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

                                Button delete = new Button(getActivity());
                                delete.setId(4000 + i);
                                row.addView(delete);
                                delete.setText("Remove");
                                delete.setTextColor(Color.rgb(0,0,0));
                                delete.setBackgroundColor(Color.rgb(0,239,239));

                                space.setText("     ");
                                course.setText(req);
                                table.addView(row, i);
                                i++;

                                delete.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        courseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                Course a = snapshot.getValue(Course.class);
                                                a.getPrereqs().remove(req);
                                                courseRef.setValue(a);

                                                NavHostFragment.findNavController(ThirdFragmentAdmin.this)
                                                        .navigate(R.id.action_thirdFragmentAdmin_self);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                    }
                                });

                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

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

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }
}