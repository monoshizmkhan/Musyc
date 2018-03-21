package com.inc.musyc.musyc.JsontoJava;

/**
 * Created by saad on 10/23/17.
 */
//mixtape song innformation
public class Mixtapesongs {
    String music,title;

    public Mixtapesongs() {
    }

    public Mixtapesongs(String music, String title) {
        this.music = music;
        this.title = title;
    }

    public String getMusic() {
        return music;
    }

    public void setMusic(String music) {
        this.music = music;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
