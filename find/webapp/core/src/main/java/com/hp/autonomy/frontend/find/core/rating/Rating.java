package com.hp.autonomy.frontend.find.core.rating;


import java.io.Serializable;

public class Rating implements Serializable {
    private String id;
    private String docreferenceid;
    private String username;
    private float rating;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDocreferenceid() {
        return docreferenceid;
    }

    public void setDocreferenceid(String docreferenceid) {
        this.docreferenceid = docreferenceid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }


}
