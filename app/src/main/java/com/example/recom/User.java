package com.example.recom;

public class User {
    String fname, mname, lname, address, phonenum, dobirth, gender, verification;

    public User() {
    }

    public User(String fname, String mname, String lname, String address, String phonenum, String dobirth, String gender, String verification) {
        this.fname = fname;
        this.mname = mname;
        this.lname = lname;
        this.address = address;
        this.phonenum = phonenum;
        this.dobirth = dobirth;
        this.gender = gender;
        this.verification = verification;
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

    public String getVerification() {
        return verification;
    }

    public void setVerification(String verification) {
        this.verification = verification;
    }
}
