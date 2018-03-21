package com.inc.musyc.musyc.Utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.inc.musyc.musyc.ActivitiesAndFragments.SocialHub.FeedFragment;
import com.inc.musyc.musyc.ActivitiesAndFragments.SocialHub.FollowFragment;
import com.inc.musyc.musyc.ActivitiesAndFragments.SocialHub.NotificationFragment;
import com.inc.musyc.musyc.ActivitiesAndFragments.SocialHub.SongsFragment;

/**
 * Created by saad on 10/1/17.
 */

//tab adaptar
public class SocialMainPagerAdapter extends FragmentPagerAdapter {

    public SocialMainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        if(position==0)
        {
            FeedFragment feed=new FeedFragment();
            return feed;
        }
        else if(position==1)
        {
            FollowFragment feed=new FollowFragment();
            return feed;
        }
        else if(position==2)
        {
            NotificationFragment feed=new NotificationFragment();
            return feed;
        }
        else if(position==3)
        {
            SongsFragment feed=new SongsFragment();
            return feed;
        }
        else return null;
    }

    @Override
    public int getCount() {
        return 4;
    }

    public CharSequence getPageTitle(int position){

        if(position==0)
        {
            return "POST";
        }
        else if(position==1)
        {
            return "FOLLOW";
        }
        else if(position==2)
        {
            return "YO!";
        }
        else if(position==3)
        {
            return "SONGS";
        }
        else return null;
    }
}
