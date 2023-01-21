package com.example.recom;

public class Announcement {
    String TypeTitle,TypeDescription,TypeImageDesc, imageUri, currentDateTime, name, image;

    public Announcement() {
    }

    public Announcement(String typeTitle, String typeDescription, String typeImageDesc, String imageUri, String currentDateTime, String name, String image) {
        this.TypeTitle = typeTitle;
        this.TypeDescription = typeDescription;
        this.TypeImageDesc = typeImageDesc;
        this.imageUri = imageUri;
        this.currentDateTime = currentDateTime;
        this.name = name;
        this.image = image;
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

    public String getCurrentDateTime() {return currentDateTime;}

    public void setCurrentDateTime(String currentDateTime) {this.currentDateTime = currentDateTime;}

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public String getImage() {return image;}

    public void setImage(String image) {this.image = image;}
}
