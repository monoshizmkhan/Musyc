package com.inc.musyc.musyc.ActivitiesAndFragments.SocialHub;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.inc.musyc.musyc.Global.Infostatic;
import com.inc.musyc.musyc.R;

/*
    change intro activity
 */
public class ChangeIntroActivity extends AppCompatActivity {

    private TextInputLayout mIntro;
    private Button mUpdate;
    private ProgressBar mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_intro);

        //init
        mUpdate=(Button) findViewById(R.id.changeintro_bt);
        mIntro=(TextInputLayout)findViewById(R.id.changeintro_intro);
        mProgress=(ProgressBar) findViewById(R.id.changeintro_pb_progress);
        mIntro.getEditText().setText(Infostatic.intro);
        //init UI
        initUI();
    }

    private void initUI()
    {
        //updates intro button
        mUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                final String mEditIntro=mIntro.getEditText().getText().toString();

                FirebaseDatabase.getInstance().getReference().child("uidtoinfo").child(Infostatic.uid).child("intro").setValue(mEditIntro).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Infostatic.intro = mEditIntro;
                            Toast.makeText(ChangeIntroActivity.this,"Success!",Toast.LENGTH_SHORT);
                            mProgress.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            finish();
                        }
                        else
                        {
                            mProgress.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            Toast.makeText(ChangeIntroActivity.this,"Try again!",Toast.LENGTH_SHORT);
                        }
                    }
                });
            }
        });
    }
}
