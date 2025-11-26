package com.example.coursemanager.ui.login;

import androidx.lifecycle.ViewModel;
import com.example.coursemanager.data.LoginRepository;

// ***
// *** This is the Presenter component of the login module
// ***

public class LoginPresenter extends ViewModel {

    private LoginRepository loginRepository;

    private LoginModel loginModel;
    private LoginActivity loginActivity;
    private int[] action = new int[2];

    public LoginModel getLoginModel() {
        return loginModel;
    }

    public LoginPresenter(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    public LoginPresenter(LoginModel model, LoginActivity view) {
        loginModel = model;
        loginActivity = view;
    }

    public void setLoginModel(LoginModel loginModel) {
        this.loginModel = loginModel;
    }

    public LoginActivity getLoginActivity() {
        return loginActivity;
    }

    public void setLoginActivity(LoginActivity loginActivity) {
        this.loginActivity = loginActivity;
    }

    // simple username validation check
    public boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        } else {
            return !username.trim().isEmpty();
        }
    }

    // simple password validation check
    public boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

    // Check username in database for students or admins
    public void checkInDB(String username, String pass) {
        loginModel.setUser(new User(username, pass));
        //loginModel checks if the username and password are correct and updates the variable that tell presenter what to do
        loginModel.checkStudentInDB();
        loginModel.checkAdminInDB();
    }


    public void loginButtonAction(String username) {
        if (loginModel.getLoginAction() == 1) {
            loginActivity.displayToastMsg("This username is not associated \nwith an account");
        }
        else if (loginModel.getLoginAction() == 2) {
            loginActivity.displayToastMsg("The password entered is incorrect");
        }
        else if (loginModel.getLoginAction() == 3) {
            loginActivity.completeActivity(username, true);
        }
        else if (loginModel.getLoginAction() ==4 ) {
            loginActivity.completeActivity(username, false);
        }
    }

    public void registerButtonAction(String username, String pass) {
        //email error
        if (loginModel.getRegisterAction() == 1) {
            loginActivity.displayToastMsg("This username is already \nassociated with an account");
        }
        //new acc
        else if (loginModel.getRegisterAction() == 2) {
            if (username.equals("admin1")) {
                loginActivity.displayToastMsg("You cannot register an \naccount with that name");
            }
            else {
                loginModel.setUser(new User(username, pass));
                loginModel.createNewAccount();

                //fix for bugs where new student accounts don't display name and crash when adding courses
                loginActivity.completeActivity(username, true);
            }
        }
    }

}