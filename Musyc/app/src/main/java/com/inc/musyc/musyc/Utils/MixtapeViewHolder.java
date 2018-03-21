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

public class MixtapeViewHolder extends RecyclerView.ViewHolder{

    public View mView;

    public MixtapeViewHolder(View itemView) {
        super(itemView);
        mView= itemView;

    }
    public void setTitle(String title)
    {
        TextView mTitle;
        mTitle=(TextView)mView.findViewById(R.id.mixtapecard_title);
        mTitle.setText(title);
    }
    public void setDescription(String description)
    {
        TextView mDescription;
        mDescription=(TextView)mView.findViewById(R.id.mixtapecard_description);
        mDescription.setText(description);
    }
    public void setImage(String image, Context ctx)
    {
        ImageView im=(ImageView)mView.findViewById(R.id.mixtapecard_img);
        if(im!=null) Picasso.with(ctx).load(image).placeholder(R.drawable.defaultmusic).into(im);
    }
}
