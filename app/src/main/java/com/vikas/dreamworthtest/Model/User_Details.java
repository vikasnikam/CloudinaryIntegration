package com.vikas.dreamworthtest.Model;

import java.io.Serializable;

/**
 * Created by Vikas on 11/23/2017.
 */

public class User_Details implements Serializable {
    String userName,UserEmail,userDOB;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return UserEmail;
    }

    public void setUserEmail(String userEmail) {
        UserEmail = userEmail;
    }

    public String getUserDOB() {
        return userDOB;
    }

    public void setUserDOB(String userDOB) {
        this.userDOB = userDOB;
    }
}
