package com.example.coursemanager;

import android.app.AlertDialog;
import android.content.Intent;
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

import com.example.coursemanager.databinding.FragmentFirstAdminBinding;
import com.example.coursemanager.ui.login.Course;
import com.example.coursemanager.ui.login.LoginActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirstFragmentAdmin extends Fragment {

    private FragmentFirstAdminBinding binding;
    static String Code;
    static List<String> names;
    static int i;
    static int k;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button cancel;
    private Button finalDelete;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstAdminBinding.inflate(inflater, container, false);
        MainActivityAdmin activity = (MainActivityAdmin) getActivity();
        binding.textviewFirst.setText("Welcome, " + activity.getUsername() + "!");
        setAdminTable(binding.addTable, activity.getTableName());
        names = new ArrayList<String>();
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.toCreateCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragmentAdmin.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
                ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        });

        binding.adminBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    protected void setAdminTable(TableLayout table, String child_name) {
        DatabaseReference courseRef = FirebaseDatabase
                .getInstance("https://course-manager-b07-default-rtdb.firebaseio.com/")
                .getReference().child("Courses");
        courseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                i=0;
                for (DataSnapshot datasnapshot : snapshot.getChildren()) {
                    Code = "";
                    for (DataSnapshot newSnap : datasnapshot.getChildren()) {
                        if (newSnap.getKey().compareTo("courseCode") == 0) {
                            Code = (String) newSnap.getValue();
                        }
                    }
                    names.add(Code);
                }
                for (String name : names){
                    final String finalName = name;

                    courseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            //This is normal behaviour
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
                            edit.setText("Edit");
                            edit.setTextColor(Color.rgb(0,0,0));
                            edit.setBackgroundColor(Color.rgb(0,239,239));
                            TextView button_space = new TextView(getActivity());
                            space.setId(5000+i);
                            row.addView(button_space);

                            Button delete = new Button(getActivity());
                            delete.setId(6000 + i);
                            row.addView(delete);
                            course.setText(finalName);
                            delete.setText("Delete");
                            delete.setTextColor(Color.rgb(0,0,0));
                            delete.setBackgroundColor(Color.rgb(0,239,239));

                            space.setText("     ");
                            button_space.setText("  ");
                            table.addView(row, i);
                            i++;
                            edit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = getActivity().getIntent();
                                    intent.putExtra("courseCode", finalName);
                                    intent.putExtra("oldCourse", snapshot.child(finalName).getValue(Course.class));

                                    NavHostFragment.findNavController(FirstFragmentAdmin.this)
                                            .navigate(R.id.action_FirstFragment_to_thirdFragmentAdmin);
                                }
                            });
                            delete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialogBuilder = new AlertDialog.Builder(getActivity());
                                    final View contactPopupView = getLayoutInflater().inflate(R.layout.popup, null);
                                    cancel = contactPopupView.findViewById(R.id.delete_cancel);
                                    finalDelete = contactPopupView.findViewById(R.id.delete_delete);

                                    dialogBuilder.setView(contactPopupView);
                                    dialog = dialogBuilder.create();
                                    dialog.show();

                                    cancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                        }
                                    });
                                    finalDelete.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            // gets each Course
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
                                                    }

                                                    courseRef.child(ds.child("courseCode").getValue().toString()).child("prereqs").setValue(deletion);
                                                }
                                            }

                                            //remove course from any student account as well
                                            DatabaseReference ref = FirebaseDatabase.getInstance("https://course-manager-b07-default-rtdb.firebaseio.com/").getReference();

                                            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                    for (DataSnapshot child : snapshot.child("students").getChildren()) {
                                                        if (child.child("coursesTaken").hasChild(finalName)) {
                                                            ref.child("students").child(child.getKey()).child("coursesTaken").child(finalName).removeValue();
                                                        }
                                                        if (child.child("coursesWanted").hasChild(finalName)) {
                                                            ref.child("students").child(child.getKey()).child("coursesWanted").child(finalName).removeValue();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                }
                                            });

                                            courseRef.child(finalName).removeValue();

                                            NavHostFragment.findNavController(FirstFragmentAdmin.this)
                                                    .navigate(R.id.action_FirstFragment_self2);

                                            dialog.dismiss();
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