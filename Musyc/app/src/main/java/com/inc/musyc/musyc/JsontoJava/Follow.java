package com.inc.musyc.musyc.JsontoJava;

/**
 * Created by saad on 10/3/17.
 */
//follow user innformation
public class Follow {

    public String id;
    public String isParty;
    public String image;
    public String title;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPartyid() {
        return partyid;
    }

    public void setPartyid(String partyid) {
        this.partyid = partyid;
    }

    public String partyid;

    public Follow(){

    }

    public String getIsParty() {
        return isParty;
    }

    public void setIsParty(String isParty) {
        this.isParty = isParty;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Follow(String id) {

        this.id = id;
    }
}