package com.inc.musyc.musyc.JsontoJava;

/**
 * Created by saad on 10/22/17.
 */
//mixtape information
public class Mixtape {
    String description;
    String title;
    String Image;
    String id;

    public Mixtape(String description, String title, String image, String id) {
        this.description = description;
        this.title = title;
        Image = image;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Mixtape() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        this.Image = image;
    }
}
