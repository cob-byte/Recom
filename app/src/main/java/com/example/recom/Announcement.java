package com.example.recom;

public class Announcement {
    String TypeTitle,TypeDescription,TypeImageDesc, imageUri;

    public Announcement() {
    }

    public Announcement(String typeTitle, String typeDescription, String typeImageDesc, String imageUri) {
        TypeTitle = typeTitle;
        TypeDescription = typeDescription;
        TypeImageDesc = typeImageDesc;
        this.imageUri = imageUri;
    }

    public String getTypeTitle() {
        return TypeTitle;
    }

    public void setTypeTitle(String typeTitle) {
        TypeTitle = typeTitle;
    }

    public String getTypeDescription() {
        return TypeDescription;
    }

    public void setTypeDescription(String typeDescription) {
        TypeDescription = typeDescription;
    }

    public String getTypeImageDesc() {
        return TypeImageDesc;
    }

    public void setTypeImageDesc(String typeImageDesc) {
        TypeImageDesc = typeImageDesc;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
