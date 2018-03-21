package com.inc.musyc.musyc.JsontoJava;

/**
 * Created by saad on 10/2/17.
 */
//single user data
public class Singleuser {

    public String username;
    public String intro;
    public String image;

    public Singleuser(){}

    public Singleuser(String username, String intor, String image) {
        this.username = username;
        this.intro = intor;
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
