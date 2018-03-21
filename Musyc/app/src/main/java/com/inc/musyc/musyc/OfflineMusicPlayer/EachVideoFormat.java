package com.inc.musyc.musyc.OfflineMusicPlayer;

//Every video's format

public class EachVideoFormat
{
    String title, path;
    EachVideoFormat(String title, String path)
    {
        this.title=title;                       //EachVideoFormat title
        this.path=path;                         //EachVideoFormat path
    }
    String getTitle(){return title;}
    String getPath(){return path;}
}
