package com.inc.musyc.musyc.Utils;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.inc.musyc.musyc.R;

/**
 * Created by saad on 10/25/17.
 */

public class MixtapeSongsViewHolder extends RecyclerView.ViewHolder{

    public View mView;

    public MixtapeSongsViewHolder(View itemView) {
        super(itemView);
        mView= itemView;

    }
    public void setTitle(String title)
    {
        TextView mTitle;
        mTitle=(TextView)mView.findViewById(R.id.mixtapesongs_name);
        mTitle.setText(title);
    }
    public void setColor()
    {
        TextView mTitle;
        mTitle=(TextView)mView.findViewById(R.id.mixtapesongs_name);
        mTitle.setBackgroundResource(R.color.colorAccent);
    }
    public void unsetColor()
    {
        TextView mTitle;
        mTitle=(TextView)mView.findViewById(R.id.mixtapesongs_name);
        mTitle.setBackgroundResource(R.color.Cards_Color);
    }

}