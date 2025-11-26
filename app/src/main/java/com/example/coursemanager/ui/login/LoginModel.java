package com.example.coursemanager.ui.login;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

// ***
// *** This is the Model component of the login module
// ***

public class LoginModel {
    private DatabaseReference ref = FirebaseDatabase.getInstance("https://course-manager-b07-default-rtdb.firebaseio.com/").getReference();

    private User user;

    private int[] loginAction = new int[1];
    private int[] registerAction = new int[1];

    public int getLoginAction() {
        return loginAction[0];
    }

    public int getRegisterAction() {
        return registerAction[0];
    }

    public boolean checkPassword(String pass) {
        return(user.password.compareTo(pass) == 0);
    }

    public boolean checkUser(User user) {
        return(this.user.equals(user));
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void createNewAccount() {
        ref.child("students").child(user.username).setValue(user);
    }

    public void checkStudentInDB() {
        ref.child("students").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (user.getUsername().compareTo("") == 0) {
                    //does nothing but prevents app from crashing
                }
                //if the username is correct, continue with the checks, otherwise, display msg
                else if (snapshot.hasChild(user.getUsername())) {
                    ref.child("students").child(user.getUsername()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {

                            DataSnapshot ds = task.getResult();
                            User login = ds.getValue(User.class);
                            if (checkPassword(login.getPassword())) {
                                // Checks if the password entered matches the password of the given username.
                                loginAction[0] = 3;
                                registerAction[0] = 1;

                            } else {
                                //wrong password msg
                                loginAction[0] = 2;
                                registerAction[0] = 1;

                            }
                        }
                    });
                } else {
                    loginAction[0] = 1;
                    registerAction[0] = 2;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void checkAdminInDB() {
        ref.child("admins").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (user.getUsername().compareTo("") == 0) {
                    //does nothing but prevents app from crashing
                }
                //if the username is correct, continue with the checks, otherwise, display msg
                else if (snapshot.hasChild(user.getUsername())) {
                    ref.child("admins").child(user.getUsername()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {

                            DataSnapshot ds = task.getResult();
                            User login = ds.getValue(User.class);
                            if (checkPassword(login.getPassword())) {

                                // Checks if the password entered matches the password of the given username.
                                // If it does, bring them to the admin landing page
                                loginAction[0] = 4;
                            } else {
                                //wrong password msg
                                loginAction[0] = 2;
                            }
                        }
                    });
                } else {
                    loginAction[0] = 1;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}

