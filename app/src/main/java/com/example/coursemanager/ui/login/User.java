package com.example.coursemanager.ui.login;

public class User {
    public String username;
    public String password;

    public User(){
    }

    public User(String username, String password){
        this.username = username;
        this.password = password;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public String getUsername(){
        return this.username;
    }

    public String getPassword(){
        return this.password;
    }

    @Override
    public boolean equals(Object obj){
        if(obj == null){
            return false;
        }
        if(!(obj instanceof User)){
            return false;
        }

        User user = (User) obj;
        if(username == user.username && password == user.password){
            return true;
        }
        return false;
    }

    @Override
    public String toString(){
        return username;
    }
}
