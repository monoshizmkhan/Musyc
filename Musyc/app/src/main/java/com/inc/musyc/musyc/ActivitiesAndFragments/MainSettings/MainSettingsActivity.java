package com.inc.musyc.musyc.ActivitiesAndFragments.MainSettings;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.inc.musyc.musyc.Global.Infostatic;
import com.inc.musyc.musyc.R;

public class MainSettingsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private Button mAboutbt;
    private Button mThemebt;
    private Button mContactbt;
    private Button mThanksbt;
    private Button mLogoutbt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_settings);

        mToolbar =(Toolbar)findViewById(R.id.appsettings_toolbar);
        mLogoutbt=(Button)findViewById(R.id.appsettings_logoutbt);
        mAboutbt=(Button)findViewById(R.id.appsettings_aboutbt);
        mThanksbt=(Button)findViewById(R.id.appsettings_thanksbt);
        mContactbt=(Button)findViewById(R.id.appsettings_contectbt);
        mThemebt=(Button)findViewById(R.id.appsettings_themebt);

        initUI();
    }

    private void initUI()
    {

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("  Settings");
        getSupportActionBar().setLogo(R.mipmap.ic_launcher_round);

        mLogoutbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Infostatic.islogedin==false)
                {
                    Toast.makeText(MainSettingsActivity.this,"You are not logged in!",Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                Infostatic.islogedin=false;
                FirebaseAuth.getInstance().signOut();

                Toast.makeText(MainSettingsActivity.this,"Logged Out!",Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        mThemebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainSettingsActivity.this,"More Theme will be available later.",Toast.LENGTH_LONG).show();
            }
        });

        mThanksbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent thank=new Intent(MainSettingsActivity.this, SpecialThanksActivit.class);
                startActivity(thank);
            }
        });

        mContactbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contect=new Intent(MainSettingsActivity.this, ContectUsActivity.class);
                startActivity(contect);
            }
        });
        mAboutbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent about=new Intent(MainSettingsActivity.this, AboutUsActivity.class);
                startActivity(about);
            }
        });
    }
}
