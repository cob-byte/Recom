package com.example.recom;

public class User {
    String fname, mname, lname, phonenum, dobirth, gender;

    public User() {
    }

    public User(String fname, String mname, String lname, String phonenum, String dobirth, String gender) {
        this.fname = fname;
        this.mname = mname;
        this.lname = lname;
        this.phonenum = phonenum;
        this.dobirth = dobirth;
        this.gender = gender;
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
}
