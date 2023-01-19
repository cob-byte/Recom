package com.example.recom;

public class verifyInfo {
    String votersCert, brgyCert, name, uid, photourl;

    public verifyInfo() {
    }

    public verifyInfo(String votersCert, String brgyCert, String name, String uid, String photourl) {
        this.votersCert = votersCert;
        this.brgyCert = brgyCert;
        this.name = name;
        this.uid = uid;
        this.photourl = photourl;
    }

    public String getVotersCert() {
        return votersCert;
    }

    public void setVotersCert(String votersCert) {
        this.votersCert = votersCert;
    }

    public String getBrgyCert() {
        return brgyCert;
    }

    public void setBrgyCert(String brgyCert) {
        this.brgyCert = brgyCert;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhotourl() {
        return photourl;
    }

    public void setPhotourl(String photourl) {
        this.photourl = photourl;
    }
}
