package com.inc.musyc.musyc.OfflineMusicPlayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.inc.musyc.musyc.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

//List adapter for every video being loaded in VideosLoaderActivity activity

public class VideoAdapterClass extends BaseAdapter
{
    ArrayList<EachVideoFormat> videosList;
    private LayoutInflater videoInf;
    Context c;
    Utilities utils = new Utilities();

    VideoAdapterClass(Context c, ArrayList<EachVideoFormat> videosList)    //Initialization
    {
        this.c=c;
        this.videosList=videosList;
        videoInf=LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return videosList.size();
    }

    @Override
    public Object getItem(int i) {
        return videosList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent)      //Loads videos on to list
    {
        RelativeLayout videoLay = (RelativeLayout) videoInf.inflate(R.layout.video_list_layout, parent, false);
        TextView videoTitle = (TextView)videoLay.findViewById(R.id.videoTitle);         //EachVideoFormat title
        ImageView videoPreview = (ImageView) videoLay.findViewById(R.id.preview);       //EachVideoFormat preview thumbnail
        EachVideoFormat currEachVideoFormat = videosList.get(position);
        videoTitle.setText(currEachVideoFormat.getTitle());
        String temp = currEachVideoFormat.getTitle();
        videoPreview.setImageResource(R.drawable.videopreview);
        if(temp.contains("Boulevard"))videoPreview.setImageResource(R.drawable.boulevard);
        if(temp.contains("Summer"))videoPreview.setImageResource(R.drawable.summer);
        if(temp.contains("I'll Be There"))videoPreview.setImageResource(R.drawable.friends);
        if(temp.contains("Fix")) Picasso.with(VideosLoaderActivity.contextOfThisActivity).load(R.drawable.fixyou).into(videoPreview);
        videoLay.setTag(position);
        return videoLay;
    }
}
