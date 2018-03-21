package com.inc.musyc.musyc.ActivitiesAndFragments.loginsignup;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.inc.musyc.musyc.Global.Infostatic;
import com.inc.musyc.musyc.R;

/*
    Welcome User to Musyc and gives them the option to signup or login
 */

public class StartActivity extends AppCompatActivity {

    //private variables
    private Button mLogin;
    private Button mSignup;
    private TextView mTitle;
    private ImageView mLogo;
    private Typeface mToolbarFont;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //init//////////////////////////////////////////////////////////////////////
        mLogin=(Button) findViewById(R.id.startpage_bt_login);
        mSignup=(Button) findViewById(R.id.startpage_bt_ac);
        mTitle=(TextView)findViewById(R.id.startpage_tv_title);
        mToolbarFont = Typeface.createFromAsset(getAssets(),"font/title9n.otf");
        mLogo=(ImageView)findViewById(R.id.startpage_iv_logo);
        //init UI
        initUI();
    }


    private void initUI()
    {
        mTitle.setTypeface(mToolbarFont);

        //login button
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent logint=new Intent(StartActivity.this,LogInActivity.class);
                startActivity(logint);
            }
        });

        //signup button
        mSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signupint=new Intent(StartActivity.this,SignUpActivity.class);
                startActivity(signupint);
            }
        });
    }
    //Android Lifecycle/////////////////////
    @Override
    protected void onResume() {
        super.onResume();
        if(Infostatic.islogedin)
        {
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(Infostatic.islogedin)
        {
           // finish();
        }
    }
}
