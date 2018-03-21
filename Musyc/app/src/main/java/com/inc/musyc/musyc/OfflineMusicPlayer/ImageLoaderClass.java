package com.inc.musyc.musyc.OfflineMusicPlayer;


import android.content.ContentUris;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.widget.ImageView;

import com.inc.musyc.musyc.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;

//Class for loading album cover art using album ID onto an imageview
//Places generic album art if album cover art is not available


public class ImageLoaderClass
{
    public void loadAndPlaceImage(long albumID, ImageView whereToPutImage)
    {
        Uri coverArt = Uri.parse("content://media/external/audio/albumart");
        Uri albumCoverArtPath = ContentUris.withAppendedId(coverArt, albumID);  //uri for individual song's album's albumCoverImage image
        try
        {
            ParcelFileDescriptor pfd = PlaylistActivity.contextOfThisActivity.getContentResolver().openFileDescriptor(albumCoverArtPath,"r");  //Opens file to read
            if(pfd!=null)Picasso.with(PlaylistActivity.contextOfThisActivity).load(albumCoverArtPath).resize(150, 150).into(whereToPutImage);  //If file exists,
            else                                                                                                                        //load image onto
                                                                                                                                        //space
                Picasso.with(PlaylistActivity.contextOfThisActivity).load(R.drawable.album).resize(64, 64).into(whereToPutImage);       //else, loads
                                                                                                                                        // generic default
                                                                                                                                        //image
        }
        catch(IOException e){}
    }
}
