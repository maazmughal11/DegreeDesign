package com.example.coursemanager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.os.Looper;

import androidx.lifecycle.MutableLiveData;

import com.example.coursemanager.data.LoginRepository;
import com.example.coursemanager.ui.login.LoginActivity;
import com.example.coursemanager.ui.login.LoginFormState;
import com.example.coursemanager.ui.login.LoginModel;
import com.example.coursemanager.ui.login.LoginPresenter;
import com.example.coursemanager.ui.login.User;

/**
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

@RunWith(MockitoJUnitRunner.class)
public class LoginPresenterUnitTests {

    @Mock
    LoginActivity view;

    @Mock
    LoginModel model;

    @Mock
    LoginRepository repo;

    LoginPresenter presenter = new LoginPresenter(repo);

    //        String goodPassword = "good";
    //        String badPassword = "bad";
    //        when(model.checkPassword(goodPassword)).thenReturn(true);
    //        when(model.checkPassword(goodPassword)).thenReturn(false);
    //        LoginPresenter loginPresenter;

    @Test
    public void testSetLoginActivity() {
        presenter.setLoginActivity(view);
        assertEquals(presenter.getLoginActivity(), view);
    }

    @Test
    public void testSetLoginModel() {
        presenter.setLoginModel(model);
        assertEquals(presenter.getLoginModel(), model);
    }

    @Test
    public void testEmptyUsername() {
        assertEquals(presenter.isUserNameValid(null), false);
    }

    @Test
    public void testValidUsername() {
        assertEquals(presenter.isUserNameValid("s1"), true);
    }

    @Test
    public void testInvalidPassword() {
        assertEquals(presenter.isPasswordValid("123"), false);
    }

    @Test
    public void testValidPassword() {
        assertEquals(presenter.isPasswordValid("123456"), true);
    }

    @Test
    public void testCheckStudentInDB() {
        User user = new User("s1", "111111");
        doNothing().when(model).setUser(user);
        doNothing().when(model).checkStudentInDB();
        doNothing().when(model).checkAdminInDB();
        when(model.checkUser(user)).thenReturn(true);

        LoginPresenter presenter = new LoginPresenter(model, view);

        presenter.checkInDB("s1", "111111");

        verify(model).checkStudentInDB();
        verify(model).checkAdminInDB();
        assertEquals(presenter.getLoginModel().checkUser(user), true);
    }

    @Test
    public void TestLoginButtonForBadUsername() {
        when(model.getLoginAction()).thenReturn(1);
        doNothing().when(view).displayToastMsg("This username is not associated \\nwith an account");

        LoginPresenter presenter = new LoginPresenter(model, view);
        presenter.loginButtonAction("x");

        verify(view).displayToastMsg("This username is not associated \nwith an account");
    }

    @Test
    public void TestLoginButtonForBadPassword() {
        when(model.getLoginAction()).thenReturn(2);
        doNothing().when(view).displayToastMsg("The password entered is incorrect");

        LoginPresenter presenter = new LoginPresenter(model, view);
        presenter.loginButtonAction("x");

        verify(view).displayToastMsg("The password entered is incorrect");
    }

    @Test
    public void TestLoginButtonForValidStudent() {
        when(model.getLoginAction()).thenReturn(3);
        doNothing().when(view).completeActivity("s1", true);

        LoginPresenter presenter = new LoginPresenter(model, view);
        presenter.loginButtonAction("s1");

        verify(view).completeActivity("s1", true);
    }

    @Test
    public void TestLoginButtonForValidAdmin() {
        when(model.getLoginAction()).thenReturn(4);
        doNothing().when(view).completeActivity("admin1", false);

        LoginPresenter presenter = new LoginPresenter(model, view);
        presenter.loginButtonAction("admin1");

        verify(view).completeActivity("admin1", false);
    }

    @Test
    public void TestRegisterButtonForDuplicateUsername() {
        when(model.getRegisterAction()).thenReturn(1);
        doNothing().when(view).displayToastMsg("This username is already \nassociated with an account");

        LoginPresenter presenter = new LoginPresenter(model, view);
        presenter.registerButtonAction("s1", "111111");

        verify(view).displayToastMsg("This username is already \nassociated with an account");
    }

    @Test
    public void TestRegisterButtonForAdminUsername() {
        when(model.getRegisterAction()).thenReturn(2);
        doNothing().when(view).displayToastMsg("You cannot register an \naccount with that name");

        LoginPresenter presenter = new LoginPresenter(model, view);
        presenter.registerButtonAction("admin1", "111111");

        verify(view).displayToastMsg("You cannot register an \naccount with that name");
    }

    @Test
    public void TestRegisterButtonForValidUsername() {
        when(model.getRegisterAction()).thenReturn(2);
        User user = new User("new_student", "111111");
        doNothing().when(model).setUser(user);
        doNothing().when(model).createNewAccount();
        doNothing().when(view).completeActivity("new_student", true);

        LoginPresenter presenter = new LoginPresenter(model, view);
        presenter.registerButtonAction("new_student", "111111");

        verify(model).setUser(user);
        verify(model).createNewAccount();
        verify(view).completeActivity("new_student", true);
    }
}