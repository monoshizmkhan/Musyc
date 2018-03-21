package com.inc.musyc.musyc.OfflineMusicPlayer;

//Controls video playback

public class VideoPlaybackManagerClass
{
    static void playVideo(String videoPath)
    {
        KaraokeHubActivity.karaokeVideoView.setVideoPath(videoPath);      //Starts playing video using video path sent as parameter
        KaraokeHubActivity.karaokeVideoView.start();
    }
    static void pauseVideo()
    {
        KaraokeHubActivity.karaokeVideoView.pause();
    }   //Pauses video

    static void stopVideo()
    {
        KaraokeHubActivity.karaokeVideoView.stopPlayback();
    }
}
