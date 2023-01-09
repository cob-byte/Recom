package com.example.recom;

public class User {
    String fname;
    String mname;
    String lname;
    String address;
    String phonenum;
    String dobirth;
    String gender;
    int userRole;

    public User() {
    }

    public User(String fname, String mname, String lname, String address, String phonenum, String dobirth, String gender, int userRole) {
        this.fname = fname;
        this.mname = mname;
        this.lname = lname;
        this.address = address;
        this.phonenum = phonenum;
        this.dobirth = dobirth;
        this.gender = gender;
        this.userRole = userRole;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getMname() {
        return mname;
    }

    public void setMname(String mname) {
        this.mname = mname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhonenum() {
        return phonenum;
    }

    public void setPhonenum(String phonenum) {
        this.phonenum = phonenum;
    }

    public String getDobirth() {
        return dobirth;
    }

    public void setDobirth(String dobirth) {
        this.dobirth = dobirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getUserRole() {
        return userRole;
    }

    public void setUserRole(int userRole) {
        this.userRole = userRole;
    }
}
