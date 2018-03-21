package com.inc.musyc.musyc.OfflineMusicPlayer;

import android.media.MediaRecorder;
import android.os.Environment;

import java.io.IOException;

//Manages voice recording methods

public class VoiceRecorderManagerClass
{
    static MediaRecorder mediaRecorder = new MediaRecorder();       //Initializes voice recorder
    static String fileName="";
    static String recordingPath="";
    static void startRecording(String fileName)
    {
        mediaRecorder.reset();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);    //Sets recording source
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);    //Sets recording output format
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);       //Sets recording encoder
        recordingPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+fileName+".3gp";
        mediaRecorder.setOutputFile(recordingPath);                     //Sets recording name and file path
        try
        {
            mediaRecorder.prepare();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        mediaRecorder.start();      //Starts recording
    }
    static void stopRecording()
    {
        mediaRecorder.stop();
        mediaRecorder.reset();      //Stops recording
        mediaRecorder.release();
    }
    static String provideRecordingPath()
    {
        return recordingPath;
    }
}
