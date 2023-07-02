package com.tomlee.demoinsta.Model;

public class User {
    private String Bio;
    private String Email;
    private String ID;
    private String Name;
    private String UserName;
    private String imageUrl;

    public User() {
    }

    public User(String bio, String email, String ID, String name, String userName, String imageUrl) {
        Bio = bio;
        Email = email;
        this.ID = ID;
        Name = name;
        UserName = userName;
        this.imageUrl = imageUrl;
    }

    public String getBio() {
        return Bio;
    }

    public void setBio(String bio) {
        Bio = bio;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
