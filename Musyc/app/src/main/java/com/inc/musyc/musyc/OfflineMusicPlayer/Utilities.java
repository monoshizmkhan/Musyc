package com.inc.musyc.musyc.OfflineMusicPlayer;

//Used for conversion
//Used for maintaining seekbar and looping functions

public class Utilities
{
    public String milliSecondsToTimer(long milliseconds)        //Conversion from miliseconds to hh:mm:ss format
    {
        String finalTimerString = "";
        String secondsString = "";
        int hours = (int)( milliseconds / (1000*60*60));
        int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
        int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
        if(hours>0)finalTimerString = hours + ":";
        if(seconds<10)secondsString = "0" + seconds;
        else
            secondsString = "" + seconds;
        finalTimerString = finalTimerString + minutes + ":" + secondsString;
        return finalTimerString;
    }

    public int milliSecondsToSeconds(long miliseconds)
    {
        return (int) miliseconds/1000;
    }   //Converts milliseconds to seconds

    public int secondsToMiliseconds(int seconds)    //Converts seconds to miliseconds
    {
        return seconds*1000;
    }

    public int getProgressPercentage(long currentDuration, long totalDuration)  //Gets seekbar progress percentage using current progress in miliseconds
    {                                                                           //and total duration in miliseconds
        Double percentage;
        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);
        percentage =(((double)currentSeconds)/totalSeconds)*100;
        return percentage.intValue();
    }

    public int progressToTimer(int progress, int totalDuration)     //Uses seekbar progress and time and converts data to miliseconds
    {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double)progress) / 100) * totalDuration);
        return currentDuration * 1000;
    }
}

