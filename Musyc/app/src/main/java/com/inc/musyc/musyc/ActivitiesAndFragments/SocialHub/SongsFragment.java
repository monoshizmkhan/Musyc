package com.inc.musyc.musyc.ActivitiesAndFragments.SocialHub;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.inc.musyc.musyc.ActivitiesAndFragments.MusicStream.MixtapelistenActivity;
import com.inc.musyc.musyc.Global.Infostatic;
import com.inc.musyc.musyc.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class SongsFragment extends Fragment {

    private TextView mLiketitle;
    private ImageView mLikeimage;
    private View mView;

    public SongsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView=inflater.inflate(R.layout.fragment_songs, container, false);

        //init/////////////////////
        mLikeimage=(ImageView) mView.findViewById(R.id.songsfragment_img2);
        mLiketitle=(TextView)mView.findViewById(R.id.songsfragment_title2);
        initUI();

        return mView;
    }

    private void initUI()
    {
        mLiketitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent MixtapeListane=new Intent(getContext(),MixtapelistenActivity.class);
                MixtapeListane.putExtra("title","Liked Songs");
                MixtapeListane.putExtra("id","liked_songs");
                MixtapeListane.putExtra("uid", Infostatic.uid);
                startActivity(MixtapeListane);
            }
        });
    }

}
