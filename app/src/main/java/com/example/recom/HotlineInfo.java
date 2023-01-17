package com.example.recom;

public class HotlineInfo
{
    //class

    private String places;
    private String contacts;

    //constructor


    public HotlineInfo(String places, String contacts) {
        this.places = places;
        this.contacts = contacts;
    }

    public String getPlaces() {
        return places;
    }

    public String getContacts() {
        return contacts;
    }
}
