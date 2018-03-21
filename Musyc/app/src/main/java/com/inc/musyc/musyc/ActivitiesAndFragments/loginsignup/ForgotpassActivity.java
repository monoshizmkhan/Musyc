package com.inc.musyc.musyc.ActivitiesAndFragments.loginsignup;

import android.graphics.Typeface;
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
    forgot password activity;
    lets user reset password
 */

public class ForgotpassActivity extends AppCompatActivity {

    //private variable

    private Button mResetPassword;
    private TextInputLayout mEmail;
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private TextView mTitle;
    private ProgressBar mProgressbar;
    private Typeface mTitle1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpass);

        //init////////////////////////////////////////////////////////////

        mResetPassword=(Button) findViewById(R.id.forgotpass_bt_sendpass);
        mEmail=(TextInputLayout)findViewById(R.id.forgotpass_et_email);
        mAuth=FirebaseAuth.getInstance();
        mToolbar=(Toolbar)findViewById(R.id.forgotpass_toolbar);
        mTitle1 = Typeface.createFromAsset(getAssets(),"font/title9n.otf");
        mTitle=(TextView)findViewById(R.id.forgotpass_tv_title);
        mProgressbar=(ProgressBar) findViewById(R.id.forgotpass_pb_progress);

        //init UI
        initUI();
    }

    private void initUI()
    {
        //toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("   MUSYC");
        getSupportActionBar().setLogo(R.mipmap.ic_launcher_round);
        mTitle.setTypeface(mTitle1);

        //resetpassword
        mResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=mEmail.getEditText().getText().toString();
                mProgressbar.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                ResetPass(email);
            }
        });
    }
    public void ResetPass(String email){


        //sends reset email to user
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotpassActivity.this,"Email Sent!",Toast.LENGTH_LONG).show();
                            mProgressbar.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            finish();
                        }
                        else
                        {
                            mProgressbar.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            Toast.makeText(ForgotpassActivity.this,"No Such Account!",Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }
}
