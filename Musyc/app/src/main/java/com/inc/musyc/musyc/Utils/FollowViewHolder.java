package com.inc.musyc.musyc.Utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.inc.musyc.musyc.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by saad on 10/25/17.
 */

public class FollowViewHolder extends RecyclerView.ViewHolder{

    public View mView;

    public FollowViewHolder(View itemView) {
        super(itemView);
        mView= itemView;

    }
    public void setName(String name)
    {
        TextView mName;
        mName=(TextView)mView.findViewById(R.id.singleuser_name);
        mName.setText(name);
    }
    public void setIntro(String intro)
    {
        TextView mName;
        mName=(TextView)mView.findViewById(R.id.singleuser_Intro);
        mName.setText(intro);
    }
    public void setImage(String image, Context ctx)
    {
        CircleImageView mImage=(CircleImageView)mView.findViewById(R.id.singleuser_img);
        Picasso.with(ctx).load(image).placeholder(R.drawable.default_avatar).into(mImage);
    }
}