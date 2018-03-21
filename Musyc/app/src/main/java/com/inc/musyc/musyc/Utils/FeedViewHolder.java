package com.inc.musyc.musyc.Utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.inc.musyc.musyc.R;
import com.squareup.picasso.Picasso;

/**
 * Created by saad on 10/25/17.
 */

public  class FeedViewHolder extends RecyclerView.ViewHolder{

    public View mView;

    public FeedViewHolder(View itemView) {
        super(itemView);
        mView= itemView;

    }
    public void setTitle(String title)
    {
        TextView mName;
        mName=(TextView)mView.findViewById(R.id.postview_title);
        mName.setText(title);
    }

    public void setArtist(String artist)
    {
        TextView mName;
        mName=(TextView)mView.findViewById(R.id.postview_artist);
        mName.setText(artist);
    }
    public void setName(String artist)
    {
        TextView mName;
        mName=(TextView)mView.findViewById(R.id.postview_follow);
        mName.setText(artist);
    }
    public void setImage(String image, Context ctx)
    {
        ImageView mImage=(ImageView)mView.findViewById(R.id.postview_img);
        if(mImage!=null) Picasso.with(ctx).load(image).placeholder(R.drawable.defaultmusic).into(mImage);
    }
}