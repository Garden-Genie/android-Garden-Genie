package com.example.gardengenie_android;

public class User {
    public String user_id;
    public String user_name;
    public String user_pwd;
    public String user_email;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_pwd() {
        return user_pwd;
    }

    public void setUser_pwd(String user_pwd) {
        this.user_pwd = user_pwd;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public User(String user_name, String user_id, String user_email, String user_pwd){
        this.user_pwd = user_pwd;
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_email = user_email;
    }

    @Override
    public String toString(){
        return "User{" + user_email + user_id + user_name + user_pwd;
    }
}
