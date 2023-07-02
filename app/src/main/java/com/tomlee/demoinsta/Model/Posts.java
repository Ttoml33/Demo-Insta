package com.tomlee.demoinsta.Model;

public class Posts {
    private String imageUrl;
    private String postID;
    private String Publisher;
    private String description;

    public Posts() {
    }

    public Posts(String imageUrl, String postID, String publisher, String description) {
        this.imageUrl = imageUrl;
        this.postID = postID;
        Publisher = publisher;
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getPublisher() {
        return Publisher;
    }

    public void setPublisher(String publisher) {
        Publisher = publisher;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
