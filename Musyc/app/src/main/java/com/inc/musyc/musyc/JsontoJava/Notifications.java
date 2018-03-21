package com.inc.musyc.musyc.JsontoJava;

/**
 * Created by saad on 10/3/17.
 */
//notification data
public class Notifications {
    public String body,fromid,time,title,type,postid;

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public Notifications(String body, String fromid, String time, String title, String type, String postid) {

        this.body = body;
        this.fromid = fromid;
        this.time = time;
        this.title = title;
        this.type = type;
        this.postid = postid;
    }

    public Notifications() {
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getFromid() {
        return fromid;
    }

    public void setFromid(String fromid) {
        this.fromid = fromid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
