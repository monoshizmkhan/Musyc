package com.inc.musyc.musyc.ActivitiesAndFragments.SocialHub;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.support.design.widget.TabLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.inc.musyc.musyc.ActivitiesAndFragments.MusicStream.CreatePostActivity;
import com.inc.musyc.musyc.Global.Infostatic;
import com.inc.musyc.musyc.R;
import com.inc.musyc.musyc.Utils.SocialMainPagerAdapter;

/*
    main page of social media.
    controls different fregments
 */
public class SocialMainActivity extends AppCompatActivity {

    //private static variable
    private static FirebaseAuth mAuth;
    private static Toolbar mtoolbar;
    private static ViewPager mViewpager;
    private static SocialMainPagerAdapter mSocialpageradapter;
    private static TabLayout mTablayout;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_main);

        mAuth = FirebaseAuth.getInstance();

        //init UI
        initUI();
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       // getSupportActionBar().setDisplayShowHomeEnabled(true);



    }

    private void initUI()
    {
        //toolbar
        mtoolbar=(Toolbar)findViewById(R.id.social_main_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("  MUSYC SOCIAL");
        getSupportActionBar().setLogo(R.mipmap.ic_launcher_round);

        //tab adaptar
        mSocialpageradapter=new SocialMainPagerAdapter(getSupportFragmentManager());
        mViewpager=(ViewPager)findViewById(R.id.social_vp_main);
        mViewpager.setAdapter(mSocialpageradapter);
        mTablayout =(TabLayout)findViewById(R.id.social_tablayout);
        mTablayout.setupWithViewPager(mViewpager);

        //set init tab view
        int TabNo=getIntent().getIntExtra("f",0);
        TabLayout.Tab tab = mTablayout.getTabAt(TabNo);
        tab.select();

    }
    //Android Lifecycle//////////////////////////////////
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser= mAuth.getCurrentUser();
        if(currentUser==null)
        {
            //Intent socialmain=new Intent(SocialMainActivity.this,StartActivity.class);
            //startActivity(socialmain);
            //finish();
        }
    }

    //Android Menu Activity////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.social_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId()==R.id.socialmenu_acset)
        {
            Intent AccountSetting=new Intent(SocialMainActivity.this,AccountSettingActivity.class);
            startActivity(AccountSetting);
        }
        else if(item.getItemId()==R.id.socialmenu_search)
        {
            Intent Search=new Intent(SocialMainActivity.this,SearchActivity.class);
            startActivity(Search);
        }
        else if(item.getItemId()==R.id.socialmenu_creatpost)
        {
            Intent CreatePost=new Intent(SocialMainActivity.this,CreatePostActivity.class);
            startActivity(CreatePost);
        }else if (item.getItemId()==R.id.socialmenu_mymixtapes)
        {
            Intent MyMixtapes=new Intent(SocialMainActivity.this,MymixtapesActivity.class);
            MyMixtapes.putExtra("isChoose","false");
            MyMixtapes.putExtra("uid",Infostatic.uid);
            startActivity(MyMixtapes);
        }
        return true;
    }
}
