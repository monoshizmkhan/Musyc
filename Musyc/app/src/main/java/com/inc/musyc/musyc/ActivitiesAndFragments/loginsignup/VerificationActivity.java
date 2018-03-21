package com.inc.musyc.musyc.ActivitiesAndFragments.loginsignup;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.inc.musyc.musyc.R;
/*
    lets user verifi their accout
 */

public class VerificationActivity extends AppCompatActivity {

    //private variable

    private Button mResend;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private TextView mTitle;
    private ProgressBar mProgressbar;
    private Typeface mToolbarFont;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        //init////////////////////////////////////////////////////////////

        mResend=(Button) findViewById(R.id.verification_bt_sendpass);
        mEmail=(TextInputLayout)findViewById(R.id.verification_et_email);
        mPassword=(TextInputLayout)findViewById(R.id.verification_et_password);
        mAuth=FirebaseAuth.getInstance();
        mToolbar=(Toolbar)findViewById(R.id.verification_toolbar);
        mToolbarFont = Typeface.createFromAsset(getAssets(),"font/title9n.otf");
        mTitle=(TextView)findViewById(R.id.verification_tv_title);
        mProgressbar=(ProgressBar) findViewById(R.id.verification_pb_progress);

        //init UI////////////////////////////////////////
        initUI();
    }

    private void initUI()
    {
        //toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("   MUSYC");
        getSupportActionBar().setLogo(R.mipmap.ic_launcher_round);
        mTitle.setTypeface(mToolbarFont);

        //resetpassword
        mResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=mEmail.getEditText().getText().toString();
                String password=mPassword.getEditText().getText().toString();
                windowOff();
                Resend(email,password);
            }
        });
    }

    //resends verivfication email
    public void Resend(String email,String password){

        mAuth.signOut();
        mAuth.signInWithEmailAndPassword(email,password);
        if(mAuth.getCurrentUser()==null)
        {
            Toast.makeText(VerificationActivity.this,"No Such Account!",Toast.LENGTH_LONG).show();
            windowOn();
            mAuth.signOut();
            finish();
            return;
        }
        FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(VerificationActivity.this,"Email Sent!",Toast.LENGTH_LONG).show();
                    windowOn();
                    finish();
                }
                else
                {
                    windowOn();
                    Toast.makeText(VerificationActivity.this,"Some thing went wrong!",Toast.LENGTH_LONG).show();
                }
            }
        });
        mAuth.signOut();
        finish();
    }

    //window and progressbar util///////////////////////////////
    private void windowOff()
    {
        //shows progressbar
        //turns window touch off
        mProgressbar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void windowOn()
    {
        //hide progressbar
        //turns window touch on
        mProgressbar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
