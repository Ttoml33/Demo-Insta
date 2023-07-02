package com.tomlee.demoinsta.Model;

public class Comment {
    public  String comment;
    public String publisher;
    private String ID;

    public Comment() {
    }

    public Comment(String comment, String publisher, String ID) {
        this.comment = comment;
        this.publisher = publisher;
        this.ID = ID;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    public String getPublisher(){
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
