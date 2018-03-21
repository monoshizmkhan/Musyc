package com.inc.musyc.musyc.ActivitiesAndFragments.loginsignup;

import android.content.Intent;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.inc.musyc.musyc.Global.Infostatic;
import com.inc.musyc.musyc.R;

public class LogInActivity extends AppCompatActivity {

    //private variable
    private Button mLogin;
    private Button mForgot;
    private Button mResend;
    private TextInputLayout mEmail;
    private TextInputLayout mPass;
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private TextView mTitle;
    private FirebaseDatabase mDatabase;
    private ProgressBar mProgressbar;
    private Typeface mTitleFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        //init//////////////////////////////////////////////////////////

        mLogin=(Button) findViewById(R.id.login_bt_login);
        mForgot=(Button) findViewById(R.id.login_bt_forgetpass);
        mEmail=(TextInputLayout)findViewById(R.id.login_et_email);
        mPass=(TextInputLayout)findViewById(R.id.login_et_password);
        mAuth=FirebaseAuth.getInstance();
        mToolbar=(Toolbar)findViewById(R.id.login_toolbar);
        mTitleFont = Typeface.createFromAsset(getAssets(),"font/title9n.otf");
        mTitle=(TextView)findViewById(R.id.login_tv_title);
        mDatabase = FirebaseDatabase.getInstance();
        mProgressbar=(ProgressBar) findViewById(R.id.login_pb_progress);
        mResend=(Button)findViewById(R.id.login_bt_resend);
        mResend.setVisibility(View.GONE);
        initUI();

    }

    private void initUI()
    {
        //toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("  MUSYC");
        getSupportActionBar().setLogo(R.mipmap.ic_launcher_round);

        mTitle.setTypeface(mTitleFont);

//        mResend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent varification = new Intent(LogInActivity.this, VerificationActivity.class);
//                startActivity(varification);
//            }
//        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String password=mPass.getEditText().getText().toString();
                String email=mEmail.getEditText().getText().toString();
                validinputchecker(password,email);
            }
        });

        mForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent socialmain = new Intent(LogInActivity.this, ForgotpassActivity.class);
                startActivity(socialmain);
            }
        });


    }
    public void validinputchecker(String password , String email)
    {
        if(email==null || email.isEmpty()) {

            Toast.makeText(LogInActivity.this,"Enter email first!",Toast.LENGTH_SHORT).show();

        }else if(password==null || password.isEmpty()) {

            Toast.makeText(LogInActivity.this,"Enter password first!",Toast.LENGTH_SHORT).show();

        }else {
            mProgressbar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            loginuser(password, email);
        }
    }

    public void loginuser(String password , String email)
    {
       // Toast.makeText(LogInActivity.this,""+email+" "+password,Toast.LENGTH_SHORT).show();
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    /*
                    email varification
                    FirebaseUser user=mAuth.getCurrentUser();

                    if(user.isEmailVerified()){

                        mInfo.islogedin=true;
                        finish();
                        Toast.makeText(LogInActivity.this,"Success!",Toast.LENGTH_LONG).show();

                    }else{

                        Toast.makeText(LogInActivity.this,"Please varifie your email first!",Toast.LENGTH_LONG).show();
                        mAuth.signOut();
                    }

                    */
                    //without email verification

                    Infostatic.uid=mAuth.getCurrentUser().getUid();
                    Infostatic.email=mAuth.getCurrentUser().getEmail();
                    //infostatic init
                    mDatabase.getReference().child("uidtoinfo").child(Infostatic.uid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(dataSnapshot.exists()) {

                                Infostatic.name = dataSnapshot.child("username").getValue().toString();
                                Infostatic.intro = dataSnapshot.child("intro").getValue().toString();
                                Infostatic.des = dataSnapshot.child("des").getValue().toString();
                                Infostatic.img=dataSnapshot.child("image").getValue().toString();
                                Infostatic.not=dataSnapshot.child("cnt").getValue().toString();

                                //Toast.makeText(LogInActivity.this, "Success!", Toast.LENGTH_LONG).show();
                                mProgressbar.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                Infostatic.islogedin = true;
                                finish();
                            }
                            else
                            {
                                mAuth.signOut();
                                Toast.makeText(LogInActivity.this,"Try again!",Toast.LENGTH_LONG).show();
                                mProgressbar.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

                }else{
                    mProgressbar.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(LogInActivity.this,task.getException().toString(),Toast.LENGTH_LONG).show();
                }
            }

        });

    }
}
