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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.inc.musyc.musyc.R;

import java.util.HashMap;
import java.util.Map;
/*
    signup activity
 */
public class SignUpActivity extends AppCompatActivity {

    //private variable
    private Button mSignup;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private TextInputLayout mUsername;
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private TextView mTitle;
    private FirebaseDatabase mDatabase;
    private ProgressBar mProgressbar;
    private Typeface mToolbarFont;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Init//////////////////////////////////////////////

        mSignup=(Button) findViewById(R.id.signup_bt_signup);
        mEmail=(TextInputLayout)findViewById(R.id.signup_et_email);
        mPassword=(TextInputLayout)findViewById(R.id.signup_et_password);
        mUsername=(TextInputLayout)findViewById(R.id.signup_et_username);
        mAuth=FirebaseAuth.getInstance();
        mToolbar=(Toolbar)findViewById(R.id.signup_toolbar);
        mToolbarFont = Typeface.createFromAsset(getAssets(),"font/title9n.otf");
        mTitle=(TextView)findViewById(R.id.signup_tv_title);
        mDatabase = FirebaseDatabase.getInstance();
        mProgressbar=(ProgressBar) findViewById(R.id.signup_pb_progress);

        //init UI///////////////////////////////////////////////
        initUI();
    }

    private void initUI()
    {
        //toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("  MUSYC");
        getSupportActionBar().setLogo(R.mipmap.ic_launcher_round);
        mTitle.setTypeface(mToolbarFont);

        //signup button
        mSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password=mPassword.getEditText().getText().toString();
                String email=mEmail.getEditText().getText().toString();
                String username=mUsername.getEditText().getText().toString();
                username=username.toLowerCase();
                valideInputCheck(username,email,password);
            }
        });
    }


    private void valideInputCheck(String username, String email,String password)
    {
        if(username==null || username.isEmpty()) {

            Toast.makeText(SignUpActivity.this,"Enter email first!",Toast.LENGTH_SHORT).show();

        }else if(email==null || email.isEmpty()) {

            Toast.makeText(SignUpActivity.this,"Enter password first!",Toast.LENGTH_SHORT).show();

        }else if(password==null || password.isEmpty()) {

            Toast.makeText(SignUpActivity.this,"Enter password first!",Toast.LENGTH_SHORT).show();

        }else if(password.length()<8){

            Toast.makeText(SignUpActivity.this,"Password Should be atleast 8 character!",Toast.LENGTH_SHORT).show();

        }else if (charCheck(username)){

            Toast.makeText(SignUpActivity.this,"User name should only have a-z, A-Z, 0-9",Toast.LENGTH_SHORT).show();

        }else  {
            windowOff();
            existUsername(username,email,password);
        }
    }

    public boolean charCheck(String username)
    {
        for(int i=0;i>username.length();i++)
        {
            char p=username.charAt(i);
            if(!(p>='a' && p<='z') && !(p>='A' && p<='Z') && !(p>='0' && p<='9'))return true;
        }
        return false;
    }

    public void existUsername(final String username , final String email,final String password)
    {
        DatabaseReference here=mDatabase.getReference().child("usernames").child(username);

        here.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        mProgressbar.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Toast.makeText(SignUpActivity.this,"User name already exists! ",Toast.LENGTH_SHORT).show();

                    }else createuser(username,email,password);

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
        });

    }

    //creats user by savng data to firebase
    public void createuser(final String username, final String email, final String password)
    {
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                    String userid=user.getUid();
                    DatabaseReference save=mDatabase.getReference();
                    HashMap<String,String>up=new HashMap<String,String>();
                    up.put("email",email);
                    up.put("username",username);
                    up.put("password",password);
                    up.put("image","default");
                    up.put("thumb","default");
                    up.put("intro","default");
                    up.put("des","default");
                    up.put("cnt","0");
                    Map update=new HashMap();
                    update.put("uidtoinfo/"+userid,up);
                    update.put("usernames/"+username,true);
                    save.updateChildren(update, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                        if(databaseError == null){

                            FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                windowOn();
                                                Toast.makeText(SignUpActivity.this,"Success!! Check Email!",Toast.LENGTH_LONG).show();
                                                FirebaseAuth.getInstance().signOut();
                                                finish();
                                            }
                                            else
                                            {
                                                windowOn();
                                                Toast.makeText(SignUpActivity.this,"Some thin went wrong! Please try again!",Toast.LENGTH_LONG).show();
                                                FirebaseAuth.getInstance().signOut();

                                            }
                                        }
                                    });

                        } else {

                            String error = databaseError.getMessage();
                            Toast.makeText(SignUpActivity.this, error, Toast.LENGTH_SHORT).show();
                            windowOn();
                        }
                    }
                });

                }
                else
                {
                    windowOn();
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(SignUpActivity.this," "+task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
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
